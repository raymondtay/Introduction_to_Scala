
public class RedBlackBST<Key extends Comparable<Key>, Value> {
  private Node root;

  // General Scheme
  // "red" links which bind together two 2-nodes to represent 3 nods
  // "black" links which bind together the 2-3 tree.

  // COLOR REPRESENTATION
  // It's "true" if the link from the parent is "red"
  // and "false" if its "black".
  private static final boolean RED = true;
  private static final boolean BLACK = false;
  private int size() { return size(root); }
  private int size(Node x) { if (x==null) return 0; else return x.N; }

  private class Node {
    Node(Key key, Value val, int N, boolean color) { this.key = key; this.val = val; this.N = N; this.color = color; }
    Key key;
    Value val;
    Node left, right;
    int N; 
    boolean color;
  }

  private boolean isRed(Node x) { 
    if (x == null) return false;
    return x.color == RED;
  }
  
  Node rotateLeft(Node h) {
    Node x = h.right;
    x.left = h;
    x.color = h.color;
    h.color = RED;
    x.N = h.N;
    h.N = 1 + size(h.left) + size(h.right);
    return x;
  }

  Node rotateRight(Node h) { 
    Node x = h.left;
    h.left = x.right;
    x.right = h;
    x.color = h.color;
    h.color = RED;
    x.N = h.N;
    h.N = 1 + size(h.left) + size(h.right);
    return x;
  }
  public void put(Key key, Value val) { 
    root = put(root, key, val);
    root.color = BLACK;
  }

  private Node put(Node h, Key key, Value val) {
    if (h == null) return new Node(key, val, 1, RED);
    int cmp = key.compareTo(h.key);
    if (cmp < 0) h.left = put(h.left, key, val);
    else if (cmp > 0) h.right = put(h.right, key, val);
    else h.val = val;
    if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
    if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
    if (isRed(h.left) && isRed(h.right)) flipColors(h);
    h.N = size(h.left) + size(h.right) + 1;
    return h;
  }
  void flipColors(Node h) {
    h.color = RED;
    h.left.color = BLACK;
    h.right.color = BLACK;
  }
}



