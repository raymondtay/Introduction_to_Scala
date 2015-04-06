
public class LinearProbingHashST<Key, Value> {
  private int N;
  private int M;
  private Key[] keys;
  private Value[] vals;
  public LinearProbingHashST() { 
    keys = (Key[]) new Object[M];
    vals = (Value[]) new Object[N];
  }

  private int hash(Key key) { return (key.hashCode() & 0x7fffffff) % M; }

  private void resize(int cap) {
    LinearProbingHashST<Key,Value> t;
    t = new LinearProbingHashST<Key,Value>(cap);
    for(int i = 0; i < M; ++i) if (keys[i] != null) t.put(keys[i], vals[i]);
    keys = t.keys;
    vals = t.vals;
    M    = t.M;
  }

  private boolean contains(Key key) { 
    boolean found = false;
    for(int i = 0; i < M; ++i) 
      if (key.equals(keys[i])) { found = true; break; }
    return found;
  }

  public void delete(Key key) { 
    if (!contains(key)) return;
    int i = hash(key);
    while(!key.equals(keys[i])) i = (i + 1) % M;

    keys[i] = null;
    vals[i] = null;
    i = (i + 1) % M;
    while(keys[i] != null) {
      Key keyToRedo = keys[i];
      Value valueToRedo = vals[i];
      N--;
      put(keyToRedo, valueToRedo);
      i = (i + 1) % M;
    }
    N--;
    if( N > 0 && N == M/8) resize(M/2);
  }

  public void put(Key key, Value val) {
    if (N >= M/2) resize(2*M); // resize iff we are two times bigger
    int i = 0; 

    for(i = hash(key); keys[i] != null; i = (i + 1) % M) 
      if (keys[i].equals(key)) { vals[i] = val; return; }
    keys[i] = key;
    vals[i] = val;
    N++;
  }

  public Value get(Key key) {
    for(int i = hash(key); keys[i] != null; i = (i + 1) % M) 
      if (keys[i].equals(key)) return vals[i];
  }
}
