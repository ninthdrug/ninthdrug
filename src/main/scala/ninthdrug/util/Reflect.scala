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
 *  Ninthdrug is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ninthdrug.  If not, see <http://www.gnu.org/licenses/>.
 */
package ninthdrug.util

import scala.language.implicitConversions
import scala.language.postfixOps
import scala.language.existentials

object Reflect {

  def hasMethod(obj: AnyRef, name: String, params: java.lang.Class[_]*): Boolean = {
    try {
      obj.getClass.getMethod(name, params: _*)
      true
    } catch {
      case _: NoSuchMethodException => false
    }
  }

  sealed abstract class WithType {
    val clazz: Class[_]
    val value: AnyRef
  }

  case class ValWithType(anyVal: AnyVal, clazz: Class[_]) extends WithType {
    lazy val value = toAnyRef(anyVal)
  }

  case class RefWithType(anyRef: AnyRef, clazz: Class[_]) extends WithType {
    val value = anyRef
  }

  implicit def refWithType[T <: AnyRef](x: T) = RefWithType(x, x.getClass)
  implicit def valWithType[T <: AnyVal](x: T) = ValWithType(x, getType(x))

  def getType(x: AnyVal): Class[_] = x match {
    case _: Byte => java.lang.Byte.TYPE
    case _: Short => java.lang.Short.TYPE
    case _: Int => java.lang.Integer.TYPE
    case _: Long => java.lang.Long.TYPE
    case _: Float => java.lang.Float.TYPE
    case _: Double => java.lang.Double.TYPE
    case _: Char => java.lang.Character.TYPE
    case _: Boolean => java.lang.Boolean.TYPE
    case _ => java.lang.Void.TYPE
  }

  def toAnyRef(x: AnyVal): AnyRef = x match {
    case x: Byte => Byte.box(x)
    case x: Short => Short.box(x)
    case x: Int => Int.box(x)
    case x: Long => Long.box(x)
    case x: Float => Float.box(x)
    case x: Double => Double.box(x)
    case x: Char => Char.box(x)
    case x: Boolean => Boolean.box(x)
    case x: Unit => x.asInstanceOf[AnyRef]
  }

  def make[T <: AnyRef](className: String)(args: WithType*)(implicit classLoader: ClassLoader): T = {
    val clazz: Class[T] = Class.forName(className, true, classLoader).asInstanceOf[Class[T]]
    val argTypes = args map (_.clazz) toArray
    val candidates = clazz.getConstructors filter {
      c => matchingTypes(c.getParameterTypes, argTypes)
    }
    val params = args map (_.value)
    candidates.head.newInstance(params: _*).asInstanceOf[T]
  }

  private def matchingTypes(
    declared: Array[Class[_]],
    actual: Array[Class[_]]
  ): Boolean = {
    declared.length == actual.length && (
      (declared zip actual) forall {
        case (declared, actual) => declared.isAssignableFrom(actual)
      }
    )
  }
}


