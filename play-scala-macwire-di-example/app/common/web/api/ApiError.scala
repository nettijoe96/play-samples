package common.web.api

import play.api.data.validation.ValidationError
import play.api.i18n.Messages
import play.api.libs.json._

/*
* Error representation for an ApiRequest.
* Contains the Status Code, a message information and an optional JSON data with additional information.
*/
case class ApiError(code: Int, msg: String, info: Option[JsValue]) extends ApiResult {
  val status: Int = ApiError.statusFromCode(code)
  val json: JsValue = ApiError.json(code, msg, info)
  val headers: Seq[(String, String)] = Seq()
}

object ApiError {

  //////////////////////////////////////////////////////////////////////
  // Status Codes

  final val STATUS_BADREQUEST = 400
  final val STATUS_UNAUTHORIZED = 401
  final val STATUS_FORBIDDEN = 403
  final val STATUS_NOTFOUND = 404
  final val STATUS_METHOD_NOT_ALLOWED = 405
  final val STATUS_CONFLICT = 409
  final val STATUS_PRECONDITION_FAILED = 412
  final val STATUS_INTERNAL_SERVER = 500
  final val STATUS_SERVICE_UNAVAILABLE = 503

  //////////////////////////////////////////////////////////////////////
  // Error Codes

  final val ERROR_CUSTOM = 0

  final val ERROR_APIKEY_NOTFOUND = 101
  final val ERROR_APIKEY_UNKNOWN = 102
  final val ERROR_APIKEY_DISABLED = 103
  final val ERROR_DATE_NOTFOUND = 104
  final val ERROR_DATE_MALFORMED = 105
  final val ERROR_TOKEN_HEADER_NOTFOUND = 106
  final val ERROR_TOKEN_NOTFOUND = 107
  final val ERROR_TOKEN_EXPIRED = 108
  final val ERROR_USER_NOTFOUND = 109
  final val ERROR_USER_EMAIL_UNCONFIRMED = 110
  final val ERROR_USER_INACTIVE = 111

  final val ERROR_PARAM_MALFORMED = 121
  final val ERROR_HEADER_MISSED = 122
  final val ERROR_HEADER_MALFORMED = 123
  final val ERROR_BODY_MISSED = 124
  final val ERROR_BODY_MALFORMED = 125

  final val ERROR_ITEM_NOTFOUND = 130
  final val ERROR_DUPLICATE_RESOURCE = 140
  final val ERROR_RESOURCE_NOT_FOUND = 141
  final val ERROR_INVALID_REF = 142
  final val ERROR_INVALID_FIELD = 143
  final val ERROR_CANNOT_DELETE = 144
  final val ERROR_INVALID_APIC_ID = 145

  final val ERROR_ACI_BADREQUEST = 150
  final val ERROR_ACI_REQUEST_TIMEOUT = 151
  final val ERROR_ACI_UNKNOWN_HOST = 160
  final val ERROR_ACI_UNAUTHORIZED = 161
  final val ERROR_ACI_HOST_NOTACCESSIBLE = 161

  final val ERROR_AUTH_METHOD_FAILED = 165

  final val ERROR_BADREQUEST = 400
  final val ERROR_UNAUTHORIZED = 401
  final val ERROR_FORBIDDEN = 403
  final val ERROR_NOTFOUND = 404
  final val ERROR_METHOD_NOT_ALLOWED = 405
  final val ERROR_REQUEST_TIMEOUT = 408
  final val ERROR_PRECONDITION_FAILED = 412
  final val ERROR_INTERNAL_SERVER = 500


  //////////////////////////////////////////////////////////////////////
  // Factory methods

  def apply(code: Int, msg: String): ApiError = ApiError(code, msg, None)
  def apply(code: Int, msg: String, info: JsValue): ApiError = ApiError(code, msg, Some(info))

  //////////////////////////////////////////////////////////////////////
  // More factory methods used as shortcuts for common errors

