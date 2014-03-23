

/**
    An iteratee consumes a stream of elements of type E, producing a result of type A. The stream itself is represented by the input trait.
    An iteratee is an immutable data type, so each step in consuming the stream generates a new Iteratee with a new state.
    At a high level, an iteratee is just a function that takes a piece of input and returns either a final result or a new function 
    that takes another piece of input. To represent this, an iteratee can be in
    one of three states (see play.api.libs.iteratee.Step): Done → which means a result an potentially some unconsumed part of the stream
    Cont →which means a function to be invoked to generate a new Iteratee from the next piece of input, Error → which means it contains 
    an error message and potentially some unconsumed part of the stream.

    One would expect to transform an Iteratee through the Cont state N times, eventually arriving at either the Done or Error state. 
    Typically, an Enumerator would be used to push data into an iteratee by invoking the function in Cont state untill
    either 1) the iteratee leaves the Cont state or 2) the enumerator runs out of data.
    The Iteratee does not do any resource management (such as closing streams): the producer pushing stuff into the ITeratee has that 
    responsibility. 
    Lastly, the state of an iteratee may not be available synchronously, it may be pending an async computation. This is the difference between iteratee and Step.

import play.api.libs.iteratee._
object AboutIterateeEnumerator {

	def total2Chunks : Iteratee[Int, Int] = {
	    def step(idx: Int, total: Int)(i :Input[Int]) : Iteratee[Int, Int] = i match {
	        case Input.EOF | Input.Empty ⇒ Done(total, Input.EOF)
	        case Input.El(e) ⇒ if (idx < 2) Cont[Int, Int](i ⇒ step(idx + 1, total + e)(i)) else Done(total, Input.EOF)
	    }
	
	    Cont[Int, Int](i ⇒ step(0, 0)(i))
	}

}

*/
