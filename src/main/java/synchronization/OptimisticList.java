package synchronization;
/** 
    Optimistic synchronization
    via a List<T> object.
    @author Raymond Tay
    @version 1.0
*/

import java.util.concurrent.locks.*;

class ONode<T> {
    private Lock lock = new ReentrantLock();
    public void lock() { lock.lock(); }
    public void unlock() { lock.unlock(); }
    public ONode(T value) {
        item = value;
        key = toInt(value);
    }

    private int toInt(T value) { return Integer.parseInt(value.toString()); }

    T item;
    int key;
    ONode next;
}

public class OptimisticList<T> {
    private ONode head;
    public OptimisticList() {
        head = new ONode<Integer>(Integer.MIN_VALUE);
        head.next  = new ONode<Integer>(Integer.MAX_VALUE);
    }

    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            ONode pred = head;
            ONode curr = pred.next;
            while(curr.key < key) {
                pred = curr;
                curr = curr.next;
            }

            pred.lock(); curr.lock();
            try {
                if (validate(pred, curr)) {
                    if (curr.key == key) { return false; }
                    else {
                        ONode node = new ONode(item);
                        node.next = curr;
                        pred.next = node;
                        return true;
                    }
                }
            } finally {
                pred.unlock(); curr.unlock();
            }
        }
    }

    private boolean validate(ONode pred, ONode curr) {
        ONode node = head;
        while(node.key <= pred.key) {
            if (node == pred) return pred.next == curr;
            node = node.next;
        }
        return false;
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            ONode pred = this.head ;
            ONode curr = pred.next;
            while(curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock(); curr.lock();
            try {
                if(validate(pred, curr)) {
                    return (curr.key == key);
                } 
            } finally {
                pred.unlock(); curr.unlock();
            }
        }
    }

    // travers the structure, ignoring locks,
    // acquires locks and validates before removing the node.
    public boolean remove(T item) {
        int key = item.hashCode();
        while(true) {
            ONode pred = head;
            ONode curr = pred.next;
            while(curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock(); curr.lock();
            try {
                if(validate(pred, curr)) {
                    if (curr.key == key) {
                        pred.next = curr.next;
                        return true;
                    } else { return false; }
                }
            } finally { 
                pred.unlock(); curr.unlock();
            }
        }
    }
}

