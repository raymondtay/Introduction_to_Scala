In ScalaTest, you need to select your unit testing style and the author of the
framework has suggested you start withi `FlatSpec` and take note that
you must create a base class (not a trait) for your unit tests.

Second, you should mix into this class all the traits you think you 
might use a lot and the author has suggested that you use the name
`UnitSpec` for this class.

You should probably start your journey by reading `ExampleSpec.scala`
afterwhich, you are free to check out the rest

