
/* 
 The binary heap is a data structure that can efficient support 
 the basic priority queue operations. In a binary heap, the keys are stored
 in an array s.t. each key guaranteed to be larger than (or equal to) the keys
 at two other specific positions. In turn, this ordering is easy to see if we  
 view as being in a binary structure with edges from each key to the keys is known to be
 smaller
*/
import java.util.Random;

public class MaxPriorityQueue<Key extends Comparable<Key>> {
  private Key[] pq;
  private int N = 0;
  public MaxPriorityQueue(int maxN) { 
    pq = (Key[]) new Comparable[maxN+1];
  }
  public boolean isEmpty() { return N == 0; }
  public int size() { return N; }

  // place the new item into the array and keep
  // "swim" the entire structure to re-order stuff.  
  public void insert(Key k) { pq[++N] = k; swim(N); }
  public Key delMax() { 
    Key max = pq[1];
    exchange(1, N--);
    pq[N+1] = null;
    sink(1);
    return max;
  }
  private boolean less(int i, int j) { return pq[i].compareTo(pq[j]) < 0; }
  private void exchange(int i, int j) {
    Key t = pq[i];
    pq[i] = pq[j];
    pq[j] = t;
  }
  // starting from a position, k, we check whether
  // the parent is less than k and if so we exchange them
  // and proceed till we reach the root
  private void swim(int k) {
    while(k > 1 && less(k/2, k)) { // check whether parent < child
      exchange(k/2, k);            // switch places and loop again
      k = k/2;
    }
  }
  private void sink(int k) {
    while(2*k <= N) {
      int j = 2*k;
      if (j < N && less(j, j+1)) j++; // check whether left-child < right-child
      if (!less(k, j)) break; // break the loop if parent >= left-child
      exchange(k, j);         // otherwise parent and left-child switch places
      k = j;
    }  
  }

  public static void main(String[] args) {
    MaxPriorityQueue<Integer> q = new MaxPriorityQueue<Integer>(1000);
    Random r = new Random(42);
    for(int i = 0; i < 1000; i++ ) {
      int n = r.nextInt(1000);
      q.insert(n);
      System.out.println(n + " inserted into PQ");
    }
    System.out.println(q.delMax());
  }
}

