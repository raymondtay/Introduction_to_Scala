
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


    def map[O2](f: O ⇒ O2) : Process[I, O2] = 
    this match {
        case Halt() ⇒ Halt()
        case Emit(h, t) ⇒ Emit(h map f, t map f)
        case Await(recv, fallback) ⇒ Await(recv andThen (_ map f) , fallback map f)
    }

    // A combinator
    def ++(p: ⇒ Process[I,O]) : Process[I,O] = 
    this match {
        case Halt() ⇒ p
        case Emit(h, t) ⇒ emitAll(h, t ++ p)
        case Await(recv, fb) ⇒ Await(recv andThen (_ ++ p), fb ++ p)
    }
   
    def emit[I,O](head: O, tail : Process[I,O] = Halt[I,O]()) : Process[I,O] = 
        emitAll(Stream(head), tail)
 
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
    
    // A primitive in Monads
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

    def filter[I](f: I ⇒ Boolean) : Process[I,I] = 
        Await[I,I](i ⇒ if(f(i)) emit(i) else Halt()) repeat
}

object Process {
    def monad[I] : Monad[({ type f[x] = Process[I,x] })#f] = 
        new Monad[({ type f[x] = Process[I,x] })#f] {
            def unit[O](o : ⇒ O) = Emit(Seq(o))
            def flatMap[O,O2](p: Process[I,O])(f: O ⇒ Process[I,O2]) : Process[I,O2] = p flatMap f
        }
}

case class Emit[I,O](head : Seq[O], tail: Process[I,O] = Halt[I,O]()) extends Process[I,O]

case class Await[I,O](recv: I ⇒ Process[I,O], fallback: Process[I,O] = Halt[I,O]()) extends Process[I,O]

case class Halt[I,O]() extends Process[I,O]

