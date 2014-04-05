/**
    A little language for talking about mutable references.
    This takes the form of a combinator library with some 
    primitive combinators. The language for talking about mutable
    memory cells should have these primitive commands:
    + Allocate a new mutable cell 
    + Write to a mutable cell 
    + Read from a mutable cell 
*/
sealed trait ST[S,A] { self ⇒

    protected def run(s: S) : (A, S)

    def map[B](f: A ⇒ B) : ST[S,B] = new ST[S,B] {
        def run(s: S) = {
            val (a, s1) = self.run(s)
            (f(a), s1) 
        }
    }

    def flatMap[B](f: A ⇒ ST[S,B]): ST[S, B] = new ST[S,B] {
        def run(s: S) = {
            val (a, s1) = self.run(s)
            f(a).run(s1)
        }
    }

}

object ST {
    def apply[S,A](a: ⇒ A) = {
        lazy val memo = a
        new ST[S,A] {
            def run(s: S) = (memo, s)
        }
    }
    def runST[A](st: RunnableST[A]) : A = 
        st.apply[Null].run(null)._1
}

sealed trait STRef[S,A] {

    protected var cell : A
    def read: ST[S,A] = ST(cell)
    def write(a: A) : ST[S, Unit] = new ST[S, Unit] {
        def run(s: S) = {
            cell = a
            ((), s)
        }
    }
}

object STRef {
    def apply[S,A](a: A) : ST[S, STRef[S,A]] = ST(new STRef[S,A] {
        var cell = a
    })
}

trait RunnableST[A] {
    def apply[S]: ST[S,A]
}

sealed abstract class STArray[S,A](implicit manifest: Manifest[A]) {
    protected def value : Array[A]
    def size : ST[S, Int] = ST(value.size)

    def write(i: Int, a: A) : ST[S, Unit] = new ST[S, Unit] {
        def run(s: S) = {
            value(i) = a
            ((), s)
        }
    }

    //def fill(xs: Map[Int, A]): ST[S, Unit] 

    def read(i: Int) : ST[S, A] = ST(value(i))

    def freeze: ST[S, List[A]] = ST(value.toList)
}

object STArray {
    def apply[S,A : Manifest](sz: Int, v: A ) : ST[S, STArray[S,A]] = 
        ST(new STArray[S,A] {
            lazy val value = Array.fill(sz)(v)
        })

    def fromList[S, A : Manifest](xs: List[A]) : ST[S, STArray[S, A]] = 
        ST(new STArray[S,A] {
            lazy val value = xs.toArray
        })
}

/**
Sample run
scala> :paste
// Entering paste mode (ctrl-D to finish)

for {
r1 <- STRef[Nothing,Int](1)
r2 <- STRef[Nothing,Int](1)
x <- r1.read
y <- r2.read
_ <- r1.write(y + 1)
_ <- r2.write(x + 1)
a <- r1.read
b <- r2.read
} yield(a,b)

// Exiting paste mode, now interpreting.

res2: ST[Nothing,(Int, Int)] = ST$$anon$2@2b2a9299
val p = new RunnableST[(Int,Int)] {
def apply[S] = 
for {
r1 <- STRef(1)
r2 <- STRef(1)
x <- r1.read
y <- r2.read
_ <- r1.write(y + 1)
_ <- r2.write(x + 1)
a <- r1.read
b <- r2.read
} yield(a,b)
}
*/
