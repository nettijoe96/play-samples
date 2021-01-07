import controllers.GreeterController
import play.api.i18n.{I18nComponents, Langs}
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.ControllerComponents
import services.ServicesModule
import play.modules.reactivemongo.ReactiveMongoApiComponents


trait GreetingModule extends ServicesModule {
  self: I18nComponents with ReactiveMongoApiComponents with AhcWSComponents =>


  import com.softwaremill.macwire._

  lazy val greeterController = wire[GreeterController]

  def langs: Langs

  def controllerComponents: ControllerComponents
}
