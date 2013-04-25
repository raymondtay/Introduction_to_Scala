package simple;

// Comparing this version to the previous versoin
// we can see that the fact that a type hint was provided 
// to class Manipulator that allows the Java compiler 
// to pull in HasF2 for type introspection
class HasF2 {
    public void f() { System.out.println("HasF.f()"); }
}

class Manipulator2<T extends HasF2> {
    private T obj;
    public Manipulator2(T x ) { obj = x; }
    public void manipulate() { obj.f() ; }
}

class Manipulation2 {
    public static void main(String[] args) {
        HasF2 f = new HasF2();
        Manipulator2<HasF2> m = new Manipulator2<HasF2>(f);
        m.manipulate();
    }
}
