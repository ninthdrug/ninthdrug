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

import java.io._
import org.jsoup.nodes._
import ninthdrug.command._

trait Script {

  def execute(request: Request, response: Response, doc: Document): Any

  def getElementById(elem: Element, id: String): Element = {
    elem.getElementById(id)
  }

  def setText(elem: Element, text: String) {
    elem.text(text)
  }

  def replaceElement(oldElem: Element, newElem: Element) {
    oldElem.replaceWith(newElem.clone)
  }

  def exec(command: String): Result = Command.exec(command)

  def shout(command: String): String = Command.shout(command)

  def redirect(url: String): Redirect = new Redirect(url)
}
