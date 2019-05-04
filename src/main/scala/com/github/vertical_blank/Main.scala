package com.github.vertical_blank;

import java.net.{ ServerSocket, Socket }
import java.io.{ BufferedReader, InputStreamReader }
import java.nio.ByteBuffer

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Main {
  val pgHost: String = "localhost"
  val pgPort: Int = 5432

  val localport = 5439

  def main(args: Array[String]): Unit = {
    println("hello")

    val server = new ServerSocket(localport)

    def infiniteLoop(doWait: => Unit): Unit = {
      doWait
      infiniteLoop(doWait)
    }

    infiniteLoop {
      val socket = server.accept()
      println("socked")

      val clientIn = socket.getInputStream
      val clientOut = socket.getOutputStream

      val pgSocket = new Socket(pgHost, pgPort)
      val pgOut = pgSocket.getOutputStream
      val pgIn = pgSocket.getInputStream

      def loop(f: => Boolean): Unit = if (f) { loop(f) }

      // client to postgres
      Future {
        loop {
          val buf = new Array[Byte](4096)
          val in = clientIn.read(buf)
          // print("from cl: ")
          // println(buf.toList.take(in))

          buf.headOption.map(_.toChar).foreach {
            case 'Q' | 'E' | 'P'
              => println(new String(buf.tail.drop(4))) // TODO refer LEN field. shoud use Seq.splitAt
            case _ => Unit
          }

          if (in == -1) {
            clientIn.close()
            false
          } else {
            pgOut.write(buf, 0, in)
            true
          }
        }
      }

      // postgres to client
      Future {
        loop {
          val buf = new Array[Byte](4096)
          val in = pgIn.read(buf)
          // print("from pg: ")
          // println(buf.toList.take(in))
          if (in == -1) {
            pgIn.close()
            false
          } else {
            clientOut.write(buf, 0, in)
            true
          }
        }
      }
    }
  }
}
