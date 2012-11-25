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

import scala.collection.mutable
import java.io._
import ninthdrug.logging.Logger
import ninthdrug.config.Config
import Util._

case class ScriptCacheEntry(path: String, script: Script, timestamp: Long)

object ScriptCache {
  private val log = Logger("ninthdrug.http.ScriptCache")
  private val cache = mutable.Map[String, ScriptCacheEntry]()
  private val libdir = Config.get("lib.dir",
    System.getProperty("user.home") + "/lib")

  def makeClassName(site: String, path: String): String = {
    val base = stripExt(path, '.')
    "ninthdrug.script." + site + base.replace("/", ".")
  }

  def getScript(cachedir: String, site: String, root: String, name: String): Script = {
    log.debug("getScript site:" + site + " root:" + root + " name:" + name)
    val path = root + name
    val file = new File(path)
    val lastModified = file.lastModified
    if (!cache.contains(path) || cache(path).timestamp < lastModified) {
      log.debug("cache miss")
      val className = makeClassName(site, name)
      var libs = ""
      val libFile = new File(libdir)
      if (libFile.isDirectory()) {
        val lib = libFile.getCanonicalPath()
        for (name <- libFile.list()) {
          if (name.endsWith(".jar")) {
            libs += ":" + lib + "/" + name
          }
        }
      }
      val webLibFile = new File(root + "/WEB-INF/lib")
      if (webLibFile.isDirectory()) {
        val lib = webLibFile.getCanonicalPath()
        for (name <- webLibFile.list()) {
          if (name.endsWith(".jar")) {
            libs += ":" + lib + "/" + name
          }
        }
      }
      val classPath = root + "/WEB-INF/classes" + libs
      log.debug("classpath: " + classPath)
      ScriptCompiler.compile(path, className, classPath, cachedir)
      val parent = classOf[ScriptClassLoader].getClassLoader
      val loader = new ScriptClassLoader(parent, cachedir + "/classes")
      val script = loader.loadClass(className).newInstance().asInstanceOf[Script]
      cache.update(path, ScriptCacheEntry(path, script, lastModified))
    }
    log.debug("script: " + cache(path).script)
    cache(path).script
  }
}
