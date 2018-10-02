package Store;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class MemoryTermStore implements TermStore{

    private PatriciaTrie<Term> termsTrie;
    private int total_term_nb,mwe_term_nb;
    private int total_term_size, mwe_total_term_size;

    /** Map integer id to  */
    private TreeMap<Integer,Term> termsByIds;

    private TreeMap<Integer,Term> mweTermsByIds;


    public MemoryTermStore(){
        termsTrie = new PatriciaTrie<>();
        termsByIds = new TreeMap<>();
        mweTermsByIds = new TreeMap<>();
    }

    @Override
    public Collection<Term> getAllTerms() {
        return termsTrie.values();
    }

    @Override
    public Collection<Term> getTerms() {
        return termsByIds.values();
    }

    @Override
    public Map<String, Term> getTermIndex() {
        return termsTrie;
    }

    @Override
    public Collection<Term> getMweTerms() {
        return mweTermsByIds.values();
    }

    @Override
    public boolean addTerm(int id, String name) {
        boolean is_mwe = name.contains("_") || name.contains(" ") || name.contains("-");
        // must optimize research with regex or search algorithm which exploit the set of mwe word identifier characters

        Term term = termsTrie.get(name);
        if (term == null) { // term not already exist

            term = new Term(id, name, is_mwe);
            termsTrie.put(name, term);
            if (is_mwe){
                mweTermsByIds.put(id, term);
                total_term_nb = termsByIds.size();
                total_term_size += name.length();
            }
            else{
                termsByIds.put(id, term);
                mwe_term_nb = mweTermsByIds.size();
                mwe_total_term_size += name.length();
            }
            return true;
        }
        int old_id = term.getId();
        if (id != old_id) {
            term.setId(id);
            if (!is_mwe) {
                termsByIds.remove(old_id);
                termsByIds.put(id, term);

            } else {
                mweTermsByIds.remove(old_id);
                mweTermsByIds.put(id, term);

            }
            return true;
        }
        return false;
    }

    @Override
    public Term getTerm(String termName) {
        return termsTrie.get(termName);
    }

    @Override
    public Term getTerm(int termId) {
        return termsByIds.get(termId);
    }

    @Override
    public Term getMweTerm(int termId) {
        return mweTermsByIds.get(termId);
    }

    @Override
    public void reset() {
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


}
