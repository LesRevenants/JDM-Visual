package Store;

import java.util.Collection;

public interface RelationTypeStore {
	
	public Integer getId(String r_name);
	
	public String getName(Integer id);
	
	public Collection<Integer> getIds();

	public Collection<String> getNames();


}
