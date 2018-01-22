package it.unibo.scafi.simulation.gui.model.common.world
//REMEMBER TO DOCUMENTATE
/**
  * define a set of basic event produced by observable world
  */
object CommonWorldEvent {

  /**
    * the type of event in a world context
    */
  trait EventType

  /**
    * the type of event produced when in a world a new node is added
    */
  object NodesAdded extends EventType

  /**
    * the type of event produced when in a world a node is erased
    */
  object NodesRemoved extends EventType


}