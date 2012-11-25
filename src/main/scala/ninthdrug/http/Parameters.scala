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

import java.net.URLDecoder
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import ninthdrug.logging.Logger
import Util.break

class Parameters(query: String) {
  lazy val params: mutable.Map[String, ArrayBuffer[String]] = parse(query)

  private def parse(query: String): mutable.Map[String, ArrayBuffer[String]] = {
    val p = mutable.Map[String, ArrayBuffer[String]]()
    if (query.length > 0) {
      val tokens = query.split("&")
      for (token <- query.split("&"); if (token.length > 0)) {
        val (k, v) = break(token, '=')
        val key = URLDecoder.decode(k.trim, "UTF-8")
        val value = URLDecoder.decode(v.trim, "UTF-8")
        if (key.length > 0) {
          if (!p.contains(key)) {
            p.update(key, new ArrayBuffer[String])
          }
          p(key) += value
        }
      }
    }
    p
  }

  def keys = params.keys

  def getFirst(key: String): String = params.apply(key)(0)

  def getAll(key: String): ArrayBuffer[String] = params.apply(key)

  def apply(key: String): String = params.apply(key).mkString(";")

  def contains(key: String): Boolean = params.contains(key)

  def add(key: String, value: String) {
    if (!params.contains(key)) {
      params.update(key, new ArrayBuffer[String])
    }
    params(key) += value
  }
}

object Parameters {
  def apply() = new Parameters("")

  def apply(string: String) = new Parameters(string)
}
