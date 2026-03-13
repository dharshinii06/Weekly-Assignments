import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queryFrequency = new HashMap<>();
}

class AutocompleteSystem {

    private TrieNode root;
    private Map<String, Integer> globalFrequency;

    public AutocompleteSystem() {
        root = new TrieNode();
        globalFrequency = new HashMap<>();
    }

    // Insert query into Trie
    public void addQuery(String query) {

        globalFrequency.put(query,
                globalFrequency.getOrDefault(query, 0) + 1);

        int freq = globalFrequency.get(query);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queryFrequency.put(query, freq);
        }
    }

    // Get top 10 suggestions for prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }

            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(
                        (a, b) -> a.getValue() - b.getValue()
                );

        for (Map.Entry<String, Integer> entry :
                node.queryFrequency.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll().getKey());
        }

        Collections.reverse(result);

        return result;
    }

    // Update frequency after new search
    public void updateFrequency(String query) {
        addQuery(query);
    }
}

public class AutocompleteTest {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript tutorial");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");
        system.addQuery("java tutorial");

        List<String> suggestions = system.search("jav");

        System.out.println("Suggestions for 'jav':");

        for (String s : suggestions) {
            System.out.println(s);
        }

        system.updateFrequency("java 21 features");
    }
}