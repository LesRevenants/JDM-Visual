package Store;

import core.Relation;

public interface WriteRelationStore extends RelationStore, WriteTermStore{
	
	public static final String MAX_RELATION_IN_DB_KEY = "MAX_RELATION_IN_DB ";

    public int getMax_size();

    public abstract void addRelation(Relation relation);

    public abstract void delete(Relation relation);

    public abstract void update(Relation oldRelation, Relation newRelation);
    
    public void resetTerms();



}
