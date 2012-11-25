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
package ninthdrug.command

import java.io.File
import scala.xml._
import scala.xml.transform._

case class EditXmlFile(filename: String, rules: RewriteRule*) extends Command {
  def result(): Result = {
    try {
      val xml = XML.loadFile(filename)
      val transformer = new RuleTransformer(rules: _*)
      Write(filename, format(transformer.transform(xml).head)).result
    } catch {
      case e: Exception => Result(e)
    }
  }

  def format(node: Node): String = {
    val pp = new PrettyPrinter(1000, 2) {
      override protected def traverse(node: Node, pscope: NamespaceBinding, ind: Int) = node match {
        case e: Elem if e.child.size == 0 => makeBox(ind, leafTag(e))
        case _ => super.traverse(node, pscope, ind)
      }
    }
    val buf = new StringBuilder()
    buf.append("""<?xml version="1.0" encoding="UTF-8"?>""")
    buf.append("\n")
    buf.append(pp.format(node))
    buf.append("\n")
    buf.toString
  }

  override def toString(): String = "EditXmlFile(" + filename + ")"
}
