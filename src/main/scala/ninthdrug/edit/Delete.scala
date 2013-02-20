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
package ninthdrug.edit

import scala.util.matching.Regex

class DeleteString(pattern: String) extends Edit {
  override def edit(lines: Seq[String]): Seq[String] = {
    lines.filter { line => line.contains(pattern) }
  }
}

class DeleteRegex(pattern: Regex) extends Edit {
  override def edit(lines: Seq[String]): Seq[String] = {
    lines.filter { line =>
      pattern.findFirstIn(line) match {
        case Some(_) => true
        case None => false
      }
    }
  }
}

object Delete {
  def apply(pattern: String): Edit = {
    new DeleteString(pattern)
  }

  def apply(pattern: Regex): Edit = {
    new DeleteRegex(pattern)
  }
}
