
class Pair<T,U> {
    private final T _1;
    private final U _2;

    public Pair(T first, U second) {this._1 = first; this._2 = second; }

    public T getFirst() { return _1; }
    public U getSecond() { return _2; }
}

public class PairModule {
    
    public static void main(String[] args) {
        Pair<String,Integer> p = new Pair("", 1);
    }
}


