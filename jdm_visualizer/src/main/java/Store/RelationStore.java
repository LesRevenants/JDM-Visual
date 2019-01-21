package Store;

import java.util.ArrayList;
import java.util.Map;

import core.FilteredQuery;
import core.Relation;
import core.TreeQuery;

public interface RelationStore {

	public Map<Integer, ArrayList<Relation>> query(FilteredQuery query) throws Exception;
	
	public Map<Integer, ArrayList<Relation>> query(TreeQuery query)throws Exception;
}
