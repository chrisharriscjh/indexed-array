package sportarray

import Skeleton.{DataType, PositionsData, ValuesData, WeightsData, PricesData}
import Skeleton.IsIdxElem
import IndicesObj.{IsIndex}

object ArrayDefs {

  sealed trait IsBaseArr[DataT <: DataType] { }

  trait IsDatum[I0 <: IsIdxElem[_], DataT <: DataType] extends IsBaseArr[DataT] {
    val ref: I0
    val value: DataT#ElemT
  }

  trait Is1dIndexArr[I0 <: IsIdxElem[_], DataT <: DataType] extends IsBaseArr[DataT] {
    type Self = Is1dIndexArr[I0, DataT]
    val indices: (IsIndex[I0])
    def loc(at: I0): Option[IsDatum[I0, DataT]]
  }

  trait Is2dIndexArr[I0 <: IsIdxElem[_], I1 <: IsIdxElem[_], DataT <: DataType] extends IsBaseArr[DataT] {
    type Self = Is2dIndexArr[I0, I1, DataT]
    val indices: (IsIndex[I0], IsIndex[I1])
    def getDim0Slice(loc: I0): Option[Is1dIndexArr[I1, DataT]]
    def getDim1Slice(loc: I1): Option[Is1dIndexArr[I0, DataT]]
    //def addDelta(delta: SelfMinus1T): Self
    def loc[I](i: I)(implicit tc: LocTC2d[I]): tc.Out = tc(i)

    trait LocTC2d[I] {
      type B <: IsIdxElem[_]
      type Out = LocTC2d.MkOut[B]
      def apply(i: I): Out
    }
    object LocTC2d {
      type MkOut[B <: IsIdxElem[_]] = Option[Is1dIndexArr[B, DataT]]

      type Aux[I, B0 <: IsIdxElem[_]] = LocTC2d[I] { type B = B0 }
      def instance[I, B0 <: IsIdxElem[_]](f: I => MkOut[B0]): Aux[I, B0] = new LocTC2d[I] {
        override type B = B0
        override def apply(i: I): Out = f(i)
      }
    
      implicit val I0Type: Aux[I0, I1] = instance(i => getDim0Slice(i))
      implicit val I1Type: Aux[I1, I0] = instance(i => getDim1Slice(i))
    }
  }

  trait Is3dIndexArr[
    I0 <: IsIdxElem[_], I1 <: IsIdxElem[_], I2 <: IsIdxElem[_], DataT <: DataType
  ] extends IsBaseArr[DataT] {
    type Self = Is3dIndexArr[I0, I1, I2, DataT]
    val indices: (IsIndex[I0], IsIndex[I1], IsIndex[I2])
    //def addDelta(delta: SelfMinus1d[I0, I1]): Self
    //def addDelta(delta: SelfMinus1d[I0, I2]): Self
    //def addDelta(delta: SelfMinus1d[I1, I2]): Self
    def getDim0Slice(loc: I0): Option[Is2dIndexArr[I1, I2, DataT]]
    def getDim1Slice(loc: I1): Option[Is2dIndexArr[I0, I2, DataT]]
    def getDim2Slice(loc: I2): Option[Is2dIndexArr[I0, I1, DataT]]
    def loc[I](i: I)(implicit tc: LocTC3d[I]): tc.Out = tc(i)

    trait LocTC3d[I] {
      type B <: IsIdxElem[_]
      type C <: IsIdxElem[_]
      type Out = LocTC3d.MkOut[B, C]
      def apply(i: I): Out
    }
    object LocTC3d {
      type MkOut[B <: IsIdxElem[_], C <: IsIdxElem[_]] = Option[Is2dIndexArr[B, C, DataT]]

      type Aux[I, B0 <: IsIdxElem[_], C0 <: IsIdxElem[_]] = LocTC3d[I] { type B = B0; type C = C0 }
      def instance[I, B0 <: IsIdxElem[_], C0 <: IsIdxElem[_]](f: I => MkOut[B0, C0]): Aux[I, B0, C0] = new LocTC3d[I] {
        override type B = B0
        override type C = C0
        override def apply(i: I): Out = f(i)
      }
    
      implicit val I0Type: Aux[I0, I1, I2] = instance(i => getDim0Slice(i))
      implicit val I1Type: Aux[I1, I0, I2] = instance(i => getDim1Slice(i))
      implicit val I2Type: Aux[I2, I0, I1] = instance(i => getDim2Slice(i))
    }
  }
}
