import scala.language.higherKinds

import property_testing._

// The syntax that refers to Parser[_] is a type constructor
// similar to the concept of haskell's type and data constructors
// In Haskell's ADT, for example the one below
// data Tree a = Tip | Node a (Tree a) (Tree a)
// 'Tree' is what is known as the type constructor whilst 'Tip' and 'Node' 
// are known as data constructors
trait Parsers[ParseError, Parser[+_]] { self =>

    // Returns a parser object that recognizes a single character
    def char(c: Char) : Parser[Char]

    // similarly for the 'char' function, we want something to recognize strings
    implicit def string(s: String) : Parser[String]

    implicit def operators[A](p : Parser[A]) = ParserOps[A](p)

    implicit def asStringParser[A](a: A)(implicit f: A => Parser[String]) : ParserOps[String] = ParserOps(f(a))

    // for recognizing either a exact match or
    // def orString(s1: String, s2: String) : Parser[String]
    def or[A](s1: Parser[A], s2: Parser[A]) : Parser[A]

    // here's a rough idea of what it means to 'run' a parser
    // so we return a ParserError if we find errors or the type of the input i.e. A
    def run[A](p : Parser[A])(input: String) : Either[ParseError,A]

    def listOfN[A](n: Int, p: Parser[A]) : List[Parser[A]] 

    // We can add other binary operations to 'Parsers'
    // and have 'ParserOps' delegate 
    case class ParserOps[A](p: Parser[A]) {
        def |[B>:A](p2: Parser[B]) : Parser[B] = self.or(p,p2)
        def or[B>:A](p2: => Parser[B]) : Parser[B] = self.or(p,p2)
        def many[A] = self.many(p)
        def map[B](f: A => B ) = self.map(p)(f)
    }

    // Let's consider the parser that recognizes 0 or more repetitions of 
    // the character 'a' and returns the number of characters it has seen.
    // combining 'map' we can write something like map(many(char('aaaa')))(_.length)
    def many[A](p: Parser[A]) : Parser[List[A]]

    def map[A,B](a: Parser[A])(f: A => B) : Parser[B]

    // the first law has emerged!
    // map(p)(id) == p
    // i.e. a parser when given an identity fn, will always produce the same parser
   
    object Laws { 
        def equal[A](p1: Parser[A], p2: Parser[A])(in: Gen[String]) : Prop = forAll(in)(s => run(p1)(s) == run(p2)(s))

        def mapLaw[A](p : Parser[A])(in: Gen[String]): Prop = equal(p, p.map(a => a))(in)
    }
}
