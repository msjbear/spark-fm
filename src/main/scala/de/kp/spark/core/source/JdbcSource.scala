package de.kp.spark.core.source

/* Copyright (c) 2014 Dr. Krusche & Partner PartG
* 
* This file is part of the Spark-Core project
* (https://github.com/skrusche63/spark-arules).
* 
* Spark-Core is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* Spark-Core is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* Spark-Core. 
* 
* If not, see <http://www.gnu.org/licenses/>.
*/

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import de.kp.spark.core.model._

import de.kp.spark.core.{Configuration,Names}
import de.kp.spark.core.io.JdbcReader

/**
 * JdbcSource retrieves data from a Jdbc database through 
 * a SQL query and a specification of the fields that have 
 * to be put into the result. The data are returned as a Map.
 */
class JdbcSource(@transient sc:SparkContext) extends Serializable {
  
  def connect(config:Configuration,req:ServiceRequest,fields:List[String]):RDD[Map[String,Any]] = {
    
    val site  = req.data(Names.REQ_SITE).asInstanceOf[Int]
    val query = req.data(Names.REQ_QUERY).asInstanceOf[String]

    new JdbcReader(sc,config,site,query).read(fields)

  }

}