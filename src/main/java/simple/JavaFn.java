
import java.util.*;

interface Fn<T,R> {
    R apply(T a);
    R f(T a);
}

class AddOneFunction implements Fn<Integer,Integer> {
    public Integer apply(Integer a) { return f(a); }
    public Integer f(Integer a) { return a + 1; }
}

public class JavaFn {
    public static void main(String[] args) { 

        Vector<Integer> list = new Vector<>();
        list.add(1);list.add(2);list.add(3);

        Fn<Integer,Integer> addOne = new AddOneFunction();

        for(Integer i = 0; i <  list.size(); ++i) {
            list.set(i, addOne.apply(list.get(i)));
        }

        for(Integer i = 0; i <  list.size(); ++i) {
            System.out.println("value at index: " + i + ", is " + list.get(i));
        }
    }
}


