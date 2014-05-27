public class BinarySearchTree<Key extends Comparable<Key>, Value> {
    private Node root;
    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        private int N;
        public Node(Key key, Value val, int N) { this.key = key; this.val= val; this.N = N; }
    }

    public int size() { return size(root); }

    public int size(Node x) { if (x == null) return 0; else return x.N; }

    public void put(Key key, Value value) {
        root = put(root, key, value);
    }

    public Value get(Key key) { return get(root, key); }

    public Key min() { return min(root).key; }

    private Node min(Node x) { 
        if (x.left == null) return x;
        return min(x.left);
    }

    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null) return null;
        return x.key;
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

    private Value get(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x.val;
        else if (cmp < 0) return get(x.left, key);
        else return get(x.right, key);
    }

    private Node put(Node node, Key key, Value val) {
        if (node == null) return new Node(key, val, 1);
        int cmp = key.compareTo(node.key);
        if (cmp == 0) node.val = val; 
        else if (cmp < 0) node.left = put(node.left, key, val);
        else if (cmp >= 0) node.right = put(node.right, key, val);
        node.N = size(node.left) + size(node.right) + 1;
        return node;
    }

}

