package simple;

import java.util.*;

// Type inference only happens in (explicit) variable assignments 
// and not when methods are invoked though arguably it can be thought
// of as variable assignment since the formal parameters are assigned the
// actual arguments; but whatever.

class Varargs {
    public static <T> 
    List<T> makeList(T... args) {
        List<T> r = new ArrayList<T>();
        for(T item: args) r.add(item);
        return r;
    }
}

class Person {}
class Pet {}
class New {
    public static <K,V> Map<K,V> map() { return new HashMap<K,V>(); }
}
public class simple {
    //static void f(Map<Person, List<? extends Pet>> petPeople) {}
    static void f(Map<Person, List<Pet>> petPeople) {}
    public static void main(String[] args) {
        // The following statement doesn't compile and gives the following
        // error message
        //     [error]   required: Map<Person,List<? extends Pet>>
        //     [error]   found: Map<Object,Object>
        //     [error]   reason: actual argument Map<Object,Object> cannot be converted to Map<Person,List<? extends Pet>> by method invocation conversion

        // f(New.map());
        // The following statement however does compile by providing explicit types
        // and providing the following method signature to 
        // static void f(Map<Person, List<Pet>> petPeople) {}
        f(New.<Person, List<Pet>>map());
    }
}

