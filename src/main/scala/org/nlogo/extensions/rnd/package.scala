package org.nlogo.extensions

package object rnd {
  def pluralize(count: Int, word: String) =
    count + " " + word + (if (count != 1) "s" else "")
}