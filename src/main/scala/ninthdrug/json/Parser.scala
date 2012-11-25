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

import java.io.Reader
import java.io.StringReader
import collection.immutable.ListMap
import collection.mutable.ListBuffer
import Token._
import ninthdrug.logging.Logger

/** A JSON parser.
 * 
 * @author Trung Dinh
 */
class Parser(reader: Reader) {
  val log = Logger("ninthdrug.json.Parser")
  val scanner = new Scanner(reader)
  var token: Token = nextToken

  /** Parse and returns a JsonValue.
   */ 
  def parse: JsonValue = {
    val value = token match {
      case Token(Token.EOF, _) => throw new JsonError("Unexpected end of file.")
      case Token(NULL, _) => JsonNull
      case Token(TRUE, _) => JsonBoolean(true)
      case Token(FALSE, _) => JsonBoolean(false)
      case Token(NUMBER, text) => JsonNumber(BigDecimal(text))
      case Token(STRING, text) => JsonString(text)
      case Token(LBRACKET, _) => parseArray
      case Token(LCURL, _) => parseObject
      case x => throw new JsonError("Unexpected " + x)
    }
    nextToken
    value
  }

  /** Parse and returns a JsonArray. */
  def parseArray: JsonArray = {
    log.debug("enter parseArray")
    log.debug("token: " + token)
    expect(LBRACKET)
    val buf = ListBuffer[JsonValue]()
    if (token.tokenType != Token.RBRACKET) {
      buf += parse
    }
    while (token.tokenType != Token.RBRACKET) {
      expect(COMMA)
      buf += parse
    }
    log.debug("token: " + token)
    log.debug("leave parseArray")
    JsonArray(buf.toSeq:_*)
  }

  /** Parse and returns a JsonObject. */
  def parseObject: JsonObject = {
    expect(LCURL)
    val buf = ListBuffer[(String,JsonValue)]()
    if (token.tokenType != Token.RCURL) {
      buf += pair
    }
    while (token.tokenType != Token.RCURL) {
      expect(COMMA)
      buf += pair
    }
    JsonObject(buf.toSeq:_*)
  }

  /** Parse and returns a String */
  private def string: String = {
    if (token.tokenType != STRING) {
      expected(STRING)
    }
    val str = token.text
    nextToken
    str
  }

  /** Parse and returns name value pair. */
  private def pair: (String, JsonValue) = {
    val name = string
    expect(COLON)
    val value = parse
    return (name, value)
  }

  private def nextToken: Token = {
    token = scanner.getToken
    token
  }

  /** If the current token if of the expected type, consume it.
   *  Otherwise throw an exception.
   */
  private def expect(tokenType: Int) {
    log.debug("expect " + Token.NAMES(tokenType))
    if (tokenType != token.tokenType) {
      expected(tokenType)
    }
    nextToken
  }

  /** Helper method to throw an exception. */
  private def expected(tokenType: Int) =
    throw new JsonError("Expected " + NAMES(tokenType) + " instead of " + token)
}

object Parser {
  def parse(string: String): JsonValue = {
    val parser = new Parser(new StringReader(string))
    parser.parse
  }
}
