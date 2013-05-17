package simple;

// This doesn't compile AT ALL
// [error] /Users/raymondtay/Introduction_to_Scala/src/main/java/simple/UnderstandExtends.java:10: error: cannot find symbol
// [error]     public void manipulate() { obj.f() ; }
// [error]                                   ^
// [error]   symbol:   method f()
// [error]   location: variable obj of type T
// [error]   where T is a type-variable:
// [error]     T extends Object declared in class Manipulator

class HasF {
    public void f() { System.out.println("HasF.f()"); }
}

class Manipulator<T> {
    private T obj;
    public Manipulator(T x ) { obj = x; }
    //public void manipulate() { obj.f() ; }
}

class Manipulation {
    public static void main(String[] args) {
        //HasF f = new HasF();
        //Manipulator<HasF> m = new Manipulator<HasF>(f);
        //m.manipulate();
    }
}
