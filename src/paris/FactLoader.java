package paris;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import paris.Config.EntityType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.datatypes.FinalMap;
import javatools.datatypes.FinalSet;
import javatools.datatypes.IterableForIterator;
import javatools.filehandlers.FileLines;
import javatools.parsers.DateParser;
import javatools.parsers.NumberParser;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class reads facts from a source ontology and populates a FactStore
 *
 * It provides code to read from 
 * - an RDF/N3 file
 * - an OWL file
 * - an RT file
 * - YAGO (TSV file + YAGO-specific translations)
 * - IMDB (TSV file + IMDB-specific translations)
 * The code does type detection on its own and 
 * ignores all type information that the file might contain. */

public class FactLoader {
	private File source;
	private String type;
	private String namespace;
	private String prefix;
	private FactStore factstore;
	
	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

  public FactLoader(File source, String type, FactStore factstore, String namespace, String prefix) {
    if (!prefix.endsWith(":")) prefix = prefix + ":";
  	this.source = source;
  	this.type = type;
  	this.factstore = factstore;
  	this.namespace = namespace;
  	this.prefix = prefix;
  	Config.prefixes.put(prefix, namespace);
  }

	/** Relations to exclude from YAGO*/
	public static final Set<String> yagoExcludeRelations = new FinalSet<String>(
			"type_star", "isCalled", "hasWikipediaUrl", "hasPreferredMeaning",
			"hasPreferredName", "hasGeoCoordinates", "hasWebsite", "hasGloss",
			"hasValue", "hasGeonamesId", "hasSynsetId", "hasUTCOffset",
			"hasDuration", "endedOnDate", "startedOnDate", "wasDestroyedOnDate",
			"hasHeight", "partOf", "hasWeight", "hasPopulationDensity",
			"hasMusicalRole", "hasPages", "hasPredecessor", "hasRevenue");
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
  
  /** Maps Yago relation names to their RDFS equivalent */
  public static FinalMap<String, String> yagoMap = new FinalMap<String, String>("type", "rdf:type", "subclassOf", "rdfs:subclassOf", "subpropertyOf",
      "rdfs:subPropertyOf", "hasDomain", "rdfs:domain", "hasRange", "rdfs:range", "means", "rdfs:label");

  /** How to treat IMDB relations*/
  public enum IMDBType {
    RESOURCE(Config.EntityType.RESOURCE), NUMBER(Config.EntityType.NUMBER), DATE(Config.EntityType.DATE), STRING(Config.EntityType.STRING), STRINGASRESOURCE(
        Config.EntityType.RESOURCE), META(null);

    public Config.EntityType entityType;

    IMDBType(Config.EntityType e) {
      entityType = e;
    }
  };

  /** TRUE for YAGO fact identifiers*/
  public static boolean isYagoFactId(String s) {
    return (s.matches("#\\d+"));
  }
  
  public void run() throws IOException {
  	factstore.registerPrefixNamespace(prefix, namespace);
  	add(source, type);
  	factstore.init();
  }
  
