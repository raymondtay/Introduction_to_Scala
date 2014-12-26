package tryouts

import scala.language._

trait Applicative[F[_]] extends Apply[F] { self =>
    def point[A](a: => A) : F[A]

    def pure[A](a: => A) : F[A] = point(a)
}

