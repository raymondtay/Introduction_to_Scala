// 
// Because of erasure in generics, generic types cannot be used
// in operations that explicitly refer to runtime types, such as casts, instanceof
// operations, and new expressions. BEcause all the type information about the parameters
// is lost, whenever you are writing generic code you must constantly be reminding yourself that it 
// onlhy appears that you have type informaiton about a parameter
// class Foo<T> { T var; }
// T can only be an object regardless what type is bound to it in the source code
// 
class GenericBase<T> {
    private T element;  
    public void set(T arg) { arg = element; }
    public T get() { return element; }
}
class Derived<T> extends GenericBase<T> {}
class Derived2 extends GenericBase {} // surprisingly no warning, there should be though
public class ErasureAndInheritance {
    @SuppressWarnings("unchecked") 
    public static void main(String[] args) {
        Derived2 d2 = new Derived2();
        Object on = d2.get();
        d2.set(on);
    }
}

