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
package ninthdrug.sql

import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Timestamp
import scala.collection.mutable.ArrayBuffer

class Database(dburl: String, user: String, password: String) {
  if (dburl.startsWith("jdbc:postgresql:")) {
    Class.forName("org.postgresql.Driver")
  } else if (dburl.startsWith("jdbc:mysql:")) {
    Class.forName("com.mysql.jdbc.Driver")
  } else if (dburl.startsWith("jdbc:oracle:thin:")) {
    Class.forName("oracle.jdbc.driver.OracleDriver")
  }

  /**
   * Execute database select query.
   */
  def select[T](query: String, params: Any*)(f: ResultSet => T): Seq[T] = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    val out = new ArrayBuffer[T]
    try {
      connection = getConnection()
      statement = buildStatement(connection, query, params: _*)
      resultSet = statement.executeQuery()
      while (resultSet.next()) {
        out += f(resultSet)
      }
    } finally {
      closeResultSet(resultSet)
      closeStatement(statement)
      closeConnection(connection)
    }
    out
  }

  /**
   * Execute database query returning a list.
   */
  def list[T](query: String, params: Any*)(f: ResultSet => T): List[T] = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    val out = new ArrayBuffer[T]
    try {
      connection = getConnection()
      statement = buildStatement(connection, query, params: _*)
      resultSet = statement.executeQuery()
      while (resultSet.next()) {
        out += f(resultSet)
      }
    } finally {
      closeResultSet(resultSet)
      closeStatement(statement)
      closeConnection(connection)
    }
    out.toList
  }

  /**
   * Execute database query returning an optional result.
   */
  def find[T](query: String, params: Any*)(f: ResultSet => T): Option[T] = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    try {
      connection = getConnection()
      statement = buildStatement(connection, query, params: _*)
      resultSet = statement.executeQuery()
      if (resultSet.next()) {
        Some(f(resultSet))
      } else {
        None
      }
    } finally {
      closeResultSet(resultSet)
      closeStatement(statement)
      closeConnection(connection)
    }
  }

  /**
   * Execute database count query.
   */
  def count(query: String, params: Any*): Long = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    var result = 0L
    try {
      connection = getConnection()
      statement = buildStatement(connection, query, params: _*)
      resultSet = statement.executeQuery()
      if (resultSet.next()) {
        result = resultSet.getLong(1)
      } else {
        throw new DatabaseException("Failed query: " + query)
      }
    } finally {
      closeResultSet(resultSet)
      closeStatement(statement)
      closeConnection(connection)
    }
    result
  }

  /**
   * Retrieve next Int from sequence.
   */
  def nextInt(seq: String): Int = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    var result = 0
    val query = "select nextval('" + seq + "')"
    try {
      connection = getConnection()
      statement = buildStatement(connection, query)
      resultSet = statement.executeQuery()
      if (resultSet.next()) {
        result = resultSet.getInt(1)
      } else {
        throw new DatabaseException("Failed query: " + query)
      }
    } finally {
      closeResultSet(resultSet)
      closeStatement(statement)
      closeConnection(connection)
    }
    result
  }

  /**
   * Retrieve next Long from sequence.
   */
  def nextLong(seq: String): Long = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    var result = 0L
    val query = "select nextval('" + seq + "')"
    try {
      connection = getConnection()
      statement = buildStatement(connection, query)
      resultSet = statement.executeQuery()
      if (resultSet.next()) {
        result = resultSet.getLong(1)
      } else {
        throw new DatabaseException("Failed query: " + query)
      }
    } finally {
      closeResultSet(resultSet)
      closeStatement(statement)
      closeConnection(connection)
    }
    result
  }

  /**
   * Return true if select query returns non empty results.
   */
  def exists(query: String, params: Any*): Boolean = {
    var connection: Connection = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet = null
    try {
      connection = getConnection()
      statement = buildStatement(connection, query, params: _*)
      resultSet = statement.executeQuery()
      if (resultSet.next()) {
        true
      } else {
        false
      }
    } finally {
      closeResultSet(resultSet)
      closeStatement(statement)
      closeConnection(connection)
    }
  }

  /**
   * Execute database update query.
   */
  def update(query: String, params: Any*) {
    execute(query, params: _*)
  }

  /**
   * Execute database delete query.
   */
  def delete(query: String, params: Any*) {
    execute(query, params: _*)
  }

  /**
   * Execute database update/delete query.
   */
  def execute(query: String, params: Any*) {
    var connection: Connection = null
    var statement: PreparedStatement = null
    try {
      connection = getConnection()
      statement = buildStatement(connection, query, params: _*)
      statement.execute()
    } finally {
      closeStatement(statement)
      closeConnection(connection)
    }
  }

  private def getConnection(): Connection = {
    DriverManager.getConnection(dburl, user, password)
  }

  private def buildStatement(connection: Connection, query: String, params: Any*): PreparedStatement = {
    val statement = connection.prepareStatement(expandParams(query, params: _*))
    var index = 0
    for (param <- params) {
      index += 1
      param match {
        case str: String => bindParam(statement, index, param)
        case seq: Seq[_] => {
          for (p <- seq) {
            bindParam(statement, index, p)
            index += 1
          }
        }
        case _ => bindParam(statement, index, param)
      }
    }
    return statement
  }

  private def expandParams(query: String, params: Any*): String = {
    val buf = new StringBuilder()
    var i = 0
    var start = 0
    var end = query.indexOf('?')
    while (end != -1) {
      buf.append(query.substring(start, end + 1))
      if (i >= params.length) {
        throw new IllegalArgumentException("Not enough params")
      }
      params(i) match {
        case str: String =>
        case seq: Seq[_] => for (n <- 1 until seq.size) buf.append(",?")
        case _ =>
      }
      start = end + 1
      end = query.indexOf('?', start)
      i += 1
    }
    buf.append(query.substring(start))
    buf.toString
  }

  private def bindParam(statement: PreparedStatement, index: Int, param: Any) {
    param match {
      case null =>
        statement.setNull(index, java.sql.Types.NULL)
      case s: String =>
        statement.setString(index, s)
      case b: Byte =>
        statement.setByte(index, b)
      case i: Int =>
        statement.setInt(index, i)
      case l: Long =>
        statement.setLong(index, l)
      case d: Double =>
        statement.setDouble(index, d)
      case b: Boolean =>
        statement.setBoolean(index, b)
      case a: Array[Byte] =>
        statement.setBytes(index, a)
      case d: Date =>
        statement.setDate(index, d)
      case t: Timestamp =>
        statement.setTimestamp(index, t)
      case _ => throw new IllegalArgumentException(
        "Unsupported query parameter type: " +
                param.asInstanceOf[Object].getClass.getName)
    }
  }

  private def closeConnection(connection: Connection) {
    if (connection != null) {
      try {
        connection.close()
      } catch {
        case e: Exception =>
      }
    }
  }

  private def closeStatement(statement: Statement) {
    if (statement != null) {
      try {
        statement.close()
      } catch {
        case e: Exception =>
      }
    }
  }

  private def closeResultSet(resultSet: ResultSet) {
    if (resultSet != null) {
      try {
        resultSet.close()
      } catch {
        case e: Exception =>
      }
    }
  }
}

object Database {
  def apply(dburl: String, user: String, password: String) = {
    new Database(dburl, user, password)
  }


  /**
   * Enclose string in single quotes and escape quotes in the string.
   */
  def quote(string: String): String = {
    "'" + string.replace("'", "''") + "'"
  }
}
