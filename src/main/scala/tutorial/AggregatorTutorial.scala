package tutorial

import scalaz._, Scalaz._  // uses scalaz 7.0.6, scala 2.11.0
 
object AggregatorTutorial extends App {
 
  /**
   * An aggregator contains a monoid and performs transformations on input and output.
   */ 
  trait Aggregator[-In,+Out] {
    type T // internal type for Monoid operations

    // Converts type In to type T
    def prepare(a: In): T
    // Converts type T to type Out
    def present(t: T): Out
    // Returns the monoid used by this Aggregator
    def monoid: Monoid[T]

    // Composes a new function before the existing prepare()
    def composePrepare[NewIn](f: NewIn => In): Aggregator[NewIn,Out] = ???     // TODO: define
    // Composes a new function after the existing present()
    def andThenPresent[NewOut](f: Out => NewOut): Aggregator[In,NewOut] = ???  // TODO: define
    // Appends a predicate-based filter to the input
    //  The identity element (zero) of a monoid should satisfy:
    //    append(a, zero) = append(zero, a) = a
    //  where a is some element of the set
    def withFilter(p: In => Boolean): Aggregator[In,Out] = ???  // TODO: define
  }

  object Aggregator {

    def fromMonoid[Z](m: Monoid[Z]): Aggregator[Z,Z] = ???  // TODO: define
    def compose[In,Out1,Out2](
      a1: Aggregator[In,Out1], 
      a2: Aggregator[In,Out2]
    ): Aggregator[In,(Out1,Out2)] = ???  // TODO: define
    def compose[In,Out1,Out2,Out3](
      a1: Aggregator[In,Out1],
      a2: Aggregator[In,Out2],
      a3: Aggregator[In,Out3]
    ): Aggregator[In,(Out1,Out2,Out3)] = ???  // TODO: define
  }

  lazy val MinMonoid: Monoid[Double] = Monoid.instance[Double](Math.min(_, _), java.lang.Double.MAX_VALUE)
  lazy val MaxMonoid: Monoid[Double] = Monoid.instance[Double](Math.max(_, _), java.lang.Double.MIN_VALUE)
  lazy val CountMonoid: Monoid[Long] = Monoid.instance[Long](_ + _, 0L)

  lazy val Min: Aggregator[Double,Double]      = ???  // TODO: define
  lazy val Max: Aggregator[Double,Double]      = ???  // TODO: define 
  lazy val Count: Aggregator[Any,Long]         = ???  // TODO: define
  lazy val CountPositive: Aggregator[Int,Long] = ???  // TODO: define (use withFilter) - counts > 0
  lazy val CountNegative: Aggregator[Int,Long] = ???  // TOOD: define (use withFilter) - counts < 0

  def runAggregator[In,Out](a: Aggregator[In,Out])(t: TraversableOnce[In]): Out = {
    type T = a.T
    val z: T = a.monoid.zero
    val seqOp:  (T,A) => T = ???  // TODO: define
    val combOp: (T,T) => T = ???  // TODO: define
    a.present( t.aggregate(z)(seqOp, combOp) )
  }
 
  // Basic usage
  val numbers = (1 to 10).map(_.toDouble).toList
  val count = runAggregator(Count)(numbers)
  val min   = runAggregator(Min)(numbers)
  val max   = runAggregator(Max)(numbers)
  println(s"count = $count (should be 10)")
  println(s"min   = $min   (should be 1)")
  println(s"max   = $max   (should be 10)")
 
  // Composition
  val compose2 = runAggregator( Aggregator.compose(Min, Max) )(numbers)
  val compose3 = runAggregator( Aggregator.compose(Count, Min, Max) )(numbers)
  println(s"compose2 = $compose2 (should be (1, 10))")
  println(s"compose3 = $compose3 (should be (10, 1, 10)")

  // Filtering
  val numbers2 = (-5 to 10).toList
  val nPositive = runAggregator(CountPositive)(numbers2)
  val nNegative = runAggregator(CountNegative)(numbers2)
  println(s"nPositive = $nPositive (should be 10)")
  println(s"nNegative = $nNegative (should be 5)")
 
}

