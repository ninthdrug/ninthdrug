/*
 * Copyright 2008-2012 Trung Dinh
 *
 *  This file is part of Ninthdrug.
 *
 *  Ninthdrug is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Nithdrug is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ninthdrug.  If not, see <http://www.gnu.org/licenses/>.
 */
package ninthdrug.http

import java.io._
import java.net.Socket
import ninthdrug.config.EncryptionKey
import ninthdrug.logging.Logger
import ninthdrug.util.Crypto
import scala.collection.Map
import scala.collection.immutable.HashMap
import scala.collection.mutable
import mutable.ArrayBuffer

sealed abstract class RequestResult
case object RequestHandled extends RequestResult
case object RequestPassed extends RequestResult

/**
 * An HTTP request.
 */
class Request(
  val method: String,
  val path: String,
  val query: String,
  val headers: Headers,
  val params: Parameters
) {
  private val cookies: Map[String, String] = {
    if (headers.contains("cookie")) {
      CookieParser.parse(headers.getFirst("cookie"))
    } else {
      HashMap[String, String]()
    }
  }

  def getParam(name: String): String = {
    if (params.contains(name)) {
      params(name)
    } else {
      null
    }
  }

  def getCookie(name: String): String = {
    if (cookies.contains(name)) {
      cookies(name)
    } else {
      null
    }
  }

  def getEncryptedCookie(name: String): String = {
    if (cookies.contains(name)) {
      Crypto.decryptCookie(EncryptionKey.key, cookies(name))
    } else {
      null
    }
  }

  def cookieNames = cookies.keys

  def host(): String = {
    val hostheader = headers.get("host")
    if (hostheader == null) {
      ""
    } else {
      hostheader.split(':')(0)
    }
  }

  def port(): Int = {
    val hostheader = headers.get("host")
    if (hostheader == null) {
      80
    } else {
      val parts = hostheader.split(':')
      if (parts.length > 1) {
        parts(1).toInt
      } else {
        80
      }
    }
  }

  def url(): String = {
    return "http://" + host + ":" + port + path
  }
}

object Request {
  private val log = Logger(classOf[Request])
  val InvalidRequest = new Request("INVALID", "", "", Headers(), Parameters())

  def apply(method: String, path: String, query: String, headers: Headers, params: Parameters) =
    new Request(method, path, query, headers, params)

  private val HTTPReq = "^(GET|POST|HEAD) (/[^ ?]*)(\\?[^ ]*)? (.*)".r

  def apply(in: InputStream): Option[Request] = {
    val reader = new BufferedReader(new InputStreamReader(in))
    try {
      val requestLine = reader.readLine()
      if (requestLine == null) {
        return None
      }
      val trimmed = requestLine.trim
      trimmed match {
        case HTTPReq(method, path, queryOpt, protocol) =>
          val query = if (queryOpt == null) "" else queryOpt.substring(1).trim
          val headers = Headers(reader)
          val params = method match {
            case "POST" => parsePostParams(headers, reader)
            case _ => Parameters(query)
          }
          Some(new Request(method, path, query, headers, params))
        case _ => None
      }
    } catch {
      case e: Exception => None
    }
  }

  private def readbuf(reader: BufferedReader, buf: Array[Char]): Int = {
    val len = buf.length
    var total = 0
    var count = 0
    while (total < len && count != -1) {
      count = reader.read(buf, total, len - total)
      if (count != -1) total = total + count
    }
    return total
  }

  private def parsePostParams(headers: Headers, reader: BufferedReader): Parameters = {
    var params = Parameters()
    try {
      val ct = headers.get("content-type")
      if (ct != null) {
        if (ct.startsWith("application/x-www-form-urlencoded")) {
          val contentLength = headers.get("content-length")
          if (contentLength != null) {
            val len = contentLength.toInt
            val buf = new Array[Char](len)
            val count = readbuf(reader, buf)
            if (count != len) {
              log.info("POST Request truncated")
            }
            val query = new String(buf, 0, count)
            params = Parameters(query)
          }
        }
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    return params
  }
}
