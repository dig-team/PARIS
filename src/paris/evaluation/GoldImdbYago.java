package paris.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javatools.administrative.Announce;
import javatools.administrative.D;
import javatools.datatypes.FinalMap;
import javatools.filehandlers.FileLines;
import javatools.filehandlers.UTF8Writer;
import javatools.parsers.Char;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Faban M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * Gold Standard for matching YAGO & DBpedia.
 * 
 * This gold standard deals with a great number of mappings of different identifiers... No fun...
 */
public class GoldImdbYago extends GoldStandard {

  /** Maps Pierres identifiers to yago identifiers*/
  public Map<String, String> pierreimdb2yago = null;

  /** Constructor*/
  public GoldImdbYago() {
    super(2800000,imdbRelations);
  }
  
  @Override
  public int numGoldStandard() {
    if (pierreimdb2yago == null) {
      Announce.doing("Loading IMDB gold standard");
      pierreimdb2yago = new TreeMap<String, String>();
      try {
        for (String line : new FileLines("c:/fabian/data/yago/fact/hasImdb.tsv", "Loading IMDB movies")) {
          String[] split = line.split("\t");
          pierreimdb2yago.put("tt" + split[2].substring(1, split[2].length() - 1), name(split[1]));
        }
        for (String line : new FileLines("c:/fabian/data/imdb/yago2pierre.tsv", "Loading IMDB people")) {
          String[] split = line.split("\t");
          pierreimdb2yago.put(name(split[1]), name(split[0]));
        }
      } catch (Exception e) {
        e.printStackTrace();
        D.exit();
        throw new RuntimeException(e);
      }
      Announce.message("Sample gold standard pairs:");
      Iterator<Entry<String, String>> idIt = pierreimdb2yago.entrySet().iterator();
      int i = 10;
      while (idIt.hasNext() && i-- > 0) {
        Announce.message(idIt.next());
      }
      Announce.done();
    }
    return pierreimdb2yago.size();
  }

  /** Does a mapping for people identifiers (used only once)*/
  public static void doPersonRedirects() throws Exception {
    Map<String, String> pierreimdb2yago = new TreeMap<String, String>();
    for (String line : new FileLines("c:/fabian/data/imdb/yago2imdb2.tsv", "UTF-8", "Loading IMDB people")) {
      String[] split = line.split("\t");
      pierreimdb2yago.put("nm" + split[2], split[1]);
      if(split[1].endsWith("nderholm")) D.p(Char.encodeBackslash(line));
    }
    for (String line : new FileLines("c:/fabian/data/imdb/pierre2imdb.tsv", "Loading identifiers for IMDB people")) {
      String[] split = line.split("\t");
      String pierreId = split[1];
      String imdbId = split[2];
      String yagoId = pierreimdb2yago.get(imdbId);
      if (yagoId != null) {
        if(yagoId.endsWith("nderholm")) D.p(Char.encodeBackslash(yagoId));
        pierreimdb2yago.remove(imdbId);
        pierreimdb2yago.put(pierreId, yagoId);
      }
    }
    Iterator<String> idIt = pierreimdb2yago.keySet().iterator();
    while (idIt.hasNext()) {
      if (idIt.next().startsWith("nm")) idIt.remove();
    }
    Writer out=new OutputStreamWriter(new FileOutputStream("c:/fabian/data/imdb/yago2pierre.tsv"),"UTF-8");
    for(Entry<String,String> entry : pierreimdb2yago.entrySet()) {
      out.write(entry.getValue()+"\t"+entry.getKey()+"\n");
    }
    out.close();
  }
  
  /** Creates the IMDB gold standard for people from Wikipedia itself*/
  public static void createImdbPersonGold(File wikipedia, File output) throws IOException {
    Writer out = new BufferedWriter(new FileWriter(output));
    String entity = "";
    Pattern imdb = Pattern.compile("imdb ?name ?\\|[ id=]*(\\d+)", Pattern.CASE_INSENSITIVE);
    long counter = 1000000000L;
    for (String line : new FileLines(wikipedia, "Parsing Wikipedia")) {
      if (line.startsWith("    <title>")) entity = line.substring("    <title>".length(), line.length() - "</title>".length());
      /*
       * {{IMDb name| id=0709446 | name=Ayn Rand}}
       * {{imdb name|id=0245385|name= Allan Dwan}}
       * {{imdb name|1152468}}
       * {{IMDb name|id=0001789}}
       */
      if (!line.contains("imdb") && !line.contains("IMD")) continue;
      Matcher m = imdb.matcher(line);
      if (!m.find()) continue;
      out.write("#" + (counter++) + "\t" + entity + "\t" + m.group(1) + "\n");
    }
    out.close();
  }

