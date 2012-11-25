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

import ninthdrug.config.EncryptionKey
import org.scalatest.FunSuite
import scala.util.Random

class CryptoSuite extends FunSuite {
  private val key = EncryptionKey.key
  private val random = new Random()

  private def equals(a : Array[Byte], b : Array[Byte]) : Boolean = {
    if (a.length != b.length) return false
    for (i <- 0 until a.length) {
      if (a(i) != b(i)) return false
    }
    return true
  }

  private def checkEncryptDecrypt(text : String) {
    val bytes = text.getBytes
    val encrypted = Crypto.encrypt(bytes, key)
    val decrypted = Crypto.decrypt(encrypted, key)
    assert(equals(bytes, decrypted))
  }

  test("encrypt/decrypt") {
    checkEncryptDecrypt("")
    checkEncryptDecrypt("abc")
   
    for (n <- 0 until 500) {
      val text = random.nextString(random.nextInt(100))
      checkEncryptDecrypt(text)
    }
  }

  test("hex conversion") {
    for (n <- 0 until 500) {
      val text = random.nextString(random.nextInt(100)).getBytes
      assert(equals(text, Crypto.decodeHex(Crypto.toHex(text))))
    }
    assert("00010203040506" == Crypto.toHex(Array[Byte](0,1,2,3,4,5,6)))
    assert("0708090a0b0c0d" == Crypto.toHex(Array[Byte](7,8,9,10,11,12,13)))
  }

  test("base64 conversion") {
    for (n <- 0 until 500) {
      val bytes = random.nextString(random.nextInt(100)).getBytes
      val base64 = Base64.encode(bytes)
      val decoded = Base64.decode(base64)
      assert(equals(bytes, decoded))
    }
  }
}
