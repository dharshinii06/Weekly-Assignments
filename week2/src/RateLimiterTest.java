import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class TokenBucket {

    private final int maxTokens;
    private final int refillRate; // tokens per hour

    private AtomicInteger tokens;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = new AtomicInteger(maxTokens);
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {

        refillTokens();

        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }

        return false;
    }

    private void refillTokens() {

        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;

        long tokensToAdd = (elapsed * refillRate) / (3600 * 1000);

        if (tokensToAdd > 0) {

            int newTokenCount = Math.min(maxTokens,
                    tokens.get() + (int) tokensToAdd);

            tokens.set(newTokenCount);

            lastRefillTime = now;
        }
    }

    public int getRemainingTokens() {
        return tokens.get();
    }
}

class RateLimiter {

    // clientId -> TokenBucket
    private ConcurrentHashMap<String, TokenBucket> clientBuckets;

    private final int MAX_REQUESTS = 1000;
    private final int REFILL_RATE = 1000; // per hour

    public RateLimiter() {
        clientBuckets = new ConcurrentHashMap<>();
    }

    public String checkRateLimit(String clientId) {

        TokenBucket bucket = clientBuckets.computeIfAbsent(
                clientId,
                id -> new TokenBucket(MAX_REQUESTS, REFILL_RATE)
        );

        boolean allowed = bucket.allowRequest();

        if (allowed) {

            return "Allowed (" +
                    bucket.getRemainingTokens() +
                    " requests remaining)";
        }

        return "Denied (0 requests remaining, retry later)";
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int remaining = bucket.getRemainingTokens();

        System.out.println(
                "{used: " + (1000 - remaining) +
                        ", limit: 1000, remaining: " +
                        remaining + "}"
        );
    }
}

public class RateLimiterTest {

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {

            String result = limiter.checkRateLimit(client);

            System.out.println(result);
        }

        limiter.getRateLimitStatus(client);
    }
}
