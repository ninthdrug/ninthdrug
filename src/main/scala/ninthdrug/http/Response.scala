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

class Response(val headers: Headers) {
  def setCookie(cookie: Cookie) {
    headers.add("set-cookie", cookie.toString)
  }

  def setCookie(name: String, value: String) {
    val cookie = new Cookie(name, value, null, null, 0, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setCookie(name: String, value: String, maxage: Int) {
    val cookie = new Cookie(name, value, null, null, maxage, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setCookie(name: String, value: String, domain: String) {
    val cookie = new Cookie(name, value, domain, null, 0, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setCookie(name: String, value: String, domain: String, maxage: Int) {
    val cookie = new Cookie(name, value, domain, null, maxage, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setCookie(name: String, value: String, domain: String, path: String) {
    val cookie = new Cookie(name, value, domain, path, 0, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setCookie(name: String, value: String, domain: String, path: String,
                maxage: Int) {
    val cookie = new Cookie(name, value, domain, path, maxage, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setCookie(name: String, value: String, domain: String, path: String,
                maxage: Int, ssl: Boolean) {
    val cookie = new Cookie(name, value, domain, path, maxage, ssl)
    headers.add("set-cookie", cookie.toString)
  }

  def setEncryptedCookie(user: String, name: String, value: String) {
    val cookie = new EncryptedCookie(user, name, value, null, null, 0, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setEncryptedCookie(user: String, name: String, value: String, domain: String) {
    val cookie = new EncryptedCookie(user, name, value, domain, null, 0, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setEncryptedCookie(user: String, name: String, value: String, domain: String, path: String) {
    val cookie = new EncryptedCookie(user, name, value, domain, path, 0, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setEncryptedCookie(user: String, name: String, value: String, domain: String, path: String,
                         maxage: Int) {
    val cookie = new EncryptedCookie(user, name, value, domain, path, maxage, false)
    headers.add("set-cookie", cookie.toString)
  }

  def setEncryptedCookie(user: String, name: String, value: String, domain: String, path: String,
                         maxage: Int, ssl: Boolean) {
    val cookie = new EncryptedCookie(user, name, value, domain, path, maxage, ssl)
    headers.add("set-cookie", cookie.toString)
  }
}
