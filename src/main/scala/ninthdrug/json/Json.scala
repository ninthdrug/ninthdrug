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

class Json[T](t: T) {
  def toJson(implicit encoder: JsonEncoder[T]): JsonValue = encoder.encode(t)
}

object Json {

  def apply(value: Boolean) = new JsonBoolean(value)
  def apply(value: Short) = new JsonNumber(value)
  def apply(value: Int) = new JsonNumber(value)
  def apply(value: Long) = new JsonNumber(value)
  def apply(value: Float) = new JsonNumber(value)
  def apply(value: Double) = new JsonNumber(value)
  def apply(value: BigDecimal) = new JsonNumber(value)
  def apply(value: BigInt) = new JsonNumber(BigDecimal(value))
  def apply(value: Char) = new JsonString(value.toString)
  def apply(value: String) = new JsonString(value)

  /** Enclose the argument string in quotes (") and escape the characters:
   *  ", \, \b, \f, \n, \r, \t.
   */
  def quote(string: String): String = {
    if (string == null) {
      "null"
    } else {
      val buf = new StringBuilder()
      buf.append('\"')
      for (c <- string) {
        c match {
          case '"'  => buf.append("\\\"")
          case '\\' => buf.append("\\\\")
          case '\b' => buf.append("\\b")
          case '\f' => buf.append("\\f")
          case '\n' => buf.append("\\n")
          case '\r' => buf.append("\\r")
          case '\t' => buf.append("\\t")
          case x => buf += x
        }
      }
      buf.append('\"')
      buf.toString
    }
  }

  val HEXDIGITS = "0123456789abcdef"

  def unquote(string: String): String = {
    if (string == "null") {
      null
    } else {
      val buf = new StringBuilder()
      var i = 0
      while (i < string.length) {
        val c = string.charAt(i)
        if (c == '\\') {
          if (i + 1 >= string.length) {
            throw new JsonError("Invalid escape character")
          }
          string.charAt(i+1) match {
            case '"'  => buf.append('"')
            case '\\' => buf.append('\\')
            case '/'  => buf.append('/')
            case 'b'  => buf.append('\b')
            case 'f'  => buf.append('\f')
            case 'n'  => buf.append('\n')
            case 'r'  => buf.append('\r')
            case 't'  => buf.append('\t')
            case 'u'  => {
              val code = getUnicodeEscape(string, i+2)
              if (code < 0) {
                throw new JsonError("Invalid unicode escape")
              }
              buf.append(code.toChar)
              i = i + 4
            }
            case _ => throw new JsonError("Invalid escape character")
          }
          i = i + 1
        } else {
          buf.append(c)
        }
        i = i + 1
      }
      buf.toString
    }
  }

  def getUnicodeEscape(str: String, index: Int): Int = {
    if (index + 4 >= str.length) {
      -1
    } else {
      val d1 = HEXDIGITS.indexOf(Character.toLowerCase(str.charAt(index)))
      val d2 = HEXDIGITS.indexOf(Character.toLowerCase(str.charAt(index + 1)))
      val d3 = HEXDIGITS.indexOf(Character.toLowerCase(str.charAt(index + 2)))
      val d4 = HEXDIGITS.indexOf(Character.toLowerCase(str.charAt(index + 3)))
      if (d1 < 0 || d2 < 0 || d3 < 0 || d4 < 0) {
        -1
      } else {
        ((d1 * 16 + d2) * 16 + d3) * 16 + d4
      }
    }
  }
}
