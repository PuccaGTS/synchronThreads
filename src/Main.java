import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Map.Entry<Integer, Integer> maxEntry = sizeToFreq.entrySet().stream()
                            .max(Comparator.comparing(Map.Entry::getValue))
                            .orElse(null);

                    System.out.println("Текущий максимум: " + maxEntry.getKey() + " (встретилось " + maxEntry.getValue() + " раз)");
                }
            }
        });
        thread.start();

        for (int i = 0; i < 1000; i++) {
            Thread thread1 = new Thread(() -> {
                int countR = 0;
                String route = generateRoute("RLRFR", 100);
                for (int j = 0; j < route.length(); j++) {
                    if (route.charAt(j) == 'R') {
                        countR++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countR)) {
                        sizeToFreq.put(countR, sizeToFreq.get(countR) + 1);
                    } else {
                        sizeToFreq.put(countR, 1);
                    }
                    sizeToFreq.notify();
                }
            });
            thread1.start();
            thread1.join();
        }
        thread.interrupt();

        printMap(sizeToFreq);
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void printMap(Map<Integer, Integer> map) {
        Map.Entry<Integer, Integer> maxEntry = map.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElse(null);

        System.out.println("Самое частое количество повторений " + maxEntry.getKey() + " (встретилось " + maxEntry.getValue() + " раз)");
        System.out.println("Другие размеры:");
        map.remove(maxEntry.getKey());

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("- " + entry.getKey() + "(" + entry.getValue() + " раз)");
        }
    }
}
