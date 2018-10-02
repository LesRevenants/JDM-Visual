import RequeterRezo.Terme;
import Store.*;
import Configuration.MasterStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UpdateManagerTest {

    public Properties prop;
    public MasterStore masterStore;
    public List<String> entries,mweEntries;

    public List<RelationQuery> queries;

    @BeforeEach
    void setUp() throws IOException{
        prop = new Properties();
        prop.put(MasterStore.ENTRIES_KEY,"data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES.txt");
        prop.put(MasterStore.IS_UPDATE_ENTRIES_KEY,false);
        prop.put(MasterStore.MWE_ENTRIES_KEY,"data/07032018-LEXICALNET-JEUXDEMOTS-ENTRIES-MWE.txt");
        prop.put(MasterStore.IS_UPDATE_MWE_ENTRIES_KEY,false);
        prop.put(MasterStore.MAX_RELATION_IN_DB_KEY,""+1000000);

        queries=new ArrayList<>();

        queries.add(new RelationQuery("requin",null,true,true,null));
        queries.add(new RelationQuery("chat",new HashSet<String>(Arrays.asList("felin","souris","nom")),true,true,new HashSet<String>(Arrays.asList("r_isa","r_pos"))));
        queries.add(new RelationQuery("chat",new HashSet<String>(Arrays.asList("felin","souris")),true,true,null));
        queries.add(new RelationQuery("chat",null,true,true,new HashSet<String>(Arrays.asList("r_isa","r_pos"))));
        queries.add(new RelationQuery("ours",null,true,true,null));
        queries.add(new RelationQuery("marmotte",null,true,true,null));
        queries.add(new RelationQuery("ours",new HashSet<String>(Arrays.asList("felin","souris","nom","miel")),true,true,new HashSet<String>(Arrays.asList("r_isa","r_associated","r_has_part"))));
        queries.add(new RelationQuery("chat",new HashSet<String>(Arrays.asList("felin","souris","nom")),true,true,new HashSet<String>(Arrays.asList("r_isa","r_pos"))));

        Instant t1 = Instant.now();
        System.out.println("\nSetUp[START]");
        masterStore = new MasterStore(prop);
        System.out.println("SetUp [OK] in : "+Duration.between(t1,Instant.now()).toMillis() + "ms");
    }


    @Test
    public void testCachedStoreInsertion()  throws IOException{
        TermStore store = masterStore.getTermStore();
        Map<String,Term> termIndex = store.getTermIndex();
        Collection<Term> terms = store.getTerms();
        Collection<Term> mweTerms = store.getMweTerms();
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
        TermStore store = masterStore.getTermStore();
        entries = Files.readAllLines(Paths.get(prop.getProperty(MasterStore.ENTRIES_KEY)),StandardCharsets.ISO_8859_1);
        mweEntries = Files.readAllLines(Paths.get(prop.getProperty(MasterStore.MWE_ENTRIES_KEY)),StandardCharsets.ISO_8859_1);

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
        ReadRelationStore relationStore = masterStore.getJDM_RelationStore();

        Instant t1 = Instant.now();

        for(RelationQuery query : queries){
            Instant t2 = Instant.now();
            Map<String,ArrayList<Terme>> results = relationStore.query(query);
            long time = Duration.between(t2,Instant.now()).toMillis();
            System.out.println(query.toString()+" : ");
            System.out.println("\t"+nbResult(results)+" relations found, time : "+time+ "ms");
        }

        System.out.println("\nQueries run[OK] in : "+Duration.between(t1,Instant.now()).toMillis()+ "ms");
    }



    private int nbResult(Map<String,ArrayList<Terme>> results){
        Integer i = 0;
        if(results == null)
            return i;
        for(String key : results.keySet()){
            i +=results.get(key).size();
        }
        return i;
    }


    private void assertEncodedList(TermStore store, Collection<String> entries, AtomicInteger i, AtomicInteger totalDicoSize){
        for (String line : entries) {
            if (line != null && !line.isEmpty()) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    Integer id = Integer.parseInt(parts[0]);
                    String name = parts[1];

                    assertNotNull(store.getTerm(name));
//                    store.getTerm(id);
//                    store.getMweTerm(id);
                    i.incrementAndGet();
                    totalDicoSize.getAndAdd(name.length());
                }
            }
        }
    }


}
