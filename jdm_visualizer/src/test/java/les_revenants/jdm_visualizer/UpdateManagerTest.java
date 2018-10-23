package les_revenants.jdm_visualizer;

import Store.*;
import configuration.MasterStore;
import core.Relation;
import core.RelationQuery;
<<<<<<< HEAD

import static org.junit.Assert.*;
=======
import core.RelationQueryFactory;
>>>>>>> Deleting some useless abstract class

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;
<<<<<<< HEAD


=======
>>>>>>> Deleting some useless abstract class


public class UpdateManagerTest {

    public static Properties prop;
    public static MasterStore masterStore;
<<<<<<< HEAD
    public static List<String> entries,mweEntries;

    public static List<RelationQuery> queries;

    @BeforeClass
    public static void setUp() throws IOException, NumberFormatException, SQLException{
    	
//        prop = new Properties();
//        prop.put(MasterStore.ENTRIES_KEY,"data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES.txt");
//        prop.put(MasterStore.IS_UPDATE_ENTRIES_KEY,false);
//        prop.put(MasterStore.MWE_ENTRIES_KEY,"data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES-MWE.txt");
//        prop.put(MasterStore.RELATION_TYPES_ENTRIES_KEY,"data/relations.txt");
//        prop.put(MasterStore.IS_UPDATE_MWE_ENTRIES_KEY,false);
//        prop.put(MasterStore.MAX_RELATION_IN_DB_KEY,""+1000000);
//        prop.put(Neo4J_RelationStore.USER_KEY,"neo4j");
//        prop.put(Neo4J_RelationStore.PASSWORD_KEY,"vLGGTq5eiHFZwn");
//        prop.put(Neo4J_RelationStore.SERVER_URI_KEY,"bolt://localhost:7687");

        queries=new ArrayList<>();
        queries.add(new RelationQuery("requin",null,true,true,null));
        queries.add(new RelationQuery("chat",new HashSet<String>(Arrays.asList("felin","souris","nom")),true,true,new HashSet<String>(Arrays.asList("r_isa","r_pos"))));
        queries.add(new RelationQuery("chat",new HashSet<String>(Arrays.asList("felin","souris")),true,true,null));
        queries.add(new RelationQuery("chat",null,true,true,new HashSet<String>(Arrays.asList("r_isa","r_pos"))));
        queries.add(new RelationQuery("ours",null,true,true,null));
        queries.add(new RelationQuery("marmotte",null,true,true,null));
        queries.add(new RelationQuery("ours",new HashSet<String>(Arrays.asList("felin","souris","nom","miel")),true,true,new HashSet<String>(Arrays.asList("r_isa","r_associated","r_has_part"))));
        queries.add(new RelationQuery("chat",new HashSet<String>(Arrays.asList("felin","souris","nom")),true,true,new HashSet<String>(Arrays.asList("r_isa","r_pos"))));
=======
    public static TermStore termStore;
    public static List<String> entries,mweEntries,allEntries;
>>>>>>> Deleting some useless abstract class

    public static List<RelationQuery> queries,queries2;
    
    @BeforeClass
    public static void setUp() throws IOException, NumberFormatException, SQLException{
    	queries=new ArrayList<>();
        queries2=new ArrayList<>();     
        entries = Files.readAllLines(Paths.get("data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES.txt"),StandardCharsets.ISO_8859_1);
        mweEntries = Files.readAllLines(Paths.get("data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES-MWE.txt"),StandardCharsets.ISO_8859_1);
        allEntries = Files.readAllLines(Paths.get("data/terms.txt"),StandardCharsets.UTF_8);


        Instant t1 = Instant.now();
        System.out.println("\nSetUp[START]");
        masterStore = new MasterStore("data/config.json");
        termStore = masterStore.getTermStore();
        System.out.println("SetUp [OK] in : "+Duration.between(t1,Instant.now()).toMillis() + "ms");
        

        RelationQueryFactory queryFactory = new RelationQueryFactory(termStore, masterStore.getRelationTypeStore());
        
        queries.add(queryFactory.create("requin"));
        queries.add(queryFactory.create("chat",Arrays.asList("felin","souris","nom"),Arrays.asList("r_isa","r_pos")));
        queries.add(queryFactory.create("chat",Arrays.asList("felin","souris","nom"),null));
        queries.add(queryFactory.create("ours"));
        queries.add(queryFactory.create("ours",Arrays.asList("felin","miel","animal","brun","griffe"),Arrays.asList("r_isa","r_associated","r_carac","r_has_part")));

        String[] words = { 
        		"chien","tortue","médicament",
        		"voiture","avocat","fichier","femme","alpinisme",
        		"sérac","piano","Everest","vin","palais","poumon"};
        for(String word : words) {
        	queries2.add(queryFactory.create(word));
        }
       
    }


    private void testRunQueries(Collection<RelationQuery> workload,boolean batch_insertion) throws Exception{
    	JDM_RelationStore inputStore = masterStore.getInputStore();
    	Neo4J_RelationStore writeStore = masterStore.getPersistentStore();
    	writeStore.reset();
    	
        Instant t1 = Instant.now(), t2;
        System.out.println("Run queries [START]");
        
        long total_jdm_query_time = 0,
        	 total_neo4j_insert_time = 0;
        long total_insertion_nb = 0;
        
    	for(RelationQuery query : workload){
    		
    		 t1 = Instant.now();
    		 Map<Integer,ArrayList<Relation>> results = inputStore.query(query);
    		 long query_time = Duration.between(t1,Instant.now()).toMillis();
    		 total_jdm_query_time += query_time;
    		
    		 t2 = Instant.now();
    		 if(results != null) {
    			 for(Integer r_type : results.keySet()) {
    				 total_insertion_nb += results.get(r_type).size();
    				 if(batch_insertion) {
    					 writeStore.addRelations(results.get(r_type));
    				 }
    				 else {  					
    					results.get(r_type).forEach(relation -> writeStore.addRelation(relation));
    	        	} 
    			}
    			
    		 }
    		 long insert_time = Duration.between(t2,Instant.now()).toMillis();
    		 total_neo4j_insert_time += insert_time;
    		 
    		 System.out.println("\n"+query.toString()+" : ");
             System.out.println("\t"+nbResult(results)+" relations found, query_time : "+query_time+ "ms "
             										+",insert time : "+insert_time+ "ms");   		    		
    	}
    	if(batch_insertion) {
    		t1 = Instant.now();
    		writeStore.flush();
    		total_neo4j_insert_time += Duration.between(t1,Instant.now()).toMillis();
    	}
    	
    	System.out.println("\nJDM Querying [OK] in : "+total_jdm_query_time+ "ms");
        System.out.println("Neo4J : "+total_insertion_nb+" insertion  [OK] in : "+total_neo4j_insert_time+ "ms");
    }
    
   @Test
   public void testQueries() throws Exception {
//	   testRunQueries(queries,false);
//	   testRunQueries(queries,true);
       testRunQueries(queries2,true);
   }
  
    
   


    private int nbResult(Map<Integer,ArrayList<Relation>> results){
        Integer i = 0;
        if(results == null)
            return i;
        for(Integer key : results.keySet()){
            i +=results.get(key).size();
        }
        return i;
    }


  


}
