import java.util.*;

public class AccountIdLookupSystem {

    static int linearFirst(String[] arr,String target){
        int comparisons=0;
        for(int i=0;i<arr.length;i++){
            comparisons++;
            if(arr[i].equals(target)){
                System.out.println("Linear first "+target+": index "+i+" ("+comparisons+" comparisons)");
                return i;
            }
        }
        System.out.println("Linear first "+target+": not found ("+comparisons+" comparisons)");
        return -1;
    }

    static int binarySearch(String[] arr,String target){
        int low=0,high=arr.length-1;
        int comparisons=0;

        while(low<=high){
            int mid=(low+high)/2;
            comparisons++;

            if(arr[mid].equals(target)){
                System.out.println("Binary "+target+": index "+mid+" ("+comparisons+" comparisons), count="+countOccurrences(arr,target));
                return mid;
            }else if(arr[mid].compareTo(target)<0){
                low=mid+1;
            }else{
                high=mid-1;
            }
        }

        System.out.println("Binary "+target+": not found ("+comparisons+" comparisons)");
        return -1;
    }

    static int countOccurrences(String[] arr,String target){
        int count=0;
        for(String s:arr){
            if(s.equals(target)) count++;
        }
        return count;
    }

    public static void main(String[] args){

        String[] logs={"accB","accA","accB","accC"};

        System.out.print("Sorted logs: [");
        for(int i=0;i<logs.length;i++){
            System.out.print(logs[i]);
            if(i<logs.length-1) System.out.print(", ");
        }
        System.out.println("]");

        linearFirst(logs,"accB");

        String[] sortedLogs=logs.clone();
        Arrays.sort(sortedLogs);

        binarySearch(sortedLogs,"accB");
    }
}