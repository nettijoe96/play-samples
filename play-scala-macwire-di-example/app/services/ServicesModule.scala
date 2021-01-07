package services

import play.api.i18n.I18nComponents
import play.api.libs.ws.ahc.AhcWSComponents
import play.modules.reactivemongo.ReactiveMongoApiComponents
import repository.BaseRepository

trait ServicesModule {
  self: I18nComponents with ReactiveMongoApiComponents with AhcWSComponents =>  //if this works, change to extending ServicesModule and test

  import com.softwaremill.macwire._

  lazy val greetingService = wire[GreetingService]
  lazy val repository = wire[BaseRepository]

}
