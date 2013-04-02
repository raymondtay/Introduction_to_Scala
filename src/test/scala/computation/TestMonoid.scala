
object TestMonoids extends App {
    val words = List("a", "b", "hello", "there")
    import Monoid._
    words.foldRight(stringMonoid.id)(stringMonoid.op)
    words.foldLeft(stringMonoid.id)(stringMonoid.op)
}
