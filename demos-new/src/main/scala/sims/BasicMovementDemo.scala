/*
 * Copyright (C) 2016-2017, Roberto Casadei, Mirko Viroli, and contributors.
 * See the LICENCE.txt file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package sims

import it.unibo.scafi.incarnations.BasicSimulationIncarnation.{AggregateProgram, BlockG}
import it.unibo.scafi.simulation.gui.configuration.environment.ProgramEnvironment.NearRealTimePolicy
import it.unibo.scafi.simulation.gui.incarnation.scafi.bridge.ScafiSimulationInitializer.RadiusSimulation
import it.unibo.scafi.simulation.gui.incarnation.scafi.bridge.reflection.{Demo, SimulationType}
import it.unibo.scafi.simulation.gui.incarnation.scafi.bridge.{MetaActionProducer, SimulationInfo}
import it.unibo.scafi.simulation.gui.incarnation.scafi.configuration.{ScafiInformation, ScafiProgramBuilder}
import it.unibo.scafi.simulation.gui.incarnation.scafi.world.ScafiWorldInitializer.Random
import it.unibo.scafi.simulation.gui.view.scalaFX.drawer.FastFXOutput
import lib.{FlockingLib, Movement2DSupport}

//use -Djavafx.animation.fullspeed=true to increase perfomance
object BasicMovementDemo extends App {
  ScafiProgramBuilder (
    Random(1000,1000,1000),
    SimulationInfo(program = classOf[BasicMovement],
      metaActions = List(MetaActionProducer.movementDtActionProducer),
      exportValutations = List.empty),
    RadiusSimulation(40),
    neighbourRender = true,
    outputPolicy = FastFXOutput,
    performance = NearRealTimePolicy
  ).launch()
}

@Demo(simulationType = SimulationType.MOVEMENT)
class BasicMovement extends AggregateProgram with SensorDefinitions with FlockingLib with BlockG with Movement2DSupport {

  private val attractionForce: Double = 10.0
  private val alignmentForce: Double = 40.0
  private val repulsionForce: Double = 80.0
  private lazy val repulsionRange: Double = ScafiInformation.configuration.simulationInitializer match {
    case RadiusSimulation(radius) => radius * 60.0 / 200
    case _ => 60.0 / 200
  }
  lazy val centerX = ScafiInformation.configuration.worldInitializer.size._1 / 2
  lazy val centerY = ScafiInformation.configuration.worldInitializer.size._2 / 2
  private val obstacleForce: Double = 400.0

  override def main:(Double, Double) = SizeConversion.normalSizeToWorldSize(rep(randomMovement())(behaviour2))

  private def behaviour1(tuple: ((Double, Double))): (Double, Double) =
    mux(sense1) {
      flock(tuple, Seq(sense1), Seq(sense3), repulsionRange, attractionForce, alignmentForce, repulsionForce, obstacleForce)
    } {
      tuple
    }

  private def behaviour2(tuple: ((Double, Double))): (Double, Double) =
    mux(sense1) {
      val m = clockwiseRotation(centerX, centerY)
      normalizeToScale(m._1,m._2)
    } {
      (.0, .0)
    }

  private def behaviour3(tuple: ((Double, Double))): (Double, Double) =
    mux(sense1) {
      val m = clockwiseRotation(centerX, centerY)
      val f = flock(tuple, Seq(sense1), Seq(sense3), repulsionRange, attractionForce, alignmentForce, repulsionForce, obstacleForce)
      normalizeToScale(m._1 + f._1, m._2 + f._2)
    } {
      (.0, .0)
    }
}