
object Test {
  @Benchmark
  def testMethod[String]: Double = {
    val x = 2.0 + 2.0
    Math.pow(x, x)
  }

  @Benchmark
  def methodWithArguments(a: Double, b: Double) = {
    val c = Math.pow(a, b)
    c > a + b
  }

}

object BenchmarkMain extends App {
  Test.testMethod
  Test.methodWithArguments(1.3f, 5.5f)
}
