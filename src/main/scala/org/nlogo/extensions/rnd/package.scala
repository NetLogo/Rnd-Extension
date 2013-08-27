// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.extensions

import scala.collection.SortedSet

import org.nlogo.util.MersenneTwisterFast

package object rnd {
  def pluralize(count: Int, word: String) =
    count + " " + word + (if (count != 1) "s" else "")

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
