package Store;

public interface WriteRelationStore extends RelationStore{

    public int getMax_size();

    public abstract void add(Relation relation);

    public abstract void delete(Relation relation);

    public abstract void update(Relation oldRelation, Relation newRelation);


}
