import java.io.File;
import java.util.*;


public class AdjacencyList {
    private int index;                          // Count different protein
    private int edges;                          // Count edges
    private AdjList adjList;                    // AdjacencyList
    private Hashtable<String,Integer> proteins; // Dictionary of protein and index of Adjacency List

    private class AdjList<Item> {
        private Stack<Item>[] array;

        private AdjList(int length){
            array = new Stack[length];
            // Create the Stack
            for (int i=0;i<length;i++) {
                array[i] = new Stack<>();
            }
        }
        private Stack get(int index) {
            return array[index];
        }

        private void addEdge(int index, Item item){
            array[index].push(item);
        }

        private int size(){
            return array.length;
        }

    }

    public AdjacencyList(int n){
        // constructor, n is the number of proteins in the network (the adjacency list)
        adjList = new AdjList(n);
        proteins = new Hashtable<>();
        index = 0;
        edges = 0;
    }

    public void add(String p1, String p2) {
        // add a pair of proteins as an edge of the network
        // throw a IndexOutOfBoundsException if adding more than n proteins
        int idx1;
        int idx2;
        if (!proteins.containsKey(p1)){
            CheckIndexException();
            proteins.put(p1,index);
            adjList.addEdge(index,p1);
            index++;
        }

        if (!proteins.containsKey(p2)){
            CheckIndexException();
            proteins.put(p2,index);
            adjList.addEdge(index,p2);
            index++;
        }

        idx1 = proteins.get(p1);
        idx2 = proteins.get(p2);

        // Check the same protein pairs
        if (!adjList.get(idx1).contains(p2)){
            adjList.addEdge(idx1,p2);
            adjList.addEdge(idx2,p1);
            edges++;
        }

    }

    private void CheckIndexException() {
        if (index>adjList.size()) throw new IndexOutOfBoundsException();
    }

    public int size(){
        // return the number of unique edges of the network
        return edges;
    }

    public String[] neighbors(String protein){
        // return all the neighbors of a protein as an array, where the proteins in the array are sorted alphabetically
        if (!proteins.containsKey(protein)) throw new IllegalArgumentException();

        int searcher = proteins.get(protein);   // The index of AdjacencyList
        Stack list = adjList.get(searcher);     // return the stack of protein in AdjacencyList

        // Copy the Stack to Array
        String[] array = new String[list.size()];
        array = (String[]) list.toArray(array);

        // Sorted alphabetically
        Arrays.sort(array);
        return array;
    }

    public String[] interactions(String protein, int k){
        // return all the interacting proteins (including both direct or indirect interactions) of a protein through at most k edges
        // where the proteins in the array are sorted alphabetically. The parameter k could be 0 or any positive integers.
        if (!proteins.containsKey(protein)) throw new IllegalArgumentException();
        if (k<0) throw new IllegalArgumentException();

        Stack<String> stack   = new Stack<>();  // Storage all interaction proteins
        Stack<String> counter = new Stack<>();  // Storage current level edge elements
        Stack<String> remove  = new Stack<>();  // Storage next level edge elements

        if (k == 0) {
            // The 0 edge is itself
            stack.add(protein);
            counter.add(protein);
        } else {
            // The neighbors protein (1 edge)
            stack.addAll(Arrays.asList(neighbors(protein)));
            counter.addAll(Arrays.asList(neighbors(protein)));
        }

        for (int i=1;i<k;i++) {
            while (!counter.isEmpty()) {
                for (String s: neighbors(counter.pop())) {
                    if (stack.contains(s)) continue;
                    stack.push(s);
                    remove.push(s);
                }
            }
            counter.addAll(remove);
            remove.clear();
        }

        counter.clear();

        String[] array = new String[stack.size()];
        array = stack.toArray(array);

        // Sorted alphabetically
        Arrays.sort(array);
        return array;
    }
    public static void main (String[] args) throws Exception{
        AdjacencyList adjacencyList = new AdjacencyList(20);
        File file = new File("input_HW2.txt") ;
        Scanner in = new Scanner(file);
        String pro1 = "";
        while(in.hasNextLine())
        {
            pro1 = in.nextLine();
            String[] pro = pro1.split("\t");
            adjacencyList.add(pro[0],pro[1]);
        }
        for (int i=1;i<21;i++){
            String s = String.valueOf(i);
            if (i<10) s = "0"+s;
            System.out.print("protein"+s+" : ");
            for(String o:adjacencyList.interactions("protein"+s,1)) {
                System.out.print(o+" ");
            }
            System.out.println();
        }
        System.out.println("========================");
        System.out.println("Total edges : "+adjacencyList.size());
    }

}

