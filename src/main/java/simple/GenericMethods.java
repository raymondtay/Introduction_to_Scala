package simple;

public class GenericMethods {
    public <T> T f(T x) {
        System.out.println(x.getClass().getName());
        return x;
    }
}

