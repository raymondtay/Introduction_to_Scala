

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

    case class ParserOps[A](p: Parser[A]) {
        def |[B>:A](p2: Parser[B]) : Parser[B] = self.or(p,p2)
        def or[B>:A](p2: => Parser[B]) : Parser[B] = self.or(p,p2)
    }
}
