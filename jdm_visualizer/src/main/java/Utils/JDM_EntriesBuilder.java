package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.trie.PatriciaTrie;

import Store.RelationTypeStore;

public class JDM_EntriesBuilder {
	
	
	public static  final String[] filters = {"\'","&","\"","$","+","-",":",".","!","(","*","/","\\"};
	public static  final HashSet<String> forbidden_set = new HashSet<String>(Arrays.asList(filters));
    
	public static void buildUniqueEntries(String entriesPath, String mweEntriesPath, String finalEntriesPath) throws IOException {	
		
	    List<String> entries = Files.readAllLines(Paths.get(entriesPath),StandardCharsets.ISO_8859_1);
	    List<String> mwe_entries = Files.readAllLines(Paths.get(mweEntriesPath),StandardCharsets.ISO_8859_1);
	    PatriciaTrie<Integer> allEntries = new PatriciaTrie<>();
	    addEncodedTerms(entries, allEntries);
	    addEncodedTerms(mwe_entries, allEntries);
	    
	    File fout = new File(finalEntriesPath);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
	    for(String key : allEntries.keySet()) {
	    	bw.write(allEntries.get(key)+","+key);
	    	bw.newLine();
	    }	    
	    bw.close();
	    fos.close();
	}
	
		
	  private static void addEncodedTerms(Collection<String> termList,PatriciaTrie<Integer> allEntries ){
		  for (String line : termList) {
		      if (line != null && !line.isEmpty()) {
		      	
		          String[] parts = line.split(";");
		          if (parts.length == 2) {
		              Integer id = Integer.parseInt(parts[0]);
		              String name = parts[1];
		              if(checkName(name)) {
		                  allEntries.put(name, id);
		              }
		          }
		      }
		  }
	}

	private static boolean checkName(String name) {   	
		if(forbidden_set.contains(name.substring(0, 1))) {
			return false;
		}  	
		try {
			Integer.parseInt(name); 	
			return false;
		}
		catch (NumberFormatException ex) {
	      return true;
	    }
	}

}
