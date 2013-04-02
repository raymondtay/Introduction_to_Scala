package computation

import org.scalatest.FunSuite

class TestMonoids extends FunSuite {

    test("List of strings should concatenate properly with String Monoids") {
        val words = List("a", "b", "hello", "there")
        import Monoid._
        val r1 = words.foldRight(stringMonoid.id)(stringMonoid.op)
        val r2 = words.foldLeft(stringMonoid.id)(stringMonoid.op)
        assert(r1 === words.mkString)
        assert(r2 === words.mkString)
    }
}
