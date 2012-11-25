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

class Ssh(command: String, host: String, user: String = null) extends Command {
  private val u = if (user == null || user == "") "" else user + "@"
  private val k = {
    if (user == null || user == "") {
      ""
    } else {
      val home = System.getProperty("user.home")
      val keyfile = home + "/.ssh/" + user + ".id_rsa"
      val file = new File(keyfile)
      if (file.exists() && file.canRead()) {
        "-i " + keyfile + " "
      } else {
        ""
      }
    }
  }
  val sshcmd = "ssh " + k + u + host + " '" + command + "'"
  val cmds = Array("sh", "-c", sshcmd)

  def result() = Exec.exec(cmds)

  override def toString(): String = "Ssh(" + sshcmd + ")"
}

object Ssh {
  def apply(command: String, host: String, user: String = "") =
    new Ssh(command, host, user)

  def main(args: Array[String]) {
    val cmd = args(0)
    val host = args(1)
    val user = if (args.length > 2) args(2) else ""
    val ssh = Ssh(cmd, host, user)
    println(ssh)
    val result = ssh.result()
    System.out.print(result.output)
    System.err.print(result.error)
    result.exception match {
      case Some(e) => e.printStackTrace
      case None =>
    }
    sys.exit(result.status)
  }
}
