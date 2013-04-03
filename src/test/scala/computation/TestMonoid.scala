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

    test("Another test for List of string should concatenate the same with string monoids") {
        val words = List("a", "b", "hello", "there")
        import Monoid._
        assert(words.foldLeft(stringMonoid.id)(stringMonoid.op) == (("" + "a") + "b") + "hellothere")
    }

    test("Test of wordsMonoid") {
        import Monoid._
        val empty = wordsMonoid("")
        import empty._
        assert(op("Hic", op("est ", "chorda ")) === "Hic est chorda") 
    }
}