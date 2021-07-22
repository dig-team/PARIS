#!/bin/bash

ls $1/*.tsv | while read relation
do
  REL=$(echo "$relation" | rev | cut -d'/' -f1 | rev)
  echo -n "y:${REL%.tsv} "
  wc -l $relation | cut -d ' ' -f1
done

