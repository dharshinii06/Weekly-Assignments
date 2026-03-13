import java.util.*;

class PlagiarismDetector {

    // n-gram -> set of document IDs
    private Map<String, Set<String>> ngramIndex;

    // documentId -> set of its n-grams
    private Map<String, Set<String>> documentNgrams;

    private int N;

    public PlagiarismDetector(int n) {
        this.N = n;
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        Set<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            ngramIndex
                    .computeIfAbsent(gram, k -> new HashSet<>())
                    .add(docId);
        }

        System.out.println("Indexed document: " + docId +
                " (" + ngrams.size() + " n-grams)");
    }

    // Analyze a new document
    public void analyzeDocument(String docId, String text) {

        Set<String> ngrams = generateNgrams(text);

        System.out.println("\nAnalyzing: " + docId);
        System.out.println("Extracted " + ngrams.size() + " n-grams");

        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : ngrams) {

            Set<String> docs = ngramIndex.get(gram);

            if (docs != null) {

                for (String d : docs) {
                    matchCounts.put(d,
                            matchCounts.getOrDefault(d, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {

            String matchedDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity =
                    (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + matchedDoc + "\"");

            System.out.printf("Similarity: %.2f%% ", similarity);

            if (similarity > 60) {
                System.out.println("(PLAGIARISM DETECTED)");
            } else if (similarity > 15) {
                System.out.println("(Suspicious)");
            } else {
                System.out.println("(Low similarity)");
            }
        }
    }

    // Generate n-grams
    private Set<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");

        Set<String> ngrams = new HashSet<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }

            ngrams.add(sb.toString().trim());
        }

        return ngrams;
    }
}

public class PlagiarismTest {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector(5);

        String essay1 =
                "machine learning is transforming the world with artificial intelligence";

        String essay2 =
                "artificial intelligence and machine learning are transforming many industries";

        String essay3 =
                "machine learning is transforming the world with artificial intelligence and data science";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        detector.analyzeDocument("essay_123.txt", essay3);
    }
}