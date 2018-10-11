package Store;

import RequeterRezo.RequeterRezo;
import RequeterRezo.Mot;
import RequeterRezo.Terme;
import core.Relation;
import core.RelationQuery;

import java.util.*;
import java.util.logging.Logger;



public class JDM_RelationStore implements ReadRelationStore{

    private RequeterRezo requeterRezo;

    public JDM_RelationStore(){
        requeterRezo = new RequeterRezo(65536);
    }


    /**
     *
     * @param relation_to_search
     * @param terms_searched
     * @param mapToQuery
     * @param allRelations
     */
    private void query(Collection<String> relation_to_search,Set<String> terms_searched, String x, HashMap<String, ArrayList<Terme>> mapToQuery,HashMap<String, ArrayList<Relation>> allRelations) {

        boolean are_y_terms_filtered = ! (terms_searched == null || terms_searched.isEmpty());

        for(String relation_type : relation_to_search){
            ArrayList<Terme> termes = mapToQuery.get(relation_type);
            if(termes != null){
                for(Terme terme : termes){
                    if(! are_y_terms_filtered  || terms_searched.contains(terme.getTerme())){
                        allRelations.putIfAbsent(relation_type,new ArrayList<>());
                        allRelations.get(relation_type).add(new Relation(0,relation_type,x,terme.getTerme(),(int) terme.getPoids()));
                    }
                }
            }

        }
    }

    @Override
    public Map<String, ArrayList<Relation>> query(RelationQuery query) throws Exception{
        String x = query.getX();
        if(x == null)
            return null;
        Mot mot = requeterRezo.requete(x);
        if(mot == null)
            return null;

        HashMap<String, ArrayList<Relation>> allRelations = new HashMap<>();
        Set<String> relations_searched = query.getRelations_searched();
        Set<String> terms_searched = query.getTerm_searched();

        boolean are_relation_filtered = ! (relations_searched == null || relations_searched.isEmpty());
        boolean in = query.isIn();
        boolean isOut = query.isOut();

        Collection<String> relations_to_search;
        if(in){
            relations_to_search = are_relation_filtered ? relations_searched : mot.getRelations_entrantes().keySet();
            query(relations_to_search,terms_searched,x,mot.getRelations_entrantes(),allRelations);
        }
        if(isOut){
            relations_to_search = are_relation_filtered ? relations_searched : mot.getRelations_sortantes().keySet();
            query(relations_to_search,terms_searched,x,mot.getRelations_sortantes(),allRelations);
        }
        return allRelations;
    }

    @Override
    public Relation get(int r_id) {
        return null;
    }
}
