import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;


public class UnionFind {
    public static void main(String[] args) {

        Hashtable<String, Integer> table = new Hashtable<>();

        Scanner in = new Scanner(System.in);

        // Read threshold
        int threshold = Integer.parseInt(in.nextLine());
        int count = 0;

        while (!in.hasNext("Q")){
            // Read Line
            String line = in.nextLine();

            // Split the Content
            String[] protein = line.split("\t");

            // Get the strength
            int strength = Integer.parseInt(protein[2]);

            if (strength >= threshold){

                // Write the Hashtable
                if (!table.containsKey(protein[0])) {
                    table.put(protein[0], count);
                    count++;
                }
                if (!table.containsKey(protein[1])) {
                    table.put(protein[1], count);
                    count++;
                }
            }
        }

        Enumeration<String> names = table.keys();

        while (names.hasMoreElements()){
            String name = names.nextElement();
            System.out.println( name + " : " +table.get(name));
        }

    }
}
