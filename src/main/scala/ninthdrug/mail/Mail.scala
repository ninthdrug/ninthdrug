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
package ninthdrug.mail

import ninthdrug.config.Config
import ninthdrug.logging.Logger
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class DefaultAuthenticator(
  username: String,
  password: String
) extends Authenticator {
  override def getPasswordAuthentication(): PasswordAuthentication = {
    return new PasswordAuthentication(username, password)
  }
}

object Mail {
  private val log = Logger("ninthdrug.mail.Mail")
  private val username = Config.get("smtp.username", null)
  private val password = Config.get("smtp.password", null)

  val auth = if (username == null || password == null) {
    null
  } else {
    new DefaultAuthenticator(username, password)
  }

  def mail(from: String, to: String, subject: String, body: String) {
    val props = new Properties()
    val smtphost = Config.get("smtp.host", "localhost")
    props.put("mail.transport.protocol", "smtp")
    props.put("mail.smtp.host", smtphost)
    log.debug("mail.smtp.host: " +  smtphost)
    if (auth != null) {
      props.put("mail.smtp.auth", "true")
      log.debug("mail.smtp.auth: true")
      log.debug("mail.smtp.user: " + auth.getPasswordAuthentication.getUserName)
      log.debug("password: " + auth.getPasswordAuthentication.getPassword)
    }
    val session = Session.getInstance(props, auth)
    val msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress(from))
    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to))
    msg.setSubject(subject)
    msg.setText(body)
    Transport.send(msg)
  }
}       
