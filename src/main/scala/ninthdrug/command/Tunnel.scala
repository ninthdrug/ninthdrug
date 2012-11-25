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

class Tunnel(
  localport: Int,
  remotehost: String,
  remoteport: Int,
  host: String,
  user: String = ""
) extends Command {
  private val u = if (user == "") "" else user + "@"
  private val k = {
    if (user == "") {
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
  val cmd = "ssh -f " +
          "-L " + localport + ":" + remotehost + ":" + remoteport +
          k + u + host + " -N"

  def result() = Exec.exec(cmd)

  override def toString(): String = "Tunnel(" + cmd + ")"
}

object Tunnel {
  def apply(localport: Int,
            remotehost: String,
            remoteport: Int,
            host: String,
            user: String = "") =
    new Tunnel(localport, remotehost, remoteport, host, user)
}
