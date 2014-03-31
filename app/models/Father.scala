package models

import reactivemongo.bson._
import models.persistance._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.util.Success
import scala.util.Failure
import scala.concurrent.duration._
import scala.language.postfixOps

case class Father (
    id: BSONObjectID = BSONObjectID.generate,
    name: String,
    gp: Option[Reference[GranPa]] = None,
    sons: List[Reference[Son]] = List()
    ) extends ModelObj(id) {
  
  val singleton = Father
  
}

object Father extends RefPersistanceCompanion[Father] with FatherPersistanceCompanion[Father,Son] with SonPersistanceCompanion[Father,GranPa]{
  
  override lazy val dbName = "reactive_due"
  val collectionName = "fathers" 
    
  object FatherReader extends BSONDocumentReader[Father] {
    def read(doc: BSONDocument): Father =
      Father(
        doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[String]("name").get,
        doc.getAs[Reference[GranPa]]("gp"),
        doc.getAs[List[Reference[Son]]]("sons").getOrElse(List())
        )
  }
  implicit val reader = FatherReader 

  object FatherWriter extends BSONDocumentWriter[Father] {
    def write(father: Father): BSONDocument =
      BSONDocument(
        "_id" -> father.id,
        "name" -> father.name,
        "gp" -> father.gp,
        "sons" -> father.sons
        )
  }
  implicit val writer = FatherWriter
  
  val FatherPC = GranPa
  val CHILD = Son
  val sonsAttName = "sons"
  val fatherAttName = "gp"
    
  override def getFather(obj: Father) = {
    obj.gp
  }	
  
  override def getSons(obj: Father) = {
    obj.sons
  }

  override def _update(id: BSONObjectID, obj: Father) = { 
	  super._update(id,obj)
   }
 
  def referenceChanged(ogp: Option[Reference[GranPa]], rel: Reference[Father]): Future[Boolean] = {
    (ogp) match {
      case None => //Delete
        delete(rel.id)
      case Some(fa) => //Update or nothing
        updateFather(rel,fa)
    } 
  } 
  
  def removeFrom(toBeRemoved: List[Reference[Son]], from: List[Reference[Father]]): Future[Boolean] = 
    cleanChildren(toBeRemoved,from)
  
  def addTo(toBeAdded: List[Reference[Son]], to: Reference[Father]): Future[Boolean] = 
	addChildren(toBeAdded, to)
  

}