package paris.evaluation;

import java.util.Map;
import java.util.TreeMap;

import paris.Config;

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
    //uri=uri.replace(" ", "").replace("_","");
    assert(!uri.contains(" "));
    return (Char.decodeAndNormalize(uri));
  }

  /** Evaluates a match between a pair of entities*/
  public EvalVal evaluate(String s1, String s2) {
    String n1=name(Config.compress(s1));
    if(n1==null) return(EvalVal.DONTKNOW);
    String n2=name(Config.compress(s2));
    if(n2==null) return(EvalVal.DONTKNOW);
    if(n1.equals(n2)) return(EvalVal.CORRECT);
    return(EvalVal.WRONG);
  }

  /** How relations should be mapped in the ideal case*/
  public Map<String,String> relationGold=new TreeMap<String, String>();
  
  /** Evaluates a pair of relations*/
  public EvalVal evaluateRelation(String key, String value) {
    String real=relationGold.get(key);
    if(real==null) return(EvalVal.DONTKNOW);
    String[] split = real.split("\\|");
    for (String possibility : split) {
    	if(value.equals(possibility))
    		return(EvalVal.CORRECT);
    }
    return(EvalVal.WRONG);
  }

  /** Number of relations matched in the ideal case*/
  public int numGoldStandardRelations() {
    return relationGold.size();
  }
  
  /** Manual relation mapping for YAGO/DBpedia (incomplete, for measuring precision only)*/
 public static Map<String,String> yagoDbpediaRelations = new FinalMap<String,String>(      
      "dbp:ontology/aSide", "NONE",
      "dbp:ontology/abbreviation", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/academyAward", "NONE", // either the prize won or the work that won the prize, so we don't align
      "dbp:ontology/address", "NONE", // street address of a place
      "dbp:ontology/administrativeDistrict", "NONE",
      "dbp:ontology/afiAward", "y:directed",
      "dbp:ontology/alias", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/almaMater", "y:graduatedFrom",
      "dbp:ontology/americanComedyAward", "y:created",
      "dbp:ontology/anthem", "NONE",
      "dbp:ontology/artist", "y:created-",
      "dbp:ontology/atcSuffix", "NONE", // part of an identifier for molecules
      "dbp:ontology/author", "y:created-",
      "dbp:ontology/automobileModel", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/award", "y:hasWonPrize",
      "dbp:ontology/baftaAward", "NONE", // either the prize won or the work that won the prize, so we don't align
      "dbp:ontology/birthDate", "y:wasBornOnDate",
      "dbp:ontology/birthName", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/birthYear", "y:wasBornOnDate", // ??
      "dbp:ontology/birthPlace", "y:wasBornIn",
      "dbp:ontology/britishComedyAwards", "y:created",
      "dbp:ontology/bodyDiscovered", "NONE", // where the body of a dead person was discovered, not the same thing as y:diedIn in general :-p
      "dbp:ontology/callSign", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/callsignMeaning", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-", // full meaning of a callsign
      "dbp:ontology/capital", "y:hasCapital",
      "dbp:ontology/ceremonialCounty", "y:isLocatedIn",
      "dbp:ontology/cesarAward", "y:actedIn",
      "dbp:ontology/chiefEditor", "y:created-",
      "dbp:ontology/child", "y:hasChild",
      "dbp:ontology/citizenship", "y:isCitizenOf",
      "dbp:ontology/collection", "NONE", // contents of a museum's collection
      "dbp:ontology/commandModule", "y:created-", // command module of a spacecraft
      "dbp:ontology/compiler", "y:created-",
      "dbp:ontology/composer", "y:created-",
      "dbp:ontology/councilArea", "y:isLocatedIn",
      "dbp:ontology/coverArtist", "y:created-",
      "dbp:ontology/creativeDirector", "y:created-",
      "dbp:ontology/creator", "y:created-",
      "dbp:ontology/currency", "y:hasCurrency",
      "dbp:ontology/currentSeason", "NONE", // current season of a recurring event (e.g., sport tournaments)
      "dbp:ontology/dateOfBurial", "NONE",
      "dbp:ontology/daylightSavingTimeZone", "y:isLocatedIn",
      "dbp:ontology/deathDate", "y:diedOnDate",
      "dbp:ontology/deathYear", "y:diedOnDate", // ??
      "dbp:ontology/deathPlace", "y:diedIn",
      "dbp:ontology/demonym", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/designCompany", "y:created-",
      "dbp:ontology/designer", "y:created-",
      "dbp:ontology/developer", "y:created-",
      "dbp:ontology/director", "y:directed-",
      "dbp:ontology/doctoralAdvisor", "y:hasAcademicAdvisor",
      "dbp:ontology/doctoralStudent", "y:hasAcademicAdvisor-",
      "dbp:ontology/dorlandsSuffix", "NONE", // apparently some sort of medical identifier of body parts
      "dbp:ontology/editing", "y:directed-",
      "dbp:ontology/emmyAward", "y:created",
      "dbp:ontology/engineType", "NONE",
      "dbp:ontology/era", "NONE", // historical period of philosophers (e.g., "contemporary philosophy")
      "dbp:ontology/executiveProducer", "y:created-",
      "dbp:ontology/faaLocationIdentifier", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/fastestDriverCountry", "NONE", // country of origin of the fastest driver of a race
      "dbp:ontology/federalState", "y:isLocatedIn", // if a city has X as federal state, then it is located in X
      "dbp:ontology/feastDay", "NONE",
      "dbp:ontology/firstDriverCountry", "NONE", // country of origin of the first driver of a race
      "dbp:ontology/filmFareAward", "y:directed",
      "dbp:ontology/formationDate", "y:wasCreatedOnDate",
      "dbp:ontology/formationYear", "y:wasCreatedOnDate", // ??
      "dbp:ontology/formerCallsign", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/formerName", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/foundationOrganisation", "y:created-",
      "dbp:ontology/foundationPerson", "y:created-",
      "dbp:ontology/foundationPlace", "y:isLocatedIn",
      "dbp:ontology/foundingDate", "y:wasCreatedOnDate",
      "dbp:ontology/foundingYear", "y:wasCreatedOnDate",
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
      "dbp:ontology/iataLocationIdentifier", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/icaoLocationIdentifier", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/icd10", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-", // ICD-10 identifier of a medical condition
      "dbp:ontology/identificationSymbol", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/illustrator", "y:created-",
      "dbp:ontology/influenced", "y:influences",
      "dbp:ontology/influencedBy", "y:influences-",
      "dbp:ontology/isbn", "y:hasISBN",
      "dbp:ontology/iupacName", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-", // full name of a molecule, e.g. "(C60-Ih)5,6fullerene"
      "dbp:ontology/knownFor", "y:isKnownFor",
      "dbp:ontology/largestSettlement", "y:isLocatedIn-",
      "dbp:ontology/leaderName", "y:isLeaderOf-",
      "dbp:ontology/license", "y:created-",
      "dbp:ontology/lieutenancyArea", "y:isLocatedIn",
      "dbp:ontology/lieutenant", "y:isLeaderOf",
      "dbp:ontology/locatedInArea", "y:isLocatedIn",
      "dbp:ontology/location", "y:isLocatedIn",
      "dbp:ontology/locationCity", "y:isLocatedIn",
      "dbp:ontology/locationIdentifier", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/lunarModule", "NONE", // lunar module of a spacecraft
      "dbp:ontology/majorShrine", "NONE",
      "dbp:ontology/management", "NONE", // organization which manages a national park
      "dbp:ontology/mayor", "y:wasBornIn-",
      "dbp:ontology/meaning", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/meshName", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/model", "NONE", // vehicle model name (?)
      "dbp:ontology/modelStartYear", "y:wasCreatedOnDate", // if a model started on year X then it "was created in" year X ???
      "dbp:ontology/motto", "y:hasMotto",
      "dbp:ontology/mouthCountry", "y:isLocatedIn",
      "dbp:ontology/mouthRegion", "y:isLocatedIn",
      "dbp:ontology/musicalArtist", "y:created-",
      "dbp:ontology/musicalBand", "y:created-",
      "dbp:ontology/narrator", "y:actedIn-", // the narrator of X "acted in" X ???
      "dbp:ontology/nationalFilmAward", "y:directed",
      "dbp:ontology/nationalTopographicSystemMapNumber", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/nationality", "y:isCitizenOf",
      "dbp:ontology/notableIdea", "y:isKnownFor",
      "dbp:ontology/notableStudent", "y:hasAcademicAdvisor-",
      "dbp:ontology/notableWork", "y:created",
      "dbp:ontology/note", "NONE", // additional info on a vehicle model?
      "dbp:ontology/numberOfEmployees", "y:hasNumberOfPeople",
      "dbp:ontology/numberOfStaff", "y:hasNumberOfPeople",
      "dbp:ontology/officialLanguage", "y:hasOfficialLanguage",
      "dbp:ontology/orbitalFlights", "y:hasNumberOfPeople",
      "dbp:ontology/olivierAward", "NONE", // either the prize won or the work that won the prize, so we don't align
      "dbp:ontology/parent", "y:hasChild-",
      "dbp:ontology/pastor", "NONE", // pastor of a church, or parish, or something
      "dbp:ontology/person", "y:created-",
      "dbp:ontology/personName", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-", // name of a person in the specific case of entities with no matching wikipedia page
      "dbp:ontology/place", "y:happenedIn",
      "dbp:ontology/placeOfBurial", "NONE",
      "dbp:ontology/poleDriverCountry", "NONE", // country of origin of the pole position driver of a race
      "dbp:ontology/populationTotal", "y:hasPopulation",
      "dbp:ontology/port1UndockingDate", "NONE", // date of undocking at the first docking station of a spacecraft
      "dbp:ontology/port2DockingDate", "NONE", // date of docking at the second docking station of a spacecraft
      "dbp:ontology/predecessor", "y:hasChild-",
      "dbp:ontology/principalArea", "y:isLocatedIn",
      "dbp:ontology/producer", "y:produced-|y:created-",
      "dbp:ontology/product", "y:created",
      "dbp:ontology/productionStartYear", "y:wasCreatedOnDate", // date of production start for a vehicle model
      "dbp:ontology/provost", "y:worksAt-", 
      "dbp:ontology/pseudonym", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/publisher", "y:created-",
      "dbp:ontology/rector", "NONE",
      "dbp:ontology/releaseDate", "y:wasCreatedOnDate",
      "dbp:ontology/reservations", "NONE", // boolean indicating if a cafe or restaurant accepts reservations
      "dbp:ontology/residence", "y:livesIn",
      "dbp:ontology/screenActorsGuildAward", "y:actedIn",
      "dbp:ontology/secondDriverCountry", "NONE", // country of origin of the second best driver of a race
      "dbp:ontology/serviceModule", "NONE", // service module of a spacecraft
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
      "dbp:ontology/subtitle", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/symbol", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/synonym", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/territory", "y:happenedIn",
      "dbp:ontology/thirdDriverCountry", "NONE", // country of origin of the third best driver of a race
      "dbp:ontology/timeZone", "y:isLocatedIn",
      "dbp:ontology/title", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "dbp:ontology/totalTravellers", "y:hasNumberOfPeople",
      "dbp:ontology/translator", "y:created-",
      "dbp:ontology/usingCountry", "y:hasCurrency-",
      "dbp:ontology/whaDraft", "NONE", // rank in the World Hockey Association entry draft for an ice hockey player
      "dbp:ontology/writer", "y:created-",
      "foaf:familyName", "y:hasFamilyName|y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "foaf:givenName", "y:hasGivenName|y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "foaf:name", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "foaf:nick", "y:hasPreferredName|y:hasPreferredMeaning-|y:means-",
      "http://www.w3.org/2003/01/geo/wgs84_pos#lat", "y:hasLatitude",
      "http://www.w3.org/2003/01/geo/wgs84_pos#long", "y:hasLongitude",
      "y:hasPreferredName", "foaf:name",
      "y:hasPreferredMeaning", "foaf:name-",
      "y:means", "foaf:name-",
      "y:actedIn", "dbp:ontology/starring-",
      "y:created", "dbp:ontology/artist-",
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
      "y:isLocatedIn", "dbp:ontology/location",
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
