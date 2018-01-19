package object client {
  type ErrorOr[T] = Either[String, T]
}
