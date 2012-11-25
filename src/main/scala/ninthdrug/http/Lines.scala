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

import java.io._
import java.net.Socket

object Lines {
  def apply(string: String) = new Iterator[String] {
    private val strings = string.split("(\r)?\n")
    private var i = 0

    override def hasNext: Boolean = i < strings.length

    override def next: String = {
      val result = strings(i)
      i = i + 1
      result
    }
  }

  def apply(file: File) = new Iterator[String] {
    val reader = new BufferedReader(new FileReader(file))
    var line = reader.readLine()

    override def hasNext: Boolean = (line != null)

    override def next: String = {
      val result = line
      line = reader.readLine()
      result
    }
  }

  def apply(socket: Socket) = new Iterator[String] {
    val reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    var line = reader.readLine()

    override def hasNext: Boolean = (line != null)

    override def next: String = {
      val result = line
      line = reader.readLine()
      result
    }
  }
}
