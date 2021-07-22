package paris.storage;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import paris.Config;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.datatypes.FinalMap;
import javatools.datatypes.PeekIterator;
import javatools.filehandlers.FileLines;
import javatools.filehandlers.FileSet;
import javatools.parsers.Char;
import javatools.parsers.DateParser;
import javatools.parsers.NumberParser;

/** Parses an ontology file*/
public abstract class Parser extends PeekIterator<String[]> {

  /** Standard namespace prefixes that we will use */
  public static final Map<String, String> standardPrefixes = new FinalMap<>("rdf:", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdfs:",
      "http://www.w3.org/2000/01/rdf-schema#", "xsd:", "http://www.w3.org/2001/XMLSchema#", "owl:", "http://www.w3.org/2002/07/owl#", "skos:",
      "http://www.w3.org/2004/02/skos/core#", "dc:", "http://purl.org/dc/terms/", "foaf:", "http://xmlns.com/foaf/0.1/", "vcard:",
      "http://www.w3.org/2006/vcard/ns#", "dbp:", "http://dbpedia.org/", "y1:", "http://www.mpii.de/yago/resource/", "y2:",
      "http://yago-knowledge.org/resource/", "geo:", "http://www.geonames.org/ontology#");

  /** Translates the URI into a Qname*/
  public static String compressUri(String s) {
    s = s.trim();
    s = Char.decodeAmpersand(s);
    s = Char.decodeBackslash(s);
    s = stripSquareBrackets(s);
    if (s.startsWith("http://")) {
      for (Entry<String, String> entry : standardPrefixes.entrySet()) {
        if (s.startsWith(entry.getValue())) {
          return (entry.getKey() + s.substring(entry.getValue().length()));
        }
      }
    }
    return (s);
  }

  /** removes <>*/
  public static String stripSquareBrackets(String s) {
    if (s.startsWith("<")) s = s.substring(1);
    if (s.endsWith(">")) s = s.substring(0, s.length() - 1);
    return (s);
  }

  /** returns the appropriate parser for the file*/
  public static Parser forFile(File f) throws IOException {
    switch (FileSet.extension(f).toLowerCase()) {
    	case ".imdb":
    		return new ImdbParser(f);
      case ".tsv":
        return (new TsvParser(f));
      case ".ttl":
      case ".n3":
      case ".n4":
      case ".nt":
        return (new TurtleParser(f));
    }
    return (null);
  }

  /** File lines*/
  protected FileLines lines;

  /** For parsing TTL. This is optimized for speed and will not work for all files! */
  public static class TurtleParser extends Parser {

    /** Current base URI*/
    protected String base = "";

    /** TTL line*/
    protected static Pattern ttlLine = Pattern.compile("([^\\s]+)\\s+([^\\s]+)\\s+(.+)");

    /** Constructor*/
    public TurtleParser(File f) throws IOException {
      lines = new FileLines(f, "UTF-8", null);
    }

    /** Compresses a URI*/
    protected String baseAndCompressUri(String s) {
      if (s.startsWith("<") && !s.startsWith("<http")) s = base + s.substring(1, s.length() - 1);
      return (compressUri(s));
    }

    @Override
    protected String[] internalNext() throws Exception {
      String line;
      do {
        // Trivial cases
        if (!lines.hasNext()) return (null);
        line = lines.next();
        line = line.trim();
        if (line.isEmpty()) continue;
        if (line.startsWith("#")) continue;
        // BASE
        if (line.startsWith("@base")) {
          base = stripSquareBrackets(line.substring(5, line.length() - 1).trim());
          continue;
        }
        // PREFIX
        if (line.startsWith("@prefix")) {
          String[] components = line.split("\\s+");
          components[2] = stripSquareBrackets(components[2]);
          if (standardPrefixes.containsKey(components[1]) && !standardPrefixes.get(components[1]).equals(components[2])) {
            Announce.warning("Ignoring non-standard redefinition of prefix", line);
            continue;
          }
          standardPrefixes.put(components[1], components[2]);
          continue;
        }
        // TTL line
        if (!line.endsWith(" .")) {
          Announce.warning("Line does not end with dot:", line);
          continue;
        }
        line = line.substring(0, line.length() - 2);
        Matcher m = ttlLine.matcher(line);
        if (!m.matches()) {
          Announce.warning("Cannot parse line:", line);
          continue;
        }
        return new String[] { baseAndCompressUri(m.group(1)), baseAndCompressUri(m.group(2)), baseAndCompressUri(m.group(3)) };
      } while (true);
    }

  }

