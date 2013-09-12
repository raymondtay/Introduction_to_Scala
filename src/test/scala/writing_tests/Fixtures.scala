
/**
    ScalaTest recommends the following strategies
    Technique:
    get-fixture methods         Use when you need the same mutable fixtures in multiple objets and don't need to clean up thereafter
    fixture-context objects     Use when you need different combinations of mutable fixture objects in different tests, and don't need to clean up after
    OneInstancePerTest          Use when porting JUnit tests to ScalaTest, or if you prefer JUnit's approach to test isolation:
                                running each test in its own instance of the test class
    withFixture(noargtest)      Use when you need to perform side effects at the beginning and end of all or most tests, or want to stack traits
                                that perform such side-effects
    load-fixture methods        Use when differnt tests need different fictures that must be cleaned up afterwards
    withFixture(oneargtest)     Use when all or most tests need the same fixture that must be cleaned up afterwards
    BeforeAndAfter              Use when you need to perform the same side-effects before and/or after tests, rather than at the beginning or end of tests
    BeforeAndAfterEach          Use when you want to stack traits that perform the same side-effects before and/or after tests, rather than at the beginning or
                                end of tests
*/

import org.scalatest.{FlatSpec, OneInstancePerTest}
import collection.mutable.ListBuffer

// get-fixture
class NewFixtureSpec extends FlatSpec {
    // declaring a function to point to a structural type
    def fixture = new {
        val builder = "ScalaTest is"
        val buffer = new ListBuffer[String]
    }

    "Testing" should "be easy" in {
        val f = fixture
        val result = f.builder + " easy!"
        assert(result === "ScalaTest is easy!")
        f.buffer += "sweet!"
    }

    it should "be fun" in {
        val f = fixture
        val result = f.builder + " fun!"
        assert(result === "ScalaTest is fun!")
        assert(f.buffer.isEmpty)
    }
}

// fixture-context
class CompoundFixtureSpec extends FlatSpec {
    // declaring a function to point to a structural type
    trait Builder {
        val builder = "ScalaTest is"
    }

    trait Buffer {
        val buffer = ListBuffer("ScalaTest ", "is ")
    }

    "Testing" should "be easy and productive" in new Builder {
        val result = builder + " easy!"
        assert(result === "ScalaTest is easy!")
    }

    it should "be readable" in new Buffer {
        buffer += "readable!"
        assert(buffer === ListBuffer("ScalaTest ", "is ", "readable!"))
    }
}

// mixing in OneInstancePerTest
class OneInstancePerTestSpec extends FlatSpec with OneInstancePerTest {
    val builder = "ScalaTest is"
    val buffer = new ListBuffer[String]

    "Testing" should "be easy" in {
        val result = builder + " easy!"
        assert(result === "ScalaTest is easy!")
        buffer += "sweet!"
    }

    it should "be fun" in {
        val result = builder + " fun!"
        assert(result === "ScalaTest is fun!")
        assert(buffer.isEmpty)
    }
}
