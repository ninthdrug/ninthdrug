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

object Base64 {
  private val CHARSET =
  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

  private val ENCODE: Array[Byte] = {
    val encodeData = new Array[Byte](64)
    for (i <- 0 until 64) {
      encodeData(i) = CHARSET.charAt(i).asInstanceOf[Byte]
    }
    encodeData
  }

  private val DECODE: Array[Int] = {
    val decodeData = new Array[Int](256)
    for (i <- 0 until 256) {
      decodeData(i) = -1
    }
    for (i <- 0 until 64) {
      decodeData(ENCODE(i)) = i
    }
    decodeData('=') = 0
    decodeData
  }

  def encode(s: String): String = encode(s.getBytes())

  def encode(bytes: Array[Byte]): String = {
    val srcLen = bytes.length
    val dstLen = (srcLen + 2) / 3 * 4
    val dst = new Array[Byte](dstLen)
    var dstIndex = 0
    var srcIndex = 0
    val eLen = (srcLen / 3) * 3
    while (srcIndex < eLen) {
      val x = (bytes(srcIndex) & 0xff) << 16 |
              (bytes(srcIndex + 1) & 0xff) << 8 |
              (bytes(srcIndex + 2) & 0xff)
      srcIndex = srcIndex + 3
      dst(dstIndex) = ENCODE((x >>> 18) & 0x3f)
      dstIndex = dstIndex + 1
      dst(dstIndex) = ENCODE((x >>> 12) & 0x3f)
      dstIndex = dstIndex + 1
      dst(dstIndex) = ENCODE((x >>> 6) & 0x3f)
      dstIndex = dstIndex + 1
      dst(dstIndex) = ENCODE(x & 0x3f)
      dstIndex = dstIndex + 1
    }

    val leftOver = srcLen - eLen

    if (leftOver > 0) {
      var x = (bytes(eLen) & 0xff) << 10
      if (leftOver == 2) {
        x = x | ((bytes(srcLen - 1) & 0xff) << 2)
      }
      dst(dstIndex) = ENCODE(x >> 12)
      dstIndex = dstIndex + 1
      dst(dstIndex) = ENCODE((x >>> 6) & 0x3f)
      dstIndex = dstIndex + 1
      if (leftOver == 2) {
        dst(dstIndex) = ENCODE(x & 0x3f)
      } else {
        dst(dstIndex) = '='
      }
      dstIndex = dstIndex + 1
      dst(dstIndex) = '='
    }
    return new String(dst)
  }

  def decode(s: String): Array[Byte] = {
    var pad = 0;
    if (s.endsWith("=")) pad = pad + 1
    if (s.endsWith("==")) pad = pad + 1

    val len = (s.length + 3) / 4 * 3 - pad
    val result = new Array[Byte](len)
    val elen = (len / 3) * 3
    var dstIndex = 0
    var srcIndex = 0
    while (dstIndex < elen) {
      val code = DECODE(s.charAt(srcIndex)) << 18 |
              DECODE(s.charAt(srcIndex + 1)) << 12 |
              DECODE(s.charAt(srcIndex + 2)) << 6 |
              DECODE(s.charAt(srcIndex + 3))
      srcIndex = srcIndex + 4

      result(dstIndex) = (code >> 16).asInstanceOf[Byte]
      dstIndex = dstIndex + 1
      result(dstIndex) = (code >> 8).asInstanceOf[Byte]
      dstIndex = dstIndex + 1
      result(dstIndex) = (code).asInstanceOf[Byte]
      dstIndex = dstIndex + 1
    }

    if (dstIndex < len) {
      var code = 0
      var i = 0
      while (srcIndex < s.length) {
        code = code | DECODE(s.charAt(srcIndex)) << (18 - i * 6)
        i = i + 1
        srcIndex = srcIndex + 1
      }
      var r = 16
      while (dstIndex < len) {
        result(dstIndex) = (code >> r).asInstanceOf[Byte]
        dstIndex = dstIndex + 1
        r = r - 8
      }
    }
    return result
  }
}
