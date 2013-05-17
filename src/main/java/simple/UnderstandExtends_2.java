package simple;

// Comparing this version to the previous versoin
// we can see that the fact that a type hint was provided 
// to class Manipulator that allows the Java compiler 
// to pull in HasF2 for type introspection
class HasF2 {
    public void f() { System.out.println("HasF.f()"); }
}

// What happens is that the type parameter erases to its first-bound
// and in this example T erases to HasF2; it's also possible to have
// multiple bounds on the type parameter
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

/////////////////////////////
// Binary compatibility
/////////////////////////////
// To allay any potential confusion about erasure, you must clearly understand that it is not a
// language feature. It is a compromise in the implementation of Java generics.
// In an erasure based implementation, generic types are treated as second class types that cannot be used
// in some important contexts. The generic types are present only during static type checking after 
// which every generic type in the program is erased by replacing it with a non-generic upper bound.

// The core motivation for erasure is that it allows generified clients to be used with non-generified
// libraries and vice versa. 
