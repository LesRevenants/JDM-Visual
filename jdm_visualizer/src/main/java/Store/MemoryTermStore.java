package Store;

import org.apache.commons.collections4.trie.PatriciaTrie;


import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class MemoryTermStore implements ReadTermStore, WriteTermStore{

    private PatriciaTrie<Integer> termsTrie;
    private int total_term_size, mwe_total_term_size;

    /** Map integer id to  */
    private TreeMap<Integer,String> termsByIds;

    private TreeMap<Integer,String> mweTermsByIds;


    public MemoryTermStore(){
        termsTrie = new PatriciaTrie<>();
        termsByIds = new TreeMap<>();
        mweTermsByIds = new TreeMap<>();
    }

  

    @Override
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


    @Override
    public void resetTerms() {
        termsTrie.clear();
        termsByIds.clear();
        mweTermsByIds.clear();
    }

    @Override
    public int size() {
        return total_term_size+mwe_total_term_size;
    }

    @Override
    public int length() {
        return termsTrie.size();
    }

    @Override
    public int getTermsSize() {
        return total_term_size;
    }

    @Override
    public int getTermsLength() {
        return termsByIds.size();
    }

    @Override
    public int getMweTermsSize() {
        return mwe_total_term_size;
    }

    @Override
    public int getMweTermsLentgh() {
        return mweTermsByIds.size();
    }



	@Override
	public Collection<String> getAllTermsName() {
		return termsTrie.keySet();
	}



	@Override
	public Collection<Integer> getAllTermsIds() {
		return termsTrie.values();
	}



	@Override
	public Collection<String> getTermsName() {
		return termsByIds.values();
	}



	@Override
	public Collection<Integer> getTermsIds() {
		return termsByIds.keySet();
	}



	@Override
	public Collection<String> getMweTermsURI() {
		return mweTermsByIds.values();
	}



	@Override
	public Collection<Integer> getMweTermsIds() {
		return mweTermsByIds.keySet();
	}



	@Override
	public Map<String, Integer> getTermIndex() {
		return termsTrie;
	}



	@Override
	public Integer getMweTermId(String termName) {
		return termsTrie.get(termName);
	}


    @Override
    public Integer getTermId(String termName) {
        return termsTrie.get(termName);
    }

	
	@Override
	public String getTermName(int termId) {
		return termsByIds.get(termId);
	}



	@Override
	public String getMweTermName(int termId) {
		return mweTermsByIds.get(termId);
	}



	@Override
	public boolean addTerm(Collection<Integer> ids, Collection<String> names) {
		// TODO Auto-generated method stub
		return false;
	}


}
