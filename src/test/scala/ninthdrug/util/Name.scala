package ninthdrug.util

import org.scalatest.FunSuite

case class Name(first: String, last: String) {
  def json() =
    """{ "name": """ + Json.quote(first) +
    """, "last": """ + Json.quote(last) +
    """}"""
}
