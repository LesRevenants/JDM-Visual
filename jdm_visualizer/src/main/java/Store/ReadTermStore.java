package Store;

import java.util.Collection;
import java.util.Map;

public interface ReadTermStore extends TermStore{
	
	 public Collection<String> getAllTermsName();  
	    public Collection<Integer> getAllTermsIds();

	    public Collection<String> getTermsName();
	    public Collection<Integer> getTermsIds();

	    public Collection<String> getMweTermsURI();   
	    public Collection<Integer> getMweTermsIds();

	    public Map<String, Integer> getTermIndex();

	    public Integer getTermId(String termName);
	    public String getTermName(int termId);
	    
	    public String getMweTermName(int termId);
	    public Integer getMweTermId(String termName);
	  
	    public int size();

	    public int length();

	    public int getTermsSize();

	    public int getTermsLength();

	    public int getMweTermsSize();

	    public int getMweTermsLentgh();
}
