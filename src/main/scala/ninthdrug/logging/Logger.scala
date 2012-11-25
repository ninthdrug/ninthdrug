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
package ninthdrug.logging

import java.text.SimpleDateFormat
import java.util.Date
import ninthdrug.config.Config

class Logger(name: String) {
  private var currentLevel: Int = {
    var key = "ninthdrug.logging.level." + name 
    while (key != "ninthdrug.logging.level" && !Config.has(key)) {
      val index = key.lastIndexOf(".")
      key = key.substring(0, index)
    }
    Logger.toLevel(Config.get(key, "INFO"))
  }

  private val levelLock = new Object

  def setLevel(level: String) {
    setLevel(Logger.toLevel(level))
  }

  def setLevel(level: Int) {
    levelLock.synchronized {
      currentLevel = level
    }
  }

  def log(level: Int, msg: => String) {
    if (level >= currentLevel) {
      Logger.append(Logger.format(level, name, msg))
    }
  }

  def trace(msg: => String) {
    log(Logger.TRACE, msg)
  }

  def debug(msg: => String) {
    log(Logger.DEBUG, msg)
  }

  def info(msg: => String) {
    log(Logger.INFO, msg)
  }

  def warn(msg: => String) {
    log(Logger.WARN, msg)
  }

  def error(msg: => String) {
    log(Logger.ERROR, msg)
  }

  def fatal(msg: => String) {
    log(Logger.FATAL, msg)
  }

  def stacktrace(e: Exception) {
    e.printStackTrace
  }
}

object Logger {
  import collection.mutable.HashMap
  import collection.mutable.SynchronizedMap

  val ALL   = Int.MinValue
  val TRACE = 10
  val DEBUG = 20
  val INFO  = 30
  val WARN  = 40
  val ERROR = 50
  val FATAL = 60
  val OFF   = Int.MaxValue

  private val _levelToString = Map(
    ALL   -> "ALL",
    TRACE -> "TRACE",
    DEBUG -> "DEBUG",
    INFO  -> "INFO",
    WARN  -> "WARN",
    ERROR -> "ERROR",
    FATAL -> "FATAL",
    OFF   -> "OFF"
  )

  private val loggers = 
    new HashMap[String,Logger] with SynchronizedMap[String,Logger]

  val levelToString = _levelToString.withDefaultValue("INFO")

  val toLevel = _levelToString.map(_.swap).withDefaultValue(INFO)

  lazy val appender = {
    if (Config.has("ninthdrug.logging.file")) {
      new FileAppender(Config.get("ninthdrug.logging.file"))
    } else {
      ConsoleAppender
    }
  }

  def apply(name: String) = {
    if (!loggers.contains(name)) {
      loggers(name) = new Logger(name)
    }
    loggers(name)
  }

  def apply(c: Class[_]) = new Logger(c.getName)

  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  def format(level: Int, name: String, msg: String): String = {
    val time = dateFormat.format(new Date())
    val lvl = levelToString(level)
    "%s %-5s %s - %s\n".format(time, lvl, name, msg)
  }

  def append(msg: String) = appender.append(msg)
}
