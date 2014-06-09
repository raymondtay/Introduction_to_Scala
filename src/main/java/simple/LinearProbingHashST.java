public class LinearProbingHashST<Key, Value> {
    private int N;
    private int M = 16;
    private Key[] keys;
    private Value[] values;
    public LinearProbingHashST(int X) {
        keys = (Key[]) new Object[X];
        values = (Value[]) new Object[X];
    }
    public LinearProbingHashST() {
        keys = (Key[]) new Object[M];
        values = (Value[]) new Object[M];
    }
    private int hash(Key key) { return (key.hashCode() & 0x7fffffff) % M; }
    public void resize(int X) {
        LinearProbingHashST<Key, Value> temp;
        temp = new LinearProbingHashST<Key, Value>(X);
        for(int i = 0; i < M; ++i) if(keys[i] != null) temp.put(keys[i], values[i]);
        keys = temp.keys;
        values = temp.values;
        M = temp.M;
    }
    private void resize() {}
    public void put(Key key, Value value) {
        if (N >= M/2) resize(2 * M);
        int i;
        for(i = hash(key); keys[i] != null; i = (i + 1) % M) 
            if (keys[i].equals(key)) { values[i] = value; return ; }
        keys[i] = key;
        values[i] = value;
        N++;
    }
    public Value get(Key key) {
        for(int i = hash(key); keys[i] != null; i = (i + 1 ) % M)
            if(keys[i].equals(key)) return values[i];
        return null;
    }
    private boolean contains(Key key) {
        for(int i = 0; i < M; ++i) 
            if(keys[i].equals(key)) return true;
        return false;
    }

    public void delete(Key key) {
        if (!contains(key)) return;
        int i = hash(key);
        while(!key.equals(keys[i])) i = (i + 1) % M;
        keys[i] = null; values[i] = null;
        i = (i + 1) % M;
        while( keys[i] != null) {
            Key keyToRedo = keys[i];
            Value valueToRedo = values[i];
            keys[i] = null; values[i] = null;
            N --;
            put(keyToRedo, valueToRedo);
            i = (i + 1) % M;
        }
        N--;
        if (N > 0 && N == M/8) resize(M/2);
    }
}

