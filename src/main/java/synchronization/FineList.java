package synchronization;
/** 
    Fine-grained synchronization
    via a List<T> object using a "hand-over" protocol
    i.e. hold onto lock-A while attempting to grab the lock-B
        and releasing lock-A after acquiring lock-B
    @author Raymond Tay
    @version 1.0
*/

import java.util.concurrent.locks.*;

class FNode<T> {
    private Lock lock = new ReentrantLock();
    public void lock() { lock.lock(); }
    public void unlock() { lock.unlock(); }
    public FNode(T value) {
        item = value;
        key = toInt(value);
    }

    private int toInt(T value) { return Integer.parseInt(value.toString()); }

    T item;
    int key;
    FNode next;
}

public class FineList<T> {
    private FNode head;
    public FineList() {
        head = new FNode<Integer>(Integer.MIN_VALUE);
        head.next  = new FNode<Integer>(Integer.MAX_VALUE);
    }

    public boolean add(T item) {
        int key = item.hashCode();
        head.lock();
        FNode pred = head;
        try {
            FNode curr = pred.next;
            curr.lock();
            try {
	            while( curr.key < key) {
	                pred.unlock();
	                pred = curr; 
	                curr = curr.next;
	                curr.lock();
	            }
	            if (key == curr.key) { return false; }
	            FNode newNode = new FNode(item);
	            newNode.next = curr;
	            pred.next = newNode;
                return true;
            } finally {
                curr.unlock();
            }
        } finally { 
            pred.unlock(); 
        }
    }

    public boolean remove(T item) {
        FNode pred = null, curr = null;
        int key = item.hashCode();
        head.lock();
        try {
            pred = head;
            curr = pred.next;
            curr.lock();
            try {
                while(curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if(curr.key == key) {
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }
}

