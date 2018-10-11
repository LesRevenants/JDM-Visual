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
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Values;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.factory.GraphDatabaseFacade;

public class Neo4J_RelationStore implements ReadRelationStore,WriteRelationStore{


    private int max_size;
    private Driver driver;
    
//    public static final String create_index_query = "CREATE INDEX ON :Term(id)"
    
    public static final String insert_node_query = "CREATE (t:Term {name:$name, id:$id } )";
    
    public static final String insert_relation_query_p1 = 
    		"MATCH (t1:Term {name:$n1}) MATCH (t2:Term {name:$n2}) CREATE (t1)-[:`"
    		,insert_relation_query_p2 = "` {weight:$w}]-> (t2)";
    
//	"MATCH (t1:Term {id:$id}) MATCH (t2:Term {id:$id2}) CREATE (t1)-[:$rid {weight:$w}]-> (t2)";

    private PatriciaTrie<String> queries;
    final static Logger logger = Logger.getLogger("Neo4J_RelationStore");
    

    public Neo4J_RelationStore(JSONObject prop, RelationTypeStore relationTypeStore) throws SQLException {
    	
        this.max_size = prop.getInt("max_relation");
        String db_server = prop.getString("url");
        String user = prop.getString("user");
        String pwd = prop.getString("pwd");     
        
        driver = GraphDatabase.driver(db_server, AuthTokens.basic(user,pwd));
        
        queries = new PatriciaTrie<>();        
        relationTypeStore.getNames().forEach(r_name -> {
			queries.put(r_name,insert_relation_query_p1+r_name+insert_relation_query_p2);
		});
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
    	
   	
 	
    	String query = queries.get(relation.getType());
    	try ( Session session = driver.session() ){
            session.writeTransaction( new TransactionWork<Integer>(){   
          	
                public Integer execute( Transaction tx )             {
                    StatementResult result = tx.run(query,Values.parameters("n1", relation.getX_id(),
                                                    		 "n2",relation.getY_id(),                                                   		 
                                                    		 "w",relation.getWeight()));     
                    return 1;
                }
            } );
        }
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
