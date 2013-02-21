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
package ninthdrug.json

import collection.immutable.ListMap
import scala.language.implicitConversions

/**
 * A JsonEncoder[T] encodes values of type T to a JasonValue.
 */
trait JsonEncoder[T] { def encode(t: T): JsonValue }

object JsonEncoder {

  implicit def bool2Json(b: Boolean): JsonBoolean = JsonBoolean(b)
  implicit def byte2Json(n: Byte): JsonNumber = JsonNumber(n)
  implicit def short2Json(n: Short): JsonNumber = JsonNumber(n)
  implicit def int2Json(n: Int): JsonNumber = JsonNumber(n)
  implicit def long2Json(n: Long): JsonNumber = JsonNumber(n)
  implicit def float2Json(n: Float): JsonNumber = JsonNumber(n)
  implicit def double2Json(n: Double): JsonNumber = JsonNumber(n)
  implicit def bigDecimal2Json(n: BigDecimal): JsonNumber = JsonNumber(n)
  implicit def bigInt2Json(n: BigInt): JsonNumber  = JsonNumber(BigDecimal(n))
  implicit def char2Json(c: Char): JsonString = JsonString(c.toString)
  implicit def string2Json(s: String): JsonString = JsonString(s)
  implicit def seq2Json[T](seq: Seq[T])(
    implicit encoder: JsonEncoder[T]
  ): JsonArray = JsonArray((seq map (encoder.encode(_))):_*)

  implicit def pimp[T](t: T) = new Json(t)

  /**
   * Implicitly converts a function of type T => JsonValue to a JsonEncoder[T].
   */
  implicit def FunctionJsonEncoder[T](f: T => JsonValue): JsonEncoder[T] = {
    new JsonEncoder[T] {
      def encode(t: T) = f(t)
    }
  }

  implicit object BooleanJsonEncoder extends JsonEncoder[Boolean] {
    def encode(bool: Boolean): JsonValue = JsonBoolean(bool)
  }

  implicit object ByteJsonEncoder extends JsonEncoder[Byte] {
    def encode(n: Byte): JsonValue = JsonNumber(n)
  } 

  implicit object ShortJsonEncoder extends JsonEncoder[Short] {
    def encode(n: Short): JsonValue = JsonNumber(n)
  } 

  implicit object IntJsonEncoder extends JsonEncoder[Int] {
    def encode(n: Int): JsonValue = JsonNumber(n)
  } 

  implicit object LongJsonEncoder extends JsonEncoder[Long] {
    def encode(n: Long): JsonValue = JsonNumber(n)
  } 

  implicit object FloatJsonEncoder extends JsonEncoder[Float] {
    def encode(n: Float): JsonValue = JsonNumber(n)
  } 

  implicit object DoubleJsonEncoder extends JsonEncoder[Double] {
    def encode(n: Double): JsonValue = JsonNumber(n)
  } 

  implicit object BigDecimalJsonEncoder extends JsonEncoder[BigDecimal] {
    def encode(n: BigDecimal): JsonValue = JsonNumber(n)
  } 

  implicit object BigIntJsonEncoder extends JsonEncoder[BigInt] {
    def encode(n: BigInt): JsonValue = JsonNumber(BigDecimal(n))
  } 

  implicit object CharJsonEncoder extends JsonEncoder[Char] {
    def encode(c: Char): JsonValue = JsonString(c.toString)
  } 

  implicit object StringJsonEncoder extends JsonEncoder[String] {
    def encode(s: String): JsonValue = JsonString(s)
  } 

  implicit object SymbolJsonEncoder extends JsonEncoder[Symbol] {
    def encode(x: Symbol): JsonValue = JsonString(x.name)
  } 

  implicit def mapJsonEncoder[T](implicit encoder: JsonEncoder[T]) = {
    new JsonEncoder[Map[String,T]] {
      def encode(m: Map[String,T]) = {
        val seq = m.toSeq map { case (k,v) => (k, encoder.encode(v)) }
        JsonObject(seq:_*)
      }
    }
  }

  implicit def arrayJsonEncoder[T](
    implicit encoder: JsonEncoder[T], m: Manifest[T]
  ): JsonEncoder[Array[T]] = {
    new JsonEncoder[Array[T]] {
      def encode(array: Array[T]) =
        JsonArray((array map (encoder.encode(_))):_*)
    }
  }

  implicit def seqJsonEncoder[T](
    implicit encoder: JsonEncoder[T]
  ): JsonEncoder[Seq[T]] = {
    new JsonEncoder[Seq[T]] {
      def encode(seq: Seq[T]) = JsonArray((seq map (encoder.encode(_))):_*)
    }
  }

