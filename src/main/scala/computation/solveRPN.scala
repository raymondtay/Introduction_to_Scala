object solveRPN extends App {

  /* 
   Solving the reverse polish notation
  */

  override def main(args: Array[String] ) = {
    val l = List("10", "4", "3", "+", "2", "*", "-")
    val r = l.foldLeft(List.empty[String])(foldingFunction)
    println(foldingFunction(r,"")) // i was wtf too ... this impl doesn't flow like the Haskell version
  }

  // List(10, 4, 3, +, 2, *, -)
  def foldingFunction(expr : List[String], ele: String): List[String] = {
    println(ele +" ...")
    expr match {
        case "*" :: fst :: snd :: xs if xs != Nil => ele :: (fst.toInt * snd.toInt).toString :: xs
        case "+" :: fst :: snd :: xs if xs != Nil => ele :: (fst.toInt + snd.toInt).toString :: xs
        case "-" :: fst :: snd :: xs if xs != Nil => ele :: (snd.toInt - fst.toInt).toString :: xs
        case "*" :: fst :: snd :: Nil => (fst.toInt * snd.toInt).toString :: Nil
        case "+" :: fst :: snd :: Nil => (fst.toInt + snd.toInt).toString :: Nil
        case "-" :: fst :: snd :: Nil => (snd.toInt - fst.toInt).toString :: Nil
        case _ => {println(ele::expr); ele :: expr }
    }}

}
