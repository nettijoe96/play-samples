package common.util


object MapUtil {

  def merge(map1: Map[String, Seq[String]], map2: Map[String, Seq[String]]): Map[String, Seq[String]] = {
    map1 ++ map2.map { case (k, v) => k -> (v ++ map1.getOrElse(k, Seq.empty[String])) }
  }

  def merge[A, B](ms: Seq[Map[A, B]])(f: (B, B) => B): Map[A, B] =
    (Map[A, B]() /: (for (m <- ms; kv <- m) yield kv)) { (a, kv) =>
      a + (if (a.contains(kv._1)) kv._1 -> f(a(kv._1), kv._2) else kv)
    }

  def merge[A, B](seqOfTuples: Seq[(A, B)]): Map[A, Seq[B]] = {
    seqOfTuples.groupBy(_._1).map { case (k, v) => (k, v.map(_._2)) }
  }

  def transform[A, B](seqOfTuples: Seq[(A, Seq[B])]): Map[A, Seq[B]] = {
    seqOfTuples.groupBy(_._1).map { case (k, v) => (k, v.flatMap(_._2).distinct) }
  }

}
