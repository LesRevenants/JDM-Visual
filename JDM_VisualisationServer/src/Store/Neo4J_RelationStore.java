package Store;

import RequeterRezo.Terme;

import java.util.ArrayList;
import java.util.Map;

public class Neo4J_RelationStore implements ReadRelationStore,WriteRelationStore{


    private int max_size;

    public Neo4J_RelationStore(int max_size) {
        this.max_size = max_size;
    }

    @Override
    public Map<String, ArrayList<Terme>> query(RelationQuery query) throws Exception {
        return null;
    }

    @Override
    public int getMax_size() {
        return 0;
    }

    @Override
    public void add(Relation relation) {

    }

    @Override
    public Relation get(int r_id) {
        return null;
    }

    @Override
    public void delete(Relation relation) {

    }

    @Override
    public void update(Relation oldRelation, Relation newRelation) {

    }


}
