package les_revenants.jdm_visualizer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.junit.BeforeClass;
import org.junit.Test;

import Store.TermStore;
import Utils.JDM_EntriesBuilder;

public class EntriesBuilderTest {
	
	public static String entries,output,outputCsv;
	
	@BeforeClass
	public static void setup() throws IOException {		
		entries = "data/10182018-LEXICALNET-JEUXDEMOTS-ENTRIES.txt";
		output = "data/all_terms.txt";
		outputCsv = "data/all_terms.csv";
	}
	
	@Test
	public void testBuild() throws IOException {
//		  JDM_EntriesBuilder.buildUniqueEntries(entries,output); 
		  JDM_EntriesBuilder.writeToCSV(output,outputCsv);
//		  List<String> mweList = Files.readAllLines(Paths.get(mweEntries),StandardCharsets.ISO_8859_1);
//		  System.out.println("Files load [OK]");

//		  
//		  TermStore termStore = new TermStore(output);
//		  System.out.println("TermStore building OK");
//
//		  
//		  for(String line : mweList) {
//			  if(! line.isEmpty()) {
//				  System.out.println(line);
//				  String[] parts = line.split(";");
//				  int id = Integer.parseInt(parts[0]);
//				  String name = parts[1];
//				  if(JDM_EntriesBuilder.checkName(name) && parts.length == 2) {
//					  assert(termStore.getTermId(name) == id);
//					  assert(termStore.getTermName(id).equals(name));
//				  }
//			  }
//			
//
//		  }
//		  
//		  for(String line : entriesList) {
//			  if(! line.isEmpty()) {
//				  System.out.println(line);
//				  String[] parts = line.split(";");
//				  Integer id = Integer.parseInt(parts[0]);
//				  String name = parts[1];
//				  if(JDM_EntriesBuilder.checkName(name) && parts.length == 2) {
////					  System.out.println(line);
//					  assertEquals(termStore.getTermId(name),id);
//					  assertEquals(termStore.getTermName(id),name);
//				  }
//			  }
//			 
//		  }
		  

	}

}
