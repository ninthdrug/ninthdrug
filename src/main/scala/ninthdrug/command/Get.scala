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

class Get(
  val src: String,
  val host: String,
  val user: String,
  val dest: String
) extends Command {
  def result(): Result = {
    val u = if (user == null) "" else user + "@"
    val k = {
      if (user == null) {
        ""
      } else {
        val home = System.getProperty("user.home")
        val keyfile = home + "/.ssh/" + user + ".id_rsa"
        val file = new File(keyfile)
        if (file.exists() && file.canRead()) {
          " -i " + keyfile
        } else {
          ""
        }
      }
    }
    val destpath = if (dest == null) src else dest
    val destfile = new File(destpath).getCanonicalFile
    val destdir = destfile.getParentFile

    val rsync = "rsync --delete -aze 'ssh " + k + "' " + u + host + ":" + src + " " + destfile
    val sh = Exec(Array("/bin/sh", "-c", rsync))
    val cmd = if (destdir.exists) {
      sh
    } else {
      Chain(Exec("mkdir -p " + destdir), sh)
    }
    cmd.result
  }

  override def toString(): String = "Get(" + src + ", " + host + ", " + user + ", " + dest + ")"
}

object Get {
  def apply(
    src: String,
    host: String,
    user: String = null,
    dest: String = null
  ) = new Get(src, host, user, dest)

  def main(args: Array[String]) {
    val src = args(0)
    val host = args(1)
    val user = if (args.length > 2) args(2) else null
    val dest = if (args.length > 3) args(3) else null
    val cmd = Get(src, host, user, dest)
    println(cmd)
    val result = cmd.result()
    System.out.print(result.output)
    System.err.print(result.error)
    result.exception match {
      case Some(e) => e.printStackTrace
      case None =>
    }
    sys.exit(result.status)
  }
}
