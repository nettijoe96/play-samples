package common.repository

import java.net.SocketException
import java.util.NoSuchElementException

import common.exception.{DuplicateResourceException, ResourceNotFoundException, UpdateFailedException}
import org.slf4j.{Logger, LoggerFactory}
import reactivemongo.api.commands.{DefaultWriteResult, LastError, UpdateWriteResult, WriteResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration
import akka.pattern.after
import akka.actor.Scheduler
import reactivemongo.core.actors.Exceptions.{PrimaryUnavailableException, ChannelNotFound}

trait Guards {

  private val MongoDuplicateCode = 11000



  /*  def duplicateGuard[T](block: => Future[T]): Future[T] = {
      block recover {
        case le: LastError if le.code.contains(MongoDuplicateCode) =>
          val ERROR_REGEX = ".*duplicate key error collection: msc.(.+) index: (.+) dup.*".r
          le.errmsg.get match {
            case ERROR_REGEX(collection, index)=> throw DuplicateResourceException(index, s"$collection: $index already exists", le)
            case _ => throw DuplicateResourceException("index", "index already exists", le)
          }
      }
    }*/


  def errorGuard[T](block: => Future[T]): Future[T] = {
    block recover {
      case le: LastError if le.code.contains(MongoDuplicateCode) =>
        val ERROR_REGEX = ".*duplicate key error collection: msc.(.+) index: (.+) dup.*".r
        le.errmsg.get match {
          case ERROR_REGEX(collection, index)=> throw DuplicateResourceException(index, s"$collection: $index already exists", le)
          case _ => throw DuplicateResourceException("index", "index already exists", le)
        }

      case _: IllegalArgumentException | _: NoSuchElementException =>
        throw ResourceNotFoundException("Resource is invalid")

      case le: LastError if !le.code.contains(0) =>
        throw UpdateFailedException(le.code, le.errmsg, null)
    }
  }

  /*  def successGuard[T](ret: T)(block: => Future[WriteResult]): Future[T] = {
      block map {
        case wr: UpdateWriteResult if (wr.nModified == 0 || !wr.ok) => {
          val leOpt: Option[LastError] = WriteResult.lastError(wr)
          val code: Option[Int] = leOpt.fold(Option(-1))(le => le.code)
          val msg: Option[String] = leOpt.fold(Option("Update failed"))(le => le.errmsg)
          throw UpdateFailedException(code, msg, null, ret)
        }

        case wr: WriteResult if wr.ok => ret

        case wr: WriteResult if (wr.n == 0 || !wr.ok) => {
          val leOpt: Option[LastError] = WriteResult.lastError(wr)
          val code: Option[Int] = leOpt.fold(Option(-1))(le => le.code)
          val msg: Option[String] = leOpt.fold(Option("Update failed"))(le => le.errmsg)
          throw UpdateFailedException(code, msg, null, ret)
        }
      }
    }*/


  def successGuard[T](ret: T)(block: => Future[WriteResult]): Future[T] = {
    block map {
      wr => maybeError(wr, ret) match {
        case Right(t) => t
        case Left(e) => throw e
      }
    }
  }


  private def maybeError[T](wr: WriteResult, ret: T): Either[UpdateFailedException[T], T] = {
    wr match {
      case UpdateWriteResult(ok, n, nModified, upserted, writeErrors,
      writeConcernError, code, errmsg) if (!ok || (n == 0 && nModified == 0)
        || writeErrors.nonEmpty || writeConcernError.nonEmpty) => Left(UpdateFailedException(code, Some("Update failed, object version in the DB has changed, refresh your client and retry"), null))


      case DefaultWriteResult(ok, n, writeErrors, writeConcernError,
      code, errmsg) if (!ok || n == 0 || writeErrors.nonEmpty || writeConcernError.nonEmpty) => Left(UpdateFailedException(code, Some("Insert/Update failed"), null))

      case LastError(ok, errmsg, code, lastOp, n, singleShard,
      updatedExisting, upserted, wnote, wtimeout, waited, wtime, writeErrors,
      writeConcernError) => Left(UpdateFailedException(code, errmsg, null))

      case _ if wr.ok => Right(ret)  // return the same thing for now

      case _ => Left(UpdateFailedException(Some(-1), Some("No error message available"), null))

    }
  }

  /*  def errorGuard[T](block: => Future[T]): Future[T] = {
      block recover {
        case _: IllegalArgumentException | _: NoSuchElementException => throw ResourceNotFoundException("Resource is invalid")
      }
    }*/

  def logGuard[T](block: => Future[T])(implicit logger: Logger): Future[T] = {
    block recover {
      case e: Exception => logger.error(e.toString, e)
        throw e
    }
  }

  /**
    * https://gist.github.com/viktorklang/9414163
    * Retries with backoff
    * Given an operation that produces a T, returns a Future containing the result of T, unless an exception is thrown,
    * in which case the operation will be retried after _delay_ time, if there are more possible retries, which is configured through
    * the _retries_ parameter. If the operation does not succeed and there is no retries left, the resulting Future will contain the last failure.
    */
  def retryGuard[T](block: => Future[T])(implicit ec: ExecutionContext, s: Scheduler, delays: Seq[FiniteDuration], retries: Int): Future[T] =
    block recoverWith {
      case e@(_: PrimaryUnavailableException | _:SocketException |
              _: ChannelNotFound) if retries > 0 =>
        // due to trait, logger cannot be used here
        println(s"[info] common.repository.guards - Database exception:${e.getMessage}, retry count:${retries}")
        after(delays.head, s)(retryGuard(block)(ec, s, delays.tail, retries - 1))
    }

}