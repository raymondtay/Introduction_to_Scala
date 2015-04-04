
import java.util.*;

public class BST<Key extends Comparable<Key>, Value> {
  private Node root;
  private class Node {
    private Key key;
    private Value val;
    private Node left, right;
    private int N;
    public Node(Key key, Value val, int N) { 
     this.key = key; this.val = val; this.N = N; 
    }
  }
  public void put(Key key, Value val) { 
    root = put(root, key, val);
  }
  public int size() { return size(root); }
  private int size(Node x) { if (x==null) return 0; else return x.N; }
  public Value get(Key key) { return get(root, key); }
  public boolean contains(Key key) { 
    return get(key) != null; 
  } 
  private Value get(Node x, Key key) { 
    if (x == null) return null;
    int cmp = key.compareTo(x.key);
    if      (cmp < 0) return get(x.left, key);
    else if (cmp > 0) return get(x.right, key);
    else return x.val;
  }
  private Node put(Node x,Key key, Value val ) { 
    if (x == null) return new Node(key, val, 1);
    int cmp = key.compareTo(x.key);
    if      (cmp < 0) x.left  = put(x.left, key, val);
    else if (cmp > 0) x.right = put(x.right, key, val);
    else x.val = val;
    x.N = size(x.left) + size(x.right) + 1;
    return x;
  }

  // the minimum element is definitely 
  // found in the left of any tree
  public Key min() { return min(root).key; }

  // keep following the left tree
  private Node min(Node x) { 
    if (x.left == null) return x;
    return min(x.left);
  }

  private Node floor(Node x, Key key) {
    if (x == null) return null;
    int cmp = key.compareTo(x.key);
    if (cmp == 0) return x;
    if (cmp < 0) return floor(x.left, key);
    Node t = floor(x.right, key);
    if (t != null) return t;
    else return x;
  }

  public Key select(int k) { return select(root, k).key; }

  private Node select(Node x, int k) { 
    if (x == null) return null;
    int t = size(x.left); // this operation is O(lg n)

    /* for each invocation of 'select', i'm going to call size(..) once */
    if (t > k) return select(x.left, k);
    else if (t < k) return select(x.right, k);
    else return x;
  }

  public int rank(Key key) { return rank(key, root); }

  public void delete(Key key) {
    root = delete(key, root);
  }

  public void deleteMin() { root = deleteMin(root); }
  public void deleteMax() { root = deleteMax(root); }

  private Node deleteMin(Node x) { 
    if (x.left == null) return x.right; // for any node, the minimum is always on the left otherwise its on the right.
    x.left = deleteMin(x.left);
    x.N = size(x.left) + size(x.right) + 1;
    return x;
  }
  private Node deleteMax(Node x) { 
    if (x.right == null) return x.left; // for any node, the minimum is always on the left otherwise its on the right.
    x.right = deleteMax(x.right);
    x.N = size(x.left) + size(x.right) + 1;
    return x;
  }

  /* 
    The delete(Key, Node) method is based on Hibbard
    (a) Save a link to the node to be deleted in t
    (b) Set x to point to its successor min(t.right)
    (c) Set the right link of x (which is supposed to point to the BST
        containing all the keys larger than x.key) to deleteMin(t.right), the link
        to the BST containing all the keys that are larger than x.key after the deletion
    (d) Set the left link of x (which was null) to t.left 
        (all the keys that are less than both the deleted 
         keys and its successor).
  */ 
  private Node delete(Key key, Node x) { 
    if (x== null) return null;
    int cmp = key.compareTo(x.key);
    // depending on whether the key we are looking to delete
    // is on the left-/right-subtree, we go look for it;
    // and when we do find it, we examine its children
    // and if any of 2 children is not present, we return the other as its successor
    // but if the located node has 2 children then we look for the minimum
    // element in the left-subtree of this located node
    if (cmp < 0 ) x.left = delete(key, x.left);
    else if (cmp > 0) x.right = delete(key, x.right);
    else {
      if (x.right == null) return x.left;
      if (x.left == null) return x.right;
      Node t = x;
      x = min(t.right);
      x.right = deleteMin(t.right);
      x.left = t.left;
    } 
    x.N = size(x.left) + size(x.right) + 1;
    return x;
  }

  private int rank(Key key, Node x) {
    // returns the number of keys less than x.key in the subtree
    // rooted at x.
    if (x == null ) return 0;
    int cmp = key.compareTo(x.key);
    if (cmp < 0) return rank(key, x.left);
    else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right);
    else return size(x.left);
  }
  public static void main(String[] args) { 
    Random r = new Random(42);
    BST<Integer, Integer> bst = new BST<Integer, Integer>();
    for(int i = 0; i < 100; i++) 
      bst.put(new Integer(r.nextInt(100000)), new Integer(r.nextInt(100000)));
 
    for(int i = 0; i < 100; i++) 
      System.out.println("Index: " + i + ", we have " + bst.get(new Integer(i)));

  }

}

