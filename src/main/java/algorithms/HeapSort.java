/*
 This code sorts a[1] through a[N] using the sink() method . The 
 for loop constructs the heap; then the while-loop exchanges the 
 largest element a[1] with a[N] and then repairs the heap, 
 continuing until the heap is empty. Decrementing the array indices
 in the implementations of exchange() and less() gives an implementation
 that sorts a[0] through a[N-1].
*/
class HeapSort {
  private static void sort(Comparable[]a) {
    int N = a.length;
    for(int k = N/2; k >= 1; k--) sink(a, k, N);
    while(N > 1) {
      exchange(a, 1, N--);
      sink(a, 1, N);
    }
  }
  private static boolean less(Comparable[] pq, int i, int j) { return pq[i-1].compareTo(pq[j-1]) < 0; }
  private static void exchange(Object[] pq, int i, int j) {
    Object t = pq[i-1];
    pq[i-1] = pq[j-1];
    pq[j-1] = t;
  }
  private static void sink(Comparable[] pq, int k, int N) {
    while(2*k <= N) {
      int j = 2*k;
      if (j < N && less(pq, j, j+1)) j++; // check whether left-child < right-child
      if (!less(pq, k, j)) break; // break the loop if parent >= left-child
      exchange(pq, k, j);         // otherwise parent and left-child switch places
      k = j;
    }  
  }


}
