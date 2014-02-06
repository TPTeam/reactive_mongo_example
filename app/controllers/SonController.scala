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

object SonController extends Controller with TablePager[Son] with CRUDer[Son] {  
  
  def index = 
    Action {	
	  implicit request =>
	  	Ok(views.html.sonPage())
  	}
  
  val singleton = Son
  
  def elemValues(gp: Son) =
    Seq(gp.id.stringify,gp.name)
    
   override val elemsToDisplay = 
    Seq("id","name")
    
  def formTemplate(formgp: Form[Son])(implicit request: RequestHeader): play.api.templates.Html =
    views.html.sonForm(formgp)
    
  def form =
    Form(
      mapping(
          //TODO verifyId
        "id" -> text,
        "name" -> nonEmptyText,
        "fa" -> text.verifyOptionBSONId
       ){(id, name, _fa) =>
          {
            val fa = Await.result(Father.findOneByIdString(_fa), 3 seconds)
            val father = tryo{Reference[Father](fa.get.id)}
            			
            (tryo({
              if (id.equals("")) throw new Exception("")
              else new BSONObjectID(id)
            })) match {
              case Some(oid) => 			//UPDATE
                Await.result(
                	  Son.update(oid,
            		      Son(
            		          id = oid,
            		          name = name,
            		          fa = father
            		      )
            		   ), 3 seconds)
            	Await.result(Son.findOneById(oid), 3 seconds).get
              case _ =>	//CREATE
                val son = {if (id.equals(""))
                			Son(
            		          name = name,
            		          fa = father
            		          )
            		       else Son(
            		          id = new BSONObjectID(id),
            		          name = name,
            		          fa = father
            		          )}
                Await.result(
            		  Son.create(
            		      son
            		      ), 3 seconds)
                  
            	Await.result(Son.findOneById(son.id), 3 seconds).get
            }
          }
      }{s => {
        Some(s.id.stringify, 
    		 s.name,
    		 s.fa.map(x => x.id.stringify).getOrElse("")
    		)
      }
      }
  )  
  
}