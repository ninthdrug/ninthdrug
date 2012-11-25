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
import ninthdrug.command._
import ninthdrug.util.Util
import java.net.InetAddress

package object ninthdrug {
  def exec(command: String): Result = Command.exec(command)
  def sh(command: String) = Command.sh(command)
  def shout(command: String) = Command.shout(command)
  def shlines(command: String) = Command.shlines(command)
  def psgrep(pattern: String) = Command.psgrep(pattern)
  def read(filename: String) = Command.read(filename)
  def readlines(filename: String) = Command.readlines(filename)
  def write(filename: String, text: String) = Command.write(filename, text)
  def writelines(filename: String, lines: List[String]) =
    Command.writelines(filename, lines)
  def kill_process_tree(ppid: Int, delay: Int = 180) =
    Command.kill_process_tree(ppid, delay)
  def kill(pid: Int, delay: Int = 180) =
    Command.kill(pid, delay)
  def interpolate(string: String, map: Map[String,String]) =
    Util.interpolate(string, map)
  def prompt(args: Seq[String], n: Int, name: String): String =
    Command.prompt(args, n, name)
  lazy val fullhostname = InetAddress.getLocalHost.getCanonicalHostName
  lazy val shorthostname = InetAddress.getLocalHost.getHostName.split('.')(0)

  /**
   * Split a string at separator.
   */
  def split(string: String, separator: Char): List[String] = {
    Util.split(string, separator)
  }
}
