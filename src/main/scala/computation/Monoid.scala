// What is a monoid?
// ================
// 
// Let's consider the algebra of string concatenation. We can add "foo" + "bar"
// to get "foobar" and the empty string is an identity element for that operation.
// That is, if we say "foo" + "" or "" + "foo" then we always get "foo".
// 
// Another thing is that when we combine 3 strings like this: r + s + t we understand
// a little better of the concatenation operator is also 'associative'
// i.e. (r + s ) + t == r + (s + t). There are many such operators that display this
// property of being associative and perform algebraic operations on them. They
// are generally termed "Monoids" 
// 
// In general, a Monoid consists of 
// - Some type A
// - A binary associative operation that takes two values of type A and combines into one
// - A value of type A that is an identity for that operation 
// 

trait Monoid[A] {
    def op(a: A, a2: A) : A 
    def id : A
}

object Monoid {
    val stringMonoid = new Monoid[String] {
        def op(a: String, b: String) : String = a + b
        def id : String = ""
    }

    val intAddition = new Monoid[Int] {
        def op(a: Int, b: Int) : Int = a + b 
        def id : Int = 0 // Sounds like it can be whatever integer is 
    }

    val intMultiplication = new Monoid[Int] {
        def op(a: Int, b: Int) : Int = a * b
        def id : Int = 1 // compare with 'intAddition' and understand why the identity function is
                         // evaluates differently. Hint: recall the definition of monoid
    }

    val booleanOr = new Monoid[Boolean] {
        def op(a: Boolean, b: Boolean) = a || b
        def id : Boolean = true 
    }

    val booleanAnd = new Monoid[Boolean] {
        def op(a: Boolean, b: Boolean) = a && b
        def id : Boolean = true 
    }
    def listMonoid[A] = new Monoid[List[A]] {
        def op(a: List[A], b : List[A]) : List[A] = a ++ b
        def id : List[A] = Nil 
    }

    def optionMonoid[A] : Monoid[Option[A]] = new Monoid[Option[A]] {
        def op(a: Option[A], b: Option[A]) : Option[A] = a orElse b 
        def id : Option[A] = None
    }
}

