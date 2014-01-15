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

package com.google.devrel.samples.ttt.spi

import java.util.Random
import com.google.api.server.spi.config.Api
import com.google.api.server.spi.config.ApiMethod
import com.google.devrel.samples.ttt.Board
import com.google.devrel.samples.ttt.spi.Ids._
import BoardV1._
import scala.collection.JavaConversions._

object BoardV1 {
  val X = 'X'
  val O = 'O'
  val DASH = '-'
}

/**
 * Defines v1 of a Board resource as part of the tictactoe API, which provides
 * clients the ability to query for a computer's next move given an input
 * board.
 */
@Api(
    name = "tictactoe",
    version = "v1",
    clientIds = Array(Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID, Ids.IOS_CLIENT_ID),
    audiences = Array(Ids.ANDROID_AUDIENCE)
)
class BoardV1 {

  @ApiMethod(name = "board.getmove", httpMethod = "POST")
  def getmove(board: Board): Board = {
    var parsed = parseBoard(board.getState)
    val free = countFree(parsed)
    parsed = addMove(parsed, free)
    val builder = new StringBuilder()
    for (i <- 0 until parsed.length) {
      builder.append(String.valueOf(parsed(i)))
    }
    val updated = new Board()
    updated.setState(builder.toString)
    updated
  }

  private def parseBoard(boardString: String): Array[Array[Char]] = {
    val board = Array.ofDim[Char](3, 3)
    val chars = boardString.toCharArray()
    if (chars.length == 9) {
      for (i <- 0 until chars.length) {
        board(i / 3)(i % 3) = chars(i)
      }
    }
    board
  }

  private def countFree(board: Array[Array[Char]]): Int = {
    var count = 0
    for (i <- 0 until board.length; j <- 0 until board(i).length 
      if board(i)(j) != BoardV1.X && board(i)(j) != BoardV1.O) {
      count += 1
    }
    count
  }

  private def addMove(board: Array[Array[Char]], free: Int): Array[Array[Char]] = {
    val index = new Random().nextInt(free) + 1
    var freeLocal = free
    for (i <- 0 until board.length; j <- 0 until board.length 
      if board(i)(j) == BoardV1.DASH) {
      if (freeLocal == index) {
        board(i)(j) = BoardV1.O
        return board
      } else {
        freeLocal = freeLocal - 1
      }
    }
    board
  }
}
