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
package ninthdrug.jmx

import javax.management.Attribute
import javax.management.JMException
import javax.management.MBeanException
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.Query
import javax.management.QueryExp
import javax.management.openmbean.CompositeData
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import javax.naming.Context
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

class JMX(serviceURL: JMXServiceURL, props: Map[String, Any]) {
  lazy val connector = connect()
  lazy val connection = connector.getMBeanServerConnection()
  private var connected = false

  /**
   * Connect to JMX server.
   */
  def connect(): JMXConnector = {
    val con = JMXConnectorFactory.newJMXConnector(serviceURL, props)
    con.connect()
    connected = true
    con
  }

  /**
   * Close connection.
   */
  def close() {
    if (connected) connector.close()
  }

  /**
   * Retrieve attribute.
   */
  def get[T](parent: ObjectName, name: String): T =
    connection.getAttribute(parent, name).asInstanceOf[T]

  /**
   * Retrieve ObjectName.
   */
  def getName(parent: ObjectName, name: String): ObjectName =
    connection.getAttribute(parent, name).asInstanceOf[ObjectName]

  /**
   * Retrieve Array of ObjectName.
   */
  def getNameArray(parent: ObjectName, name: String): Array[ObjectName] = {
    connection.getAttribute(parent, name).asInstanceOf[Array[ObjectName]]
  }

  /**
   * Retrieve Array of Bytes.
   */
  def getByteArray(parent: ObjectName, name: String): Array[Byte] = {
    connection.getAttribute(parent, name).asInstanceOf[Array[Byte]]
  }
  
  /**
   * Retrieve String attribute.
   */
  def getString(parent: ObjectName, name: String): String = {
    connection.getAttribute(parent, name).asInstanceOf[String]
  }

  /**
   * Retrieve Array of String.
   */
  def getStringArray(parent: ObjectName, name: String): Array[String] = {
    connection.getAttribute(parent, name).asInstanceOf[Array[String]]
  }

  /**
   * Retrieve Long attribute.
   */
  def getLong(parent: ObjectName, name: String): Long = {
    connection.getAttribute(parent, name).asInstanceOf[Long]
  }

  /**
   * Retrieve Int attribute.
   */
  def getInt(parent: ObjectName, name: String): Int = {
    connection.getAttribute(parent, name).asInstanceOf[java.lang.Integer].toInt
  }

  /**
   * Retrieve CompositeData attribute.
   */
  def getCompositeData(parent: ObjectName, name: String): CompositeData = {
    connection.getAttribute(parent, name).asInstanceOf[CompositeData]
  }

  /**
   * Set an attribute of an MBean.
   */
  def setAttribute(parent: ObjectName, name: String, value: Any): Unit = {
    val attribute = new Attribute(name, value.asInstanceOf[java.lang.Object])
    connection.setAttribute(parent, attribute)
  }

  /**
   * Invoke a method on an MBean.
   */
  def invoke[T](obj: ObjectName, method: String, params: Any*): T = {
    connection.invoke(
      obj,
      method,
      JMX.makeObjectArray(params: _*),
      JMX.makeTypeArray(params: _*)
    ).asInstanceOf[T] 
  }

  /**
   * Retrieve Set of ObjectName matching query.
   */
  def queryNames(
    scope: ObjectName,
    query: QueryExp
  ): Set[ObjectName] = {
    connection.queryNames(scope, query).toSet
  }

  /**
   * Print the attributes of an MBean.
   */
  def printAttributes(obj: ObjectName) {
    val attributes = connection.getMBeanInfo(obj).getAttributes()
    for (a <- attributes) {
      println("  " + a.getName)
    }
  }
}
  
object JMX {
  def apply(
    serviceURL: JMXServiceURL,
    props: Map[String,Any]
  ): JMX = {
    new JMX(serviceURL, props)
  }

  def apply(host: String, port: Int, user: String, password: String): JMX = {
    val serviceURL = new JMXServiceURL(
      "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi")
    val props = Map(JMXConnector.CREDENTIALS -> Array(user, password))
    new JMX(serviceURL, props)
  }

  private def makeObjectArray(params: Any*): Array[java.lang.Object] = {
    val array = new Array[java.lang.Object](params.length)
    var i = 0
    for (param <- params) {
      array(i) = param.asInstanceOf[java.lang.Object]
      i += 1
    }
    array
  }

  private def makeTypeArray(params: Any*): Array[String] = {
    val array = new Array[String](params.length)
    var i = 0
    for (param <- params) {
      array(i) = param.getClass.getName
      i += 1
    }
    array
  }
}
