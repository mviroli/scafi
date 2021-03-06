package it.unibo.scafi.distrib.actor

import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorRef}

import scala.concurrent.duration.FiniteDuration

/**
 * @author Roberto Casadei
 *
 */

trait PlatformMessages { self: Platform.Subcomponent =>

  // Input/information messages (which provides data to the recipient actor)
  case class MsgLocalSensorValue[T](name: LSNS, value: T)
  case class MsgSensorValue(id: ID, name: LSNS, value: Any)
  case class MsgNbrSensorValue(name: NSNS, values: Map[ID, Any])
  case class MsgExport(from: ID, export: EXPORT) extends ScafiMessage
  case class MsgExports(exports: Map[ID, EXPORT])
  case class MsgDeviceLocation(id: ID, ref: ActorRef)
  case class MsgWithDevices(devs: Map[ID, ActorRef])
  case class MsgNeighbor(id: ID, idn: ID)
  case class MsgNeighborhood(id: ID, nbrs: Set[ID])
  case class MsgMyFrequency(delay: FiniteDuration)
  case class MyNameIs(id: ID)
  case class MsgRound(id: ID, n: Long)
  case class MsgProgram(ap: ExecutionTemplate, dependencies: Set[Class[_]] = Set())
  case class MsgAddSensor(name: LSNS, provider: ()=>Any)
  case class MsgAddPushSensor(ref: ActorRef)
  case class MsgAddActuator(name: LSNS, consumer: Any=>Unit)
  case class DevInfo(nid: ID, ref: ActorRef)

  // Invitation messages (please do "that" for me; the sender expects no reply)
  case class MsgRegistration(id: ID)
  case class MsgSetFrequency(n: Int, unit: TimeUnit)
  case class MsgRemoveNeighbor(idn: ID)
  case class MsgShipProgram(programMsg: MsgProgram)
  case class MsgDeliverTo(id: ID, msg: Any)

  // Command messages (please do "that" for me; the sender *does* expect some reply)
  case class MsgAddDevice(id: ID, props: Props)

  // Request/Response messages (the sender expects a reply from the recipient)
  case class MsgGetNbrSensorValue(sns: NSNS, idn: ID)
  case class MsgGetSensorValue(sns: LSNS)
  case class MsgLookup(id: ID)
  case class MsgGetNeighborhood(id: ID)
  case class MsgGetNeighborhoodExports(id: ID)
  case class MsgNeighborhoodExports(id: ID, nbrs: Map[ID,Option[EXPORT]]) extends ScafiMessage
  val MsgGetIds = "msg_get_ids".hashCode
  val MsgGetExport = "msg_get_export".hashCode
  val MsgGetNeighbors = "msg_get_neighbors".hashCode
  case class Ack(id: ID)

}
