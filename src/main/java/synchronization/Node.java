package synchronization;

public class Node<T> {
    public Node(T value) {
        item = value;
        key = toInt(value);
    }

    private int toInt(T value) { return Integer.parseInt(value.toString()); }

    T item;
    int key;
    Node next;
}