	/** Reads into a fact store */
	public void add(File file, String parseType) throws IOException {
		// this is the case where we read from an existing database
		// so we don't need to load anything
		if (parseType.equalsIgnoreCase("berkeley"))
			return;
		
    // Recursive case
    if (file.isDirectory()) {
      Announce.doing("Loading", file);
      for (File f : file.listFiles())
        add(f, parseType);
      Announce.done();
      return;
    }
    
    boolean test = false;

    // Recursive case
    if (file.isDirectory()) {
      Announce.doing("Loading", file);
      for (File f : file.listFiles())
        add(f, parseType);
      Announce.done();
      return;
    }

    // This is the special fact adder for IMDB
    // This will not necessarily work any more
    if (parseType.equalsIgnoreCase("pierreimdb")) {
      String relation = file.getName().substring(0, file.getName().length() - 4);
      IMDBType targetType = imdbRelations.get(relation);
      if (targetType == null) return;
      Set<String> stringsThatAreEntities = new TreeSet<String>();
      relation = relation.equals("type") ? "rdf:type" : relation.equals("label") ? "rdfs:label" : "imdb:" + relation;
      FileLines lines = new FileLines(new InputStreamReader(new FileInputStream(file), "UTF8"));
      int counter = 0;
      for (String line : lines) {
        if (test && counter++ > 10) {
          lines.close();
          break;
        }
        String[] split = line.split("\t");
        if (split.length < 3) continue;
        String arg1 = split[1];
        String arg2 = split[2];
        arg1 = "imdb:" + arg1;
        switch (targetType) {
          case DATE:
            arg2 = DateParser.normalize(arg2);
            break;
          case META:
            Announce.warning("Don't know what to do with meta facts");
            return;
          case NUMBER:
            if (arg2.contains("1/2\"")) continue;
            arg2 = arg2.replaceAll("(\\d+)' (\\d+)\"", "\\1 feet \\2 inches");
            arg2 = arg2.replaceAll("(\\d+)'", "\\1 feet");
            arg2 = NumberParser.normalize(arg2).trim();
            break;
          case STRING:
            if (arg2.contains("$") || arg2.length() < 3 || (!Character.isLetter(arg2.charAt(0)) && !Character.isDigit(arg2.charAt(0)))
                || arg2.equals("Too")) continue;
            break;
          case RESOURCE:
            arg2 = "imdb:" + arg2;
            break;
          case STRINGASRESOURCE:
            stringsThatAreEntities.add(arg2);
            arg2 = "imdb:" + arg2.replace(' ', '_');
        }
        factstore.add(arg1, relation, arg2, targetType.entityType);
      }
      for (String entity : stringsThatAreEntities)
        factstore.add("imdb:" + entity.replace(' ', '_'), "rdfs:label", entity, EntityType.RESOURCE);
      return;
    }

    // This is the special fact adder for YAGO
    if (parseType.equalsIgnoreCase("yagonative")) {
      String relation = file.getName().substring(0, file.getName().length() - 4);
      if (relation.startsWith("_") || yagoExcludeRelations.contains(relation)) return;
      boolean swapArgs = relation.equals("means");
      if (yagoMap.containsKey(relation)) relation = yagoMap.get(relation);
      else relation = "y:" + relation;
      FileLines lines = new FileLines(file, "Loading " + relation);
      int counter = 0;
      for (String line : lines) {
        if (test && counter++ > 10) {
          lines.close();
          break;
        }
        String[] split = line.split("\t");
        if (split.length < 3) continue;
        if (isYagoFactId(split[1]) || isYagoFactId(split[2])) {
          lines.close();
          Announce.message("  Meta facts only");
          break;
        }
        String arg1 = split[1];
        String arg2 = split[2];
        if (Config.entityType(arg1) == EntityType.RESOURCE) arg1 = "y:" + arg1;
        if (Config.entityType(arg2) == EntityType.RESOURCE) arg2 = "y:" + arg2;
        if (swapArgs) {
          String t = arg1;
          arg1 = arg2;
          arg2 = t;
        }
        EntityType etype = Config.entityType(arg2, EntityType.RESOURCE);
        factstore.add(arg1, relation, arg2, etype);
      }
      return;
    }

    // This is the generic fact TSV fact loader
    if (file.getName().endsWith(".tsv")) {
      if (!NumberParser.isInt(parseType) || parseType.length() != 3) {
        Announce.error("For TSV files, the parseType has to be 3 digits, and not", parseType);
        return;
      }
      int subjpos = parseType.charAt(0) - '1';
      if (subjpos < 0 || subjpos > 9) Announce.error("Invalid subject position identifier in parseType", parseType);
      int verbpos = parseType.charAt(1) - '1';
      if (verbpos < 0 || verbpos > 9) Announce.error("Invalid predicate position identifier in parseType", parseType);
      int objpos = parseType.charAt(2) - '1';
      if (objpos < 0 || objpos > 9) Announce.error("Invalid object position identifier in parseType", parseType);
      Map<String, Integer> entityTypes = new TreeMap<String, Integer>();
      Set<String> inverse = new TreeSet<String>();
      Map<String, Integer> counter = new TreeMap<String, Integer>();
      Announce.doing("Loading " + file.getName());
      FileLines lines = new FileLines(file);
      for (String line : lines) {
        if (line.trim().length() == 0) continue;
        String[] split = line.split("\t");
        if (split.length <= objpos || split.length <= subjpos || split.length <= verbpos) {
          Announce.warning("Not enough tab-separated elements in line\n" + line);
          continue;
        }
        String arg1 = split[subjpos];
        String relation = split[verbpos];
        String arg2 = split[objpos];
        if (test) {
          if (D.getOrZero(counter, relation) > 10) continue;
          D.addKeyValue(counter, relation, 1);
        }
        
        // Hacks for Konstantina
//        if(file.getName().startsWith("imdb")) {
//          if(relation.equals("actedIn")) {
//            int pos=arg1.indexOf(',');
//            if(pos>0) arg1=arg1.substring(pos+2)+' '+arg1.substring(0,pos);
//          }
//          if(arg2.matches(".+19..")) {
//            arg2=arg2.substring(0,arg2.length()-5);
//          }
//          if(arg1.matches(".+19..")) {
//            arg1=arg1.substring(0,arg1.length()-5);
//          }
//        }
        
        if (!entityTypes.containsKey(relation)) {
          Announce.message();
          D.p("Please choose among the following options:");
          D.p("Exclude the relationship", relation, "                 (1)");
          D.p("Facts are of the form    ENTITY", relation, "ENTITY    (2)");
          D.p("Facts are of the form    LITERAL", relation, "ENTITY   (3)");
          D.p("Facts are of the form    ENTITY", relation, "LITERAL   (4)");
          D.p("Facts are of the form    LITERAL", relation, "LITERAL  (5)");
          switch ((int) D.readLong("Your choice:")) {
            case 2:
              entityTypes.put(relation, 2);
              break;
            case 3:
              entityTypes.put(relation, 1);
              inverse.add(relation);
              break;
            case 4:
              entityTypes.put(relation, 1);
              break;
            case 5:
              D.println("Such relations cannot be converted to RDF. Skipping.");
              entityTypes.put(relation, 0);
              continue;
            default:
              D.println("Skipping.");
              entityTypes.put(relation, 0);
              continue;
          }
        }
        int type = entityTypes.get(relation);
        if (inverse.contains(relation)) {
          String a = arg1;
          arg1 = arg2;
          arg2 = a;
          relation += "INV";
        }
        if (!Config.hasPrefix(arg1)) arg1 = prefix + Config.stripQuotes(arg1);
        if (!Config.hasPrefix(relation)) relation = prefix + Config.stripQuotes(relation);
        switch (type) {
          case 0:
            // Exclude
            continue;
          case 1:
            // For literals            
            factstore.add(arg1, relation, arg2, Config.literalType(arg2));
            break;
          case 2:
            // For entities
            if (!Config.hasPrefix(arg2)) arg2 = prefix + Config.stripQuotes(arg2);
            factstore.add(arg1, relation, arg2, EntityType.RESOURCE);
            break;
        }
      }
      lines.close();
      Announce.done();
      return;
    }

    // OWL and RDF
    if (file.getName().endsWith(".owl") || file.getName().endsWith(".rdf")) {
      Announce.doing("Loading", file);
      Announce.doing("Creating model");
      Model model = ModelFactory.createDefaultModel();
      InputStream in = new FileInputStream(file);
      model.read(in, null);
      in.close();
      Announce.done();
      Announce.progressStart("Storing model", model.size());
      int counter = 0;
      for (Statement s : new IterableForIterator<Statement>(model.listStatements())) {
        if (test && counter++ > 10) break;
        if (s.getObject().isLiteral()) {
          factstore.add(s.getSubject().toString(), s.getPredicate().toString(), s.getObject().toString(), Config.literalType(s.getObject().toString()));
        } else {
          factstore.add(s.getSubject().toString(), s.getPredicate().toString(), s.getObject().toString(), EntityType.RESOURCE);
        }
      }
      Announce.progressDone();
      model.close();
      Announce.done();
      return;
    }

    // NT
    if (file.getName().endsWith(".nt")) {
      FileLines lines = new FileLines(file, "Loading " + file);
      int counter = 0;
      for (String line : lines) {
        if (test && counter++ > 100) break;
        line = line.trim();
        if (line.length() == 0) continue;
        try {
          String subj = line.substring(1, line.indexOf('>'));
          line = line.substring(subj.length() + 4);
          String pred = line.substring(0, line.indexOf('>'));
          line = line.substring(pred.length() + 2);
          String obj = line.substring(0, line.length() - 2);
          if (obj.startsWith("<")) {
            factstore.add(subj, pred, obj.substring(1, obj.length() - 1), EntityType.RESOURCE);
          } else if (obj.startsWith("\"")) {
            String indication = "";
            if (obj.contains("^^"))
              indication = obj.substring(obj.indexOf("^^"));
            obj = Config.stripQuotes(obj);
            EntityType guessedType = Config.literalType(obj);
            if (indication.equals("^^<http://www.w3.org/2001/XMLSchema#gYear>"))
              guessedType = EntityType.DATE;
            factstore.add(subj, pred, obj, guessedType);
          }
        } catch (Exception e) {
          D.p(line);
          D.p(e.getMessage());
        }
      }
      lines.close();
      return;
    }

    Announce.warning("Unknown file type", file.getName(), "for parse type", parseType);
  }

  
}
