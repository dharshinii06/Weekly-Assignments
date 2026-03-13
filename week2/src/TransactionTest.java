import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction {

    int id;
    double amount;
    String merchant;
    String account;
    LocalDateTime time;

    public Transaction(int id, double amount, String merchant, String account, String timeStr) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return "{id:" + id + ", amount:" + amount + ", merchant:" + merchant + ", account:" + account + ", time:" + time + "}";
    }
}

class TransactionAnalyzer {

    List<Transaction> transactions;

    public TransactionAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Classic Two-Sum
    public List<List<Transaction>> findTwoSum(double target) {

        Map<Double, Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : transactions) {
            double complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), t));
            }
            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum within a time window (minutes)
    public List<List<Transaction>> findTwoSumWithinWindow(double target, int minutes) {

        List<List<Transaction>> result = new ArrayList<>();

        Map<Double, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            double complement = target - t.amount;

            if (map.containsKey(complement)) {
                for (Transaction t2 : map.get(complement)) {
                    long diff = Math.abs(java.time.Duration.between(t.time, t2.time).toMinutes());
                    if (diff <= minutes) {
                        result.add(Arrays.asList(t2, t));
                    }
                }
            }

            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }

        return result;
    }

    // K-Sum (recursive)
    public List<List<Transaction>> findKSum(int k, double target) {
        transactions.sort(Comparator.comparingDouble(t -> t.amount));
        return kSumHelper(0, k, target);
    }

    private List<List<Transaction>> kSumHelper(int start, int k, double target) {

        List<List<Transaction>> res = new ArrayList<>();

        if (k == 2) { // Two-sum
            int left = start, right = transactions.size() - 1;
            while (left < right) {
                double sum = transactions.get(left).amount + transactions.get(right).amount;
                if (Math.abs(sum - target) < 1e-6) {
                    res.add(Arrays.asList(transactions.get(left), transactions.get(right)));
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        } else {
            for (int i = start; i < transactions.size(); i++) {
                List<List<Transaction>> temp = kSumHelper(i + 1, k - 1, target - transactions.get(i).amount);
                for (List<Transaction> lst : temp) {
                    List<Transaction> combination = new ArrayList<>();
                    combination.add(transactions.get(i));
                    combination.addAll(lst);
                    res.add(combination);
                }
            }
        }

        return res;
    }

    // Duplicate detection: same amount & merchant, different accounts
    public List<Map<String, Object>> detectDuplicates() {

        Map<String, List<Transaction>> map = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Transaction t : transactions) {
            String key = t.amount + "|" + t.merchant;
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {
            Set<String> accounts = new HashSet<>();
            for (Transaction t : entry.getValue()) accounts.add(t.account);
            if (accounts.size() > 1) {
                String[] parts = entry.getKey().split("\\|");
                Map<String, Object> dup = new HashMap<>();
                dup.put("amount", Double.parseDouble(parts[0]));
                dup.put("merchant", parts[1]);
                dup.put("accounts", accounts);
                result.add(dup);
            }
        }

        return result;
    }
}

public class TransactionTest {

    public static void main(String[] args) {

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1, 500, "Store A", "acc1", "2026-03-13 10:00"),
                new Transaction(2, 300, "Store B", "acc2", "2026-03-13 10:15"),
                new Transaction(3, 200, "Store C", "acc3", "2026-03-13 10:30"),
                new Transaction(4, 500, "Store A", "acc4", "2026-03-13 11:00")
        );

        TransactionAnalyzer analyzer = new TransactionAnalyzer(transactions);

        System.out.println("=== Classic Two-Sum (target=500) ===");
        List<List<Transaction>> twoSum = analyzer.findTwoSum(500);
        for (List<Transaction> pair : twoSum) System.out.println(pair);

        System.out.println("\n=== Two-Sum within 60 minutes (target=500) ===");
        List<List<Transaction>> twoSumWindow = analyzer.findTwoSumWithinWindow(500, 60);
        for (List<Transaction> pair : twoSumWindow) System.out.println(pair);

        System.out.println("\n=== K-Sum (k=3, target=1000) ===");
        List<List<Transaction>> kSum = analyzer.findKSum(3, 1000);
        for (List<Transaction> combo : kSum) System.out.println(combo);

        System.out.println("\n=== Duplicate Detection ===");
        List<Map<String, Object>> duplicates = analyzer.detectDuplicates();
        for (Map<String, Object> dup : duplicates) System.out.println(dup);
    }
}