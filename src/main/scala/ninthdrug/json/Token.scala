/*
 * Copyright 2012 Trung Dinh
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

case class Token( tokenType: Int, text: String) {
  override def toString() = "Token(" + Token.NAMES(tokenType) + "," + text + ")"
}

object Token {
  val EOF = -1.toChar.toInt
  val NULL = 0;
  val TRUE = 1;
  val FALSE = 2;
  val NUMBER = 3;
  val STRING = 4;
  val LCURL = 5;
  val RCURL = 6;
  val LBRACKET = 7;
  val RBRACKET = 8;
  val COLON = 9;
  val COMMA = 10;
  val NAMES = Map(
    EOF -> "EOF",
    NULL -> "null",
    TRUE -> "true",
    FALSE -> "false",
    NUMBER -> "NUMBER",
    STRING -> "STRING",
    LCURL -> "LCURL",
    RCURL -> "RCURL",
    LBRACKET -> "LBRACKET",
    RBRACKET -> "RBRACKET",
    COLON -> "COLON",
    COMMA -> "COMMA"
  )
}
