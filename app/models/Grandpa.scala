package models

import reactivemongo.bson._
import models.persistance._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.util.Success
import scala.util.Failure


case class GrandPa (
    id: BSONObjectID = BSONObjectID.generate,
    name: String,
    sons: List[Reference[Father]] = List()
    ) extends ModelObj(id) {
  val singleton = GrandPa
}

object GrandPa extends RefPersistanceCompanion[GrandPa] with FatherPersistanceCompanion[GrandPa, Father] {
  
  override lazy val dbName = "reactive_uno"
  val collectionName = "grandpas"
    
  object GrandPaReader extends BSONDocumentReader[GrandPa] {
    def read(doc: BSONDocument): GrandPa =
      GrandPa(
        doc.getAs[BSONObjectID]("_id").get,
        doc.getAs[String]("name").get,
        doc.getAs[List[Reference[Father]]]("sons").getOrElse(List())
        )
  }
  implicit val reader = GrandPaReader 

  object GrandPaWriter extends BSONDocumentWriter[GrandPa] {
    def write(GrandPa: GrandPa): BSONDocument =
      BSONDocument(
        "_id" -> GrandPa.id,
        "name" -> GrandPa.name,
        "sons" -> GrandPa.sons
        )
  }
  
  implicit val writer = GrandPaWriter
  
  val CHILD = Father
  val sonsAttName = "sons"
  override def getSons(obj: GrandPa) = {
    obj.sons
  }
  
  
  override def _update(id: BSONObjectID, obj: GrandPa) = {
    	super._update(id,obj)
    }

  
  def removeFrom(toBeRemoved: List[Reference[Father]], from: List[Reference[GrandPa]]): Future[Boolean] = 
    cleanChildren(toBeRemoved,from)
  
  def addTo(toBeAdded: List[Reference[Father]], to: Reference[GrandPa]): Future[Boolean] = 
	addChildren(toBeAdded, to)
  
}