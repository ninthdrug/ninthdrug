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
 *  Nithdrug is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ninthdrug.  If not, see <http://www.gnu.org/licenses/>.
 */
package ninthdrug.util

import java.util._
import java.security._
import java.security.interfaces._
import java.security.spec._
import javax.crypto._
import javax.crypto.interfaces._
import javax.crypto.spec._
import scala.util.Random
import ninthdrug.logging.Logger

object Crypto {
  private val log = Logger("ninthdrug.util.Crypto")
  /**
   * Encrypt a cookie.
   */
  def encryptCookie(
    serverKey: Array[Byte],
    userName: String,
    cookieValue: String,
    cookieTime: Int
  ): String = {
    val expiration = if (cookieTime > 0) {
      System.currentTimeMillis() + 1000L * cookieTime
    } else {
      -System.currentTimeMillis()
    }
    val buf = new StringBuffer()
    buf.append(userName).append(":").append(expiration)
    val userNameExpiration = buf.toString()
    val key = hmac(userNameExpiration, serverKey)
    val encrypted = encrypt(cookieValue.getBytes(), key)
    val cookieVal = Base64.encode(encrypted)
    buf.append(":").append(cookieVal)
    val block = buf.toString()
    val hash = toHex(hmac(block, key))
    buf.append(":").append(hash)
    return buf.toString
  }

  /**
   * Decrypt a cookie.
   */
  def decryptCookie(serverKey: Array[Byte], cookie: String): String =
  {
    if (cookie == null) {
      return null
    }

    val tokens = cookie.split(":", 4)
    if (tokens.length != 4) {
      return null
    }

    val userName = tokens(0)
    val expiration = tokens(1)
    val data = tokens(2)
    val hmacString = tokens(3)

    val expirationTime = expiration.toLong
    if (expirationTime > 0 && System.currentTimeMillis() > expirationTime) {
      return null
    }
    val key = hmac(userName + ":" + expiration, serverKey)
    val block = userName + ":" + expiration + ":" + data
    val hex = toHex(hmac(block, key))
    if (!(hmacString.equals(hex))) {
      log.debug("hash does not match")
      return null
    }
    val decoded = Base64.decode(data)
    val decrypted = decrypt(decoded, key)
    return new String(decrypted)
  }

  def hmac(message: String, key: String, alg: String): Array[Byte] = {
    return hmac(message, key.getBytes(), alg)
  }

  def hmac(message: String, key: String): Array[Byte] = {
    return hmac(message, key.getBytes(), null)
  }

  def hmac(message: String, key: Array[Byte]): Array[Byte] = {
    return hmac(message, key, null)
  }

  def hmac(message: String, key: Array[Byte], algor: String): Array[Byte] = {
    if (message == null) {
      throw new NullPointerException()
    }
    if (key == null) {
      throw new NullPointerException()
    }
    val alg = if (algor == null) "HMacMD5" else algor

    var result: Array[Byte] = null
    try {
      val h = Mac.getInstance(alg)
      val k = new SecretKeySpec(key, alg)
      h.init(k)
      result = h.doFinal(message.getBytes())
    } catch {
      case e: Exception => {
        e.printStackTrace()
        throw new RuntimeException(e.getMessage(), e)
      }
    }
    return result
  }

  private val ivBytes: Array[Byte] = Array(
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f)

  /**
   * Encrypt a byte array using AES.
   */
  def encrypt(data: Array[Byte], key: Array[Byte]): Array[Byte] = {
    var output: Array[Byte] = null
    try {
      val sk = makeAESKey(key)
      val iv = new IvParameterSpec(ivBytes)
      val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
      cipher.init(Cipher.ENCRYPT_MODE, sk, iv)
      output = cipher.doFinal(data)
    } catch {
      case e: Exception => {
        e.printStackTrace()
        throw new RuntimeException("Encryption error", e)
      }
    }
    return output
  }

  /**
   * Encrypt a String using AES.
   */
  def encrypt(data: String, key: Array[Byte]): Array[Byte] =
    encrypt(data.getBytes(), key)

  def decrypt(input: Array[Byte], key: Array[Byte]): Array[Byte] = {
    var output: Array[Byte] = null
    try {
      val sk = makeAESKey(key)
      val iv = new IvParameterSpec(ivBytes)
      val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
      cipher.init(Cipher.DECRYPT_MODE, sk, iv)
      output = cipher.doFinal(input)
    } catch {
      case e: Exception => {
        e.printStackTrace()
        throw new RuntimeException("Encryption error", e)
      }
    }
    return output
  }

