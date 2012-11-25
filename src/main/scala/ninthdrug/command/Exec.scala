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
package ninthdrug.command

import java.io.File

class Exec(val command: Array[String]) extends Command {
  def result(): Result = {
    if (command.length == 1) {
      Exec.exec(command(0))
    } else {
      Exec.exec(command)
    }
  }

  override def toString(): String =
    command.mkString("Exec(", " ", ")")
}

object Exec {
  def apply(command: String) = new Exec(Array(command))

  def apply(command: Array[String]) = new Exec(command)

  def exec(cmd: String) = Result(Runtime.getRuntime.exec(cmd))

  def exec(cmd: Array[String]) = Result(Runtime.getRuntime.exec(cmd))
}

