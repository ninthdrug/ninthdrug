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

import scala.actors.Actor
import scala.actors.Actor._
import java.net._
import java.nio.channels._
import java.io._
import ninthdrug.config.Config
import ninthdrug.logging.Logger

case class Connection(id: Int, socket: Socket)
case class Idle(worker: Int)

class WebServer(config: ServerConfig) {
  private val log = Logger(classOf[WebServer])
  val sites = config.sites map {site => new Site(config, site)}
  val dispatcher = new Dispatcher(sites)
  private val ports = (sites map { site => site.siteConfig.port }).distinct
  private val selector = Selector.open()

  for (port <- ports) {
    val channel = ServerSocketChannel.open()
    channel.configureBlocking(false)
    channel.socket.bind(new InetSocketAddress(port))
    channel.register(selector, SelectionKey.OP_ACCEPT)
  }

  var i = 0

  def run() = {
    log.info("WebServer running")
    dispatcher.start
    while (selector.isOpen()) {
      selector.select()
      log.debug("select() returns")
      val keys = selector.selectedKeys()
      val iterator = keys.iterator()
      while (iterator.hasNext()) {
        val key = iterator.next().asInstanceOf[SelectionKey]
        log.debug("key: " + key)
        if (key.isAcceptable()) {
          log.debug("key is acceptable")
          val channel = key.channel().asInstanceOf[ServerSocketChannel]
          val socket = channel.accept()
          if (socket != null) {
            dispatcher ! Connection(i, socket.socket())
            i += 1
          }
        }
      }
    }
  }
}

class Dispatcher(val sites: List[Site]) extends Actor {
  import scala.collection.mutable.ListBuffer
  import scala.util.Random

  private val log = Logger(classOf[Dispatcher])
  val maxWorkers = Config.getInt("ninthdrug.http.maxworkers", 1000)
  val workers = new Array[Worker](maxWorkers)
  val idlers = new ListBuffer[Int]
  val random = new Random

  for (i <- 0 until maxWorkers) {
    workers(i) = new Worker(i, this)
    workers(i).start
    idlers += i
  }

  def act() {
    log.debug("Dispatcher: started")
    loop {
      react {
        case Idle(id) =>
          idlers += id
        case conn: Connection =>
          log.debug("Dispatcher: connection ")
          val workerId =
          if (idlers.isEmpty)
            random.nextInt(maxWorkers)
          else
            idlers.remove(0)
          val worker = workers(workerId)
          worker ! conn
      }
    }
  }
}

class Worker(val id: Int, val dispatcher: Dispatcher) extends Actor {
  private val log = Logger(classOf[Worker])

  def act() {
    loop {
      react {
        case conn: Connection =>
          handleConnection(conn)
          dispatcher ! Idle(id)
      }
    }
  }

  def handleConnection(conn: Connection) {
    try {
      Request(conn.socket.getInputStream) match {
        case Some(req) =>
          log.debug("method: " + req.method)
          log.debug("path: " + req.path)
          log.debug("query: " + req.query)
          val headers = req.headers
          for (key <- headers.keys) log.debug(key + " : " + headers(key))
          val params = req.params
          log.debug("PARAMS===============================")
          for (key <- params.keys) log.debug("    " + key + " : " + params.getAll(key))
          val host = headers.get("host")
          if (host != null) {
            val parts = host.split(":")
            val servername = parts(0)
            val port = if (parts.length > 1) {
              parts(1).toInt
            } else {
              80
            }
            log.debug("servername: " + servername)
            log.debug("port: " + port)
         
            dispatcher.sites exists {
              (s: Site) => (s.handle(servername, port, req, conn) == RequestHandled)
            }
          }
        case None =>
      }
    } catch {
      case e: Exception => e.printStackTrace
    } finally {
      try {
        conn.socket.close()
      } catch {
        case e: Exception => // ignore
      }
    }
  }
}
