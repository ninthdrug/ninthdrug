/*
 * Copyright 2008-2011 Trung Dinh
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
package ninthdrug.util

import org.scalatest.FunSuite
import ninthdrug.util.Util._
import ninthdrug.util.Json._

class UtilSuite extends FunSuite {
  test("interpolate") {
    val map = Map(
      "domain.name" -> "poc_wls_dev"
    )
    assert(interpolate("domain.name=${domain.name}", map) == 
      "domain.name=poc_wls_dev"
    )
  }

  test("json") {
    val list = List(
      Name("John", "Smith"),
      Name("Bart", "Simson")
    )
    assert(list.json === """[{ "name": "John", "last": "Smith"},{ "name": "Bart", "last": "Simson"}]""")
    assert(List("ab", "cd").json === """["ab","cd"]""")
  }

  test("quote") {
    assert(Util.quote(null) === null)
    assert(Util.quote("") === "\"\"")
    assert(Util.quote("\"") === "\"\\\"\"")
    assert(Util.quote("abc") === "\"abc\"")
  }

  test("split") {
    assert(Util.split("", ';') === List())
    assert(Util.split(";", ';') === List("",""))
    assert(Util.split("a;b;c", ';') === List("a", "b", "c"))
    assert(Util.split("a;b;c;;;", ';') === List("a", "b", "c", "", "", ""))
    assert(Util.split("a;;b", ';') === List("a", "", "b"))
  }
}
