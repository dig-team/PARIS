#!/bin/bash

for a in $1/*.tsv; do echo -ne "$a\t"; head -1 $a; done | grep -E '	(#[^	]*	|[^	]*	#)[^	]*$' | cut -f1 | xargs rm

for a in type_star isCalled hasWikipediaUrl hasGeoCoordinates hasWebsite hasGloss hasValue hasGeonamesId hasSynsetId hasUTCOffset hasDuration endedOnDate startedOnDate wasDestroyedOnDate hasHeight partOf hasWeight hasPopulationDensity hasMusicalRole hasPages hasPredecessor hasSuccessor hasRevenue _disambiguationPattern _extendedContextWikiPattern _extendedFullTextWikiPattern _extendedStructureWikiPattern _hasConfidence _hasTypeCheckPattern _hasVirtualRelationImplementation _implies _timeToLocation _wikiBrackets _wikiKeep _wikiPattern _wikiReplace _wikiSplit _wordnetFile
do
  rm -f "$1/$a.tsv"
done

rmdir $1/bdb_tmp
rm $1/yago.ini
