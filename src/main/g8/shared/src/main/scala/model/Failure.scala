package model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Failure(message: String)
