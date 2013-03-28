// The key to recovering referential transparency is to make these state updates explicit.
// That is, do not update the state as a side effect, but simply return the new state along
// with the value we are generating.
object StateObj {

    // All the functions we have written in the object RNG follows a particular
    // pattern and they are actually RNG => (A, RNG) where the input to the functions
    // is state represented by RNG and returns a new tuple which contains the new 
    // state RNG together with a value. 
    type Rand[+A] = RNG => (A, RNG)

    // The following 2 expressions are equivalent to 
    // "type Rand[+A] = RNG => (A, RNG)"
    // type Rand[A] = State[RNG,A]
    // type State[s,+a] = s => (a,s)

    val int: Rand[Int] = _.nextInt
    def unit[A](a: A ) : Rand[A] = rng => (a, rng) 

    // Instead of mutating a state in-place, the better approach is to 
    // leave it unmodified and instead return a new state as shown below.
    trait RNG {
        def nextInt : ( Int, RNG) 
  
        def flatMap[A,B](f: Rand[A])(g: A => Rand[B]) : Rand[B] = 
            rng => {
                    val (i, r) = f(rng)
                    g(i)(r)
            }

        def map2ViaflatMap[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A,B) => C) : Rand[C] = flatMap(ra)(a => map(rb)(b => f(a,b)))

        def mapViaflatMap[A,B](s: Rand[A])(f: A => B) : Rand[B] = flatMap(s)(a => unit(f(a)))

        def map[A,B](s: Rand[A])(f: A => B) : Rand[B] = 
            rng => { val (a, rng2) = s(rng)
                  (f(a), rng2) }

        def positiveMax(n: Int) : Rand[Int] = 
            rng => RNG.positiveInt(rng) 

        def doubleViaMap(r: RNG) : Rand[Double] = map(RNG.positiveInt)(i => i/Int.MaxValue.toDouble + 1)

        // 'map' is not powerful enough to implement intDouble and doubleInt from before. 
        // what we need is a new combinator map2, that can combine two RNG actions into one 
        // using a binary rather than unary function.
        def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A,B) => C) : Rand[C] = 
            rng  => {
                       val (i1, r2) = ra(rng)
                       val (i2, r3) = rb(rng)
                       (f(i1,i2), r3) }
        def sequence[A](fs: List[Rand[A]]) : Rand[List[A]] = 
            (rng:RNG) => (fs.map(u => u(rng)._1), rng)
    }

    // There is an efficiency loss that comes with computing next states using pure functions
    // because it means we cannot actually mutate the data in place. This loss of efficiency can be 
    // mitigated by using efficient purely functional data structures.
    object RNG {
        def randomPair(rng: RNG) : ((Int,Int), RNG) = {
            val (i1, rng2) = rng.nextInt
            val (i2, rng3) = rng2.nextInt
            ((i1, i2), rng3)
        }

        def positiveInt(rng: RNG) : (Int, RNG) = {
            val (i,n) = rng.nextInt
            i match {
                case Int.MinValue => ((i + 1).abs,n)
                case _ => (i.abs,n)
            }
        }

        def double(rng: RNG) : (Double, RNG) = {
            val (_, r2) = rng.nextInt
            (math.random, r2)
        }

        def intDouble(rng: RNG) : ((Int,Double), RNG) = {
            val (i1, r1) = rng.nextInt
            val (d, r2) = double(r1)
            ((i1 -> d) -> r2)
        }

        def doubleInt(rng: RNG) : ((Double, Int), RNG) = {
            val ((i,d), r) = intDouble(rng)
            ((d,i), r)
        }

        def double3(rng: RNG) : ((Double, Double, Double), RNG) = {
            val (d1, r1) = double(rng)  
            val (d2, r2) = double(r1)
            val (d3, r3) = double(r2) 
            ((d1,d2,d3),r3)
        }

        def ints(count: Int)(rng: RNG) : (List[Int], RNG) = {
            var _r : RNG = rng 
            def go(cnt: Int)(r: RNG) : List[Int] =
                cnt match {
                    case 0 => { _r = r; Nil}
                    case _ => val (i, r2) = r.nextInt
                              i :: go(cnt - 1)(r2)
                }
            (go(count)(rng), _r)
        }

        def simple(seed: Long) : RNG = new RNG {
            def nextInt = {
                val seed2 = (seed*0x5deece66dl + 0xbl) & ((1L << 48) -1)
                // the new state is encapsulated in the second element of the 2-tuple.
                ((seed2 >>> 16).asInstanceOf[Int], simple(seed2))
            }
        }
    }

    object State {
        def sequence[A,S](fs: List[State[S,A]]) : State[S,List[A]] =  {
            def compute(s: S, whatsleft: List[State[S,A]], list: List[A]) : (List[A], S) = {
                whatsleft match {
                    case Nil => (Nil,s)
                    case h :: t => h.run(s) match { case (a1, sa1) => compute(sa1, t, a1 :: list) }
                }
            }
            State( s => compute(s, fs, List()) )
        }

        def unit[S,A](a: A) : State[S,A] = State(rng => (a, rng))
        def get[S] : State[S,S] = State(s => (s,s))
        def set[S](s: S) : State[S, Unit] = State(s => ((), s))
        def modify[S](f: S => S) : State[S, Unit] = for {
            s <- get
            _ <- set(f(s))
        } yield ()
    }

    import State._

    // The function 'run' is a state-generator
    case class State[S, +A](run: S => (A, S)) {
        def map[B](f: A => B) : State[S,B] = 
            State{s => { val (a, s2) = run(s) 
                    (f(a), s2) }}
        def flatMap[B](f: A => State[S, B]) : State[S,B] = 
            State{s => val (a, s2) = run(s)
                 f(a).run(s2)}
        def map2[C,B](r: State[S,B])(f: (A, B) => C) : State[S,C] = 
            flatMap(a => r.map(b => f(a,b)))
            // the code below is a long-winded way of saying the first
            // thing above. cool isn't it !?
           /*State{s => val (a, s2) = run(s)
                val (b, s3) = r.run(s)
                (f(a,b), s3)}*/
    }

    sealed trait Input
    case object Coin extends Input
    case object Turn extends Input
    case class Machine(locked: Boolean, candies: Int, coins:Int) 
    def simulateMachine(inputs: List[Input]) : State[Machine,Int] =
}

