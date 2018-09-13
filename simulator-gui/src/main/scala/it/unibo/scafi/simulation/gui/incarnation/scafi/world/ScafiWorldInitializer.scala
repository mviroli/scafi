package it.unibo.scafi.simulation.gui.incarnation.scafi.world

import it.unibo.scafi.simulation.gui.configuration.information.WorldInitializer
import it.unibo.scafi.simulation.gui.incarnation.scafi.configuration.ScafiWorldInformation
import it.unibo.scafi.simulation.gui.model.graphics2D.BasicShape2D.Rectangle
import it.unibo.scafi.simulation.gui.model.space.Point3D

import scala.util.{Random => RandomGenerator}

/**
  * the interface of a scafi world initializer
  */
trait ScafiWorldInitializer extends WorldInitializer[ScafiWorldInformation]
object ScafiWorldInitializer {
  type INFO = ScafiWorldInformation
  val standardShape = Rectangle(2,2)
  /**
    * create a random world
    * @param node the node number
    * @param width the max width where node put in the world
    * @param height the max height where node put in the world
    */
  case class Random(node : Int,
                    width : Int,
                    height : Int) extends ScafiWorldInitializer {
    override def init(worldInfo : INFO): Unit = {
      scafiWorld.boundary = worldInfo.boundary
      val r = new RandomGenerator()
      //all nodes on the same 2d planes
      val z = 0
      scafiWorld clear()
      (0 until node) foreach {x => scafiWorld.insertNode(new scafiWorld.NodeBuilder(x,Point3D(r.nextInt(width),r.nextInt(height),z),worldInfo.shape,worldInfo.deviceProducers.toList))}

    }
  }

  /**
    * create a grid like world
    * @param space number of row
    * @param row number of column
    * @param column space between node
    */
  case class Grid(space : Int,
                  row: Int,
                  column : Int) extends ScafiWorldInitializer {
    override def init(worldInfo : INFO): Unit = {
      scafiWorld.boundary = worldInfo.boundary
      val z = 0
      var nodes = 0
      scafiWorld clear()
      for(i <- 1 to row) {
        for(j <- 1 to column) {
          nodes += 1
          scafiWorld.insertNode(new scafiWorld.NodeBuilder(nodes,Point3D(i * space,j * space,z),worldInfo.shape,worldInfo.deviceProducers.toList))
        }
      }
    }
  }
}