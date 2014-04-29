package models

import reactivemongo.bson._
import models.persistance._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.util.Success
import scala.util.Failure


case class GranPa (
    id: BSONObjectID = BSONObjectID.generate,
    name: String,
    sons: List[Reference[Father]] = List()
    ) extends ModelObj(id) {
  val singleton = GranPa
}

object GranPa extends RefPersistanceCompanion[GranPa] with FatherPersistanceCompanion[GranPa, Father] {
  
  override lazy val dbName = "reactive_uno"
  val collectionName = "granpas"
    
  object GranPaReader extends BSONDocumentReader[GranPa] {
    def read(doc: BSONDocument): GranPa =
      GranPa(
        doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[String]("name").get,
        doc.getAs[List[Reference[Father]]]("sons").getOrElse(List())
        )
  }
  implicit val reader = GranPaReader 

  object GranPaWriter extends BSONDocumentWriter[GranPa] {
    def write(granpa: GranPa): BSONDocument =
      BSONDocument(
        "_id" -> granpa.id,
        "name" -> granpa.name,
        "sons" -> granpa.sons
        )
  }
  
  implicit val writer = GranPaWriter
  
  val CHILD = Father
  val sonsAttName = "sons"
  override def getSons(obj: GranPa) = {
    obj.sons
  }
  
  
  override def _update(id: BSONObjectID, obj: GranPa) = {
    	super._update(id,obj)
    }

  
  def removeFrom(toBeRemoved: List[Reference[Father]], from: List[Reference[GranPa]]): Future[Boolean] = 
    cleanChildren(toBeRemoved,from)
  
  def addTo(toBeAdded: List[Reference[Father]], to: Reference[GranPa]): Future[Boolean] = 
	addChildren(toBeAdded, to)
  
}