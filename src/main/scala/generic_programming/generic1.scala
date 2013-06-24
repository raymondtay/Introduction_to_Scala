package generic_programming
// Reducing Code Duplication with Type Constructor Polymorphism
/*
// Listing 1 shows a Scala implementaiton of the trait Iterable[T]
// It contains an abstract method filter and a convenience method 
// 'remove'. Subclasses should implement filter so that it creates a
// new collection by retaining only the elements of the current colelction that
// satisfy the predicate p. 
trait Iterable[T] {
  def filter(p : T => Boolean) : Iterable[T]
  def remove(p: T => Boolean) : Iterable[T] = filter(x => !p(x))
}

trait List[T] extends Iterable[T] {
    def filter(p: T => Boolean) : List[T]
    override def remove(p : T => Boolean) : List[T] = filter(x => !p(x))
}

// The solution to the code duplication because of the weakness in the type system
// is to introduce a type constructor to abtract over the the container that
// 'filter' and 'remove' represents. 
trait Iterable[T, Container[X]] {
    def filter(p : T => Boolean) : Container[T]
    def remove(p : T => Boolean) : Container[T] = filter(x => !p(x))
}

trait List[T] extends Iterable[T, List]

// Builder relies on type constructor polymorphism as it must
// abstract over the type constructor that represents the collection
// it builds. The += method i s used to supply the 
// elements in the order in which they should appear in the collection.
trait Builder[Container[X], T] {
    def += (element : T ) : Unit
    def finalize() : Container[T]
}

trait Iterator[T] {
    def next(): T 
    def hasNext: Boolean
    def foreach(op: T => Unit) : Unit = while(hasNext) op(next())
}

// The design continues to evolve till...
// The new 'Iterable' design uses a type constructor 
// member, Container, to abstract over the precise type of the container
// whereas 'Buildable' uses a parameter.
trait Buildable[Container[X]] {
  def build[T] : Builder[Container, T] 
  def buildWith[T](f: Builder[Container, T] => Unit) : Container[T] = {
    val buff = build[T]
    f(buff) // the loan patternn
    buff.finalize()
  }
}

object OptionBuildable extends Buildable[Option] {
  def build[T] : Builder[Option, T] = new Builder[Option, T] {
        var res: Option[T] = None
        def +=(el: T) = if(res.isEmpty) res = Some(el)
          else throw new UnsupportedOperationException(">1 elements")
        def finalize(): Option[T] = res
  }
}

object ListBuildable extends Buildable[List] {
  def build[T] : Builder[List, T] = new ListBuffer[T] with Builder[List, T] {
        def finalize() : List[T] = toList
  }
}
// Notice that this definition of Iterable has a type constructor
// member that abstracts the precise type of the container 
// and this makes sense in this situation because most users of Iterable
// don't care about the container it's referencing to so its encapsulated
// here.
trait Iterable[T] {
  type Container[X] <: Iterable[X]

  def elements: Iterator[T]
  
  def mapTo[U, C[X]](f: T => U)(b: Buildable[C]): C[U] = {
    val buff = b.build[T]
    val elems = elements
    while(elems.hasNext) buff += f(elems.next)

    buff.finalize()
  }

  def filterTo[C[X]](p : T => Boolean)(b: Buildable[C]) : C[T] = {
    val elems = elements
    while(elems.hasNext) {
      val e = elems.next
      if(p(e)) buff += e
    }
  }

  def flatMapTo[U, C[X]](f: T => Iterable[U])(b: Buildable[C]) : C[U] = {
    val buff = b.build[U]
    val elems = elements

    while(elems.hasNext) {
      f(elems.next).elements.foreach{ e1 => buff += e1 }
    }
    buff.finalize()
  }

  def map[U](f: T => U)(b: Buildable[Container]): Container[U] = mapTo[U, Container](f)(b)
  def filter(p: T => Boolean)(b: Buildable[Container]): Container[T] = filterTo[Container](p)(b)
  def flatMap[U](f: T => Container[U])(b: Buildable[Container]): Container[U] = flatMapTo[U, Container](f)(b)

}*/
