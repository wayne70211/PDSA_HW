import java.util.Hashtable;
import java.util.Scanner;


public class UnionFind {
    public static void main(String[] args) {

        Hashtable<String,Integer> table = new Hashtable<>();

        Scanner in = new Scanner(System.in);

        int strength = Integer.parseInt(in.nextLine());
        int count = 0;

        while (!in.hasNext("0")){
            String line = in.nextLine();
            String[] protein = line.split("\t");
            if (Integer.parseInt(protein[2]) > strength){
                for (int i=0;i<2;i++){
                    System.out.println(protein[i]);
                    if (!table.contains(protein[i])){
                        table.put(protein[i],count);
                        count++;
                    }


                }
            }
        }




    }
}
