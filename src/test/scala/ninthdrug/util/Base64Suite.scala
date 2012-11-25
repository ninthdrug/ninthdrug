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
package ninthdrug.util

import org.scalatest.FunSuite
import scala.util.Random
import Base64._

class Base64Suite extends FunSuite {
  test("Base64 empty byte array") {
    assert(encode(new Array[Byte](0)) === "")
    assert(decode("").size === 0)
  }

  test("Base64 on random data") {
    for (i <- 0 to 1000) {
      val a = new Array[Byte](Random.nextInt(1000) + 1)
      Random.nextBytes(a)
      val b = decode(encode(a))
      for (n <- 0 until a.size) {
        assert(a(n) == b(n))
      }
    }
  }
}

