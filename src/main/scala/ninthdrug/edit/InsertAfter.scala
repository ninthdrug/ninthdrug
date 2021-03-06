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
package ninthdrug.edit

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

class InsertAfterString(pattern: String, insert: String) extends Edit {

  override def edit(lines: Seq[String]): Seq[String] = {
    val buf = ListBuffer[String]()
    for (line <- lines) {
      buf.append(line)
      if (line.contains(pattern)) {
        buf.append(insert)
      }
    }
    buf.toSeq
  }
}

class InsertAfterRegex(pattern: Regex, insert: String) extends Edit {

  override def edit(lines: Seq[String]): Seq[String] = {
    val buf = ListBuffer[String]()
    for (line <- lines) {
      buf.append(line)
      pattern.findFirstIn(line) match {
        case Some(_) => buf.append(insert)
        case None =>
      }
    }
    buf.toSeq
  }
}

object InsertAfter {
  def apply(pattern: String, insert: String): Edit = {
    new InsertAfterString(pattern, insert)
  }

  def apply(pattern: Regex, insert: String): Edit = {
    new InsertAfterRegex(pattern, insert)
  }
}
