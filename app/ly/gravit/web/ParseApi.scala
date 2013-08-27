package ly.gravit.web

import play.api.Play.current
import scala.concurrent.Future
import play.api.libs.ws._
import play.api.Play
import play.Logger

/**
 * Created with IntelliJ IDEA.
 * User: ginduc
 * Date: 8/26/13
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
object ParseApi {
  private lazy val PARSE_API_URL = "https://api.parse.com";
  private lazy val PARSE_API_URL_CLASSES = "/1/classes/";
  private lazy val PARSE_API_HEADER_APP_ID = "X-Parse-Application-Id"
  private lazy val PARSE_API_HEADER_REST_API_KEY = "X-Parse-REST-API-Key"
  private lazy val PARSE_API_HEADER_CONTENT_TYPE = "Content-Type"
  private lazy val CONTENT_TYPE_JSON = "application/json"
  private lazy val APP_ID = Play.application.configuration.getString("parseapi.app.id").getOrElse(
    throw new IllegalStateException("Parse App ID is required"))
  private lazy val REST_API_KEY = Play.application.configuration.getString("parseapi.restapi.key").getOrElse(
    throw new IllegalStateException("Parse Rest API Key is required"))

  private def parseBaseUrl = "%s%s".format(PARSE_API_URL, PARSE_API_URL_CLASSES)

  def create(className: String, properties: Map[String, Any]) = {
    if (properties != null) {
      val reqParams = new StringBuilder(512)
      var idx = 0

      properties.map { case (key, value) =>
        if (idx > 0) {
          reqParams.append(",")
        }
        value match {
          case s: String => reqParams.append("\"%s\":\"%s\"".format(key, value))
          case _ => reqParams.append("\"%s\":%s".format(key, value))
        }
        idx = idx + 1
      }

      val query = WS.url("%s%s/%s".format(parseBaseUrl, className))
        .withHeaders(PARSE_API_HEADER_APP_ID -> APP_ID)
        .withHeaders(PARSE_API_HEADER_REST_API_KEY -> REST_API_KEY)
        .withHeaders(PARSE_API_HEADER_CONTENT_TYPE -> CONTENT_TYPE_JSON)

      query.post("{%s}".format(reqParams.toString))

    } else {
      throw new IllegalStateException("Create needs class properties")
    }
  }

  def get(className: String, objectId: String): Future[Response] = {
    if (objectId != null) {
      val query = WS.url("%s%s/%s".format(parseBaseUrl, className, objectId))
        .withHeaders(PARSE_API_HEADER_APP_ID -> APP_ID)
        .withHeaders(PARSE_API_HEADER_REST_API_KEY -> REST_API_KEY)

      if (Logger.isDebugEnabled) {
        Logger.debug("Find: " + query.url)
      }

      query.get
    } else {
      throw new IllegalStateException("Get methods must have an object id")
    }
  }

  def find(className: String, criteria: Map[String, Any]) = {
    if (criteria != null) {
      val reqParams = new StringBuilder(512)
      var idx = 0

      criteria.map { case (key, value) =>
        if (idx > 0) {
          reqParams.append(",")
        }
        value match {
          case s: String => reqParams.append("\"%s\":\"%s\"".format(key, value))
          case _ => reqParams.append("\"%s\":%s".format(key, value))
        }
        idx = idx + 1
      }

      val query = WS.url("%s%s".format(parseBaseUrl, className))
        .withHeaders(PARSE_API_HEADER_APP_ID -> APP_ID)
        .withHeaders(PARSE_API_HEADER_REST_API_KEY -> REST_API_KEY)
        .withQueryString("where" -> ",{%s}".format(reqParams.toString))

      if (Logger.isDebugEnabled) {
        Logger.debug("Find: " + query.url)
      }

      query.get
    } else {
      throw new IllegalStateException("Finder methods must have request parameters")
    }
  }
}
