package ninthdrug.command

import java.io._
import ninthdrug.config.Config
import ninthdrug.fullhostname
import ninthdrug.shorthostname

class Lazarus(
  val command: Command,
  val host: String,
  val user: String
) extends Command {

  def result(): Result = {
    var out: ObjectOutputStream = null
    try {
      val file = File.createTempFile("laz", ".rip")
      val path = file.getPath
      out = new ObjectOutputStream(new FileOutputStream(file))
      out.writeObject(command)
      val libdir = Config.get("lib.dir")
      val libs = Command.shlines("ls " + libdir).map(
        libdir + "/" + _
      ).mkString(":")

      val sshcmd =
        Config.get("scala.home") +
        "/bin/scala" +
        " -cp " + libs +
        " ninthdrug.command.Rez " +
        path

      Chain(
        Put(path, host, user, path),
        Ssh(sshcmd, host, user),
        Exec("rm -f " + path)
      ).result
    } catch {
      case e: Exception => Result(e)
    } finally {
      try {
        if (out != null) out.close()
      } catch {
        case e: Exception =>
      }
    }
  }

  override def toString(): String = {
    "Lazarus(" + command + ", " + host + ", " + user + ")"
  }
}

object Lazarus {
  def apply(
    command: Command,
    host: String,
    user: String = null
  ): Command = {
    if (
      host == "localhost" ||
      host == "localhost.localdomain" ||
      host == fullhostname ||
      host == shorthostname
    ) {
      command
    } else {
      new Lazarus(command, host, user)
    }
  }
}
