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

import scala.collection.mutable.ListBuffer

class Chain(val commands: Traversable[Command]) extends Command {
  def result(): Result = {
    val results = ListBuffer[Result]()
    for (command <- commands) {
      val res = command.result
      results += res
      if (!res) {
        return Result(results)
      }
    }
    Result(results)
  }

  override def print(): Unit = {
    for (command <- commands) {
      val res = command.result
      res.print
      if (!res) {
        return
      }
    }
  }

  override def run(): Unit = {
    for (command <- commands) {
      val res = command.result
      if (!res) {
        return
      }
    }
  }

  override def toString(): String = {
    commands.mkString("Chain(", ",", ")")
  }
}

object Chain {
  def apply(commands: Traversable[Command]) = new Chain(commands)

  def apply(commands: Command*) = new Chain(commands)
}
