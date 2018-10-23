package Store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.lucene.queryparser.classic.ParseException;

public class RelationTypeStore {
	
	private PatriciaTrie<Integer> ids;
	private HashMap<Integer,String> names;
	
	 
	 public RelationTypeStore(String filePath) throws IOException {
		 ids = new PatriciaTrie<>();
		 names = new HashMap<>();
		 
		 List<String> lines = Files.readAllLines(Paths.get(filePath));
		 for(String line : lines) {
			 String[] parts = line.split(",");
			 if(parts.length != 2) {
				
			 }
			 String name= parts[0];
			 Integer id = Integer.parseInt(parts[1]);		
			 ids.put(name, id);
			 names.put(id,name);
		 }
	 }
	 
	 	
	public Integer getId(String r_name) {
		return ids.get(r_name);
	}

	
	public String getName(Integer id) {
		return names.get(id);
	}

	
	public Collection<Integer> getIds() {
		return ids.values();
	}

	
	public Collection<String> getNames() {
		return names.values();
	}

	public static void buildRelationEntries(String relationNamesPath, String relationIdsPath, String finalRelationPath) throws IOException {
		
		ArrayList<String> relation_names = new ArrayList<>(
				Files.readAllLines(Paths.get(relationNamesPath)));
					
		ArrayList<Integer> relation_ids = new ArrayList<>(
				Files.readAllLines(Paths.get(relationIdsPath)).stream()
				.map(line -> Integer.parseInt(line))
				.collect(Collectors.toList()));
								 
		assert(relation_ids.size() == relation_names.size());
		
	    File fout = new File(finalRelationPath);
	    fout.createNewFile();
		FileOutputStream fos = new FileOutputStream(fout,false);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for(int i=0;i<relation_ids.size();i++) {
			bw.write(relation_names.get(i)+","+relation_ids.get(i));
			bw.newLine();
		}
		bw.close();
	    fos.close();
	}
}
