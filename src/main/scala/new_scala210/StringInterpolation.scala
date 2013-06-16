package new_scala210

// There are two primary ways to interpolate scala strings
// in 2.10 and they are the 's', 'f' and 'raw' interpolators

object StringInterpolator {
    // prepending s to any string literal allows the usage of 
    // variables directly in the string. 
    val name = "james"
    println(s"Hello $name")

    // String interpolators can take arbitrary expressions
    // and you embed that into the ${} 
    println(s"1 + 1 = ${1 + 1}")
    var myvar = 0
    println(s"1 + 1 = ${trait A {def apply(s: Int) : Int = {println(s"You gave me a $s");s}}; val obj = new Object with A {}; obj(myvar) }")
    val now1 = s"1 + 1 = ${trait A {def apply(s: Int) : Int = {println(s"You gave me a $s");s}}; val obj = new Object with A {}; obj(myvar) }"
    myvar = 3
    val now2 = s"1 + 1 = ${trait A {def apply(s: Int) : Int = {println(s"You gave me a $s");s}}; val obj = new Object with A {}; obj(myvar) }"
    // the expression '1 + 1 = ' is to confuse u ;) 


    // Prepending f to any string literal allows the creation of simple formatted strings, similar to 
    // printf in other languages. When using the f interpolator, all variable references should be 
    // followed by a printf-style format string

    val height = 1.81d
    val namef = "James"
    println(f"$namef%s is $height%2.2f meters tall")

    // The 'raw' interpolator is similar to the s interpolator except that it performs no escaping of literals
    // within the stirng. 
    val keepNewLines = raw"This is a header.\nThis is a body.\n"
}

object AdvancedStringInterpolator {
    // In Scala, all processed string literals of the form 
    // id"string content"
    // are transformed into a method call (id) on an instance of StringContext.
    // This method can also be available on implicit scope. To define our own string 
    // interpolation, we simply need to create an implicit class that adds a new
    // method to StringContext.
    import util.parsing.json._
    implicit class JsonHelper(val sc: StringContext) extends AnyVal {
        def json(args: Any*) : JSONObject = sys.error("TODO")
    }

    val name = "Raymond"
    val id   = "Wouldn't u like to know"

    def giveMeSomeJson(x: JSONObject) : String = x.toString()

    giveMeSomeJson(json"{name: $name, id: $id}") 
}


