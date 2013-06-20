package opencl_computation

import java.nio.ByteOrder
import com.nativelibs4java.opencl._
import com.nativelibs4java.opencl.util._
import com.nativelibs4java.util._
import org.bridj.Pointer
import org.bridj.Pointer._
import java.lang.Math._ /* no import static here */

class AddFloats_Kernel extends App {
/*
    override def main(args: Array[String] ) = {
        val context: CLContext = JavaCL.createBestContext
        val queue: CLQueue = context.createDefaultQueue()
        val byteOrder: ByteOrder = context.getByteOrder

        val n = 1024
        val aPtr = allocateFloats(n).order(byteOrder)
        val bPtr = allocateFloats(n).order(byteOrder)

        for(i <- 0 to n) {
            aPtr.set(i, cos(i).asInstanceOf[scala.Float])
            bPtr.set(i, sin(i).asInstanceOf[scala.Float])
        }

        val a: CLBuffer[Float] = context.createBuffer(Usage.Input, aPtr)
        val b: CLBuffer[Float] = context.createBuffer(Usage.Input, bPtr)

        val out: CLBuffer[Float] = context.createBuffer(Usage.Output, n)

        val src = IOUtils.readText(classOf[AddFloats_Kernel].getClassLoader.getResourceAsStream("addfloats.cl"))
        val program = context.createProgram(src)

        val kernel: CLKernel = program.createKernel("add_floats")
        kernel.setArgs(a, b, out, n:java.lang.Integer)
        val event: CLEvent = kernel.enqueueNDRange(queue, Array[Int]())

        val outPtr : Pointer[Float] = out.read(queue, event)

        for(i <- 0 to n)
          println(s"out[%d] = %d", i, outPtr.get(i))
    }
*/
}
