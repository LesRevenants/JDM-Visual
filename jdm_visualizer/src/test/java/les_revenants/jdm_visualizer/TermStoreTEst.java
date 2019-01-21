package les_revenants.jdm_visualizer;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.BeforeClass;
import org.junit.Test;

import Store.TermStore;

public class TermStoreTEst {

	public static TermStore termStore;
	public static List<String> allEntries;
	
	@BeforeClass
	public static void setup() throws IOException {		
	     allEntries = Files.readAllLines(Paths.get("data/terms.txt"),StandardCharsets.UTF_8);
	     termStore = new TermStore();
	     for(String line : allEntries) {
	    	 if (line != null && !line.isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
        	    	 termStore.addTerm(termStore.length(),parts[1]);
                }
	    	 }    	
	     }
	     System.out.println(termStore.length());
	}
	
//	@Test
//	public void testEntriesBelonging() {
//		
//		for(String line : allEntries) {
//			 if (line != null && !line.isEmpty()) {
//                String[] parts = line.split(",");
//                if (parts.length == 2) {
//                	assertNotNull(termStore.getTermId(parts[1]));
//                }
//			 }	              			
//		}
//	}
	
    @Test
    public void testTermMapping() {
 	   String[] words = { 
        		"chien","tortue","médicament",
        		"voiture","avocat","fichier","femme","alpinisme",
       		"sérac","piano","Everest","vin","palais","poumon"};
 	   	 
 	    for(String word : words) {	    	
 	    	assertNotNull(termStore.getTermId(word));
 	    }     
    }
    
    @Test
    public void testIndexIntegrity()  throws IOException{

        Map<String,Integer> termIndex = termStore.getTermIndex();
        Collection<String> terms = termStore.getTermsName();
//        Collection<String> mweTerms = termStore.getMweTermsURI();
//        assert(termIndex.size() == terms.size() + mweTerms.size());
        assert(termStore.getTermsLength() == terms.size());
//        assert(termStore.getMweTermsLentgh() == mweTerms.size());
        assert(termStore.length() == termIndex.size());
        System.out.println("\tlength="+termStore.length()+", size="+termStore.getTotalSize());
//        System.out.println("\tTerms.length()="+termStore.getTermsLength()+", MweTerms.length()="+termStore.getMweTermsLentgh());
    }


    @Test
    public void testSearchTerms() throws IOException {      
        Instant t1 = Instant.now();
        System.out.println("\nTerm search [START]");

        AtomicInteger nb_searched_word = new AtomicInteger(0);
        AtomicInteger totalDicoSize= new AtomicInteger(0);
        assertEncodedList(termStore,allEntries,nb_searched_word,totalDicoSize);

        System.out.println("Search into store[OK] in : "+Duration.between(t1,Instant.now()).toMillis() + "ms");
        System.out.println("\tword_nb : "+nb_searched_word+", total_size : "+totalDicoSize);
    }
    
    private void assertEncodedList(TermStore store, Collection<String> entries, AtomicInteger i, AtomicInteger totalDicoSize){
        for (String line : entries) {
            if (line != null && !line.isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    Integer id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    assertNotNull(store.getTermId(name));
                    store.getTermName(id);
//                    store.getMweTermName(id);
                    i.incrementAndGet();
                    totalDicoSize.getAndAdd(name.length());
                }
            }
        }
    }

}
