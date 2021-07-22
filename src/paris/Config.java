package paris;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javatools.administrative.Announce;
import javatools.administrative.D;
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
 * technical report for more details. */


public class Config {

  //-----------------------------------------------------------------------------
  //                   Settings
  //-----------------------------------------------------------------------------

  /** Treat the ids of entities as an additional property (e.g., generates the virtual fact 
   * y:Berlin---hasLabel-->"Berlin")
   * Default is FALSE.*/
  public static boolean treatIdAsRelation = false;
  
  /** Use literalDistance for equality (and not just for punishment). Slows things down. */
  public static final boolean literalDistanceForEquality = true;
  
  /** Threshold on distance between literals to take them into account as a possible match */
  public static final double literalDistanceThreshold = 0.5;

  /** Anything below this threshold is ignored. Value does not have much influence. Default is at 0.1.*/
  public static final double THETA = 0.1;

  /** Take both sub- and super relations into account for computing the equality. 
   * This is a bit slower, but the right way to do it. Default is TRUE.*/
  public static final boolean subAndSuper = true;

  /** Initial small value for all relations. This value does not have much effect, but should be greater-equal than THETA.
   * Default is THETA.*/
  public static double IOTA = Config.THETA;
  
  /** Make initial small value depend on the length of relations (hacky) */
  public static int iotaDependenceOnLength = 20;

	/** should we compute class alignments? */
	public static boolean doComputeClasses = true;
  
// enabling this parameter is not supported anymore
//  /** Set this to TRUE to take into account negative evidence (counter evidence) for an equality assignment.
//   * This slows down the process, costs a lot in terms of recall, but can increase precision. Consider
//   * combining with different string distances or string normalization. Default is FALSE.*/
//  public static boolean punish = false;

// disabling this parameter is not supported anymore
//  /** If switched on, see all literal relations as weakly equivalent at value IOTA. The system will not work if you set this to FALSE. */
//  public static boolean initialSmallEquivalence = true;
  
  /** Use suffixes to infer the type; can be useful */
  public static final boolean useSuffixes = false;
  
  /** Break ties between perfect matches and multiple perfect matches */
  public static double epsilon = 1.01;
  
  /** Ignore classes when aligning entities (only use them for the class alignment) */
  public static boolean ignoreClasses = true;
  
  /** Use the real normalizer, not the wrong version of PARIS 0.1 */
  public static boolean realNormalizer = true;
  
  /** Use the inverse functionality as well as the functionality */
  public static boolean bothWayFunctionalities = false;
    
  /** explore all length one relations after the sampling phase no matter their score during the sampling phase */
  public static boolean allLengthOneAfterSample = true;
  
  /** Standard namespace prefixes, add your prefixes here to simplify your life.*/
  public static Map<String, String> prefixes = new FinalMap<String, String>(
  		"<http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:",
  		"<http://www.w3.org/2000/01/rdf-schema#", "rdfs:",
  		"<http://www.w3.org/2001/XMLSchema#", "xsd:",
  		"<http://www.w3.org/2002/07/owl#", "owl:",
  		"<http://purl.org/dc/terms/", "dc:",
  		"<http://xmlns.com/foaf/0.1/", "foaf:",
  		"<http://www.w3.org/2006/vcard/ns#", "vcard:",
  		"<http://dbpedia.org/", "dbp:",
  		"<http://www.mpii.de/yago/resource/", "y:",
  		"<http://www.geonames.org/ontology#", "geo:",
  		"<http://bnb.data.bl.uk/id/", "bnb:"
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
  @SuppressWarnings("unused")
	public static EntityType literalType(String e, String suffix) {
    if (useSuffixes && suffix.equals("^^<http://www.w3.org/2001/XMLSchema#gYear>"))
    	return EntityType.DATE;
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
    Announce.message("  Theta: " + Config.THETA);
    Announce.message("  Iota: " + Config.IOTA);
    Announce.message("  Super- and subrelations: " + Config.subAndSuper);
  }

  /** Compresses a string by prefix*/
  public static String compress(String s) {
  	Matcher matcher = prefixRegexp.matcher(s);
  	if (matcher.matches()) {
  		//System.out.printf("matches %s %s for %s\n", matcher.group(1), prefixes.get(matcher.group(1)), s);
  		String result = (prefixes.get(matcher.group(1)) + s.substring(matcher.group(1).length()));
  		//System.out.printf(result);
  		if (result.endsWith(">")) {
  			return result.substring(0, result.length()-1);
  		} else {
  			assert(result.endsWith("-"));
  			return result.substring(0, result.length()-2) + "-";
  		}
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
  
  
  public static void main(String[] args) {
  	D.p(compress("<http://foo>"));
  }
}
