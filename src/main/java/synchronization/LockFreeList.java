package synchronization;
/** 
    Non-blocking via a List<T> object.

    @author Raymond Tay
    @version 1.0
*/

import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

class Window {
    public LockFreeNode pred, curr;
    Window(LockFreeNode myPred, LockFreeNode myCurr) {
        pred = myPred; curr = myCurr;
    }
}

class LockFreeNode<T> {
    public Boolean marked = false; // indicates whether the item is in the set. true => yes, false => no
    private Lock lock = new ReentrantLock();
    public void lock() { lock.lock(); }
    public void unlock() { lock.unlock(); }
    public LockFreeNode(T value) {
        item = value;
        key = toInt(value);
    }

    private int toInt(T value) { return Integer.parseInt(value.toString()); }

    T item;
    int key;
    AtomicMarkableReference<LockFreeNode> next;
}

public class LockFreeList<T> {
    private LockFreeNode head;
    public LockFreeList() {
        head = new LockFreeNode<Integer>(Integer.MIN_VALUE);
    }

    // lock-free add(T) 
    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Window window = find(head, key);
            LockFreeNode pred = window.pred, curr = window.curr;
            if (curr.key == key) { return false; } else {
                LockFreeNode node = new LockFreeNode(item);
                node.next = new AtomicMarkableReference(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true; 
                }
            }
        }
    }

    // wait-free method
    public boolean contains(T item) {
        boolean[] marked = {false};
        int key = item.hashCode();
        LockFreeNode<T> curr = head;
        while(curr.key < key) {
            curr = curr.next.getReference();
            LockFreeNode succ = curr.next.get(marked);  
        }
        return (curr.key == key && !marked[0]);
    }

    // calls find() to locate pred, and curr
    // and atomically marks the node for removal
    // also lock-free
    public boolean remove(T item) {
        int key = item.hashCode();
        boolean snip;
        while(true) {
            Window window = find(head, key);
            LockFreeNode<T> pred = window.pred, curr = window.curr;
            if (curr.key != key) { return false; }
            else {
                LockFreeNode succ = curr.next.getReference();
                snip = curr.next.compareAndSet(succ, succ, false, true);
                if (!snip) continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    // returns a structure containing the nodes
    // on either side of the key. it removes marked nodes
    // when it encounters them.
    public Window find(LockFreeNode head, int key) {
        LockFreeNode<T> pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;

        retry : while(true) {
            pred = head;
            curr = pred.next.getReference();
            while(true) {
                succ = curr.next.getReference();
                while(marked[0]) {
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if (!snip) continue retry;
                    curr = succ;
                    succ = curr.next.get(marked);
                }
                if(curr.key >= key) return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }

}

