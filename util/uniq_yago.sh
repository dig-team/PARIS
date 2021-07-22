#!/bin/bash

cd "$1"
for a in *.tsv
do
  pv $a | rev | awk --field-separator='\t' '!a[$1, $2]++' | rev | sponge $a
done

