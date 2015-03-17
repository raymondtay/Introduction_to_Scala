public class MergeSort {

    private static boolean less(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }

    // this version uses a temporary array whose size is proportional to its length, N.
    // and it only does 1 thing, to merge two (sorted) arrays into one array.
    public static void merge(Comparable[] a, int low, int mid, int high) {
        int i = low;
        int j = mid + 1;
        Comparable[] aux = new Comparable[a.length];
        for( int k = low; k <= high; ++k )  // copy a[low..high] to aux[low..high]
            aux[k] = a[k];

        for(int k = low; k <= high; ++k)
            if      (i > mid)             a[k] = aux[j++];
            else if (j > high)            a[k] = aux[i++];
            else if (less(aux[j],aux[j])) a[k] = aux[j++];
            else                          a[k] = aux[i++];

    }

    public static void sort(Comparable[] a, int low, int high) {
        if (high <= low) return;
        int mid = low + (high - low)/2;
        sort(a, low, mid);
        sort(a, mid+1, high);
        merge(a, low, mid, high);
    }

    public static void main(String[] args) {
        Comparable[] data = new Comparable[100000]; // took < 5 seconds on my OSX Intel Core i7 @ 2.7Ghz
        for(int i =0; i < data.length; ++i) 
            data[i] = i;
        sort(data, 0, data.length -1);
    }

}
