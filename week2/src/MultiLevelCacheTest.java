import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class MultiLevelCache {

    // L1: In-memory cache (LinkedHashMap for LRU)
    private final int L1_CAPACITY = 10000;
    private LinkedHashMap<String, VideoData> l1Cache;

    // L2: SSD-backed cache simulation (HashMap)
    private final int L2_CAPACITY = 100000;
    private Map<String, VideoData> l2Cache;
    private Map<String, Integer> accessCount;

    // L3: Database simulation (all videos)
    private Map<String, VideoData> database;

    // Statistics
    private AtomicLong l1Hits = new AtomicLong(0);
    private AtomicLong l1Misses = new AtomicLong(0);
    private AtomicLong l2Hits = new AtomicLong(0);
    private AtomicLong l2Misses = new AtomicLong(0);
    private AtomicLong l3Hits = new AtomicLong(0);
    private AtomicLong totalRequests = new AtomicLong(0);

    public MultiLevelCache() {
        l1Cache = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        l2Cache = new HashMap<>();
        accessCount = new HashMap<>();
        database = new HashMap<>();
    }

    // Add video to database
    public void addToDatabase(VideoData video) {
        database.put(video.videoId, video);
    }

    // Get video from multi-level cache
    public VideoData getVideo(String videoId) {

        totalRequests.incrementAndGet();

        // 1️⃣ L1 Check
        if (l1Cache.containsKey(videoId)) {
            l1Hits.incrementAndGet();
            simulateDelay(0.5);
            return l1Cache.get(videoId);
        } else {
            l1Misses.incrementAndGet();
        }

        // 2️⃣ L2 Check
        if (l2Cache.containsKey(videoId)) {
            l2Hits.incrementAndGet();
            simulateDelay(5);
            promoteToL1(videoId);
            return l2Cache.get(videoId);
        } else {
            l2Misses.incrementAndGet();
        }

        // 3️⃣ L3 Database
        if (database.containsKey(videoId)) {
            l3Hits.incrementAndGet();
            simulateDelay(150);
            addToL2(videoId);
            return database.get(videoId);
        }

        // Video not found
        return null;
    }

    private void promoteToL1(String videoId) {
        VideoData video = l2Cache.get(videoId);
        l1Cache.put(videoId, video);
    }

    private void addToL2(String videoId) {
        if (!database.containsKey(videoId)) return;

        // Add or increment access count
        accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);

        // Promote to L1 if access exceeds threshold
        if (accessCount.get(videoId) >= 3) {
            l1Cache.put(videoId, database.get(videoId));
        } else {
            // Maintain L2 capacity
            if (l2Cache.size() >= L2_CAPACITY) {
                String firstKey = l2Cache.keySet().iterator().next();
                l2Cache.remove(firstKey);
                accessCount.remove(firstKey);
            }
            l2Cache.put(videoId, database.get(videoId));
        }
    }

    private void simulateDelay(double ms) {
        try {
            Thread.sleep((long)(ms));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Print statistics
    public void getStatistics() {

        long total = totalRequests.get();
        double l1HitRate = total == 0 ? 0 : (l1Hits.get() * 100.0 / total);
        double l2HitRate = total == 0 ? 0 : (l2Hits.get() * 100.0 / total);
        double l3HitRate = total == 0 ? 0 : (l3Hits.get() * 100.0 / total);

        System.out.println("\n--- Cache Statistics ---");
        System.out.printf("L1 Hit Rate: %.2f%%, Avg Time: 0.5ms\n", l1HitRate);
        System.out.printf("L2 Hit Rate: %.2f%%, Avg Time: 5ms\n", l2HitRate);
        System.out.printf("L3 Hit Rate: %.2f%%, Avg Time: 150ms\n", l3HitRate);

        double overallHitRate = l1HitRate + l2HitRate + l3HitRate;
        double avgTime = (l1Hits.get() * 0.5 + l2Hits.get() * 5 + l3Hits.get() * 150) / Math.max(total,1);
        System.out.printf("Overall Hit Rate: %.2f%%, Avg Time: %.2fms\n", overallHitRate, avgTime);
        System.out.println("-------------------------\n");
    }
}

public class MultiLevelCacheTest {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        // Populate database
        for (int i = 1; i <= 20; i++) {
            cache.addToDatabase(new VideoData("video_" + i, "VideoContent" + i));
        }

        // Access videos
        cache.getVideo("video_1"); // L3 → L2
        cache.getVideo("video_1"); // L2 → L1
        cache.getVideo("video_1"); // L1 hit
        cache.getVideo("video_2"); // L3 → L2
        cache.getVideo("video_3"); // L3 → L2
        cache.getVideo("video_3"); // L2 → L1

        cache.getStatistics();
    }
}