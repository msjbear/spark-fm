package de.kp.spark.fm.actor
/* Copyright (c) 2014 Dr. Krusche & Partner PartG
 * 
 * This file is part of the Spark-FM project
 * (https://github.com/skrusche63/spark-cluster).
 * 
 * Spark-FM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Spark-FM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * Spark-FM. 
 * 
 * If not, see <http://www.gnu.org/licenses/>.
 */

import de.kp.spark.fm.model._
import de.kp.spark.fm.io.{ElasticBuilderFactory => EBF,ElasticIndexer}

class FMIndexer extends BaseActor {
  
  def receive = {
    
    case req:ServiceRequest => {
     
      val origin = sender    
      val uid = req.data("uid")

      try {

        val index   = req.data("index")
        val mapping = req.data("type")
    
        val (names,types) = fieldspec(req.data)
    
        val builder = EBF.getBuilder("feature",mapping,names,types)
        val indexer = new ElasticIndexer()
    
        indexer.create(index,mapping,builder)
        indexer.close()
      
        val data = Map("uid" -> uid, "message" -> Messages.SEARCH_INDEX_CREATED(uid))
        val response = new ServiceResponse(req.service,req.task,data,FMStatus.SUCCESS)	
      
        val origin = sender
        origin ! Serializer.serializeResponse(response)
      
      } catch {
        
        case e:Exception => {
          
          log.error(e, e.getMessage())
      
          val data = Map("uid" -> uid, "message" -> e.getMessage())
          val response = new ServiceResponse(req.service,req.task,data,FMStatus.FAILURE)	
      
          val origin = sender
          origin ! Serializer.serializeResponse(response)
          
        }
      
      } finally {
        
        context.stop(self)

      }
    }
    
  }
 
  private def fieldspec(params:Map[String,String]):(List[String],List[String]) = {
    
    val records = params.filter(kv => kv._1.startsWith("lbl.") || kv._1.startsWith("fea."))
    val spec = records.map(rec => {
      
      val (k,v) = rec
      /* Actually all values are specified as string types */
      val _name = k.replace("lbl.","").replace("fea.","")
      val _type = "string"    

      (_name,_type)
    
    })
    
    val names = spec.map(_._1).toList
    val types = spec.map(_._2).toList
    
    (names,types)
    
  }  
 
}