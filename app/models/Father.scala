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
  
  def updateGranpa(rgp: Option[Reference[GranPa]]) = {
    singleton._update(this.id,
        Father(
            this.id,
            this.name,
            rgp,
            this.sons
            )
        )
  }
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
  
  override def getFather(obj: Father) = {
    obj.gp
  }	
  
  override def getSons(obj: Father) = {
    obj.sons
  }
  
  def removeFrom(toBeRemoved: List[Reference[Son]], from: List[Father]): Future[Boolean] = {    
    // remove from any father every link to parents toBeRemoved
    val res = from.map(_ => Promise[Boolean])
 
    for ( fa <- from.zipWithIndex) {
        val newSons = fa._1.sons.filterNot(e => toBeRemoved.contains(e))
        if (newSons.length!=fa._1.sons.length) {
          _update(fa._1.id,
              Father(
                  fa._1.id,
                  fa._1.name,
                  fa._1.gp,
                  newSons
                  )
              ).map {
                  case _ => res(fa._2).trySuccess(true)
          	}
        } else {
          res(fa._2).trySuccess(true)
        }
      }
    val r = res.map(x => x.future)

    val re = Future.fold(r)(true)((i, l) => l)

    re
  }
  

  override def _update(id: BSONObjectID, obj: Father) = { 
	  super._update(id,obj)
   }
 
  
  def addTo(toBeAdded: List[Reference[Son]], to: Father): Future[Boolean] = {
	val newSons = to.sons.filterNot(e => toBeAdded.contains(e))
    val res = Promise[Boolean]
	_update(to.id,
            Father(
                to.id,
                to.name,
                to.gp,
                newSons ++ toBeAdded
                )
        ).onComplete{
	  case _ => res.trySuccess(true)
	}
    res.future
  }
    
  
  def referenceChanged = (ogp, rel) => {
    (ogp) match {
      case None => //Delete
        delete(rel.id)
      case Some(gp) => //Update or nothing
        (for {
          g <- findOneById(rel.id) 
        } yield 
        g.map(x => {
        	x.updateGranpa(Some(gp))
        })).map(o =>
          o match  {
            case Some(_) =>  true
            case _ => false
          })   
    } 
  }
  
}