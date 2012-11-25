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

object Mime {
  val typeOf = Map[String,String](
    "ai" -> "application/postscript",
    "avi" -> "video/x-msvideo",
    "css" -> "text/css",
    "dvi" -> "application/x-dvi",
    "eps" -> "application/postscript",
    "flv" -> "video/flv",
    "gif" -> "image/gif",
    "gz" -> "application/x-gzip",
    "html" -> "text/html",
    "htm" -> "text/html",
    "ico" -> "image/x-icon",
    "ief" -> "image/ief",
    "jpeg" -> "image/jpeg",
    "jpe" -> "image/jpeg",
    "jpg" -> "image/jpeg",
    "js" -> "text/javascript",
    "json" -> "application/json",
    "mov" -> "video/quicktime",
    "mp2" -> "video/mpeg",
    "mp4" -> "video/mp4",
    "mpeg" -> "video/mpeg",
    "mpe" -> "video/mpeg",
    "mpg" -> "video/mpeg",
    "pdf" -> "application/pdf",
    "png" -> "image/png",
    "ps" -> "application/postscript",
    "qt" -> "video/quicktime",
    "rtf" -> "text/rtf",
    "sgml" -> "text/sgml",
    "sgm" -> "text/sgml",
    "swf" -> "application/x-shockwave-flash",
    "tar" -> "application/x-tar",
    "tgz" -> "application/x-tar-gz",
    "tiff" -> "image/tiff",
    "tif" -> "image/tiff",
    "txt" -> "text/plain",
    "wmv" -> "video/x-ms-wmv",
    "wmx" -> "video/x-ms-wmx",
    "wvx" -> "video/x-ms-wvx",
    "xhtml" -> "text/html",
    "xml" -> "text/xml"
  ).withDefaultValue("text/plain")
}
