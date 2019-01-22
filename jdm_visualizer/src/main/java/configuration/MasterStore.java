package configuration;

import Store.*;
import core.Relation;
import core.FilteredQuery;
import core.RelationQueryFactory;
import core.TreeQuery;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class MasterStore implements RelationStore{

  
    
    /** The JDM network entry point */
    private JDM_RelationStore jdmStore;
    
    /** The Neo4J DB used for store new relationships */
    private Neo4J_RelationStore neo4jStore;
    
    /** The set of terms for which relation are stored into DB */
    private CacheManager cacheManager;
    
    /** The in memory termStore */
    private TermStore termStore;
    
    /**The in memory RelationType Store  */
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
		
		termStore = new TermStore();
		cacheManager = new CacheManager();
	        
        List<String> lines = Files.readAllLines(Paths.get(serialized_terms_path),StandardCharsets.ISO_8859_1);
        Iterator<String> it = lines.iterator();
        if(it.hasNext()){
        	it.next(); // split first csv line
        	while(it.hasNext()){
        		String line = it.next();
        		if(line != null && !line.isEmpty()) {
    				String[] parts = line.split(",");	
    				if(parts.length == 3) {
    					Integer id = Integer.parseInt(parts[0]);
    					Boolean isCached = Boolean.parseBoolean(parts[2]);
    					termStore.addTerm(id,parts[1]);
    					if(isCached){
    						//cacheManager.addTerm(id);
    					}
    				}	
    			}
        	}
        }
	
		logger.info("MemoryTermStore init[OK] "+lines.size()+"terms read, "+cacheManager.getNbTermCached()+" terms cached");
			
		String relationsTypePath = memoryObj.getString("relation_types");									
		relationTypeStore = new RelationTypeStore(relationsTypePath);
		logger.info("RelationTypeStore init[OK]");
				
		JSONObject persistentObj = rootObj.getJSONObject("persistent");
		JSONObject storeObj = persistentObj.getJSONObject("stores");
		JSONObject neo4jObj = storeObj.getJSONObject("Neo4j");

		neo4jStore = new Neo4J_RelationStore(neo4jObj,relationTypeStore,termStore);	
        logger.info("Neo4J store building [OK]");		
            
    	jdmStore = new JDM_RelationStore(termStore);

        queryFactory  = new RelationQueryFactory(termStore, relationTypeStore);
             
    }
    
    public void init(String dataDirPath) {
    	File dir = new File(dataDirPath);
    	if(! dir.isDirectory()) {
    		logger.severe("Error "+dataDirPath+" is not a directory");
    	}
//    	persistentStore.insertNodes();
//    	for(File file : dir.listFiles()) {
//    		
//    	}
    }
    
    public JDM_RelationStore getInputStore() {
		return jdmStore;
	}

	public Neo4J_RelationStore getPersistentStore() {
		return neo4jStore;
	}

	public TermStore getTermStore() {
        return termStore;
    } 	

    public RelationTypeStore getRelationTypeStore() {
		return relationTypeStore;
	}
    
    
    @Override
    public Map<Integer, ArrayList<Relation>> query(FilteredQuery query) throws Exception {
    	int xId = query.getX();
    	if(cacheManager.isCached(xId)) {
    		logger.info(query.toString()+"[CACHED]");
    		Map<Integer, ArrayList<Relation>> results = neo4jStore.query(query); 
    		logger.info(query.toString()+"[OK]");
    		return results;
    	}
		logger.info(query.toString()+"[UNCACHED]");
    	Map<Integer, ArrayList<Relation>> results = jdmStore.query(query);
		logger.info(query.toString()+"[OK]");

    	return results;
//    	if(results != null) {
//    		boolean askPersistent = applyUpdateStrategy(xId,results);
//        	if(askPersistent) {
//        		persistentStore.insert(results);
//        		persistentStore.flush();
//        		termWithRelationsInDB.add(xId);    		
//        	} 	 
//    	}
    		
    }
    
    

	@Override
	public Map<Integer, ArrayList<Relation>> query(TreeQuery query) {
		// TODO Auto-generated method stub
		return null;
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
		JSONArray predicatesArray = rootObj.getJSONArray("predicates");
		JSONArray yTermsArray = rootObj.getJSONArray("terms");
		Boolean isIn = ! Boolean.parseBoolean(rootObj.getString("in"));
		Boolean isOut= ! Boolean.parseBoolean(rootObj.getString("out"));
		String format = rootObj.getString("format");

		List<String> relationsSearched = new ArrayList<>(predicatesArray.length());
		for(int i=0;i<predicatesArray.length();i++){
			relationsSearched.add(predicatesArray.getString(i));
		}
		List<String> yTerms = new ArrayList<>(yTermsArray.length());
		for (int i = 0; i < yTermsArray.length(); i++) {
			yTerms.add(yTermsArray.getString(i));
		}
		
		FilteredQuery query = queryFactory.create(x,yTerms,isIn,isOut,relationsSearched);
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
    		case "sorted": {
    			
    		}
    		default : {
    			break;
    		}
    	}
    	return jsonObj.toString();
    }
    
    
    
    private boolean applyUpdateStrategy(int xId,Map<Integer, ArrayList<Relation>> results) {    	
    	return true;
    }



    
   
    
   
}
