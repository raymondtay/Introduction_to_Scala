object DDD {

	trait Format[A] {
	    def format(s: String) : A
	}
	
	def printf[A](format: Format[A]) : A = 
	    format.format("")
	
	class I[A](formatD: Format[A]) extends Format[Int => A] {
	    def format(s: String) = i => formatD.format(s + i.toString)
	}

    class C[A](formatD : Format[A]) extends Format[Char => A] {
        def format(s: String) = c => formatD.format(s + c.toString)
    }

    class E extends Format[String] {
        def format(s: String) = s
    }
    
    class S[A](l: String, formatD: Format[A]) extends Format[A] {
        def format(s: String) = formatD.format(s + l)
    }

    val fmt: Format[Int => Char => String] = 
        new S("Int: ", new I(new S(" Char: ", new C(new S(".", new E)))))

    val test = printf(fmt)(3)('c')

    println(s"test is ${test}")
}
