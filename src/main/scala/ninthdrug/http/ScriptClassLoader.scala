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

import Util._
import java.io._

class ScriptClassLoader(parent: java.lang.ClassLoader, dir: String)
        extends ClassLoader(parent: java.lang.ClassLoader) {
  private def getBytes(filename: String): Array[Byte] = {
    val file = new File(filename)
    val len = file.length.asInstanceOf[Int]
    val bytes = new Array[Byte](len)
    val fin = new FileInputStream(file)
    if (fin.read(bytes) != len)
      throw new IOException("Error reading " + filename)
    fin.close()
    bytes
  }

  @throws(classOf[java.lang.ClassNotFoundException])
  override def loadClass(className: String, resolve: Boolean): java.lang.Class[_] = {
    if (!className.startsWith("ninthdrug.script.")) {
      parent.loadClass(className)
    } else {
      val name = className
      val path = dir + "/" + name.replace('.', '/')
      val classFilename = path + ".class"
      val classFile = new File(classFilename)
      val bytes = getBytes(classFilename)
      defineClass(className, bytes, 0, bytes.length)
    }
  }
}
