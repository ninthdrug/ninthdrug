package ninthdrug.command

import java.io._

class Install(
  val installer: String,
  val host: String,
  val user: String
) extends Command {

  def result(): Result = {
    val file = new File(installer).getCanonicalFile
    Chain(
      Put(installer, host, user, installer),
      Ssh("cd " + file.getParent + "; ./" + file.getName, host, user)
    ).result
  }

  override def toString(): String = {
    "Install(" + installer + ", " + host + ", " + user + ")"
  }
}

object Install {
  def apply(
    installer: String,
    host: String = "localhost",
    user: String = null
  ) = new Install(installer, host, user)
}
