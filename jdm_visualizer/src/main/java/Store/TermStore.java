package Store;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TermStore {

    private PatriciaTrie<Integer> termsTrie;
    private int total_term_size, mwe_total_term_size;

    /** Map integer id to  */
    private Map<Integer,String> termsByIds;
    

    private Map<Integer,String> mweTermsByIds;


    public TermStore(){
        termsTrie = new PatriciaTrie<>();
        termsByIds = new HashMap<>();
        mweTermsByIds = new HashMap<>();
    }

  
    public TermStore(String filePath) throws IOException {
    	this();
    	List<String> lines = Files.readAllLines(Paths.get(filePath),StandardCharsets.UTF_8);
		for(String line : lines) {
			String[] parts = line.split(",");
			int id = Integer.parseInt(parts[0]);
			if(parts.length == 2) {
				addTerm(id,parts[1]);
			}	
		}
    }
    
    public TermStore(Collection<String> names, Collection<Integer> ids) {
    	this();
    	assert(names.size() == ids.size());
    	Iterator<String> namesIt = names.iterator();
    	Iterator<Integer> idsIt = ids.iterator();
    	while(namesIt.hasNext() && idsIt.hasNext()) {
    		Integer id = idsIt.next();
    		String name = namesIt.next();
    		addTerm(id,name);
    	}
    }
    
    

    
    public void addTerm(int id, String name) {
        boolean is_mwe = name.contains("_") || name.contains(" ") || name.contains("-");
        // must optimize research with regex or search algorithm which exploit the set of mwe word identifier characters

        Integer termId = termsTrie.get(name);
        if (termId == null) { // term not already exist

            termsTrie.put(name,id);
            if (is_mwe){
                mweTermsByIds.put(id,name);
                total_term_size += name.length();
            }
            else{
                termsByIds.put(id, name);
                mwe_total_term_size += name.length();
            }
            return;
        }
        if (id != termId) {
            if (!is_mwe) {
                termsByIds.remove(termId);
                termsByIds.put(id, name);

            } else {
                mweTermsByIds.remove(termId);
                mweTermsByIds.put(id, name);

            }         
        }
    }


    
    public void resetTerms() {
        termsTrie.clear();
        termsByIds.clear();
        mweTermsByIds.clear();
    }

    
    public int size() {
        return total_term_size+mwe_total_term_size;
    }

    
    public int length() {
        return termsTrie.size();
    }

    
    public int getTermsSize() {
        return total_term_size;
    }

    
    public int getTermsLength() {
        return termsByIds.size();
    }

    
    public int getMweTermsSize() {
        return mwe_total_term_size;
    }

    
    public int getMweTermsLentgh() {
        return mweTermsByIds.size();
    }



	
	public Collection<String> getAllTermsName() {
		return termsTrie.keySet();
	}



	
	public Collection<Integer> getAllTermsIds() {
		return termsTrie.values();
	}



	
	public Collection<String> getTermsName() {
		return termsByIds.values();
	}



	
	public Collection<Integer> getTermsIds() {
		return termsByIds.keySet();
	}



	
	public Collection<String> getMweTermsURI() {
		return mweTermsByIds.values();
	}



	
	public Collection<Integer> getMweTermsIds() {
		return mweTermsByIds.keySet();
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

	public String getMweTermName(int termId) {
		return mweTermsByIds.get(termId);
	}


	public boolean addTerm(Collection<Integer> ids, Collection<String> names) {
		// TODO Auto-generated method stub
		return false;
	}


}
