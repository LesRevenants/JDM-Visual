package Store;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.neo4j.cypher.internal.frontend.v2_3.ast.In;

import core.Ambiguity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TermStore {

	/**
	 * 
	 */
    private PatriciaTrie<Integer> termsTrie;
    
    /** Map integer id to  */
    private Map<Integer,String> termsByIds;
    
    /** */
    private HashSet<Integer> newTerms;
    

    
    /** Associate to each ambigous term the list of semantic raffinement term id
     *  ex : (avocat, id=104789) -> { 157204: 71637 (justice) , .}
     *  */ 
//    private HashMap<Integer,HashMap<Integer,Integer>> ambiguity;
    private HashMap<Integer,Ambiguity> ambiguityRefs;
    

    private PatriciaTrie<HashMap<Integer,LinkedList<Integer>>> tmpAmbiguity;
    
    /**
     * 
     */
    private HashMap<Integer,ArrayList<Integer>> conflicts;
    
   
    
    private int resolvedAmbiguityNb, unresolvedAmbiguityNb;
    
    private int total_term_size;


    

    public TermStore(){
        termsTrie = new PatriciaTrie<>();
        termsByIds = new HashMap<>();     
        tmpAmbiguity = new PatriciaTrie<>();
        conflicts = new HashMap<>();
        ambiguityRefs = new HashMap<>();
        newTerms = new HashSet<>();
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
//    	ambiguity = new HashMap<>(); 	
    	for(String rootTerm : tmpAmbiguity.keySet()){ 	
    		Integer rootTermId = termsTrie.get(rootTerm);
    		if(rootTermId != null){
    			HashMap<Integer,LinkedList<Integer>> termIdToRaffinement = tmpAmbiguity.get(rootTerm);
        		for(Integer termId : termIdToRaffinement.keySet()){
        			Ambiguity amb = new Ambiguity(termId, rootTermId, termIdToRaffinement.get(termId));
        			ambiguityRefs.put(termId,amb);
        		}      		
        		resolvedAmbiguityNb += tmpAmbiguity.get(rootTerm).size();
    		}
    		
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
    	
    	Integer oldId = termsTrie.get(name);
	  	if(oldId == null) {
	  		
	  		 termsTrie.put(name,id);            	    
	  	     total_term_size += name.length();
	       	 termsByIds.put(id, name);
	       	 
	       	 String[] parts = name.split(">");       	 
	       	 if(parts.length > 1){ // ambiguity found 
	       		 
	       		 String rootTerm = parts[0];
	       		 if(! rootTerm.isEmpty()){
	       			 LinkedList<Integer> refTermIds = new LinkedList<>();
	       			 for(int i=1;i<parts.length;i++){
	       				 
	       				String ambigiousTerm = parts[i];
	       				Integer refTermId;
		       			 try{
		       				refTermId = Integer.parseInt(ambigiousTerm);			       		
			       		 } catch(NumberFormatException e){ 
			       			 refTermId = termsTrie.get(ambigiousTerm);
			       		}
		       			if(refTermId != null) {
		       				tmpAmbiguity.putIfAbsent(rootTerm, new HashMap<>());
				       		refTermIds.add(refTermId);		
		       			}
	       			}
	       			if(tmpAmbiguity.containsKey(rootTerm)) {
	       				tmpAmbiguity.get(rootTerm).put(id, refTermIds);
	       			}
	       		 }
	       	 }
	  	}
	       	
	  	else {
	  		conflicts.putIfAbsent(oldId, new ArrayList<>(2));
	  		conflicts.get(oldId).add(id);
	  		conflicts.putIfAbsent(id, new ArrayList<>(2));
	  		conflicts.get(id).add(oldId);
	  	}
    }

//    if(parts.length == 2){
//  		 String rootTerm = parts[0];
//  		 if(! rootTerm.isEmpty()){
//  			 
//  			 LinkedList<Integer> refTermIds = new LinkedList<>();
//  			 String ambigiousTerm = parts[1];
////      		 int recursiveAmbiguityIdx = ambigiousTerm.indexOf('>');
////      		 if(recursiveAmbiguityIdx == -1){
//  			 try{
//  				Integer refTermId = Integer.parseInt(ambigiousTerm);
//	       		tmpAmbiguity.putIfAbsent(rootTerm, new HashMap<>());
//	       		refTermIds.add(refTermId);
//	       		tmpAmbiguity.get(rootTerm).put(id, refTermIds);
////		       		refTerms.add(refTermId);
////		       		Ambiguity amb = new Ambiguity(id, leftId, rightIds)
//  			 }
//  			 catch(NumberFormatException e){
//  				 unresolvedAmbiguityNb++;		       			
//  			 }
//      			
////      		 }
////      		 else{
////      			   unresolvedAmbiguityNb++;
////      		 }
//  		 }	  
//  	 }
//  	 else if(parts.length > 2){
//  		 
//  	 }
//  	 
//
//  	 int ambiguityIdx = name.indexOf('>');
//  	 if(ambiguityIdx != -1){
//  		 String rootTerm = name.substring(0,ambiguityIdx);  
//  		
//  		 if(! rootTerm.isEmpty()){
//  			 
//  			 LinkedList<Integer> refTermIds = new LinkedList<>();
//  			 
//  			 String ambigiousTerm = name.substring(ambiguityIdx+1);
//      		 int recursiveAmbiguityIdx = ambigiousTerm.indexOf('>');
//      		 if(recursiveAmbiguityIdx == -1){
//      			 try{
//      				Integer refTermId = Integer.parseInt(ambigiousTerm);
//		       		tmpAmbiguity.putIfAbsent(rootTerm, new HashMap<>());
//		       		refTermIds.add(refTermId);
//		       		tmpAmbiguity.get(rootTerm).put(id, refTermIds);
////		       		refTerms.add(refTermId);
////		       		Ambiguity amb = new Ambiguity(id, leftId, rightIds)
//      			 }
//      			 catch(NumberFormatException e){
//      				 unresolvedAmbiguityNb++;		       			
//      			 }
//      			
//      		 }
//      		 else{
//      			   unresolvedAmbiguityNb++;
//      		 }
//  		 }	       		     		 
//  	 }
//	}

    
    public HashMap<Integer, ArrayList<Integer>> getConflicts() {
		return conflicts;
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
	
	public Ambiguity getAmbiguity(Integer termId){
		return ambiguityRefs.get(termId);
	}
	


}
