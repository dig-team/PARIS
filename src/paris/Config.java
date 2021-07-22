package paris;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javatools.administrative.Announce;
import javatools.datatypes.FinalMap;
import javatools.parsers.DateParser;
import javatools.parsers.NumberParser;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class contains the design choices for PARIS. 
 * PARIS is parameter-free in the sense that one setting should do a decent job on all
 * ontologies. If it does not, consider fiddling with these parameters. See our 
 * paper for more details. */

public class Config {

  //-----------------------------------------------------------------------------
  //                   Settings
  //-----------------------------------------------------------------------------

  /** Treat the ids of entities as an additional property (e.g., generates the virtual fact 
   * y:Berlin---hasLabel-->"Berlin")
   * Default is FALSE.*/
  public static boolean treatIdAsRelation = false;

  /** Normalize strings to lowercase letters and numbers when loading RDF/N3 triples into the FactStore. 
   * Switch this on BEFORE YOU GENERATE THE ONTOLOGIES, if the ontologies that you want to match contain names and
   * strings in slight variations (Berlin=berlin). Default is FALSE.*/
  public static boolean normalizeStrings = false;

  /** Normalize dates to the years. . 
   * Switch this on BEFORE YOU GENERATE THE ONTOLOGIES, if the ontologies that you want to match contain dates on one side
   * and years on the other side. Default is FALSE.*/
  public static boolean normalizeDatesToYears = false;

  /** Types of string distance used in Computed.compareStrings()*/
  public static enum LiteralDistance {
    IDENTITY, BAGOFCHARS, NORMALIZE, BAGOFWORDS, LEVENSHTEIN, SHINGLING, SHINGLINGLEVENSHTEIN
  };

  /** String distance used in Computed.compareStrings() for negative evidence.
   * Has an effect only if punish=TRUE. 
   * There is not much use tinkering with this value, leave it at the default value of IDENTITY.
   * If you need a string distance, use normalizeStrings=TRUE.*/
  /* if you use SHINGLING or SHINGLINGLEVENSHTEIN, make sure that the fact stores were generated with the literal indexes */
  public static LiteralDistance literalDistance = LiteralDistance.IDENTITY;
  
  /** divide approx matches by this value */
  public static double penalizeApproxMatches = 2.;
  
  /** Use literalDistance for equality (and not just for punishment). Slows things down. */
  public static final boolean literalDistanceForEquality = true;
  
  /** Threshold on distance between literals to take them into account as a possible match */
 public static final double literalDistanceThreshold = 0.5;

  /** Anything below this threshold is ignored. Value does not have much influence. Default is at 0.1.*/
  public static final double THETA = 0.1;

  /** Take only the maximum assignment. This greatly speeds up the process at virtually no cost. Default is TRUE.*/
  public static final boolean takeMax = true;

  /** Take only one of the maximal assignments. This speeds up the process even more at no cost. Default is TRUE.*/
  public static final boolean takeMaxMax = true;

  /** Take both sub- and super relations into account for computing the equality. 
   * This is a bit slower, but the right way to do it. Default is TRUE.*/
  public static final boolean subAndSuper = true;

  /** Initial small value for all relations. This value does not have much effect, but should be greater-equal than THETA.
   * Default is THETA.*/
  public static double IOTA = Config.THETA;

  /** Set this to TRUE to take into account negative evidence (counter evidence) for an equality assignment.
   * This slows down the process, costs a lot in terms of recall, but can increase precision. Consider
   * combining with different string distances or string normalization. Default is FALSE.*/
  public static boolean punish = false;

  /** If switched on, see all literal relations as weakly equivalent at value IOTA. The system will not work if you set this to FALSE. */
  public static boolean initialSmallEquivalence = true;
  
  /** Compress namespaces using prefixes. Might slow things down, so you might want to do it separately */
  public static boolean performCompression = true;
  
  /** Initial size for large HashMap's and HashSet's */
  public static int initialSize = 1024*1024;

  /** Standard namespace prefixes, add your prefixes here to simplify your life.*/
  public static Map<String, String> prefixes = new FinalMap<String, String>(
  		"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:",
  		"http://www.w3.org/2000/01/rdf-schema#", "rdfs:",
  		"http://www.w3.org/2001/XMLSchema#", "xsd:",
  		"http://www.w3.org/2002/07/owl#", "owl:",
  		"http://purl.org/dc/terms/", "dc:",
  		"http://xmlns.com/foaf/0.1/", "foaf:",
  		"http://www.w3.org/2006/vcard/ns#", "vcard:",
  		"http://dbpedia.org/", "dbp:",
  		"http://www.mpii.de/yago/resource/", "y:",
  		"http://www.geonames.org/ontology#", "geo:"
  	);
  
  public static String join(String[] items, String separator) {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (String item : items) {
    	if (!first)
    		builder.append(separator);
    	first = false;
      builder.append(item);
    }
    return builder.toString();
  }

  public static String[] namespaces = prefixes.keySet().toArray(new String[0]);
  public static String namespacesDisjunct = "(" + join(namespaces, "|") + ").*";
  public static Pattern prefixRegexp = Pattern.compile(namespacesDisjunct);
  
  //-----------------------------------------------------------------------------
  //                   Common code
  //-----------------------------------------------------------------------------

  /** Types of entities with their formatters */
  public enum EntityType {
    NUMBER, DATE, STRING, RESOURCE
  }

