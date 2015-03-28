
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
    if (cmp < 0) return get(x.left, key);
    else if (cmp > 0) return get(x.right, key);
    else return x.val;
  }
  private Node put(Node x,Key key, Value val ) { 
    if (x == null) return new Node(key, val, 1);
    int cmp = key.compareTo(x.key);
    if (cmp < 0) x.left = put(x.left, key, val);
    else if (cmp > 0) x.right = put(x.right, key, val);
    else x.val = val;
    x.N = size(x.left) + size(x.right) + 1;
    return x;
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

