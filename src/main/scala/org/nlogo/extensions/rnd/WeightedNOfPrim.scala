// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.extensions.rnd

import scala.collection.JavaConverters._
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

object WeightedNOfPrim extends DefaultReporter {
  val name = "WEIGHTED-N-OF"

  override def getSyntax = reporterSyntax(
    Array(NumberType, ListType | AgentsetType, ReporterTaskType),
    ListType | AgentsetType)
  def report(args: Array[Argument], context: Context): AnyRef = {

    val n: Int = getN(args(0))
    val candidates: Vector[AnyRef] = getCandidates(n, args(1))
    if (n == candidates.size) return args(1).get
    val weightFunction = getWeightFunction(args(2), context)
    val weights = candidates.map(weightFunction)

    val count = weights.count(_ > 0.0)
    if (count < n) throw new ExtensionException(
      "Requested " + n + " random items from " + count + " candidates with weight > 0.0.")

    val indices =
      if (count == n) {
        weights.zipWithIndex.collect { case (w, i) if w > 0 ⇒ i }
      } else {
        val probs = cumulativeProbabilities(weights)
        pickIndices(n, probs, context.getRNG)
      }

    args(1).get match {
      case list: LogoList ⇒
        val b = new LogoListBuilder
        for (i ← indices) b.add(candidates(i))
        b.toLogoList
      case agentSet: agent.AgentSet ⇒
        val b = Array.newBuilder[agent.Agent]
        for (i ← indices) b += candidates(i).asInstanceOf[agent.Agent]
        new agent.ArrayAgentSet(agentSet.`type`, b.result, agentSet.world)
    }
  }

  def getN(arg: Argument): Int = {
    val n = arg.getIntValue
    if (n < 0) throw new ExtensionException(I18N.errors.getN(
      "org.nlogo.prim.etc.$common.firstInputCantBeNegative", name))
    n
  }

  def getCandidates(n: Int, arg: Argument): Vector[AnyRef] = {
    val candidates =
      arg.get match {
        case list: LogoList           ⇒ list.toVector
        case agentSet: agent.AgentSet ⇒ Vector() ++ agentSet.agents.asScala
      }
    if (candidates.size < n) throw new ExtensionException(
      "Requested " + n + " random items from " + candidates.size + " candidates.")
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

  def cumulativeProbabilities(weights: Iterable[Double]): Array[Double] = {
    val probs = Array.ofDim[Double](weights.size)
    var sum = 0.0
    for ((w, i) ← weights.zipWithIndex) {
      sum += w
      probs(i) = sum
    }
    probs
  }

  def pickIndices(n: Int, probs: Array[Double], rnd: MersenneTwisterFast): SortedSet[Int] = {
    /*
     * This is a much too naive implementation.
     * 
     * Potential problem:
     *   rnd:weighted-n-of 999 (n-values 1000 [ ? ]) [ ifelse-value (? = 0) [ 1E10 ] [ 1E-10 ] ]
     * ...won't terminate in a long long time.
     * 
     * Potential fix: exclude already selected items and 
     * recalculate cumulative probabilities?
     */
    val max = probs.last
    var picks = SortedSet[Int]()
    while (picks.size < n) {
      val target = rnd.nextDouble * max
      picks += probs.indexWhere(_ > target)
    }
    picks
  }
}
