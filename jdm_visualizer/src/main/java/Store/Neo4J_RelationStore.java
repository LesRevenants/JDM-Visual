package Store;

import core.Relation;
import core.RelationQuery;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.json.JSONObject;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import Store.Neo4J_Relationship.JDM_Relation_Type;

import org.neo4j.graphdb.RelationshipType;


public class Neo4J_RelationStore {

//  public static final String create_index_query = "CREATE INDEX ON :Term(id)"    
//  public static final String insert_node_query = "CREATE (t:Term {name:$name, id:$id } )";
//  
//  public static final String insert_relation_query_p1 = 
//  		"MATCH (t1:Term {name:$n1}) MATCH (t2:Term {name:$n2}) CREATE (t1)-[:`"
//  		,insert_relation_query_p2 = "` {weight:$w}]-> (t2)";
  
//	"MATCH (t1:Term {id:$id}) MATCH (t2:Term {id:$id2}) CREATE (t1)-[:$rid {weight:$w}]-> (t2)";

	/**
	 * The max number of relation into DB
	 */
    private int max_size;

    private GraphDatabaseService graphDb;


    /**
     * The main entry point to Neo4j graph management
     */
    private GraphDatabaseService graph;
    private Label termLabel = Label.label("Term");
    
    
    public static final String delete_all_relationship = " MATCH ()-[r]->() delete r";
   
    /**
     * Trie which associate a NEO4J_RELATION_LABEL to his corresponding name as string
     * Used to access to the good {@link NEO4J_RELATION_LABEL} from a relation encoded as string
     */
    private PatriciaTrie<NEO4J_RELATION_LABEL> jdm_to_neo4j_relations;
    
    
    private TermStore termStore;
    
    private RelationTypeStore relationTypeStore;
    
    /**
     * Buffer used to store temporarily a list of relation. Used for minimize number
     * of Neo4j transaction
     */
    private ArrayList<Relation> relationBuffer;
    
    /**
     * The max number of relation into {@link relationBuffer}
     */
    private static final int MAX_BATCH_SIZE = 8192*8;
   
    final static Logger logger = Logger.getLogger("Neo4J_RelationStore");
    

    /**
     * 
     * @param prop : a json object which store property of the Neo4j server
     * @param relationTypeStore
     * @param termStore
     * @throws SQLException
     */
    public Neo4J_RelationStore(JSONObject prop, RelationTypeStore relationTypeStore, TermStore termStore) throws SQLException {
    	
        this.max_size = prop.getInt("max_relation");
        String db_server = prop.getString("url");
        File serverDir = new File(db_server);
        graph =  new GraphDatabaseFactory().newEmbeddedDatabase(serverDir); 
        jdm_to_neo4j_relations = new PatriciaTrie<>();
        
        for(String r_name : relationTypeStore.getNames()) {
        	String neo4j_r_name = r_name.replaceAll("-", "_").toUpperCase();
        	neo4j_r_name = neo4j_r_name.replaceAll(">","_").toUpperCase();
        		NEO4J_RELATION_LABEL label = NEO4J_RELATION_LABEL.valueOf(neo4j_r_name);
            	jdm_to_neo4j_relations.put(r_name, label);
//        	}	
        }  
        
        this.termStore = termStore;
        this.relationTypeStore = relationTypeStore;
        relationBuffer = new ArrayList<>(MAX_BATCH_SIZE);
    }
    

    public Map<Integer, ArrayList<Relation>> query(RelationQuery query) throws Exception {
    	long x = query.getX();   
         String x_name = termStore.getTermName((int) x);
         if(x_name == null) {
        	 return null;
         }
          

          HashMap<Integer, ArrayList<Relation>> allRelations = new HashMap<>();
          Set<Integer> relations_searched = query.getRelations_searched();
          Set<Long> terms_searched = query.getTerm_searched();

          boolean are_relation_filtered = ! (relations_searched == null || relations_searched.isEmpty());
          boolean in = query.isIn();
          boolean isOut = query.isOut();
              
          if(in){
        	  
          }
          if(isOut) {
        	  
          }
          return allRelations;
    }

   
    public int getMax_size() {
        return max_size;
    }
    	
 	
//    	String query = queries.get(relation.getType());
//    	try ( Session session = driver.session() ){
//            session.writeTransaction( new TransactionWork<Integer>(){   
//          	
//                public Integer execute( Transaction tx )             {
//                    StatementResult result = tx.run(query,Values.parameters("n1", relation.getX_id(),
//                                                    		 "n2",relation.getY_id(),                                                   		 
//                                                    		 "w",relation.getWeight()));     
//                    return 1;
//                }
//            } );
//        }



	/**
	 * Add a list of relations to Neo4j DB
	 * You shoud  use this method only if you need to ensure 
	 * that each relation insertion is a success DB before inserting new terms
	 * Using this method to insert a big list of relation is the best direction to bad performance ! 
	 * @param relations : a Collection of @See{Relation}
	 */
    public void addRelation(Relation relation) {   
    	try ( Transaction tx = graph.beginTx() ){  		
    		Node x_node = graph.findNode(termLabel,"id",relation.getX_id());
        	Node y_node = graph.findNode(termLabel,"id",relation.getY_id()); 	

        	if(x_node != null && y_node != null) {
        		String r_name = relationTypeStore.getName(relation.getType()); // get relation name into Neo4j
            	RelationshipType r_type = jdm_to_neo4j_relations.get(r_name); // get correspondig ENUM which has r_name as name
        		Relationship relationship = x_node.createRelationshipTo(y_node,r_type);
            	relationship.setProperty("weight",relation.getWeight());  
        	}       		   
    	    tx.success();
    	}

    }

   
    public void delete(Relation relation) {

    }

   
    public void update(Relation oldRelation, Relation newRelation) {

    }


	/**
	 * Add a list of relations to Neo4j DB
	 * You shoud prefer this method because batch inserting is applied, which allow 
	 * noteworthy insert speed improvement
	 * You should use flush when you finish insertion in order to ensure
	 * that all relations are inserted into DB
	 * @param relations : a Collection of @See{Relation}
	 */
	public void addRelations(Collection<Relation> relations) {  
		
		if(relations.size() <= MAX_BATCH_SIZE) {			
			relationBuffer.addAll(relations);	
			if(relationBuffer.size() + relations.size() >= MAX_BATCH_SIZE) {				
				insertRelations(relationBuffer);	
				relationBuffer.clear();
			}	
		}
		
	}
	
	private void insertRelations(Collection<Relation> relations) {
		try ( Transaction tx = graph.beginTx() ){
    		
    		for(Relation relation : relationBuffer) {
    			Node x_node = graph.findNode(termLabel,"id",relation.getX_id());
            	Node y_node = graph.findNode(termLabel,"id",relation.getY_id());                  	
            	if(x_node != null && y_node != null) {
            		String r_name = relationTypeStore.getName(relation.getType()); // get relation name into Neo4j
                	RelationshipType r_type = jdm_to_neo4j_relations.get(r_name); // get correspondig ENUM which has r_name as name
            		Relationship relationship = x_node.createRelationshipTo(y_node,r_type);
                	relationship.setProperty("weight",relation.getWeight());   	  
            	}          	
    		}
    		tx.success();    		
    	}
	}

	public void reset() {
	  try (Transaction tx =graph.beginTx()) {
		  graph.execute(delete_all_relationship);
		  tx.success();
	  }
				
	}

	/**
	 * Flush all relation into @See{relationBuffer} into DB
	 */
	public void flush() {
		if(! relationBuffer.isEmpty()) {
			insertRelations(relationBuffer);
		}		
	}
    


}