  /** Parses TSV files*/
  public static class TsvParser extends Parser {

    /** If the TSV file does not contain the relation, use this one*/
    protected String relationName;

    /** Constructor with default relation name*/
    public TsvParser(File f) throws IOException {
      lines = new FileLines(f, "UTF-8", null);
      relationName = FileSet.newExtension(f.getName(), null);
      if (relationName.equals("type")) relationName = "rdf:type";
      if (relationName.equalsIgnoreCase("subclassof")) relationName = "rdfs:subclassOf";
      relationName = compressUri(relationName);
    }

    @Override
    protected String[] internalNext() throws Exception {
      if (!lines.hasNext()) return (null);
      String[] line = lines.next().split("\t");
      switch (line.length) {
        case 2:
          // Old YAGO: subject TAB object
          return (new String[] { compressUri(line[0]), relationName, compressUri(line[1]) });
        case 3:
          // Old YAGO: #id TAB subject TAB object
          if (line[0].startsWith("#")) return (new String[] { compressUri(line[1]), relationName, compressUri(line[2]) });
          // Standard TSV: subject TAB predicate TAB object
          return (new String[] { compressUri(line[0]), compressUri(line[1]), compressUri(line[2]) });
        case 4:
          // Old YAGO: #id TAB subject TAB object TAB confidence
          if (line[0].startsWith("#")) return (new String[] { compressUri(line[1]), relationName, compressUri(line[2]) });
          // else fall thru
        case 5:
          return (new String[] { compressUri(line[1]), compressUri(line[2]), compressUri(line[3]) });
        default:
          Announce.warning("Unsupported number of columns (", line.length, ")", line);
          return (null);
      }
    }

  }
  
  /** Parses IMDB TSV files */
  public static class ImdbParser extends Parser {
    /** How to treat IMDB relations*/
    public enum IMDBType {
      RESOURCE(Config.EntityType.RESOURCE), NUMBER(Config.EntityType.NUMBER), DATE(Config.EntityType.DATE), STRING(Config.EntityType.STRING), STRINGASRESOURCE(
          Config.EntityType.RESOURCE)/* , META(null)*/;

      public Config.EntityType entityType;

      IMDBType(Config.EntityType e) {
        entityType = e;
      }
    };

    /** If the TSV file does not contain the relation, use this one*/
    protected String relationName;
    IMDBType targetType;
    Set<String> stringsThatAreEntities;
    int counter;
    Iterator<String> stringsAsEntities;
    
    /** Maps IMDB relations to their target type*/
    public static final Map<String, IMDBType> imdbRelations = new FinalMap<String, IMDBType>(
                    "actedIn",
                    IMDBType.RESOURCE,
                    "bornIn",
                    IMDBType.RESOURCE,
                    "bornOn",
                    IMDBType.DATE,
                    //"character",IMDBType.META,
                    "cinematographerOf",
                    IMDBType.RESOURCE,
                    "composerOf",
                    IMDBType.RESOURCE,
                    //"constumeDesignerOf",IMDBType.RESOURCE,
                    //"creditPosition",IMDBType.NUMBER,
                    //"deceasedFrom",IMDBType.STRING,
                    "deceasedIn", IMDBType.RESOURCE, "deceasedOn", IMDBType.DATE,
                    "directorOf", IMDBType.RESOURCE, "editorOf", IMDBType.RESOURCE,
                    "episodeOf", IMDBType.RESOURCE,
                    "firstName",
                    IMDBType.STRING,
                    //"gender",IMDBType.RESOURCE,
                    "hasHeight", IMDBType.NUMBER,
                    "hasLanguage",
                    IMDBType.STRINGASRESOURCE,
                    //"inCountry",IMDBType.META,
                    "label", IMDBType.STRING, "lastName", IMDBType.STRING, "locatedIn",
                    IMDBType.RESOURCE, "nickName", IMDBType.STRING, "producedIn",
                    IMDBType.STRINGASRESOURCE, "producerOf", IMDBType.RESOURCE,
                    //"productionDesignerOf",IMDBType.RESOURCE,
                    "releasedOn", IMDBType.DATE, "type", IMDBType.STRINGASRESOURCE,
                    "writerOf", IMDBType.RESOURCE);

