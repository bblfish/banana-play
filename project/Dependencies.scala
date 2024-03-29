import sbt._

object Dependencies {

	/**
	 * Jena
	 *
	 * @see https://jena.apache.org/
	 * @see https://repo1.maven.org/maven2/org/apache/jena
	 */
	val jenaLibs = "org.apache.jena" % "apache-jena-libs" % "4.1.0"

	val munit = "org.scalameta" %% "munit" % "0.7.28"

	/**
	 * RDF4J
	 *
	 * @see https://www.rdf4j.org/
	 * @see https://repo1.maven.org/maven2/org/eclipse/rdf4j/
	 */
	object RDF4J {
		val Version = "3.7.2"
		lazy val QueryAlgebra = "org.eclipse.rdf4j" % "rdf4j-queryalgebra-evaluation" % Version
		lazy val QueryParser = "org.eclipse.rdf4j" % "rdf4j-queryparser-sparql" % Version
		lazy val QueryResult = "org.eclipse.rdf4j" % "rdf4j-queryresultio-sparqljson" % Version
		lazy val RioTurtle = "org.eclipse.rdf4j" % "rdf4j-rio-turtle" % Version
		lazy val RioRdfxml = "org.eclipse.rdf4j" % "rdf4j-rio-rdfxml" % Version
		lazy val RioJsonLd = "org.eclipse.rdf4j" % "rdf4j-rio-jsonld" % Version
		lazy val SailMemory = "org.eclipse.rdf4j" % "rdf4j-sail-memory" % Version
		lazy val SailNativeRdf = "org.eclipse.rdf4j" % "rdf4j-sail-nativerdf" % Version
		lazy val RepositorySail = "org.eclipse.rdf4j" % "rdf4j-repository-sail" % Version
	}

	/**
	 * jsonld-java
	 *
	 * @see https://github.com/jsonld-java/jsonld-java
	 * @see https://repo.typesafe.com/typesafe/snapshots/com/github/jsonld-java/jsonld-java-tools
	 */
	val jsonldJava = "com.github.jsonld-java" % "jsonld-java" % "0.13.3"

	/**
	 * slf4j-nop. Test dependency for logging.
	 * @see https://www.slf4j.org
	 */
	val slf4jNop = "org.slf4j" % "slf4j-nop" % "1.7.32" % Test
	
}