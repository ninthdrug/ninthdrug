/*
 * Copyright 2008-2012 Trung Dinh, Thomas Heuring
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
package ninthdrug.cms

import java.sql._
import scala.collection.mutable.ListBuffer
import ninthdrug.config.Config
import ninthdrug.sql._
import ninthdrug.util.Json._

object CmsCache {
  private var db: Database = null

  private var _users = List[User]()
  private var _groups = List[Group]()
  private var _roles = List[Role]()
  private var _group_roles = List[(String,Int,String)]()
  private var _websites = List[Website]()
  private var _pages = List[Page]()
  private var _widgets = List[Widget]()
  private var _javascripts = List[Javascript]()
  private var _layouts = List[Layout]()

  private var _userMap = Map[String, User]()
  private var _groupMap = Map[String, Group]()
  private var _roleMap = Map[String, Role]()
  private var _websiteMap = Map[Int, Website]()
  private var _pageMap = Map[Int, Page]()
  private var _widgetMap = Map[Int, Widget]()
  private var _javascriptMap = Map[Int, Javascript]()
  private var _layoutMap = Map[Int, Layout]()
  

  load()

  def load() {
    val dburl = Config.get("ninthdrug.cms.dburl")
    val dbuser = Config.get("ninthdrug.cms.dbuser")
    val dbpass = Config.get("ninthdrug.cms.dbpassword")
    db = Database(dburl, dbuser, dbpass)

    _users = {
      db.list[User]("select * from users order by userid") {
        (rs: ResultSet) => User(
          rs.getString("userid"),
          rs.getString("name"),
          rs.getString("password"),
          rs.getString("email")
        )
      }
    }
    _userMap = Map() ++ (_users.map(user => (user.userid, user)))

    _groups = {
      db.list[Group]("select groupname from groups order by groupname") {
        (rs: ResultSet) => Group(
          rs.getString("groupname")
        )
      }
    }
    _groupMap = Map() ++ (_groups.map(group => (group.groupname, group)))

    _roles = {
      db.list[Role]("select * from roles order by rolename") {
        (rs: ResultSet) => Role(
          rs.getString("rolename")
        )
      }
    }
    _roleMap = Map() ++ (_roles.map(role => (role.rolename, role)))

    _group_roles = {
      db.list[(String,Int,String)]("select * from group_roles") {
        (rs: ResultSet) => (
          rs.getString("groupname"),
          rs.getInt("websiteid"),
          rs.getString("rolename")
        )
      }
    }

    _websites = {
      db.list[Website]("select * from websites") {
        (rs: ResultSet) => Website(
          rs.getInt("websiteid"),
          rs.getString("name"),
          rs.getString("description"),
          rs.getString("hostname"),
          rs.getInt("port")
        )
      }
    }
    _websiteMap = Map() ++ (_websites.map(website => (website.websiteid, website)))

    _pages = {
      db.list[Page]("select * from pages") {
        (rs: ResultSet) => Page(
          rs.getInt("pageid"),
          rs.getInt("websiteid"),
          rs.getString("url"),
          rs.getString("name"),
          rs.getString("description"),
          rs.getInt("layoutid")
        )
      }
    }
    _pageMap = Map() ++ (_pages.map(page => (page.pageid, page)))

    _widgets = {
      db.list[Widget]("select * from widgets") {
        (rs: ResultSet) => Widget(
          rs.getInt("widgetid"),
          rs.getString("name"),
          rs.getString("html")
        )
      }
    }
    _widgetMap = Map() ++ (_widgets.map(widget => (widget.widgetid, widget)))

    _javascripts = {
      db.list[Javascript]("select * from javascripts") {
        (rs: ResultSet) => Javascript(
          rs.getInt("javascriptid"),
          rs.getString("name"),
          rs.getString("javascript")
        )
      }
    }
    _javascriptMap = Map() ++ (_javascripts.map(javascript => (javascript.javascriptid, javascript)))

    _layouts = {
      db.list[Layout]("select * from layouts") {
        (rs: ResultSet) => Layout(
          rs.getInt("layoutid"),
          rs.getString("html"),
          rs.getString("csslink")
        )
      }
    }
    _layoutMap = Map() ++ (_layouts.map(layout => (layout.layoutid, layout)))

  }
 
  def users = _users
  def groups = _groups
  def roles = _roles
  def websites = _websites
  def pages = _pages
  def widgets = _widgets

  def getUser(userid: String) = _userMap(userid)

  def getGroup(name: String) = _groupMap(name)

  def getWebsite(id: Int) = _websiteMap(id)

  def getPage(id: Int) = _pageMap(id)

  def getWidget(id: Int) = _widgetMap(id)

  def getJavascript(id: Int) = _javascriptMap(id)
  
  def getLayout(id: Int) = _layoutMap(id)

  def hasUser(name : String) : Boolean = _userMap.contains(name)

  def hasWebsite(id : Int) : Boolean = _websiteMap.contains(id)
}
