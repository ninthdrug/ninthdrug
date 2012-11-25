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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.net.URLDecoder
import java.net.URLEncoder
import ninthdrug.config.EncryptionKey
import ninthdrug.util.Crypto
import scala.collection.mutable
import Util.break

object CookieParser {
  def parse(header: String): Map[String, String] = {
    val cookies = mutable.Map[String, String]()
    if (header != null && header.length > 0) {
      for (token <- header.split("; "); if (token.length > 0)) {
        val (k, v) = break(token, '=')
        val key = URLDecoder.decode(k.trim, "UTF-8")
        val value = URLDecoder.decode(v.trim, "UTF-8")
        if (key.length > 0 && !cookies.contains(key)) {
          cookies.update(key, value)
        }
      }
    }
    return Map[String, String]() ++ cookies
  }
}
