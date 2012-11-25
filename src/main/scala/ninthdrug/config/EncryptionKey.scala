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
package ninthdrug.config

import java.io._
import ninthdrug.util.Base64
import ninthdrug.command.Command
import scala.util.Random

/**
 * The default 32 bytes key used for encryption.  The key is read from a file.
 * The file name is looked up in the Config under the name "ninthdrug.encryption.keyfile".  If the config is not found, the file .key in the user home is used.
 * If the file does not exits, a random key generated and stored in the file using Base64 encoding.
 */
object EncryptionKey {
  private lazy val _key: Array[Byte] = initkey()

  /**
   * Returns the encryption key.
   */
  def key = _key

  private def initkey(): Array[Byte] = {
    val keyFileName = Config.get(
      "ninthdrug.encryption.keyfile",
      System.getProperty("user.home") + "/.key"
    )
    val keyFile = new File(keyFileName)
    if (keyFile.exists) {
      Base64.decode(Command.read(keyFileName).trim)
    } else {
      val bytes = generateKey()
      Command.write(keyFileName, Base64.encode(bytes))
      bytes
    }
  }

  /**
   * Generate a 32 bytes key.
   */
  def generateKey(): Array[Byte] = {
    val bytes = new Array[Byte](32)
    Random.nextBytes(bytes)
    bytes
  }
}