  /** Cleans the IMDB gold standard*/
  public static void cleanImdbPersonGold(File gold, File goldnew, File yagoFacts) throws IOException {
    Map<String,String> yago2imdb=new TreeMap<String, String>();
    for (String line : new FileLines(gold, "UTF8", "Loading IMDB people")) {
      String[] split = line.split("\t");
      yago2imdb.put(split[1].replace(' ','_'), split[2]);
    }
    for (String line : new FileLines(new File(yagoFacts,"type.tsv"), "Killing fictional IMDB people")) {
      String[] split = line.split("\t");
      if((split[2].contains("ictional") || split[2].contains("haracter")) && yago2imdb.containsKey(Char.decode(split[1]))) {
        yago2imdb.remove(Char.decode(split[1]));
      }
    }    
    Writer out=new UTF8Writer(goldnew);
    long counter=1000000000L;
    for (String line : new FileLines(new File(yagoFacts,"type_star.tsv"), "Cleaning IMDB people")) {
      String[] split = line.split("\t");
      if(split[2].equals("wordnet_person_100007846") && yago2imdb.containsKey(Char.decode(split[1]))) {
        out.write((counter++)+"\t"+Char.decodeBackslash(split[1])+"\t"+yago2imdb.get(Char.decodeBackslash(split[1]))+"\n");
      }
    }    
    out.close();    
  }

  @Override
  public EvalVal evaluate(String s1, String s2) {
    if (pierreimdb2yago == null) numGoldStandard();
    s1 = name(s1);
    s2 = name(s2);
    if (!pierreimdb2yago.containsKey(s2)) return (EvalVal.DONTKNOW);
    if(pierreimdb2yago.get(s2).equals(s1)) {
      return(EvalVal.CORRECT);
    } else {
      //D.p("Wrong:",s1,s2,"instead of",pierreimdb2yago.get(s2));
      return(EvalVal.WRONG);
    }
  }
  
  /** Use to create the gold standard*/
  public static void main(String[] args) throws Exception {
    //cleanImdbPersonGold(new File("c:/fabian/data/imdb/yago2imdb.tsv"), new File("c:/fabian/data/imdb/yago2imdb2.tsv"), new File("c:/fabian/data/yago/fact"));
    //doPersonRedirects();
  }
  

  /** IMDB relations with their manual mapping to YAGO relations*/
  protected static Map<String,String> imdbRelations=new FinalMap<String, String>(
      "imdb:actedIn", "y:actedIn",
      "imdb:bornIn","y:wasBornIn",
      "imdb:bornOn","y:wasBornOnDate",
      "imdb:deceasedIn", "y:diedIn",
      "imdb:deceasedOn", "y:diedOnDate",
      "imdb:directorOf", "y:directed",
      "imdb:firstName", "y:hasGivenName",
      "imdb:hasHeight", "y:hasHeight",      
      "rdfs:label", "rdfs:label",
      "imdb:lastName", "y:hasFamilyName",
      "imdb:locatedIn", "y:isLocatedIn",
      "imdb:nickName", "rdfs:label",      
      "imdb:writerOf", "y:created",
      "imdb:producerOf","y:produced",
      "imdb:releasedOn","y:wasCreatedOnDate",
      
      "y:actedIn","imdb:actedIn", 
      "y:wasBornIn","imdb:bornIn",
      "y:wasBornOnDate","imdb:bornOn",
      "y:diedIn","imdb:deceasedIn", 
      "y:diedOnDate","imdb:deceasedOn", 
      "y:directed","imdb:directorOf", 
      "y:hasGivenName","imdb:firstName", 
      "y:hasHeight", "imdb:hasHeight",
      "y:hasFamilyName","imdb:lastName", 
      "y:isLocatedIn","imdb:locatedIn", 
      "y:created","imdb:writerOf", 
      "y:produced","imdb:producerOf",
      "y:wasCreatedOnDate","imdb:releasedOn"
  
  );

}
