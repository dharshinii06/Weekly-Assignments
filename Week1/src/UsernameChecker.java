import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameChecker {

    // Stores username -> userId
    private ConcurrentHashMap<String, Integer> usernameToUserId;

    // Tracks how many times a username was attempted
    private ConcurrentHashMap<String, Integer> attemptFrequency;

    // Track most attempted username
    private String mostAttemptedUsername;
    private int maxAttempts;

    public UsernameChecker() {
        usernameToUserId = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
        mostAttemptedUsername = "";
        maxAttempts = 0;
    }

    // Register a username
    public boolean registerUser(String username, int userId) {
        if (usernameToUserId.containsKey(username)) {
            return false;
        }

        usernameToUserId.put(username, userId);
        return true;
    }

    // Check if username is available
    public boolean checkAvailability(String username) {

        // Update attempt frequency
        int attempts = attemptFrequency.getOrDefault(username, 0) + 1;
        attemptFrequency.put(username, attempts);

        // Update most attempted username
        if (attempts > maxAttempts) {
            maxAttempts = attempts;
            mostAttemptedUsername = username;
        }

        return !usernameToUserId.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // Append numbers
        for (int i = 1; i <= 5; i++) {
            String candidate = username + i;

            if (!usernameToUserId.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        // Replace underscore with dot
        if (username.contains("_")) {
            String dotVersion = username.replace("_", ".");
            if (!usernameToUserId.containsKey(dotVersion)) {
                suggestions.add(dotVersion);
            }
        }

        // Add prefix
        String prefixVersion = "real_" + username;
        if (!usernameToUserId.containsKey(prefixVersion)) {
            suggestions.add(prefixVersion);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {
        return mostAttemptedUsername + " (" + maxAttempts + " attempts)";
    }

    // Print all registered users (for testing)
    public void printUsers() {
        for (Map.Entry<String, Integer> entry : usernameToUserId.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    // Main method for testing
    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        // Register some users
        checker.registerUser("john_doe", 1001);
        checker.registerUser("alice99", 1002);
        checker.registerUser("admin", 1);

        // Availability checks
        System.out.println("john_doe available? " + checker.checkAvailability("john_doe"));
        System.out.println("jane_smith available? " + checker.checkAvailability("jane_smith"));

        // Suggestions
        System.out.println("Suggestions for john_doe:");
        List<String> suggestions = checker.suggestAlternatives("john_doe");

        for (String s : suggestions) {
            System.out.println(s);
        }

        // Simulate many attempts
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        // Most attempted username
        System.out.println("Most attempted: " + checker.getMostAttempted());
    }
}