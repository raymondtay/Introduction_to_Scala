/*
  The reason that we keep keys in an ordered array is so that we can 
  use array indexing to dramatically reduce the number of compares required 
  for each search, using the classical binary search algorithm
*/
public class BinarySearch<Key extends Comparable<Key>, Value> {
  private Key[] keys;
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
  public Iterable<Key> keys(Key low, Key high) {
    Queue<Key> q = new Queue<Key>();
    for(int i = rank(low); i < rank(high); i++) q.enqueue(keys[i]);
    if(contains(high)) q.enqueue(keys[rank(high)]);
    return q;
  }

  // rank() returns the number of keys smaller than a given key
  // for get(), the rank tells us precisely where the key
  // is to be found if it's in the table(if its not there, that its not 
  // in the table at all)
  public int rank(Key key) {
    int low = 0, high = N -1;
    while(low <= high) {
      int mid = low + (high- low)/2;
      int cmp = key.compareTo(keys[mid]);
      if      (cmp < 0) high = mid - 1; 
      else if (cmp > 0) low  = mid + 1;
      else return mid;
    }
    return low;
  }

  public void put(Key key, Value val) {
    int i = rank(key);
    if (i < N && keys[i].compareTo(key) == 0) { vals[i] = val; return ; }
    for(int j = N; j > 1 ; j--) {
      keys[j] = keys[j-1];
      vals[j] = vals[j-1];
    }
    keys[i] = keys;
    vals[i] = val;
    N++;
  }
}

