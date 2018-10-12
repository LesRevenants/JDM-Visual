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
//import org.neo4j.driver.v1.AuthTokens;
//import org.neo4j.driver.v1.Driver;
//import org.neo4j.driver.v1.GraphDatabase;
//import org.neo4j.driver.v1.Session;
//import org.neo4j.driver.v1.StatementResult;
//import org.neo4j.driver.v1.Transaction;
//import org.neo4j.driver.v1.TransactionWork;
//import org.neo4j.driver.v1.Values;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import Store.Neo4J_Relationship.JDM_Relation_Type;

public class Neo4J_RelationStore implements ReadRelationStore,WriteRelationStore{


    private int max_size;
    private GraphDatabaseService graphDb;
    private MemoryTermStore termStore;
    
    public static final String insert_relation_query_p1 = 
    		"MATCH (t1:Term {name:$n1}) MATCH (t2:Term {name:$n2}) CREATE (t1)-[:`"
    		,insert_relation_query_p2 = "` {weight:$w}]-> (t2)";
    
//	"MATCH (t1:Term {id:$id}) MATCH (t2:Term {id:$id2}) CREATE (t1)-[:$rid {weight:$w}]-> (t2)";

    private PatriciaTrie<Neo4J_Relationship.JDM_Relation_Type> jdm_relation_types;
    final static Logger logger = Logger.getLogger("Neo4J_RelationStore");
    

    public Neo4J_RelationStore(JSONObject prop, RelationTypeStore relationTypeStore, MemoryTermStore termStore) throws SQLException {
    	this.termStore = termStore;
        this.max_size = prop.getInt("max_relation");
       
        String dbPath = prop.getString("url");
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbPath) );
        logger.info("Neo4J database access [OK]");
        
        jdm_relation_types = new PatriciaTrie<>();        
        for(String r_name : relationTypeStore.getNames()){
        	JDM_Relation_Type label = JDM_Relation_Type.valueOf(r_name);
        	if(label != null){
        		jdm_relation_types.put(r_name, label);
        	}
        }
    }
    
    


    @Override
    public Map<String, ArrayList<Relation>> query(RelationQuery query) throws Exception {
    	  String x = query.getX();
          if(x == null)
              return null;
          

          HashMap<String, ArrayList<Relation>> allRelations = new HashMap<>();
          Set<String> relations_searched = query.getRelations_searched();
          Set<String> terms_searched = query.getTerm_searched();

          boolean are_relation_filtered = ! (relations_searched == null || relations_searched.isEmpty());
          boolean in = query.isIn();
          boolean isOut = query.isOut();
              
          if(in){
        	  
          }
          if(isOut) {
        	  
          }
          return allRelations;
    }

    @Override
    public int getMax_size() {
        return max_size;
    }

    @Override
    public void addRelation(Relation relation) {
//    	
//    	int t1_id = termStore.getTermId(relation.getX_id());
//    	int t2_id = termStore.getTermId(relation.getY_id());
    	
    	Label termLabel = Label.label( "Term" );

    	try ( Transaction tx = graphDb.beginTx() )   {		
    		
    		Node t1 = graphDb.findNode(termLabel,"name",relation.getX_id());
    		Node t2 = graphDb.findNode(termLabel,"name",relation.getY_id());
    	    JDM_Relation_Type relation_type = jdm_relation_types.get(relation.getType());   	    
    	    Relationship relationship = t1.createRelationshipTo(t2, relation_type);
    	    relationship.setProperty("weight",relation.getWeight());
    	    tx.success();
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


	@Override
	public void addRelations(Collection<Relation> relations) {
		
		
	}
    


}
