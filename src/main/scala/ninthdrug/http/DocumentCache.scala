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

import java.io.File
import scala.collection.mutable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

case class DocumentCacheEntry(path: String, doc: Document, timestamp: Long)

object DocumentCache {
  val cache = mutable.Map[String, DocumentCacheEntry]()

  def getDocument(path: String): Document = {
    val file = new File(path)
    if (!file.exists() || !file.isFile() || !file.canRead()) {
      throw new java.io.FileNotFoundException("File not found: " + path)
    }
    val lastModified = file.lastModified()
    if (!cache.contains(path) || cache(path).timestamp < lastModified) {
      val doc = Jsoup.parse(new File(path), null)
      val entry = DocumentCacheEntry(path, doc, lastModified)
      cache.update(path, entry)
      doc.clone
    } else {
      cache(path).doc.clone
    }
  }
}
