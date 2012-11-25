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

import org.xml.sax.EntityResolver
import org.xml.sax.InputSource

/**
 * EntityResolver for XHTML DTDs.
 */
class XHTMLEntityResolver(dir: String) extends EntityResolver {
  private val publicIds = Map(
    "-//W3C//DTD XHTML 1.0 Strict//EN" -> (dir + "/xhtml1-strict.dtd"),
    "-//W3C//DTD XHTML 1.0 Transitional//EN" -> (dir + "/xhtml1-transitional.dtd"),
    "-//W3C//DTD XHTML 1.0 Frameset//EN" -> (dir + "/xhtml1-frameset.dtd"),
    "-//W3C//DTD XHTML 1.1//EN" -> (dir + "/xhtml11.dtd")
    )

  private val systemIds = Map(
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" -> (dir + "/xhtml1-strict.dtd"),
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" -> (dir + "/xhtml1-transitional.dtd"),
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd" -> (dir + "/xhtml1-frameset.dtd"),
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" -> (dir + "/xhtml11.dtd")
    )

  def resolveEntity(publicId: String, systemId: String): InputSource = {
    if (publicId != null && publicIds.isDefinedAt(publicId)) {
      return new InputSource(publicIds(publicId));
    }
    if (systemId != null && systemIds.isDefinedAt(systemId)) {
      return new InputSource(systemIds(systemId))
    }
    return null;
  }
}
