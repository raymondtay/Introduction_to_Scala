

/* An array-backed queue implementation */
public class Queue<Key> {
  Key[] repr;
  int last_index = 0;
  int default_capacity = 100;
  public Queue() { repr = (Key[]) new Object[default_capacity]; }
  public Queue(int capacity) { repr = (Key[]) new Object[capacity]; }
  public int size() { return repr.length; }
  private void resize() {
    int newlen = size() * 2;
    Key[] temp = (Key[])new Object[newlen]; // WARNING: unchecked typecast
    int len = size();
    for(int i = 0; i < len; i++) 
      temp[i] = repr[i];
    repr = temp; 
  }

  // O(n) operation, in general.
  public Key dequeue() {
    Key t = repr[0];
    Key[] t2 = (Key[]) new Object[repr.length];
    for(int i = 1; i < repr.length; ++i) 
     t2[i-1] = repr[i];
    repr = t2;
    return t;
  }
  private Key[] getCopy() { 
    Key[] copy = (Key[])new Object[repr.length];
    for(int i = 0; i < copy.length; ++i) copy[i] = repr[i];
    return copy;
  }
  // return a iterable-version of my queue
  public Iterable<Key> values() { 
    return new Iterable<Key>() {
    public java.util.Iterator<Key> iterator() {
    return 
	  new java.util.Iterator<Key>() {
	    int current_index = 0; 
	    Key[] copy = getCopy();
	    public boolean hasNext() { return copy[current_index] == null; }
	    public Key next() { return copy[current_index]; }
	    public void remove() { throw new UnsupportedOperationException("not supported!"); } 
	  };
    }};
  }
 
  // At the expense of a 4-byte counter which points to the
  // most recently added element, we can avoid the O(n) array scan
  public void enqueue(Key key) {
    repr[last_index++] = key;
  }

  public static void main(String[] args) {
    Queue<Integer> qi = new Queue<Integer>(100);
    for(int i = 0; i < 100; i ++) qi.enqueue(i); 
    for(int i = 0; i < 100; i ++) System.out.print(qi.dequeue() + ","); 
  }
}

