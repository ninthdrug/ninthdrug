package ninthdrug.config

import java.io._
import org.scalatest.FunSuite

class ConfigSuite extends FunSuite {

  test("read") {
    val config = """
# a comment
domain_root=/data/domains  # another comment
my_home=/home/benjanmin
answer=42
name=Tom
name=Dick
name=Harry
permitted.host=localhost
permitted.host=doom-server
"""
    val temp = File.createTempFile("test", ".conf")
    temp.deleteOnExit()
    val writer = new FileWriter(temp)
    writer.write(config)
    writer.close()
    Config.clear()
    Config.read(temp)

    assert(Config.get("domain_root", "") == "/data/domains")
    assert(Config.get("foobar", "xyz") == "xyz")
    assert(Config.getInt("answer") == 42)
    intercept[ConfigError] {
      Config.get("undefined")
    }
    assert(Config.all("name") == Set("Tom","Dick","Harry"))
    assert(Config.all("permitted.host") == Set("localhost","doom-server"))
  }
}
