package new_scala210

object ValueClass {
	// Value classes are the latest addition to the Scala 2.10 language
	// The purpose for their existence is to avoid runtime allocation of objects.
	
	// Value classes are defined by subtyping from 'AnyVal'
	// The parameter 'underlying' is the runtime representation of the
	// compile-time type 'Wrapper'
	class Wrapper(val underlying : Int) extends AnyVal 
	
	// Value classes can define def's but no val's, var's or nested
	// trait's class's or object's
	
	class Wrapper2(val underlying : Int) extends AnyVal {
	    def foo: Wrapper2 = new Wrapper2(underlying * 42)
	}
	
	// A value class can only extend universal traits and 
	// cannot be extended itself. A universal trait is a trait that
	// extends 'Any', only has def's as members and does not initialization.
	// Universal traits allow simple inheritance of methods for value classes
	// but they incur the overhead of allocation. 
	trait Printable extends Any {
	    def print(): Unit = println(this)
	}
	class Wrapper3(val underlying : Int) extends AnyVal with Printable
	
	val w = new Wrapper3(42)
	w.print()

    // One use case for value classes is to combine them with implicit classes for allocation
    // free extension methods. Using an implicit class provides a more convenient syntax for defining
    // extension methods, while value classes remove the runtime overhead. A good example is the
    // RichInt class in the standard library. RichInt extends Int type with several methods.
    // Because it is a value class, an instance of RichInt doesn't need to be created when
    // using RichInt methods.
    // Let's solidify our understanding with this example from RichInt
    // class RichInt(val self: Int) extends AnyVal {
    //     def toHexString : String = java.lang.Integer.toHexString(self)
    // }
    // The above snippet is taken from the Scala source code base
    // and a common operation we do is to allow the expression: 3.toHexString
    // and what happens at runtime is that '3.toHexString' is optimized to the 
    // equivalent of a method call on a static object (RichInt$.MODULE$.extension$toHexString(3))
    // rather than a method call on a newly instantiated object.

    // Another use case for value classes is to get the type safety of a data type w/o
    // the runtime allocation overhead. The code example would avoid the runtime creation
    // of object instances but rather adds the two doubles together
    class Meter(val value: Double) extends AnyVal {
        def +(m: Meter) : Meter = new Meter(value + m.value)
    }
    val x = new Meter(3.4)
    val y = new Meter(3.5)
    val z = x + y

    // As the JVM does not support value classes directly, there are cases
    // where Scala will generate classes and everything else will follow
    // the usual object interaction. The following list defines when a value
    // class will be instantiated 
    // 1. a value class is treated as another type
    // 2. a value class is assigned to an array
    // 3. doing runtime type tests, such as pattern matching

    // whenever a value class is treated as another type, including a universal trait
    // an instance of the value class must be instantiated
    trait Distance extends Any
    case class Meter2(val value: Double) extends AnyVal with Distance
    // the expression below will generate value classes
    def addD(d1: Distance, d2: Distance) : Distance = new Meter2(d1.asInstanceOf[Meter2].value + d2.asInstanceOf[Meter2].value)
    // the expression below will not generate value classes
    def addM(d1: Meter2, d2: Meter2) : Meter2 = new Meter2(d1.value + d2.value)

    // another situation applies when a value class is used as a type
    // argument. The example below illustrates this:
    def identity[T](t: T) : T = t
    identity(Meter2(5.0))

    // another situation where allocation is necessary is when assigning to 
    // an array, even if it is an array of value class
    val m = Meter2(5.0)
    val arrayOfMeter = Array[Meter2](m) // the array's elements type are all of Meter2 instead of Double
    
    case class P(val i: Int) extends AnyVal
    val p = new P(3)
    p match {
        case P(3) => println("Matched 3")
        case P(x) => println("Not 3")
    }

	// A value class …
	// 
	// … must have only a primary constructor with exactly one public, val parameter whose type is not a value class.
	// … may not have specialized type parameters.
	// … may not have nested or local classes, traits, or objects
	// … may not define a equals or hashCode method.
	// … must be a top-level class or a member of a statically accessible object
	// … can only have defs as members. In particular, it cannot have lazy vals, vars, or vals as members.
	// … cannot be extended by another class.

}

object ValueClass_Demo2 {
    // Value classes in Scala are prohibited from having state
    // the following state declaration will fail at compile-time
    import scala.util.Random._
    var freeVar    : Long = _
    var freeVarStr : StringBuffer = _

    class StateVC(val a: Long) extends AnyVal {
        // uncomment the following statement & recompile to see failure
        //var state: StringBuffer = _
    }

    class ClosureVC_good(val a : Long) extends AnyVal {
        // As a matter of fact, JVM will perform operand promotion/demotion
        // i.e. implicit primitive widening or narrowing conversions
        // so as long as you operate on primitive types, you'll avoid runtime allocation
        // in this situation
        def bindMe() : Long = {freeVar += (nextLong() + a); freeVar }
    }

    class ClosureVC_bad(val a : Long) extends AnyVal {
        def biteMe() : String = freeVarStr.append(nextLong).toString + a
    }
}

