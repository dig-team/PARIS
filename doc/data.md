Data for PARIS
==============

We reproduce here some of the datasets on which PARIS was tested.

OAEI
----

The [Ontology Alignment Evaluation Initiative](https://oaei.ontologymatching.org/) provides several knowledge bases for alignment. We used:
* OAEI Persons
  - The ontologies are available at the homepage of the OAEI 2010 team. We reproduce them in data/persons.zip.
  - The gold standard for instances, classes, and relations can trivially be determined from the URIs. 
* OAEI Restaurants
  - The ontologies are available at the homepage of the OAEI 2010 team. We reproduce them in data/restaurants.zip. Note that this version has been modified by us to fix errors in the structure of the dataset.
  - The gold standard for instances, classes, and relations can trivially be determined from the URIs. 

YAGO & DBpedia
-----

[YAGO](https://yago-knowledge.org) and [DBpedia](https://www.dbpedia.org/) are two large general-purpose knowledge bases.
        The YAGO ontology is available on the homepage of YAGO (core version in native text format).
        The DBpedia ontology is available on the homepage of DBpedia ("DBpedia Ontology" + "Ontology Infobox Types" + "Ontology Infobox Properties").
        The precision of the instance matching is simply determined by comparing the URIs of the entities. To compute recall, count the number of instances that the two ontologies have in common. In our versions, the two ontologies had 1429686 instances in common.
        The gold standard for relations is here. It contains a tab-separated list of subproperty-superproperty pairs. Note that this gold standard is incomplete and can serve only for precision!
        The gold standard for classes is here. It contains a tab-separated list of subclass-superclass pairs, together with the notion TRUE or FALSE. Note that this gold standard is incomplete and can serve only for precision! 
    YAGO & IMDb
        The YAGO ontology is available on the homepage of YAGO (core version in native text format).
        The IMDb ontology is derived from IMDb's plain text interface and available upon request.
        The gold standard for relations is available here. It contains a tab-separated list of subproperty-superproperty pairs.
        The gold standard for instances is available here. It is a tab-separated list of YAGO names and IMDB person/movie identifiers.
        The gold standard for classes is available here. It contains a tab-separated list of subclass-superclass pairs, together with the notion TRUE or FALSE. Note that this gold standard is incomplete and can serve only for precision! 

Mappings YAGO/DBpedia
We provide here the mappings between the concepts, instances and relations of YAGO and DBpedia, as computed by PARIS.

    Matchings of instances/individuals between YAGO and DBpedia: download as TSV (with precision)
    Note that these mappings are computed automatically and may not be 100% accurate. If you want 100% accuracy, go to the Web site of YAGO. The mappings on the YAGO Web-site are 100% accurate, since they have been computed by trivially aligning the URIs of the instances.
    Mappings of the classes/concepts between YAGO and DBpedia: download as TSV (with precision), download as RDF/TTL (cut at 60% precision)
    Note that DBpedia uses multiple types of classes. The mappings computed by PARIS concern the classes of the manually constructed DBpedia ontology with YAGO classes. These mappings are asymmetric rdfs:subclassOf-mappings in both directions. Please be aware that these mappings are not of very good quality.
    DBpedia also contains a class hierarchy that was derived from the YAGO class hierarchy. If you are looking for mappings for these classes to YAGO, see again the Web site of YAGO. These mappings are of trivially perfect quality.
    Mappings of the relations/properties between YAGO and DBpedia: download as TSV (with precision), download as RDF/TTL (cut at 40% precision).
    These are asymmetric rdfs:subPropertyOf mappings between the YAGO relations and the relations of the manual ontology of DBpedia. 
