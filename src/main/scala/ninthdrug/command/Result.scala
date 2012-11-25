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

package ninthdrug.command

import java.io._

abstract class Result extends Serializable {
  def status: Int
  def output: String
  def error: String
  def exception: Option[Exception]

  def print() {
    if (output != null && output != "") {
      System.out.println(output)
    }
    if (error != null && error != "") {
      System.out.println(error)
    }
    exception match {
      case Some(e) => e.printStackTrace
      case None =>
    }
  }

  def toInt() = status

  def toBoolean(): Boolean = {
    status == 0 && exception == None
  }

  def unary_!(): Boolean = {
    status != 0 || exception != None
  }

  override def equals(obj: Any) = obj match {
    case that: Result =>
      this.status == that.status &&
      this.output == that.output &&
      this.error == that.error &&
      this.exception == that.exception
    case _ => false
  }

  override def hashCode(): Int = {
    var code = 4 * 41 + status
    code = code * 41 + (if (output == null) 0 else output.hashCode())
    code = code * 41 + (if (error == null) 0 else error.hashCode())
    code = code * 41 + (if (exception == None) 0 else exception.get.hashCode())
    code
  }
}

class SimpleResult(
  _status: Int,
  _output: String,
  _error: String,
  _exception: Option[Exception]
) extends Result {
  def status = _status
  def output = _output
  def error = _error
  def exception = _exception
}

class CompoundResult(results: Traversable[Result]) extends Result {
  def status = results.map(_.status).find(_ != 0).getOrElse(0)
  def output = results.map(_.output.trim).filter(_ != "").mkString("\n").trim
  def error = results.map(_.error.trim).filter(_ != "").mkString("\n").trim
  def exception = results.flatMap(_.exception).find(_ => true)
}

object Result {
  val OK = new SimpleResult(0, "", "", None)

  def apply(): Result = OK

  def apply(status: Int): Result = new SimpleResult(status, "", "", None)

  def apply(output: String): Result = new SimpleResult(0, output, "", None)

  def apply(status: Int, string: String) = {
    if (status == 0) {
      new SimpleResult(status, string, "", None)
    } else {
      new SimpleResult(status, "", string, None)
    }
  }

  def apply(exception: Exception) = {
    new SimpleResult(1, "", "", Some(exception))
  }

  def apply(message: String, exception: Exception) = {
    new SimpleResult(1, "", message, Some(exception))
  }

  def apply(
    status: Int,
    output: String,
    error: String
  ): Result = {
    new SimpleResult(status, output, error, None)
  }

  def apply(
    status: Int,
    output: String,
    error: String,
    exception: Exception
  ): Result = {
    if (exception == null) {
       new SimpleResult(status, output, error, None)
    } else {
       new SimpleResult(status, output, error, Some(exception))
    }
  }

  def apply(results: Traversable[Result]): Result = {
    new CompoundResult(results)
  }

  def apply(process: Process): Result = {
    var stat = 0
    var out = ""
    var err = ""
    var ex: Option[Exception] = None
    try {
      stat = process.waitFor()
      val buf = new StringBuilder()
      var reader = new BufferedReader(new InputStreamReader(process.getInputStream))
      var line = reader.readLine()
      while (line != null) {
        buf.append(line).append("\n")
        line = reader.readLine()
      }
      reader.close()
      out = buf.toString.trim
      reader = new BufferedReader(new InputStreamReader(process.getErrorStream))
      buf.clear()
      line = reader.readLine()
      while (line != null) {
        buf.append(line).append("\n")
        line = reader.readLine()
      }
      reader.close()
      err = buf.toString.trim
    } catch {
      case e: Exception => ex = Some(e)
    }
    new SimpleResult(stat, out, err, ex)
  }
}
