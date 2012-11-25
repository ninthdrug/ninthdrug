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

import collection.immutable.ListMap
import org.scalatest.FunSuite
import Parser.parse
import Json.quote
import ninthdrug.json.JsonEncoder._

class JsonParserSuite extends FunSuite {
  test("json.Parser null") {
    parse("null") === JsonNull
  }

  test("json.Parser true") {
    assert(parse("true") === JsonBoolean(true))
  }

  test("json.Parser false") {
    assert(parse("false") === JsonBoolean(false))
  }

  test("json.Parser 0") {
    assert(parse("0") === JsonNumber(0))
  }

  test("json.Parser -1") {
    assert(parse("-1") === JsonNumber(-1))
  }

  test("json.Parser empty string") {
    assert(parse("\"\"") === JsonString(""))
  }

  test("json.Parser \"hello\"") {
    assert(parse(quote("hello")) === JsonString("hello"))
  }

  test("json.Parser []") {
    assert(parse("[]") === JsonArray())
  }

  test("json.Parser [1]") {
    assert(parse("[1]") === JsonArray(1))
  }

  test("json.Parser [1,2]") {
    assert(parse("[1,2]") === JsonArray(1,2))
  }

  test("json.Parser [[]]") {
    assert(parse("[[]]") === JsonArray(JsonArray()))
  }

  test("json.Parser [[1,2],[3,4]]") {
    assert(parse("[[1,2],[3,4]]") === JsonArray(JsonArray(1,2), JsonArray(3,4)))
  }

  test("""json.Parser [{"a":1},{"b":2}]""") {
    assert(
      parse("""[{"a":1},{"b":2}]""") === JsonArray(
        JsonObject("a" -> 1),
        JsonObject("b" -> 2)
      )
    )
  }

  test("json.Parser {}") {
    assert(parse("{}") === JsonObject())
  }

  test("""json.Parser {"id":1}""") {
    assert(parse("""{"id":1}""") === JsonObject("id" -> 1))
  }

  test("""json.Parser {"id":1,"desc":"one"}""") {
    val json = parse("""{"id":1,"desc":"one"}""")
    assert(json === JsonObject("id" -> 1, "desc" -> "one"))
  }
}
