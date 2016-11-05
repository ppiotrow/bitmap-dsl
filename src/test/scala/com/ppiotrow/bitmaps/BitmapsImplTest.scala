package com.ppiotrow.bitmaps

import com.ppiotrow.bitmaps.Implicits.{ ConcurrentRoaringBitmapImpl, RoaringBitmapImpl }
import org.roaringbitmap.RoaringBitmap
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.concurrent.Future

class BitmapsImplTest extends MustMatchers with WordSpecLike with ScalaFutures {

  "RoaringBitmapImpl" must {
    "calculate batchAND" in {
      RoaringBitmapImpl.batchAnd(and) mustBe RoaringBitmap.bitmapOf(8, 9)
    }
    "calculate batchOR" in {
      RoaringBitmapImpl.batchOr(or) mustBe RoaringBitmap.bitmapOf(1, 2, 3, 4, 5, 7, 8, 9, 10)
    }
  }

  "ConcurentRoaringBitmapImpl" must {
    "calculate batchAND" in {
      val futures = and.map(Future.successful)
      whenReady(ConcurrentRoaringBitmapImpl.batchAnd(futures)) { result =>
        result mustBe RoaringBitmap.bitmapOf(8, 9)
      }
    }
    "calculate batchOR" in {
      val futures = or.map(Future.successful)
      whenReady(ConcurrentRoaringBitmapImpl.batchOr(futures)) { result =>
        result mustBe RoaringBitmap.bitmapOf(1, 2, 3, 4, 5, 7, 8, 9, 10)
      }
    }
    "calculate longer batchAnd" in {
      val futures = longerAnd.map(Future.successful)
      whenReady(ConcurrentRoaringBitmapImpl.batchAnd(futures)) { result =>
        result mustBe RoaringBitmap.bitmapOf(8)
      }
    }
    "calculate empty batchOr" in {
      whenReady(ConcurrentRoaringBitmapImpl.batchOr(Nil)) { result =>
        result mustBe RoaringBitmap.bitmapOf()
      }
    }
    "calculate empty batchAnd" in {
      whenReady(ConcurrentRoaringBitmapImpl.batchAnd(Nil)) { result =>
        result mustBe RoaringBitmap.bitmapOf()
      }
    }
    "calculate group sized batchAnd" in {
      val futures = longerAnd.take(ConcurrentRoaringBitmapImpl.GROUP_SIZE).map(Future.successful)
      whenReady(ConcurrentRoaringBitmapImpl.batchAnd(futures)) { result =>
        result mustBe RoaringBitmap.bitmapOf(8, 9)
      }
    }
  }

  val and = List(
    RoaringBitmap.bitmapOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
    RoaringBitmap.bitmapOf(5, 6, 7, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 2, 3, 4, 7, 8, 9, 10),
    RoaringBitmap.bitmapOf(4, 5, 6, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 2, 4, 6, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 7, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 5, 6, 7, 8, 9)
  )

  val or = List(
    RoaringBitmap.bitmapOf(1, 2, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 3, 5, 9, 10),
    RoaringBitmap.bitmapOf(1, 2, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 5, 10),
    RoaringBitmap.bitmapOf(1, 2, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 2, 8, 9, 10),
    RoaringBitmap.bitmapOf(1, 4, 7)
  )
  val longerAnd = and ::: List(
    RoaringBitmap.bitmapOf(3, 4, 7, 8, 9),
    RoaringBitmap.bitmapOf(8, 9, 10),
    RoaringBitmap.bitmapOf(1, 2, 4, 6, 8, 9, 10),
    RoaringBitmap.bitmapOf(8, 9, 10),
    RoaringBitmap.bitmapOf(1, 5, 6, 7, 8, 9),
    RoaringBitmap.bitmapOf(1, 2, 3, 8, 9, 10),
    RoaringBitmap.bitmapOf(8, 9, 10),
    RoaringBitmap.bitmapOf(1, 8),
    RoaringBitmap.bitmapOf(1, 7, 8, 9),
    RoaringBitmap.bitmapOf(1, 8, 9, 10),
    RoaringBitmap.bitmapOf(2, 8, 9, 10)
  )
}
