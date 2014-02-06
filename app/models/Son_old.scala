package models

class Son_old {

}

/*object Son extends PersistanceCompanion[Son] with ReverseRefPersistanceCompanion[Son, Father] {
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
  
  
  override def create(obj: Son) = {
    if (obj.fa.isDefined)
      for {
        fa <- Father.findOneById(obj.fa.get.id)
      } yield Father.addTo(List(new Reference[Son](obj.id)), fa.get)
    super.create(obj)
  }
 
  
  override def delete(id: BSONObjectID) = {
	  super.delete(id)
  }
  
  
  def _update(id: BSONObjectID, obj: Son) = {
    super.update(id,obj)
  }
  
  
  override def update(id: BSONObjectID, obj: Son) = {
    val fathersRemoveFromBlock = Promise[Boolean]
    val fathersAddToBlock = Promise[Boolean]
    val overallBlock = Promise[Option[Son]]
    
	for {
    	fathers <- Father.findAll.collect[List]()
    }
    yield
    {	// update fathers updating son references
      Father.removeFrom(List(Reference[Son](id)), fathers).onComplete{
        _ => fathersRemoveFromBlock.trySuccess(true)
      }
      if(obj.fa.isDefined) 
    	for{
    		fathOpt <- Father.findOneById(obj.fa.get.id)
    	}
    	yield{
    		Father.addTo(List(Reference[Son](obj.id)), fathOpt.get).onComplete{ 
    		  _ => fathersAddToBlock.trySuccess(true)
    		}
    	}
      else
        fathersAddToBlock.trySuccess(true)
    }
    
    for{
      b1 <- fathersRemoveFromBlock.future
      b2 <- fathersAddToBlock.future
    }yield{
      super.update(id, obj).onComplete{
        case Success(s) => overallBlock.success(s)
        case Failure(f) => overallBlock.failure(f)
      }
    }
    overallBlock.future
    
  }
  
}*/