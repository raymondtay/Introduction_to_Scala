import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ConcurrentNumberService {
    AtomicIntegerArray arr = new AtomicIntegerArray(Integer.MAX_VALUE);
    public void process(int number) {
        arr.addAndGet(number, 1);
    }
 
    public int getProcessFrequency(int number) {
        return arr.get(number);
    }

} 

class Add implements Callable<Integer> {
    ConcurrentNumberService svc;
    Add(final ConcurrentNumberService svc) {
        this.svc = svc;
    }
    @Override
    public Integer call() throws Exception {
        svc.process(4);
        return svc.getProcessFrequency(4);
    }
}

class TestConcurrentNumberService {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentNumberService s = new ConcurrentNumberService();
        Vector<Add> tasks = new Vector<Add>();
        for(int i = 0; i < 4; ++i) 
            tasks.add(new Add(s));
        ForkJoinPool pool = new ForkJoinPool(8);
        pool.invokeAll(tasks);
        pool.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println(s.getProcessFrequency(4));
    }

}


