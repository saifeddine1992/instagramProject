package com.fluidcode.processing.silver

import com.fluidcode.configuration.Configuration
import com.fluidcode.models.bronze.{Captions, Comments, CommentsData, Data, DataElements, DimensionsData, EdgesElements,
                            GraphImagesElements, Info, Likes, NodeData, Owner, OwnerData, ProfileInfo, ThumbnailElements}
import com.fluidcode.models.silver.PostsData
import com.fluidcode.processing.bronze.BronzeLayer
import com.fluidcode.processing.silver.GetPostsInfoAndCreateTable._
import org.apache.spark.sql.QueryTest
import org.apache.spark.sql.delta.test.DeltaExtendedSparkSession
import org.apache.spark.sql.test.SharedSparkSession

class GetPostsInfoAndCreateTableSpec extends QueryTest
  with SharedSparkSession
  with DeltaExtendedSparkSession {

  test("getPostsInfo should create post info table from Bronze Layer" ) {
    withTempDir { dir =>
      val sparkSession = spark
      val conf = Configuration(dir.toString)
      conf.init(sparkSession)

      import sparkSession.implicits._

      val path = s"${conf.rootPath}/${conf.database}/${conf.sample}"

      val sampleDf = Seq(
        Data(Array(GraphImagesElements(
          "Graph1", CommentsData(Array(DataElements(
            1623779105, "1382894360", OwnerData("138289436000", "https://profile_pic_url1", "mehrez"), "comment_text1")
          )),
          comments_disabled = true, DimensionsData(1080, 1920), "https://instagram.url1", Likes(100),
          Captions(Array(EdgesElements(NodeData("caption_text1")))),
          Comments(50), null, "image_id1", is_video = false, null, "s564gsd", Owner("138289436"), "shortcode1", Array("tag1"),
          1623779104, Array(ThumbnailElements(150, 150, "https://instagramthumbnail_src1")), "https://thumbnail_src1",
          Array("url1", "url2"), "benz")),
          ProfileInfo(1623779107, Info("biography1", 1000, 500, "full_name1", "138289cc", is_business_account = true, is_joined_recently = false, is_private = false, 100,
            "https://profile_pic_url1"), "benz"))
      ).toDF()

      sampleDf.write.format("json").save(path)

      val bronzeLayer = new BronzeLayer(conf, sparkSession, path)
      bronzeLayer.createBronzeTable()

      createPostsInfoTable(s"${conf.rootPath}/${conf.database}/${conf.bronzeTable}", conf, spark)
      Thread.sleep(5000)

      val result = spark.read.format("delta").load(s"${conf.rootPath}/${conf.database}/${conf.postInfoTable}")
      val expectedResult = Seq(
        PostsData(comments_disabled = true, 100, "caption_text1", 50, null, "image_id1", is_video = false, null,
          "138289436", "shortcode1", "tag1", 1623779104, "benz")
      ).toDF()
      assert(result.except(expectedResult).isEmpty)
    }
  }

  test("getPostsInfo should select post info data from Bronze Layer" ) {
    withTempDir { dir =>
      val sparkSession = spark
      val conf = Configuration(dir.toString)
      conf.init(sparkSession)

      import sparkSession.implicits._

      val path = s"${conf.rootPath}/${conf.database}/${conf.sample}"

      val sampleDf = Seq(
        Data(Array(GraphImagesElements("Graph1", CommentsData(Array(DataElements(1623779105, "1382894360", OwnerData("138289436000", "https://profile_pic_url1", "mehrez"), "comment_text1"))),
          comments_disabled = true, DimensionsData(1080, 1920), "https://instagram.url1", Likes(100), Captions(Array(EdgesElements(NodeData("caption_text1")))),
          Comments(50), null, "image_id1", is_video = false, null, "s564gsd", Owner("138289436"), "shortcode1", Array("tag1"),
          1623779104, Array(ThumbnailElements(150, 150, "https://instagramthumbnail_src1")), "https://thumbnail_src1", Array("url1", "url2"),
          "benz")),
          ProfileInfo(1623779107, Info("biography1", 1000, 500, "full_name1", "138289cc", is_business_account = true, is_joined_recently = false, is_private = false, 100,
            "https://profile_pic_url1"), "benz"))
      ).toDF()

      sampleDf.write.format("json").save(path)

      val bronzeLayer = new BronzeLayer(conf, sparkSession, path)
      bronzeLayer.createBronzeTable()
      Thread.sleep(5000)

      val bronzeData = spark.read.load(s"${conf.rootPath}/${conf.database}/${conf.bronzeTable}")

      val result = getPostsInfo(bronzeData)

      val expectedResult = Seq(
        PostsData(comments_disabled = true, 100, "caption_text1", 50, null, "image_id1", is_video = false, null,
          "138289436", "shortcode1", "tag1", 1623779104, "benz")
      ).toDF()
      assert(result.except(expectedResult).isEmpty)
    }
  }
}