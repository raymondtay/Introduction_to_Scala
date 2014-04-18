object ZipZipZip {

	case class Zero()
	case class Succ[N](x: N)
	trait ZipWith[N,S] {
	    type ZipWithType
	    def manyApp:N => Stream[S] => ZipWithType
	    def zipWith : N => S => ZipWithType = 
	        n => f => manyApp(n)(repeat(f))
	
	    def repeat[A](x: A): Stream[A] = cons(x, repeat(x))
	
	    def cons[A](x: A, sa: Stream[A]) = x +: sa
	}
	
	def zWith[N,S](n: N, s: S) (implicit zw:ZipWith[N,S]) : zw.ZipWithType = 
	    zw.zipWith(n)(s)
	
	implicit def ZeroZW[S] = new ZipWith[Zero,S] {
	    type ZipWithType = Stream[S]
	    def manyApp = n => xs => xs
	}
/*
    implicit def SuccZW[N,S,R](implicit zw: ZipWith[N,R]) = 
        new ZipWith[Succ[N], S => R] {
            type ZipWithType = Stream[S] => zw.ZipWithType
            def manyApp = n => xs => xs => n match {
                case Succ(i) => zw.manyApp(i)(zapp(xs,ss))
            }
        }

    def zapp[A,B](xs: Stream[A => B], ys: Stream[A]) = 
        (xs,ys) match {
            case (cons(f,fs), cons(s,ss)) => cons(f(s), zapp(fs,ss))
            case (_,_) => Stream.empty
        }
*/
}
 
