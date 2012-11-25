/*
 * Copyright 2008-2012 Trung Dinh, Thomas Heuring
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
package ninthdrug.cms

import java.net._
import java.io._
import java.util.Date
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ninthdrug.http._
import ninthdrug.logging.Logger

class CmsHandler(
  serverConfig: ServerConfig,
  siteConfig: SiteConfig
) extends Handler {
  private val root = siteConfig.root
  private val sitename = siteConfig.name
  private val log = Logger(classOf[CmsHandler])

  override def handle(req: Request, conn: Connection): RequestResult = {
    
    val url = req.url
    log.debug(url) 
    val pages = CmsCache.pages
    pages.find(page => page.url == url) match {
      case None =>
        RequestPassed
      case Some(page) =>
        log.debug("Found CMS Page")
        log.debug(page.toString)
        val layout = CmsCache.getLayout(page.layoutid)
        val html = layout.html
        log.debug(html)
        val contentLength = html.length
        val headers = """|HTTP/1.1 200 OK
                         |Date: Sat, 07 Mar 2009 12:20:25 GMT
                         |Server: IT-DO.nl server
                         |Accept-Ranges: bytes
                         |Content-Length: """.stripMargin + contentLength + "\n" +
                      """|Connection: close
                         |Content-Type: text/html
                         |
                         |""".stripMargin

        val response = headers + html
        val writer = new OutputStreamWriter(conn.socket.getOutputStream)
        writer.write(response)
        writer.flush()
        writer.close()
        RequestHandled
    }
  }
}
