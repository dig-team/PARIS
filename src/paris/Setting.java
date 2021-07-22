package paris;

import java.io.File;
import java.io.IOException;

import javatools.administrative.Parameters;
import javatools.filehandlers.FileSet;
import paris.evaluation.GoldImdbYago;
import paris.evaluation.GoldStandard;


/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class holds a setting of two ontologies.*/
public class Setting {
  /** Folder where the computed equalities shall be stored*/
  public final File home;
  /** First ontology*/
  public final File ontology1;
  /** Third ontology. Just kidding. It's the second, of course...*/
  public final File ontology2;
  // there used to be several possible choices for types, now only one remains
  /** Type of the first ontology */
  public final String ontologyType1 = "memory";
  /** Type of the third ontology */
  public final String ontologyType2 = "memory";
  /** Folder where the TSV files shall be output*/
  public final File tsvFolder;
  /** Folder where the Computed-object lives*/
  public final File berkeleyFolder;
  /** Gold standard for evaluation*/
  public final GoldStandard gold;
  /** Name of the setting*/
  public final String name;
  /** Start iteration*/
  public int startIteration;
  /** Start entity*/
  public int startEntity;
  /** Start iteration*/
  public int endIteration;
  /** number of threads */
  public int nThreads;
  /** Constructs a setting*/
  public Setting(String name, String homeFolder, String o1, String o2, String berkeley, String tsv,GoldStandard g) {
    this.name=name;
    home=new File(homeFolder);
    ontology1=new File(home,o1);
    ontology2=new File(home,o2);
    tsvFolder=new File(home,tsv);
    berkeleyFolder=new File(home,berkeley);    
    gold=g;
    startIteration=0;
    startEntity=0;
    endIteration=5;
  }
  /** Constructs a setting from an ini file
   * @throws IOException */
  public Setting(File ini) throws IOException {
    Parameters.init(ini);
    name=FileSet.newExtension(ini.getName(),"");
    tsvFolder=Parameters.getOrRequestAndAddFile("resultTSV", "Enter the folder where the result shall be stored in TSV format:");
    berkeleyFolder=Parameters.getOrRequestAndAddFile("resultDB", "Enter the folder where the result shall be stored in database format:");
    gold=null;
    ontology1=Parameters.getOrRequestAndAddFile("factstore1", "Enter the folder where the first fact store lives:");
    ontology2=Parameters.getOrRequestAndAddFile("factstore2", "Enter the folder where the second fact store lives:");
    home=Parameters.getOrRequestAndAddFile("home", "Enter the folder where log information can be stored");
    startIteration=Parameters.getInt("startIteration", 0);
    startEntity=Parameters.getInt("startEntity", 0);
    endIteration=Parameters.getOrRequestAndAddInt("endIteration","Enter the number of iterations to run (e.g., 4)");
    nThreads=Parameters.getOrRequestAndAddInt("nThreads","Enter the number of threads");
  }
  // Different settings
  public static final Setting restaurants=new Setting("Restaurants","c:/fabian/data/restaurant","restaurant1","restaurant2","eqv","eqvtsv",new GoldStandard(112));
  public static final Setting restaurantsnormalized=new Setting("RestaurantsNormalized","c:/fabian/data/restaurant_normalized","restaurant1","restaurant2","eqv","eqvtsv",new GoldStandard(112));
  public static final Setting persons =new Setting("Persons","c:/fabian/data/personA","person1","person2","eqv","eqvtsv",new GoldStandard(500));
  public static final Setting personsnormalized=new Setting("PersonsNormalized","c:/fabian/data/person_normalized","person1","person2","eqv","eqvtsv",new GoldStandard(500));
  public static final Setting yagodbpedia=new Setting("YagoDbpedia","/media/ssd/fabian/data","yago/berkeley","dbpedia/berkeley","eqv","eqvtsv",new GoldStandard(1429686,GoldStandard.yagoDbpediaRelations));
  public static final Setting yagodbpediaMoreFacts=new Setting("YagoDbpediaMoreFacts","/media/ssd/fabian/data","yago/berkeley","dbpedia/berkeley","eqv","eqvtsv",new GoldStandard(1049629,GoldStandard.yagoDbpediaRelations));
  public static final Setting dbpediaSelf=new Setting("DbpediaSelf","/media/ssd/fabian/data","dbpedia/berkeley","dbpedia/berkeley","eqv","eqvtsv",new GoldStandard(2365777,GoldStandard.yagoDbpediaRelations));
  public static final Setting yagodbpediaNew=new Setting("YagoDbpediaNew","/home/a3nm/DOCUMENTS/stage/paris","yago/memory","dbpedia/memory","eqv","eqvtsv",new GoldStandard(1484735,GoldStandard.yagoDbpediaRelations));
  public static final Setting imdbyago=new Setting("ImdbYago","/media/ssd/fabian/data","yago/berkeley","imdb/berkeley","eqv","eqvtsv",new GoldImdbYago());
}
