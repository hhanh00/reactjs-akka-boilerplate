package com.hanh

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json._

case class Strategy(strategy: String, team: String)

object Marshallers extends DefaultJsonProtocol {
  implicit val strategyMarshaller = jsonFormat2(Strategy)
}

object Server extends App with SprayJsonSupport {
  val data = List(Strategy("SAa", "A"), Strategy("SAb", "A"), Strategy("SAc", "A")
    , Strategy("TBa", "B"), Strategy("TBb", "B"))

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  import Marshallers._

  val route =
    path("") {
      get {
        parameters('team?) { team =>
          complete {
            val d = team.map { team =>
              data.filter(_.team == team)
            } getOrElse data
            d.toJson
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 3001)
}
