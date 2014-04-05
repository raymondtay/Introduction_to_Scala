package iomonad.free

sealed trait Free[F[_], A]

case class Return[F[_], A]() extends Free[F,A]

case class Suspend[F[_], A](s: F[Free[F,A]]) extends Free[F,A]

case class FlatMap[F[_],A,B](s: Free[F,A], f: A => Free[F,B]) extends Free[F,B]

sealed trait Console[R]

case class ReadLine[R](k : Option[String] => R ) extends Console[R]

case class PrintLine[R](s: String, k: () => R) extends Console[R]

object Console {
    type ConsoleIO[A] = Free[Console, A]

    //def ReadLine = IO { readLine }

    //def PrintLine(msg: String) = IO { println(msg) }

    //def readLn : ConsoleIO[Option[String]] = Suspend(ReadLine((s: Option[String]) => Return(s)))

    //def printLn(s: String) : ConsoleIO[Unit] = Suspend(PrintLine(s, () => Return(())))
}

