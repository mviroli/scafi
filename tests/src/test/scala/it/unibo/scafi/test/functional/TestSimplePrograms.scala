package it.unibo.scafi.test.functional

/**
 * Created by: Roberto Casadei
 * Created on date: 29/10/15
 */

import it.unibo.scafi.test.FunctionalTestIncarnation._
import org.scalatest._

class TestSimplePrograms extends FlatSpec with Matchers {
  import ScafiAssertions._
  import ScafiTestUtils._

  private[this] trait SimulationContextFixture {
    val net: Network with SimulatorOps =
      SetupNetwork(simulatorFactory.gridLike(n = 3, m = 3, stepx = 1, stepy = 1, eps = 0, rng = 1.5))
    implicit val node = new BasicAggregateInterpreter
  }

  private[this] def SetupNetwork(n: Network with SimulatorOps) = {
    n.addSensor(name = "sensor1", value = 0)
    n.chgSensorValue(name = "sensor1", ids = Set(1,8), value = 1)
    n.addSensor(name = "sensor2", value = "off")
    n.chgSensorValue(name = "sensor2", ids = Set(2,8), value = "on")
    n
  }

  it should "evaluate a field of node ids" in new SimulationContextFixture {
    // ARRANGE
    import node._
    // ACT
    implicit val endNet = runProgram{ mid() } (net)
    // ASSERT
    assertNetworkValues((0 to 8).zip(0 to 8).toMap)
  }

  it should "work with sensors" in new SimulationContextFixture {
    // ARRANGE
    import node._
    // ACT
    implicit val endNet = runProgram{
      Tuple2(sense[Int]("sensor1"),sense[String]("sensor2"))
    } (net)
    // ASSERT
    assertNetworkValues((0 to 8).zip(List(
      (0,"off"),(1,"off"),(0,"on"),
      (0,"off"),(0,"off"),(0,"off"),
      (0,"off"),(0,"off"),(1,"on"))).toMap)
  }
}
