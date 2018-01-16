package it.unibo.scafi.simulation.gui.test.core

import it.unibo.scafi.simulation.gui.model.common.sensor.Sensor
import it.unibo.scafi.simulation.gui.model.common.world.ObservableWorld
import it.unibo.scafi.simulation.gui.model.space.{Point, Point2D}
import it.unibo.scafi.simulation.gui.test.help.{BasicTestableAggregateDevice, BasicTestableAggregateNode, BasicTestableAggregateWorld, BasicTestableObserverWorld}
import org.scalatest.{FunSpec, Matchers}

class BasicAggregateWorldTest extends FunSpec with Matchers{
  val checkThat = new ItWord
  val point = Point2D(1,1)
  val aggregateWorld = new BasicTestableAggregateWorld
  val dev = new BasicTestableAggregateDevice("mydevice",false)
  val superDevice = new BasicTestableAggregateDevice("adevice",true) with Sensor {
    override type VALUE = String

    override def value: VALUE = "nothing"
  }

  val node = new BasicTestableAggregateNode(id = 1,devices = Set(dev),position = Point.ZERO)
  val anotherNode = new BasicTestableAggregateNode(id = 2, devices = Set(dev), position = point)
  checkThat("An aggregate node is immutable") {
    val pos = node.position
    node.movedTo(point)
    assert(node.position == pos)
    val currentDev = node.devices
    node.removeDevice(dev)
    assert(node.devices == currentDev)
    val simpleDev = node.getDevice(dev.name)
    node.turnOnDevice(dev.name)
    val sameDev = node.getDevice(dev.name)
    assert(simpleDev.get.state == sameDev.get.state)
  }
  val observer = new BasicTestableObserverWorld with ObservableWorld.ObserverWorld
  checkThat("clear queue of event") {
    observer.clearChange
    assert(observer.nodeChanged.isEmpty)
  }

  aggregateWorld <-- observer
  checkThat("i can add a node in the world") {
    assert(aggregateWorld.insertNode(node))
  }
  checkThat("i can change the position of a node in the world") {
    assert(aggregateWorld.moveNode(node,point))
    val changeNode= aggregateWorld(node.id)
    assert(changeNode.isDefined)
    assert(changeNode.get.position == point)
  }
  checkThat("i can change the state of device in a node in the aggregateWorld") {
    assert(aggregateWorld.switchOnDevice(node,dev.name))
    val changedNode = aggregateWorld(node.id)
    assert(changedNode.isDefined)
    val changedDev = changedNode.get.getDevice(dev.name)
    assert(changedDev.isDefined)
    assert(changedDev.get.state)
    assert(aggregateWorld.switchOffDevice(node,dev.name))
  }
  aggregateWorld + anotherNode
  checkThat("i can switch on a set of device") {
    assert(aggregateWorld.switchOnDevices( Map(node -> dev.name, anotherNode -> dev.name)))
  }
  checkThat("i can switch off a set of device") {
    assert(aggregateWorld.switchOffDevices( Map(node -> dev.name, anotherNode -> dev.name)))
  }
  checkThat("i can add a device") {
    assert(aggregateWorld.addDevice(node,superDevice))
    val changedNode = aggregateWorld(node.id)
    assert(changedNode.isDefined)
    assert(changedNode.get.getDevice(superDevice.name).isDefined)
    assert(aggregateWorld.removeDevice(node,superDevice))
    val anotherChangedNode = aggregateWorld(node.id)
    assert(anotherChangedNode.isDefined)
    assert(anotherChangedNode.get.getDevice(superDevice.name).isEmpty)
  }

  checkThat("i can add a set of device") {
    assert(aggregateWorld.addDevices(Map(node -> superDevice, anotherNode -> superDevice)))
  }

  checkThat("i can remove a set of device") {
    assert(aggregateWorld.removeDevices(Map(node -> superDevice, anotherNode -> superDevice)))
  }

  checkThat("multiple event store only the node changed") {
    observer.clearChange
    assert(observer.nodeChanged.isEmpty)
    aggregateWorld.switchOnDevice(node,dev.name)
    assert(observer.nodeChanged.size == 1)
    aggregateWorld.switchOffDevice(node,dev.name)
    aggregateWorld.addDevice(node,superDevice)
    aggregateWorld.removeDevice(node,superDevice)
    assert(observer.eventCount() > 1)
  }
}