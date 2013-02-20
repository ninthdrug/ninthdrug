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

abstract class Edit extends Serializable {
  def edit(lines: Seq[String]): Seq[String]
}

object Edit {
  /**
   * Apply edits to a list of lines.
   */
  def edit(lines: Seq[String], edits: Edit*): Seq[String] = {
    var results = lines
    for (e <- edits) {
      results = e.edit(results)
    }
    results
  }
}