  /** Formats an entity. This should be a method of EntityType, but then the type is not persistent */
  public static String format(String s, EntityType t) {
    switch(t) {
      case NUMBER:
        s = NumberParser.normalize(s);
        Double d = NumberParser.getDouble(s);
        if(d==null) return(null);
        if(d.intValue()==d.doubleValue()) return(""+d.intValue()); 
        return d.toString();
      case DATE:
        s = DateParser.normalize(s);
        String[] da = DateParser.getDate(s);
        if (da == null) return (null);
        if (normalizeDatesToYears) return (da[0]);
        return (DateParser.newDate(da[0], da[1], da[2]));
      case STRING:
      s = stripQuotes(s);
        if (normalizeStrings) s = normalizeString(s);
        return ('"' + s + '"');
      case RESOURCE:
        // If we normalize, also normalize the resources, 
        // in order to make treatIdAsRelation work
        if (normalizeStrings && treatIdAsRelation) {
          String pref=prefix(s);
          String name=normalizeString(entityName(s));
          s=pref+name;
        }
        if (performCompression)
          return compress(s);
        else
        	return s;
    }
    return(null);
  }
  
  /** Returns the entity name without prefix */
  public static String entityName(String n) {
    int pos=n.lastIndexOf('#');
    pos=Math.max(pos,n.lastIndexOf('/'));
    pos=Math.max(pos,n.lastIndexOf(':'));
    if(pos>0) n=n.substring(pos+1);
    return (n);
  }

  /** Returns the prefix */
  public static String prefix(String n) {
    int pos=n.lastIndexOf('#');
    pos=Math.max(pos,n.lastIndexOf('/'));
    pos=Math.max(pos,n.lastIndexOf(':'));
    if(pos>0) return(n.substring(0,pos+1));
    return (null);
  }

  /** TRUE if the entityname has a prefix*/
  public static boolean hasPrefix(String n) {
    return(prefix(n)!=null);
  }
  
  /** Guesses the type of entity*/
  public static EntityType entityType(String e) {
    return entityType(e, EntityType.STRING);
  }

  /** Guesses the type of entity*/
  public static EntityType entityType(String e, EntityType def) {
    if (e.startsWith("\"")) return (EntityType.STRING);
    if (e.startsWith("http://") || e.matches("[a-z0-9]{1,10}:\\S+")) return (EntityType.RESOURCE);
    if (e.length() > 0 && (e.charAt(0) == '+' || e.charAt(0) == '-' || Character.isDigit(e.charAt(0)))) {
      if (DateParser.isDate(e)) return (EntityType.DATE);
      if (NumberParser.isNumberAndUnit(e)) return (EntityType.NUMBER);
    }
    return def;
  }
  
  /** Guesses the type of literal*/
  public static EntityType literalType(String e) {
    e=stripQuotes(e);
    if (e.length() > 0 && (e.charAt(0) == '+' || e.charAt(0) == '-' || Character.isDigit(e.charAt(0)))) {
      if (DateParser.isDate(e)) return (EntityType.DATE);
      if (NumberParser.isNumberAndUnit(e) || NumberParser.isFloat(e)) return (EntityType.NUMBER);
    }
    return (EntityType.STRING);
  }

  /** Removes quotes*/
  public static String stripQuotes(String s) {
    // "" --> EMPTY
    // "blah"@XX --> blah
    // blah^^datatype --> blah
    // "blah"^^datatype  --> blah
    // ""blah""^^datatype  --> "blah"
    // ""blah""	--> "blah"
    // anything else --> anything else
    if (s.startsWith("\"")) s = s.substring(1);
    if (s.contains("^^")) s = s.substring(0, s.indexOf("^^"));
    if (s.contains("\"@")) s = s.substring(0, s.indexOf("\"@"));
    if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);
    return (s);
  }

  /** Normalizes a string*/
  public static String normalizeString(String s) {
    return (s.toLowerCase().replaceAll("[^a-z0-9]", ""));
  }

  /** Prints config*/
  public static void print() {
    Announce.message("Config:");
    Announce.message("  Ids: " + (treatIdAsRelation ? "treated as relation" : "not treated as relation"));
    Announce.message("  Literal relations: " + (initialSmallEquivalence ? "have a small initial equivalence" : "have initial equivalence of 0"));
    Announce.message("  Theta: " + Config.THETA);
    Announce.message("  Iota: " + Config.IOTA);
    Announce.message("  Literal distance: " + Config.literalDistance);
    Announce.message("  Normalize strings: " + Config.normalizeStrings);
    Announce.message("  Normalize dates to years: " + Config.normalizeDatesToYears);    
    Announce.message("  Punishment: " + Config.punish);
    Announce.message("  Take only max: " + Config.takeMax);
    Announce.message("  Take only one max: " + Config.takeMaxMax);
    Announce.message("  Super- and subrelations: " + Config.subAndSuper);
  }

  /** Compresses a string by prefix*/
  public static String compress(String s) {
  	Matcher matcher = prefixRegexp.matcher(s);
  	if (matcher.matches()) {
  		//System.out.printf("matches %s %s for %s\n", matcher.group(1), prefixes.get(matcher.group(1)), s);
  		String result = (prefixes.get(matcher.group(1)) + s.substring(matcher.group(1).length()));
  		//System.out.printf(result);
  		return result;
  	} else return s;
  }

  /** TRUE if the property ends with '-' */
  public static boolean isInverse(String p) {
    return (p.endsWith("-"));
  }

  /** Inverts a relationship */
  public static String invert(String n) {
    if (isInverse(n)) return (n.substring(0, n.length() - 1));
    else return (n + '-');
  }

  /** Test */
  public static void main(String[] args) {
  }
}
