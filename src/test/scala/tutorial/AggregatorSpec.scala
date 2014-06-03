package tutorial

import org.specs2._
import org.scalacheck.{Prop, Gen}, Prop.forAll

import scalaz._, Scalaz._

import AggregatorTutorial._

class AggregatorSpec extends Specification with ScalaCheck { def is = s2"""

Aggregator
==========

Operations:
  run $run
  composePrepare $composePrepare
  andThenPresent $andThenPresent
  withFilter $withFilter

Creation:
  create $create
  fromMonoid $fromMonoid

Individual aggregators:
  count aggregator $count
  min aggregator $min
  max aggregator $max

Composition:
  compose two $compose2
  compose three $compose3

Filtering:
  count positive $countPositive
  count negative $countNegative

"""

  def run = {
    val a = new TestAggregator
    a.run(List(1,2,3)) must_== 6
  }

  def composePrepare = {
    val a = new TestAggregator
    val b = a.composePrepare[Int]{ i => i.toDouble }
    b.run(List(42))
    a.lastPrepared must_== 42.0
  }

  def andThenPresent = {
    val a = new TestAggregator
    val b = a.andThenPresent[Int]{ l => l.toInt }
    b.run(List(42.0))
    a.lastPresented must_== 42.0
  }

  def withFilter = {
    val a = new TestAggregator
    val b = a.withFilter[Double]{ d => d > 0 }
    b.run(List(-10.0, 42.0))
    a.lastPrepared must_== 42.0
  }

  def create = {
    val a = Aggregator.create[Int,Int,Int] (identity, identity, Monoid.instance[Int](_ + _, 0))
    a.run(List(1, 2, 3)) must_== 6
  }

  def fromMonoid = {
    val a = Aggregator.fromMonoid(Monoid.instance[Int](_ + _, 0))
    a.run(List(5, 2, 3)) must_== 10
  }

  def count = forAll { l: List[Int]    => (l.length > 0) ==> (Count.run(l) === l.length.toLong) }
  def min   = forAll { l: List[Double] => (l.length > 0) ==> (Min.run(l)   === l.min          ) }
  def max   = forAll { l: List[Double] => (l.length > 0) ==> (Max.run(l)   === l.max          ) }

  def compose2 = forAll { l: List[Double] => (l.length > 0) ==>
    (Aggregator.compose(Min, Max).run(l) === (l.min, l.max))
  }
  def compose3 = forAll { l: List[Double] => (l.length > 0) ==>
    (Aggregator.compose(Count, Min, Max).run(l) === (l.length.toLong, l.min, l.max))
  }

  def countPositive = forAll { l: List[Int] => (l.filter(_ > 0).length > 0) ==>
    (CountPositive.run(l) === l.filter(_ > 0).length)
  }
  def countNegative = forAll { l: List[Int] => (l.filter(_ < 0).length > 0) ==>
    (CountNegative.run(l) === l.filter(_ < 0).length)
  }

  // Special stateful Aggregator only used for testing
  private class TestAggregator extends Aggregator[Double,Long] {
    protected type T = Int
    protected def prepare(a: Double) = {
      m_lastPrepared = a
      a.toInt
    }
    protected def present(t: Int) = {
      m_lastPresented = t
      t.toLong
    }
    protected def monoid = Monoid.instance[Int](_ + _, 0)

    private var m_lastPrepared: Double = 0.0
    private var m_lastPresented: Int   = 0
    def lastPrepared: Double  = m_lastPrepared
    def lastPresented: Double = m_lastPresented
  }

}
