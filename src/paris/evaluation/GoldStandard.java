package paris.evaluation;

import java.util.Map;
import java.util.TreeMap;

import javatools.datatypes.FinalMap;
import javatools.parsers.Char;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class can evaluate a given mapping wrt a gold standard mapping.
 */

public class GoldStandard {

  /** Number of entities matched in the ideal case*/
  protected int numGoldStandard;
  
  /** Number of entities matched in the ideal case*/
  public int numGoldStandard() {
    return(numGoldStandard);
  }
  
  /** Constructor*/
  protected GoldStandard() {
    
  }
  
  /** Constructor with the number of entities matched in the ideal case*/
  public GoldStandard(int num) {
    numGoldStandard=num;
  }

  /** Constructor with the number of entities matched in the ideal case + the gold standard relation mapping*/
  public GoldStandard(int num, Map<String,String> relations) {
    numGoldStandard=num;
    relationGold=relations;
  }
  
  /** Types of evaluations*/
  public enum EvalVal {CORRECT, WRONG, DONTKNOW};
  
  /** Returns the name of an URI. Does some normalization for comparing certain ontologies*/
  public static String name(String uri) {
    if(uri.startsWith("okkam2:person") || uri.startsWith("okkam:person")) {
      if(!uri.contains("-Person")) return(null);
      if(!uri.endsWith("0")) return(Char.cutLast(uri).replace(":person2",":person1")+"0");
      return(uri);
    }
    if(uri.startsWith("okkam:restaurant") || uri.startsWith("okkam1:restaurant") || uri.startsWith("okkam2:restaurant")) {
      if(!uri.contains("-Restaurant")) return(null);
      return(uri.replace("2:",":").replace("1:", ":"));
    }
    int pos = uri.lastIndexOf('/');
    if (pos != -1) uri = uri.substring(pos + 1);
    pos = uri.lastIndexOf(':');
    if (pos != -1) uri = uri.substring(pos + 1);
    uri=uri.replace(" ", "").replace("_","");
    return (Char.decodeAndNormalize(uri));
  }

  /** Evaluates a match between a pair of entities*/
  public EvalVal evaluate(String s1, String s2) {
    String n1=name(s1);
    if(n1==null) return(EvalVal.DONTKNOW);
    String n2=name(s2);
    if(n2==null) return(EvalVal.DONTKNOW);
    if(n1.equals(n2)) return(EvalVal.CORRECT);
    return(EvalVal.WRONG);
  }

  /** How relatins should be mapped in the ideal case*/
  public Map<String,String> relationGold=new TreeMap<String, String>();
  
  /** Evaluates a pair of relations*/
  public EvalVal evaluateRelation(String key, String value) {
    String real=relationGold.get(key);
    if(real==null) return(EvalVal.DONTKNOW);
    if(real.equals(value)) return(EvalVal.CORRECT);
    return(EvalVal.WRONG);
  }

  /** Number of relations matched in the ideal case*/
  public int numGoldStandardRelations() {
    return relationGold.size();
  }
  
