package ly.gravit.web.helpers

import play.api.Play.current
import play.api.Logger
import java.io.File
import fly.play.s3.S3
import fly.play.s3.BucketFile
import java.util.UUID
import fly.play.s3.BucketFile
import scala.io.Source
import fly.play.s3.PUBLIC_READ


class S3Helper(image: File, mineType: Option[String]) {
  def upload() = {
    val source = Source.fromFile(image)(scala.io.Codec.ISO8859)
    val byteArray = source.map(_.toByte).toArray
    source.close
    val imageName = S3Helper.generateFileName(mineType)
    val result = S3Helper.bucket.add(BucketFile(imageName, mineType.get, byteArray, Some(PUBLIC_READ)))
  }
}

object S3Helper {

   def apply(image: File, mineType: Option[String]) = new S3Helper(image, mineType)

    private val config = play.api.Play.current.configuration
    private val bucketName = config.getString("s3.uploads.bucket").get
    val bucket = S3(bucketName)



   private def generateFileName(mineType: Option[String]) = {
         UUID.randomUUID().toString() + generateExtension(mineType)
       }
   private def generateExtension(mineType: Option[String]) = {
           mineType match {
             case Some("image/png") => ".png"
             case Some("image/gif") => ".gif"
             case Some("image/jpeg") => ".jpeg"
             case Some(_) => ".jpeg"
             case None => ".jpeg"
           }
         }


}