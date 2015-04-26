import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
/*
public class BoundedQueue<T> {
    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    AtomicInteger size;
    volatile Node head, tail;

    int capacity;
    public BoundedQueue(int capacity) {
        this.capacity = capacity;
        head = new Node(null);
        tail = head;
        size = new AtomicInteger(0);
        enqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();
        deqLock = new ReentrantLock();
        notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T x) {
        boolean mustWakeupDequeuers = false;
        enqLock.lock();
        try {
            while(size.get() == capacity) notFullCondition.await();

            Node e = new Node(x);
            tail.next = tail; tail = e;
            if (size.getAndIncrement() == 0) mustWakeupDequeuers = true;
        } finally {
            enqLock.unlock();
        }

        if(mustWakeupDequeuers) { 
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }
}
*/
