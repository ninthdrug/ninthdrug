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
package ninthdrug.util

import scala.language.implicitConversions
import scala.language.reflectiveCalls

object Json {
  type JsonType = { def json(): String }

  class JsonList(list: List[JsonType]) {
    def json(): String = {
      list.map(s => s.json).mkString("[", ",", "]")
    }
  }

  class JsonString(string: String) {
    def json(): String = Json.quote(string)
  }

  class JsonStringList(list: List[String]) {
    def json(): String = {
      list.map(s => Json.quote(s)).mkString("[", ",", "]")
    }
  }

  implicit def string2JsonString(string: String) = new JsonString(string)

  implicit def stringlist2JsonStringList(list: List[String]) = new JsonStringList(list)

  implicit def list2JsonList[A <: JsonType](xs: List[A]) = new JsonList(xs)

  def quote(string: String): String = {
    if (string == null) {
      "null"
    } else {
      "\"" + string.
      replace("\"", "\\\"").
      replace("\b", "\\b").
      replace("\f", "\\f").
      replace("\n", "\\n").
      replace("\r", "\\r").
      replace("\t", "\\t") +
      "\""
    }
  }
}
