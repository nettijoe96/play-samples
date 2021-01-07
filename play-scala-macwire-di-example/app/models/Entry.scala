package models

//import play.api.libs.json.{Format, Json, Writes}
import play.api.libs.json._

case class Entry(name: String,
                num: Int)

object Entry {
  implicit val format: OFormat[Entry] = Json.format[Entry]
  implicit val writes: OWrites[Entry] = Json.writes[Entry]

}