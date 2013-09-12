package org.nlogo.extensions.rnd

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.collection.immutable
import scala.collection.immutable.SortedSet
import scala.collection.mutable.ListBuffer

import org.nlogo.util.MersenneTwisterFast

object Picker {
  def newPickFunction(
    allWeights: immutable.IndexedSeq[Double],
    unselectedIndices: immutable.IndexedSeq[Int],
    rng: MersenneTwisterFast): () ⇒ Int = {
    val unselectedWeights = unselectedIndices.map(allWeights)
    val sum = unselectedWeights.sum
    val pick: () ⇒ Int =
      if (sum == 0.0) { // if we only have 0s left, just pick one at random
        () ⇒ rng.nextInt(unselectedIndices.length)
      } else {
        val probs: ListBuffer[java.lang.Double] =
          unselectedWeights.map(w ⇒ Double.box(w / sum))(breakOut)
        val aliasMethod = new AliasMethod(probs.asJava, rng)
        () ⇒ aliasMethod.next
      }
    () ⇒ unselectedIndices(pick())
  }

  def pickIndices(
    n: Int,
    candidates: Vector[AnyRef],
    weightFunction: (AnyRef) ⇒ Double,
    rng: MersenneTwisterFast): SortedSet[Int] = {
    val weights = candidates.map(weightFunction)
    @tailrec def loop(
      unselected: Set[Int] = weights.indices.toSet,
      selected: SortedSet[Int] = SortedSet.empty[Int]) //
      (picker: () ⇒ Int = newPickFunction(weights, unselected.toIndexedSeq, rng)): SortedSet[Int] =
      if (selected.size == n)
        selected
      else {
        val i = picker()
        if (selected.contains(i))
          // use new picker if we start getting duplicates
          loop(unselected, selected)()
        else
          loop(unselected - i, selected + i)(picker)
      }
    loop()()
  }
}
