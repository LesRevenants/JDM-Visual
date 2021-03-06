package les_revenants.jdm_visualizer;

import Store.*;
import configuration.MasterStore;
import core.Relation;
import core.FilteredQuery;


import static org.junit.Assert.*;
import core.RelationQueryFactory;


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

public class UpdateManagerTest {

    public static Properties prop;
    public static MasterStore masterStore;
    public static TermStore termStore;
    public static RelationTypeStore relationTypeStore;
//    public static List<String> allEntries;

    public static List<FilteredQuery> queries,queries2;
    
    @BeforeClass
    public static void setUp() throws IOException, NumberFormatException, SQLException{
    	
    	queries=new ArrayList<>();
        queries2=new ArrayList<>();     
//        entries = Files.readAllLines(Paths.get("data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES.txt"),StandardCharsets.ISO_8859_1);
//        mweEntries = Files.readAllLines(Paths.get("data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES-MWE.txt"),StandardCharsets.ISO_8859_1);
//        allEntries = Files.readAllLines(Paths.get("data/terms.txt"),StandardCharsets.UTF_8);


        Instant t1 = Instant.now();
        System.out.println("\nSetUp[START]");
        masterStore = new MasterStore("data/config.json");
        termStore = masterStore.getTermStore();
        relationTypeStore = masterStore.getRelationTypeStore();
        System.out.println("SetUp [OK] in : "+Duration.between(t1,Instant.now()).toMillis() + "ms");
        

        RelationQueryFactory queryFactory = new RelationQueryFactory(termStore, masterStore.getRelationTypeStore());
        
        queries.add(queryFactory.create("requin"));
//        queries.add(queryFactory.create("chat",Arrays.asList("félin","souris","nom"),Arrays.asList("r_isa","r_pos")));
//        queries.add(queryFactory.create("chat",Arrays.asList("félin","souris","nom"),null));
//        queries.add(queryFactory.create("ours"));
//        queries.add(queryFactory.create("ours",Arrays.asList("félin","miel","animal","brun","griffe"),Arrays.asList("r_isa","r_associated","r_carac","r_has_part")));

        String[] words = { 
        		"piano"
        		,"chien","tortue","médicament",
        		"voiture","avocat","fichier","femme","alpinisme",
        		"sérac","piano","Everest","vin","palais","poumon",
        		"langoustine","militaire","histoire","médecin","biologie",
        		"pétrole","colbat","glacier","théâtre","masque","peinture",
        		"liberalisme","anarchisme","communisme","facisme","socialisme",
        		"compotée","cheval","mandragore","factorisation","graphe","sémantique",
        		"dictionnaire","amplificateur","astre","LSD","alcool","buffer-overflow"
        };
        
        String[] r_types = {"r_isa","r_pos","r_associated","r_carac","r_has_part"};
        String[] y_terms = {"félin","souris","nom","piolet","encre","plume","chiot","bouteille",
        					"table","ficelle","violon","poumon"};
        
        for(String word : words) {
        	for(String type : r_types) {
        		for(String term : y_terms) {
        			FilteredQuery q = queryFactory.create(word,Arrays.asList(term),Arrays.asList(type));
        			if(q != null) {
            			queries2.add(q);
        			}
        		}       	     	
        	}
        }
//        List<FilteredQuery> queriesCopy = new ArrayList<>(queries2);
//        queries2.addAll(queriesCopy);
       
    }


    private void testRunQueries(Collection<FilteredQuery> workload) throws Exception{
    	JDM_RelationStore inputStore = masterStore.getInputStore();
    	Neo4J_RelationStore writeStore = masterStore.getPersistentStore();
    	writeStore.reset();
    	
        System.out.println("Run queries [START]");
        Instant t1 = Instant.now();
        
        long total_jdm_query_time = 0,
        	 total_neo4j_insert_time = 0,
        	 total_neo4j_querying_time = 0,
        	 insert_time = 0, query_time = 0;
        
    	for(FilteredQuery query : workload){
    		
    		t1 = Instant.now();
    		Map<Integer,ArrayList<Relation>> results = masterStore.query(query);
    		query_time = Duration.between(t1,Instant.now()).toMillis();
    		if(results != null && ! results.isEmpty()) {
       		    System.out.println(query+" : "+nbResult(results)+" relations found, query_time : "+query_time+ "ms ");  
    		}
    		
//    		
//    		 t1 = Instant.now();
//    		 Map<Integer,ArrayList<Relation>> results = inputStore.query(query);
//    		 query_time = Duration.between(t1,Instant.now()).toMillis();
//    		 total_jdm_query_time += query_time;
//    		
//    		 t1 = Instant.now();
//    		 if(results != null) {
//    			 for(Integer r_type : results.keySet()) {
//    				 writeStore.insert(results.get(r_type));
//    			 }   			
//    		 }
//    		 insert_time = Duration.between(t1,Instant.now()).toMillis();
//    		 total_neo4j_insert_time += insert_time;
//	   		  
//    		 System.out.println(query+" : JDM : "+nbResult(results)+" relations found, query_time : "+query_time+ "ms ");   
//            
    	}
    	
//    	t1 = Instant.now();
//    	writeStore.flush();
//    	insert_time = Duration.between(t1,Instant.now()).toMillis();
//		total_neo4j_insert_time += insert_time;
//		System.out.println();
//		
//    	for(RelationQuery query : workload){
//
//       	 	t1 = Instant.now();  	 
//   		 	Map<Integer,ArrayList<Relation>> results2 = writeStore.query(query);	
//      		long query_time2 = Duration.between(t1,Instant.now()).toMillis();
//      		total_neo4j_querying_time += query_time2;
//      		 
//      		System.out.println(query+" : Neo4J : "+nbResult(results2)+" relations found, query_time : "+query_time2+ "ms");   
//    	}
//    	  	
//    	System.out.println("\nJDM Querying [OK] in : "+total_jdm_query_time+ "ms");
//        System.out.println("Neo4J Insertion  [OK] in : "+total_neo4j_insert_time+ "ms");
//    	System.out.println("Neo4j Querying [OK] in : "+total_neo4j_querying_time+ "ms");

    }
    
//   @Test
//   public void testQueries() throws Exception {
//	   testRunQueries(queries);
////	   testRunQueries(queries,true);
////       testRunQueries(queries2);
//   }
  
    
   @Test
   public void testJSON() throws Exception{
	    System.out.println("TEST_JSON");
	    for(FilteredQuery q : queries) {
	    	String q1JSON = q.asJSON(termStore, relationTypeStore,"grouped");
//			System.out.println(q1JSON);			
			String q1ResultsJSON = masterStore.query(q1JSON);
			System.out.println(q1ResultsJSON);
	    }
//		FilteredQuery q1 = queries.get(1);
		
   }

    public static int nbResult(Map<Integer,ArrayList<Relation>> results){
        if(results == null)
            return 0;
        int i = 0;
        for(Integer key : results.keySet()){
            i +=results.get(key).size();
        }
        return i;
    }


  


}
