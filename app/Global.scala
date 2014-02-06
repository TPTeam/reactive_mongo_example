import play.api._
import models._
import persistance._
import reactivemongo.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    
    /*
    val father = Father(
            name = "papa1"
            )
    import Father._
    val son = Son(
        name = "qui",
        fa = Some(father)
        )
   
   Father.create(father)
   Son.create(son)
      */
   /*
    Son.create{
      Son(
          name = "proviamo1")
    }
    Father.create(
        Father(
            name = "papa1"
            )
        )
    Son.create{
      Son(
          name = "proviamo2")
    }
    Father.create(
        Father(
            name = "papa2"
            )
        )*/
   
   /*
    println("QUI!!! "+
    		MongoDBsConnector.dbs.size
    )*/
  }
  
}