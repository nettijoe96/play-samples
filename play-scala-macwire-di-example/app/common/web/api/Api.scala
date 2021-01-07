package common.web.api

import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import common.domain.LinkMeta
import common.util.MapUtil
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.i18n.Lang
import play.api.mvc.{Call, RequestHeader}

/*
* Set of general values and methods for the API
*/
object Api {

  //////////////////////////////////////////////////////////////////////
  // Headers

  final val HEADER_CONTENT_TYPE = "Content-Type"
  final val HEADER_CONTENT_LANGUAGE = "Content-Language"
  final val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
  final val HEADER_DATE = "Date"
  final val HEADER_LOCATION = "Location"
  final val HEADER_LINK = "Link"

  final val HEADER_API_KEY = "X-Api-Key"
  final val HEADER_AUTH_TOKEN = "X-Auth-Token"

  final val HEADER_PAGE = "X-Page"
  final val HEADER_PAGE_FROM = "X-Page-From"
  final val HEADER_PAGE_SIZE = "X-Page-Size"
  final val HEADER_PAGE_TOTAL = "X-Page-Total"

  final val HEADER_PAGE_PREV_OFFSET = "X-Page-Prev-Offset"
  final val HEADER_PAGE_NEXT_OFFSET = "X-Page-Next-Offset"
  final val HEADER_PAGE_LIMIT = "X-Page-Limit"

  final val HEADER_TOTAL_COUNT = "X-Total-Count"

  final val HEADER_CONNECTION = "Connection"


  def basicHeaders(implicit lang: Lang) = Seq(
    HEADER_DATE -> printHeaderDate(new DateTime()),
    HEADER_CONTENT_LANGUAGE -> lang.language,
    HEADER_CONNECTION -> "close"
  )

  def locationHeader(uri: String): (String, String) = HEADER_LOCATION -> uri
  def locationHeader(call: Call)(implicit request: RequestHeader): (String, String) = locationHeader(call.url)

  def linkAndCountHeaders(linkMetaOpt: Option[LinkMeta])(implicit request: RequestHeader): Seq[(String, String)] = {
    linkMetaOpt.fold(Seq.empty[(String, String)])(linkMeta => Seq(linkHeader(linkMeta)(request), totalCountHeader(linkMeta)))
  }

  def totalCountHeader(linkMeta: LinkMeta): (String, String) = HEADER_TOTAL_COUNT -> linkMeta.total.toString

  def linkHeader(linkMeta: LinkMeta)(implicit request: RequestHeader): (String, String) = {

    def makeLink(params: Map[String, Seq[String]], rel: String) = s"<${makeUrl(params)}>; rel=${'"'}$rel${'"'}"
    def makeUrl(params: Map[String, Seq[String]]): String = {
      request.path + "?" + params.map(param => param._1 + "=" + URLEncoder.encode(param._2.head, "UTF-8")).mkString("&")
    }

    val paramsWithLimit = MapUtil.merge(
      request.queryString - "limit" - "offset" - "jwt",
      Map("limit" -> Seq(linkMeta.limit.toString))
    )
    val links = Seq(
      linkMeta.prevOffset.fold(Option.empty[String]) { prev =>
        Option(makeLink(MapUtil.merge(paramsWithLimit, Map("offset" -> Seq(prev.toString))), "prev"))
      },
      linkMeta.nextOffset.fold(Option.empty[String]) { next =>
        Option(makeLink(MapUtil.merge(paramsWithLimit, Map("offset" -> Seq(next.toString))), "next"))
      }
    )
    HEADER_LINK -> links.flatten.mkString(", ")
  }


  //////////////////////////////////////////////////////////////////////
  // Date and joda.DateTime utils

  private final val longDateTimeFormatter = DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss 'GMT'").withLocale(Locale.ENGLISH).withZoneUTC()
  def parseHeaderDate(dateStr: String): DateTime = longDateTimeFormatter.parseDateTime(dateStr)
  def printHeaderDate(date: DateTime): String = longDateTimeFormatter.print(date)

  private final val dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
  def parseDateTime(dateStr: String): Date = dateTimeFormatter.parse(dateStr)
  def printDateTime(date: Date): String = dateTimeFormatter.format(date)
  private final val dateFormatter = new SimpleDateFormat("dd-MM-yyyy")
  def parseDate(dateStr: String): Date = dateFormatter.parse(dateStr)
  def printDate(date: Date): String = dateFormatter.format(date)

  //////////////////////////////////////////////////////////////////////
  // Sorting

  object Sorting {
    final val ASC = false
    final val DESC = true
  }
}