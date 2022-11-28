import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String [] texts = new String[1000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateRoute("RLRFR", 100);
        }
        List<Future<Integer>> futures = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(texts.length);

        for(String text : texts) {
            Future<Integer> futureTask = threadPool.submit(() -> {
                int countR = 0;
                for (int j = 0; j< text.length(); j++) {
                    if (text.charAt(j) == 'R') {
                        countR++;
                    }
                }
                return countR;
            });
            futures.add(futureTask);
        }
        for (Future<Integer> future : futures) {
            int numder = future.get();
            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(numder)) {
                    sizeToFreq.put(numder, sizeToFreq.get(numder) + 1);
                } else {
                    sizeToFreq.put(numder, 1);
                }
            }
        }
        printMap(sizeToFreq);
        threadPool.shutdown();
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
