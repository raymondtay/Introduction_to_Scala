package state

trait RNG {
    def nextInt : (Int, RNG)
}

case class SimpleRNG(seed: Long) extends RNG {
    def nextInt: (Int, RNG) = {
        val newseed = (seed * 0x5deece66dl + 0xbl) & 0xffffffffffffL
        val next = SimpleRNG(newseed)
        val n = (newseed >>> 16).toInt
        (n, next)
    }
}

object XX {

    type Rand[+A] = RNG => (A, RNG)

    val int : Rand[Int] = _.nextInt
    val double : Rand[Double] = map(_.nextInt)(_.toDouble)

    def unit[A](a: A) : Rand[A] = rng => (a, rng)

    def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A,B) => C) : Rand[C] = 
        rng => {
            val (a1, rng2) = ra(rng)
            val (a2, rng3) = rb(rng2)
            (f(a1,a2), rng3)
        }

    def map2ViaflatMap[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A,B) => C) : Rand[C] = 
        flatMap(ra)(a => 
            flatMap(rb)(b =>
                rng => (f(a,b), rng)))
    def mapViaflatMap[A,B](a: Rand[A])(f: A => B) : Rand[B] = 
       flatMap(a)(a => rng => (f(a), rng))

    def map[A,B](a: Rand[A])(f : A => B) : Rand[B] = 
        rng => {
            val (i2, rng2) = a(rng)
            (f(i2), rng2)
        }
    def nonNegativeInt(rng: RNG) : (Int, RNG) = {
        import scala.util.Random._
        import math._
        val (i, rng2) = rng.nextInt
        (abs(i), rng2)
    }

    def both[A,B](ra: Rand[A], rb: Rand[B]) : Rand[(A,B)] = map2(ra, rb)((_,_))
    val randIntDouble : Rand[(Int, Double)] = both(int, double)
    val randDoubleInt : Rand[(Double, Int)] = both(double, int)

    // i don't have a `formal` education on functional programming
    // but this function called `sequence` can be interpreted, to me at least,
    // to mean that the RNG is propagated in each application of `map`
    // the alternate implementation of sequence is presented in `sequence_another_meaning` 
    def sequence[A](fs: List[Rand[A]]) : Rand[List[A]] = 
        rng => (fs.map(ra => ra(rng)._1), rng)

    def sequence_another_meaning[A](fs: List[Rand[A]]) : Rand[List[A]] = {
        @annotation.tailrec
        def map(rng: RNG)(xs: List[Rand[A]])(acc: List[A]) : (List[A],RNG)  = 
            xs match {
                case h :: t => 
                    val (ra1, rng1) = h(rng)
                    map(rng1)(t)(acc :+ ra1)
                case Nil => (acc, rng)
            }
        rng => map(rng)(fs)(List[A]())
    }

    def flatMap[A,B](f: Rand[A])(g: A => Rand[B]) : Rand[B] = 
        rng => {
            val (ra, rng2) = f(rng)
            g(ra)(rng2)
        }

    def nonNegativeEven: Rand[Int] = map(nonNegativeInt)(i => i - i % 2)
}


