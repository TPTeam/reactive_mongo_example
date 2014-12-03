package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.libs.json._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Execution.Implicits._
import controllerhelper._
import tp_utils.Tryer._
import reactivemongo.bson._
import scala.language.reflectiveCalls
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import controllers.helper.{TablePager, CRUDer}

object GrandpaController extends Controller with TablePager[GrandPa] with CRUDer[GrandPa] {  
  
  def index = 
    Action {	
	  implicit request =>
	  	Ok(views.html.familyPage("grandpa", controllers.routes.GrandpaController.table, elemsToDisplay))
  	}
  
  val singleton = GrandPa
  
  def elemValues(gp: GrandPa) =
    Seq(gp.id.stringify,gp.name)
    
  override val elemsToDisplay = 
    Seq("id","name")
    
  override val elemsToFilter =
    Seq("name")
    
  def formTemplate(formgp: Form[GrandPa])(implicit request: RequestHeader): play.api.templates.Html =
    views.html.grandPaForm(formgp)
    
  def tryChilds(_sons: String): Future[List[Option[Reference[Father]]]] = {
    Future.traverse(
    tryo{Json.parse(_sons)} match {
                case Some(s : JsArray) =>
                  s.value.seq.map(v =>
                    for {
                      s <- Father.findOneByIdString(v.as[String])
                    } yield
                    	tryo{Reference[Father](s.get.id)}).toList
                case _ => List()
              }
    )(x => x)
  }   
  
  def form =
    Form(
      mapping(
          //TODO verifyId
        "id" -> text,
        "name" -> nonEmptyText,
        "sons" -> text.verifyOptJson
       ){(id, name, _sons) =>
          {
            val sons: List[Reference[Father]] = 
              Await.result(tryChilds(_sons), 10 seconds).flatten
              			
            (tryo({
              if (id.equals("")) throw new Exception("")
              else BSONObjectID.parse(id).toOption.get
              })) match {
              case Some(oid) => //UPDATE
                	Await.result(
                	  GrandPa.update(oid,
            		      GrandPa(
            		          id = oid,
            		          name = name,
            		          sons = sons
            		      )
            		   ), 3 seconds)
            		 Await.result(
            		   GrandPa.findOneById(oid), 3 seconds).get
              case _ => //CREATE
                val gp = GrandPa(
            		          name = name,
            		          sons = sons
            		          )
            		Await.result(
            		  GrandPa.create(
            		      gp
            		      ), 3 seconds)
            		  Await.result(
            		   GrandPa.findOneById(gp.id), 3 seconds).get
            }
          }
      }{gp => {
        Some(gp.id.stringify, 
    		 gp.name,
    		 Json.stringify(Json.toJson(gp.sons.map(s => s.id.stringify)))
    		)
      }
      }
  )  
  
}