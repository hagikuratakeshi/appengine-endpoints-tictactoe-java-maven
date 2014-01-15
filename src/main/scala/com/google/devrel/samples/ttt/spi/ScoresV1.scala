/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devrel.samples.ttt.spi

import java.io.IOException
import java.util.Date
import java.util.List
import javax.annotation.Nullable
import javax.inject.Named
import javax.jdo.PersistenceManager
import javax.jdo.Query
import com.google.api.server.spi.config.Api
import com.google.api.server.spi.config.ApiMethod
import com.google.appengine.api.oauth.OAuthRequestException
import com.google.appengine.api.users.User
import com.google.devrel.samples.ttt.PMF
import com.google.devrel.samples.ttt.Score
import com.google.devrel.samples.ttt.spi.Ids._
import ScoresV1._
import scala.collection.JavaConversions._

object ScoresV1 {
  private val WHEN = "1"
  private val OUTCOME = "2"
  private val DEFAULT_LIMIT = "10"
  private def getPersistenceManager(): PersistenceManager = PMF.get.getPersistenceManager
}

@Api(name = "tictactoe", version = "v1", clientIds = Array(Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID, Ids.IOS_CLIENT_ID), audiences = Array(Ids.ANDROID_AUDIENCE))
class ScoresV1 {

  @ApiMethod(name = "scores.list")
  def list(@Named("limit") limit: String, @Named("order") order: String, user: User): List[Score] = {
    val pm = getPersistenceManager
    val query = pm.newQuery(classOf[Score])
    var limitLocal = limit
    if (order != null) {
      if (order == WHEN) {
        query.setOrdering("played desc")
      } else if (order == OUTCOME) {
        query.setOrdering("outcome asc")
      }
    } else {
      query.setOrdering("played desc")
    }
    if (user != null) {
      query.setFilter("player == userParam")
      query.declareParameters("com.google.appengine.api.users.User userParam")
    } else {
      throw new OAuthRequestException("Invalid user.")
    }
    if (limitLocal == null) {
      limitLocal = DEFAULT_LIMIT
    }
    query.setRange(0, new java.lang.Long(limitLocal))
    pm.newQuery(query).execute(user).asInstanceOf[List[Score]]
  }

  @ApiMethod(name = "scores.insert")
  def insert(score: Score, user: User): Score = {
    if (user != null) {
      score.played = new Date
      score.player = user
      val pm = getPersistenceManager
      pm.makePersistent(score)
      pm.close()
      score
    } else {
      throw new OAuthRequestException("Invalid user.")
    }
  }
}