    /** Constructor with default relation name*/
    public ImdbParser(File f) throws IOException {
      lines = new FileLines(f, "UTF-8", null);
      relationName = FileSet.newExtension(f.getName(), null);
      targetType = imdbRelations.get(relationName);
      relationName = relationName.equals("type") ? "rdf:type" : relationName.equals("label") ? "rdfs:label" : relationName;
      relationName = compressUri(relationName);
      assert (targetType != null);
      stringsThatAreEntities = new TreeSet<String>();
      counter = 0;
    }
    
    @Override
    protected String[] internalNext() throws Exception {
    	if (!lines.hasNext()) {
    		if (stringsAsEntities == null)
    			stringsAsEntities = stringsThatAreEntities.iterator();
    		if (!stringsAsEntities.hasNext())
    			return null;
    		String entity = stringsAsEntities.next();
        return new String[] { entity.replace(' ', '_'), "rdfs:label", "\"" + entity + "\""};
    	}
      String[] split = lines.next().split("\t");
      if (split.length < 3) {
      	// some facts in bornOn have no object
    		// jump to next fact
      	return internalNext();
      }
      String arg1 = split[1];
      String arg2 = split[2];
      switch (targetType) {
        case DATE:
          arg2 = DateParser.normalize(arg2);
          assert(!arg2.startsWith("\""));
          arg2 = "\"" + arg2 + "\"";
          break;
        case NUMBER:
          if (arg2.contains("1/2\"")) return internalNext();
          arg2 = arg2.replaceAll("(\\d+)' (\\d+)\"", "\\1 feet \\2 inches");
          arg2 = arg2.replaceAll("(\\d+)'", "\\1 feet");
          arg2 = NumberParser.normalize(arg2).trim();
          assert(!arg2.startsWith("\""));
          arg2 = "\"" + arg2 + "\"";
          break;
        case STRING:
          if (arg2.contains("$") || arg2.length() < 3 || (!Character.isLetter(arg2.charAt(0)) && !Character.isDigit(arg2.charAt(0)))
              || arg2.equals("Too")) return internalNext();
          assert(!arg2.startsWith("\""));
          arg2 = "\"" + arg2 + "\"";
          break;
        case RESOURCE:
          break;
        case STRINGASRESOURCE:
          stringsThatAreEntities.add(arg2);
          arg2 = arg2.replace(' ', '_');
      }
      assert(arg2 != null);
      return new String[] { arg1, relationName, arg2 };
    }
  }

  @Override
  public void close() {
    lines.close();
  }

  public static void main(String[] args) throws Exception {
    int counter = 0;
    // Test with old YAGO
    /*for(String[] fact : Parser.forFile(new File("c:/fabian/data/yago2/wasBornOnDate.tsv"))) {
      D.p((Object[])fact);
      if(counter++>10) break;
    }*/
    // Test with new YAGO: TSV
    /*counter=0;
    for(String[] fact : Parser.forFile(new File("c:/fabian/data/yago2s/yagoLiteralFacts.tsv"))) {
      D.p((Object[])fact);
      if(counter++>15) break;
    }*/
    // Test with new YAGO: TTL
    counter = 0;
    for (String[] fact : Parser.forFile(new File("c:/fabian/data/yago2s/yagoLiteralFacts.ttl"))) {
      D.p((Object[]) fact);
      if (counter++ > 20) break;
    }
  }

}
