public class MergeSort {

    private static boolean less(Comparable a, Comparable b) {
        return a.compareTo(b) < 0;
    }
    private static boolean lessOrEqualTo(Comparable a, Comparable b) {
        return a.compareTo(b) < 0 || a.compareTo(b) == 0;
    }


    // this version uses a temporary array whose size is proportional to its length, N.
    // and it only does 1 thing, to merge two (sorted) arrays into one array.
    public static void merge(Comparable[] a, int low, int mid, int high, Comparable[] aux) {
        int i = low;
        int j = mid + 1;
        for( int k = low; k <= high; ++k )  // copy a[low..high] to aux[low..high]
            aux[k] = a[k];

        for(int k = low; k <= high; ++k)
            if      (i > mid)             a[k] = aux[j++];
            else if (j > high)            a[k] = aux[i++];
            else if (less(aux[j],aux[j])) a[k] = aux[j++];
            else                          a[k] = aux[i++];

    }

    // The runtime is about O(N lg N) and this is valid regardless
    // of the order of the elements in the array & values in the array.
    public static void sort(Comparable[] a, int low, int high, Comparable [] aux) {
        if (high <= low) return;
        int mid = low + (high - low)/2;
        sort(a, low, mid, aux);
        sort(a, mid+1, high, aux);

        // we modify a little to allow O(N) runtime
        // by checking if the a[mid] < a[mid+1] because if it were
        // then we do not need to conduct the merge routine => shaving the temporary array
        // needed and running the routine 
        // C(N) = C(floor(N/2)) + C(ceiling(N/2)) + N is the recurrence relation when `lessOrEqualTo` is false
        // C(N) = C(floor(N/2)) + C(ceiling(N/2))  is the recurrence relation when `lessOrEqualTo` is true
        //  
        if (lessOrEqualTo(a[mid], a[mid+1])) return;
        else merge(a, low, mid, high, aux);
    }

    public static void main(String[] args) {
        Comparable[] data = new Comparable[100000]; // took < 5 seconds on my OSX Intel Core i7 @ 2.7Ghz
        for(int i =0; i < data.length; ++i) 
            data[i] = i;
        Comparable[] aux = new Comparable[data.length];
        sort(data, 0, data.length -1, aux);
        for(int i =0; i < data.length; ++i) 
            System.out.println(data[i]+",");
    }

}
