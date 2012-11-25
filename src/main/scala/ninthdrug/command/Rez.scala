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

import java.io._

class Rez(val path: String) extends Command {
  def result(): Result = {
    var in: ObjectInputStream = null
    val file = new File(path)
    try {
      in = new ObjectInputStream(new FileInputStream(file))
      val cmd = in.readObject().asInstanceOf[Command]
      cmd.result()
    } catch {
      case e: Exception => Result(e)
    } finally {
      try {
        if (in != null) in.close()
      } catch {
        case e: Exception =>
      }
      try {
        file.delete()
      } catch {
        case e: Exception =>
      }
    }
  }
}

object Rez {
  def apply(path: String) =
    new Rez(path)

  def main(args: Array[String]) {
    val rez = Rez(args(0))
    val result = rez.result()
    System.out.print(result.output)
    System.err.print(result.error)
    result.exception match {
      case Some(e) => e.printStackTrace
      case None =>
    }
    sys.exit(result.status)
  }
}
