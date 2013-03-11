
object Chapter4 {
   
    // The general rule of thumb is that we use exceptions only if no
    // reasonable program would ever catch the exception - if for some
    // callers the exception might be a recoverable error, we use Option to
    // give them flexibility 
    sealed trait Option[+A] {
        def map[B](f: A => B) : Option[B] = this match {
            case Some(v) => Some(f(v))
            case None    => None
        }
        def flatMap[B](f: A => Option[B]) : Option[B] = this match {
            case Some(v) => f(v)
            case None    => None
        }
        def flatMapViaMap[B](f: A => Option[B]) : Option[B] = map(f) getOrElse None

        def getOrElse[B >: A](default: => B) : B = this match {
            case Some(v) => v
            case None    => default
        }
        def orElse[B >: A](ob: => Option[B]) : Option[B] = this match {
            case None   => ob
            case _ => this 
        }
        def filter(f: A => Boolean) : Option[A] = this match {
            case Some(v) if f(v) => this
            case None   => None
        } 

        // error: missing parameter type for expanded function ((x$1) => x$1.map(f))
        // def lift[A,B](f: A => B) = _ map f
        // the reason is because scalac knows that x$1.map(f) : Option[B] since f: A => B 
        // which in turn means that x$1 : Option[A] (this is by examining the type signatures)
        // hence when we tell it what the return type is expected to be, then it becomes clear
        def lift[A,B](f: A => B) : Option[A] => Option[B] = _ map f

        def map2[A,B,C](a: Option[A], b: Option[B])(f: (A,B) => C ) : Option[C] = 
            for {
                a1 <- a
                b1 <- b
            } yield f(a1,b1) 

	    def traverse[A,B](a: List[A])(f: A => Option[B]) : Option[List[B]] = a match {
	        case Nil => Some(Nil)
	        case h :: t => map2(f(h), traverse(t)(f))(_ :: _)  
	    } 
    }
    // Variance is defined as the sum of the squared difference between each data point 
    // and the mean of those data points.
    def variance(xs: Seq[Double]) : Option[Double] = {
        val mean = (xs reduce (_ + _))  / xs.size 
        Some(xs.foldLeft(List[Double]())( (l,e) => math.pow(e - mean, 2) :: l ) reduce ( _ + _ ) ) map ( _ / 2 )
    }
    case class Some[+A](get: A) extends Option[A]
    case object None extends Option[Nothing]

    def sequence[A](a: List[Option[A]]) : Option[List[A]] = a match {
        case Nil => Some(Nil)
        case h :: t => h flatMap (h2 => sequence(t) map (h2 :: _) )
    }
   
    object examples {
        import java.util.regex._
        def pattern(s: String) : Option[Pattern] = 
            try {
                Some(Pattern.compile(s))
            } catch {
                case e : PatternSyntaxException => None
            }
        def mkMatcher(pat: String) : Option[String => Boolean] = 
            pattern(pat) map (p => (s: String) => p.matcher(s).matches)
        def mkMatcher_1(pat: String) : Option[String => Boolean] = 
            for {
                p <- pattern(pat)
            } yield( (s: String) => p.matcher(s).matches )
        def doesMatch(pat: String, s: String) : Option[Boolean] = 
            for {
                p <- mkMatcher_1(pat)
            } yield p(s)

        def bothMatch(pat: String, pat2: String, s: String) : Option[Boolean] = 
            for {
                f <- mkMatcher(pat)
                g <- mkMatcher(pat2)
            } yield f(s) && g(s)
        def bothMatch_1(pat: String, pat2: String, s: String) : Option[Boolean] = 
            mkMatcher(pat) flatMap(f => mkMatcher(pat2) map (g => f(s) && g(s)))

        def map2[A,B,C](a: Option[A], b: Option[B])(f: (A,B) => C ) : Option[C] = 
            for {
                a1 <- a
                b1 <- b
            } yield f(a1,b1) 

        def bothMatch_2(pat: String, pat2: String, s: String) : Option[Boolean] = 
            map2( mkMatcher(pat), mkMatcher(pat2) )( (p1,p2) => p1(s) && p2(s) ) 
    
        // Traverses the list 'a' twice. Not very efficient for long lists.
        def parsePatterns(a: List[String]) : Option[List[Pattern]] = 
            sequence(a map pattern)
    }
}

