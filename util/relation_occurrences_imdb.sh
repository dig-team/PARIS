#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# PARIS maps IMDB relations to other relations, so we have to take it into
# account; the sed command is from FactLoader.java
ls $1/*.tsv | while read relation
do
  REL=$(echo "$relation" | rev | cut -d'/' -f1 | rev)
  echo -n "imdb:${REL%.tsv} "
  wc -l $relation | cut -d ' ' -f1
done | sed 's/imdb:type/rdf:type/;s/imdb:label/rdfs:label/'

