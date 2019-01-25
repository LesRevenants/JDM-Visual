package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JDM_EntriesBuilder {
	
	
	public static  final String[] filters = {"\'","&","\"",",","$","+","-",":",".","!","(",")","*","/","\\","%"};
	public static  final HashSet<String> forbidden_set = new HashSet<String>(Arrays.asList(filters));
    
	public static void buildUniqueEntries(String entriesPath, String finalEntriesPath) throws IOException {	
		
	    List<String> entries = Files.readAllLines(Paths.get(entriesPath),StandardCharsets.ISO_8859_1);
//	    List<String> mwe_entries = Files.readAllLines(Paths.get(mweEntriesPath),StandardCharsets.ISO_8859_1);
	    Map<Integer,String> allEntries = new TreeMap<>();
	    
	    addEncodedTerms(entries, allEntries);
//	    addEncodedTerms(mwe_entries, allEntries);
	    
	    File fout = new File(finalEntriesPath);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
	    for(Integer key : allEntries.keySet()) {
	    	bw.write(key+";"+allEntries.get(key));
	    	bw.newLine();
	    }	    
	    bw.close();
	    fos.close();
	}
	
	
	public static void writeToCSV(String finalEntriesPath, String csvFile) throws IOException {
	    List<String> entries = Files.readAllLines(Paths.get(finalEntriesPath));
	    File fout = new File(csvFile);
	  	FileOutputStream fos = new FileOutputStream(fout);
	  	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos,StandardCharsets.ISO_8859_1));
	  	
	  	bw.write("id,name");
	  	bw.newLine();
	  	for(String line : entries) {
	  		String[] parts = line.split(";");
	  		Integer id = Integer.parseInt(parts[0]);
            String name = parts[1];
	  		bw.write(id+","+name);
	  		bw.newLine();
	  	}
	  	
	    bw.close();
	    fos.close();

	}
	
		
	  private static void addEncodedTerms(Collection<String> termList,Map<Integer,String> allEntries ){
		  for (String line : termList) {
		      if (line != null && !line.isEmpty()) {
		          String[] parts = line.split(";");
		          if (parts.length == 2) {
		              Integer id = Integer.parseInt(parts[0]);
		              String name = parts[1];
		              if(checkName(name)) {
		                  allEntries.put(id,name);
		              }
		          }
		      }
		  }
	}

	public static boolean checkName(String name) {   	
		if(name.isEmpty()) {
			return false;
		}
		if(forbidden_set.contains(name.substring(0, 1))) {
			return false;
		}  	
		if(forbidden_set.contains(name)) {
			return false;
		}
		return true;
	}

}
