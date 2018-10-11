package les_revenants.jdm_visualizer;

import Store.*;
import configuration.MasterStore;
import core.Relation;
import core.RelationQuery;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class UpdateManagerTest {

    public static Properties prop;
    public static MasterStore masterStore;
    public static List<String> entries,mweEntries;

    public static List<RelationQuery> queries;

    @BeforeAll
    static void setUp() throws IOException, NumberFormatException, SQLException{
    	
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

        entries = Files.readAllLines(Paths.get("data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES.txt"),StandardCharsets.ISO_8859_1);
        mweEntries = Files.readAllLines(Paths.get("data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES-MWE.txt"),StandardCharsets.ISO_8859_1);

        Instant t1 = Instant.now();
        System.out.println("\nSetUp[START]");
        masterStore = new MasterStore("data/config.json");
        System.out.println("SetUp [OK] in : "+Duration.between(t1,Instant.now()).toMillis() + "ms");
    }


    @Test
    public void testCachedStoreInsertion()  throws IOException{
        ReadTermStore store = masterStore.getTermStore();
        Map<String,Integer> termIndex = store.getTermIndex();
        Collection<String> terms = store.getTermsName();
        Collection<String> mweTerms = store.getMweTermsURI();
        assert(termIndex.size() == terms.size() + mweTerms.size());
        assert(store.getTermsLength() == terms.size());
        assert(store.getMweTermsLentgh() == mweTerms.size());
        assert(store.length() == termIndex.size());
        System.out.println("\tlength="+store.length()+", size="+store.size());
        System.out.println("\tTerms.length()="+store.getTermsLength()+", MweTerms.length()="+store.getMweTermsLentgh());
        System.out.println("\tTerms.size()="+store.getTermsSize()+", MweTerms.size()="+store.getMweTermsSize());
    }

    @Test
    public void testSearchTerms() throws IOException {
        MemoryTermStore store = masterStore.getTermStore();


        Instant t1 = Instant.now();
        System.out.println("\nTerm search [START]");

        AtomicInteger nb_searched_word = new AtomicInteger(0);
        AtomicInteger totalDicoSize= new AtomicInteger(0);
        assertEncodedList(store,entries,nb_searched_word,totalDicoSize);
        assertEncodedList(store,mweEntries,nb_searched_word,totalDicoSize);

        System.out.println("Search into store[OK] in : "+Duration.between(t1,Instant.now()).toMillis() + "ms");
        System.out.println("\tword_nb : "+nb_searched_word+", total_size : "+totalDicoSize);
    }

    @Test
    public void testRunQueries() throws Exception{
        System.out.println("\nQueries run[START]\n");
        ReadRelationStore relationStore = masterStore.getInputStore();
        RelationTypeStore relationTypeStore = masterStore.getRelationTypeStore();

        Instant t1 = Instant.now();

        for(RelationQuery query : queries){
            Instant t2 = Instant.now();
            Map<String,ArrayList<Relation>> results = relationStore.query(query);
            long time = Duration.between(t2,Instant.now()).toMillis();
            System.out.println(query.toString()+" : ");
            System.out.println("\t"+nbResult(results)+" relations found, time : "+time+ "ms");
        }
        System.out.println("\nQueries run[OK] in : "+Duration.between(t1,Instant.now()).toMillis()+ "ms");
        
        for(RelationQuery query : queries){
        	if(query.getRelations_searched() != null) {
        		for(String r_name : query.getRelations_searched()) {
            		assertNotNull(relationTypeStore.getId(r_name));
            	}
        	}
        	
        }
    }
    

    
    @Test
    public void testRelationTypeStore() {
    	RelationTypeStore store = masterStore.getRelationTypeStore();
    	Collection<Integer> ids = store.getIds();
    	Collection<String> names = store.getNames();
    	assert(ids.size() == 143);
    	assert(names.size() == ids.size());
       	ArrayList<String> namesList = new ArrayList<>(names);
       	for (int i = 0; i < namesList.size()-1; i++) {
			assert(store.getId(namesList.get(i)) < store.getId(namesList.get(i+1)));
		}
    }
    
    @Test
    public void testNeo4_Setup() throws Exception {
    	
    	ReadRelationStore inputStore = masterStore.getInputStore();
    	WriteRelationStore writeStore = masterStore.getPersistentStore();
    	
        Instant t1 = Instant.now();
        System.out.println("Neo4J insertion [START]");
        
    	for(RelationQuery query : queries){
    		 Instant t2 = Instant.now();
    		 Map<String,ArrayList<Relation>> results = inputStore.query(query);
    		 results.forEach((k,v)-> {
    			 v.forEach(relation -> writeStore.addRelation(relation));
    		 });
    		 long time = Duration.between(t2,Instant.now()).toMillis();
    		 System.out.println("\t"+nbResult(results)+" relations inserted, time : "+time+ "ms");
    	}
        System.out.println("\n\"Neo4Jinsertion  [OK] in : "+Duration.between(t1,Instant.now()).toMillis()+ "ms");

    	
//    	List<String> lines = new ArrayList<>();
//    	lines.add("id,name");
//    	
//    	for(String term : terms ) {
//    		lines.add(termStore.getTermId(term)+","+term);
//    	}
//    	Files.write(Paths.get("load.csv"),lines);
//    	System.out.println("terms writed into CSV [OK]");
     	System.out.println("TestNeo4_Setup[OK]");

    }



    private int nbResult(Map<String,ArrayList<Relation>> results){
        Integer i = 0;
        if(results == null)
            return i;
        for(String key : results.keySet()){
            i +=results.get(key).size();
        }
        return i;
    }


    private void assertEncodedList(ReadTermStore store, Collection<String> entries, AtomicInteger i, AtomicInteger totalDicoSize){
        for (String line : entries) {
            if (line != null && !line.isEmpty()) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    Integer id = Integer.parseInt(parts[0]);
                    String name = parts[1];
//                    assertNotNull(store.getTermId(name));
                    store.getTermName(id);
                    store.getMweTermName(id);
                    i.incrementAndGet();
                    totalDicoSize.getAndAdd(name.length());
                }
            }
        }
    }


}
