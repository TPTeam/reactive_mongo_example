package models

class GranPa_old {

}

/*
  object GranPaWriter extends BSONDocumentWriter[GranPa] {
    def write(granpa: GranPa): BSONDocument =
      BSONDocument(
        "_id" -> granpa.id,
        "name" -> granpa.name,
        "sons" -> granpa.sons
        )
  }
  implicit val writer = GranPaWriter
    
  override def delete(id: BSONObjectID) = {
    for {
      g <- findOneById(id) 
    } yield
    g.map(gp => {
      gp.sons.foreach(x => Father.referenceChanged(None,x))
      false
    })
    super.delete(id)
  }
    
  override def create(obj: GranPa) = {
    for {
      granpas <- findAll.collect[List]()
    } yield
    	removeFrom(obj.sons,granpas.filterNot(x => x.id==obj.id).toList)
    obj.sons.map(x => Father.referenceChanged(Some(new Reference[GranPa](obj.id)),x))
    super.create(obj)
  }
  

  private def _update(id: BSONObjectID, obj: GranPa) = {
    	super.update(id,obj)
    }

  
    def removeFrom(toBeRemoved: List[Reference[Father]], from: List[GranPa]): Future[Boolean] = {
      
      val res = Promise[Boolean]
      for ( // remove from any gp every link to parents toBeRemoved
          gp <- from
          ) {
        val newSons = 
          gp.sons.filterNot(e => toBeRemoved.contains(e))
              
        if (newSons.length!=gp.sons.length) {
          _update(gp.id,
              GranPa(
                  gp.id,
                  gp.name,
                  newSons
                  )
              ).andThen{
            case _ => res.trySuccess(true)
          }
            
        }
        else
          res.trySuccess(true)
      }
      res.future
  }
  
  
  def addTo(toBeAdded: List[Reference[Father]], to: GranPa): Future[Boolean] = {
    
    _update(to.id,
            GranPa(
                to.id,
                to.name,
                to.sons ++ toBeAdded
                )
        ).map(o =>
          o match  {
            case Some(_) =>  true
            case _ => false
          })
  }

  
  override def update(id: BSONObjectID, obj: GranPa) = {
    val overallBlock = Promise[Option[GranPa]]
    val removeFromGPBlock = Promise[Boolean]
    val updateFathersBlock = Promise[Boolean]
    
    for {
      granpas <- findAll.collect[List]()
    } yield{ // remove the new granpa sons from the other granpas
    	removeFrom(obj.sons, granpas.filterNot(x => x.id==obj.id).toList).onComplete{
    	  _ => removeFromGPBlock.trySuccess(true)
    	} 
    
    	for {
    		gp <- findOneById(id)
    	} yield {
    		val olds = gp.get.sons
    		val news = obj.sons
    		val oldsForBlock = olds.map( _ => Promise[Boolean] )
    		val newsForBlock = news.map( _ => Promise[Boolean] )
    		
    		for (o <- olds.zipWithIndex)
    			if (!news.exists(x => x.id==o._1.id)) //delete fathers from the old
    				Father.referenceChanged(None,o._1).onComplete{
    					_ => oldsForBlock(o._2).trySuccess(true)
    			}
    			else
    				oldsForBlock(o._2).trySuccess(true)
    	  
    		for (n <- news.zipWithIndex)
    			if (!olds.exists(x => x.id == n._1.id)) //add new 
    				Father.referenceChanged(Some(Reference(id)),n._1).onComplete{
    					_ => newsForBlock(n._2).trySuccess(true)
    			}
    			else
    				newsForBlock(n._2).trySuccess(true)
    	
    		val resOldSons = Future.fold(oldsForBlock.map(x => x.future))(true)((i, l) => l)
    		val resNewSons = Future.fold(newsForBlock.map(x => x.future))(true)((i, l) => l)
    	
    		for{	//finally update gp
    			block1 <- removeFromGPBlock.future
    			block2 <- resOldSons
    			block3 <- resNewSons
    		}yield{
    			play.Logger.debug("Let's updating gp")
    			super.update(id, obj).onComplete{
    				case Success(r) => overallBlock.success(r)            
    				case Failure(f) => overallBlock.failure(f)
    			}
    		}
    	}
    }
    overallBlock.future
  }  
  
}*/