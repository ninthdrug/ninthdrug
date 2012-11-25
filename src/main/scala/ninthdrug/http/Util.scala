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
package ninthdrug.http

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.SimpleTimeZone
import java.io._

object Util {
  private def makeHTTPDateFormat: SimpleDateFormat = {
    val format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
    format.setCalendar(Calendar.getInstance(new SimpleTimeZone(0, "GMT")))
    return format
  }

  private val HTTPDateFormat = makeHTTPDateFormat

  def formatHTTPDate(date: Date): String = HTTPDateFormat.format(date)

  def stripExt(string: String, sep: Char): String = {
    val i = string.lastIndexOf(sep)
    if (i == -1) string else string.substring(0, i)
  }

  def getExt(string: String, sep: Char): String = {
    string.substring(string.lastIndexOf(sep) + 1)
  }

  def break(s: String, sep: Char): Tuple2[String, String] = {
    val i = s.indexOf(sep)
    if (i == -1) (s, "") else (s.substring(0, i), s.substring(i + 1))
  }

  def exec(command: String): Tuple2[Int, String] = {
    val process = Runtime.getRuntime.exec(command)
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))
    val buf = new StringBuilder()
    var line = reader.readLine()
    while (line != null) {
      buf.append(line).append("\n")
      line = reader.readLine()
    }
    val output = buf.toString
    var status = 0
    try {
      status = process.waitFor()
    } catch {
      case e: InterruptedException =>
    }
    (status, output)
  }
}
