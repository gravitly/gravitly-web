package ly.gravit.web

import play.api._
import ly.gravit.web.dao.parseapi.AccountDaoImpl
import ly.gravit.web.auth.{Administrator, Account}

/**
 * Created with IntelliJ IDEA.
 * User: ginduc
 * Date: 8/22/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Gravitly Web has started")

    if (Play.isDev(Play.current)) {
      if (Logger.isDebugEnabled) {
        Logger.debug("Entering dev mode")
      }
      //initDevMode
    }
  }

  override def onStop(app: Application) {
    Logger.info("Gravitly Web shut down...")
  }

  private def initDevMode = {
    AccountDaoImpl.getByEmail("admin@gravitly.com") match {
      case Some(account) => /* noop */
      case None => {
        val admin = new Account(null, "ned@flanders.com", "password", "Ned Flanders", Administrator)
        AccountDaoImpl.create(admin) match {
          case Some(id) => {
            if (Logger.isDebugEnabled) {
              Logger.debug("Admin ID: %s".format(id))
            }
          }
          case None => throw new IllegalStateException("Cannot create default admin account")
        }
      }
    }
  }
}
