/* Copyright 2013 Google Inc. All Rights Reserved.
 *
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

package com.google.devrel.samples.ttt

import java.util.Date
import javax.jdo.annotations.IdGeneratorStrategy
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.PrimaryKey
import com.google.appengine.api.users.User
import scala.reflect.{ BeanProperty, BooleanBeanProperty }
import scala.collection.JavaConversions._

object Score {
}

@PersistenceCapable(identityType = IdentityType.APPLICATION)
class Score(@Persistent @BeanProperty var player: User, 
    @Persistent @BeanProperty var outcome: String, 
    @Persistent @BeanProperty var played: Date) {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @BeanProperty
  var id: java.lang.Long = _
}
