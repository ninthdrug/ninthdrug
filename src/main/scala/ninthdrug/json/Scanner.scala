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
 *  but WITHOUT ANY WARRANTY without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ninthdrug.  If not, see <http://www.gnu.org/licenses/>.
 */
package ninthdrug.json

import java.io._
import java.util._
import Token._

class Scanner(reader: Reader) {
  private var c = ' '

  def nextChar() {
    c = reader.read.toChar
  }

  def getToken(): Token = {
    while (c.isWhitespace) {
      nextChar()
    }

    if (c.toChar.isLetter) {
      identifier()
    } else if (c.toChar.isDigit || c == '-') {
      number()
    } else if (c == '"') {
      quote()
    } else if (c == '{') {
      nextChar()
      makeToken(LCURL, "{")
    } else if (c == '}') {
      nextChar()
      makeToken(RCURL, "}")
    } else if (c == '[') {
      nextChar()
      makeToken(LBRACKET, "[")
    } else if (c == ']') {
      nextChar()
      makeToken(RBRACKET, "]")
    } else if (c == ',') {
      nextChar()
      makeToken(COMMA, ",")
    } else if (c == ':') {
      nextChar()
      makeToken(COLON, ":")
    } else if (c == EOF) {
      makeToken(EOF, "")
    } else {
      throw new JsonError("Invalid character: " + c)
    }
  }

  def expect(x: Char) {
    if (x != c) {
      throw new JsonError("Expected '" + x + "'")
    }
    nextChar()
  }

  def identifier(): Token = {
    val buf = new StringBuilder()
    while (c.isLetter) {
      buf.append(c)
      nextChar()
    }
    buf.toString match {
      case "null" => makeToken(NULL, "null")
      case "true" => makeToken(TRUE, "true")
      case "false" => makeToken(FALSE, "false")
      case t => throw new JsonError("Unknown token: " + t)
    }
  }

  def number(): Token = {
    val buf = new StringBuilder()
    if (c == '-') {
      buf.append(c.toChar)
      nextChar()
      if (!c.toChar.isDigit) {
        throw new JsonError("Bad number")
      }
    }
    while (c.toChar.isDigit) {
      buf.append(c.toChar)
      nextChar()
    }
    if (c == '.') {
      buf.append(c)
      nextChar()
      if (!c.toChar.isDigit) {
        throw new JsonError("Expect digit after decimal point")
      }
      while (c.toChar.isDigit) {
        buf.append(c)
        nextChar()
      }
    }
    if (c == 'E' || c == 'e') {
      buf.append(c)
      nextChar()
      if (c == '+' || c == '-') {
        buf.append(c)
        nextChar()
      }
      if (!c.isDigit) {
        throw new JsonError("Bad number: expected digit after exponent")
      }
      while (c.isDigit) {
        buf.append(c)
        nextChar()
      }
    }
    makeToken(NUMBER, buf.toString)
  }

  def escape(b: StringBuilder) {
    nextChar()
    if (c == '"') {
      b.append('\"')
    } else if (c == '\\') {
      b.append('\\')
    } else if (c == '/') {
      b.append('/')
    } else if (c == 'b') {
      b.append('\b')
    } else if (c == 'f') {
      b.append('\f')
    } else if (c == 'n') {
      b.append('\n')
    } else if (c == 'r') {
      b.append('\r')
    } else if (c == 't') {
      b.append('\t')
    } else if (c == 'u') {
      var code = 0
      for (i <- 0 to 3) {
        nextChar()
        val n = Json.HEXDIGITS.indexOf(Character.toLowerCase(c))
        if (n < 0) {
          throw new JsonError("Invalid unicode escape")
        }
        code = 16 * code + n
      }
      b.append(code.toChar)
    } else {
      b.append(c)
    }
  }

  def quote(): Token = {
    expect('"')
    val buf = new StringBuilder()
    while (c != '"' && c != EOF) {
      if (c == '\\') {
        escape(buf)
      } else {
        buf.append(c)
      }
      nextChar()
    }
    expect('"')
    makeToken(STRING, buf.toString)
  }

  def makeToken(tokenType: Int, text: String): Token = {
    Token(tokenType, text)
  }
}

object Scanner {
  def apply(file: File): Scanner = {
    new Scanner(new FileReader(file))
  }

  def apply(stream: InputStream): Scanner = {
    new Scanner(new InputStreamReader(stream))
  }

  def apply(string: String): Scanner = {
    new Scanner(new StringReader(string))
  }
}
