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
  
  override def getSons(obj: GranPa) = {
    obj.sons
  }
  
  
  override def _update(id: BSONObjectID, obj: GranPa) = {
    	super._update(id,obj)
    }

  
  def removeFrom(toBeRemoved: List[Reference[Father]], from: List[GranPa]): Future[Boolean] = { 
    // remove from any father every link to parents toBeRemoved
    val res = from.map(_ => Promise[Boolean])
 
    for ( gp <- from.zipWithIndex) {
        val newSons = gp._1.sons.filterNot(e => toBeRemoved.contains(e))
        if (newSons.length!=gp._1.sons.length) {
          _update(gp._1.id,
              GranPa(
                  gp._1.id,
                  gp._1.name,
                  newSons
                  )
              ).map {
                  case _ => res(gp._2).trySuccess(true)
          	}
        } else {
          res(gp._2).trySuccess(true)
        }
      }
    val r = res.map(x => x.future)

    val re = Future.fold(r)(true)((i, l) => l)

    re
  }
  
  
def addTo(toBeAdded: List[Reference[Father]], to: GranPa): Future[Boolean] = {
	val newSons = to.sons.filterNot(e => toBeAdded.contains(e))
    val res = Promise[Boolean]
	_update(to.id,
                GranPa(
                  to.id,
                  to.name,
                  newSons ++ toBeAdded
                )
        ).onComplete{
	  case _ => res.trySuccess(true)
	}
    res.future
  }
  
}