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

class Cookie(name: String,
             value: String,
             domain: String,
             path: String,
             maxage: Int,
             ssl: Boolean) {
  override def toString(): String = {
    val buf = new StringBuilder()
    buf.append(URLEncoder.encode(name, "UTF-8"))
    buf.append("=")
    buf.append(URLEncoder.encode(value, "UTF-8"))
    if (domain != null) {
      buf.append("; domain=").append(domain)
    }
    if (path != null) {
      buf.append("; path=").append(path)
    }
    if (maxage != 0) {
      val fmt = new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm:ss")
      fmt.setTimeZone(TimeZone.getTimeZone("GMT"))
      val expires = new Date(System.currentTimeMillis + 1000 * maxage)
      buf.append("; expires=").append(fmt.format(expires)).append(" GMT")
    }
    if (ssl) {
      buf.append("; secure")
    }
    buf.toString
  }
}

class EncryptedCookie(
  user: String,
  name: String,
  value: String,
  domain: String,
  path: String,
  maxage: Int,
  ssl: Boolean
) extends Cookie(name, value, domain, path, maxage, ssl) {
  override def toString(): String = {
    val encryptedValue = Crypto.encryptCookie(EncryptionKey.key, user, value, maxage)
    val buf = new StringBuilder()
    buf.append(URLEncoder.encode(name, "UTF-8"))
    buf.append("=")
    buf.append(URLEncoder.encode(encryptedValue, "UTF-8"))
    if (domain != null) {
      buf.append("; domain=").append(domain)
    }
    if (path != null) {
      buf.append("; path=").append(path)
    }
    if (maxage > 0) {
      val fmt = new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm:ss")
      fmt.setTimeZone(TimeZone.getTimeZone("GMT"))
      val expires = new Date(System.currentTimeMillis + 1000 * maxage)
      buf.append("; expires=").append(fmt.format(expires)).append(" GMT")
    }
    if (ssl) {
      buf.append("; secure")
    }
    buf.toString
  }
}
