package simple;

// this basically shields you from writing
// new BasicGenerator<Type>(Type.class) 
// and instead allows you to write
// BasicGenerator.create(Type.class)
public class BasicGenerator<T> implements Generator<T> {
    private Class<T> type;
    public BasicGenerator(Class<T> type) { this.type = type; }
    public T next() {
        try {
            return type.newInstance();
        } catch (Exception e) { throw new RuntimeException(e) ; }
    }

    public static <T> 
    Generator<T> create(Class<T> type) { return new BasicGenerator<T>(type); }
}

class TestBasicGenerator {
    public static void main(String[] args) {
        Generator<String> ss = BasicGenerator.create(String.class);
        System.out.println(ss);
    }
}

