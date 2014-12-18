package controllers

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.concurrent.Akka.system
import com.codahale.jerkson.Json._
import models.bot.{BotState, BotCommand}
import services.{TinderBot, TinderService}


object Bot extends Controller {

  implicit val timeout = Timeout(10 seconds)

  /**
   * Pulls up an active Tinder session in a dashboard.
   */
  def bot(xAuthToken: String) = Action.async { implicit request =>
    val f = future { TinderService.fetchSession(xAuthToken) }
    f.map { result =>
      result match {
        case Some(session) => Ok(views.html.Bot.bot(session))
        case None => BadRequest
      }
    }
  }

  /**
   * Get the current state of the Bot.
   */
  def state = Action.async { implicit request =>
    val f = TinderBot.context ? BotCommand("state")
    f.map { result => Ok(generate(result)).as("application/json") }
  }

  /**
   * Get the current state of the Bot.
   */
  def userLog(userId: String) = Action.async { implicit request =>
    val f = future { TinderBot.fetchLog(userId) }
    f.map { result => Ok(generate(result)).as("application/json") }
  }

  /**
   * Get the current state of the Bot.
   */
  def userLogUpdates(userId: String) = Action.async { implicit request =>
    val f = future { TinderBot.fetchLogUpdates(userId) }
    f.map { result => Ok(generate(result)).as("application/json") }
  }

}