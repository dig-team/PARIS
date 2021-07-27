Data for PARIS
==============

We reproduce here some of the datasets on which PARIS was tested.

OAEI
----

The [Ontology Alignment Evaluation Initiative](https://oaei.ontologymatching.org/) provides several knowledge bases for alignment. We used:
* OAEI Persons
  - The ontologies are available at the homepage of the OAEI 2010 team. We reproduce them [here](../data/oaei-persons.zip).
  - The gold standard for instances, classes, and relations can trivially be determined from the URIs. 
* OAEI Restaurants
  - The ontologies are available at the homepage of the OAEI 2010 team. We reproduce them [here](../data/oaei-restaurants.zip). Note that this version has been modified by us to fix errors in the structure of the dataset.
  - The gold standard for instances, classes, and relations can trivially be determined from the URIs. 

YAGO & DBpedia
-----

[YAGO](https://yago-knowledge.org) and [DBpedia](https://www.dbpedia.org/) are two large general-purpose knowledge bases.

*Since the sources have changed in both format and content, and since the original data sources are no longer available, we are currently unable to reproduce the original experimental results!*

For YAGO, we used the core version in native text format. For DBpedia, we combined "DBpedia Ontology" + "Ontology Infobox Types" + "Ontology Infobox Properties". The gold standard for the instances can be established by simply comparing the URIs.

The gold standard for relations is [here](../data/yago-dbpedia-relations-gold.txt). It contains a tab-separated list of subproperty-superproperty pairs. Note that this gold standard is incomplete and can serve only for precision!

The gold standard for classes is [here](../data/yago-dbpedia-classes-gold.txt). It contains a tab-separated list of subclass-superclass pairs, together with the notion TRUE or FALSE. Note that this gold standard is incomplete and can serve only for computing precision, not recall! 

YAGO & IMDb
-------

We also matched [YAGO](https://yago-knowledge.org) and [IMDb](https://www.imdb.com/), a large movie database.

*Again, these sources have changed and we are currently unable to reproduce the original experimental results!*

We still make available 
- The gold standard [for relations](../data/yago-imdb-relations-gold.txt). The file contains a tab-separated list of subproperty-superproperty pairs.
- The gold standard [for instances](../data/yago-imdb-instances-gold.zip). The file is a tab-separated list of YAGO names and IMDB person/movie identifiers.
- The gold standard [for classes](../data/yago-imdb-classes-gold.txt). The file contains a tab-separated list of subclass-superclass pairs, together with the notion TRUE or FALSE. Note that this gold standard is incomplete and can serve only to compute precision, not recall! 

Results: Mappings YAGO & DBpedia
-----------

We provide here the mappings between the concepts, instances and relations of YAGO and DBpedia, as computed by PARIS in 2012. These mappings are not 100%, as they are the output of an automated process.

- Matchings of instances/individuals between YAGO and DBpedia as [TSV with precision values](../data/yago-dbpedia-instances-result.zip).
- Mappings of the classes/concepts between YAGO and DBpedia as [TSV with precision, as well as in RDF/TTL cut at 60% precision](../data/yago-dbpedia-classes-result.zip). DBpedia uses multiple types of classes. The mappings computed by PARIS concern the classes of the manually constructed DBpedia ontology with YAGO classes. These mappings are asymmetric rdfs:subclassOf-mappings in both directions. These mappings are not of very good quality.
- Mappings of the relations/properties between YAGO and DBpedia as [TSV with precision, as well as in RDF/TTL cut at 40% precision](../data/yago-dbpedia-relations-result.zip). These are asymmetric rdfs:subPropertyOf mappings between the YAGO relations and the relations of the manual ontology of DBpedia. 