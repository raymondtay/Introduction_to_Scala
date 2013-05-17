package simple;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.*;
import org.junit.runners.Suite.*;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestGenericMethods {
    @Test
    public void test1() {
        GenericMethods gm = new GenericMethods();
        assertEquals(gm.f(""),"");
    }

}


