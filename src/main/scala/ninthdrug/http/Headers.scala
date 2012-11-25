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
package ninthdrug.http

import java.io.BufferedReader

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

class Headers {
  val keys = new ListBuffer[String]()
  val values = new ListBuffer[String]()

  def add(key: String, value: String) {
    keys += key
    values += value
  }

  def size = keys.size

  def contains(key: String): Boolean = keys.contains(key)

  def getFirst(key: String): String = {
    for (i <- 0 until keys.size) {
      if (keys(i) == key) return values(i)
    }
    return null
  }

  def getAll(key: String): List[String] = {
    val array = new ArrayBuffer[String]()
    for (i <- 0 until keys.size) {
      if (keys(i) == key) {
        array.append(values(i))
      }
    }
    return List[String]() ++ array
  }

  def get(key: String): String = getFirst(key)

  def apply(key: String): String = getAll(key).mkString(",")

  def apply(index: Int): String = keys(index) + ": " + values(index)
}

object Headers {
  def apply(): Headers = {
    new Headers()
  }

  def apply(reader: BufferedReader): Headers = {
    val headers = new Headers()
    var line = reader.readLine()
    while (line != null && line.length != 0) {
      val i = line.indexOf(':')
      if (i > 0) {
        val key = line.substring(0, i).trim.toLowerCase
        val value = line.substring(i + 1).trim
        headers.add(key, value)
      }
      line = reader.readLine()
    }
    headers
  }
}