  implicit def listJsonEncoder[T](
    implicit encoder: JsonEncoder[T]
  ): JsonEncoder[List[T]] = {
    new JsonEncoder[List[T]] {
      def encode(list: List[T]) = JsonArray((list map (encoder.encode(_))):_*)
    }
  }

  implicit def tuple2JasonEncoder[A,B](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B]
  ): JsonEncoder[(A,B)] = {
    new JsonEncoder[(A,B)] {
      def encode(t: (A,B)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2)
      )
    }
  }

  implicit def tuple3JasonEncoder[A,B,C](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B],
    c3: JsonEncoder[C]
  ): JsonEncoder[(A,B,C)] = {
    new JsonEncoder[(A,B,C)] {
      def encode(t: (A,B,C)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2),
        c3.encode(t._3)
      )
    }
  }

  implicit def tuple4JasonEncoder[A,B,C,D](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B],
    c3: JsonEncoder[C],
    c4: JsonEncoder[D]
  ): JsonEncoder[(A,B,C,D)] = {
    new JsonEncoder[(A,B,C,D)] {
      def encode(t: (A,B,C,D)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2),
        c3.encode(t._3),
        c4.encode(t._4)
      )
    }
  }

  implicit def tuple5JasonEncoder[A,B,C,D,E](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B],
    c3: JsonEncoder[C],
    c4: JsonEncoder[D],
    c5: JsonEncoder[E]
  ): JsonEncoder[(A,B,C,D,E)] = {
    new JsonEncoder[(A,B,C,D,E)] {
      def encode(t: (A,B,C,D,E)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2),
        c3.encode(t._3),
        c4.encode(t._4),
        c5.encode(t._5)
      )
    }
  }

  implicit def tuple6JasonEncoder[A,B,C,D,E,F](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B],
    c3: JsonEncoder[C],
    c4: JsonEncoder[D],
    c5: JsonEncoder[E],
    c6: JsonEncoder[F]
  ): JsonEncoder[(A,B,C,D,E,F)] = {
    new JsonEncoder[(A,B,C,D,E,F)] {
      def encode(t: (A,B,C,D,E,F)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2),
        c3.encode(t._3),
        c4.encode(t._4),
        c5.encode(t._5),
        c6.encode(t._6)
      )
    }
  }

  implicit def tuple7JasonEncoder[A,B,C,D,E,F,G](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B],
    c3: JsonEncoder[C],
    c4: JsonEncoder[D],
    c5: JsonEncoder[E],
    c6: JsonEncoder[F],
    c7: JsonEncoder[G]
  ): JsonEncoder[(A,B,C,D,E,F,G)] = {
    new JsonEncoder[(A,B,C,D,E,F,G)] {
      def encode(t: (A,B,C,D,E,F,G)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2),
        c3.encode(t._3),
        c4.encode(t._4),
        c5.encode(t._5),
        c6.encode(t._6),
        c7.encode(t._7)
      )
    }
  }

  implicit def tuple8JasonEncoder[A,B,C,D,E,F,G,H](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B],
    c3: JsonEncoder[C],
    c4: JsonEncoder[D],
    c5: JsonEncoder[E],
    c6: JsonEncoder[F],
    c7: JsonEncoder[G],
    c8: JsonEncoder[H]
  ): JsonEncoder[(A,B,C,D,E,F,G,H)] = {
    new JsonEncoder[(A,B,C,D,E,F,G,H)] {
      def encode(t: (A,B,C,D,E,F,G,H)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2),
        c3.encode(t._3),
        c4.encode(t._4),
        c5.encode(t._5),
        c6.encode(t._6),
        c7.encode(t._7),
        c8.encode(t._8)
      )
    }
  }

  implicit def tuple9JasonEncoder[A,B,C,D,E,F,G,H,I](implicit
    c1: JsonEncoder[A],
    c2: JsonEncoder[B],
    c3: JsonEncoder[C],
    c4: JsonEncoder[D],
    c5: JsonEncoder[E],
    c6: JsonEncoder[F],
    c7: JsonEncoder[G],
    c8: JsonEncoder[H],
    c9: JsonEncoder[I]
  ): JsonEncoder[(A,B,C,D,E,F,G,H,I)] = {
    new JsonEncoder[(A,B,C,D,E,F,G,H,I)] {
      def encode(t: (A,B,C,D,E,F,G,H,I)) = JsonArray(
        c1.encode(t._1),
        c2.encode(t._2),
        c3.encode(t._3),
        c4.encode(t._4),
        c5.encode(t._5),
        c6.encode(t._6),
        c7.encode(t._7),
        c8.encode(t._8),
        c9.encode(t._9)
      )
    }
  }
}

