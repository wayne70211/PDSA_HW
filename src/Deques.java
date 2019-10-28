import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Deques<Item> implements Iterable<Item> {
    private Node<Item> first;    // beginning of queue
    private Node<Item> last;     // end of queue
    private int n;               // number of elements on queue

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
        private Node<Item> previous;
    }

    // construct an empty deque
    public Deques(){
        first = null;
        last  = null;
        n = 0;
    }

    // is the deque empty?
    public boolean isEmpty(){
        return first == null || last == null;
    }

    // return the number of items on the deque
    public int size(){
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        assertNullPointerException(item);
        Node<Item> oldFirst = first;
        first = new Node<>();
        first.item = item;
        first.next = oldFirst;
        first.previous = null;
        if (oldFirst == null){
            last = first;
        } else {
            oldFirst.previous = first;
        }
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        assertNullPointerException(item);
        Node<Item> oldLast = last;
        last = new Node<>();
        last.item = item;
        last.next = null;
        if (oldLast == null)  {
            first = last;
        } else {
            last.previous = oldLast;
            oldLast.next = last;
        }
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        assertNoSuchElementException();
        Item item = first.item;
        first = first.next;
        n--;
        if (first == null) {
            last = null;   // to avoid loitering
        } else {
            first.previous = null;
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        assertNoSuchElementException();
        Item item = last.item;
        last = last.previous;
        n--;
        if (last == null) {
            first = null;       // to avoid loitering
        } else {
            last.next = null;
        }
        return item;
    }

    private void assertNoSuchElementException() {
        if (isEmpty()){
            throw new NoSuchElementException();
        }
    }

    private void assertNullPointerException(Item item) {
        if (item == null) {
            throw new NullPointerException();
        }
    }

    //return the fist item of the deque
    public Item peekFirst() {
        if (isEmpty()) {
            return null;
        }
        return first.item;
    }

    //return the last item of the deque
    public Item peekLast() {
        if (isEmpty()) {
            return null;
        }
        return last.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator(){
        return new ListIterator(first);
    }

    private class ListIterator implements Iterator<Item> {
        private Node<Item> current;

        public ListIterator(Node<Item> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }
    }


    // Sliding window
    public static void main (String[] args) throws FileNotFoundException {
        //File file = new File(args[0]) ;// file name assigned

        File file = new File("input_HW3.txt") ;
        Scanner in = new Scanner(file);

        // Size of sliding window
        int k = Integer.parseInt(in.nextLine());
        Deques<Integer> array = new Deques<>();
        int[] subArray = new int[k];

        while (in.hasNextLine()) {
            array.addLast(Integer.parseInt(in.nextLine()));
        }

        int size = array.size();

        int currentMax = array.removeFirst();
        subArray[0] = currentMax;

        // Sliding array
        for (int i=1;i<k;i++) {
            int temp = array.removeFirst();
            subArray[i] = temp;
            if (temp > currentMax) currentMax = temp;
        }

        System.out.println(currentMax);

        for (int i=0;i<size-k;i++) {
            int last  = array.removeFirst();
            int first = subArray[i%k];
            subArray[i%k] = last;

            if (first == currentMax) {
                currentMax = subArray[0];
                for (int j=1;j<k;j++) {
                    if (subArray[j] > currentMax) currentMax = subArray[j];
                }
            }

            if (last > currentMax) currentMax = last;
            System.out.println(currentMax);
        }
    }
}