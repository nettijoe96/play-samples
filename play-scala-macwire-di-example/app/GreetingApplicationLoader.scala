
import _root_.controllers.{Assets, AssetsComponents}
import com.softwaremill.macwire._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n.I18nComponents
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import play.modules.reactivemongo.{ReactiveMongoApiComponents, ReactiveMongoApiFromContext, ReactiveMongoComponents}
import router.Routes

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class GreetingApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = new GreetingComponents(context).application
}

class GreetingComponents(context: Context) extends ReactiveMongoApiFromContext(context)
  with GreetingModule
  with AssetsComponents
  with I18nComponents
  with AhcWSComponents
  with play.filters.HttpFiltersComponents {

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }
  override lazy val httpErrorHandler: ErrorHandler = wire[ErrorHandler]
  lazy val assets : Assets = new Assets(httpErrorHandler)
  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}

// //class GreetingComponents(context: Context) extends BuiltInComponentsFromContext(context)
// class GreetingComponents(val context: Context)
//   extends ReactiveMongoApiFromContext(context)
//     with GreetingModule
//     with I18nComponents
//     with AhcWSComponents
//     with ReactiveMongoComponents {
// //    with AssetsComponents
// //    with play.filters.HttpFiltersComponents {

//   // val dbName = "entryDb"
//   // val name = "entries"
//   // set up logger
// //  LoggerConfigurator(context.environment.classLoader).foreach {
// //    _.configure(context.environment, context.initialConfiguration, Map.empty)
// //  }

//   lazy val routerOption = Some(router)
//   lazy val assets : Assets = new Assets(httpErrorHandler)
//   // Routes is a generated class
//   override def router: Router =
//     new Routes(httpErrorHandler, assets)
//   //override lazy val httpErrorHandler: ErrorHandler = wire[ErrorHandler]
//   //lazy val assets : Assets = new Assets(httpErrorHandler)
// //  lazy val router: Router = {
// //    lazy val prefix = "/"
// //    wire[Routes]
// //  }
// }
