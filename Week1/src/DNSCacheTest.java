import java.util.*;
import java.util.concurrent.*;

class DNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private final int MAX_CACHE_SIZE;

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    private long cacheHits = 0;
    private long cacheMisses = 0;

    public DNSCache(int maxSize) {

        this.MAX_CACHE_SIZE = maxSize;

        cache = new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long startTime = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {

            cacheHits++;

            long endTime = System.nanoTime();

            System.out.println("Cache HIT → " + entry.ipAddress +
                    " (lookup " + (endTime - startTime) / 1_000_000.0 + " ms)");

            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
            System.out.println("Cache EXPIRED → " + domain);
        }

        cacheMisses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 5); // TTL 5 seconds
        cache.put(domain, newEntry);

        long endTime = System.nanoTime();

        System.out.println("Cache MISS → Query upstream → " + ip +
                " (lookup " + (endTime - startTime) / 1_000_000.0 + " ms)");

        return ip;
    }

    // Simulate upstream DNS query
    private String queryUpstreamDNS(String domain) {

        try {
            Thread.sleep(100); // simulate 100ms DNS latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    // Background thread to remove expired entries
    private void startCleanupThread() {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {

            synchronized (this) {

                Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, DNSEntry> entry = iterator.next();

                    if (entry.getValue().isExpired()) {
                        iterator.remove();
                    }
                }
            }

        }, 5, 5, TimeUnit.SECONDS);
    }

    // Cache statistics
    public void getCacheStats() {

        long total = cacheHits + cacheMisses;

        double hitRate = total == 0 ? 0 : (cacheHits * 100.0) / total;

        System.out.println("\nCache Stats:");
        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }
}

public class DNSCacheTest {

    public static void main(String[] args) throws Exception {

        DNSCache cache = new DNSCache(5);

        cache.resolve("google.com");
        cache.resolve("google.com");

        cache.resolve("openai.com");
        cache.resolve("github.com");

        Thread.sleep(6000);

        cache.resolve("google.com");

        cache.getCacheStats();
    }
}