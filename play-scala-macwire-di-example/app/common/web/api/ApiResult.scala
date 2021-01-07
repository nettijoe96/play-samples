package common.web.api

import common.web.api.ApiError._
import common.web.api.ApiResponse._
import play.api.i18n.Lang
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}


/*
* The result of an ApiRequest.
*/
trait ApiResult {
  val status: Int
  val json: JsValue
  val headers: Seq[(String, String)]

  /*
	* Log the request
	*/
  def saveLog[R <: RequestHeader](implicit request: ApiRequestHeader[R]): ApiResult = {
    // TODO: Log the request, response status and json response body.
    this
  }

  /*
	* Envelopes the resulting JSON in case the API client couldn't access to the headers
	*/
  def envelopedJson(implicit lang: Lang): JsValue = Json.obj(
    "data" -> json,
    "status" -> status,
    "headers" -> JsObject((headers ++ Api.basicHeaders).map(h => h._1 -> JsString(h._2)))
  )

  /*
	* Returns a Result with the ApiResult information.
	* If needed, it envelopes the resulting JSON in case the API client couldn't access to the headers
	*/
  def toResult[R <: RequestHeader](implicit request: R): Result = {
    val envelope = request.getQueryString("envelope") == Some("true")
    toResult(envelope)
  }
  def toResult(envelope: Boolean = false): Result = {
    val js = if (envelope) envelopedJson else json
    (status match {
      case STATUS_CREATED => if (js == JsNull) Created else Created(js)
      case STATUS_ACCEPTED => if (js == JsNull) Accepted else Accepted(js)
      case STATUS_NOCONTENT => NoContent
      case STATUS_REDIRECT => Found("")
      case s if s < 300 => if (js == JsNull) Ok else Ok(js)

      case STATUS_BADREQUEST => BadRequest(js)
      case STATUS_UNAUTHORIZED => Unauthorized(js)
      case STATUS_FORBIDDEN => Forbidden(js)
      case STATUS_NOTFOUND => NotFound(js)
      case STATUS_CONFLICT => Conflict(js)
      case STATUS_PRECONDITION_FAILED => PreconditionFailed(js)
      case s if s > 400 && s < 500 => BadRequest(js)
      case _ => InternalServerError(js)
    }).withHeaders((headers ++ Api.basicHeaders): _*)
  }
}