  /** Manual relation mapping for YAGO/DBpedia (incomplete, for measuring precision only)*/
 public static Map<String,String> yagoDbpediaRelations=new FinalMap<String,String>(      
      "dbp:ontology/aSide", "NONE",
      "dbp:ontology/abbreviation", "rdfs:label",
      "dbp:ontology/academyAward", "y:created",
      "dbp:ontology/afiAward", "y:directed",
      "dbp:ontology/alias", "rdfs:label",
      "dbp:ontology/almaMater", "y:graduatedFrom",
      "dbp:ontology/americanComedyAward", "y:created",
      "dbp:ontology/anthem", "NONE",
      "dbp:ontology/artist", "y:created-",
      "dbp:ontology/author", "y:created-",
      "dbp:ontology/automobileModel", "rdfs:label",
      "dbp:ontology/award", "y:hasWonPrize",
      "dbp:ontology/baftaAward", "y:created",
      "dbp:ontology/birthDate", "y:wasBornOnDate",
      "dbp:ontology/birthName", "rdfs:label",
      "dbp:ontology/birthPlace", "y:wasBornIn",
      "dbp:ontology/britishComedyAwards", "y:created",
      "dbp:ontology/callSign", "rdfs:label",
      "dbp:ontology/capital", "y:hasCapital",
      "dbp:ontology/ceremonialCounty", "y:isLocatedIn",
      "dbp:ontology/cesarAward", "y:actedIn",
      "dbp:ontology/chiefEditor", "y:created-",
      "dbp:ontology/child", "y:hasChild",
      "dbp:ontology/citizenship", "y:isCitizenOf",
      "dbp:ontology/compiler", "y:created-",
      "dbp:ontology/composer", "y:created-",
      "dbp:ontology/councilArea", "y:isLocatedIn",
      "dbp:ontology/coverArtist", "y:created-",
      "dbp:ontology/creativeDirector", "y:created-",
      "dbp:ontology/creator", "y:created-",
      "dbp:ontology/currency", "y:hasCurrency",
      "dbp:ontology/dateOfBurial", "NONE",
      "dbp:ontology/daylightSavingTimeZone", "y:isLocatedIn",
      "dbp:ontology/deathDate", "y:diedOnDate",
      "dbp:ontology/deathPlace", "y:diedIn",
      "dbp:ontology/demonym", "rdfs:label",
      "dbp:ontology/designCompany", "y:created-",
      "dbp:ontology/designer", "y:created-",
      "dbp:ontology/developer", "y:created-",
      "dbp:ontology/director", "y:directed-",
      "dbp:ontology/doctoralAdvisor", "y:hasAcademicAdvisor",
      "dbp:ontology/doctoralStudent", "y:hasAcademicAdvisor-",
      "dbp:ontology/editing", "y:directed-",
      "dbp:ontology/emmyAward", "y:created",
      "dbp:ontology/engineType", "NONE",
      "dbp:ontology/executiveProducer", "y:created-",
      "dbp:ontology/faaLocationIdentifier", "rdfs:label",
      "dbp:ontology/feastDay", "NONE",
      "dbp:ontology/filmFareAward", "y:directed",
      "dbp:ontology/formationDate", "y:wasCreatedOnDate",
      "dbp:ontology/formerCallsign", "rdfs:label",
      "dbp:ontology/formerName", "rdfs:label",
      "dbp:ontology/foundationOrganisation", "y:created-",
      "dbp:ontology/foundationPerson", "y:created-",
      "dbp:ontology/foundationPlace", "y:isLocatedIn",
      "dbp:ontology/foundingDate", "y:wasCreatedOnDate",
      "dbp:ontology/foundingPerson", "y:created-",
      "dbp:ontology/geminiAward", "y:hasWonPrize",
      "dbp:ontology/goldenCalfAward", "y:actedIn",
      "dbp:ontology/goldenGlobeAward", "y:created",
      "dbp:ontology/goldenRaspberryAward", "y:actedIn",
      "dbp:ontology/government", "y:isLocatedIn-",
      "dbp:ontology/governmentType", "NONE",
      "dbp:ontology/governor", "y:isLeaderOf-",
      "dbp:ontology/goyaAward", "y:directed",
      "dbp:ontology/grammyAward", "y:created",
      "dbp:ontology/headquarter", "y:isLocatedIn",
      "dbp:ontology/headquarters", "y:isLocatedIn",
      "dbp:ontology/heir", "y:hasChild",
      "dbp:ontology/highestRegion", "y:isLocatedIn",
      "dbp:ontology/iataLocationIdentifier", "rdfs:label",
      "dbp:ontology/icaoLocationIdentifier", "rdfs:label",
      "dbp:ontology/identificationSymbol", "rdfs:label",
      "dbp:ontology/illustrator", "y:created-",
      "dbp:ontology/influenced", "y:influences",
      "dbp:ontology/influencedBy", "y:influences-",
      "dbp:ontology/isbn", "y:hasISBN",
      "dbp:ontology/knownFor", "y:isKnownFor",
      "dbp:ontology/largestSettlement", "y:isLocatedIn-",
      "dbp:ontology/leaderName", "y:isLeaderOf-",
      "dbp:ontology/license", "y:created-",
      "dbp:ontology/lieutenancyArea", "y:isLocatedIn",
      "dbp:ontology/lieutenant", "y:isLeaderOf",
      "dbp:ontology/locatedInArea", "y:isLocatedIn",
      "dbp:ontology/location", "y:isLocatedIn",
      "dbp:ontology/locationCity", "y:isLocatedIn",
      "dbp:ontology/locationIdentifier", "rdfs:label",
      "dbp:ontology/majorShrine", "NONE",
      "dbp:ontology/mayor", "y:wasBornIn-",
      "dbp:ontology/meaning", "rdfs:label",
      "dbp:ontology/meshName", "rdfs:label",
      "dbp:ontology/motto", "y:hasMotto",
      "dbp:ontology/mouthCountry", "y:isLocatedIn",
      "dbp:ontology/mouthRegion", "y:isLocatedIn",
      "dbp:ontology/musicalArtist", "y:created-",
      "dbp:ontology/musicalBand", "y:created-",
      "dbp:ontology/nationalFilmAward", "y:directed",
      "dbp:ontology/nationalTopographicSystemMapNumber", "rdfs:label",
      "dbp:ontology/nationality", "y:isCitizenOf",
      "dbp:ontology/notableIdea", "y:isKnownFor",
      "dbp:ontology/notableStudent", "y:hasAcademicAdvisor-",
      "dbp:ontology/notableWork", "y:created",
      "dbp:ontology/numberOfEmployees", "y:hasNumberOfPeople",
      "dbp:ontology/numberOfStaff", "y:hasNumberOfPeople",
      "dbp:ontology/officialLanguage", "y:hasOfficialLanguage",
      "dbp:ontology/orbitalFlights", "y:hasNumberOfPeople",
      "dbp:ontology/parent", "y:hasChild-",
      //"dbp:ontology/pastor", "NONE", What is this?
      "dbp:ontology/person", "y:created-",
      "dbp:ontology/place", "y:happenedIn",
      "dbp:ontology/placeOfBurial", "NONE",
      "dbp:ontology/populationTotal", "y:hasPopulation",
      "dbp:ontology/predecessor", "y:hasChild-",
      "dbp:ontology/principalArea", "y:isLocatedIn",
      "dbp:ontology/producer", "y:produced-",
      "dbp:ontology/product", "y:created",
      "dbp:ontology/provost", "y:worksAt-", 
      "dbp:ontology/pseudonym", "rdfs:label",
      "dbp:ontology/publisher", "y:created-",
      "dbp:ontology/rector", "NONE",
      "dbp:ontology/releaseDate", "y:wasCreatedOnDate",
      "dbp:ontology/residence", "y:livesIn",
      "dbp:ontology/screenActorsGuildAward", "y:actedIn",
      "dbp:ontology/shipBeam", "NONE",
      "dbp:ontology/significantDesign", "y:created",
      "dbp:ontology/slogan", "y:hasMotto",
      "dbp:ontology/sourceConfluenceRegion", "y:isLocatedIn",
      "dbp:ontology/sourceConfluenceState", "y:isLocatedIn",
      "dbp:ontology/sourceCountry", "y:isLocatedIn",
      "dbp:ontology/sourceRegion", "y:isLocatedIn",
      "dbp:ontology/spouse", "y:isMarriedTo",
      "dbp:ontology/starring", "y:actedIn-",
      "dbp:ontology/state", "y:isLocatedIn",
      "dbp:ontology/stateDelegate", "y:livesIn",
      "dbp:ontology/stateOfOrigin", "NONE",
      "dbp:ontology/storyEditor", "y:created-",
      "dbp:ontology/subtitle", "rdfs:label",
      "dbp:ontology/symbol", "rdfs:label",
      "dbp:ontology/synonym", "rdfs:label",
      "dbp:ontology/territory", "y:happenedIn",
      "dbp:ontology/timeZone", "y:isLocatedIn",
      "dbp:ontology/title", "rdfs:label",
      "dbp:ontology/totalTravellers", "y:hasNumberOfPeople",
      "dbp:ontology/translator", "y:created-",
      "dbp:ontology/usingCountry", "y:hasCurrency-",
      "dbp:ontology/writer", "y:created-",
      "foaf:familyName", "y:hasFamilyName",
      "foaf:givenName", "y:hasGivenName",
      "foaf:name", "rdfs:label",
      "foaf:nick", "rdfs:label",
      "http://www.w3.org/2003/01/geo/wgs84_pos#lat", "y:hasLatitude",
      "http://www.w3.org/2003/01/geo/wgs84_pos#long", "y:hasLongitude",
      "rdfs:label", "foaf:name",
      "y:actedIn", "dbp:ontology/starring-",
      "y:created", "dbp:ontology/writer-",
      "y:diedIn", "dbp:ontology/deathPlace",
      "y:diedOnDate", "dbp:ontology/deathDate",
      "y:directed", "dbp:ontology/director-",
      "y:graduatedFrom", "dbp:ontology/almaMater",
      "y:happenedIn", "dbp:ontology/place",
      "y:hasAcademicAdvisor", "dbp:ontology/doctoralAdvisor",
      "y:hasCapital", "dbp:ontology/capital",
      "y:hasChild", "dbp:ontology/parent-",
      "y:hasCurrency", "dbp:ontology/currency",
      "y:hasISBN", "dbp:ontology/isbn",
      "y:hasLatitude", "http://www.w3.org/2003/01/geo/wgs84_pos#lat",
      "y:hasLongitude", "http://www.w3.org/2003/01/geo/wgs84_pos#long",
      "y:hasMotto", "dbp:ontology/motto",
      "y:hasNumberOfPeople", "dbp:ontology/numberOfStaff",
      "y:hasOfficialLanguage", "dbp:ontology/language",
      "y:hasPopulation", "dbp:ontology/populationTotal",
      "y:hasWonPrize", "dbp:ontology/award",
      "y:influences", "dbp:ontology/influencedBy-",
      "y:isAffiliatedTo", "dbp:ontology/party",
      "y:isCitizenOf", "dbp:ontology/nationality",
      "y:isKnownFor", "dbp:ontology/knownFor",
      "y:isLeaderOf", "dbp:ontology/leaderName-",
      "y:isMarriedTo", "dbp:ontology/spouse",
      "y:isPoliticianOf", "dbp:ontology/deathPlace",
      "y:livesIn", "dbp:ontology/residence",
      "y:produced", "dbp:ontology/producer-",
      "y:wasBornIn", "dbp:ontology/birthPlace",
      "y:wasBornOnDate", "dbp:ontology/birthDate",
      "y:wasCreatedOnDate", "dbp:ontology/releaseDate",
      "y:worksAt", "dbp:ontology/almaMater"
);
 
}
