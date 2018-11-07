package core;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import Store.RelationTypeStore;
import Store.TermStore;

public class RelationQuery {

    private int x;

    private Set<Long> term_searched;

    private boolean in,out;

    public Set<Integer> relations_searched;


    public RelationQuery(int x,Set<Long> term_searched, boolean in, boolean out, Set<Integer> relations_searched) {
        this.x = x;
        this.term_searched = term_searched;
        this.in = in;
        this.out = out;
        this.relations_searched = relations_searched;
    }
    

    public int getX() {
        return x;
    }

    public Set<Long> getTerm_searched() {
        return term_searched;
    }

    public boolean isIn() {
        return in;
    }

    public boolean isOut() {
        return out;
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
    	queryObj.put("moty", yTermsArray);
    	
    	JSONArray predicatesArray = new JSONArray();
    	if(relations_searched != null){   		
        	for(int r_id : relations_searched){
        		predicatesArray.put(relationTypeStore.getName(r_id));
        	}
    	} 	
    	queryObj.put("predicat", predicatesArray);
    	
    	queryObj.put("input",in ? "true" : "false");
    	queryObj.put("output",out ? "true" : "false");
    	queryObj.put("format",format);
    	return queryObj.toString();
    }

    @Override
    public String toString() {
        return "{"+x+","+term_searched+","+relations_searched+","+in+","+out+"}";
    }
}
