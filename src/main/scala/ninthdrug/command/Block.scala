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

class Block(val commands: Traversable[Command]) extends Command {
  def result(): Result = {
    Result(commands.map(_.result))
  }

  override def print(): Unit = {
    commands.foreach(_.print)
  }

  override def run(): Unit = {
    commands.foreach(_.run)
  }

  override def toString(): String = {
    commands.mkString("Block(", ",", ")")
  }
}

object Block {
  def apply(commands: Traversable[Command]) = new Block(commands)

  def apply(commands: Command*) = new Block(commands)
}
