package common.exception

case class UpdateFailedException[T](code: Option[Int] = None, errMsg: Option[String] = None, throwable: Throwable = null) extends RuntimeException(errMsg.getOrElse("No err msg available"), throwable) {

/*    override def toString: String = {
        s"""
           |Code = ${code.fold("N/A")(cd => cd.toString)}
           |Message = ${errMsg.fold("N/A")(msg => msg.toString)}
           |Cause = ${if (throwable == null) "N/A" else throwable.toString}
         """.stripMargin
    }*/

    override def toString: String = errMsg.fold("N/A")(msg => msg)
    
    override def getMessage: String = toString
}
