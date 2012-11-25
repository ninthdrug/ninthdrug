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

/**
 * A JsonDecoder[T] converts a JsonValue to a value of type T.
 */
trait JsonDecoder[T] { def decode(value: JsonValue): T }

object JsonDecoder {
  private def error(msg: String) = throw new JsonError(msg)
   
  implicit object BooleanJsonDecoder extends JsonDecoder[Boolean] {
    def decode(value: JsonValue): Boolean = value match {
      case JsonBoolean(bool) => bool
      case x => error("Expected JsonBoolean instead of " + x)
    }
  }

  implicit object ByteJsonDecoder extends JsonDecoder[Byte] {
    def decode(value: JsonValue): Byte = value match {
      case JsonNumber(n) => n.byteValue
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object ShortJsonDecoder extends JsonDecoder[Short] {
    def decode(value: JsonValue): Short = value match {
      case JsonNumber(n) => n.shortValue
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object IntJsonDecoder extends JsonDecoder[Int] {
    def decode(value: JsonValue): Int = value match {
      case JsonNumber(n) => n.intValue
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object LongJsonDecoder extends JsonDecoder[Long] {
    def decode(value: JsonValue): Long = value match {
      case JsonNumber(n) => n.longValue
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object FloatJsonDecoder extends JsonDecoder[Float] {
    def decode(value: JsonValue): Float = value match {
      case JsonNumber(n) => n.floatValue
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object DoubleJsonDecoder extends JsonDecoder[Double] {
    def decode(value: JsonValue): Double = value match {
      case JsonNumber(n) => n.doubleValue
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object BigDecimalJsonDecoder extends JsonDecoder[BigDecimal] {
    def decode(value: JsonValue): BigDecimal = value match {
      case JsonNumber(n) => n
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object BigIntJsonDecoder extends JsonDecoder[BigInt] {
    def decode(value: JsonValue): BigInt = value match {
      case JsonNumber(n) => n.toBigInt
      case x => error("Expected JsonNumber instead of " + x)
    }
  }

  implicit object CharJsonDecoder extends JsonDecoder[Char] {
    def decode(value: JsonValue): Char = value match {
      case JsonString(s) if s.length == 1 => s.charAt(0)
      case x => error("Expected JsonString of length 1 instead of " + x)
    }
  }

  implicit object StringJsonDecoder extends JsonDecoder[String] {
    def decode(value: JsonValue): String = value match {
      case JsonString(s) => s
      case JsonNull => null
      case x => error("Expected JsonString instead of " + x)
    }
  }

  implicit object SymbolJsonDecoder extends JsonDecoder[Symbol] {
    def decode(value: JsonValue): Symbol = value match {
      case JsonString(s) => Symbol(s)
      case x => error("Expected JsonString instead of " + x)
    }
  }

  implicit def mapJsonDecoder[T](implicit decoder: JsonDecoder[T]) = {
    new JsonDecoder[Map[String,T]] {
      def decode(value: JsonValue): Map[String,T] = value match {
        case obj: JsonObject => obj.entries map {
          case (k, v) => (k, decoder.decode(v))
        }
        case x => error("Expected JsonObject instead of " + x)
      }
    }
  }

  implicit def arrayJsonDecoder[T](
    implicit decoder: JsonDecoder[T],
    m: Manifest[T]
  ) = {
    new JsonDecoder[Array[T]] {
      def decode(value: JsonValue): Array[T] = value match {
        case a: JsonArray => (a.vector map (decoder.decode(_))).toArray[T]
        case x => error("Expected JsonArray instead of " + x)
      }
    }
  }

  implicit def listJsonDecoder[T](implicit decoder: JsonDecoder[T]) = {
    new JsonDecoder[List[T]] {
      def decode(value: JsonValue): List[T] = value match {
        case a: JsonArray => a.vector.toList map (decoder.decode(_))
        case x => error("Expected JsonArray instead of " + x)
      }
    }
  }

  implicit def seqJsonDecoder[T](implicit decoder: JsonDecoder[T]) = {
    new JsonDecoder[Seq[T]] {
      def decode(value: JsonValue): Seq[T] = value match {
        case a: JsonArray => a.vector map (decoder.decode(_))
        case x => error("Expected JsonArray instead of " + x)
      }
    }
  }

  implicit def tuple2JasonDecoder[A,B](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B]
  ): JsonDecoder[(A,B)] = {
    new JsonDecoder[(A,B)] {
      def decode(value: JsonValue): (A,B) = value match {
        case JsonArray(a,b) => (
          c1.decode(a),
          c2.decode(b)
        )
        case x => error("Expected JsonArray with 2 elements instead of " + x)
      }
    }
  }

  implicit def tuple3JasonDecoder[A,B,C](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B],
    c3: JsonDecoder[C]
  ): JsonDecoder[(A,B,C)] = {
    new JsonDecoder[(A,B,C)] {
      def decode(value: JsonValue): (A,B,C) = value match {
        case JsonArray(a,b,c) => (
          c1.decode(a),
          c2.decode(b),
          c3.decode(c)
        )
        case x => error("Expected JsonArray with 3 elements instead of " + x)
      }
    }
  }

  implicit def tuple4JasonDecoder[A,B,C,D](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B],
    c3: JsonDecoder[C],
    c4: JsonDecoder[D]
  ): JsonDecoder[(A,B,C,D)] = {
    new JsonDecoder[(A,B,C,D)] {
      def decode(value: JsonValue): (A,B,C,D) = value match {
        case JsonArray(a,b,c,d) => (
          c1.decode(a),
          c2.decode(b),
          c3.decode(c),
          c4.decode(d)
        )
        case x => error("Expected JsonArray with 4 elements instead of " + x)
      }
    }
  }

  implicit def tuple5JasonDecoder[A,B,C,D,E](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B],
    c3: JsonDecoder[C],
    c4: JsonDecoder[D],
    c5: JsonDecoder[E]
  ): JsonDecoder[(A,B,C,D,E)] = {
    new JsonDecoder[(A,B,C,D,E)] {
      def decode(value: JsonValue): (A,B,C,D,E) = value match {
        case JsonArray(a,b,c,d,e) => (
          c1.decode(a),
          c2.decode(b),
          c3.decode(c),
          c4.decode(d),
          c5.decode(e)
        )
        case x => error("Expected JsonArray with 5 elements instead of " + x)
      }
    }
  }

  implicit def tuple6JasonDecoder[A,B,C,D,E,F](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B],
    c3: JsonDecoder[C],
    c4: JsonDecoder[D],
    c5: JsonDecoder[E],
    c6: JsonDecoder[F]
  ): JsonDecoder[(A,B,C,D,E,F)] = {
    new JsonDecoder[(A,B,C,D,E,F)] {
      def decode(value: JsonValue): (A,B,C,D,E,F) = value match {
        case JsonArray(a,b,c,d,e,f) => (
          c1.decode(a),
          c2.decode(b),
          c3.decode(c),
          c4.decode(d),
          c5.decode(e),
          c6.decode(f)
        )
        case x => error("Expected JsonArray with 6 elements instead of " + x)
      }
    }
  }

  implicit def tuple7JasonDecoder[A,B,C,D,E,F,G](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B],
    c3: JsonDecoder[C],
    c4: JsonDecoder[D],
    c5: JsonDecoder[E],
    c6: JsonDecoder[F],
    c7: JsonDecoder[G]
  ): JsonDecoder[(A,B,C,D,E,F,G)] = {
    new JsonDecoder[(A,B,C,D,E,F,G)] {
      def decode(value: JsonValue): (A,B,C,D,E,F,G) = value match {
        case JsonArray(a,b,c,d,e,f,g) => (
          c1.decode(a),
          c2.decode(b),
          c3.decode(c),
          c4.decode(d),
          c5.decode(e),
          c6.decode(f),
          c7.decode(g)
        )
        case x => error("Expected JsonArray with 7 elements instead of " + x)
      }
    }
  }

  implicit def tuple8JasonDecoder[A,B,C,D,E,F,G,H](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B],
    c3: JsonDecoder[C],
    c4: JsonDecoder[D],
    c5: JsonDecoder[E],
    c6: JsonDecoder[F],
    c7: JsonDecoder[G],
    c8: JsonDecoder[H]
  ): JsonDecoder[(A,B,C,D,E,F,G,H)] = {
    new JsonDecoder[(A,B,C,D,E,F,G,H)] {
      def decode(value: JsonValue): (A,B,C,D,E,F,G,H) = value match {
        case JsonArray(a,b,c,d,e,f,g,h) => (
          c1.decode(a),
          c2.decode(b),
          c3.decode(c),
          c4.decode(d),
          c5.decode(e),
          c6.decode(f),
          c7.decode(g),
          c8.decode(h)
        )
        case x => error("Expected JsonArray with 8 elements instead of " + x)
      }
    }
  }

  implicit def tuple9JasonDecoder[A,B,C,D,E,F,G,H,I](implicit
    c1: JsonDecoder[A],
    c2: JsonDecoder[B],
    c3: JsonDecoder[C],
    c4: JsonDecoder[D],
    c5: JsonDecoder[E],
    c6: JsonDecoder[F],
    c7: JsonDecoder[G],
    c8: JsonDecoder[H],
    c9: JsonDecoder[I]
  ): JsonDecoder[(A,B,C,D,E,F,G,H,I)] = {
    new JsonDecoder[(A,B,C,D,E,F,G,H,I)] {
      def decode(value: JsonValue): (A,B,C,D,E,F,G,H,I) = value match {
        case JsonArray(a,b,c,d,e,f,g,h,i) => (
          c1.decode(a),
          c2.decode(b),
          c3.decode(c),
          c4.decode(d),
          c5.decode(e),
          c6.decode(f),
          c7.decode(g),
          c8.decode(h),
          c9.decode(i)
        )
        case x => error("Expected JsonArray with 9 elements instead of " + x)
      }
    }
  }
}

