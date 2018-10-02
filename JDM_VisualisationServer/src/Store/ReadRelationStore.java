package Store;

import RequeterRezo.Terme;

import java.util.ArrayList;
import java.util.Map;

public interface ReadRelationStore extends RelationStore {

    public Map<String,ArrayList<Terme>> query(RelationQuery query) throws Exception;

    public Relation get(int r_id);

}