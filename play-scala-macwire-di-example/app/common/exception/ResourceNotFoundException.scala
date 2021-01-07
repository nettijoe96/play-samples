package common.exception

case class ResourceNotFoundException(message: String, throwable: Throwable = null) extends RuntimeException(message, throwable)
