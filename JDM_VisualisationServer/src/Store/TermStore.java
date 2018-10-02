package Store;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public interface TermStore {

    public Collection<Term> getAllTerms();

    public Collection<Term> getTerms();

    public Collection<Term> getMweTerms();

    public Map<String, Term> getTermIndex();

    public boolean addTerm(int id, String name) throws OutOfMemoryError;

    public Term getTerm(String termName);

    public Term getTerm(int termId);

    public Term getMweTerm(int termId);


    public void reset();

    public int size();

    public int length();

    public int getTermsSize();

    public int getTermsLength();

    public int getMweTermsSize();

    public int getMweTermsLentgh();

}
