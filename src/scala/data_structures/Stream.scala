import scala.collection.mutable._

// Laziness lets us separate the description of an expression from the evaluation of that expression. This gives 
// us a powerful ability - we may choose to describe an larger expression than we need, then evaluate only a portion of it.
// As an example, consider foldRight - we can implement this for Stream much like we did for List, but lazily
object Chapter5 {

	trait Stream[+A] {
	    def foldRight[B](z: => B)(f: (A, => B) => B) : B =
	        uncons match {
	            case Some((h, t)) => f(h, t.foldRight(z)(f))
	            case None => z
	        }
	    // This is about separating the concerns of describing
	    // a computation from the concern of evaluation 
	    // makes our descriptions more reusable than when these concerns 
	    // are intertwined.
	    def exists(p : A => Boolean) : Boolean = 
	        foldRight(false)((a,b) => p(a) || b) 
	    def forAll(p : A => Boolean) : Boolean = 
	        foldRight(false)((a,b) => p(a) || b)
	        
	    def uncons : Option[(A, Stream[A])]
	
	    def isEmpty : Boolean = uncons.isEmpty
	
	    def takeWhileViaFoldRight(p : A => Boolean) : Stream[A] = {
	        import Stream._
	        foldRight(empty[A])((e, l) => if (p(e)) cons(e,l) else empty)
	    }
	
	    def map[B](f: A => B ) : Stream[B] = {
	        import Stream._
	        foldRight(empty[B])((e,l) => cons(f(e), l))
	    }
	
	    // Because the implementations are incremental, chains of transformations
	    // will avoid fully instantiating the intermediate data structures. Let's 
	    // look at a simplified program trace for the motivating example we 
	    // started this chapter.
	    def filter[B](f: A => Boolean) : Stream[B] = {
	        import Stream._
	        foldRight(empty[B])((e, l) => if (f(e)) l else empty)
	    }
	
	    def append[B >:A](s: Stream[B]) : Stream[B] = 
	        foldRight(s)((e,l) => Stream.cons(e,l))
	
	    def flatMap[B](f: A => Stream[B]) : Stream[B] = 
	        foldRight(Stream.empty[B])((e,l) => f(e) append l)
	
	    def takeWhile(p : A => Boolean) : Stream[A] = {
	        def process(s: Stream[A]) : Stream[A] = s uncons match {
	            case Some((h, t)) if p(h) => Stream.cons(h, process(t))
	            case _ => Stream() 
	        }
	        process(this)
	    } 
	
	    def take(n: Int) : Stream[A] = uncons match {
	        case Some((h,t)) if n > 0 => Stream.cons(h, t.take(n - 1))
	        case _ => Stream.empty[A]
	    }
	
	   def toList : List[A] = {
	        var buf = ListBuffer[A]()
	        
	        // Two choices of we can implement the 'toList'
	        // feature.
	         def recurse2(l: Stream[A]) : List[A] = l uncons match {
	             case Some((h, t)) => buf += h; recurse2(t) 
	             case _ => buf toList
	         }
	         recurse2(this)
	
	        def recurse(s: Option[(A, Stream[A])]) : List[A] = uncons match {
	            case Some((h, t)) => h :: t.toList
	            case _ => Nil
	        }
	        recurse(uncons)
	    } 

        // general function for building streams.
        // Option is used to indicate when the Stream should be terminated, if at all.
        // The function unfold is the most general Stream-building function. Notice how
        // closely it mirrors the structure of the Stream data type.

        // while a recursive function consumes data and eventually terminates, a 
        // corecursive function produces data and coterminates.
        // We say that such a function is productive, which just means that we can always
        // evaluate more of the result in a finite amount of time. Corecursion is also 
        // sometimes regarded as guarded recursion.
        def unfold[A,S](z: S)(f: S => Option[(A, S)]) : Stream[A] =
            f(z) match {
                case Some((h, t)) => Stream.cons(h, unfold(t)(f))
                case None => Stream()
            }

        def takeWhileViaUnfold(f: A => Boolean) : Stream[A] = 
            unfold(this){
                x => x.uncons match {
                                    case Some((h, t)) if f(h) => Some((h,t))
                                    case _ => None
                    }
            }

        def zipWith[B,C](other: Stream[B])(f: (A,B) => C) : Stream[C] = 
            unfold((this,other)){ 
                case (l,r) => 
                            (l.uncons, r.uncons) match {
                                    case ( Some((h1,t1)), Some((h2, t2)) ) => Some(( f(h1,h2), (t1,t2) ))
                                    case _ => None 
                }
            }
        def takeViaUnfold(n: Int) : Stream[A] =
            unfold((this,n)){ 
                case(s,n) if n > 0 => s.uncons map { case (h,t) => (h, (t, n -1)) }
                case _     => None
            }

        def mapViaUnfold[B](f: A => B ) : Stream[B] = 
            unfold(this)(_.uncons map { case (h,t) => (f(h),t) })
	}
	object Stream {
	    def unfold[A,S](z: S)(f: S => Option[(A, S)]) : Stream[A] =
            f(z) match {
                case Some((h, t)) => cons(h, unfold(t)(f))
                case None => Stream()
            }

	    def constant[A](a: A) : Stream[A] = 
	        Stream.cons(a, constant(a))

        def constantViaUnfold[A](a: A) : Stream[A] = 
            unfold(a)(_ => Some((a, a)))

	    def from(n: Int) : Stream[Int] = cons(n, from(n + 1))

        def fibs(a: Int,b:Int) : Stream[Int] = cons(a, fibs(a + b, a))

	    def empty[A] : Stream [A] = new Stream[A] { def uncons = None }
	
	    def cons[A](hd: => A , tl: => Stream[A]) : Stream[A] = 
	        new Stream[A] { lazy val uncons = Some((hd, tl)) }
	
	    def apply[A](as: A*) : Stream[A] = 
	        if(as.isEmpty) empty
	        else cons(as.head, apply(as.tail: _*))
	}

}
