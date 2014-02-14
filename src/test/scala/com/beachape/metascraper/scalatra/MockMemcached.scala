package com.beachape.metascraper.scalatra

import shade.memcached.{Codec, Memcached}
import scala.concurrent.duration.Duration
import scala.concurrent.Future
import scala.collection.mutable

/**
 * Quick and dirty MockMemcached Connection
 */
class MockMemcached extends Memcached {

  @volatile var internalMap = mutable.Map[String, Array[Byte]]()

  def set[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Unit] = {
    internalMap(key) = codec.serialize(value)
    Future.successful(Unit)
  }
  def get[T](key: String)(implicit codec: Codec[T]): Future[Option[T]] = Future.successful(internalMap.get(key).map( codec.deserialize(_)))
  def add[T](key: String, value: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] = Future.successful(true)
  def delete(key: String): Future[Boolean] = {
    internalMap.remove(key)
    Future.successful(true)
  }

  // These are even more garbage than the ones above
  def close(): Unit = {}
  override def transformAndGet[T](key: String, exp: Duration)(cb: Option[T] => T)(implicit codec: Codec[T]): Future[T] =
    Future.successful(codec.deserialize(internalMap.getOrElse(key, "hello".getBytes("UTF-8"))))
  override def getAndTransform[T](key: String, exp: Duration)(cb: (Option[T]) => T)(implicit codec: Codec[T]): Future[Option[T]] =
    // Not reusing the one above because we don't want to have to require an execution context to fulfull map
    Future.successful(Some(codec.deserialize(internalMap.getOrElse(key, "hello".getBytes("UTF-8")))))
  override def compareAndSet[T](key: String, expecting: Option[T], newValue: T, exp: Duration)(implicit codec: Codec[T]): Future[Boolean] =
    Future.successful(true)
}
