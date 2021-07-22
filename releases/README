PARIS - Probabilistic Alignment of Relations, Instances, and Schema
A project of the Webdam team at INRIA Saclay
http://webdam.inria.fr/paris/
Version 0.3

== Introduction ==

PARIS is a system for the automatic alignment of RDF ontologies. PARIS aligns
not only instances, but also relations and classes. Alignments at the instance
level cross-fertilize with alignments at the schema level. Thereby, our system
provides a truly holistic solution to the problem of ontology alignment. The
heart of the approach is probabilistic, i.e., we measure degrees of matchings
based on probability estimates. This allows PARIS to run without parameter
tuning. Experiments show that PARIS obtains a precision of around 90% in
experiments with some of the world’s largest ontologies.

== Contributors ==

PARIS is a project of the Webdam team at INRIA Saclay. The main contributors
are:

* Fabian M. Suchanek <http://suchanek.name>
* Pierre Senellart <http://pierre.senellart.com>
* Mayur Garg
* Antoine Amarilli <http://a3nm.net>
* Serge Abiteboul <http://www-rocq.inria.fr/~abitebou/>

== License ==

PARIS is available under the Creative Commons BY-NC license
<https://creativecommons.org/licenses/by-nc/3.0/>.

This license does not cover the third-party libraries used (see "Dependencies"
below).

== Publications ==

Fabian M. Suchanek, Serge Abiteboul, Pierre Senellart
"PARIS: Probabilistic Alignment of Relations, Instances, and Relations"
38th International Conference on Very Large Databases (VLDB 2012)
PVLDB Journal, Volume 5, Number 3, November 2011. 

Marilena Oita, Antoine Amarilli, Pierre Senellart
"Cross-Fertilizing Deep Web Analysis and Ontology Enrichment"
Second International Workshop on Searching and Integrating New Web Data Sources (VLDS 2012) 

== Results ==

See http://webdam.inria.fr/paris/.

== Dependencies ==

- A Java runtime environment (> 1.6)
- A machine with a lot of RAM (around 24 GB of RAM are needed for the
  DBpedia-YAGO alignment task with the DBpedia version from March 2012, it may
  work with less).

The following dependencies are shipped as part of the source archive and as part
of the JAR file for convenience:

- The MPI Java Tools <http://mpii.de/yago-naga/javatools>
- Primitive Collections for Java <http://pcj.sourceforge.net/>

== Usage ==

PARIS targets RDF ontologies, i.e. ontologies that have many instances, many
assertions about these instances, and a class hierarchy on top. PARIS supports
ontologies in N-TRIPLES format (not N3 or TURTLE). To convert your ontology to
N-TRIPLES, you can use e.g. <http://www.l3s.de/~minack/rdf2rdf/>. Ensure that
the resulting file has an extension of ".nt". An ontology can also consist of
multiple such files.

There are three ways to invoke PARIS:
* Fast track:
    java -jar paris_0_3.jar <ontology1> <ontology2> <outputfolder>
  where
  - <ontology1> and <ontology2> are the ontologies. Each can be either
    am individual N-triples file or a folder that contains such files.
  - <outputfolder> is a folder where PARIS can work.
* Detailed track:
    java -jar paris_0_3.jar <settingsfile>
  where <settingsfile> is an existing, but empty file. In this mode, PARIS
  will ask the user for the settings and store them in <settingsfile>. Many
  settings can be added in <settingsfile> to change the behavior of PARIS, cf.
  Settings.java.
* Detailed reuse track:
   java -jar paris_0_3.jar <settingsfile>
  where <settingsfile> is a previously generated setting file.
  
== Output ==

PARIS dumps its output after each iteration into the output folder.
It will contain, for each iteration n
* n_eqv.tsv
   equalities found after this iteration
* n_superrelations1.tsv
   Relations of ontology 1 that are superrelations of relations of ontology 2
* n_superrelations2.tsv
   Relations of ontology 2 that are superrelations of relations of ontology 1
* n_superclasses1.tsv
   Superclasses of ontology 1 (last iteration only)  
* n_superclasses2.tsv
   Superclasses of ontology 2 (last iteration only)

=== Aligning YAGO and DBpedia ===

For archival purposes only, we will demonstrate the use of PARIS to align
DBpedia (version from March 2012) and YAGO (version from January 2012). In the
meantime, YAGO has been updated to use N3 format, so to use this newer version
of YAGO, proceed rather as above.

1. Retrieve the two ontologies:

   $ wget 'http://webdam.inria.fr/paris/yago2core_20120109_fixed.7z'
   $ wget 'http://webdam.inria.fr/paris/dbpedia_all_20120320.nt.7z'
   $ 7z x yago2core_20120109_fixed.7z
   $ 7z x dbpedia_all_20120320.nt.7z

Alternatively, you can prepare the sources yourself, as follows.

To prepare a current version of dbpedia_all.nt:

   $ wget 'http://downloads.dbpedia.org/3.8/en/instance_types_en.nt.bz2'
   $ wget 'http://downloads.dbpedia.org/3.8/en/mappingbased_properties_en.nt.bz2'
   $ bunzip2 instance_types_en.nt.bz2
   $ bunzip2 mappingbased_properties_en.nt.bz2
   $ cat instance_types_en.nt mappingbased_properties_en.nt | grep -v '^#' |
       sort -S1000M | uniq > dbpedia_all.nt

To prepare YAGO:

   $ wget 'http://www.mpi-inf.mpg.de/yago-naga/yago1_yago2/download/yago2/yago2core_20120109.7z'
   $ util/remove_yago.sh yago2core_20120109
   $ util/fix_yago.sh yago2core_20120109
   $ util/uniq_yago.sh yago2core_20120109

2. Run the alignment, adjusting 47000m relative to the available quantity of RAM
on your machine:

   $ java -Xmx47000m -jar paris.jar yago2core_20120109 dbpedia_all.nt tsv

3. Prepare the gold standard alignment. If you used the suggested version of
YAGO and DBpedia, then just run:

   $ echo 1484671 > num

Otherwise:

   $ java -Xmx20000m -jar paris.jar dbpedia.dat dbpedia_entities.dat
   $ java -Xmx26000m -jar paris.jar yago.dat yago_entities.dat
   $ grep '^dbp:resource/' dbpedia_entities.dat | cut -f2- -d/ |
       ./util/decode_percent.py |
       sort -S1000M | uniq > dbpedia_entities.txt
   $ native2ascii -encoding UTF-8 -reverse yago_entities.dat | sort -S1000M |
       uniq > yago_entities.txt
   $ comm -1 -2 dbpedia_entities.txt yago_entities.txt > gold
   $ wc -l gold | awk '{print $1}' > num

4. Prepare the occurrences file. (Those files count the number of occurrences of
   each relation to weigh the scoring of the relation alignment adequately.)

   $ util/relation_occurrences_dbpedia.sh < dbpedia_all.nt > dbpedia.occurrences
   $ util/relation_occurrences_yago.sh yago2core_20120109 > yago.occurrences

5. Evaluate the alignments (where evaluation.jar executes the main
   method of the Evaluation class in evaluation/Evaluation.java):

   $ java -jar tsv/ yago-dbpedia `cat num` yago.occurrences dbpedia.occurrences

The last few lines of the output give the alignment scores.

== Contact ==

Technical inquiries should be addressed to Fabian Suchanek
(firstName@lastName.name) and Antoine Amarilli (a3nmNOSPAM@a3nm.net).
