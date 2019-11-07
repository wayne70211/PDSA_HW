import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ConvexHull {

    public static int[] ConvexHullVertex(Point2D[] a) {

        // 回傳ConvexHullVertex的index set，
        // index 請依該點在a陣列中的順序編號：0, 1, 2, 3, 4, ....a.length-1
        HashMap<Point2D,Integer> hashMap = new HashMap<>();
        ArrayList<Point2D> VertexSet = new ArrayList<>();

        Point2D[] temp = a.clone();

        // Save the index of a
        for (int i=0;i<a.length;i++) {
            hashMap.put(a[i],i);
        }

        // Find minimum y
        Arrays.sort(temp, Point2D.Y_ORDER);
        // sort by the angle of the minimum y coordinate point
        Arrays.sort(temp, temp[0].polarOrder());

        int vertex = 0;

        for (Point2D point : temp) {
            while (vertex >= 2 && Point2D.ccw(VertexSet.get(vertex-2), VertexSet.get(vertex-1), point) < 0) {
                // If the point is not ccw to other vortex, the point is not vortex.
                vertex--;
                VertexSet.remove(vertex);
            }
            VertexSet.add(vertex,point);
            vertex++;
        }

        int[] set = new int[VertexSet.size()];

        for (int i=0;i<VertexSet.size();i++) {
            set[i] = hashMap.get(VertexSet.get(i));
        }

        return set;
    }

    public static void main(String[] args) throws FileNotFoundException {

        File file = new File("input_HW4.txt") ;// file name assigned

        Scanner in = new Scanner(file);
        double distance = Double.parseDouble(in.nextLine());
        int size = Integer.parseInt(in.nextLine());
        HashMap<Point2D,Integer> hashMap = new HashMap<>();
        Point2D[] points = new Point2D[size];

        // 1. read in the file containing N 2-dimentional points
        for (int i=0;i<size;i++) {
            String[] point = in.nextLine().split(" ");
            double x  = Double.parseDouble(point[0]);
            double y  = Double.parseDouble(point[1]);
            points[i] = new Point2D(x,y);
            hashMap.put(new Point2D(x,y),i);
        }

        // 2. create an edge for each pair of points with a distance <= d
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(points.length);
        for (int i=0;i<size;i++) {
            for (int j=i;j<size;j++) {
                if (points[i].distanceTo(points[j]) <=distance) {
                    uf.union(hashMap.get(points[i]),hashMap.get(points[j]));
                }
            }
        }

        HashSet<Integer> hashSet = new HashSet<>();

        for (int i = 0; i < size; i++) {
            hashSet.add(uf.find(i));
        }

        int N = 0;

        for (int idx : hashSet) {
            ArrayList<Point2D> arrayList = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (uf.connected(idx,j)) {
                    arrayList.add(points[j]);
                }
            }

            // find connected components (CCs) with a size >= 3
            if (arrayList.size() > 2) {
                Point2D[] temp = new Point2D[arrayList.size()];
                for (int i = 0; i < arrayList.size(); i++) {
                    temp[i] = arrayList.get(i);
                }

                // for each CC, find its convex hull vertices by calling ConvexHullVertex(a[])
                N += ConvexHullVertex(temp).length;
            }
        }

        // count the number of points in N serving as a convex hull vertex, print it
        System.out.println(N);

    }
}
