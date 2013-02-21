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
import scala.io.Source
import ninthdrug.logging.Logger
import Util._

class StaticHandler(val root: String) extends Handler {
  private val log = Logger(classOf[StaticHandler])
  override def handle(req: Request, conn: Connection): RequestResult = {
    val out = conn.socket.getOutputStream
    val path = root + req.path
    log.debug("StaticHandler: " + path)
    val file = new File(path)
    if (forbidden(req.path) || (file.exists && !file.canRead)) {
      write(out, "HTTP/1.1 403 Forbidden\n")
      write(out, "Content-Length: 0\n")
      write(out, "Content-Type: text/plain\n")
      write(out, "\n")
    } else if (!file.exists) {
      write(out, "HTTP/1.1 404 Not Found\n")
      write(out, "Content-Length: 0\n")
      write(out, "Content-Type: text/plain\n")
      write(out, "\n")
    } else {
      write(out, "HTTP/1.1 200 OK\n")
      write(out, "Date: " + Util.formatHTTPDate(new Date()) + "\n")
      write(out, "Server: ScalaServer\n")
      write(out, "Accept-Ranges: bytes\n")
      write(out, "Content-Length: " + file.length() + "\n")
      write(out, "Connection: close\n")
      write(out, "Content-Type: " + Mime.typeOf(getExt(path, '.')) + "\n")
      write(out, "\n")

      val in = new FileInputStream(file)
      val buf = new Array[Byte](1024)
      var len = in.read(buf)
      while (len > 0) {
        out.write(buf, 0, len)
        len = in.read(buf)
      }
      in.close()
    }
    RequestHandled
  }

  private def write(out: OutputStream, string: String) {
    val bytes = string.getBytes()
    out.write(bytes, 0, bytes.length)
  }

  private val forbiddenExts = List("class", "scala", "taak")

  private def forbidden(path: String): Boolean =
    path.startsWith("/WEB-INF") || forbiddenExts.contains(getExt(path, '.'))
}
