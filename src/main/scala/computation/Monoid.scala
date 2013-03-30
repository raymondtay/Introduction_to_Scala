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

