import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HierarchicalClusteringTree {
    private int size;
    private MinPQ<Edge> minPQ;
    private final Hashtable<Point2D, Integer> degree;
    private final Hashtable<Point2D, TreeNode> tree;
    private LinkedList[] cluster;

    // comparable for two points distance
    private class Edge implements Comparable<Edge>{
        private Point2D a,b;
        private double distance;

        Edge(Point2D a, Point2D b){
            this.a = a;
            this.b = b;
            this.distance = a.distanceTo(b);
        }

        @Override
        public int compareTo(Edge that) {
            return Double.compare(this.distance,that.distance);
        }

    }

    public class TreeNode {
        private TreeNode parent;            // parent
        private TreeNode left, right;       // two children
        private Point2D point;              // name of node
        private int size;

        // create a leaf node
        TreeNode(Point2D point) {
            this.point = point;
            this.size = 1;
        }

        // create an internal node that is the parent of x and y
        TreeNode(Point2D point, TreeNode x, TreeNode y) {
            this.point = point;
            this.left = x;
            this.right = y;
            this.size  = x.size+y.size;
            x.parent = this;
            y.parent = this;
        }

        // return root
        public TreeNode root() {
            TreeNode x = this;
            while (x.parent != null)
                x = x.parent;
            return x;
        }

    }


    // create clustering tree
    HierarchicalClusteringTree(Point2D[] point2DS) {

        size = point2DS.length;
        minPQ = new MinPQ<>();
        degree = new Hashtable<>();
        tree = new Hashtable<>();
        cluster = new LinkedList[size];

        ArrayList<Point2D> arrayList = new ArrayList<>();
        Arrays.sort(point2DS,Point2D.Y_ORDER);
        cluster[size-1] = new LinkedList();

        // Compute all distance between all two points
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                Edge edge = new Edge(point2DS[i],point2DS[j]);
                minPQ.insert(edge);
            }
            TreeNode node = new TreeNode(point2DS[i]);
            arrayList.add(point2DS[i]);
            degree.put(point2DS[i],1);
            tree.put(point2DS[i],node);
            cluster[size-1].push(1);
        }


        for (int i = 0; i < size-1; i++) {
            addCluster(arrayList);
            cluster[size-2-i] = new LinkedList();
            for (Point2D p: arrayList) {
                cluster[size-2-i].push(degree.get(p));
            }
        }

        StdDraw.show();

    }

    private void addCluster(ArrayList<Point2D> arrayList) {

        // Find Min edge and delete two point
        Edge minEdge = minPQ.delMin();
        while (!(arrayList.contains(minEdge.a) && arrayList.contains(minEdge.b))) {
            minEdge = minPQ.delMin();
        }

        // Compute centroid point
        double cx = (minEdge.a.x() * degree.get(minEdge.a) + minEdge.b.x() * degree.get(minEdge.b))/(degree.get(minEdge.a)+ degree.get(minEdge.b));
        double cy = (minEdge.a.y() * degree.get(minEdge.a) + minEdge.b.y() * degree.get(minEdge.b))/(degree.get(minEdge.a)+ degree.get(minEdge.b));
        Point2D centroid = new Point2D(cx,cy);
        int mass = degree.get(minEdge.a)+ degree.get(minEdge.b);


        // Add to tree
        TreeNode cluster = new TreeNode(centroid,tree.get(minEdge.a),tree.get(minEdge.b));
        tree.put(centroid,cluster);

        // Remove two points
        arrayList.remove(minEdge.a);
        arrayList.remove(minEdge.b);

        // Find the distance between the new centroid and all other points
        for (Point2D point2D : arrayList){
            Edge edge = new Edge(centroid,point2D);
            minPQ.insert(edge);
        }

        // Add the centroid into remain points
        arrayList.add(centroid);
        degree.put(centroid,mass);

        // Plot
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius(0.005);
        minEdge.a.drawTo(minEdge.b);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.01*Math.sqrt(mass));
        centroid.draw();
        StdDraw.show();
        StdDraw.pause(200);

    }

    // k is the number of clusters. return the number of nodes in each cluster (in ascending order)
    int[] cluster (int k) {
        if (k > size) {
            throw new IndexOutOfBoundsException();
        }

        int[] nodes = new int[k];
        LinkedList<Integer> linkedList = (LinkedList<Integer>) cluster[k-1].clone();

        for (int i = 0; i < k; i++) {
            int node = linkedList.pop();
            nodes[i] = node;
        }

        Arrays.sort(nodes);

        return nodes;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("input_HW7.txt") ; // file name assigned
        Scanner in = new Scanner(file);

        int size = Integer.parseInt(in.nextLine());
        Point2D[] points = new Point2D[size];

        StdDraw.setCanvasSize(600, 600);
        StdDraw.setXscale(0, 1.2);
        StdDraw.setYscale(-0.2, 1.2);
        StdDraw.setPenRadius(0.01);
        StdDraw.enableDoubleBuffering();

        for (int i=0;i<size;i++) {
            String[] point = in.nextLine().split(" ");
            double x  = Double.parseDouble(point[0]);
            double y  = Double.parseDouble(point[1]);
            points[i] = new Point2D(x,y);
            points[i].draw();
        }

        StdDraw.show();

        HierarchicalClusteringTree main = new HierarchicalClusteringTree(points);
        int a = 3;

        int[] clusters = main.cluster(a);

        for (int i : clusters){
            System.out.println(i);
        }

    }

}
