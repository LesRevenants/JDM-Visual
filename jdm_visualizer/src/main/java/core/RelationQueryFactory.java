package core;

import java.util.HashSet;
import java.util.List;
import Store.TermStore;
import Store.RelationTypeStore;

public class RelationQueryFactory {

	private TermStore termStore;
	private RelationTypeStore relationTypeStore;
	
	public RelationQueryFactory(TermStore termStore, RelationTypeStore relationTypeStore) {
		super();
		this.termStore = termStore;
		this.relationTypeStore = relationTypeStore;
	}
	
	public FilteredQuery create(String x_word) {
		Integer x_id = termStore.getTermId(x_word);
		if(x_id == null) {
			return null;
		}
		FilteredQuery query = new FilteredQuery(x_id, null, true,true, null);
		return query;
	}
	
	public FilteredQuery create(String x_word,List<String> term_searched, boolean in, boolean out, List<String> relations_searched) {
		Integer x_id = termStore.getTermId(x_word);
		if(x_id == null) {
			return null;
		}
		HashSet<Long> term_ids = new HashSet<>();
		if(relations_searched != null) {
			for(String term : term_searched) {
				Integer term_id = termStore.getTermId(term);
				if(term_id != null) {
					term_ids.add( (long) term_id);
				}
			}
		}	
		HashSet<Integer> relation_ids = new HashSet<>();
		if(relations_searched != null ) {				
			for(String r_name : relations_searched) {
				Integer r_id = relationTypeStore.getId(r_name);
				if(r_id != null) {
					relation_ids.add(r_id);
				}
			}
		}
		FilteredQuery relationQuery = new FilteredQuery(x_id, term_ids, in, out, relation_ids);
		return relationQuery;
	}
	
	public FilteredQuery create(String x_word,List<String> term_searched, List<String> relations_searched) {
		return create(x_word,term_searched,true,true,relations_searched);
	}
}
