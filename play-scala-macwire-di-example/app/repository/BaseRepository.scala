package repository

import models.Entry
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import common.repository.Repository

class BaseRepository(reactiveMongo: ReactiveMongoApi) extends Repository[Entry] {

  override protected def indexes: Seq[Index] = Seq(Index(key = Seq("id" -> IndexType.Ascending), name = Some("id"), unique = true))

  override protected def collection: Future[JSONCollection] = reactiveMongo.database.map(_.collection[JSONCollection]("entry"))

}
