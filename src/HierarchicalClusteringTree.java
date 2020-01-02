import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HierarchicalClusteringTree {
    private int size;
    private MinPQ<Edge> minPQ;
    private final Hashtable<Point2D, Integer> table;


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


    // create clustering tree
    HierarchicalClusteringTree(Point2D[] point2DS) {

        // Add the minimum distance points into graph
        // Remember the cluster number

        size = point2DS.length;
        minPQ = new MinPQ<>();
        table = new Hashtable<>();

        double minDistance;
        ArrayList<Point2D> arrayList = new ArrayList<>();

        // Arrays.sort(point2DS,Point2D.Y_ORDER);

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
            arrayList.add(point2DS[i]);
            table.put(point2DS[i],1);
        }


        for (int i = 0; i < 18; i++) {
            addCluster(arrayList);
        }


    }

    private void addCluster(ArrayList<Point2D> arrayList) {

        // Find Min edge and delete two point
        Edge minEdge = minPQ.delMin();
        while (!(arrayList.contains(minEdge.a) && arrayList.contains(minEdge.b))) {
            minEdge = minPQ.delMin();
        }
        arrayList.remove(minEdge.a);
        arrayList.remove(minEdge.b);
        System.out.println(arrayList.size());
        System.out.println("-------|--------");
        System.out.println(minEdge.a.x());
        System.out.println(minEdge.b.x());

        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setPenRadius(0.005);
        minEdge.a.drawTo(minEdge.b);


        // Compute centroid point
        double cx = (minEdge.a.x() * table.get(minEdge.a) + minEdge.b.x() * table.get(minEdge.b))/(table.get(minEdge.a)+table.get(minEdge.b));
        double cy = (minEdge.a.y() * table.get(minEdge.a) + minEdge.b.y() * table.get(minEdge.b))/(table.get(minEdge.a)+table.get(minEdge.b));
        Point2D centroid = new Point2D(cx,cy);


        // Find the distance between the new centroid and all other points
        int mass = table.get(minEdge.a)+table.get(minEdge.b);
        Edge min = new Edge(arrayList.get(0),centroid);
        double minDistance = min.distance;
        for (Point2D point2D : arrayList){
            Edge edge = new Edge(point2D,centroid);
            if (edge.distance < minDistance ) {
                minDistance = edge.distance;
                min = edge;
            }
        }
        minPQ.insert(min);


        // Add the centroid into remain points
        arrayList.add(centroid);
        table.put(centroid,mass);

        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.01*Math.sqrt(mass));
        centroid.draw();
        StdDraw.show();
        StdDraw.pause(200);
        System.out.println(arrayList.size());

    }

    // k is the number of clusters. return the number of nodes in each cluster (in descending order)
    int[] cluster (int k) {
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("test.txt") ;// file name assigned
        Scanner in = new Scanner(file);

        int size = Integer.parseInt(in.nextLine());
        Point2D[] points = new Point2D[size];

        StdDraw.setCanvasSize(500, 500);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
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


    }

}
