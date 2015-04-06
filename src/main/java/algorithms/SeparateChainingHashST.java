/* 
  In a separate chaining has table with M lists and N keys, 
  the probability that the number of keys in a list is within a small
  constant factor of N/M is extremely close to 1.
  But this is dependent on the fact that hash functions 
  we use uniformly and independently distributes keys among 
  the integer values between 0 and M - 1.
*/
public class SeparateChainingHashST<Key, Value> {
  private int N;
  private int M;
  private SequentialSearch<Key,Value>[] st;
  public SeparateChainingHashST() { this(997); }
  public SeparateChainingHashST(int M) {
    this.M = M;
    st = (SequentialSearch<Key,Value>[]) new SequentialSearch[M];
    for(int i = 0; i < M; ++i) st[i] = new SequentialSearch();
  }
  private int hash(Key key) { return (key.hashCode() & 0x7fffffff) % M; }
  private Value get(Key key) { return (Value) st[hash(key)].get(key); }
  public void put(Key key, Value val) { 
    st[hash(key)].put(key, val);
  }
}
