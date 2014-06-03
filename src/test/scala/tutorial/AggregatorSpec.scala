package tutorial

import org.specs2._
import org.scalacheck.{Prop, Gen}, Prop.forAll

import AggregatorTutorial._

class AggregatorSpec extends Specification with ScalaCheck { def is = s2"""

Aggregator
==========

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

  def count = forAll { l: List[Int]    => Count.run(l) === l.length.toLong }
  def min   = forAll { l: List[Double] => Min.run(l)   === l.min           }
  def max   = forAll { l: List[Double] => Max.run(l)   === l.max           }

  def compose2 = forAll { l: List[Double] =>
    Aggregator.compose(Min, Max).run(l) === (l.min, l.max)
  }
  def compose3 = forAll { l: List[Double] =>
    Aggregator.compose(Count, Min, Max).run(l) === (l.length.toLong, l.min, l.max)
  }

  def countPositive = forAll { l: List[Int] =>
    CountPositive.run(l) === l.filter(_ > 0).length
  }
  def countNegative = forAll { l: List[Int] =>
    CountNegative.run(l) === l.filter(_ < 0).length
  }

}
