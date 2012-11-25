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
package ninthdrug.util
import java.text.SimpleDateFormat
import java.sql.Timestamp
import java.util.regex._
import scala.collection.mutable.ListBuffer

object Util {
  def quote(s: String): String = {
    if (s == null) {
      null
    } else {
      "\"" + s.replace("\"", "\\\"") + "\""
    }
  }

  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  /**
   * Format a Timestamp with the format "yyyy-MM-dd HH:mm:ss.SSS")
   */
  def formatTimestamp(t: Timestamp): String = {
    if (t == null) {
      null
    } else {
      dateFormat.format(t)
    }
  }

  private val PATTERN_STRING = """\$\{[\w_][\w\d_\.]*\}"""
  private val PATTERN = Pattern.compile(PATTERN_STRING);

  /**
   * Replace all patterns ${name} in input string with corresponding
   * values from the input map.  For example, ${version} would be replaced
   * with map("version").
   */
  def interpolate(string: String, map: Map[String,String]): String = {
    val buf = new StringBuilder();
    val matcher = PATTERN.matcher(string);
    var index = 0;
    while (matcher.find(index)) {
      val start = matcher.start();
      val end = matcher.end();
      val name = string.substring(start + 2, end - 1);
      if (map.contains(name)) {
        val value = map(name)
        buf.append(string.substring(index, start));
        buf.append(value);
      } else {
        buf.append(string.substring(index, end));
      }
      index = end;
    }
    if (index < string.length()) {
      buf.append(string.substring(index, string.length()));
    }
    buf.toString
  }

  /**
   * Split a string at separator.
   */
  def split(string: String, separator: Char): List[String] = {
    if (string == "") {
      List()
    } else {
      val buf = ListBuffer[String]()
      var prev = 0
      while (prev != -1) {
        val next = string.indexOf(separator, prev)
        if (next != -1) {
          buf.append(string.substring(prev, next))
          prev = next + 1
        } else {
          buf.append(string.substring(prev, string.length))
          prev = -1
        }
      }
      buf.toList
    }
  }
}
