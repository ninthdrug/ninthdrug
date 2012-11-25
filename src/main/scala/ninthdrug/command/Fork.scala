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

class Fork(val commands: Traversable[Command]) extends Command {
  import java.util.concurrent.Callable

  def result(): Result = {
    val futures = commands map {
      cmd => Fork.executor.submit(
        new Callable[Result] { def call(): Result = cmd.result() }
      )
    }
    val results = futures map {_.get()}
    Result(results)
  }

  override def run(): Unit = {
    val futures = commands map {
      cmd => Fork.executor.submit(
        new Runnable { def run(): Unit = cmd.run() }
      )
    }
  }

  override def toString(): String = {
    commands.mkString("Fork(", ",", ")")
  }
}

object Fork {
  import java.util.concurrent.Executors

  val executor = Executors.newFixedThreadPool(10)

  def apply(commands: Traversable[Command]) = new Fork(commands)

  def apply(commands: Command*) = new Fork(commands)
}
