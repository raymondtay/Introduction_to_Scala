sealed case class Stop()
sealed case class In[-A,+B](recv: A ⇒ B)
sealed case class Out[+A,+B](data: A, cont: B)

trait Session[S] {
    type Self = S
    type Dual
    type DualOf[D] = Session[Self] { type Dual = D }
    def run(self: Self, dual:Dual) : Unit
}

object PP {

    def add_server = 
        In{x : Int ⇒
        In{y: Int ⇒ println("Thinking")
        Out(x + y, Stop())}}

    def add_client = 
        Out(3,
        Out(4, { println("Waiting") 
        In{z : Int ⇒ println(z); Stop()}}))

    implicit object StopDual extends Session[Stop] {
        type Dual = Stop
        def run(self: Self, dual : Dual) = {}
    }

    implicit def InDual[D,C](implicit cont: Session[C])
    = new Session[In[D,C]] {
        type Dual = Out[D, cont.Dual]
        def run(self: Self, dual : Dual) : Unit = 
            cont.run(self.recv(dual.data), dual.cont)
        }

    implicit def OutDual[D,C](implicit cont: Session[C])
    = new Session[Out[D,C]] {
        type Dual = In[D, cont.Dual]
        def run(self: Self, dual : Dual) : Unit = 
            cont.run(self.cont, dual.recv(self.data))
    }

    def runSession[S,D:Session[S]#DualOf](session: S, dual: D) = 
        implicitly[Session[S]#DualOf[D]].run(session,dual)

    def myRun = runSession(add_server, add_client)
}
