/*
 * Copyright 2008-2013 Trung Dinh
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

  /**
   * Execute a system command and return the result.
   */
  def exec(command: String): Result = Command.exec(command)

  /**
   * Execute a shell command and return the output as a list of string.
   */
  def sh(command: String) = Command.sh(command)

  /**
   * Execute a shell command and return the output as a string.
   */
  def shout(command: String) = Command.shout(command)

  /**
   * Execute a shell command and return the output as a list of string.
   */
  def shlines(command: String) = Command.shlines(command)

  /**
   * Return a list of process ids with commands matching input pattern.
   */
  def psgrep(pattern: String) = Command.psgrep(pattern)

  /**
   * Read a file and return its contents as a string.
   */
  def read(filename: String) = Command.read(filename)

  /**
   * Read a file and return its contents as a list of string.
   */
  def readlines(filename: String) = Command.readlines(filename)

  /**
   * Write a string to a file.
   */
  def write(filename: String, text: String) = Command.write(filename, text)

  /**
   * Write a sequence of string to a file.
   */
  def writelines(filename: String, lines: Seq[String]) =
    Command.writelines(filename, lines)

  /**
   * Kill a process and all its descendants.
   */
  def kill_process_tree(ppid: Int, delay: Int = 180) =
    Command.kill_process_tree(ppid, delay)

  /**
   * Kill a process.
   */
  def kill(pid: Int, delay: Int = 180) =
    Command.kill(pid, delay)

  /**
   * Replace all patterns ${name} in input string with corresponding
   * values from the input map.  For example, ${version} would be replaced
   * with map("version").
   */
  def interpolate(string: String, map: Map[String,String]) =
    Util.interpolate(string, map)

  /**
   * Return the nth element of args, or prompt it from the user if
   * there is no nth element in args.
   */
  def prompt(args: Seq[String], n: Int, name: String): String =
    Command.prompt(args, n, name)

  /**
   * The canonical host name.
   */
  lazy val HOST = InetAddress.getLocalHost.getCanonicalHostName

  /**
   * The short host name.
   */
  lazy val HOSTNAME = InetAddress.getLocalHost.getHostName.split('.')(0)

  /**
   * Split a string at separator.
   */
  def split(string: String, separator: Char): List[String] = {
    Util.split(string, separator)
  }
}
