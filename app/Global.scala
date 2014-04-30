import play.api._
import models._
import persistance._
import reactivemongo.api._
import reactivemongo.bson.BSONObjectID
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Await
import scala.concurrent.duration._
object Global extends GlobalSettings {

  override def onStart(app: Application) {
	  //println("1- GRANPA!!!!!")
    val granPa1 = GranPa(
    		id= BSONObjectID.generate,
            name = "granpa1"
            )           
    val granPa2 = GranPa(
            id= BSONObjectID.generate,
            name = "granpa2"
            )           
    val granPa3 = GranPa(
            id= BSONObjectID.generate,
            name = "granpa3"
            )  
    Await.result(GranPa.create(granPa1), 3 seconds)  
    Await.result(GranPa.create(granPa2), 3 seconds)        
    Await.result(GranPa.create(granPa3), 3 seconds)
    //println("2- FATHER!!!!!")
    Await.result( 
    for{
      gp <- GranPa.findAll.collect[List]()
    }yield{

    val father1 = Father(
            id= BSONObjectID.generate,
            name = "papa1",
            gp=Some(gp(1))
            )
    val father2 = Father(
    		id= BSONObjectID.generate,
            name = "papa2",
            gp=Some(gp(1))
            )
    val father3 = Father(
            id= BSONObjectID.generate,
            name = "papa3",
            gp=Some(gp(2))
            )
    val father4 = Father(
            id= BSONObjectID.generate,
            name = "papa4",
            gp=Some(gp(2))
            )
    val father5 = Father(
            id= BSONObjectID.generate,
            name = "papa5",
            gp=Some(gp(0))
            )
    val father6 = Father(
            id= BSONObjectID.generate,
            name = "papa6",
            gp=Some(gp(0))
            )     
    Await.result(Father.create(father1), 4 seconds)        
    Await.result(Father.create(father2), 4 seconds)
    Await.result(Father.create(father3), 4 seconds)        
    Await.result(Father.create(father4), 4 seconds)
    Await.result(Father.create(father5), 4 seconds)
    }, 7 seconds)
    //println("3- SON!!!!!")    
      for{
        f <- Father.findAll.collect[List]()
      }yield{
               val son1 = Son(
            name = "son1",
            fa = Some(f(1))
            )
    val son2 = Son(
            name = "son2",
            fa = Some(f(1))
            )
    val son3 = Son(
            name = "son3",
            fa = Some(f(2))
            )
    
    val son4 = Son(
            name = "son4",
            fa = Some(f(0))
            )
    val son5 = Son(
            name = "son5",
            fa = Some(f(3))
            )
    val son6 = Son(
            name = "son6",
            fa = Some(f(3))
            )     
            
    val son7 = Son(
            name = "son7",
            fa = Some(f(4))
            )
    val son8 = Son(
            name = "son8",
            fa = Some(f(4))
            )
    val son9 = Son(
            name = "son9",
            fa = Some(f(0))
            ) 

       
       Son.create(son1)   
    Son.create(son2)         
    Son.create(son3)        
    Son.create(son4)         
    Son.create(son5)         
    Son.create(son6)       
    Son.create(son7)       
    Son.create(son8)        
    Son.create(son9)     
            
      }

}
}