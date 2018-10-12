package Store;

import org.neo4j.graphdb.RelationshipType;

public class Neo4J_Relationship {
	
	 public static enum JDM_Relation_Type implements RelationshipType {
		 r_associated,
		 r_isa,
		 r_raff_sem,
		 r_pos,
		 r_has_part,
		 r_pos_1,
		 
	 }
}
