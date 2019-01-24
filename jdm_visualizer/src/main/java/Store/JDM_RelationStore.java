package Store;

import core.Relation;
import core.TreeQuery;
import core.FilteredQuery;
import requeterRezo.Filtre;
import requeterRezo.Mot;
import requeterRezo.RequeterRezoDump;
import requeterRezo.Voisin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Class which handle the querying to JDM rezoDump 
 *
 */
public class JDM_RelationStore implements RelationStore {

    private RequeterRezoDump requeterRezo;
    private TermStore termStore;

    public JDM_RelationStore(TermStore termStore){
        requeterRezo = new RequeterRezoDump("96h","128Mo");
//        requeterRezo.viderCache();
        this.termStore = termStore;
    }
    
	@Override
	public Map<Integer, ArrayList<Relation>> query(TreeQuery query) throws Exception{
		long x = query.getX();
        String x_name = termStore.getTermName((int) x);
        if(x_name == null) {
        	return null;
        }

        HashMap<Integer, ArrayList<Relation>> allRelations = new HashMap<>();       
        HashMap<Long,Set<Long>> patterns = query.getPatterns();
//        boolean are_relation_filtered = ;
        boolean in = query.isIn();
        boolean isOut = query.isOut();

        Filtre filtre = (in && isOut) ? Filtre.FiltreNull :  // use JDM filter for incoming/outcoming relations
        	( (!in) ? Filtre.FiltreRelationsEntrantes : Filtre.FiltreRelationsSortantes);
        if(! patterns.isEmpty()) {   
        	StringBuilder relation_filter = new StringBuilder(); // the relation filter send to requterRezo.requeteMultiple() as str
        	Iterator<Entry<Long,Set<Long>>> it = patterns.entrySet().iterator();
        	Long firstRelation = it.next().getKey();
        	relation_filter.append(firstRelation);
//    		
    		if(patterns.size() > 1) {
    			while(it.hasNext()) {
    				relation_filter.append(";"+it.next()); // build relation filter string for requeteMultiple
    			}
    			ArrayList<Mot> mots = requeterRezo.requeteMultiple(x_name, relation_filter.toString());
    			if(mots != null && ! mots.isEmpty()) {
    				for(Mot mot : mots) {
//        				queryFiltered(mot,in,isOut,relations_searched,terms_searched,allRelations);   	
        			}    			
    			}
//    			
//    		}
    		}
    		else {
    			Mot mot = requeterRezo.requete(x_name,relation_filter.toString(),filtre);
            	if(mot != null) {            		
        			queryFiltered(mot,in,isOut,patterns.get(firstRelation),allRelations);   			
    			}
    		}
        }
        else {
        	Mot mot = requeterRezo.requete(x_name,filtre);
        	if(mot != null) {
            	queryFiltered(mot,in,isOut,null,allRelations);  
        	}
        }
        return allRelations;
	}
	

    
    /**
     * Allow to query JDM rezeoDump with a query
     * @param query
     * @return a Map which associate to a relation_id each Relation which match to query
     * @throws Exception
     */
    public Map<Integer, ArrayList<Relation>> query(FilteredQuery query) throws Exception {
    	HashMap<Integer, ArrayList<Relation>> allRelations = new HashMap<>();
        long x = query.getX();
        String x_name = termStore.getTermName((int) x);
        if(x_name == null) {
        	return allRelations;
        }
   
        Set<Integer> relations_searched = query.getRelations_searched();
        Set<Long> terms_searched = query.getTerm_searched();

        boolean are_relation_filtered = ! (relations_searched == null || relations_searched.isEmpty());
        boolean in = query.isIn();
        boolean isOut = query.isOut();

        Filtre filtre = (in && isOut) ? Filtre.FiltreNull :  // use JDM filter for incoming/outcoming relations
        	( (!in) ? Filtre.FiltreRelationsEntrantes : Filtre.FiltreRelationsSortantes);
        
        
        if(are_relation_filtered) {   
        	StringBuilder relation_filter = new StringBuilder(); // the relation filter send to requterRezo.requeteMultiple() as str
    		Iterator<Integer> it = relations_searched.iterator();
    		Integer first_relation = it.next();
    		relation_filter.append(first_relation);
    		
    		if(relations_searched.size() > 1) {
    			while(it.hasNext()) {
    				relation_filter.append(";"+it.next()); // build relation filter string for requeteMultiple
    			}
    			ArrayList<Mot> mots = requeterRezo.requeteMultiple(x_name, relation_filter.toString());
    			if(mots != null && ! mots.isEmpty()) {
    				for(Mot mot : mots) {
        				queryFiltered(mot,in,isOut,terms_searched,allRelations);   	
        			}    			
    			}
    			
    		}
    		else { // search only one relation
    			Mot mot = requeterRezo.requete(x_name,first_relation,filtre);
            	if(mot != null) {
        			queryFiltered(mot,in,isOut,terms_searched,allRelations);   			
    			}
    		}
        }
        else { // no particular relation searched
        	Mot mot = requeterRezo.requete(x_name,filtre);
        	if(mot != null) {
            	queryFiltered(mot,in,isOut,terms_searched,allRelations);  
        	}
        }

        return allRelations;
    }
    
