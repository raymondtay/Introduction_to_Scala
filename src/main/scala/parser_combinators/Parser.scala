package parser_combinators

object ParserDef {

    trait Parsers[ParseError, Parser[+_]] { self ⇒ 
        // Even implicit functions cannot be referenced before they are defined?????
        implicit def string(s: String) : Parser[String]
        implicit def operators[A](p: Parser[A]) = ParserOps[A](p)
        implicit def asStringParser[A](a: A)(implicit f: A ⇒ Parser[String]) : ParserOps[String] = ParserOps(f(a))




        // The combination of `many` and `map` lets us express the parsing task
        // of counting the occurrences of a particular character and let's develop
        // a more advanced version that allows us to see what portion of the input 
        // string is the parser examining
        def slice[A](p: Parser[A]) : Parser[String] 

        // builds a parser for type `Char`
        def char(c: Char) : Parser[Char] = map(string(c.toString))(_.charAt(0))

        // Always succeeds with a value given by that of `a`
        def succeed[A](a: A) : Parser[A] = map(string(""))( _ ⇒ a )

        def run[A](p: Parser[A])(input: String): Either[ParseError,A]
        def or[A](s1: Parser[A], s2: Parser[A]) : Parser[A]
        def listOfN[A](n: Int, p: Parser[A]) : Parser[List[A]]
        def many[A](p: Parser[A]) : Parser[List[A]]
        def map[A,B](p: Parser[A])(f: A ⇒ B) : Parser[B]
        def map2[A,B,C](p: Parser[A], q: Parser[B])(f: (A,B) ⇒ C) : Parser[C]
        def product[A,B](p: Parser[A], p2: Parser[B]) : Parser[(A,B)]

        val numA: Parser[Int] = map(many(char('a')))(_.size)

        case class ParserOps[A](p : Parser[A]) { 
            def |[B >: A](p2: Parser[B]) : Parser[B]    = self.or(p, p2)
            def or[B >: A](p2: ⇒ Parser[B]) : Parser[B] = self.or(p, p2)
            def **[B](p2: Parser[B]) = self.product(p, p2)
        }
    }
}
