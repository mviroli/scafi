package it.unibo.scafi.core

/**
 * @author Mirko Viroli
 * @author Roberto Casadei
 *
 * This trait defines a component that extends Semantics.
 * It defines an implementation of Context and Export (and Path),
 * with associated factories.
 */

import scala.collection.{ Map => GMap }
import scala.collection.mutable.{ Map => MMap}

trait Engine extends Semantics {

  override type EXPORT = ExportImpl
  override type CONTEXT = ContextImpl
  override type FACTORY = EngineFactory

  override implicit val factory = new EngineFactory with Serializable

  class ExportImpl() extends Export with ExportOps with Serializable { self: EXPORT =>
    private val map = MMap[Path,Any]()
    def put[A](path: Path, value: A) : A = { map += (path -> value); value }
    def get[A](path: Path): Option[A] = map get(path) map (_.asInstanceOf[A])
    def root[A](): A = get[A](factory.emptyPath()).get

    override def toString: String = map.toString
  }

  class PathImpl(val path: List[Slot]) extends Path with Equals with Serializable {
    def push(s: Slot): Path = new PathImpl(s :: path)
    def pull(): Path = path match {
      case s :: p => new PathImpl(p)
      case _ => throw new Exception()
    }

    override def isRoot: Boolean = path.isEmpty

    override def toString(): String = "P:/"+path.reverse.mkString("/")

    def matches(p: Path): Boolean = this == p

    def canEqual(other: Any) = {
      other.isInstanceOf[Engine.this.PathImpl]
    }

    override def equals(other: Any) = {
      other match {
        case that: Engine.this.PathImpl => that.canEqual(PathImpl.this) && path == that.path
        case _ => false
      }
    }

    override def hashCode() = path.hashCode
  }

  abstract class BaseContextImpl(val selfId: ID,
                                 _exports: Iterable[(ID, EXPORT)])
    extends Context with ContextOps with Serializable { self: CONTEXT =>

    private val exportsMap : MMap[ID,EXPORT] = MMap() ++ _exports

    def updateExport(id: ID, export:EXPORT) = exportsMap.put(id, export)

    override def exports(): Iterable[(ID, ExportImpl)] = exportsMap

    def readSlot[A](i: ID, p:Path): Option[A] = {
      exportsMap get(i) flatMap (_.get[A](p))
    }
  }

  class ContextImpl(
      selfId: ID,
      exports: Iterable[(ID,EXPORT)],
      val localSensor: GMap[LSNS,Any],
      val nbrSensor: GMap[NSNS,GMap[ID,Any]])
    extends BaseContextImpl(selfId, exports) { self: CONTEXT =>

    override def toString() = "C[I:"+selfId+",E:"+exports+",S1:"+localSensor+",S2:"+nbrSensor+"]"

    override def sense[T](lsns: LSNS): Option[T] = localSensor.get(lsns).map(_.asInstanceOf[T])

    override def nbrSense[T](nsns: NSNS)(nbr: ID): Option[T] = nbrSensor.get(nsns).flatMap(_.get(nbr)).map(_.asInstanceOf[T])
  }

  class EngineFactory extends Factory { self: FACTORY =>
    def /(): Path = emptyPath()
    def /(s: Slot): Path = path(s)
    def emptyPath(): Path = new PathImpl(List())
    def emptyExport(): EXPORT = new ExportImpl
    def path(slots: Slot*): Path = new PathImpl(List(slots:_*).reverse)
    def export(exps: (Path,Any)*): EXPORT = {
      val exp = new ExportImpl()
      exps.foreach { case (p,v) => exp.put(p,v) }
      exp
    }
    def context(selfId: ID, exports: Map[ID,EXPORT], lsens: Map[LSNS,Any] = Map(), nbsens: Map[NSNS,Map[ID,Any]] = Map()): CONTEXT =
      new ContextImpl(selfId, exports, lsens, nbsens)
  }

  trait Interop[T] extends Serializable {
    def toString: String
    def fromString(s: String): T
  }

  trait LinearizableTo[T,N] extends Serializable {
    def toNum(v: T): N
    def fromNum(n: N): T
  }

  trait Linearizable[T] extends LinearizableTo[T,Int]

  implicit val linearID: Linearizable[ID]
  implicit val interopID: Interop[ID]
  implicit val interopLSNS: Interop[LSNS]
  implicit val interopNSNS: Interop[NSNS]
}
