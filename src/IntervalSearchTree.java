import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class IntervalSearchTree<Key extends Comparable<Key>,Value> {

    private Node<Key,Value> root;
    private TreeMap<Key,Value> treeMap;

    private static class Node<Key extends Comparable<Key>,Value> {
        private Node<Key,Value> left,right;
        private Value val;
        private Key lo,hi,max;

        private boolean intersects(Node<Key,Value> target) {
            if (target.hi.compareTo(this.lo) < 0) return false;
            if (this.hi.compareTo(target.lo) < 0) return false;
            return true;
        }

        private boolean contains(Node<Key,Value> target) {
            return target.hi.compareTo(this.hi) <= 0 && target.lo.compareTo(this.lo) >= 0;
        }

        private boolean containedBy(Node<Key,Value> target) {
            return target.hi.compareTo(this.hi) >= 0 && target.lo.compareTo(this.lo) <= 0;
        }
    }

    // create interval search tree
    IntervalSearchTree() {
        treeMap = new TreeMap<>();
    }

    // put interval-value pair into ST
    void put(Key lo, Key hi, Value val) {
        checkInterval(lo, hi);
        Node<Key,Value> target = new Node<>();
        target.lo = lo;
        target.hi = hi;
        target.val = val;
        target.max = hi;
        // Compare
        if (get(root,target) != null) delete(lo,hi);
        root = put(root,target);
        treeMap.put(lo,val);

    }

    private void checkInterval(Key lo, Key hi) {
        if (lo.compareTo(hi) > 0) throw new IllegalArgumentException();
    }

    private Node<Key,Value> put(Node<Key,Value> current, Node<Key,Value> target) {
        if (current == null) {
            current = target;
        }

        if (target.lo.compareTo(current.lo) < 0 ) {
            current.left  = put(current.left , target);
            if (current.max.compareTo(current.left.max) < 0) {
                current.max = current.left.max;
            }
        } else if (target.lo.compareTo(current.lo) > 0 ) {
            current.right = put(current.right, target);
            if (current.max.compareTo(current.right.max) < 0) {
                current.max = current.right.max;
            }
        }

        return current;
    }

    // value paired with given interval
    Value get(Key lo, Key hi) {
        checkInterval(lo, hi);
        Node<Key,Value> target = new Node<>();
        target.lo = lo;
        target.hi = hi;
        Node<Key,Value> current = get(root,target);
        if (current == null) return null;
        return current.val;
    }

    private Node<Key,Value> get(Node<Key,Value> current, Node<Key,Value> target) {
        if (current == null) {
            return null;
        }

        if (target.lo.compareTo(current.lo) < 0 ) {
            return get(current.left , target);
        } else if (target.lo.compareTo(current.lo) > 0 ) {
            return get(current.right, target);
        } else {
            if (target.hi.compareTo(current.hi) == 0) return current;
            else return null;
        }
    }

    // delete the given interval
    void delete(Key lo, Key hi) {
        checkInterval(lo, hi);
        Node<Key,Value> target = new Node<>();
        target.lo = lo;
        target.hi = hi;
        root = delete(root, target);
        treeMap.remove(lo);
    }

    private Node<Key,Value> delete(Node<Key,Value> current, Node<Key,Value> target) {
        if (current == null) {
            throw new NoSuchElementException();
        }

        if (target.lo.compareTo(current.lo) < 0 ) {
            current.left  = delete(current.left , target);
        } else if (target.lo.compareTo(current.lo) > 0 ) {
            current.right = delete(current.right, target);
        } else {
            if (target.hi.compareTo(current.hi) == 0) {
                if (current.left == null)  return current.right;
                if (current.right == null) return current.left;
                Node<Key,Value> temp = current;
                current = min(temp.right);
                current.right = deleteMin(temp.right);
                current.left  = temp.left;
            } else {
                throw new NoSuchElementException();
            }
        }

        return current;
    }

    private Node<Key,Value> min(Node<Key,Value> x) {
        if (x.left == null) return x;
        else                return min(x.left);
    }

    private Node<Key,Value> deleteMin(Node<Key,Value> x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        return x;
    }

    // all intervals that intersect the given interval
    Iterable<Value> intersects(Key lo, Key hi) {
        LinkedList<Value> linkedList = new LinkedList<>();
        Node<Key,Value> target = new Node<>();
        target.lo = lo;
        target.hi = hi;
        intersects(root,target,linkedList);
        return linkedList;
    }

    private boolean intersects(Node<Key,Value> current, Node<Key,Value> target,LinkedList<Value> list) {
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        if (current == null)
            return false;
        if (current.intersects(target)) {
            list.add(current.val);
            found1 = true;
        }
        if (current.left != null && current.left.max.compareTo(target.lo) >= 0 )
            found2 = intersects(current.left, target, list);
        if (found2 || current.left == null || current.left.max.compareTo(target.lo) < 0)
            found3 = intersects(current.right, target, list);
        return found1 || found2 || found3;
    }

    // all intervals that contain the given interval
    Iterable<Value> contains(Key lo, Key hi) {
        LinkedList<Value> linkedList = new LinkedList<>();
        Node<Key,Value> target = new Node<>();
        target.lo = lo;
        target.hi = hi;
        contains(root,target,linkedList);
        return linkedList;
    }

    private boolean contains(Node<Key,Value> current, Node<Key,Value> target,LinkedList<Value> list) {
        boolean found1 = false;
        boolean found2;
        boolean found3;

        if (current == null)
            return false;

        if (current.contains(target)) {
            list.add(current.val);
            found1 = true;
        }

        found2 = contains(current.left, target, list);
        found3 = contains(current.right, target, list);

        return found1 || found2 || found3;
    }


    // all intervals that are contained by the given interval
    Iterable<Value> containedBy(Key lo, Key hi) {
        LinkedList<Value> linkedList = new LinkedList<>();
        Node<Key,Value> target = new Node<>();
        target.lo = lo;
        target.hi = hi;
        containsBy(root,target,linkedList);
        return linkedList;
    }

    private boolean containsBy(Node<Key,Value> current, Node<Key,Value> target,LinkedList<Value> list) {
        boolean found1 = false;
        boolean found2;
        boolean found3;

        if (current == null) {
            return false;
        }

        if (current.containedBy(target)) {
            list.add(current.val);
            found1 = true;
        }

        found2 = containsBy(current.left, target, list);
        found3 = containsBy(current.right, target, list);

        return found1 || found2 || found3;
    }


    // in order traversal of the tree
    Iterable<Value> inorderTraversal() {
        if (root == null) throw new NoSuchElementException();
        LinkedList<Value> linkedList = new LinkedList<>();
        inorderTraversal(root,linkedList);
        return treeMap.values();
        //return linkedList;
    }

    private void inorderTraversal(Node<Key,Value> current,LinkedList<Value> list) {
        if (current == null) return;
        inorderTraversal(current.left,list);
        list.add(current.val);
        inorderTraversal(current.right,list);
    }

    public static void main(String[] args){
        IntervalSearchTree<Integer,String> main = new IntervalSearchTree<>();

        main.put(0, 3, "ACAC");
        main.put(2, 7, "TCAATG");
        main.put(5, 6, "AT");

        System.out.println("intersects");
        for (String s : main.intersects(1,4)){
            System.out.println(s);
        }

        System.out.println("contains");
        for (String s : main.contains(3, 5)){
            System.out.println(s);
        }

        System.out.println("containedBy");

        for (String s : main.containedBy(0, 6)){
            System.out.println(s);
        }

    }


}

