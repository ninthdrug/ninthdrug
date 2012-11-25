package ninthdrug.json

case class Item(id: String, desc: String, qty: Int)

object Item {
  import ninthdrug.json.JsonEncoder._

  implicit object ItemJsonEncoder extends JsonEncoder[Item] {
    def encode(t: Item): JsonValue = JsonObject(
      "id" -> t.id,
      "desc" -> t.desc,
      "qty" -> t.qty
    )
  }

  implicit object ItemJsonDecoder extends JsonDecoder[Item] {
    def decode(value: JsonValue): Item = {
      try {
        val obj = value.asInstanceOf[JsonObject]
        val id = obj.entries("id").asInstanceOf[JsonString].value
        val desc = obj.entries("desc").asInstanceOf[JsonString].value
        val qty = obj.entries("qty").asInstanceOf[JsonNumber].value.intValue
        Item(id, desc, qty)
      } catch {
        case cce: ClassCastException =>
          throw new JsonError("Error decoding Item from " + value)
      }
    }
  }
}
