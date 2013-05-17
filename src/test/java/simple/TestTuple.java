package simple;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.*;
import org.junit.runners.Suite.*;
import org.junit.runners.JUnit4;

class Amphibian {}
class Vehicle {}

@RunWith(JUnit4.class)
public class TestTuple {
    static TwoTuple<String,Integer> 
    f() {
        return new TwoTuple<String,Integer>("hi", 42);
    }

    @Test
    public void test1() {
        TwoTuple<String, Integer> ttsi = f();
        String first  = ttsi.first;
        int second  = ttsi.second.intValue();
        assertEquals(first,"hi");
        assertEquals(second,42);
    }

}


