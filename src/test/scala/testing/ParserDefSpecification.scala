package parser_combinators
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object ParserDefSpecification extends Properties("ParserDef") {
    import ParserDef._

    property("lists") = forAll { (l1: List[Int], l2: List[Int]) => l1.size + l2.size == (l1 ::: l2).size }
}
 
