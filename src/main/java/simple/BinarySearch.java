import java.util.Queue;

public class BinarySearch<Key extends Comparable<Key>, Value> {
    private Key[] keys;
    private Key sentinel = (Key)new Comparable(){ public int compareTo(Object o) { return -1; } };
    private Value[] vals;
    private int N;

    public BinarySearch(int capacity) {
        keys = (Key[]) new Comparable[capacity];
        vals = (Value[]) new Object[capacity];
    }
    public int size() { return N; }
    public Value get(Key key) { 
        if (isEmpty()) return null;
        int i = rank(key);
        if (i < N && keys[i].compareTo(key) == 0) return vals[i];
        else return null;
    }
    private boolean isEmpty() { return keys.length == 0; }
    public int rank(Key key) {
        int lo = 0; 
        int hi = N - 1;
        while(lo <= hi) {
            int mid = lo + (hi - lo)/2;
            int cmp = key.compareTo(keys[mid]);
            if (cmp < 0) hi = mid - 1;
            else if (cmp > 0) lo = mid + 1;
            else return mid;
        }
        return lo;
    }

    public void put(Key key, Value val) { 
        int i = rank(key);
        if (i < N && keys[i].compareTo(key) == 0) { vals[i] = val; return; }
        for(int j = N; j > 1; j-- ) {
            keys[j] = keys[j-1];
            vals[j] = vals[j-1];
        }
        keys[i] = key;
        vals[i] = val;
        N++;
    }

    public Key min() { return keys[0]; }
    public Key max() { return keys[N - 1]; }
    public Key select(int k) { return keys[k]; }
    public Key ceiling(Key key) { int i = rank(key); return keys[i]; }
    public Key floor(Key key) { 
        int i  = rank(key); 
        if (i <= 0 || i >= N) return sentinel;
        else return keys[i-1];
    }
/*
    public Iterable<Key> keys(Key lo, Key hi) {
        Queue<Key> q = new Queue<Key>();
        int rankOfHi = rank(hi);
        for( int i = rank(lo); i < rankOfHi; ++i) q.enqueue(keys[i]);
        if (contains(hi)) q.enqueue(keys[rankOfHi]);
        return q;
    }
*/
}

