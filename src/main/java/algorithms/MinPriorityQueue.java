public class MinPriorityQueue<Key extends Comparable<Key>> implements Iterable<Integer> {
  private int NMAX; // maximum number of elements on PQ
  private int N; // nujmber of elements on PQ
  private int[] pq; // binary heap using 1-based indexing
  private int[] qp; // inverse of pq
  private Key[] keys; // keys[i] = priority of i

  public int minIndex() { 
    if (N == 0) throw new NoSuchElementException("Priorituy Queue underflow");
    return pq[1];
  }

  public void insert(int i, Key key) {
    if(i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
    if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
    N++;
    qp[i] = N;
    pq[N] = i;
    keys[i] = key;
    swim(N);
  }

  public MinPriorityQueue(int NMAX) {
    if (NMAX < 0) throw new IllegalArgumentException();
    this.NMAX = NMAX;
    keys = (Key[]) new Comparable[NMAX + 1];
    pq = new int[NMAX+1];
    qp = new int[NMAX+1];
    for(int i =0; i <= NMAX; ++i) qp[i] = -1;
  }

  public int size() { return N; }

  public Key minKey() {
    if (N == 0) throw new NoSuchElementException("priority queue underflow")
    return keys[pq[1]];
  }

  public boolean contains(int i) { 
    if (i < 0 || i >= NMAX) throw new IndexOutOfBoundsException();
    return qp[i] != -1;
  }

  private boolean isEmpty() { return N == 0; }

} 
