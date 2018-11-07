package configuration;

import Store.*;
import core.Relation;
import core.RelationQuery;
import core.RelationQueryFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
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
    
    private RelationQueryFactory queryFactory;
    
    
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
        queryFactory  = new RelationQueryFactory(termStore, relationTypeStore);
       
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
    
    /**
     * 
     * @param query
     * @return
     * @throws Exception
     */
    public Map<Integer, ArrayList<Relation>> query(RelationQuery query) throws Exception {
    	int xId = query.getX();
    	if(termWithRelationsInDB.contains(xId)) {
    		return persistentStore.query(query); 		
    	}
    	
    	Map<Integer, ArrayList<Relation>> results = inputStore.query(query);
    	applyUpdateStrategy(results);
		return results;   	
    }
    
   
    
    /**
     * 
     * @param queryAsJson
     * @return
     * @throws Exception
     */
    public String query(String queryAsJson) throws Exception{
    	    	
    	byte[] encodedjSON = queryAsJson.getBytes();
    	String stringJSON = new String(encodedjSON);
		JSONObject rootObj =  new JSONObject(stringJSON);
		
		String x = rootObj.getString("motx");
		JSONArray predicatesArray = rootObj.getJSONArray("predicat");
		JSONArray yTermsArray = rootObj.getJSONArray("moty");
		Boolean isIn = Boolean.parseBoolean(rootObj.getString("input"));
		Boolean isOut= Boolean.parseBoolean(rootObj.getString("output"));
		String format = rootObj.getString("format");

		List<String> relationsSearched = new ArrayList<>(predicatesArray.length());
		for(int i=0;i<predicatesArray.length();i++){
			relationsSearched.add(predicatesArray.getString(i));
		}
		List<String> yTerms = new ArrayList<>(yTermsArray.length());
		for (int i = 0; i < yTermsArray.length(); i++) {
			yTerms.add(yTermsArray.getString(i));
		}
		
		RelationQuery query = queryFactory.create(x,yTerms,isIn,isOut,relationsSearched);
		Map<Integer, ArrayList<Relation>> queryResults = query(query);
    	return buildJsonContent(queryResults, format);
    }
    
    /**
     * 
     * @param queryResults
     * @param format
     * @return
     */
    private String buildJsonContent(Map<Integer, ArrayList<Relation>> queryResults,String format){
    	JSONObject jsonObj = new JSONObject();
    	
    	switch(format) {
    		case "grouped" : {
    			for(Integer r_id : queryResults.keySet()){
    				String relationName = relationTypeStore.getName(r_id);
    			    JSONArray allRelations = new JSONArray();
    			    
    				for(Relation relation: queryResults.get(r_id)){
    					JSONArray relationArray = new JSONArray();
    					String x_name = termStore.getTermName((int) relation.getX_id());
    					String y_name = termStore.getTermName((int) relation.getY_id());
    					relationArray.put(x_name);
    					relationArray.put(y_name);
    					relationArray.put(relation.getWeight());
    					allRelations.put(relationArray);
    				}
    				jsonObj.put(relationName, allRelations);
    			}
    			break;
    		}
    		default : {
    			break;
    		}
    	}
    	return jsonObj.toString();
    }
    
    
    
    private void applyUpdateStrategy(Map<Integer, ArrayList<Relation>> results) {
    	
    }
    
    
    
   
}
