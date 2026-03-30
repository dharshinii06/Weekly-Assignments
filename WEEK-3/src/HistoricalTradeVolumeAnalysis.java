import java.util.*;

class Trade {
    String id;
    int volume;

    Trade(String id, int volume) {
        this.id = id;
        this.volume = volume;
    }
}

public class HistoricalTradeVolumeAnalysis {

    static void mergeSort(Trade[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    static void merge(Trade[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Trade[] L = new Trade[n1];
        Trade[] R = new Trade[n2];

        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i].volume <= R[j].volume) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    static void quickSortDesc(Trade[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSortDesc(arr, low, pi - 1);
            quickSortDesc(arr, pi + 1, high);
        }
    }

    static int partition(Trade[] arr, int low, int high) {
        int pivot = arr[high].volume;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j].volume > pivot) {
                i++;
                Trade temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        Trade temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    static int totalVolume(Trade[] arr) {
        int sum = 0;
        for (Trade t : arr) sum += t.volume;
        return sum;
    }

    public static void main(String[] args) {

        Trade[] trades = {
                new Trade("trade3", 500),
                new Trade("trade1", 100),
                new Trade("trade2", 300)
        };

        System.out.print("Input: [");
        for (int i = 0; i < trades.length; i++) {
            System.out.print(trades[i].id + ":" + trades[i].volume);
            if (i < trades.length - 1) System.out.print(", ");
        }
        System.out.println("]");

        Trade[] mergeArray = trades.clone();
        mergeSort(mergeArray, 0, mergeArray.length - 1);

        System.out.print("MergeSort: [");
        for (int i = 0; i < mergeArray.length; i++) {
            System.out.print(mergeArray[i].id.replace("trade","") + ":" + mergeArray[i].volume);
            if (i < mergeArray.length - 1) System.out.print(", ");
        }
        System.out.println("] // Stable");

        Trade[] quickArray = trades.clone();
        quickSortDesc(quickArray, 0, quickArray.length - 1);

        System.out.print("QuickSort (desc): [");
        for (int i = 0; i < quickArray.length; i++) {
            System.out.print(quickArray[i].id.replace("trade","") + ":" + quickArray[i].volume);
            if (i < quickArray.length - 1) System.out.print(", ");
        }
        System.out.println("] // Pivot: median");

        int total = totalVolume(mergeArray);
        System.out.println("Merged morning+afternoon total: " + total);
    }
}