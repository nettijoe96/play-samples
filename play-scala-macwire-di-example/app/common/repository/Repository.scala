package common.repository

import akka.actor.{ActorSystem, Scheduler}
import common.formatter.MongoObjectIdFormatter
import play.api.libs.json.{JsObject, Json, OWrites, Reads}
import reactivemongo.api.indexes.Index
import reactivemongo.api.{Cursor, CursorProducer, QueryOpts, ReadPreference}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import common.repository.{IdParser, Guards}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


trait Repository[T] extends MongoObjectIdFormatter[T] with IdParser with Guards {

  protected def dropIndexes: Seq[String] = Nil

  protected def indexes: Seq[Index] = Nil

  protected def collection: Future[JSONCollection]

  protected val jsWriter = JSONSerializationPack.IdentityWriter

  val defaultDelays: Seq[FiniteDuration] = Seq(1 second)
  val defaultRetries: Int = 1
  implicit val scheduler: Scheduler = ActorSystem("repo").scheduler

  //
  // ...
  //

  def insert(document: T)(implicit writes: OWrites[T], delays: Seq[FiniteDuration] = defaultDelays, retries: Int = defaultRetries): Future[T] = {
    retryGuard {
      errorGuard {
        successGuard(document)(collection.flatMap(_.insert(document)(mongoWrites(writes), global)))
      }
    }
  }

  //
  // ...
  //

}
