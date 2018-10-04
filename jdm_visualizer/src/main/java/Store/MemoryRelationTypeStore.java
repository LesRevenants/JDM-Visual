package Store;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections4.trie.PatriciaTrie;

public class MemoryRelationTypeStore implements RelationTypeStore{
	
	private PatriciaTrie<Integer> ids;
	private ArrayList<String> names;
	
	 public MemoryRelationTypeStore(Collection<String> relation_types) {
		ids = new PatriciaTrie<>();
		names = new ArrayList<>(relation_types.size());
		int i=0;
		for(String r_type : relation_types) {
			ids.put(r_type, i++);
			names.add(r_type);
		}
	}
	 
	 

	@Override
	public Integer getId(String r_name) {
		return ids.get(r_name);
	}

	@Override
	public String getName(Integer id) {
		return names.get(id);
	}


	@Override
	public Collection<Integer> getIds() {
		return ids.values();
	}

	
	@Override
	public Collection<String> getNames() {
		return names;
	}

}
