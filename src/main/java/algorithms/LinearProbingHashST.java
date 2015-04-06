
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

  private void resize() {}

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
