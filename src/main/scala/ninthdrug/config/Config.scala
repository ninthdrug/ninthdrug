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
package ninthdrug.config

import java.io._
import ninthdrug.command.Command
import scala.collection.mutable
import scala.collection.mutable.{HashMap,SynchronizedMap}
import scala.util.matching.Regex

case class ConfigError(msg: String) extends Exception(msg)

object Config {
  private val map = new HashMap[String,mutable.Set[String]] with 
   SynchronizedMap[String,mutable.Set[String]]
  private val Comment = """#.*""".r
  private val Assign = """([\w_.][\w\d_.-]*)[ \t]*=([^#]*).*""".r
  init()
  
  def init() {
    map.clear()
    val base = new File(System.getProperty("user.home") + "/.ninthdrug")
    if (base.exists && base.isFile) {
      for (path <- Command.readlines(base.getCanonicalPath)) {
        val trimmedPath = path.trim
        if (trimmedPath != "") {
          read(new File(path))
        }
      }
    }
  }

  def clear() { map.clear() }

  def has(name: String) : Boolean = map.contains(name)

  def get(name: String) : String = {
    map.get(name) match {
      case Some(s) if s.nonEmpty => s.head
      case _ => throw ConfigError("Undefined: " + name)
    }
  }
       
  def get(name: String, default: String) : String = {
    map.get(name) match {
      case Some(s) if s.nonEmpty => s.head
      case _ => default
    }
  }

  def all(name: String) : Set[String] = {
    map.get(name) match {
      case Some(s) => s.toSet
      case None => Set.empty[String]
    }
  }
  
  def getInt(name: String): Int = get(name).toInt

  def getInt(name: String, default: Int): Int = {
    map.get(name) match {
      case Some(s) if s.nonEmpty => s.head.toInt
      case _ => default
    }
  }

  def getLong(name: String): Long = get(name).toLong

  def getInt(name: String, default: Long): Long = {
    map.get(name) match {
      case Some(s) if s.nonEmpty => s.head.toLong
      case _ => default
    }
  }

  def add(name: String, value: String) {
    if (map.contains(name)) {
      map(name).add(value)
    } else {
      map(name) = mutable.Set(value)
    }
  }

  def set(name: String, value: String) {
    map(name) = mutable.Set(value)
  }

  def set(name: String, value: Int) {
    map(name) = mutable.Set(value.toString)
  }

  def set(name: String, value: Long) {
    map(name) = mutable.Set(value.toString)
  }

  def read(file: File) {
    val path = file.getCanonicalPath
    if (!file.exists()) {
      throw new ConfigError("Nonexistent path: " + path)
    }
    if (!file.canRead()) {
      throw new ConfigError("Unreadable path: " + path)
    }
    if (file.isDirectory) {
      readDir(file)
    } else {
      readFile(file)
    }
  }

  def read(path: String) {
    read(new File(path))
  }

  private def readFile(file: File) {
    val reader = new BufferedReader(new FileReader(file))
    var line = reader.readLine()
    while (line != null) {
      line.trim match {
        case "" =>
        case Comment() =>
        case Assign(name, value) => {
          add(name, value.trim)
        }
        case _ => throw ConfigError(
           "Configuration error in " +
           file.getCanonicalPath + ": " +
           line
        )
      }
      
      line = reader.readLine()
    }
    reader.close()
  }

  private def readDir(dir: File) {
    for (file <- dir.listFiles(); if !file.getName.startsWith(".")) {
      if (file.isDirectory) {
        readDir(file)
      } else {
        readFile(file)
      }
    }
  }
}
