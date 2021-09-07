package org.w3.banana.jena

import org.apache.jena.datatypes.{BaseDatatype, RDFDatatype, TypeMapper}

import org.w3.banana.RDF

import scala.reflect.TypeTest
import scala.util.Try

object JenaRdf extends RDF {
	import org.apache.jena.graph as jena
	import org.apache.jena.graph.{NodeFactory, Factory}

	override opaque type Graph = jena.Graph
	override opaque type Triple = jena.Triple
	override opaque type Node = jena.Node
	override opaque type URI <: Node = jena.Node_URI
	override opaque type BNode <: Node = jena.Node_Blank
	override opaque type Literal <: Node = jena.Node_Literal
	override opaque type Lang = String

	override val Triple: TripleOps = new TripleOps  {
		def apply(subj: Node, rel: URI, obj: Node): Triple =
			jena.Triple.create(subj, rel, obj).nn
		override inline
		def untuple(t: Triple): TripleI =
			(subjectOf(t), relationOf(t), objectOf(t))
		override inline
		def subjectOf(triple: Triple): Node = triple.getSubject().nn
		override inline
		def relationOf(triple: Triple): URI = triple.getPredicate.asInstanceOf[URI].nn
		override inline
		def objectOf(triple: Triple): Node  = triple.getObject().nn
	}

	//		given tripleTT: TypeTest[Any,Triple] with {
	//			import compiletime.asMatchable
	//			override def unapply(s: Any): Option[s.type & Triple] = s.asMatchable match
	//				case x: (s.type & jena.Triple) => Some(x)
	//				case _ => None
	//		}

	given uriTT: TypeTest[Any,URI] with {
		import compiletime.asMatchable
		override def unapply(s: Any): Option[s.type & jena.Node_URI] = s.asMatchable match
			//note: this does not compile if we use URI instead of jena.Node_URI
			case x: (s.type & jena.Node_URI) => Some(x)
			case _ => None
	}

	override val URI : URIOps = new URIOps  {
		//todo: this will throw an exception, should return Option
		override inline
		def mkUri(iriStr: String): Try[URI] = Try(NodeFactory.createURI(iriStr).asInstanceOf[URI])
		override inline
		def asString(uri: URI): String = uri.getURI().nn
	}

	override val Literal: LiteralOps = new LiteralOps {
		// TODO the javadoc doesn't say if this is thread safe
		lazy val mapper: TypeMapper = TypeMapper.getInstance.nn
		private val xsdString: RDFDatatype = mapper.getTypeByName("http://www.w3.org/2001/XMLSchema#string").nn
		//			private val __xsdStringURI: URI = URI("http://www.w3.org/2001/XMLSchema#string")
		private val xsdLangString: RDFDatatype = mapper.getTypeByName("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString").nn
		//todo: are we missing a Datatype Type? (check other frameworks)
		def jenaDatatype(datatype: URI): RDFDatatype =
			val iriString: String = URI.asString(datatype)
			val typ: RDFDatatype | Null = mapper.getTypeByName(iriString)
			if typ == null then
				val datatype = new BaseDatatype(iriString)
				mapper.registerDatatype(datatype)
				datatype
			else typ

		def apply(plain: String): Literal =
			NodeFactory.createLiteral(plain).nn.asInstanceOf[Literal]

		def dtLiteral(lex: String, dataTp: URI): Literal =
			NodeFactory.createLiteral(lex, jenaDatatype(dataTp)).nn.asInstanceOf[Literal]

		def langLiteral(lex: String, lang: Lang): Literal =
			NodeFactory.createLiteral(lex, lang).nn.asInstanceOf[Literal]

		def unapply(lit: Literal): Option[LiteralI] =
			import LiteralI.*
			val lex: String = lit.getLiteralLexicalForm.nn
			val dt: RDFDatatype | Null = lit.getLiteralDatatype
			val lang: String | Null = lit.getLiteralLanguage
			if (lang == null || lang.isEmpty) then
				if dt == null || dt == xsdString then Some(Plain(lex))
				else Some(^^(lex, URI(dt.getURI.nn)))
			else if dt == null || dt == xsdLangString then
				Some(`@`(lex, lang))
			else None
	}

	given literalTT: TypeTest[Any,Literal] with {
		import compiletime.asMatchable
		override def unapply(s: Any): Option[s.type & Literal] = s.asMatchable match
			//note: this does not compile if we use URI instead of jena.Node_URI
			case x: (s.type & jena.Node_Literal) => Some(x)
			case _ => None
	}

	override val Lang: LangOps =  new LangOps {
		override inline def apply(lang: String): Lang = lang
		override inline def label(lang: Lang): String = lang
	}

	override val Graph: GraphOps = new GraphOps {
		override inline def empty: Graph = Factory.empty().nn
		override inline def apply(triples: Triple*): Graph =
			val graph: Graph = Factory.createDefaultGraph.nn
			triples.foreach { triple =>
				graph.add(triple)
			}
			graph

		import scala.jdk.CollectionConverters.{given,*}
		def triplesIn(graph: Graph): Iterable[Triple] =
			import org.apache.jena.graph.Node.ANY
			graph.find(ANY, ANY, ANY).nn.asScala.to(Iterable)
	}
}