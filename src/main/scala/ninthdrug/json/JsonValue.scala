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
 *  Ninthdrug is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ninthdrug.  If not, see <http://www.gnu.org/licenses/>.
 */
package ninthdrug.json

import collection.immutable.ListMap
import collection.immutable.Vector

trait JsonValue {
  def convertTo[T](implicit decoder: JsonDecoder[T]): T =
    decoder.decode(this)
}

case class JsonObject (pairs: (String,JsonValue)*) extends JsonValue {
  val entries: ListMap[String,JsonValue] = ListMap() ++ pairs

  def apply(name: String): JsonValue = entries(name)

  override lazy val toString: String = {
    entries.map {
      case (k, v) => Json.quote(k) + ":" + v.toString
    }.mkString("{", ",", "}")
  }
}

case class JsonArray (entries: JsonValue*) extends JsonValue {
  val vector = Vector() ++ entries

  def apply(index: Int): JsonValue = vector(index)

  override lazy val toString: String = entries.mkString("[", ",", "]")
}

case class JsonString (value: String) extends JsonValue {
  override lazy val toString: String = Json.quote(value)
}

case class JsonNumber (value: BigDecimal) extends JsonValue {
  override lazy val toString: String = value.toString
}

case class JsonBoolean (value: Boolean) extends JsonValue {
  override lazy val toString: String = value.toString
}

case object JsonNull extends JsonValue {
  override val toString: String = "null"
}

object JsonValue {
  def apply(value: JsonValue): JsonValue = value
}

