
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


