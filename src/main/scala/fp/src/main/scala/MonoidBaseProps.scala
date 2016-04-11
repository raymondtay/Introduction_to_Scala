package monoidsonly

import monoidsonly._

import org.scalacheck.Gen // for arbitrary data generation
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll

object PropTests {

  implicit val anyMonoid : Arbitrary[Monoid[Int]] = 
    Arbitrary{
      for {
        n <- Gen.choose(0,10)
        y <- Gen.choose(0,10)
      } yield monoidsonly.Monoid.IntMonoid
    }
  val defaultMonoidProps = forAll{ m: Monoid[Int] => m.mzero == 0 }

}
