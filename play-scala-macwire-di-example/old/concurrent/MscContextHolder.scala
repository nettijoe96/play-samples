package common.concurrent

import scala.util.DynamicVariable


object MscContextHolder {

  object Keys extends Enumeration {
    type Key = Value
    val Token = Value
  }

  private val context = Map(Keys.Token -> new DynamicVariable[Option[String]](None))

  def get(key: Keys.Key): DynamicVariable[Option[String]] = context.getOrElse(key, throw new IllegalArgumentException(s"'$key' is not available in the context"))

  def getValue(key: Keys.Key): Option[String] = get(key).value

  def setValue(key: Keys.Key, value: Option[String]): Unit = get(key).value = value

  def setValues(map: Map[Keys.Key, Option[String]]): Unit = for ((key, value) <- map) setValue(key, value)

  def copyValues: Map[Keys.Key, Option[String]] = context.transform((_, dynVar) => dynVar.value)

}