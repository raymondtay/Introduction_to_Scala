/*
If the number of inversions in an array is less than a constant multiple of the array size, we say that the array is partially sorted.
Inversions. Develop and implement a linearithmic algorithm for computing the number of inversions in a given array (the number of exchanges that would be performed by insertion sort for that array
*/
import java.util.*;

class Pair<A,B> {
    Pair(A a, B b) { this.left = a; this.right = b; }
    private final A left;
    private final B right;
    public A getLeft() { return this.left; }
    public B getRight() { return this.right; }
}

public class Inversions {

    private static boolean isInversion(Comparable a, Comparable b) { 
        return a.compareTo(b) > 0;
    }

    private static ArrayList<Pair<Integer,Integer>> data = null;

    // split the array into 2 halves each time, 
    // search 1 halve followed by the other halve
    // break down till there 2 leaves and perform function
    // isInversion(x, [x1,x2]) where [x1,x2] are the leaves 
    public static void findAllInversions(int x, Comparable[] a, int low, int high) {
        if (low == high) return ;
        int mid = a.length/2;
       
        findInversions(x, a, low, mid);
        findInversions(x, a, mid+1, high);
    }

    private static void findInversions(int x, Comparable[] a, int low, int high) {
        //System.out.println("Examining indices ("+low+","+high+") with comparison index "+ x);
        if ( high == low || Math.abs(high-low) == 1) {
            if (x > low && isInversion(a[low], a[x])) {
                //System.out.println("Checking index " + x + " is smaller than "+ low);
                data.add(new Pair<Integer,Integer>(low,x));
            }
            else if (x < high && isInversion(a[x], a[high]))  {
                //System.out.println("Checking index " + x + " is greater than "+ high);
                data.add(new Pair<Integer,Integer>(high,x));
            }
            else data.add(new Pair<Integer,Integer>(-99,-99)); // sentinel value
        } else {
            int mid = low + (high-low)/2;
            findInversions(x, a, low, mid);
            findInversions(x, a, mid+1, high);
        }
    }

    public static void main(String[] args) {
        data = new ArrayList<Pair<Integer,Integer>>();
        int size = 10;
        int count = size; 
        Comparable[] a = new Comparable[count]; 
        for(int i = 0; i < size; ++i, --count) 
            a[i] = count;
        for(int i = 0 ; i < size; i++)
            findAllInversions(i, a, 0, a.length-1);
        System.out.println("Number of inversions detected: " + data.size() + ", for a array of size: " + size);
        for(Pair<Integer,Integer> p : data)  {
            if (p.getLeft() != -99) System.out.println("("+p.getLeft() +"," + p.getRight()+")");
        }
    }

}

