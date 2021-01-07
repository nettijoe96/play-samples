package common.formatter

import play.api.libs.json._
import reactivemongo.play.json._

trait MongoObjectIdFormatter[T] {

  implicit def mongoWrites(implicit w: OWrites[T]): OWrites[T] = {
    w.transform { (js: JsObject) => js - "id" ++ Json.obj("_id" -> Json.obj("$oid" -> (js \ "id").as[String])) }
  }

  implicit def mongoReads(implicit r: Reads[T]): Reads[T] = {
    __.json.update((__ \ 'id).json.copyFrom((__ \ '_id \ '$oid).json.pick[JsString] )) andThen r
  }

}