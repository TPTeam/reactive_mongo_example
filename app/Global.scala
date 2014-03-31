import play.api._
import models._
import persistance._
import reactivemongo.api._
import reactivemongo.bson.BSONObjectID
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Await
import scala.concurrent.duration._
object Global extends GlobalSettings {

//  override def onStart(app: Application) {
//    val granPa1 = GranPa(
//    		id= BSONObjectID.generate,
//            name = "granpa1"
//            )           
//    val granPa2 = GranPa(
//            id= BSONObjectID.generate,
//            name = "granpa2"
//            )           
//    val granPa3 = GranPa(
//            id= BSONObjectID.generate,
//            name = "granpa3"
//            )  
//    Await.result(        
//    {GranPa.create(granPa1)  
//    GranPa.create(granPa2)        
//    GranPa.create(granPa3)}, 3 seconds)
//    
//    for{
//      gp1 <- GranPa.findOneById(granPa1.id)
//      gp2 <- GranPa.findOneById(granPa2.id)
//      gp3 <- GranPa.findOneById(granPa3.id)
//    }yield{
//      
//    val father1 = Father(
//            id= BSONObjectID.generate,
//            name = "papa1",
//            gp=Some(Reference[GranPa](gp1.get.id))
//            )
//    val father2 = Father(
//    		id= BSONObjectID.generate,
//            name = "papa2",
//            gp=Some(Reference[GranPa](gp1.get.id))
//            )
//    val father3 = Father(
//            id= BSONObjectID.generate,
//            name = "papa3",
//            gp=Some(Reference[GranPa](gp2.get.id))
//            )
//    val father4 = Father(
//            id= BSONObjectID.generate,
//            name = "papa4",
//            gp=Some(Reference[GranPa](gp2.get.id))
//            )
//    val father5 = Father(
//            id= BSONObjectID.generate,
//            name = "papa5",
//            gp=Some(Reference[GranPa](gp3.get.id))
//            )
//    val father6 = Father(
//            id= BSONObjectID.generate,
//            name = "papa6",
//            gp=Some(Reference[GranPa](gp3.get.id))
//            )     
//    
//    Await.result({
//    Father.create(father1)        
//    Father.create(father2)
//    Father.create(father3)        
//    Father.create(father4)
//    Father.create(father5)        
//    Father.create(father6)}, 3 seconds)
//        
//    for{
//      f1 <- Father.findOneById(father1.id)
//      f2 <- Father.findOneById(father2.id)
//      f3 <- Father.findOneById(father3.id)
//      f4 <- Father.findOneById(father4.id)
//      f5 <- Father.findOneById(father5.id)
//      f6 <- Father.findOneById(father6.id)
//    }yield{
//      
//       val son1 = Son(
//            name = "son1",
//            fa = Some(Reference[Father](f1.get.id))
//            )
//    val son2 = Son(
//            name = "son2",
//            fa = Some(Reference[Father](f2.get.id))
//            )
//    val son3 = Son(
//            name = "son3",
//            fa = Some(Reference[Father](f3.get.id))
//            )
//    
//    val son4 = Son(
//            name = "son4",
//            fa = Some(Reference[Father](f4.get.id))
//            )
//    val son5 = Son(
//            name = "son5",
//            fa = Some(Reference[Father](f5.get.id))
//            )
//    val son6 = Son(
//            name = "son6",
//            fa = Some(Reference[Father](f6.get.id))
//            )     
//            
//    val son7 = Son(
//            name = "son7",
//            fa = Some(Reference[Father](f1.get.id))
//            )
//    val son8 = Son(
//            name = "son8",
//            fa = Some(Reference[Father](f2.get.id))
//            )
//    val son9 = Son(
//            name = "son9",
//            fa = Some(Reference[Father](f3.get.id))
//            ) 
//    Await.result({        
//    Son.create(son1)   
//    Son.create(son2)        
//    Son.create(son3)        
//    Son.create(son4)        
//    Son.create(son5)        
//    Son.create(son6)        
//    Son.create(son7)        
//    Son.create(son8)        
//    Son.create(son9)}, 3 seconds)        
//      
//    }
//    
//     
//   }  
//            
//    /*
//    val father = Father(
//            name = "papa1"
//            )
//    import Father._
//    val son = Son(
//        name = "qui",
//        fa = Some(father)
//        )
//   
//   Father.create(father)
//   Son.create(son)
//      */
//   /*
//    Son.create{
//      Son(
//          name = "proviamo1")
//    }
//    Father.create(
//        Father(
//            name = "papa1"
//            )
//        )
//    Son.create{
//      Son(
//          name = "proviamo2")
//    }
//    Father.create(
//        Father(
//            name = "papa2"
//            )
//        )*/
//   
//   /*
//    println("QUI!!! "+
//    		MongoDBsConnector.dbs.size
//    )*/
//  }
//  
}