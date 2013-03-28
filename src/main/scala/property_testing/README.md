To build your own property based testing library, first we need to understand the 
concept of property testing.

Below is a code snippet of what it means to have a property

val intList = Gen.listOf(Gen.choose(0, 100)) // generator of a list of ints
val prop = 
    forAll(intList)(l => l.reverse.reverse == l) &&
    forAll(intList)(l => l.headOption == l.reverse.lastOption)  // test the property that reversing twice is the same,
                                                                // test the property that the first element is the same as 
                                                                // reversing the list and picking the last element
val failingProp = forAll(intList)(l => l.reverse == l) // test a property which is false

