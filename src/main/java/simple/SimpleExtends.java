import java.util.*;

public class SimpleExtends {

    public static void test1() {

        // Refer to the `Get and Put` Principle
        List<Object> os = Arrays.<Object>asList(2, 31.4, "aaas");
        List<Integer> ints = Arrays.asList(4,2);
        // it works because `object` is a super type of `integer`
        // Object >: Number >: Integer is the type hierarchy
        // and the following works too !
        AnotherSimpleExtends.copy(os, ints);
        AnotherSimpleExtends.<Object>copy(os, ints);
        AnotherSimpleExtends.<Integer>copy(os, ints);
        AnotherSimpleExtends.<Number>copy(os, ints);


        List<?> list1 = AnotherSimpleExtends.Lists.factory();
        List<?> list2 = AnotherSimpleExtends.Lists.<Object>factory();
        List<Number> list3 = AnotherSimpleExtends.Lists.factory();
        list3.add(3.14);
    }
    public static void main(String[] args) {
    
    List<Integer> ints = new ArrayList<Integer>();
    ints.add(1);
    ints.add(2);
    List<? extends Number> nums = ints;
    //nums.add(3.14); // compile-time error since `? extends Number` means 
                    // that nums.add(x) cannot be done safely since there are many sub-types of Number
    assert ints.toString().equals("[1, 2, 3.14]");

        test1();
    }
}

class AnotherSimpleExtends {

    // why `dst` can be allowed to invoke 
    public static<T> void copy(List<? super T> dst, List<? extends T> src) {
        for( int i = 0; i < src.size(); i ++ ) 
            dst.set(i, src.get(i));
    }

    static class Lists {
        public static <T> List<T> factory() { return new ArrayList<T>(); }
    }
}

class ReverseWithTypeCapture {

    public static <T> void reverse(List<T> list) {
        List<T> tmp = new ArrayList<T>(list);
        for(int i = 0; i < list.size(); i ++ ) 
            list.set(i, tmp.get(list.size() - i -1));
    }

    // this is called type capture of the wildcard.
    // using method defined in `rev`
    public static void reverse_1(List<?> list) { 
        rev(list);
    }

    private static <T> void rev(List<T> list) {
        List<T> tmp = new ArrayList<T>(list);
        for(int i = 0; i < list.size(); i ++ ) 
            list.set(i, tmp.get(list.size() - i -1));
    }

}

