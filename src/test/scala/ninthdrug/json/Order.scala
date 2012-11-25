package ninthdrug.json

case class Order(id: String, items: Seq[Item])

object Order {
  import ninthdrug.json.JsonEncoder._
  import ninthdrug.json.Item._

  implicit object OrderJsonEncoder extends JsonEncoder[Order] {
    def encode(t: Order): JsonValue = JsonObject(
      "id" -> t.id,
      "items" -> t.items
    )
  }

  implicit object OrderJsonDecoder extends JsonDecoder[Order] {
    def decode(value: JsonValue): Order = {
      try {
        val obj = value.asInstanceOf[JsonObject]
        val id = obj.entries("id").asInstanceOf[JsonString].value
        val items = obj.entries("items").convertTo[Seq[Item]]
        Order(id, items)
      } catch {
        case cce: ClassCastException =>
          throw new JsonError("Error decoding Order from " + value)
      }
    }
  }
}
