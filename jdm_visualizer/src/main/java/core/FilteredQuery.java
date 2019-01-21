package core;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import Store.RelationTypeStore;
import Store.TermStore;


/*
 * Special of type of query (x, R : {r1,r2...rk}, T : {t1,t2,tl}} 
 * Query used to search if some term x has any relation ri  with any term tj
 *  equivalent to : 
 * 		?x r_isa {animal,voiture,adv,autoroute}
 *      ?x r_pos {animal,voiture,adv,autoroute}
 *      ?x r_association {animal,voiture,adv,autoroute}
 */
public class FilteredQuery extends Query {

    /** The set of terms */
    private Set<Long> term_searched;
    
    /** the set of relations */ 
    private Set<Integer> relations_searched;


    public FilteredQuery(int x,Set<Long> term_searched, boolean in, boolean out, Set<Integer> relations_searched) {
        super(x,in,out);
    	this.term_searched = term_searched;      
        this.relations_searched = relations_searched;
    }
    
    public Set<Long> getTerm_searched() {
        return term_searched;
    }
    
    public Set<Integer> getRelations_searched() {
        return relations_searched;
    }
    
    public String asJSON(TermStore termStore, RelationTypeStore relationTypeStore, String format){
    	JSONObject queryObj = new JSONObject();
    	queryObj.put("motx",termStore.getTermName(x));
    	
    	JSONArray yTermsArray = new JSONArray();
    	if(term_searched != null){
    		for(long y_id : term_searched){
        		yTermsArray.put(termStore.getTermName((int)y_id));
        	}
    	}  	
    	queryObj.put("terms", yTermsArray);
    	
    	JSONArray predicatesArray = new JSONArray();
    	if(relations_searched != null){   		
        	for(int r_id : relations_searched){
        		predicatesArray.put(relationTypeStore.getName(r_id));
        	}
    	} 	
    	queryObj.put("predicates", predicatesArray);   	
    	queryObj.put("in",in ? "true" : "false");
    	queryObj.put("out",out ? "true" : "false");
    	queryObj.put("format",format);
    	return queryObj.toString();
    }

    @Override
    public String toString() {
        return "{"+x+","+term_searched+","+relations_searched+","+in+","+out+"}";
    }
}
