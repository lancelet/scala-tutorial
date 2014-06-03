package tutorial

import scalaz._, Scalaz._  // uses scalaz 7.0.6, scala 2.11.0
 
object AggregatorTutorial extends App {
 
  /**
   * An aggregator contains a `Monoid` and processes a `TraversableOnce` to compute something.
   *
   * The Monoid (see
   * [[https://github.com/scalaz/scalaz/blob/scalaz-seven/core/src/main/scala/scalaz/Monoid.scala]])
   * has an identity (or zero) element, and an associative binary operator.
   *
   * Input to the Aggregator is processed using the `prepare` method, while output is processed
   * using `present`.  These two methods separate the input and output types (`In` and `Out`) from
   * the internal representation that is used by the monoid (`T`).
   *
   * See the test class (tutorial.AggregatorSpec) for example usage and test cases.
   */ 
  trait Aggregator[-In,+Out] {

    protected type T // internal type for Monoid operations

    /**
     * Prepares a value for input to the Aggregator.
     * @param a input value
     * @return input value converted to the internal monoid type
     */
    protected def prepare(a: In): T

    /**
     * Presents a value as output from the Aggregator.
     * @param t internal monoid type to prepare for output
     * @return internal monoid type converted to the output type
     */
    protected def present(t: T): Out

    /**
     * Returns the monoid used by this Aggregator.
     */
    protected def monoid: Monoid[T]

    /**
     * Composes a new `prepare` function before the current `prepare`.
     * @param f `prepare` function to compose
     * @tparam NewIn new input type
     * @return new Aggregator
     */
    def composePrepare[NewIn](f: NewIn => In): Aggregator[NewIn,Out] = ??? // TODO: define

    /**
     * Composes a new `present` function after the current `present`.
     * @param f `present` function to compose
     * @tparam NewOut new output type
     * @return new Aggregator
     */
    def andThenPresent[NewOut](f: Out => NewOut): Aggregator[In,NewOut] = ??? // TODO: define

    /**
     * Filters input to the Aggregator.
     *
     * Only elements, e, for which p(e) is true are processed by the Aggregator.
     *
     * Hint: The identity element (zero) of a Monoid should satisfy:
     *   append(e, zero) == append(aero, e) == e
     *
     * @param p predicate for the filter
     * @tparam NewIn new input type
     * @return new Aggregator
     */
    def withFilter[NewIn <: In](p: NewIn => Boolean): Aggregator[NewIn,Out] = ??? // TODO: define

    /**
     * Runs the Aggregator on a `TraversableOnce` to produce a result.
     * @param t `TraversableOnce` on which to run the aggregator
     * @return result of the aggregation
     */
    def run(t: TraversableOnce[In]): Out = {
      val seqOp:  (T,In) => T = ???  // TODO: define
      val combOp: (T,T)  => T = ???  // TODO: define
      present( t.aggregate(monoid.zero)(seqOp, combOp) )
    }

  }

  object Aggregator {

    // Creates an Aggregator from a Scalaz Monoid
    def fromMonoid[Z](m: Monoid[Z]): Aggregator[Z,Z] = ???  // TODO: define

    // Composes two Aggregators
    def compose[In,Out1,Out2](
      a1: Aggregator[In,Out1],
      a2: Aggregator[In,Out2]
    ): Aggregator[In,(Out1,Out2)] = ???  // TODO: define

    // Composes three Aggregators
    def compose[In,Out1,Out2,Out3](
      a1: Aggregator[In,Out1],
      a2: Aggregator[In,Out2],
      a3: Aggregator[In,Out3]
    ): Aggregator[In,(Out1,Out2,Out3)] = ???  // TODO: define

    // Question: How would you abstract composition over arbitrary arity?  (Difficult problem!)

  }

  // Some sample Scalaz Monoids
  lazy val MinMonoid: Monoid[Double] = Monoid.instance[Double](Math.min(_, _), Double.MaxValue)
  lazy val MaxMonoid: Monoid[Double] = Monoid.instance[Double](Math.max(_, _), Double.MinValue)
  lazy val CountMonoid: Monoid[Long] = Monoid.instance[Long](_ + _, 0L)

  // Some sample Aggregators
  lazy val Min: Aggregator[Double,Double]      = ???  // TODO: define
  lazy val Max: Aggregator[Double,Double]      = ???  // TODO: define
  lazy val Count: Aggregator[Any,Long]         = ???  // TODO: define
  lazy val CountPositive: Aggregator[Int,Long] = ???  // TODO: define (use withFilter) - counts > 0
  lazy val CountNegative: Aggregator[Int,Long] = ???  // TOOD: define (use withFilter) - counts < 0

}

