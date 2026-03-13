import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class FlashSaleInventoryManager {

    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waitingList = new ConcurrentHashMap<>();

    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new ConcurrentLinkedQueue<>());
    }

    public String purchaseItem(String productId, int userId) {

        AtomicInteger stock = inventory.get(productId);

        while (true) {

            int currentStock = stock.get();

            if (currentStock > 0) {

                if (stock.compareAndSet(currentStock, currentStock - 1)) {
                    return "User " + userId + " purchased successfully";
                }

            } else {

                ConcurrentLinkedQueue<Integer> queue = waitingList.get(productId);
                queue.add(userId);

                return "User " + userId + " added to waiting list #" + queue.size();
            }
        }
    }

    public int getRemainingStock(String productId) {
        return inventory.get(productId).get();
    }

    public int getWaitingListSize(String productId) {
        return waitingList.get(productId).size();
    }
}

public class FlashSaleTest {

    public static void main(String[] args) throws InterruptedException {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        String product = "IPHONE15_256GB";

        int stock = 100;
        int users = 50000;

        manager.addProduct(product, stock);

        ExecutorService executor = Executors.newFixedThreadPool(200);

        CountDownLatch latch = new CountDownLatch(users);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= users; i++) {

            int userId = i;

            executor.execute(() -> {
                manager.purchaseItem(product, userId);
                latch.countDown();
            });
        }

        latch.await();

        long endTime = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("Flash Sale Completed!");
        System.out.println("Remaining Stock: " + manager.getRemainingStock(product));
        System.out.println("Waiting List Size: " + manager.getWaitingListSize(product));
        System.out.println("Total Users: " + users);
        System.out.println("Execution Time: " + (endTime - startTime) + " ms");
    }
}