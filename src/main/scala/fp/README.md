
### Typeclasses 101

Learn You a Haskell for Great Good says

<pre>
A typeclass is a sort of interface that defines some behavior. If a type is a part of
a typeclass, that means that it supports and implements the behavior the typeclass
describes.
</pre>

Scalaz says:
<pre>
It provides purely functional data structures to complement those from the Scala
standard library. It defines a set of foundational type classes (e.g. Functor, Monad) from
corresponding instances for a large number of data structures.
</pre>

# For-comprehensions

Since lifting functions is so common in Scala, Scala provides a syntactic construct
called the _for-comprehension_ that it expands automatically to a series of `flatMap`
and `map` calls. Let's look at how `map2` could be implemented with for-comprehensions.

```scala
def map2[A,B,C](a: Option[A], b: Option[B])(f : (A,B) => C) : Option[C] =
  a flatMap (aa => 
    b map (bb =>
      f(aa, bb)
    )
  )
```
and here's the exact same code written as a for-comprehension:
```scala
def map2[A,B,C](a: Option[A], b: Option[B])(f : (A,B) => C) : Option[C] =
  for {
    aa <- a
    bb <- b
  } yield f(aa, bb)
```

A for-comprehension consists of a sequence of bindings, like `aa <- a` followed by a `yield`
after the closing brace, where the `yield` may make use of any of the values on the left side
of any previous `<-` binding. The compiler desugars the bindings to `flatMap` calls, with the final
binding and `yield` being converted to a call to `map`.

You should feel free to use for-comprehensions in place of explicit calls to `flatMap` and `map`.

```scala

def unfold[A,S](z: S)(f: S => Option[(A,S)]) : Stream[A]

```

The `unfold` function is an example of what's sometimes called a corecursive function.
Whereas a recursive function consumes data, a corecursive function produces data. And 
whereas recursive functions terminate by recursing on smaller inputs, corecursive functions 
need not terminate so long as they remain productive, which just means that we can always
evaluate more of the result in a finite amount of time. The `unfold` function is productive
as long as f terminates, since we just need to run the function f one more time to generate
the next element of the `Stream`. Corecursion is also sometimes called _guarded recursion_, 
and productivity is also sometimes called _cotermination_.

# Understand State 

```scala
trait RNG {
  def nextInt : (Int, RNG)
}
```
The above is our state object which encapsulates a _driver_ which drives
the creation of the next _state_. Let's quickly take a look at how 
we can prototype the next few functions:
```scala

type Rand[+A] = RNG => (A, RNG) // basically a function that takes in 1 RNG and returns a tuple

val int : Rand[Int] = _.nextInt

// 
// the "unit" function is akin to "pure", "return" in Applicatives and Monads
// respectively.
//
def unit[A](a : A) : Rand[A] = rng => (a, rng)

// 
// Traverses the structure with function application
//
def map[A,B](a: Rand[A])(f: A => B) : Rand[B] = 
  rng => {
    val (a, rng2) = a(rng)
    (f(a), rng2)
  }

type State[S,+A] = S => (A, S)
// State is short for computation that carries some state along, or
// state action, state transition or even statement.
// we can also write the following:
case class State[S,+A](run : S => (A, S)) 

type Rand[A] = State[RNG,A] // this is a generalization of the earlier

```
The representation does not matter too much. What is important is that we have a single,
general purpose type, and using this type we can write general purpose functions for 
capturing common patterns of stateful programs.

```scala
// Takes an unevaluated A and returning a
// computation that might evaluate it in a separate thread. 
// `Unit` in a sense creates a unit of parallelism that wraps 
// a single value.
def unit[A](a: => A) : Par[A]
```

Function arguments in Scala are strictly evaluated from left to right, so if 
`unit` delays execution untill `get` is called, we will both spawn
the parallel computations and wait for it to finish before spawning the second
parallel computation. This means the computation is effectively sequential :(

```scala
class ExecutorService {
  def submit[A](a: Callable[A]) : Future[A]
}
trait Callable[A] { def call : A }
trait Future[A] {
  def get : A
  def get(timeout: Long, unit: TimeUnit) : A
  def cancel(evenifRunning : Boolean) : Boolean
  def isDone : Boolean
  def isCancelled : Boolean
}

object Par 

  def unit[A](a: A) : Par[A] = (es: ExecutorService) => UnitFuture(a)

  private case class UnitFuture[A](get: A) extends Future[A] {
    def isDone = true
    def get(timeout : Long, units: TimeUnit) = get
    def isCancelled = false
    def cancel(evenIfRunning : Boolean) = false
  }

  def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) => C) : Par[C] = 
    (es: ExecutorService) => {
      val af = a(es)
      val bf = b(es)
      UnitFuture(f(af, bf))
    }

  def fork[A](a: => Par[A]) : Par[A] = 
    es => es.submit(new Callable[A] {
      def call = a(es).get
    }
}
```

# Why laws about code and proofs are important

It may seem unusual to state and prove properties about an API. This certainly isn't 
something typically done in ordinary programming. Why is important in FP ?

In functional programming it's easy and expected, to factor out common functionality
into generic, reusable components that can be composed. Side effects hurt compositionality
but more generally, any hidden or out-of-band assumption or behavior that
prevents us from treating our components (be they functions or anything else) as _black boxes_
makes composition difficult or impossible.

# Recognizing the expressiveness and limitations of an algebra

As you practice more functional programming, one of the skills you will develop is the 
ability to recognize what functions are expressions from an algebra, and what 
are the limitations of that algebra are. For instance, in the preceding example, it may not
have been obvious at first that a function like `choice` couldnt' be expressed purely 
in terms of `map`, `map2` and `unit` and it may not have been obvious that `choice` 
was just a special case of `flatMap`. Over time, observations like this will come quickly
and you will also get better at spotting how to modify your algebra to make 
some needed combinator expressions. These skills will be helpful for all of your API design work.

As a practical consideration, being able to reduce an API to a minimal set of primitive 
functions is extremely useful. As we noted earlier when we implemented `parMap` in 
terms of existing combinators, it is frequently the case that primitive combinators
encapsulate some rather tricky logic and resuing them means we don't have to duplicate this logic.


