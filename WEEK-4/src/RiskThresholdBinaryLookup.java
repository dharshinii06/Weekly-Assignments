import java.util.*;

public class RiskThresholdBinaryLookup {

    static void linearSearch(int[] arr,int target){
        int comparisons=0;
        boolean found=false;

        for(int i=0;i<arr.length;i++){
            comparisons++;
            if(arr[i]==target){
                found=true;
                break;
            }
        }

        if(found)
            System.out.println("Linear: threshold="+target+" → found ("+comparisons+" comps)");
        else
            System.out.println("Linear: threshold="+target+" → not found ("+comparisons+" comps)");
    }

    static void binaryFloorCeiling(int[] arr,int target){
        int low=0,high=arr.length-1;
        int comparisons=0;

        Integer floor=null,ceiling=null;

        while(low<=high){
            int mid=(low+high)/2;
            comparisons++;

            if(arr[mid]==target){
                floor=arr[mid];
                ceiling=arr[mid];
                break;
            }
            else if(arr[mid]<target){
                floor=arr[mid];
                low=mid+1;
            }
            else{
                ceiling=arr[mid];
                high=mid-1;
            }
        }

        System.out.println("Binary floor("+target+"): "+floor+", ceiling: "+ceiling+" ("+comparisons+" comps)");
    }

    public static void main(String[] args){

        int[] risks={10,25,50,100};

        System.out.print("Sorted risks: [");
        for(int i=0;i<risks.length;i++){
            System.out.print(risks[i]);
            if(i<risks.length-1) System.out.print(", ");
        }
        System.out.println("]");

        linearSearch(risks,30);
        binaryFloorCeiling(risks,30);
    }
}
