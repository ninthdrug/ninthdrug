package ninthdrug.http

import org.scalatest.FunSuite

class HttpSuite extends FunSuite {
  test("makeClassName") {
    assert(ScriptCache.makeClassName("localhost", "/index.taak") ==
      "ninthdrug.script.localhost.index")
  }
}
