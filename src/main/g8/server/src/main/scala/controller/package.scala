import akka.util.ByteString
import io.circe.Encoder
import play.api.http.{ContentTypes, Writeable}

package object controller {
  implicit def fromJsonWritable[T](implicit e: Encoder[T]): Writeable[T] = new Writeable[T](
    (v: T) => ByteString(e(v).toString),
    Some(ContentTypes.JSON)
  )
}