  def errorApiKeyNotFound(implicit m: Messages): ApiError = apply(ERROR_APIKEY_NOTFOUND, Messages("api.error.apikey.notfound"))
  def errorApiKeyUnknown(implicit m: Messages): ApiError = apply(ERROR_APIKEY_UNKNOWN, Messages("api.error.apikey.unknown"))
  def errorApiKeyDisabled(implicit m: Messages): ApiError = apply(ERROR_APIKEY_DISABLED, Messages("api.error.apikey.disabled"))
  def errorDateNotFound(implicit m: Messages): ApiError = apply(ERROR_DATE_NOTFOUND, Messages("api.error.date.notfound"))
  def errorDateMalformed(implicit m: Messages): ApiError = apply(ERROR_DATE_MALFORMED, Messages("api.error.date.malformed"))
  def errorTokenNotFound(implicit m: Messages): ApiError = apply(ERROR_TOKEN_HEADER_NOTFOUND, Messages("api.error.token.notfound"))
  def errorTokenUnknown(implicit m: Messages): ApiError = apply(ERROR_TOKEN_NOTFOUND, Messages("api.error.token.unknown"))
  def errorTokenExpired(implicit m: Messages): ApiError = apply(ERROR_TOKEN_EXPIRED, Messages("api.error.token.expired"))
  def errorUserNotFound(implicit m: Messages): ApiError = apply(ERROR_USER_NOTFOUND, Messages("api.error.user.notfound"))
  def errorUserEmailUnconfirmed(implicit m: Messages): ApiError = apply(ERROR_USER_EMAIL_UNCONFIRMED, Messages("api.error.user.email.unconfirmed"))
  def errorUserInactive(implicit m: Messages): ApiError = apply(ERROR_USER_INACTIVE, Messages("api.error.user.inactive"))

  def errorParamMalformed(param: String)(implicit m: Messages): ApiError = apply(ERROR_PARAM_MALFORMED, Messages("api.error.param.malformed", param))
  def errorHeaderNotFound(header: String)(implicit m: Messages): ApiError = apply(ERROR_HEADER_MISSED, Messages("api.error.header.notfound", header))
  def errorHeaderMalformed(header: String)(implicit m: Messages): ApiError = apply(ERROR_HEADER_MALFORMED, Messages("api.error.header.malformed", header))
  def errorBodyMissed(implicit m: Messages): ApiError = apply(ERROR_BODY_MISSED, Messages("api.error.body.missed"))
  def errorBodyMalformed(errors: Seq[(JsPath, Seq[ValidationError])])(implicit m: Messages): ApiError =
    apply(ERROR_BODY_MALFORMED, Messages("api.error.body.malformed"), JsObject(errors.map { e =>
      e._1.toJsonString -> JsArray(e._2.map { ve =>
        JsString(Messages(ve.message, ve.args: _*))
      })
    }))

  def errorMethodForbidden(implicit m: Messages): ApiError = apply(ERROR_FORBIDDEN, Messages("api.error.forbidden"))

  def errorItemNotFound(implicit m: Messages): ApiError = apply(ERROR_NOTFOUND, Messages("api.error.item.notfound"))
  def errorCustom(error: String)(implicit m: Messages): ApiError = apply(ERROR_CUSTOM, Messages(error))
  def errorInternal(error: String)(implicit m: Messages): ApiError = apply(ERROR_INTERNAL_SERVER, Messages(error))
  def errorInternal(implicit m: Messages): ApiError = errorInternal("api.error.internal")

  def errorBadRequest(error: String)(implicit m: Messages): ApiError = apply(ERROR_BADREQUEST, Messages("api.error.badrequest", error))
  def errorBadRequestMessage(error: String)(implicit m: Messages): ApiError = apply(ERROR_BADREQUEST, Messages(error))
  def errorUnauthorized(implicit m: Messages): ApiError = apply(ERROR_UNAUTHORIZED, Messages("api.error.unauthorized"))
  def errorForbidden(implicit m: Messages): ApiError = apply(ERROR_FORBIDDEN, Messages("api.error.forbidden"))
  def errorNotFound(implicit m: Messages): ApiError = apply(ERROR_NOTFOUND, Messages("api.error.notfound"))
  def errorPreconditionFailed(implicit m: Messages): ApiError = apply(ERROR_PRECONDITION_FAILED, Messages("api.error.precondition_failed"))
  def errorInternalServer(error: String)(implicit m: Messages): ApiError = apply(ERROR_INTERNAL_SERVER, error)

