PARIS - Probabilistic Alignment of Relations, Instances, and Schema
===================================================================

Introduction
------------

PARIS is a system for the automatic alignment of RDF knowledge bases. PARIS aligns
not only instances, but also relations and classes. Alignments at the instance
level cross-fertilize with alignments at the schema level. Thereby, our system
provides a truly holistic solution to the problem of knowledge base alignment. The
heart of the approach is probabilistic, i.e., we measure degrees of matchings
based on probability estimates. This allows PARIS to run without parameter
tuning. Experiments have shown that PARIS obtains a precision of around 90% in
experiments with some of the world’s largest knowledge bases.

To do that, PARIS needs two knowledge bases (KBs), which each contain:
1. a large number of instances. PARIS will not work with ontologies that contain mainly the schema, i.e., the classes and the relations.
2. a limited number of relations. PARIS works best with few relations, each of which has many facts. It will work less well with open knowledge bases, which have relations that are not predefined.
3. a large number of facts between instances.
4. a large number of facts between an instance and a literal (such as `hasName`, `hasID`, `hasBirthDate`, etc.). PARIS needs these to bootstrap its algorithm, and will not work without.
5. optionally a class hierarchy (taxonomy).

See [here](https://suchanek.name/work/publications/vldb2012/index.html) for a slideshow, and [here](https://www.youtube.com/watch?v=Fom62wWTHK) for a recorded talk.

PARIS today
----------

PARIS dates from 2012. It has not been updated since, and it is no longer maintained. However, it still works, and it is used as a baseline for modern knowledge graph alignment (KG alignment) methods. In fact [a recent survey](https://www.vldb.org/pvldb/vol15/p1712-arora.pdf) concluded that “PARIS, the state-of-the-art non-neural method, statistically significantly outperforms all the representative state-of-the-art neural methods in terms of both efficacy and efficiency across a wide variety of dataset types and scenarios”, and that “PARIS should be used as a baseline in every follow-up work on Entity Alignment”.

That said, any comparison has to consider the following differences between PARIS and entity alignment methods:
1. PARIS aligns not just instances, but also relations and classes. That is: if one KB calls the relation `wasBornInCity`, and the other KB calls it `birthPlace`, then PARIS will find out that these relations are the same. This works also if one relation merely subsumes the other. The same goes for classes (finding, e.g., that `Singers` in one KB is a subclass of `Artists` in the other).
2. PARIS deals also with relations whose objects are literals, such as `wasBornOnDate`, `hasNumberOfInhabitants`, etc.
3. PARIS can deal with the situation where not all instances of one KB have a matching counterpart in the other KB.
4. Finally, PARIS does not need any training data. It works out of the box.

These differences have to be taken into account when PARIS is compared to other systems.

License
--------

PARIS is a project of the [Webdam team at INRIA Saclay](http://webdam.inria.fr/wordpress/index.html). The main contributors
are:

* [Fabian M. Suchanek](http://suchanek.name)
* [Pierre Senellart](http://pierre.senellart.com)
* [Antoine Amarilli](http://a3nm.net)
* [Serge Abiteboul](https://abiteboul.com/)
* Mayur Garg

PARIS is available under the [Creative Commons BY-NC license](https://creativecommons.org/licenses/by-nc/3.0/). This means that PARIS can be used freely, except for commercial purposes. Furthermore, the authors of PARIS do not guarantee that the code works as intended.

This license does not cover the third-party libraries used. These are shipped as part of the source archive and as part
of the JAR file for convenience:
* The [MPI Java Tools](http://mpii.de/yago-naga/javatools)
* [Primitive Collections for Java](http://pcj.sourceforge.net)

Publications
------------

If you use PARIS for academic works, please cite

> Fabian M. Suchanek, Serge Abiteboul, Pierre Senellart:  
> [“PARIS: Probabilistic Alignment of Relations, Instances, and Relations”](https://suchanek.name/work/publications/vldb2012.pdf)  
> International Conference on Very Large Databases (VLDB), 2012  
> [Slideshow](https://suchanek.name/work/publications/vldb2012/index.html)

An update of PARIS is discussed in

> Antoine Amarilli, Luis Galárraga, Nicoleta Preda, Fabian M. Suchanek:  
> [“Recent Research Topics around the YAGO Knowledge Base”](https://suchanek.name/work/publications/apweb2014.pdf)  
> Asia Pacific Web Conference  (APWEB), 2014

A detailed comparison of PARIS with KG Alignment approaches can be found in

> Xiang Zhao, Weixin Zeng, Jiuyang Tang, Wei Wang, Fabian M. Suchanek:  
> [“An Experimental Study of State-of-the-Art Entity Alignment Approaches”](https://suchanek.name/work/publications/tkde-2020.pdf)  
> [IEEE Transactions on Knowledge and Data Engineering  (TKDE), 2020](https://ieeexplore.ieee.org/xpl/RecentIssue.jsp?punumber=69)

Running PARIS
------------
See [doc/RunPARIS.md](doc/RunPARIS.md)

Experimental Data
------------
See [doc/Data.md](doc/Data.md)
