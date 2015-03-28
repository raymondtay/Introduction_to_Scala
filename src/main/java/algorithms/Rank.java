public class Rank<Key extends Comparable> {
  private Comparable[] keys; 

  public int rank(Key key, int low, int high) {
    if (high < low) return low;
    int mid = low + (high - low)/2;
    int cmp = key.compareTo(keys[mid]);
    if (cmp < 0 ) return rank(key, low, mid+1);
    else if (cmp > 0) return rank(key, mid+1, high);
    else return mid;
  }

  public static void main(String[] args) {
  }

}

