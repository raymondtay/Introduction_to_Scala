package tryouts

import scala.language._

import scalaz._
import Scalaz._

sealed trait TrafficLight
case object Red extends TrafficLight
case object Green extends TrafficLight
case object Yellow extends TrafficLight

object TrafficLightImplicits {
    implicit val TrafficLightEQ : Equal[TrafficLight] = Equal.equal(_ == _)
}

/**
    If we wanted to define a typeclass with a particular knack
    for telling whether "something" is true or not, we can go right
    ahead and create it. Its a little too verbose as compared to say
    Haskell

    To run the following, enter the following in your favourite terminal
    $> sbt.console
    #> // brings u into the scala console
    #> import tryouts._; import ToIsItTrueOps._ ; import InstancesOfIsItTrue._
    #> List(111).truthy; Nil.truthy // etc
*/

// this trait defines what it is to be "true"
trait IsItTrue[A] { self =>
    def truthys(a: A) : Boolean
}

// this companion object defines the usual factory pattern
object IsItTrue {
    def apply[A : IsItTrue] : IsItTrue[A] = implicitly[IsItTrue[A]]
    def truthys[A](f: A => Boolean) : IsItTrue[A] = new IsItTrue[A] {
        def truthys(a: A) : Boolean = f(a)
    }

}

// this trait allows the developer the capability to define
// extensions to the "true-ness" of "true" and it has a few niceties
// from encapsulating the object in question via `self` and 
// confining the implicits lookup through this trait ;
// not to mention delaying the definition of `truthys` in the original
// trait.
trait IsItTrueOps[A] {
    def self: A
    implicit def F: IsItTrue[A]
    final def truthy: Boolean = F.truthys(self)
}

object ToIsItTrueOps {
    implicit def toIsItTrueOps[A](value: A)(implicit evidence : IsItTrue[A]) = 
        new IsItTrueOps[A] {
            def self = value
            implicit def F = evidence
        }
}

object InstancesOfIsItTrue {
    
    implicit val IsItTrue4Int : IsItTrue[Int] = 
        IsItTrue.truthys({
            case 0 => false
            case x if x <= Integer.MAX_VALUE && x >= Integer.MIN_VALUE => true
        })

    implicit def IsItTrue4List[A] : IsItTrue[List[A]] = 
        IsItTrue.truthys({
            case Nil => false
            case h :: t => true
        })

    // special handler for the Nil.type
    implicit val IsItTrue4Nil : IsItTrue[Nil.type] = 
        IsItTrue.truthys(_ => false)

    implicit val IsItTrue4Bool : IsItTrue[Boolean] = IsItTrue.truthys(identity)
}


