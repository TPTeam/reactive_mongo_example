package models

class Father_old {

}

/*

object Father extends PersistanceCompanion[Father]
		with ReverseRefPersistanceCompanion[Father,GranPa] 
		with DirectRefPersistanceCompanion[Father, Son] {
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
  

  def _update(id: BSONObjectID, obj: Father) = { 
	  super.update(id,obj)
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
  
  
  override def create(obj: Father) = {
    for {
    	elems <- findAll.collect[List]()
    } yield {
        // remove from the other parents every links to the sons which have to be under this new father 
    	removeFrom(obj.sons,elems.filterNot(x => x.id==obj.id).toList) 
    	//ADDED update the link to the related gran parent
    	if(obj.gp.isDefined)
    		for {
    			gpa <- GranPa.findOneById(obj.gp.get.id)
    		} yield {
    			GranPa.addTo(List(new Reference[Father](obj.id)), gpa.get)
    		}
    }
    obj.sons.map(x => Son.referenceChanged(Some(obj),x))
    super.create(obj)
  }
    
  
  override def delete(id: BSONObjectID) = {
    for {
      f <- findOneById(id) 
    } yield
    {
    	for {
          granpas <- GranPa.findAll.collect[List]()
        } yield {
    	  GranPa.removeFrom(List(new Reference[Father](f.get.id)), granpas)
    	  f.get.sons.foreach(x => Son.referenceChanged(None,x))
    	}
    }
    super.delete(id)
  }
  
  
  override def update(id: BSONObjectID, newObj: Father) = {
    play.Logger.debug("START UPDATE FATHER")
    val res = Promise[Option[Father]]
    
    for {
    		oldObjOpt <- findOneById(id) 
    } yield {
      for {
       oldObj <- oldObjOpt 
      } yield {
        
        val granpaRes = Promise[Boolean]
        
        for{
        	granpas <- GranPa.findAll.collect[List]()
        } yield {
        (oldObj.gp, newObj.gp) match {
          case (None, None) => play.api.Logger.debug("Non faccio niente");granpaRes.trySuccess(true)// Do nothing
          case (Some(old), Some(newer)) => 
            if (old!= newer) {
               GranPa.removeFrom(List(new Reference[Father](newObj.id)), granpas.filter(g => g.id == old.id))
               	for {
            	gp <- GranPa.findOneById(newer.id) 
               	} yield {
               		GranPa.addTo(List(new Reference[Father](newObj.id)), gp.get).onComplete{
               		  case _ =>  granpaRes.trySuccess(true)
               		}
               	}
            } else {granpaRes.trySuccess(true)} //Do nothing
          case (Some(old), None) =>
            GranPa.removeFrom(List(new Reference[Father](newObj.id)), granpas.filter(g => g.id == old.id)).onComplete{
               		  case _ =>  granpaRes.trySuccess(true)
               		}
          case (None, Some(newer)) =>
            for {
            	gp <- GranPa.findOneById(newer.id) 
            } yield {
            	GranPa.addTo(List(new Reference[Father](newObj.id)), gp.get).onComplete{
               		  case _ =>  granpaRes.trySuccess(true)
               		}
            }
        }
        }
         
        val olds = oldObjOpt.get.sons
        val news = newObj.sons
        
        val resRemFromF = Promise[Boolean]
        val oldSonsRes = olds.map(_ => Promise[Boolean])
        val newSonsRes = news.map(_ => Promise[Boolean])
 
        for (o <- olds.zipWithIndex)
        	if (!news.exists(x => x.id==o._1.id))
        		{//delete old sons with no link to the new father
    				 Son.referenceChanged(None,o._1).onComplete {
    				   case _ => oldSonsRes(o._2).trySuccess(true)
    				 }
        		}
        	else {
        	  oldSonsRes(o._2).trySuccess(true)
        	}
        for (n <- news.zipWithIndex)
        	if (!olds.exists(x => x.id == n._1.id)) //update new sons with the link to the new parent 
        		{
    				Son.referenceChanged(Some(Reference(id)),n._1).onComplete {
    				   case _ => newSonsRes(n._2).trySuccess(true)
    				 } // update father link inside new sons
        		}
        	else {
        	  newSonsRes(n._2).trySuccess(true)
        	}
        
        for {
        	fathers <- Father.findAll.collect[List]()
        } yield {
          play.Logger.debug("once?")
        	Father.removeFrom(news, fathers.filterNot(_.id == id)).onComplete(_ =>{ play.Logger.debug("back"); resRemFromF.trySuccess(true)})
        }
        
        val resOldSons = Future.fold(oldSonsRes.map(x => x.future))(true)((i, l) => l)
        val resNewSons = Future.fold(newSonsRes.map(x => x.future))(true)((i, l) => l)
        
        
        for {
          r1 <- resRemFromF.future
          r2 <- resOldSons
          r3 <- resNewSons
          r4 <- granpaRes.future
        } yield {
          play.Logger.debug("FINAL UPDATE FATHER")
          _update(id, newObj).onComplete{
            case Success(r) =>
            	res.trySuccess(r)
            case Failure(f) =>
            	res.failure(f)
          }
        }
        
      }
    }
      
    res.future
  }
   
*/