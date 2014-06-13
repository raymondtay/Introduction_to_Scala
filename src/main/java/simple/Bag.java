import java.util.Iterator;
import java.util.NoSuchElementException;

public class Bag<Item> implements Iterable<Item> {
    private int N; // number of elements in bag
    private Node<Item> first; // beginning of bag

    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    public Bag() { 
        N = 0; first = null;
    }

    public Iterator<Item> iterator() { return new ListIterator<Item>(first); }

    public class ListIterator<Item> implements Iterator<Item> {
        private Node<Item> current;
        public ListIterator(Node<Item> first) { current = first ; }
        public boolean hasNext() { return current != null; }
        public void remove() { throw new UnsupportedOperationException(); }

        public Item next() { 
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        } 
    }

    public boolean isEmpty() { return first == null; }

    public int size() { return N; }

    public void add(Item item) {
        Node<Item> oldFirst = first;
        first = new Node<Item>();
        first.item = item;
        first.next = oldFirst;
    }
}