  def makeAESKey(key: Array[Byte]): SecretKeySpec = {
    var sk: SecretKeySpec = null
    val keylen = key.length
    if (keylen == 16) {
      sk = new SecretKeySpec(key, "AES")
    } else if (keylen > 16) {
      sk = new SecretKeySpec(key, 0, 16, "AES")
    } else {
      val paddedKey = new Array[Byte](16)
      Arrays.fill(paddedKey, 0.asInstanceOf[Byte])
      for (i <- 0 until keylen) {
        paddedKey(i) = key(i)
      }
      sk = new SecretKeySpec(paddedKey, "AES")
    }
    return sk
  }

  private val HEX_DIGITS = "0123456789abcdef"

  /**
   * Convert a byte array to hex string.
   */
  def toHex(a: Array[Byte]): String = {
    return toHex(a, a.length)
  }

  /**
   * Convert a byte array to hex string.
   * The second argument specifies how many bytes to encode.
   */
  def toHex(a: Array[Byte], len: Int): String = {
    val buf = new StringBuffer(len * 2)
    for (i <- 0 until len) {
      val b = a(i) & 0xff
      buf.append(HEX_DIGITS.charAt(b >> 4))
      buf.append(HEX_DIGITS.charAt(b & 0xf))
    }
    return buf.toString
  }

  /**
   * Convert a hex string to byte array.
   */
  def decodeHex(str: String): Array[Byte] = {
    val len = str.length
    val half = len / 2
    val a = new Array[Byte](half)
    var i = 0
    var n = 0
    while (i < half) {
      val hi = decodeHex(str.charAt(n))
      val lo = decodeHex(str.charAt(n + 1))
      a(i) = ((hi << 4) | lo).asInstanceOf[Byte]
      i = i + 1
      n = n + 2
    }
    return a
  }

  private def decodeHex(c: Char): Int = {
    if (c >= '0' && c <= '9') {
      return c - '0'
    } else if (c >= 'a' && c <= 'f') {
      return c - 'a' + 10
    } else if (c >= 'A' && c <= 'F') {
      return c - 'A' + 10
    }
    throw new IllegalArgumentException("Invalid hex digit: " + c)
  }

  private val BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

  /**
   * Convert a byte array to base32 string.
   */
  def toBase32(a: Array[Byte]): String = {
    return toBase32(a, a.length)
  }

  /**
   * Convert a byte array to base32 string.
   * The second argument specifies how many bytes to convert.
   */
  def toBase32(a: Array[Byte], len: Int): String = {
    var n = 0
    var size = 0
    val buf = new StringBuffer(len * 8 / 5 + 1)
    for (i <- 0 until len) {
      n = n << 8
      n = n | (a(i) & 0xff)
      size = size + 8
      while (size >= 5) {
        size -= 5
        buf.append(BASE32_CHARS.charAt((n >>> size) & 0x1f))
      }
    }
    if (size != 0) {
      n = n << (5 - size)
      buf.append(BASE32_CHARS.charAt(n & 0x1f))
    }
    return buf.toString()
  }

  /**
   * Convert a base32 string to byte array.
   */
  def decodeBase32(str: String): Array[Byte] = {
    val len = str.length
    val result = new Array[Byte](len * 5 / 8)
    var n = 0
    var size = 0
    var k = 0
    for (i <- 0 until len) {
      val c = str.charAt(i)
      n = n << 5
      n = n | decodeBase32(c)
      size = size + 5
      if (size >= 8) {
        size = size - 8
        result(k) = (n >>> size).asInstanceOf[Byte]
        k = k + 1
      }
    }
    return result
  }

  /**
   * Generate a 32 bytes key.
   */
  def generateKey(): Array[Byte] = {
    val key = new Array[Byte](32)
    Random.nextBytes(key)
    key
  }

  private def decodeBase32(c: Char): Int = {
    if (c >= 'a' && c <= 'z') {
      return c - 'a'
    } else if (c >= 'A' && c <= 'Z') {
      return c - 'A'
    } else if (c >= '2' && c <= '7') {
      return c - '2'
    }
    throw new IllegalArgumentException("Invalid base32 char: " + c)
  }
}
