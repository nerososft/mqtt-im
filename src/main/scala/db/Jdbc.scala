//package db
//
//import slick.util.AsyncExecutor
//
///**
//  * Created by root on 16-5-11.
//  */
//object mysqldb {
//  lazy val db = Database.forURL(
//    "jdbc:mysql://localhost:3306/huodong?autoReconnect=true&useUnicode=true&characterEncoding=utf8",
//    user = "root",
//    password = "baby520587",
//    driver = "com.mysql.jdbc.Driver",
//    executor = AsyncExecutor("user1", 10, 1000)
//  )
//}