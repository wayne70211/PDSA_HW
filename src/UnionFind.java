//import java.io.File;
//import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;


public class UnionFind {
    private static int count;      // number of components
    private static ArrayList<Integer> parent ;
    private static ArrayList<Integer> size   ;

    private static int find(int p) {
        while (p != parent.get(p))
            p = parent.get(p);
        return p;
    }

    private static boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    private static void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;

        // make smaller root point to larger one
        if (size.get(rootP) < size.get(rootQ)) {
            parent.set(rootP, rootQ);
            size.set(rootQ, size.get(rootQ)+size.get(rootP));
        }
        else {
            parent.set(rootQ, rootP);
            size.set(rootP, size.get(rootQ)+size.get(rootP));
        }
        count--;
    }

    public static void main(String[] args) {

        Hashtable<String, Integer> table = new Hashtable<>();

        parent = new ArrayList<>();
        size = new ArrayList<>();

        Scanner in = new Scanner(System.in);

//        File file = new File("input_HW1.txt") ;
//        Scanner in = null;
//        try {
//            in = new Scanner(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        // Read threshold
        int threshold = Integer.parseInt(in.nextLine());

        // Initializes index
        int index = 0;

        while (in.hasNextLine()) {
            // Read Line
            String line = in.nextLine();

            // Split the Content
            String[] protein = line.split("\t");

            // Get the strength
            int strength = Integer.parseInt(protein[2]);

            if (strength >= threshold) {

                // Write the Hashtable
                for (int i=0;i<2;i++){
                    if (!table.containsKey(protein[i])) {
                        table.put(protein[i], index);

                        // Initializes WeightedQuickUnionUF
                        parent.add(index);
                        size.add(1);
                        count++;
                        index++;
                    }
                }

                // Weighted Quick-Union
                if (connected(table.get(protein[0]), table.get(protein[1]))) continue;
                union(table.get(protein[0]),table.get(protein[1]));

            }
        }

        in.close();

        System.out.println(index);
        System.out.println(count);

    }
}
