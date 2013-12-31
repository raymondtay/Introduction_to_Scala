object QS {

    type Pred2[a] = (a,a) ⇒ Boolean 
    type Pred[a] = (a) ⇒ Boolean

    def quicksort[a](lt: Pred2[a])(xs: List[a]) : List[a] = 
        xs.isEmpty match {
            case true  ⇒ xs
            case false ⇒ {
                    val pivot : a = xs.head
                    val smaller        = quicksort(lt)(xs.tail.filter(e ⇒ lt(e, pivot)))
                    val greaterOrEqual = quicksort(lt)(xs.tail.filter(e ⇒ !lt(e, pivot)))
                    smaller ::: List(pivot) ::: greaterOrEqual
            }
        }

    def partition[a](xs: List[a], pred: Pred[a]) : (List[a], List[a]) = 
        xs.isEmpty match {
            case true  ⇒ (List(), List())
            case false ⇒ {
                    val tail = partition(xs.tail, pred)
                    pred(xs.head) match {
                        case true  ⇒ (xs.head :: tail._1, tail._2)
                        case false ⇒ (tail._1, xs.head :: tail._2)
                    }
            }
        }

    def quicksortP[a](pred: Pred2[a])(xs: List[a]) : List[a] = 
        xs.isEmpty match {
            case true  ⇒ xs
            case false ⇒ {
                    val pivot = xs.head
                    val sub = partition(xs.tail, { e:a ⇒ pred(e, pivot) })
                    quicksortP(pred)(sub._1) ::: List(pivot) ::: quicksortP(pred)(sub._2)
            }
        } 

}

