package ninthdrug.json

case class Name(first: String, last: String)

object Name {
  implicit object NameJsonEncoder extends JsonEncoder[Name] {
    def encode(t: Name): JsonValue = JsonObject(
      "first" -> JsonString(t.first),
      "last" -> JsonString(t.last)
    )
  }

  implicit object NameJsonDecoder extends JsonDecoder[Name] {
    def decode(value: JsonValue): Name = {
      try {
        val obj = value.asInstanceOf[JsonObject]
        val first = obj.entries("first").asInstanceOf[JsonString].value
        val last = obj.entries("last").asInstanceOf[JsonString].value
        Name(first, last)
      } catch {
        case cce: ClassCastException =>
          throw new JsonError("Error decoding Name from " + value)
      }
    }
  }
}
