package property_testing

trait Prop {
    def &&(p: Prop) : Prop 
}

trait Gen[+A] {
    def listOf[A](a: Gen[A]) : Gen[List[A]]
    
    def listOfN[A](n: Int, a: Gen[A]) : Gen[List[A]]
    def forAll[A](a: Gen[A])(f: A => Boolean) : Prop
}


