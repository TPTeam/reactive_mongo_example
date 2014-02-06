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
  
  def updateFather(rfa: Option[Reference[Father]]) =
    singleton._update(this.id,
        Son(
            this.id,
            this.name,
            rfa
            )
        )    
}


object Son extends RefPersistanceCompanion[Son] with SonPersistanceCompanion[Son, Father] {
  
  override lazy val dbName = "reactive_uno"
  val collectionName = "sons"  
    
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
  
   def referenceChanged = (ogp, rel) => {
    (ogp) match {
      case None => //Delete
        delete(rel.id)
      case Some(fa) => //Update or nothing
        (for {
          g <- findOneById(rel.id) 
        } yield 
        g.map(x => {
        	x.updateFather(ogp)
        })).map(o =>
          o match  {
            case Some(_) =>  true
            case _ => false
          })   
    } 
  }    
  
  val FatherPC = Father 
   
  override def getFather(obj: Son) = {
	  obj.fa
  }	
  /*
  override def delete(id: BSONObjectID) = {
      for {
        fa <- Father.findOneById(id)
      } yield Father.removeFrom(List(new Reference[Son](id)), fa.get)
	 super.delete(id)
  }*/
  
  override def _update(id: BSONObjectID, obj: Son) = { 
	  super._update(id,obj)
   }
  
}

