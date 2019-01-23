package Store;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TermStore {

    private PatriciaTrie<Integer> termsTrie;
    
    /** Associate to each ambigous term the list of semantic raffinement term id
     *  ex : (avocat, id=104789) -> { 157204: 71637 (justice) , .}
     *  */ 
    private HashMap<Integer,HashMap<Integer,Integer>> ambiguity;
    
    private PatriciaTrie<HashMap<Integer,Integer>> tmpAmbiguity;
    
    private int resolvedAmbiguityNb, unresolvedAmbiguityNb;
    
    private int total_term_size;

    /** Map integer id to  */
    private Map<Integer,String> termsByIds;
    

    public TermStore(){
        termsTrie = new PatriciaTrie<>();
        termsByIds = new HashMap<>();     
        tmpAmbiguity = new PatriciaTrie<>();
    }

  
    public TermStore(String filePath) throws IOException {
    	this();
    	addTerms(filePath);   	
    }
    
    public void addTerms(String filePath) throws IOException {
    	List<String> lines = Files.readAllLines(Paths.get(filePath),StandardCharsets.ISO_8859_1);
		for(String line : lines) {
			if(line != null && !line.isEmpty()) {
				String[] parts = line.split(",");	
				if(parts.length == 3) {
					int id = Integer.parseInt(parts[0]);
					addTerm(id,parts[1]);
				}	
			}
			
		}
    }
    
    public void resolveAmbiguity(){
    	if(tmpAmbiguity.isEmpty()){
    		return;
    	}
    	resolvedAmbiguityNb = 0;
    	ambiguity = new HashMap<>();
    	for(String term : tmpAmbiguity.keySet()){ 	
    		ambiguity.put(termsTrie.get(term), tmpAmbiguity.get(term));		
    		resolvedAmbiguityNb += tmpAmbiguity.get(term).size();
    	}
    	tmpAmbiguity.clear();
    }
    
//    public TermStore(Collection<String> names, Collection<Integer> ids) {
//    	this();
//    	assert(names.size() == ids.size());
//    	Iterator<String> namesIt = names.iterator();
//    	Iterator<Integer> idsIt = ids.iterator();
//    	while(namesIt.hasNext() && idsIt.hasNext()) {
//    		Integer id = idsIt.next();
//    		String name = namesIt.next();
//    		addTerm(id,name);
//    	}
//    }
    
    

    
    public void addTerm(Integer id, String name) {
    	
	  	if(! termsTrie.containsKey(name)) {
	  		 termsTrie.put(name,id);            	    
	  	     total_term_size += name.length();
	       	 termsByIds.put(id, name);

	       	 int ambiguityIdx = name.indexOf('>');
	       	 if(ambiguityIdx != -1){
	       		 String termName = name.substring(0,ambiguityIdx);  
	       		 if(! termName.isEmpty()){
	       			 
	       			 String ambigiousTerm = name.substring(ambiguityIdx+1);
		       		 int recursiveAmbiguityIdx = ambigiousTerm.indexOf('>');
		       		 if(recursiveAmbiguityIdx == -1){
		       			 try{
		       				Integer refTermId = Integer.parseInt(ambigiousTerm);
				       		tmpAmbiguity.putIfAbsent(termName, new HashMap<>());
				       		tmpAmbiguity.get(termName).put(id, refTermId);
		       			 }
		       			 catch(NumberFormatException e){
		       				 unresolvedAmbiguityNb++;		       			
		       			 }
		       			
		       		 }
		       		 else{
		       			   unresolvedAmbiguityNb++;
		       		 }
	       		 }	       		     		 
	       	 }
	  	}
    }



    
    public void resetTerms() {
        termsTrie.clear();
        termsByIds.clear();
//        mweTermsByIds.clear();
    }
    
    public int length() {
        return termsTrie.size();
    }

    
    public int getTotalSize() {
        return total_term_size;
    }

  
	public Collection<String> getTermsName() {
		return termsTrie.keySet();
	}
	
	public Collection<Integer> getTermsIds() {
		return termsByIds.keySet();
	}
	
	public Map<String, Integer> getTermIndex() {
		return termsTrie;
	}

    
    public Integer getTermId(String termName) {
        return termsTrie.get(termName);
    }
	
	public String getTermName(int termId) {
		return termsByIds.get(termId);
	}


	public int getResolvedAmbiguityNb() {
		return resolvedAmbiguityNb;
	}


	public int getUnresolvedAmbiguityNb() {
		return unresolvedAmbiguityNb;
	}
	
	
	
	


}
