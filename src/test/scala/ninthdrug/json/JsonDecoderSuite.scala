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

class JsonDecoderSuite extends FunSuite {
  test("json.decode JsonNull") {
    assert(JsonNull.convertTo[String] === null)
  }

  test("json.decode true") {
    assert(JsonBoolean(true).convertTo[Boolean] === true)
  }

  test("json.decode false") {
    assert(JsonBoolean(false).convertTo[Boolean] === false)
  }

  test("json.decode Int") {
    assert(JsonNumber(0).convertTo[Int] === 0.toInt)
  }

  test("json.Parser -1") {
    assert(JsonNumber(-1).convertTo[Int] === -1.toInt)
  }

  test("json.decode empty string") {
    assert(JsonString("").convertTo[String] === "")
  }

  test("""json.decode "hello" """) {
    assert("hello" === JsonString("hello").convertTo[String])
  }

  test("json.decode empty JsonArray") {
    assert(List() === JsonArray().convertTo[List[Int]])
  }

  test("json.decode List(1)") {
    assert(List(1) === JsonArray(1).convertTo[List[Int]])
  }

  test("json.decode List(1,2)") {
    assert(List(1,2) === JsonArray(1,2).convertTo[List[Int]])
  }

/*
  test("json.decode [[]]") {
    parse(") === JsonArray(JsonArray())
  }
*/

  test("json.decode List(List(1,2),List(3,4))") {
    assert(List(List(1,2),List(3,4)) === 
      JsonArray(JsonArray(1,2), JsonArray(3,4)).convertTo[List[List[Int]]]
    )
  }

  test("""json.decode List(Map("a"->1,"b"->2))""") {
    assert(
      List(Map("a" -> 1, "b" -> 2)) ===
      JsonArray(JsonObject("a" -> 1, "b" -> 2)).convertTo[List[Map[String,Int]]]
    )
  }

/*
  test("json.decode Map[String,JsonValue]()") {
    val m = Map[String,JsonValue]()
    assert(m === JsonObject().convertTo[Map[String,JsonValue]])
  }
*/

  test("""json.decode Map("id"->1)""") {
    assert(Map("id" -> 1) === JsonObject("id" -> 1).convertTo[Map[String,Int]])
  }

/*
  test("""json.decode Map("id" -> 1, "desc" -> "one")""") {
    assert(
      Map("id" -> 1, "desc" -> "one") ===
      JsonObject("id" -> 1, "desc" -> "one").convertTo[Map[String,Any]]
    )
  }
*/
  test("""json.decode Name """) {
    assert(
      Name("Benjamin", "Dinh") ===
      JsonObject("first" -> "Benjamin", "last" -> "Dinh").convertTo[Name]
    )
  }

  test("""json.decode Item """) {
    assert(
      Item("xyz", "Widget", 1) ===
      JsonObject("id" -> "xyz", "desc" -> "Widget", "qty" -> 1).convertTo[Item]
    )
  }

  test("""json.decode Order """) {
    assert(
      Order("1001", List(Item("101", "Yoyo", 42))) ===
      JsonObject(
        "id" -> "1001",
        "items" -> JsonArray(
          JsonObject("id" -> "101", "desc" -> "Yoyo", "qty" -> 42)
        )
      ).convertTo[Order]
    )
  }
}
