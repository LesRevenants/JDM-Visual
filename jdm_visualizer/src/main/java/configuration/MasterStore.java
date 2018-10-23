package configuration;

import Store.*;
import core.Relation;
import core.RelationQuery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONObject;

public class MasterStore {


   
    
    /**
     * The JDM network entry point
     */
    private JDM_RelationStore inputStore;
    
    /**
     * The Neo4J DB used for store new relationships
     */
    private Neo4J_RelationStore persistentStore;
    
	public HashSet<Integer> termWithRelationsInDB;
    
    /**
     * The in memory termStore
     */
    private TermStore termStore;
    
    /**
     * The in memory RelationType Store
     */
    private RelationTypeStore relationTypeStore;
      
 
    final static Logger logger = Logger.getLogger("MasterStore");
    
    
    /**
     * 
     * @param config_file_path
     * @throws IOException
     * @throws SQLException
     */
    public MasterStore(String config_file_path) throws IOException, SQLException {
    	byte[] encodedjSON = Files.readAllBytes(Paths.get(config_file_path));
    	String stringJSON = new String(encodedjSON);
		JSONObject rootObj =  new JSONObject(stringJSON);
		
		JSONObject memoryObj = rootObj.getJSONObject("memory");
		String serialized_terms_path = memoryObj.getString("terms");
		
		termStore = new TermStore(serialized_terms_path);
		logger.info("MemoryTermStore init[OK]");
			
		String relationsPath = memoryObj.getString("relations");									
		relationTypeStore = new RelationTypeStore(relationsPath);
		logger.info("RelationTypeStore init[OK]");
				
		JSONObject persistentObj = rootObj.getJSONObject("persistent");
		JSONObject storeObj = persistentObj.getJSONObject("stores");
		JSONObject neo4jObj = storeObj.getJSONObject("Neo4j");

		persistentStore = new Neo4J_RelationStore(neo4jObj,relationTypeStore,termStore);	
        logger.info("Neo4J store building [OK]");		
            
    	inputStore = new JDM_RelationStore(termStore);
        logger.info("JDM store building [OK]");
        
        termWithRelationsInDB = new HashSet<>();   	
       
    }
    
  
    
    public JDM_RelationStore getInputStore() {
		return inputStore;
	}

	public Neo4J_RelationStore getPersistentStore() {
		return persistentStore;
	}

	public TermStore getTermStore() {
        return termStore;
    } 	

    public RelationTypeStore getRelationTypeStore() {
		return relationTypeStore;
	}
    
    public Map<Integer, ArrayList<Relation>> query(RelationQuery query) throws Exception {
    	int xId = query.getX();
    	if(termWithRelationsInDB.contains(xId)) {
    		return persistentStore.query(query); 		
    	}
    	
    	Map<Integer, ArrayList<Relation>> results = inputStore.query(query);
    	applyUpdateStrategy(results);
		return results;   	
    }
    
    private void applyUpdateStrategy(Map<Integer, ArrayList<Relation>> results) {
    	
    }
    
    
    
   
}
