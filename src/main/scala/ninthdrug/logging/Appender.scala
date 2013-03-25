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
package ninthdrug.logging

import java.io.FileWriter
import ninthdrug.config.Config

abstract class Appender {
  def append(msg: String): Unit
}

class FileAppender(val filename: String) extends Appender {
  private val writer = new FileWriter(filename, true)

  def append(msg: String) {
    writer.write(msg)
    writer.flush
  }

  def appendln(msg: String) {
    append(msg + "\n")
  }

  def flush() = writer.flush
}

object ConsoleAppender extends Appender {
  def append(msg: String) {
    print(msg)
  }
}