    /**
     * SubMethod used to ease to querying according incoming and/or outcoming relation filtering
     * 
     * @param mot the x word to search
     * @param in : true if incoming x relation are required
     * @param out : true if outgoing x relation are required
     * 
     * @param relations_searched : the list of different relation like for all x r y | r belongs to relations_searched
     * if null, no filter is applied on relations
     * 
     * @param terms_searched : the list of different term like for all x r y | y belongs to terms_searched
     * if null, no filter is applied on terms
     * 
     * @param allRelations : Map which associate to a relation_id each Relation which match to query
     */
    private void queryFiltered(Mot mot,
    		boolean in, boolean out,	
    		Set<Long> terms_searched,
    		HashMap<Integer, ArrayList<Relation>> allRelations) {
    	
		if(in){
            queryFiltered(terms_searched,mot.getID(),false,mot.getRelations_entrantes(),allRelations);
        }
        if(out){
            queryFiltered(terms_searched,mot.getID(),true,mot.getRelations_sortantes(),allRelations);
        }
    }

    /**
     * @param terms_searched : the list of different term like for all x r y | y belongs to terms_searched
     * if null, no filter is applied on terms
     * @param x_id : the id of x word
     * @param is_x_to_y_relation : indicate the order of relation if true, <x,r,y> relation is created else it's <y,r,x> 
     * @param mapToQuery : the subset of x relation to check, could be set of incoming or outcoming relations for x 
     * @param allRelations : Map which associate to a relation_id each Relation which match to query
     */
   private void queryFiltered(
   		Set<Long> terms_searched, long x_id, 
   		boolean is_x_to_y_relation,
   		HashMap<Integer, ArrayList<Voisin>> mapToQuery,
   		HashMap<Integer, ArrayList<Relation>> allRelations) {

       boolean are_y_terms_filtered = ! (terms_searched == null || terms_searched.isEmpty());

       for(Integer relation_type : mapToQuery.keySet()){
           ArrayList<Voisin> termes = mapToQuery.get(relation_type);
           if(termes != null){
               for(Voisin y : termes){
            	   if(y.getNoeud() != null) {
            		   long y_id = y.getNoeud().getID();      
            		   if(! are_y_terms_filtered  || terms_searched.contains(y_id)){            			   
//	   	                    if(termStore.getTermName((int) y_id) == null) {
//	   	                    	
//	   	                    }
//	   	                    else {
	   	                    	allRelations.putIfAbsent(relation_type,new ArrayList<>());
	   	                    	
		   	                   	Relation r = new Relation(0,relation_type,x_id,y_id,y.getPoids(),is_x_to_y_relation);
//		   	                   			is_x_to_y_relation ?
//	   	                   			  // change x and y relation according to order
//	   	                   			new Relation(0,relation_type,y_id,x_id,y.getPoids());
		   	                   	allRelations.get(relation_type).add(r);                
//	   	                    }
		   	                 	
//	   	                    }
//	   	                    else {
//	   	                    	System.out.println("JDM:"+y_id+":"+y.getNom());
//	   	                    }
	   	                           
                      }
            	   }
                  
               }
           }

       }
   }
   
   private void queryTree() {
	   
   }




}
