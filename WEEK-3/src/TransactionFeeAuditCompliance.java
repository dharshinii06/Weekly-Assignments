import java.util.*;

class Transaction {
    String id;
    double fee;
    String ts;

    Transaction(String id,double fee,String ts){
        this.id=id;
        this.fee=fee;
        this.ts=ts;
    }
}

public class TransactionFeeAuditCompliance {

    static int passes;
    static int swaps;

    static void bubbleSort(List<Transaction> list){
        int n=list.size();
        passes=0;
        swaps=0;

        for(int i=0;i<n;i++){
            passes++;
            boolean swapped=false;

            for(int j=0;j<n-i-1;j++){
                if(list.get(j).fee>list.get(j+1).fee){
                    Transaction temp=list.get(j);
                    list.set(j,list.get(j+1));
                    list.set(j+1,temp);
                    swaps++;
                    swapped=true;
                }
            }

            if(!swapped) break;
        }
    }

    static void insertionSort(List<Transaction> list){
        for(int i=1;i<list.size();i++){
            Transaction key=list.get(i);
            int j=i-1;

            while(j>=0 && list.get(j).fee>key.fee){
                list.set(j+1,list.get(j));
                j--;
            }

            list.set(j+1,key);
        }
    }

    static List<Transaction> highFee(List<Transaction> list){
        List<Transaction> res=new ArrayList<>();
        for(Transaction t:list){
            if(t.fee>50) res.add(t);
        }
        return res;
    }

    public static void main(String[] args){

        List<Transaction> transactions=new ArrayList<>();
        transactions.add(new Transaction("id1",10.5,"10:00"));
        transactions.add(new Transaction("id2",25.0,"09:30"));
        transactions.add(new Transaction("id3",5.0,"10:15"));

        System.out.println("Input transactions:");
        for(Transaction t:transactions){
            System.out.println(t.id+", fee="+t.fee+", ts="+t.ts);
        }

        System.out.println();

        List<Transaction> bubbleList=new ArrayList<>(transactions);
        bubbleSort(bubbleList);

        System.out.print("BubbleSort (fees): [");
        for(int i=0;i<bubbleList.size();i++){
            Transaction t=bubbleList.get(i);
            System.out.print(t.id+":"+t.fee);
            if(i<bubbleList.size()-1) System.out.print(", ");
        }
        System.out.println("] // "+passes+" passes, "+swaps+" swaps");

        List<Transaction> insertionList=new ArrayList<>(transactions);
        insertionSort(insertionList);

        System.out.print("InsertionSort (fee+ts): [");
        for(int i=0;i<insertionList.size();i++){
            Transaction t=insertionList.get(i);
            System.out.print(t.id+":"+t.fee+"@"+t.ts);
            if(i<insertionList.size()-1) System.out.print(", ");
        }
        System.out.println("]");

        List<Transaction> outliers=highFee(transactions);

        if(outliers.isEmpty()){
            System.out.println("High-fee outliers: none");
        }else{
            System.out.print("High-fee outliers: ");
            for(Transaction t:outliers){
                System.out.print(t.id+" ");
            }
        }
    }
}