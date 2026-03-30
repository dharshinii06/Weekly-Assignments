class Client {
    String name;
    int riskScore;

    Client(String name, int riskScore) {
        this.name = name;
        this.riskScore = riskScore;
    }
}

public class ClientRiskScoreRanking {

    static int swaps = 0;

    static void bubbleSort(Client[] arr) {
        int n = arr.length;
        swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].riskScore > arr[j + 1].riskScore) {
                    Client temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swaps++;
                    swapped = true;
                }
            }

            if (!swapped) break;
        }
    }

    static void insertionSortDesc(Client[] arr) {
        for (int i = 1; i < arr.length; i++) {
            Client key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j].riskScore < key.riskScore) {
                arr[j + 1] = arr[j];
                j--;
            }

            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {

        Client[] clients = {
                new Client("clientC", 80),
                new Client("clientA", 20),
                new Client("clientB", 50)
        };

        System.out.print("Input: [");
        for (int i = 0; i < clients.length; i++) {
            System.out.print(clients[i].name + ":" + clients[i].riskScore);
            if (i < clients.length - 1) System.out.print(", ");
        }
        System.out.println("]");

        Client[] bubbleArray = clients.clone();
        bubbleSort(bubbleArray);

        System.out.print("Bubble (asc): [");
        for (int i = 0; i < bubbleArray.length; i++) {
            System.out.print(bubbleArray[i].name.replace("client","") + ":" + bubbleArray[i].riskScore);
            if (i < bubbleArray.length - 1) System.out.print(", ");
        }
        System.out.println("] // Swaps:" + swaps);

        Client[] insertionArray = clients.clone();
        insertionSortDesc(insertionArray);

        System.out.print("Insertion (desc): [");
        for (int i = 0; i < insertionArray.length; i++) {
            System.out.print(insertionArray[i].name.replace("client","") + ":" + insertionArray[i].riskScore);
            if (i < insertionArray.length - 1) System.out.print(", ");
        }
        System.out.println("]");

        System.out.print("Top 3 risks: ");
        for (int i = 0; i < insertionArray.length; i++) {
            System.out.print(insertionArray[i].name.replace("client","") + "(" + insertionArray[i].riskScore + ")");
            if (i < insertionArray.length - 1) System.out.print(", ");
        }
    }
}