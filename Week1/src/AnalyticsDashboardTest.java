import java.util.*;
import java.util.concurrent.*;

class PageEvent {
    String url;
    String userId;
    String source;

    public PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class RealTimeAnalytics {

    // Page -> total visits
    private ConcurrentHashMap<String, Integer> pageViews = new ConcurrentHashMap<>();

    // Page -> unique users
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();

    // Source -> visits
    private ConcurrentHashMap<String, Integer> trafficSources = new ConcurrentHashMap<>();

    public void processEvent(PageEvent event) {

        // Update page views
        pageViews.merge(event.url, 1, Integer::sum);

        // Track unique visitors
        uniqueVisitors
                .computeIfAbsent(event.url, k -> ConcurrentHashMap.newKeySet())
                .add(event.userId);

        // Track traffic source
        trafficSources.merge(event.source, 1, Integer::sum);
    }

    // Get Top 10 pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);
        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }

    // Dashboard view
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank++ + ". " + page +
                    " - " + views + " views (" +
                    unique + " unique)");
        }

        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent = (entry.getValue() * 100.0) / total;

            System.out.printf("%s: %.2f%%\n", entry.getKey(), percent);
        }

        System.out.println("===============================\n");
    }
}

public class AnalyticsDashboardTest {

    public static void main(String[] args) {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        ScheduledExecutorService dashboardUpdater =
                Executors.newScheduledThreadPool(1);

        // Update dashboard every 5 seconds
        dashboardUpdater.scheduleAtFixedRate(
                analytics::getDashboard,
                5,
                5,
                TimeUnit.SECONDS
        );

        // Simulate streaming page view events
        String[] pages = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-future",
                "/world/election",
                "/finance/market"
        };

        String[] sources = {
                "google",
                "facebook",
                "direct",
                "twitter",
                "linkedin"
        };

        Random random = new Random();

        while (true) {

            String url = pages[random.nextInt(pages.length)];
            String user = "user_" + random.nextInt(10000);
            String source = sources[random.nextInt(sources.length)];

            analytics.processEvent(new PageEvent(url, user, source));

            try {
                Thread.sleep(5); // simulate high traffic
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
