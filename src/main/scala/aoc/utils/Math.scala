package aoc.utils

/**
 * Created by 
 *
 * @author Raphael Ludwig
 * @version 10.12.21
 */
object Math {

  def medianAndSortedList[T](list: Seq[T])(implicit integral: Integral[T], ordering: Ordering[T]): (Seq[T], T) = {
    val sorted = list.sorted
    val median = if (list.size % 2 == 1) sorted(list.size/2)
    else integral.quot(integral.plus(sorted(list.size/2-1), sorted(list.size/2)), integral.fromInt(2))

    (sorted, median)
  }

  def median[T](list: Seq[T])(implicit integral: Integral[T], ordering: Ordering[T]): T = {
    medianAndSortedList(list)._2
  }

}
