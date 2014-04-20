
class PPair<T,U> {
    private final T _1;
    private final U _2;

    public PPair(T first, U second) {this._1 = first; this._2 = second; }

    public T getFirst() { return _1; }
    public U getSecond() { return _2; }
}

public class PPairModule {
    
    public static void main(String[] args) {
        PPair<String,Integer> p = new PPair("", 1);
    }
}


