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

    // set it true to rebuild database
    if (false) {
      models.persistance.MongoDBsConnector.dbs.foreach(db => {
        val res = Await.result(
          db._2.db.command(
            new reactivemongo.core.commands.DropDatabase()), 3 seconds)
        println("DB: " + db._1 + " DROPPED: " + res)
      })

      //println("1- GrandPa!!!!!")
      val GrandPa1 = GrandPa(
        id = BSONObjectID.generate,
        name = "GrandPa1")
      val GrandPa2 = GrandPa(
        id = BSONObjectID.generate,
        name = "GrandPa2")
      val GrandPa3 = GrandPa(
        id = BSONObjectID.generate,
        name = "GrandPa3")
      Await.result(GrandPa.create(GrandPa1), 3 seconds)
      Await.result(GrandPa.create(GrandPa2), 3 seconds)
      Await.result(GrandPa.create(GrandPa3), 3 seconds)
      //println("2- FATHER!!!!!")
      Await.result(
        for {
          gp <- GrandPa.findAll.collect[List]()
        } yield {

          val father1 = Father(
            id = BSONObjectID.generate,
            name = "papa1",
            description = "pfff",
            age = 42,
            gp = Some(gp(1)))
          val father2 = Father(
            id = BSONObjectID.generate,
            name = "papa2",
            age = 34,
            description = "gimbojoe",
            gp = Some(gp(1)))
          val father3 = Father(
            id = BSONObjectID.generate,
            name = "papa3",
            age = 30,
            gp = Some(gp(2)))
          val father4 = Father(
            id = BSONObjectID.generate,
            name = "papa4",
            age = 48,
            gp = Some(gp(2)))
          val father5 = Father(
            id = BSONObjectID.generate,
            name = "papa5",
            age = 57,
            gp = Some(gp(0)))
          val father6 = Father(
            id = BSONObjectID.generate,
            name = "papa6",
            age = 43,
            gp = Some(gp(0)))
          Await.result(Father.create(father1), 4 seconds)
          Await.result(Father.create(father2), 4 seconds)
          Await.result(Father.create(father3), 4 seconds)
          Await.result(Father.create(father4), 4 seconds)
          Await.result(Father.create(father5), 4 seconds)
        }, 7 seconds)
      //println("3- SON!!!!!")    
      for {
        f <- Father.findAll.collect[List]()
      } yield {
        val son1 = Son(
          name = "son1",
          fa = Some(f(1)))
        val son2 = Son(
          name = "son2",
          fa = Some(f(1)))
        val son3 = Son(
          name = "son3",
          fa = Some(f(2)))

        val son4 = Son(
          name = "son4",
          fa = Some(f(0)))
        val son5 = Son(
          name = "son5",
          fa = Some(f(3)))
        val son6 = Son(
          name = "son6",
          fa = Some(f(3)))

        val son7 = Son(
          name = "son7",
          fa = Some(f(4)))
        val son8 = Son(
          name = "son8",
          fa = Some(f(4)))
        val son9 = Son(
          name = "son9",
          fa = Some(f(0)))

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
}