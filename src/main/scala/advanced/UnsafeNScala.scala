
package using.unsafe

import java.lang.reflect.Field
import sun.misc.Unsafe

class UnsafeScala{
    def getUnsafe : Unsafe = {
        val f : Field = classOf[Unsafe].getDeclaredField("theUnsafe")
        f.setAccessible(true)
        f.get(classOf[UnsafeScala]).asInstanceOf[Unsafe]
    }
}

class Something {
    val s = 
    """
    You should see this message normally
    but it can be suppressed via the sun.misc.Unsafe APIs
    """
    println(s)
}

object UnsafeDemo extends App {
    val unsafe = (new UnsafeScala).getUnsafe
    unsafe.allocateInstance(classOf[Something]) 
    // new Something // uncomment this line and you'll see constructor invocation as per JVM specification

    val INT_SIZE = 4
    val address = unsafe.allocateMemory(1 * 1024 * 1024 * 1024 * INT_SIZE)
    unsafe.setMemory(address, 1 * 1024 * 1024 * 1024 * INT_SIZE, 0)
    Thread.sleep(25000)
    unsafe.freeMemory(address)
}


