package models

import reactivemongo.bson._
import models.persistance._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.util._


case class Son (
    id: BSONObjectID = BSONObjectID.generate,
    name: String,
    fa: Option[Reference[Father]] = None
    ) extends ModelObj(id) {
  val singleton = Son
     
}


object Son extends RefPersistanceCompanion[Son] with SonPersistanceCompanion[Son, Father] {
  
  override lazy val dbName = "reactive_uno"
  val collectionName = "sons"  
  val fatherAttName = "fa" 
  
  object SonReader extends BSONDocumentReader[Son] {
    def read(doc: BSONDocument): Son =
      Son(
        doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[String]("name").get,
        doc.getAs[Reference[Father]]("fa")
        )
  }
  
  implicit val reader = SonReader 

  object SonWriter extends BSONDocumentWriter[Son] {
    def write(son: Son): BSONDocument =
      BSONDocument(
        "_id" -> son.id,
        "name" -> son.name,
        "fa" -> son.fa
        )
  }
  
  implicit val writer = SonWriter
  
   def referenceChanged(ogp: Option[Reference[Father]], rel: Reference[Son]): Future[Boolean] = {
    (ogp) match {
      case None => //Delete
        super._delete(rel.id)
      case Some(fa) => //Update or nothing
        updateFather(rel,fa)
    } 
  }    
   
  val FatherPC = Father 
   
  override def getFather(obj: Son) = {
	  obj.fa
  }	
  
  override def _update(id: BSONObjectID, obj: Son) = { 
	  super._update(id,obj)
   }
  
}

