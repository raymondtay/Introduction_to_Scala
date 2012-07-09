import scala.collection.immutable.{Stream => _ }

trait Stream[A] {

  /* This is actually a Monad */

  def uncons: Option[(A, Stream[A])]
  def toList: List[A] = {
    uncons.map {
      case (hd, tl) => hd :: tl.toList
    }.getOrElse(Nil)
  }

  def foldRight[B](z: => B)(f: (A, => B) => B) : B = 
    uncons.map {
        case (h,t) => f(h, t.foldRight(z)(f))
    }.getOrElse(z)
     
  def exists(f: A => Boolean) : Boolean = foldRight(false)((h,t) => f(h) || t)
  def forall(f: A => Boolean) : Boolean = foldRight(true)((h, t) => f(h) && t)

  def takeWhile2(f: A => Boolean) : Stream[A] = 
    foldRight(Stream.empty[A])((h, t) => if (f(h)) Stream.cons(h, t.takeWhile2(f)) else Stream.empty[A])

  /*
    uncons.map {
        case (hd, tl) => if (f(hd)) {println("passed: " + hd); Stream.cons(hd, tl takeWhile2 f)} else Stream.empty[A]
    }.getOrElse(Stream.empty[A])
*/
    //logical error somewhere --------> foldRight(Stream.empty[A])((h,t) => if (f(h)) Stream.cons(h, t) else Stream.empty[A] )

    // The expression below doesn't work and it'll complain of variance issues
    // The workaround is to include the type annotation in 'Stream.empty[A]' so 
    // that the scalac compiler will infer that 'h' is of type 'A' and it works.
    // Again, type inference works better in Scala with curried parameter lists.
    // foldRight(Stream.empty)((h,t) => Stream.cons(h, t) )

  def map[B](f: A => B) : Stream[B] =
    foldRight(Stream.empty[B])((h,t) => Stream.cons(f(h), t))

  def filter(f: A => Boolean): Stream[A] = takeWhile2(f)
        
  def ++(s2: Stream[A]) : Stream[A] = 
    foldRight(s2)((h,t) => Stream.cons(h, t))

  def flatMap[B](f: A => Stream[B]): Stream[B] =
    foldRight(Stream.empty[B])((h,t) => f(h) ++ t)

  override def toString : String = {
       var sb = new StringBuilder
       sb.append("[")
       this.toList.foreach{ i => sb.append(i + " ,") }
       sb.append("]")
       sb.toString
  }

  def take(n: Int) : Stream[A] = {
    def takeIn(n : Int)(ss: Stream[A]): Stream[A] = {
      if (n == 0) Stream.empty[A]
      else 
      ss.uncons.map {
        case (h,t)  => Stream.cons(h, takeIn(n - 1)(t))
      }.getOrElse(Stream.empty[A])
    }
    takeIn(n)(this)
  }
}

object Stream {
  def empty[A]:Stream[A] = new Stream[A] {
    def uncons = None
  }

  // Non-strict evaluation of the arguments
  def cons[A](hd: => A, tl: => Stream[A]) : Stream[A] = 
    new Stream[A] {
        lazy val uncons = Some((hd, tl))
    }
 
  def apply[A](as: A*): Stream[A] = 
    if (as.isEmpty) empty
    else cons(as.head, apply(as.tail: _*))

  def constant[A](a: A) : Stream[A] = cons(a, constant(a))
  def from(i: Int) : Stream[Int] = cons(i, from(i + 1))
}


object TestStream extends App {
    val s = Stream(1,2,3,4,5,6,7,8,9,10)
    println(s.map(_ % 2 == 0))
    /*
        You're actually dealing with this evaluation
        f(1, f(2, f(3, f(4, f(5, f(6, f(7, f(8, f(9, f(10, Stream.empty[Boolean]))))))))))
        
        Here, the function 'f' is actually the body of 'map' which is

        Stream.cons(f(h), t)

        and the function 'f' is actually the function literal "_ % 2 == 0" often referred to 
        as a predicate function

        Evaluating the expression from right-to-left, you actually get a lazy list of bools
    */
    println(s.filter(_ % 2 == 0))

    println(s ++ Stream(11,12,13))

    println(s.flatMap(i => Stream(i*math.Pi)))

    lazy val ones : Stream[Int] = Stream.cons(1, ones)
    println("* " + ones.map(_ + 1).exists(_ % 2 == 0))
    println("* " + ones.forall( _ != 1))
    println("* " + ones.take(5) ) 
    
    println("* "+ ones.takeWhile2(_ == 1) ) 
    
    val r = Stream.from(5)
    println("* " +r.take(10))
}

