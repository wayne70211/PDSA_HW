import edu.princeton.cs.algs4.DepthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;

import java.io.*;

public class CriticalDistance {
    private static Point2D[] points;

    // comparable for two points distance
    private static class Edge implements Comparable<Edge>{
        private int adx,bdx;
        private double distance;

        Edge(int adx, int bdx){
            this.adx = adx;
            this.bdx = bdx;
            this.distance = points[adx].distanceTo(points[bdx]);
        }

        public int compareTo(Edge that) {
            return Double.compare(this.distance,that.distance);
        }

    }

    public static void main(String[] args) throws IOException {

        FileReader fileReader = new FileReader("input_HW8.txt"); // file name assigned
        //FileReader fileReader = new FileReader(args[0]); // file name assigned
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int size = Integer.parseInt(bufferedReader.readLine());
        points = new Point2D[size];

        int source = 0;
        int target = size;

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double xy;

        MinPQ<Edge> minPQ = new MinPQ<>();
        Edge edge = null;

        // Read file
        for (int i=0;i<size;i++) {
            String[] point = bufferedReader.readLine().split(" ");
            double x  = Double.parseDouble(point[0]);
            double y  = Double.parseDouble(point[1]);
            points[i] = new Point2D(x,y);
        }


        // Add edge to MinPQ
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                if (points[i].x() < points[j].x() && points[i].y() < points[j].y()) {
                    edge = new Edge(i,j);
                    minPQ.insert(edge);
                }
                if (points[i].x() > points[j].x() && points[i].y() > points[j].y()) {
                    edge = new Edge(j,i);
                    minPQ.insert(edge);
                }
            }

            xy = points[i].x() + points[i].y();

            // Find max and min nodes
            if (xy < min) {
                source = i;
                min = xy;
            } else if (xy > max) {
                target = i;
                max = xy;
            }
        }

        // Create Directed Graph
        Digraph digraph = new Digraph(size);
        DepthFirstDirectedPaths paths;

        // Add min edge to graph
        for (int i = 0; i < minPQ.size(); i++) {
            edge = minPQ.delMin();
            digraph.addEdge(edge.adx, edge.bdx);
            paths = new DepthFirstDirectedPaths(digraph,source);

            // If the edge connected to target , return the edge
            if (paths.hasPathTo(target)) {
                break;
            }
        }

        // Return the distance
        double d = edge.distance;
        System.out.printf("%5.5f\n", d);

    }
}
