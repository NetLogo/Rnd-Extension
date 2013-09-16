// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.extensions.rnd

import scala.collection.immutable.SortedSet

import org.nlogo.agent
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.I18N
import org.nlogo.api.LogoList
import org.nlogo.api.LogoListBuilder
import org.nlogo.api.Syntax._
import org.nlogo.nvm
import org.nlogo.util.MersenneTwisterFast

trait WeightedRndPrim extends DefaultReporter {
  val name: String

  def getCandidates(minSize: Int, arg: Argument, rng: MersenneTwisterFast): Vector[AnyRef] = {
    val candidates =
      arg.get match {
        case list: LogoList ⇒ list.toVector
        case agentSet: agent.AgentSet ⇒
          val b = Vector.newBuilder[AnyRef]
          val it = agentSet.shufflerator(rng)
          while (it.hasNext) b += it.next
          b.result
      }
    if (candidates.size < minSize) throw new ExtensionException(
      "Requested " + pluralize(minSize, "random item") +
        " from " + pluralize(candidates.size, "candidate") + ".")
    candidates
  }

  def getWeightFunction(arg: Argument, context: Context): (AnyRef) ⇒ Double = {
    val task = arg.getReporterTask.asInstanceOf[nvm.ReporterTask]
    if (task.formals.size > 1) throw new ExtensionException(
      "Task expected only 1 input but got " + task.formals.size + ".")
    (obj: AnyRef) ⇒ {
      val res = task.report(context, Array(obj))
      val w = try
        res.asInstanceOf[Number].doubleValue
      catch {
        case e: ClassCastException ⇒ throw new ExtensionException(
          "Got " + Dump.logoObject(res) + " as a weight but all weights must be numbers.")
      }
      if (w < 0.0) throw new ExtensionException(
        "Got " + w + " as a weight but all weights must be >= 0.0.")
      w
    }
  }

  def pluralize(count: Int, word: String) =
    count + " " + word + (if (count != 1) "s" else "")
}

object WeightedOneOfPrim extends WeightedRndPrim {

  override val name = "WEIGHTED-ONE-OF"

  override def getSyntax = reporterSyntax(
    Array(ListType | AgentsetType, ReporterTaskType),
    WildcardType)

  def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get match {
      case agentSet: agent.AgentSet if agentSet.count == 0 ⇒
        org.nlogo.api.Nobody$.MODULE$
      case _ ⇒
        val rng = context.getRNG
        val candidates: Vector[AnyRef] = getCandidates(1, args(0), rng)
        val weightFunction = getWeightFunction(args(1), context)
        val i = Picker.pickIndicesWithRepeats(1, candidates, weightFunction, rng).head
        candidates(i)
    }
}

trait WeightedNOfPrim extends WeightedRndPrim {

  def pickIndices(n: Int,
    candidates: Vector[AnyRef],
    weightFunction: (AnyRef) ⇒ Double,
    rng: MersenneTwisterFast): Iterable[Int]

  val allowRepeats: Boolean

  def report(args: Array[Argument], context: Context): AnyRef = {
    val n = args(0).getIntValue
    if (n < 0) throw new ExtensionException(I18N.errors.getN(
      "org.nlogo.prim.etc.$common.firstInputCantBeNegative", name))
    val rng = context.getRNG
    val minSize = if (allowRepeats) math.min(n, 1) else n
    val candidates: Vector[AnyRef] = getCandidates(minSize, args(1), rng)
    val weightFunction = getWeightFunction(args(2), context)
    val indices = pickIndices(n, candidates, weightFunction, rng)

    def buildList: LogoList = {
      val b = new LogoListBuilder
      for (i ← indices) b.add(candidates(i))
      b.toLogoList
    }

    def buildAgentSet(originalAgentSet: agent.AgentSet): agent.ArrayAgentSet = {
      val b = Array.newBuilder[agent.Agent]
      for (i ← indices) b += candidates(i).asInstanceOf[agent.Agent]
      new agent.ArrayAgentSet(originalAgentSet.`type`, b.result, originalAgentSet.world)
    }

    args(1).get match {
      case _: LogoList                       ⇒ buildList
      case _: agent.AgentSet if allowRepeats ⇒ buildList
      case agentSet: agent.AgentSet          ⇒ buildAgentSet(agentSet)
    }
  }
}

object WeightedNOfWithoutRepeatsPrim extends WeightedNOfPrim {
  override val name = "WEIGHTED-N-OF"
  override val allowRepeats = false
  override def getSyntax = reporterSyntax(
    Array(NumberType, ListType | AgentsetType, ReporterTaskType),
    ListType | AgentsetType) // returns either list or agentset depending on args(1)
  override def pickIndices(n: Int,
    candidates: Vector[AnyRef],
    weightFunction: (AnyRef) ⇒ Double,
    rng: MersenneTwisterFast): SortedSet[Int] =
    Picker.pickIndicesWithoutRepeats(n, candidates, weightFunction, rng)
}

object WeightedNOfWithRepeatsPrim extends WeightedNOfPrim {
  override val name = "WEIGHTED-N-OF-WITH-REPEATS"
  override val allowRepeats = true
  override def getSyntax = reporterSyntax(
    Array(NumberType, ListType | AgentsetType, ReporterTaskType),
    ListType) // always returns a list because agentsets don't allow repeats
  override def pickIndices(n: Int,
    candidates: Vector[AnyRef],
    weightFunction: (AnyRef) ⇒ Double,
    rng: MersenneTwisterFast): Seq[Int] =
    Picker.pickIndicesWithRepeats(n, candidates, weightFunction, rng)
}
