
object Partial1 {
    /* 
        Functions in Scala are non-curried forms by default, so if
        you need curried function representations, then u need to use
        `curried` to convert a non-curried function
    */
    def partial1[A,B,C](a: A, f: (A, B) ⇒ C) : B ⇒ C = f.curried(a)
}

