// See the Scala counterpart defined in src/main/scala/data_structures/Tree.scala
// Under the umbrella of the same SBT config, we cannot have duplicate class names
// so we named our tree from "Tree" to "TreeA"
class TreeA<A> {
    A value;
    TreeA<A> left;
    TreeA<A> right;

    public TreeA(A v, TreeA<A> lhs, TreeA<A> rhs) {
        left = lhs;
        right = lhs;
        value = v;
    }
}

class TreeGen {
    static TreeA<String> simpleT() { 
        return new TreeA<String>("root", new TreeA<String>("left hand side", null, null), new TreeA<String>("right hand side", null, null));
    }
}

