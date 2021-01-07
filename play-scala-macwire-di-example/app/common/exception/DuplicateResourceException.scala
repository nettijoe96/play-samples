package common.exception

case class DuplicateResourceException(index: String, message: String, throwable: Throwable = null) extends RuntimeException(message, throwable)
