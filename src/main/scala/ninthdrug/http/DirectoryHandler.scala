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

import java.net._
import java.io._
import java.util.Date
import ninthdrug.config.Config
import ninthdrug.logging.Logger
import Util._

class DirectoryHandler(
  val host: String,
  val port: Int,
  val root: String
) extends Handler {
  private val log = Logger(classOf[DirectoryHandler])

  override def handle(req: Request, conn: Connection): RequestResult = {
    val file = new File(root + req.path)
    if (!file.isDirectory) {
      return RequestPassed
    } else {
      val base = if (req.path.endsWith("/")) req.path else req.path + "/"
      val indexes = {
        val key = "ninthdrug.http." + host + "." + port + ".index"
        Config.all(key).toList match {
          case List() =>
            Config.all("ninthdrug.http.index").toList match {
              case List() => List("index", "home")
              case list => list
            }
          case list => list
        }
      }
      val exts = List(".htm", ".html", ".taak")
      for (index <- indexes; ext <- exts) {
        if (new File(root + base + index + ext).isFile) {
          val path = base + index + ext;
          val location = if (req.port == 80) {
              "http://" + req.host + path
          } else {
              "http://" + req.host + ":" + req.port + path
          }
          log.debug("redirect to " + location)
          val writer = new BufferedWriter(
            new OutputStreamWriter(
              conn.socket.getOutputStream))
          writer.write("HTTP/1.1 302 Found\n")
          writer.write("Location: " + location + "\n")
          writer.write("Content-Type: text/html\n")
          writer.write("\n")
          writer.write("<html>\n")
          writer.write("<head></head>\n")
          writer.write("<body>\n")
          writer.write("Moved to " + path + "\n")
          writer.write("</body>\n")
          writer.write("</html>\n")
          writer.close()
          return RequestHandled
        }
      }
      val writer = new BufferedWriter(
        new OutputStreamWriter(
          conn.socket.getOutputStream))
      writer.write("HTTP/1.1 403 Forbidden\n")
      writer.write("Content-Type: text/html\n")
      writer.write("\n")
      writer.write("<html>\n")
      writer.write("<head></head>\n")
      writer.write("<body>\n")
      writer.write("401 Forbidden: ")
      writer.write(req.path + "\n")
      writer.write("</body>\n")
      writer.write("</html>\n")
      writer.close()
      RequestHandled
    }
  }
}
