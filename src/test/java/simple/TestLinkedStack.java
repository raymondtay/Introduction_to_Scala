package simple;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.*;
import org.junit.runners.Suite.*;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TestLinkedStack {
    static LinkedStack<String> 
    f() {
        return new LinkedStack<String>();
    }

    @Test
    public void test1() {
        LinkedStack<String> ss = f();
        String testString = "Phasers on stun!"; 
        for(String s : testString.split(" ")) ss.push(s);

        String s;
        while((s = ss.pop())!= null) assertEquals(s,s);
    }

}


