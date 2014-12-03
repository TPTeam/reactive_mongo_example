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

object FatherNoStringsController extends Controller with TablePager[Father] with CRUDer[Father] {  
  
  def index = 
    Action {	
	  implicit request =>  
	  	Ok(views.html.familyPage("father", controllers.routes.FatherNoStringsController.table, elemsToDisplay))
  	}
  
  val singleton = Father
    
  def elemValues(fa: Father) =
    Seq(fa.id.stringify,fa.name)
    
  override val elemsToDisplay =
    Seq("id", "age"/*,"name","description"*/)
  
  // defining custom reader for int
  override def elemReader(keys: Seq[String])(doc: BSONDocument): Seq[String] = {
    keys.map(k => {
      (k) match {
        case "id" => doc.getAs[BSONObjectID]("_id").get.stringify
        case "age" => doc.getAs[Int]("age").get.toString
        case _ => doc.getAs[String](k).get.toString
      }
    })
  }
      
  def formTemplate(formgp: Form[Father])(implicit request: RequestHeader): play.api.templates.Html =
    views.html.fatherForm(formgp)

  def tryChilds(_sons: String): Future[List[Option[Reference[Son]]]] = {
    Future.traverse(
    tryo{Json.parse(_sons)} match {
                case Some(s : JsArray) =>
                  s.value.seq.map(v =>
                    for {
                      s <- Son.findOneByIdString(v.as[String])
                    } yield
                    	tryo{Reference[Son](s.get.id)}).toList
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
        "description" -> text,
        "age" -> of[Int],
        "gp" -> text.verifyOptionBSONId,
        "sons" -> text.verifyOptJson
       ){(id, name, description, age, gp, _sons) =>
          {
            val gr = Await.result(GrandPa.findOneByIdString(gp), 3 seconds)
            val so = Await.result(tryChilds(_sons), 10 seconds)
            
            val granpa = tryo{Reference[GrandPa]({
              gr.get.id})}
            val sons: List[Reference[Son]] = 
              so.flatten
				
            (tryo({
              if (id.equals("")) throw new Exception("")
              else BSONObjectID.parse(id).toOption.get
              })) match {
              case Some(oid) => //UPDATE
                	Await.result({
                	  Father.update(oid,
            		      Father(
            		          id = oid,
            		          name = name,
            		          description = description,
            		          age = age,
            		          gp = granpa,
            		          sons = sons
            		      )
                		)}, 300 seconds)
            		   Await.result({
            		   Father.findOneById(oid).map(_.get)}, 3 seconds)//.get
              case _ =>	//CREATE
                val faht = Father(
            		          name = name,
            		          description = description,
            		          age = age,
            		          gp = granpa,
            		          sons = sons
            		          )
                Await.result(
            		  Father.create(
            		      faht
            		      ), 300 seconds)
                Await.result(
            		   Father.findOneById(faht.id).map(_.get), 3 seconds)
            }
          }
      }{f => {
        Some(f.id.stringify, 
    		 f.name,
    		 f.description,
    		 f.age,
    		 f.gp.map(x => x.id.stringify).getOrElse(""),
    		 Json.stringify(Json.toJson(f.sons.map(s => s.id.stringify)))
    		)
      }
      }
  )  
  
}