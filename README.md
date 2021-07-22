# PARIS

Probabilistic Alignment of Relations, Instances, and Schema

A project of the Webdam team at INRIA Saclay
http://webdam.inria.fr/paris/
Version 0.2

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

The following files may not be covered by this license:
NotifyingBlockingThreadPoolExecutor.java

== Publications ==

Fabian M. Suchanek, Serge Abiteboul, Pierre Senellart
"PARIS: Probabilistic Alignment of Relations, Instances, and Relations"
38th International Conference on Very Large Databases (VLDB 2012)
PVLDB Journal, Volume 5, Number 3, November 2011. 

== Results ==

See http://webdam.inria.fr/paris/.

== Dependencies ==

- A Java runtime environment (> 1.6)
- Jena to load the ontologies <http://jena.sourceforge.net/ontology>
- The MPI Java Tools <http://mpii.de/yago-naga/javatools>
- A machine with a lot of RAM (about 48GB are needed for the DBpedia-YAGO
  alignment task with the DBpedia version from March 2012, more RAM may be
  needed for more recent versions).

== Example usage ==

We will demonstrate the use of PARIS to align DBpedia (version from March 2012)
and YAGO (version from January 2012).

1. Retrieve the two ontologies:

   $ wget 'http://www.mpi-inf.mpg.de/yago-naga/yago/download/yago2/yago2core_20120109.7z'
   $ wget 'http://webdam.inria.fr/paris/dbpedia_all_20120320.nt.7z'
   $ 7z x yago2core_20120109.7z
   $ 7z x dbpedia_all_20120320.7z

Alternatively, to prepare a current version of dbpedia_all.nt, do as follows:

   $ wget 'http://downloads.dbpedia.org/3.8/en/instance_types_en.nt.bz2'
   $ wget 'http://downloads.dbpedia.org/3.8/en/mappingbased_properties_en.nt.bz2'
   $ bunzip instance_types_en.nt.bz2
   $ bunzip mappingbased_properties_en.nt.bz2
   $ cat instance_types_en.nt mappingbased_properties_en.nt | grep -v '^#' |
       sort -S1000M | uniq > dbpedia_all.nt

2. Load and serialize the two ontologies:

   $ java -Xmx26000m -jar paris.jar yago2core_20120109/ yagoNative yago.dat \
       "http://www.mpii.de/yago/resource/" "y:" false
   $ java -Xmx20000m -jar paris.jar dbpedia_all.nt rdf dbpedia.dat \
       "http://dbpedia.org/" "dbp:" false

3. Prepare the output folders and the configuration file:

   $ mkdir {log,tsv}
   $ cat > conf << EOF
resultTSV = tsv
factstore1 = yago.dat
factstore2 = dbpedia.dat
home = log
endIteration = 10
nThreads = NTHREADS
EOF
   $ sed -i "s/NTHREADS/`cat /proc/cpuinfo | grep '^processor' | wc -l`/" conf

4. Run the alignment:

   $ java -Xmx47000m -jar paris.jar dbpedia-yago-serialized/conf

5. Prepare the gold standard alignment. If you used the suggested version of
YAGO and DBpedia, then just run:

   $ echo 1484735 > num

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

6. Evaluate the alignments (where evaluation.jar executes the main
   method of the Evaluation class in evaluation/Evaluation.java):

   $ java -jar evaluation.jar tsv/ `cat num`

   The last few lines of the output give the alignment scores.

== General usage on your own ontologies ==

1. You should load and serialize your ontologies. The general syntax is:

   $ java -Xmx<memory> -jar paris.jar <sourceFile> <parseType> <target> \
       <namespace> <prefix> <shinglings>

   - <memory> should be an estimate of the memory required to load the ontology
     (it's best to indicate almost all of your available RAM)
   - <sourceFile> is the file containing the ontology (or the folder, when
     loading YAGO)
   - <parseType> is the ontology type. PARIS accepts ontologies in OWL, RDF/XML
     and N-TRIPLES format (not N3 or TURTLE) with the "rdf" parseType (the more
     common situation), as well as YAGO's native TSV format with the
     "yagoNative" parseType (illustrated in the previous section. Note that when
     loading RDF/XML or OWL, throwaway identifiers might be created for
     anonymous intermediate nodes. In case of doubt, the safest way is to
     convert your ontology to N-TRIPLES using e.g.
     <http://www.l3s.de/~minack/rdf2rdf/>, ensure that the resulting file has an
     extension of ".nt", and load this into PARIS.
   - <target> is where to store the serialized ontology (e.g. "ontology.dat")
   - <namespace> is the namespace of the ontology
   - <prefix> is how to abbreviate this namespace. If you are not sure, just
     specify whatever you want as namespace and prefix.
   - <shinglings> is either "true" or "false" depending on whether you want to
     use the approximate string index or not.

2. Once you have loaded and serialized both ontologies, you should create a
   configuration file for PARIS as illustrated in the previous section.

3. You should then run PARIS on the configuration file, which will produce the
   alignment between both ontologies.

== Contact ==

Technical inquiries should be addressed to Fabian Suchanek
(firstName@lastName.name) and Antoine Amarilli (a3nmNOSPAM@a3nm.net).

