package com.adform.bitmaps

sealed trait BitmapOperation
trait BitmapDB[T] {
  def getBitmap(id: String): T
  def full: T
  case class BitmapReference(bitmap: T) extends BitmapOperation
}
case class Alternative(bitmaps: List[BitmapOperation]) extends BitmapOperation {
  override def toString = bitmaps.mkString("Or(", ",", ")")
}
case class Conjunction(bitmaps: List[BitmapOperation]) extends BitmapOperation {
  override def toString = bitmaps.mkString("And(", ",", ")")
}
case class Get(id: String) extends BitmapOperation {
  override def toString = s"""Get("$id")"""
}
case class Negation(not: BitmapOperation) extends BitmapOperation {
  override def toString = s"""Not(${not.toString})"""
}

case object Empty extends BitmapOperation
case object Full extends BitmapOperation

object And {
  def apply(bitmaps: List[BitmapOperation]): BitmapOperation = bitmaps match {
    case Nil => Empty
    case h :: Nil => h
    case _ => Conjunction(bitmaps)
  }

  def apply(bitmaps: BitmapOperation*): BitmapOperation = apply(bitmaps.toList)
  def optional(bitmaps: List[BitmapOperation]): Option[BitmapOperation] = Option(bitmaps).filter(_.nonEmpty).map(And.apply)
}

object Or {
  def apply(bitmaps: List[BitmapOperation]): BitmapOperation = bitmaps match {
    case Nil => Empty
    case h :: Nil => h
    case _ => Alternative(bitmaps)
  }
  def apply(bitmaps: BitmapOperation*): BitmapOperation = apply(bitmaps.toList)
  def optional(bitmaps: List[BitmapOperation]): Option[BitmapOperation] = Option(bitmaps).filter(_.nonEmpty).map(Or.apply)
}

object Not {
  def apply(not: BitmapOperation): BitmapOperation = not match {
    case Empty => Full
    case Full => Empty
    case Negation(bitmap) => bitmap
    case _ => Negation(not)
  }
}
