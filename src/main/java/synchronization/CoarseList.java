package synchronization;
/** 
    Coarse-grained synchronization
    via a List<T> object
    @author Raymond Tay
    @version 1.0
*/

import java.util.concurrent.locks.*;

public class CoarseList<T> {
    private Node head;
    private Lock lock = new ReentrantLock();
    public CoarseList() {
        head = new Node<Integer>(Integer.MIN_VALUE);
        head.next  = new Node<Integer>(Integer.MAX_VALUE);
    }

    public boolean add(T item) {
        Node pred, curr;
        int key = item.hashCode();
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while( curr.key < key) {
                pred = curr; curr = curr.next;
            }
            if (key == curr.key) { return false; } else {
                Node node = new Node(item);
                node.next = curr;
                pred.next = node;
                return true;
            }
        } finally { lock.unlock(); }
    }

    public boolean remove(T item) {
        Node pred, curr;
        int key = item.hashCode();
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while(curr.key < key) {
                pred = curr; curr = curr.next; 
            }
            if (key == curr.key) {
                pred.next = curr.next; return true;
            } else { return false; }
        } finally {
            lock.unlock();
        }
    }
}

