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
import scala.io.Source._
import ninthdrug.logging.Logger
import Util._

class CompilerError(message: String, cause: Throwable) extends
RuntimeException(message, cause)

object ScriptCompiler {
  private val log = Logger("ninthdrug.http.ScriptCompiler")
  def compile(
    scriptFilename: String,
    className: String,
    classPath: String,
    dir: String) {
    try {
      val scalaFilename = dir + "/scala/" + className.replace('.', '/') + ".scala"
      val classFilename = dir + "/classes/" + className.replace('.', '/') + ".class"
      log.debug("script:  " + scriptFilename)
      log.debug("scala:   " + scalaFilename)
      log.debug("class:   " + classFilename)
      val scriptFile = new File(scriptFilename)
      val scalaFile = new File(scalaFilename)
      val classFile = new File(classFilename)
      if (!scalaFile.exists() ||
              scalaFile.lastModified < scriptFile.lastModified) {
        preprocess(scriptFilename, scalaFilename, className)
      }
      if (!classFile.exists() ||
              classFile.lastModified < scalaFile.lastModified) {
        new File(dir + "/classes").mkdirs()
        val cmd = "fsc -classpath " + classPath +
                " -d " + dir + "/classes " +
                scalaFilename
        log.debug(cmd)
        val (status, output) = exec(cmd)
        log.debug(output)
        if (status != 0) {
          val message = "Error compiling " + scriptFilename + "\n" + output
          throw new CompilerError(message, null)
        }
      }
    } catch {
      case ce: CompilerError => throw ce
      case t: Exception =>
        throw new CompilerError("Error compiling " + scriptFilename, t)
    }
  }

  def preprocess(
    scriptFilename: String,
    scalaFilename: String,
    className: String) {

    val pkg = stripExt(className, '.')
    val name = getExt(className, '.')
    val scriptFile = new File(scriptFilename)
    val scalaFile = new File(scalaFilename)
    scalaFile.getParentFile.mkdirs()
    val writer = new BufferedWriter(new FileWriter(scalaFilename))
    writer.write("package ");
    writer.write(pkg);
    writer.newLine()
    writer.write("import ninthdrug.http.{Request,Response,Script}");
    writer.newLine()
    writer.write("import org.jsoup.nodes.{Document,Element,Node}");
    writer.newLine()
    writer.write("class " + name + " extends Script {");
    writer.newLine()
    writer.write("def execute(request : Request, response : Response, doc : Document) : Any = {")
    writer.newLine()
    writer.newLine()
    val reader = new BufferedReader(new FileReader(scriptFilename))
    var line = reader.readLine()
    while (line != null) {
      writer.write(line)
      writer.newLine()
      line = reader.readLine()
    }
    reader.close()
    writer.newLine()
    writer.write("}");
    writer.newLine()
    writer.write("}");
    writer.newLine()
    writer.close()
  }

  def main(args: Array[String]) {
    val scriptFilename = args(0)
    val className = args(1)
    val classPath = if (args.length > 2) args(2) else "build/classes"
    val dir = if (args.length > 3) args(3) else "build"
    compile(scriptFilename, className, classPath, dir)
  }
}
