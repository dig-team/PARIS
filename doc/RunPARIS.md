Running PARIS
===========

Prerequisites
------------

To run PARIS, you need 
* A Java runtime environment (> 1.6)
* A machine with a lot of RAM (around 24 GB of RAM are needed for the
  DBpedia-YAGO alignment task with the DBpedia version from March 2012, it may
  work with less).

PARIS targets RDF knowledge bases, i.e. knowledge bases that have many instances, many
facts about these instances, facts that involve literals, and a class hierarchy on top. PARIS supports
knowledge bases in N-TRIPLES format (not N3 or TURTLE). To convert your knowledge base to
N-TRIPLES, you can use e.g. [this tool](http://www.l3s.de/~minack/rdf2rdf/). Ensure that
the resulting file has an extension of ".nt". A knowledge base can also consist of
multiple such files.

Running PARIS
-------------

There are three ways to invoke PARIS:
* Fast track:
    `java -jar paris.jar <kb1> <kb2> <outputfolder>`
  where `<kb1>` and `<jb2>` are the knowledge bases. Each can be either
    an individual N-triples file or a folder that contains such files.
  `<outputfolder>` is a folder where PARIS can work.
* Detailed track:
    `java -jar paris.jar <settingsfile>`
  where `<settingsfile>` is an existing, but empty file. In this mode, PARIS
  will ask the user for the settings and store them in `<settingsfile>`. Many
  settings can be added in `<settingsfile>` to change the behavior of PARIS, cf.
  `Settings.java`.
* Detailed reuse track:
   `java -jar paris.jar <settingsfile>`
  where `<settingsfile>` is a previously generated setting file.
  
Output
---------

PARIS dumps its output after each iteration into the output folder.
It will contain, for each iteration n
* `n_eqv.tsv`
   equalities found after this iteration
* `n_superrelations1.tsv`
   Relations of knowledge base 1 that are superrelations of relations of knowledge base 2
* `n_superrelations2.tsv`
   Relations of knowledge base 2 that are superrelations of relations of knowledge base 1. 
* `n_superclasses1.tsv`
   Superclasses of knowledge base 1 (last iteration only)  
* `n_superclasses2.tsv`
   Superclasses of knowledge base 2 (last iteration only)

Each file will contain the first item (instance, class, or relation), the aligned second item, and a probabilistic score that indicates the confidence of this alignment..

Example: Aligning YAGO and DBpedia
-------------------

For archival purposes only, we show here how we used PARIS to align DBpedia (version from March 2012) and YAGO (version from January 2012), and how we compared this alignment to the gold standard. 

*In the meantime, the data sources have changed in format and in content, and the original data sources are no longer available. We are currently unable to reproduce the results of the original experiments!*

1. Retrieve the two knowledge bases:

```
wget 'http://webdam.inria.fr/paris/yago2core_20120109_fixed.7z'
wget 'http://webdam.inria.fr/paris/dbpedia_all_20120320.nt.7z'
7z x yago2core_20120109_fixed.7z
7z x dbpedia_all_20120320.nt.7z
```

Alternatively, you can prepare the sources yourself, as follows.

To prepare a current version of dbpedia_all.nt:

```
wget 'http://downloads.dbpedia.org/3.8/en/instance_types_en.nt.bz2'
wget 'http://downloads.dbpedia.org/3.8/en/mappingbased_properties_en.nt.bz2'
bunzip2 instance_types_en.nt.bz2
bunzip2 mappingbased_properties_en.nt.bz2
cat instance_types_en.nt mappingbased_properties_en.nt | grep -v '^#' | sort -S1000M | uniq > dbpedia_all.nt
```

To prepare YAGO:

```
wget 'http://www.mpi-inf.mpg.de/yago-naga/yago1_yago2/download/yago2/yago2core_20120109.7z'
util/remove_yago.sh yago2core_20120109
util/fix_yago.sh yago2core_20120109
util/uniq_yago.sh yago2core_20120109
```

2. Run the alignment, adjusting 47000m relative to the available quantity of RAM
on your machine:

```
java -Xmx47000m -jar paris.jar yago2core_20120109 dbpedia_all.nt tsv
```

3. Prepare the gold standard alignment. If you used the suggested version of
YAGO and DBpedia, then just run:

```
echo 1484671 > num
```

Otherwise:

```
java -Xmx20000m -jar paris.jar dbpedia.dat dbpedia_entities.dat
java -Xmx26000m -jar paris.jar yago.dat yago_entities.dat
grep '^dbp:resource/' dbpedia_entities.dat | cut -f2- -d/ | ./util/decode_percent.py | sort -S1000M | uniq > dbpedia_entities.txt
native2ascii -encoding UTF-8 -reverse yago_entities.dat | sort -S1000M | uniq > yago_entities.txt
comm -1 -2 dbpedia_entities.txt yago_entities.txt > gold
wc -l gold | awk '{print $1}' > num
```

4. Prepare the occurrences file. (Those files count the number of occurrences of
   each relation to weigh the scoring of the relation alignment adequately.)

```
util/relation_occurrences_dbpedia.sh < dbpedia_all.nt > dbpedia.occurrences
util/relation_occurrences_yago.sh yago2core_20120109 > yago.occurrences
```

5. Evaluate the alignments (where evaluation.jar executes the main
   method of the Evaluation class in evaluation/Evaluation.java):

```
java -jar tsv/ yago-dbpedia `cat num` yago.occurrences dbpedia.occurrences
```

The last few lines of the output give the alignment scores.
