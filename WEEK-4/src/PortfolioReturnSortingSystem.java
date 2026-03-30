class Asset {
    String name;
    double returnRate;
    double volatility;

    Asset(String name,double returnRate,double volatility){
        this.name=name;
        this.returnRate=returnRate;
        this.volatility=volatility;
    }
}

public class PortfolioReturnSortingSystem {

    static void mergeSort(Asset[] arr,int left,int right){
        if(left<right){
            int mid=(left+right)/2;
            mergeSort(arr,left,mid);
            mergeSort(arr,mid+1,right);
            merge(arr,left,mid,right);
        }
    }

    static void merge(Asset[] arr,int left,int mid,int right){
        int n1=mid-left+1;
        int n2=right-mid;

        Asset[] L=new Asset[n1];
        Asset[] R=new Asset[n2];

        for(int i=0;i<n1;i++) L[i]=arr[left+i];
        for(int j=0;j<n2;j++) R[j]=arr[mid+1+j];

        int i=0,j=0,k=left;

        while(i<n1 && j<n2){
            if(L[i].returnRate<=R[j].returnRate){
                arr[k]=L[i];
                i++;
            }else{
                arr[k]=R[j];
                j++;
            }
            k++;
        }

        while(i<n1){
            arr[k]=L[i];
            i++;
            k++;
        }

        while(j<n2){
            arr[k]=R[j];
            j++;
            k++;
        }
    }

    static void quickSort(Asset[] arr,int low,int high){
        if(low<high){
            int pi=partition(arr,low,high);
            quickSort(arr,low,pi-1);
            quickSort(arr,pi+1,high);
        }
    }

    static int partition(Asset[] arr,int low,int high){
        Asset pivot=arr[high];
        int i=low-1;

        for(int j=low;j<high;j++){
            if(arr[j].returnRate>pivot.returnRate){
                i++;
                Asset temp=arr[i];
                arr[i]=arr[j];
                arr[j]=temp;
            }
        }

        Asset temp=arr[i+1];
        arr[i+1]=arr[high];
        arr[high]=temp;

        return i+1;
    }

    public static void main(String[] args){

        Asset[] assets={
                new Asset("AAPL",12,20),
                new Asset("TSLA",8,35),
                new Asset("GOOG",15,18)
        };

        System.out.print("Input: [");
        for(int i=0;i<assets.length;i++){
            System.out.print(assets[i].name+":"+assets[i].returnRate+"%");
            if(i<assets.length-1) System.out.print(", ");
        }
        System.out.println("]");

        Asset[] mergeArray=assets.clone();
        mergeSort(mergeArray,0,mergeArray.length-1);

        System.out.print("Merge: [");
        for(int i=0;i<mergeArray.length;i++){
            System.out.print(mergeArray[i].name+":"+mergeArray[i].returnRate+"%");
            if(i<mergeArray.length-1) System.out.print(", ");
        }
        System.out.println("]");

        Asset[] quickArray=assets.clone();
        quickSort(quickArray,0,quickArray.length-1);

        System.out.print("Quick (desc): [");
        for(int i=0;i<quickArray.length;i++){
            System.out.print(quickArray[i].name+":"+quickArray[i].returnRate+"%");
            if(i<quickArray.length-1) System.out.print(", ");
        }
        System.out.println("]");
    }
}