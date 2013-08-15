package simple;

import java.util.concurrent.CountDownLatch;

// The CountDownLatch allows the possibility of coordinating 
// threads to perform actions 
public class CDLDemo {

    public static void main(String[] args) throws InterruptedException {
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch end = new CountDownLatch(100);
            for(int i = 0; i < 100; i++ ) 
                new Thread(new Worker(start, end)).start();
        start.countDown(); // get all threads going!!!
        end.await(); // don't go off dying till everyone's back
    }

}

class Worker implements Runnable {
    CountDownLatch start;
    CountDownLatch end;
    Worker(CountDownLatch start, CountDownLatch end) {
            this.start = start;
            this.end = end;
    }

    public void run() {
        try {
            start.await();
            doWork();
            end.countDown();
        } catch (InterruptedException ie) {} 
    }

    void doWork() { System.out.println("I'm doing some work" + Thread.currentThread()); }
}

