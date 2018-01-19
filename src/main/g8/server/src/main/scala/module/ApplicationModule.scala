package module

import javax.inject.Singleton

import com.google.inject.AbstractModule
import controller.ApplicationController
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ApplicationModule extends AbstractModule with ScalaModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind[ApplicationController].in[Singleton]
  }
}
