package Store;

import java.util.Collection;

public interface WriteTermStore {
	
	public void addTerm(int id, String name) throws Exception;
	
	public boolean addTerm(Collection<Integer> ids, Collection<String> names);
	
	public void resetTerms();
	

}
