package com.fluidcode.processing.silver

import com.fluidcode.configuration.Configuration
import org.apache.spark.sql.SparkSession
import com.fluidcode.processing.silver.CreateProfileInfoTable.createProfileInfoTable

object Silver {
  def main(args: Array[String]): Unit = {
    val spark : SparkSession = SparkSession.builder()
      .appName("instagram_pipeline")
      .getOrCreate()

    val conf = Configuration(args(0))

    createProfileInfoTable(conf, spark)
  }
}