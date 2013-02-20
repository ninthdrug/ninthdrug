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
 *  Nithdrug is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ninthdrug.  If not, see <http://www.gnu.org/licenses/>.
 */
package ninthdrug.command

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.IOException
import ninthdrug.util.Util
import scala.collection.mutable.ListBuffer
import scala.util.matching._

abstract class Command extends Serializable {
  def result(): Result
  def run(): Unit = result()
  def print(): Unit = result.print()
}

object Command {
  /**
   * Execute a system command and return the result.
   */
  def exec(command: String): Result = Exec.exec(command)

  /**
   * Execute a system command and return the result.
   */
  def exec(command: Array[String]): Result = Exec.exec(command)

  /**
   * Execute a shell command and return the result.
   */
  def sh(command: String): Result = {
    Result(Runtime.getRuntime.exec(Array("sh", "-c", command)))
  }

  /**
   * Execute a shell command and return the output as a string.
   */
  def shout(command: String): String = {
    val process = Runtime.getRuntime.exec(command)
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))
    val buf = new StringBuilder()
    var line = reader.readLine()
    while (line != null) {
      buf.append(line).append("\n")
      line = reader.readLine()
    }
    process.waitFor()
    buf.toString.trim
  }

  /**
   * Execute a shell command and return the output as a list of string.
   */
  def shlines(command: String): Seq[String] = {
    val process = Runtime.getRuntime.exec(command)
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))
    val buf = new ListBuffer[String]()
    var line = reader.readLine()
    while (line != null) {
      buf.append(line)
      line = reader.readLine()
    }
    process.waitFor()
    buf.toSeq
  }

  /**
   * Return a list of process ids with commands matching input pattern.
   */
  def psgrep(pattern: String): Seq[Int] = {
    val pids = ListBuffer[Int]()
    val re = new Regex(pattern)
    for (line <- shlines("ps -e -o pid,command").tail) {
      val cols = line.trim.split(" ", 2).toList
      if (cols(1) != "grep") {
        re.findFirstMatchIn(cols(1)) match {
          case Some(m) => pids += cols.head.toInt
          case None =>
        }
      }
    }
    pids.toSeq
  }

  /**
   * Read a file into a string.
   */
  def read(filename: String): String = {
    val file = new File(filename)
    val length = file.length.toInt
    val buf = new Array[Byte](length)
    var f: FileInputStream = null
    try {
      f = new FileInputStream(filename)
      f.read(buf)
    } finally {
      if (f != null) {
        try {
          f.close()
        } catch {
          case e: IOException => 
        }
      }
    }
    return new String(buf)
  }

  /**
   * Read all lines from a file into a list.
   */
  def readlines(filename: String): Seq[String] = {
    val buf = ListBuffer[String]()
    val reader = new BufferedReader(new FileReader(filename))
    var line = reader.readLine()
    while (line != null) {
      buf.append(line)
      line = reader.readLine()
    }
    reader.close()
    buf.toSeq
  }

  /**
   * Write a string to a file.
   */
  def write(filename: String, text: String) {
    val writer = new FileWriter(filename)
    writer.write(text)
    writer.close()
  }

  /**
   * Write lines to a file.
   */
  def writelines(filename: String, lines: Seq[String]) {
    val writer = new FileWriter(filename)
    for (line <- lines) {
      writer.write(line)
      writer.write("\n")
    }
    writer.close()
  }

  /**
   * Kill process tree.
   */
  def kill_process_tree(ppid: Int, delay: Int = 180) {
    var pids = get_child_processes(ppid)
    for (pid <- pids) {
      kill_process_tree(pid, delay)
      pids = get_child_processes(ppid)
    }
    kill(ppid, delay)
  }

  /**
   * Kill process.
   */
  def kill(pid: Int, delay: Int = 180) {
    exec("kill " + pid)
    for (i <- 0 to delay) {
      if (shout("ps -o pid= --pid " + pid) == "") return
      Thread.sleep(1000L)
    }
    exec("kill -9 " + pid)
    for (i <- 0 to 30) {
      if (shout("ps -o pid= --pid " + pid) == "") return
      Thread.sleep(1000L)
    }
    throw new Exception("Timeout killing " + pid)
  }

  /**
   * Return subprocesses for a parent process.
   */
  def get_child_processes(pid: Int): Seq[Int] = {
    shlines("ps -o pid= --ppid " + pid) map { pid => pid.toInt }
  }

  /**
   * Replace all patterns ${name} in input string with corresponding
   * values from the input map.  For example, ${version} would be replaced
   * with map("version").
   */
  def interpolate(string: String, map: Map[String,String]): String = {
    Util.interpolate(string, map)
  }

  /**
   * Return the nth element of args, or prompt it from the user if
   * there is no nth element in args.
   */
  def prompt(args: Seq[String], n: Int, name: String): String = {
    if (n < args.length) {
      args(n)
    } else {
      Console.readLine(name + ": ")
    }
  }

  /**
   * Split a string into a list of substrings separated by the input separator.
   * This method returns an empty list when splitting an empty string.
   * The normal String.split returns an array with an empty element.
   */
  def split(string: String, separator: String, limit: Int = 0): Seq[String] = {
    if (string == "") {
      Seq()
    } else {
      string.split(separator, limit).toSeq
    }
  }
}
