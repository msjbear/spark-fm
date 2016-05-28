package de.kp.spark.core.actor

/* Copyright (c) 2014 Dr. Krusche & Partner PartG
 * 
 * This file is part of the Spark-Core project
 * (https://github.com/skrusche63/spark-core).
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

import de.kp.spark.core.{Configuration,Names}
import de.kp.spark.core.model._

/**
 * The ParamQuestor retrieves the parameters used
 * for a certain data mining or model building task
 */
class ParamQuestor(config:Configuration) extends RootActor(config) {

  def receive = {

    case req:ServiceRequest => {
      
      val origin = sender    
      val uid = req.data(Names.REQ_UID)
         
      val response = if (cache.paramsExist(req) == false) {           
        failure(req,messages.TASK_DOES_NOT_EXIST(uid))           

      } else {            
        params(req)
            
      }
           
      origin ! response
      context.stop(self)
      
    }
    
    case _ => {
      
      val origin = sender               
      val msg = messages.REQUEST_IS_UNKNOWN()          
          
      origin ! failure(null,msg)
      context.stop(self)

    }
  
  }

  protected def params(req:ServiceRequest):ServiceResponse = {
    
    val params = Params(cache.params(req))
    
    val uid = req.data(Names.REQ_UID)
    val data = Map(Names.REQ_UID -> uid, Names.REQ_RESPONSE -> serializer.serializeParams(params))
                
    new ServiceResponse(req.service,req.task,data,status.SUCCESS)	

  }

}