  def errorMethodNotAllowed(error: String)
                           (implicit m: Messages): ApiError = apply(ERROR_METHOD_NOT_ALLOWED, Messages("api.error.methodnotallowed"))
  // Custom
  def errorDuplicateResource(index: String, message: String)(implicit m: Messages): ApiError = apply(ERROR_DUPLICATE_RESOURCE, Messages("api.error.duplicateresource", index), JsObject(Seq((index, JsArray(Seq(JsString(message)))))))
  def errorNotFound(message: String)(implicit m: Messages): ApiError = apply(ERROR_NOTFOUND, Messages("api.error.resourcenotfound", message))
  def errorPreconditionFailed(message: String)(implicit m: Messages): ApiError = apply(ERROR_PRECONDITION_FAILED, Messages("api.error.precondition_failed", message))
  def errorResourceNotFound(message: String)(implicit m: Messages): ApiError = apply(ERROR_RESOURCE_NOT_FOUND, Messages("api.error.resourcenotfound", message))
  def errorInvalidRef(message:String)(implicit m: Messages): ApiError = apply(ERROR_INVALID_REF, Messages("api.error.invalidref", message))
  def errorInvalidField(message:String)(implicit m: Messages): ApiError = apply(ERROR_INVALID_FIELD, Messages("api.error.invalidfield", message))
  def errorCannotDelete(message:String)(implicit m: Messages): ApiError = apply(ERROR_CANNOT_DELETE, Messages("api.error.cannotdelete", message))
  def errorInvalidApicId(message:String)(implicit m: Messages): ApiError = apply(ERROR_INVALID_APIC_ID, Messages("api.error.invalidapicid", message))
  def errorAuthenticationFailure(message:String)(implicit m: Messages): ApiError = apply(ERROR_UNAUTHORIZED, Messages("api.error.authenticationfailure", message))
  def errorAciBadRequest(message:String)(implicit m: Messages): ApiError = apply(ERROR_ACI_BADREQUEST, Messages("api.aci.error.badrequest", message))
  def errorAciUnauthorized(message:String)(implicit m: Messages): ApiError = apply(ERROR_ACI_UNAUTHORIZED, Messages("api.aci.error.unauthorized", message))
  def errorAciUnknownHost(message: String)(implicit m: Messages): ApiError = apply(ERROR_ACI_UNKNOWN_HOST, Messages("api.aci.error.unknownhost", message))
  def errorAciHostNotAccessible(message: String)(implicit m: Messages): ApiError = apply(ERROR_ACI_HOST_NOTACCESSIBLE, Messages("api.aci.error.hostnotaccessible", message))
  def errorAciRequestTimeout(message: String)(implicit m: Messages): ApiError = apply(ERROR_REQUEST_TIMEOUT, Messages("api.aci.error.requesttimeout", message))
  def errorAuthMethodFailed(message: String)(implicit m: Messages): ApiError = apply(ERROR_AUTH_METHOD_FAILED, Messages("api.error.authmethodfailed", message))
  def errorNoExternalAuthConfiguredFailure(message: String)(implicit m: Messages): ApiError = apply(STATUS_BADREQUEST, Messages("api.error.externalauth.notconfigured", message))
  def errorUnsupportedAuthException(message: String)(implicit m: Messages): ApiError = apply(ERROR_UNAUTHORIZED, Messages("api.error.unsupported.authmethod", message))
  def errorBackupRestoreFailed(message:String, restoreLogs: JsValue)(implicit m: Messages): ApiError = apply(ERROR_INTERNAL_SERVER, message, restoreLogs)

  //////////////////////////////////////////////////////////////////////
  // Utility methods

  /*
	* Create a JSON object with the representation of the error, with a code, a message and an optional additional information.
	*/
  def json(code: Int, msg: String, info: Option[JsValue]): JsValue = {
    val o = Json.obj(
      "code" -> code,
      "message" -> msg
    )
    info.map(i => o + ("info" -> i)).getOrElse(o)
  }

  /*
	* Returns a Status Code from an Error Code
	*/
  def statusFromCode(code: Int): Int = code match {
    case ERROR_APIKEY_NOTFOUND => STATUS_BADREQUEST
    case ERROR_APIKEY_UNKNOWN => STATUS_UNAUTHORIZED
    case ERROR_APIKEY_DISABLED => STATUS_UNAUTHORIZED
    case ERROR_DATE_NOTFOUND => STATUS_BADREQUEST
    case ERROR_DATE_MALFORMED => STATUS_BADREQUEST
    case ERROR_TOKEN_HEADER_NOTFOUND => STATUS_UNAUTHORIZED
    case ERROR_TOKEN_NOTFOUND => STATUS_UNAUTHORIZED
    case ERROR_TOKEN_EXPIRED => STATUS_UNAUTHORIZED

    case ERROR_DUPLICATE_RESOURCE => STATUS_CONFLICT
    case ERROR_CANNOT_DELETE => STATUS_CONFLICT

    case ERROR_BADREQUEST => STATUS_BADREQUEST
    case ERROR_UNAUTHORIZED => STATUS_UNAUTHORIZED
    case ERROR_FORBIDDEN => STATUS_FORBIDDEN
    case ERROR_NOTFOUND => STATUS_NOTFOUND
    case ERROR_METHOD_NOT_ALLOWED => STATUS_METHOD_NOT_ALLOWED
    case ERROR_PRECONDITION_FAILED => STATUS_PRECONDITION_FAILED
    case ERROR_INTERNAL_SERVER => STATUS_INTERNAL_SERVER

    case ERROR_AUTH_METHOD_FAILED => STATUS_SERVICE_UNAVAILABLE

    case _ => STATUS_BADREQUEST
  }

}
