public class SequentialSearch<Key,Value> {
  private Node first;
  private class Node {
    Key key;
    Value val ;
    Node next;
    public Node(Key key, Value val, Node next) {
      this.key = key; this.val = val; this.next = next;
    }
  }
  public Value get(Key key) { 
    for(Node x = first; x != null; x = x.next) 
      if (key.equals(x.key)) return x.val;
    return null;
  }

  public void put(Key key, Value val) {
    for(Node x = first; x != null; x = x.next) 
      if (key.equals(x.key)) {
        x.val = val;
        return;
      }
    first = new Node(key, val, first);
  }

  public static void main(String[] args) {
    SequentialSearch<Integer,Integer> ss = new SequentialSearch<Integer, Integer>();
    for(int i = 0; i < 1000; ++i) 
      ss.put(new Integer(i), new Integer(i));

    System.out.println(ss.get(new Integer(588)));
    System.out.println(ss.get(new Integer(2289)));

  }
}

