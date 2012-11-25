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

package ninthdrug.expect

import java.io._
import scala.actors.threadpool.BlockingQueue
import scala.actors.threadpool.LinkedBlockingQueue
import scala.util.matching._
import ninthdrug.logging.Logger

class Expect(cmd: String, timeout: Int) {
  private val log = Logger(classOf[Expect])
  val process = Runtime.getRuntime.exec(cmd)
  val queue: BlockingQueue[Any] = new LinkedBlockingQueue[Any]()
  val outmon = new OutputMonitor(queue, process.getInputStream)
  val writer = new OutputStreamWriter(process.getOutputStream)
  val outbuf = new StringBuilder()
  private var _before = new String()
  outmon.start

  def expect(pattern: String) {
    var index = 0
    log.debug("\nexpecting: " + pattern)
    while (true) {
      val i = outbuf.indexOf(pattern)
      if (i == -1) {
        val n = outbuf.length - pattern.length
        if (n > 0) {
          //outbuf.delete(0, outbuf.length)
        }
      } else {
        val n = i + pattern.length
        _before = outbuf.substring(0, n)
        outbuf.delete(0, n)
        return
      }
      queue.take match {
        case EndOfOutputStream => throw new UnexpectedEndOfStream()
        case OutputCharacter(character) => outbuf.append(character)
      }
    }
  }

  def send(text: String) {
    log.debug("\nsending: " + text)
    writer.write(text)
    writer.flush()
  }

  def sendline(text: String) {
    log.debug("\nsending: " + text)
    writer.write(text + "\n")
    writer.flush()
  }

  def interact() {
    while(true) {
      val line = scala.Console.readLine
      writer.write(line)
      writer.flush()
    }
  }

  def before = _before
}

object Expect {
  def spawn(cmd: String): Expect = new Expect(cmd, 0)
}

class UnexpectedEndOfStream extends Exception

case object EndOfOutputStream
case class OutputCharacter(character: java.lang.Character)

class OutputMonitor(queue: BlockingQueue[Any], stream: InputStream)
  extends Thread {

  override def run() {
    val reader = new InputStreamReader(stream)
    val buf = new Array[Char](1)
    while (true) {
      val n = reader.read(buf)
      if (n == -1) {
        queue.add(EndOfOutputStream)
        return
      } else if (n == 1) {
        val character = buf(0).asInstanceOf[java.lang.Character]
        print(character)
        queue.add(new OutputCharacter(character))
      }
    }
  }
}
