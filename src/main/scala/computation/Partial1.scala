
object Partial1 {
    /* 
        Functions in Scala are non-curried forms by default, so if
        you need curried function representations, then u need to use
        `curried` to convert a non-curried function
    */
    def partial1[A,B,C](a: A, f: (A, B) ⇒ C) : B ⇒ C = f.curried(a)

    def curry[A,B,C](f: (A, B) ⇒ C) : A ⇒ B ⇒ C = f.curried

    def uncurry[A,B,C](f: A ⇒ B ⇒ C) : (A,B) ⇒ C = Function.uncurried(f)

    def compose[A,B,C](f: B ⇒ C, g: A ⇒ B) : A ⇒ C = g andThen f
}

