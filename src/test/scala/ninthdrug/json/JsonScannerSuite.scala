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
import Token._

class JsonScannerSuite extends FunSuite {
  test("json.Scanner empty string") {
    val s = Scanner("")
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner whitespace") {
    val s = Scanner(" \t\r\n")
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner null") {
    val s = Scanner("null")
    assert(s.getToken === Token(NULL, "null"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner true") {
    val s = Scanner("true")
    assert(s.getToken === Token(TRUE, "true"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner false") {
    val s = Scanner("false")
    assert(s.getToken === Token(FALSE, "false"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner {}") {
    val s = Scanner("{}")
    assert(s.getToken === Token(LCURL, "{"))
    assert(s.getToken === Token(RCURL, "}"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner []") {
    val s = Scanner("[]")
    assert(s.getToken === Token(LBRACKET, "["))
    assert(s.getToken === Token(RBRACKET, "]"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 0") {
    val s = Scanner("0")
    assert(s.getToken === Token(NUMBER, "0"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 0.0") {
    val s = Scanner("0.0")
    assert(s.getToken === Token(NUMBER, "0.0"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner -0") {
    val s = Scanner("-0")
    assert(s.getToken === Token(NUMBER, "-0"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner -.") {
    val s = Scanner("-.")
    intercept[JsonError] {
      s.getToken
    }
  }

  test("json.Scanner 1.") {
    val s = Scanner("1.")
    intercept[JsonError] {
      s.getToken
    }
  }

  test("json.Scanner 1.0") {
    val s = Scanner("1.0")
    assert(s.getToken === Token(NUMBER, "1.0"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1e0") {
    val s = Scanner("1e0")
    assert(s.getToken === Token(NUMBER, "1e0"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1e1") {
    val s = Scanner("1e1")
    assert(s.getToken === Token(NUMBER, "1e1"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1e+10") {
    val s = Scanner("1e+10")
    assert(s.getToken === Token(NUMBER, "1e+10"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1e-1") {
    val s = Scanner("1e-1")
    assert(s.getToken === Token(NUMBER, "1e-1"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1.0e-3") {
    val s = Scanner("1.0e-3")
    assert(s.getToken === Token(NUMBER, "1.0e-3"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1E0") {
    val s = Scanner("1E0")
    assert(s.getToken === Token(NUMBER, "1E0"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1E1") {
    val s = Scanner("1E1")
    assert(s.getToken === Token(NUMBER, "1E1"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1E+10") {
    val s = Scanner("1E+10")
    assert(s.getToken === Token(NUMBER, "1E+10"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1E-1") {
    val s = Scanner("1E-1")
    assert(s.getToken === Token(NUMBER, "1E-1"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner 1.0E-3") {
    val s = Scanner("1.0E-3")
    assert(s.getToken === Token(NUMBER, "1.0E-3"))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner "" """) {
    val s = Scanner(""" "" """)
    assert(s.getToken === Token(STRING, ""))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " " """) {
    val s = Scanner(""" " " """)
    assert(s.getToken === Token(STRING, " "))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " \" " """) {
    val s = Scanner(""" " \" " """)
    assert(s.getToken === Token(STRING, """ " """))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " \\ " """) {
    val s = Scanner(""" " \\ " """)
    assert(s.getToken === Token(STRING, """ \ """))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " \b " """) {
    val s = Scanner(""" " \b " """)
    assert(s.getToken === Token(STRING, " \b "))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " \f " """) {
    val s = Scanner(""" " \f " """)
    assert(s.getToken === Token(STRING, " \f "))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " \n " """) {
    val s = Scanner(""" " \n " """)
    assert(s.getToken === Token(STRING, " \n "))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " \r " """) {
    val s = Scanner(""" " \r " """)
    assert(s.getToken === Token(STRING, " \r "))
    assert(s.getToken.tokenType === EOF)
  }

  test("""json.Scanner " \t " """) {
    val s = Scanner(""" " \t " """)
    assert(s.getToken === Token(STRING, " \t "))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner \"\\u0061\" ") {
    val str = " \"\\u0061\" "
    val s = Scanner(str)
    assert(s.getToken === Token(STRING, "a"))
    assert(s.getToken.tokenType === EOF)
  }

  test("json.Scanner \"\\uxxxx\" ") {
    val hexdigits = "0123456789abcdef"
    for (h1 <- hexdigits; h2 <- hexdigits; h3 <- hexdigits; h4 <- hexdigits) {
      val xxxx = "" + h1 + h2 + h3 + h4
      val u = " \"\\u" + xxxx + "\" "
      val U = " \"\\u" + xxxx.toUpperCase + "\" "
      val string = Integer.parseInt(xxxx, 16).toChar.toString
      val s = Scanner(u)
      val S = Scanner(U)
      assert(s.getToken === Token(STRING, string))
      assert(S.getToken === Token(STRING, string))
    }
  }
}
