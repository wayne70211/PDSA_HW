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
    private TreeNode root;
    private LinkedList<TreeNode> list;


    private class Edge implements Comparable<Edge>{
        private Point2D a,b;
        private double distance;

        Edge(Point2D a, Point2D b){
            // comparable for two points distance
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

        // Add the minimum distance points into graph
        // Remember the cluster number

        size = point2DS.length;
        minPQ = new MinPQ<>();
        degree = new Hashtable<>();
        tree = new Hashtable<>();
        list = new LinkedList<>();

        double minDistance;
        ArrayList<Point2D> arrayList = new ArrayList<>();
        Arrays.sort(point2DS,Point2D.Y_ORDER);

        // Compute all distance between all two points
        for (int i = 0; i < size; i++) {
            minDistance = Double.POSITIVE_INFINITY;
            for (int j = i+1; j < size; j++) {
                Edge edge = new Edge(point2DS[i],point2DS[j]);
                if (edge.distance < minDistance) {
                    minDistance = edge.distance;
                    minPQ.insert(edge);
                }
            }
            TreeNode cluster = new TreeNode(point2DS[i]);
            arrayList.add(point2DS[i]);
            degree.put(point2DS[i],1);
            tree.put(point2DS[i],cluster);
            list.push(cluster);
        }


        for (int i = 0; i < size-1; i++) {
            addCluster(arrayList);
        }
        StdDraw.show();

    }

    private void addCluster(ArrayList<Point2D> arrayList) {

        // Find Min edge and delete two point
        Edge minEdge = minPQ.delMin();
        while (!(arrayList.contains(minEdge.a) && arrayList.contains(minEdge.b))) {
            minEdge = minPQ.delMin();
        }

        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius(0.005);
        minEdge.a.drawTo(minEdge.b);

        // Compute centroid point
        double cx = (minEdge.a.x() * degree.get(minEdge.a) + minEdge.b.x() * degree.get(minEdge.b))/(degree.get(minEdge.a)+ degree.get(minEdge.b));
        double cy = (minEdge.a.y() * degree.get(minEdge.a) + minEdge.b.y() * degree.get(minEdge.b))/(degree.get(minEdge.a)+ degree.get(minEdge.b));
        Point2D centroid = new Point2D(cx,cy);

        // Add to tree
        TreeNode cluster = new TreeNode(centroid,tree.get(minEdge.a),tree.get(minEdge.b));
        tree.put(centroid,cluster);
        list.push(cluster);

        // Root
        if (arrayList.size()<=2) {
            root = cluster;
        }

        arrayList.remove(minEdge.a);
        arrayList.remove(minEdge.b);

        // Find the distance between the new centroid and all other points
        int mass = degree.get(minEdge.a)+ degree.get(minEdge.b);

        for (Point2D point2D : arrayList){
            Edge edge = new Edge(point2D,centroid);
            minPQ.insert(edge);
        }

        // Add the centroid into remain points
        arrayList.add(centroid);
        degree.put(centroid,mass);

        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.01*Math.sqrt(mass));
        centroid.draw();
        StdDraw.show();
        StdDraw.pause(200);

    }




    // k is the number of clusters. return the number of nodes in each cluster (in ascending order)
    int[] cluster (int k) {
        int[] nodes = new int[k];
        LinkedList<TreeNode> linkedList = (LinkedList<TreeNode>) list.clone();

        if (k == 2) {
            TreeNode node = linkedList.pop();
            int total = node.size;
            node = linkedList.pop();
            nodes[0] = node.size;
            nodes[1] = total - node.size;
            Arrays.sort(nodes);
            return nodes;
        } else if (k == 1) {
            TreeNode node = linkedList.pop();
            nodes[0] = node.size;
            return nodes;
        }

        for (int i = 1; i < k; i++) {
            linkedList.pop();
        }

        for (int i = 0; i < k; i++) {
            TreeNode node = linkedList.pop();
            nodes[i] = node.size;
        }

        Arrays.sort(nodes);

        return nodes;
    }

    public static void main(String[] args) throws FileNotFoundException {
        //File file = new File("input_HW7.txt") ;// file name assigned
        File file = new File("test.txt") ;// file name assigned
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
        int[] clusters = main.cluster(2);


        for (int i = 0; i < clusters.length; i++) {
            System.out.println(clusters[i]);
        }



    }

}
