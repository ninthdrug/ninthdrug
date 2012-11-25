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
import ninthdrug.command.Command._

/*
 * Command to kill process with specified id.
 */
case class Kill(pid: Int, delay: Int = 180) extends Command {
  def result(): Result = {

    exec("kill " + pid)
    for (i <- 0 to delay) {
      if (shout("ps -o pid= --pid " + pid) == "") return Result()
      Thread.sleep(1000L)
    }
    exec("kill -9 " + pid)
    for (i <- 0 to 30) {
      if (shout("ps -o pid= --pid " + pid) == "") return Result()
      Thread.sleep(1000L)
    }
    return Result(new Exception("Cannot kill " + pid))
  }

  override def toString(): String =
    "Kill(" + pid + ")"
}
