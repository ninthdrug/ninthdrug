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

class CannedHandler extends Handler {
  val CannedHeaders = """|HTTP/1.1 200 OK
                         |Date: Sat, 07 Mar 2009 12:20:25 GMT
                         |Server: Scala Thingie
                         |Accept-Ranges: bytes
                         |Content-Length: 44
                         |Connection: close
                         |Content-Type: text/html
                         |
                         |""".stripMargin

  val CannedResponse = CannedHeaders +
          "<html><body><h1>It works!</h1></body></html>"

  override def handle(req: Request, conn: Connection): RequestResult = {
    val writer = new OutputStreamWriter(conn.socket.getOutputStream)
    writer.write(CannedResponse)
    writer.flush()
    RequestHandled
  }
}
