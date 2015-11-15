package com.hanh

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json._

case class Strategy(strategy: String, team: String)
case class ExposureRow(group: String, exposure: Int)
case class Exposure(strategy: String, team: String, exposure: Int)

object Marshallers extends DefaultJsonProtocol {
  implicit val exposureRowMarshaller = jsonFormat2(ExposureRow)
}

object Server extends App with SprayJsonSupport {
  val teams = List(Strategy("SAa", "A"), Strategy("SAb", "A"), Strategy("SAc", "A")
    , Strategy("SBa", "B"), Strategy("SBb", "B")).map { case Strategy(s, t) => s -> t}.toMap
  val exposure = Map("SAa" -> 1, "SAb" -> 2, "SAc" -> 3, "SBa" -> 10, "SBb" -> 11)
  val exposureFacts = exposure.map { case (s, v) => Exposure(s, teams(s), v)}

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  import Marshallers._

  val route =
    path("") {
      get {
        parameters('team?, 'strategy?, 'group) { (team, strategy, group) =>
          complete {
            val es = exposureFacts.filter { case Exposure(s, t, _) =>
              team.map(team => t == team).getOrElse(true) &&
              strategy.map(strategy => s == strategy).getOrElse(true)
            }

            val r =
              (group match {
                case "team" => es.groupBy(exp => exp.team)
                case "strategy" => es.groupBy(exp => exp.strategy)
                case "product" => es.groupBy(exp => exp.strategy)
              }) map { case (g, v) => ExposureRow(g, v.map(_.exposure).sum)}

            r.toList.toJson
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 3001)
}
