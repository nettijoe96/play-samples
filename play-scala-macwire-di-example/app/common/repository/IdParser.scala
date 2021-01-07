package common.repository

import java.util.NoSuchElementException

import common.exception.ResourceNotFoundException
import reactivemongo.bson.BSONObjectID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait IdParser {

  def parse[T](id: String)(action: BSONObjectID => Future[T]): Future[T] = {
    Future.fromTry(BSONObjectID.parse(id)).flatMap(action) recover {
      case _: IllegalArgumentException | _: NoSuchElementException => throw ResourceNotFoundException(s"Resource $id is invalid")
    }
  }

  // had to rename method; couldn't find a solution to ambiguous reference to overloaded definition
  def parseWith[T](id: String)(action: BSONObjectID => T): T = {
    val objectId = BSONObjectID.parse(id).getOrElse(throw ResourceNotFoundException(s"Resource $id is invalid"))
    action(objectId)
  }

  def parse[T](ids: Seq[String])(action: Seq[BSONObjectID] => Future[T]): Future[T] = {
    Future.sequence(ids.map(id => Future.fromTry(BSONObjectID.parse(id)))).flatMap(action)
  }

}