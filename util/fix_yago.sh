#!/bin/bash

# fix a glitch in YAGO
cd "$1"

pv means.tsv | sed 's/	\([^"]*\)	/	"\1"	/' > meansfixed.tsv
mv meansfixed.tsv means.tsv
sed -i 's/f*e*male$/"&"/' hasGender.tsv

