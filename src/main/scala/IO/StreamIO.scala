
// Stream Transducer is a name given to the mechanism where 
// it transforms a stream of I values to O values.
// It so happens to be a Monad as well

trait Process[I,O] {
    def apply(s: Stream[I]) : Stream[O] = 
    this match {
        case Halt() ⇒ Stream()
        case Await(recv, fallback) ⇒ s match {
            case h #:: t ⇒ recv(h)(t)
            case _       ⇒ fallback(s)
        }
        case Emit(h, t) ⇒ h.toStream append t(s)
    }

    // What `map` does is to return `Halt` when there is 
    // no more input to be consumed, or any output to be emitted;
    // when there is output to be emitted, it would transform the 
    // elements via `f` and next state would also have a transformation
    // and this also implies that we can nest transformations 
    // Finally, when the input stream is consumed the same transformation is 
    // also applied and when there is no more input, the fallback would be consulted
    // with the same transformation, previously applied.
    def map[O2](f: O ⇒ O2) : Process[I, O2] = 
    this match {
        case Halt() ⇒ Halt()
        case Emit(h, t) ⇒ Emit(h map f, t map f)
        case Await(recv, fallback) ⇒ Await(recv andThen (_ map f) , fallback map f)
    }

    // Given two processes, x and y, `x ++ y` runs x to completion 
    // then runs y to completion on whatever input remains after the
    // first has halted.
    def ++(p: ⇒ Process[I,O]) : Process[I,O] = 
    this match {
        case Halt() ⇒ p
        case Emit(h, t) ⇒ emitAll(h, t ++ p)
        case Await(recv, fb) ⇒ Await(recv andThen (_ ++ p), fb ++ p)
    }
    // what `emit` does is to emit the current value i.e. `head` as a `Process`
    def emit[I,O](head: O, tail : Process[I,O] = Halt[I,O]()) : Process[I,O] = 
        emitAll(Stream(head), tail)

    // what `emitAll` does is to emit all of the values discovered 
    def emitAll[I, O](head: Seq[O], tail: Process[I,O] = Halt[I,O]()) = 
    tail match {
        case Emit(h2, t2) ⇒ Emit(head ++ h2, t2)
        case _ ⇒ Emit(head, tail)
    }

    def flatMap[O2](f: O ⇒ Process[I,O2]) : Process[I,O2] = 
    this match {
        case Halt() ⇒ Halt()
        case Emit(h, t) ⇒ if (h.isEmpty) t flatMap f else f(h.head) ++ emitAll(h.tail, t).flatMap(f)
        case Await(recv, fallback) ⇒ Await(recv andThen (_ flatMap f ), fallback flatMap f)
    }
    
    // A primitive in Monads which leverages on `emit` to build a `Process` value
    def unit[O](o : ⇒ O) : Process[I,O] = emit(o)

    // converts any function f: I => O to a Process[I,O] by 'lifting'
    def lift[I,O](f: I ⇒ O) : Process[I,O] = Await((in: I) ⇒ emit(f(in), lift(f)))

    // this is not tail-recursive
    def repeat : Process[I,O] = {
        def go(p : Process[I,O]) : Process[I,O] = p match {
            case Halt() ⇒ go(this)
            case Await(recv, fallback) ⇒ Await(recv andThen go, fallback)
            case Emit(h, t) ⇒ Emit(h, go(t))
        }
        go(this)
    }

    def sum : Process[Double,Double] = {
        def go(acc: Double) : Process[Double,Double] = 
            Await( (d: Double) ⇒ emit(d + acc, go(d + acc)) )
        go(0.0)
    }

    def take[I](n: Int) : Process[I,I] = 
    n match {
        case 0 ⇒ Halt[I,I]()
        case _ ⇒ 
            Await[I,I](i ⇒ emit(i))   
            this.take(n -1) 
    }
       
    def filter[I](f: I ⇒ Boolean) : Process[I,I] = 
        Await[I,I](i ⇒ if(f(i)) emit(i) else Halt()) repeat
}

object Process {

    def monad[I] : Monad[({ type f[x] = Process[I,x] })#f] = 
        new Monad[({ type f[x] = Process[I,x] })#f] {
            def unit[O](o : ⇒ O) = Emit(Seq(o))
            def flatMap[O,O2](p: Process[I,O])(f: O ⇒ Process[I,O2]) : Process[I,O2] = p flatMap f
        }

    import scala.util.parsing.json._

    def monadJson[I <: JSONObject] : Monad[({ type f[x] = Process[I,x] })#f] = 
        new Monad[({ type f[x] = Process[I,x] })#f] {
            def unit[O](o : ⇒ O) = Emit(Seq(o))
            def flatMap[O,O2](p: Process[I,O])(f: O ⇒ Process[I,O2]) : Process[I,O2] = p flatMap f
        }
}

// Emit(head, tail) indicates to the driver that the `head` values should be emitted to the
// output stream, and that `tail` should be the next state following that.
case class Emit[I,O](head : Seq[O], tail: Process[I,O] = Halt[I,O]()) extends Process[I,O]

// Await(recv, fallback) requests a value from the input stream, indicate that `recv` 
// should be used by the driver to produce the next state, and that `fallback` should be
// consulted if the input has no more elements available
case class Await[I,O](recv: I ⇒ Process[I,O], fallback: Process[I,O] = Halt[I,O]()) extends Process[I,O]

// Halt indicates to the driver that there is no more elements to be read or emitted to.
case class Halt[I,O]() extends Process[I,O]

