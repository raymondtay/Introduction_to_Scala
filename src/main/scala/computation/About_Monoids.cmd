Monoid Homomorphisms
=======================

If you have your law discovering cap on while reading this chapter, 
you may notice that there is a law that holds for some functions between
monoids. Take the string concatenation monoid and the integer addition 
monoid. If you take the length of two strings and add them up, it's the same 
as taking the length of the concatenation of those strings:

Here length is a function from Strig to Int that preserves the monoid
structure. Such a function is called a monoid homomorphism. A monoid homomorphism
f between monoids M and N obeys the following general law for all values x and y

M.op(f(x), f(y)) == f(N.op(x,y))

The same law holds for the homomorphism from String => WC in the example found in Monoid.scala
This property can be very useful when designing your own libraries. If two types that your 
library uses are monoids, and there exist functions between them, it is a good idea to think 
about whether those functions are expected to preserve the monoid structure and to check the monoid
homomorphism law with automated tests.

There is a higher order funciton that can take any function of type A => B where B is a 
monoid, and turn it into a monoid homomorphism from List[A] to B.

Sometimes, there will be a homomorphism in both directions between 2 monoids. Such a relationship
is called monoid isomorphism and we say that the two monoids are isomorphic.


