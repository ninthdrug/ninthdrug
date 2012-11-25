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

import ninthdrug.cms.CmsHandler
import ninthdrug.logging.Logger

class Site(
  val serverConfig: ServerConfig,
  val siteConfig: SiteConfig
) {
  private val log = Logger(classOf[Site])
  val handlers = siteConfig.siteType match {
    case StaticSite =>
      List(
        new DirectoryHandler(siteConfig.host, siteConfig.port, siteConfig.root),
        new StaticHandler(siteConfig.root)
      )
    case CmsSite =>
      List(
        new CmsHandler(serverConfig, siteConfig),
        new DirectoryHandler(siteConfig.host, siteConfig.port, siteConfig.root),
        new ScriptHandler(serverConfig, siteConfig),
        new StaticHandler(siteConfig.root)
      )
    case DynamicSite =>
      List(
        new DirectoryHandler(siteConfig.host, siteConfig.port, siteConfig.root),
        new ScriptHandler(serverConfig, siteConfig),
        new StaticHandler(siteConfig.root)
      )
  }

  def handle(servername: String, port: Int, req: Request, conn: Connection): RequestResult = {
    if ((servername == siteConfig.host || siteConfig.host == "") &&
        port == siteConfig.port &&
        req.path.startsWith(siteConfig.prefix)) {
      handlers exists (_.handle(req, conn) != RequestPassed)
      RequestHandled
    } else {
      RequestPassed
    }
  }
}

object Site {
  val STATIC = 1
  val DYNAMIC = 2
  val CMS = 3
}
