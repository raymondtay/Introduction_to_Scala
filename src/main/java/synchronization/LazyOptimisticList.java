package synchronization;
/** 
    Lazy Optimistic synchronization
    via a List<T> object.

    The OptimistList implementation works best if the cost of traversing
    the list twice w/o blocking is significantly less than the cost of 
    traversing the list once with locking. 
    One drawback of this particular algorithm is that `contains()`
    acquires locks, which is unattractive since contains() calls are likely
    to be much more common than calls to other methods.

    The next step is to refine this algorithm so that contains() calls are
    wait-free, and add() and remove() methods while still blocking, traverse
    the list only once( in the absence of contention).

    The principal disadvantage of the LazyOptimisticList is that add() and remove()
    are blocking : if one thread is delayed, then others may also be delayed.
    @author Raymond Tay
    @version 1.0
*/

import java.util.concurrent.locks.*;

class LONode<T> {
    public Boolean marked = false; // indicates whether the item is in the set. true => yes, false => no
    private Lock lock = new ReentrantLock();
    public void lock() { lock.lock(); }
    public void unlock() { lock.unlock(); }
    public LONode(T value) {
        item = value;
        key = toInt(value);
    }

    private int toInt(T value) { return Integer.parseInt(value.toString()); }

    T item;
    int key;
    LONode next;
}

public class LazyOptimisticList<T> {
    private LONode head;
    public LazyOptimisticList() {
        head = new LONode<Integer>(Integer.MIN_VALUE);
        head.next  = new LONode<Integer>(Integer.MAX_VALUE);
    }

    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            LONode pred = head;
            LONode curr = pred.next;
            while(curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try {
                curr.lock();
                try {
	                if (validate(pred, curr)) {
	                    if (curr.key == key) { return false; }
	                    else {
	                        LONode node = new LONode(item);
	                        node.next = curr;
	                        pred.next = node;
	                        return true;
	                    }
                    }
                } finally { 
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    // `validate` no longer traverses the entire list incontra to OptimisticList
    private boolean validate(LONode pred, LONode curr) {
        return !pred.marked && !curr.marked && pred.next == curr;
    }

    // `contains` is now wait-free as the attempts to grab the locks are removed.
    // and we check the fact whether the node has been deleted.
    // @see [OptimisticList.contains]

    public boolean contains(T item) {
        int key = item.hashCode();
        LONode curr = head;
        while(curr.key < key) 
            curr = curr.next;
        return curr.key == key && !curr.marked;
    }

    // travers the structure, ignoring locks,
    // acquires locks and validates before removing the node.
    public boolean remove(T item) {
        int key = item.hashCode();
        while(true) {
            LONode pred = head;
            LONode curr = pred.next;
            while(curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try {
                curr.lock();
                try {
	                if(validate(pred, curr)) {
	                    if (curr.key != key) {
                            return false;
	                    } else {
                            curr.marked = true;
                            pred.next = curr.next;
                            return true;
                        }
	                }
                } finally {
                    curr.unlock();
                }
            } finally { 
                pred.unlock();
            }
        }
    }
}

