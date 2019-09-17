import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;


public class UnionFind {
    private int count;      // number of components
    private ArrayList<Integer> parent ;
    private ArrayList<Integer> size   ;

    private int count() {
        return count;
    }

    private int find(int p) {
        while (p != parent.get(p))
            p = parent.get(p);
        return p;
    }

    private boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    private void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;

        // make smaller root point to larger one
        if (size.get(rootP) < size.get(rootQ)) {
            parent.set(rootP, rootQ);
            size.set(rootQ, size.get(rootP));
        }
        else {
            parent.set(rootQ, rootP);
            size.set(rootP, size.get(rootQ));
        }
        count--;
    }

    public static void main(String[] args) {

        Hashtable<String, Integer> table = new Hashtable<>();
        UnionFind uf = new UnionFind();

        uf.parent = new ArrayList<>();
        uf.size = new ArrayList<>();

        Scanner in = new Scanner(System.in);

        // Read threshold
        int threshold = Integer.parseInt(in.nextLine());

        // Initializes index
        int index = 0;

        while (in.hasNext()){
            // Read Line
            String line = in.nextLine();

            try {
                // Split the Content
                String[] protein = line.split("\t");

                // Get the strength
                int strength = Integer.parseInt(protein[2]);

                if (strength >= threshold){

                    // Write the Hashtable
                    for (int i=0;i<2;i++){
                        if (!table.containsKey(protein[i])) {
                            table.put(protein[i], index);

                            // Initializes WeightedQuickUnionUF
                            uf.parent.add(index);
                            uf.size.add(1);
                            uf.count +=1;

                            index++;
                        }
                    }

                    // Weighted Quick-Union
                    if (uf.connected(table.get(protein[0]), table.get(protein[1]))) continue;
                    uf.union(table.get(protein[0]),table.get(protein[1]));

                }
            }catch (Exception e){
                break;
            }
        }

        System.out.println(index);
        System.out.println(uf.count());

    }
}
