package computation
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

// There is a slight terminology mismatch between programmers and 
// mathematicians, when they talk about a type being a monoid as 
// against having a monoid instances. As a programmer , it is natural
// to think of the instance of type Monoid[A] as being a monoid. But that is not
// accurate terminology. The monoid is actually two things - the type and the instance
// When we say that a method accepts a value of type Monoid[A], we don't say that 
// it takes a monoid but that it takes evidence that the type A is monoid.
// 
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

    /* 
     A function having the same argument and return type is called a 
     "endofunction".
     */
    def EndoMonoid[A] : Monoid[A => A] = new Monoid[A => A] {
        def op(a: A => A, b: A => A) : A => A = a compose b
        def id : A => A = zero
        def zero[A](v: A) : A = v
    }

    // Inserts spaces between words unless there is already one, and trims
    // spaces off the ends of the result.
    def wordsMonoid(s: String) : Monoid[String] = new Monoid[String] {
        def op(s: String, s2: String) : String = s.trim() + " " + s2.trim()
        def id : String = ""
    }

    // Folds a list with a monoid
    def concatenate[A](as: List[A], m : Monoid[A]) : A = as.foldLeft(m.id)(m.op) 

    /*
     Don't quite like this implmentation as it's traversing the list twice
     Q: Can we do better? Sure! look at foldMap2
    */
    def foldMap[A,B](as: List[A], m: Monoid[B])(f: A => B) : B = as.map(f).foldLeft(m.id)(m.op) 
    def foldMap2[A,B](as: List[A], m: Monoid[B])(f: A => B) : B = as.foldLeft(m.id)((b,a) => m.op(f(a), b)) 

    def foldLeft[A,B](as: List[A])(z: B)(f: (B,A) => B) : B = foldMap2(as, EndoMonoid[B])(a=>b=>f(b,a))(z)
}

