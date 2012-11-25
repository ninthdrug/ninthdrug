package ninthdrug.cms

case class Page(
  pageid: Int,
  websiteid: Int,
  url: String,
  name: String,
  description: String,
  layoutid: Int
)
