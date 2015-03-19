
/*
 QuickSort is a recursive program that sorts a subarray a[low..high] by using a 
 partition() method that puts a[i] into position and arranges the rest of the entries
 such that the recursive calls finish the sort
*/

public class QuickSort {

    public static void sort(Comparable[] a) { 
        sort(a, 0, a.length - 1);
    }
    private static boolean less(Comparable a, Comparable b) { return a.compareTo(b) < 0; }

    private static void exchange(Comparable[] a, int i, int j) {
        Comparable x = a[i];
        a[i] = a[j];
        a[j] = x;
    }
    private static int partition(Comparable[] a, int low, int high) {
        int i = low, j = high + 1;

        Comparable v = a[low];
        while(true) {
            while( less(a[++i],v) ) if (i == high) break;
            while( less(v, a[--j]) ) if (j == low) break;
            if (i >= j) break;
            exchange(a, i, j);
        }
        exchange(a, low, j);
        return j;
    }

    private static void sort(Comparable[] a, int low, int high) {
        if (high <= low) return;
    
        int pivot = partition(a, low, high);
        sort(a, low, pivot - 1);
        sort(a, pivot + 1, high);
    }

}

