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
 *  Ninth is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ninthdrug.  If not, see <http://www.gnu.org/licenses/>.
 */
package ninthdrug.json

import org.scalatest.FunSuite
import ninthdrug.json.JsonEncoder._
import ninthdrug.json.JsonDecoder._

class JsonEncoderSuite extends FunSuite {
/*
  test("json.encode null") {
    assert(Json(null) === JsonNull)
  }
*/

  test("json.encode true") {
    assert(true.toJson === JsonBoolean(true))
  }

  test("json.encode false") {
    assert(false.toJson === JsonBoolean(false))
  }

  test("json.encode 0") {
    assert(0.toJson === JsonNumber(0))
  }

  test("json.encode -1") {
    assert((-1).toJson === JsonNumber(-1))
  }

  test("json.encode empty string") {
    assert("".toJson === JsonString(""))
  }

  test("""json.encode "hello" """) {
    assert("hello".toJson === JsonString("hello"))
  }

/*
  test("json.encode empty list") {
    assert(List().toJson === JsonArray())
  }
*/

  test("json.encode List(1)") {
    assert(List(1).toJson === JsonArray(1))
  }

  test("json.encode List(1,2)") {
    assert(List(1,2).toJson === JsonArray(1,2))
  }

/*
  test("json.encode [[]]") {
    parse("[[]]") === JsonArray(JsonArray())
  }
*/

  test("json.encode List(List(1,2),List(3,4))") {
    assert(List(List(1,2),List(3,4)).toJson === 
      JsonArray(JsonArray(1,2), JsonArray(3,4))
    )
  }

  test("""json.encode List(Map("a"->1,"b"->2))""") {
    assert(
      List(Map("a" -> 1, "b" -> 2)).toJson ===
      JsonArray(JsonObject("a" -> 1, "b" -> 2))
    )
  }

  test("json.encode Map[String,String]()") {
    val m = Map[String,String]()
    assert(m.toJson === JsonObject())
  }

  test("""json.encode Map("id"->1)""") {
    assert(Map("id" -> 1).toJson === JsonObject("id" -> 1))
  }

/*
  test("""json.encode Map("id" -> 1, "desc" -> "one")""") {
    assert(
      Map("id" -> 1, "desc" -> "one").toJson ===
      JsonObject("id" -> 1, "desc" -> "one")
    )
  }
*/
}
