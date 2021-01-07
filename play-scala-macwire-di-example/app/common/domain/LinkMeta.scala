package common.domain

case class LinkMeta(total         : Int         = 0,
                    prevOffset    : Option[Int] = None,
                    nextOffset    : Option[Int] = None,
                    limit         : Int         = 0)