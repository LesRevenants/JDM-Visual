package configuration;

import Store.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.logging.log4j.core.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MasterStore {
	
    public final static String
                            ENTRIES_KEY = "ENTRIES",
                            MWE_ENTRIES_KEY ="MWE_ENTRIES",
                            IS_UPDATE_ENTRIES_KEY = "IS_UPDATE_ENTRIES",
                            IS_UPDATE_MWE_ENTRIES_KEY = "IS_UPDATE_MWE_ENTRIES",
                            RELATION_TYPES_ENTRIES_KEY = "LAST_OUTPUT_STATS";

    private Properties properties;
    private MemoryTermStore termStore;
    private ReadRelationStore inputStore;
    private WriteRelationStore persistentStore;
    private RelationTypeStore relationTypeStore;
    
    private static final String[] filters = {"\'","&","\"","$","+","-",":",".","!","(","*","/","\\"};
    private static final HashSet<String> forbidden_set = new HashSet<String>(Arrays.asList(filters));
    
 
    final static Logger logger = Logger.getLogger("MasterStore");
    
    public MasterStore() {
    	termStore = new MemoryTermStore();
        logger.info("InMemoryTermStore init[OK]");
        
        inputStore = new JDM_RelationStore();   
        logger.info("JDM_RelationStore init[OK]");
    }

    
    public MasterStore(String config_file_path) throws IOException, SQLException {
    	this();
    	byte[] encodedjSON = Files.readAllBytes(Paths.get(config_file_path));
    	String stringJSON = new String(encodedjSON);
		JSONObject rootObj =  new JSONObject(stringJSON);
		
		JSONObject memoryObj = rootObj.getJSONObject("memory");
		String serialized_terms_path = memoryObj.getString("serialized_terms_path");
		
		if(serialized_terms_path == null || serialized_terms_path.isEmpty()) {
			JSONArray terms_paths = memoryObj.getJSONArray("terms_paths");
			for (int i = 0; i < terms_paths.length(); i++) {
				String path = terms_paths.getString(i);
				List<String> lines = Files.readAllLines(Paths.get(path),StandardCharsets.ISO_8859_1);
				addEncodedTerms(lines);
		        logger.info("\tdata_block:"+path+" insertion[OK]");
			}
		}
		else {
			List<String> lines = Files.readAllLines(Paths.get(serialized_terms_path),StandardCharsets.ISO_8859_1);
			for(String line : lines) {
				String[] parts = line.split(",");
				int id = Integer.parseInt(parts[0]);
				if(parts.length == 2) {
					termStore.addTerm(id,parts[1]);
				}	
			}
	        logger.info("\tWhole term list insertion[OK]");
		}
		
		String relation_types_path = memoryObj.getString("relation_types_path");
		List<String> relation_types = Files.readAllLines(Paths.get(relation_types_path));
		relationTypeStore = new MemoryRelationTypeStore(relation_types);
		
		JSONObject persistentObj = rootObj.getJSONObject("persistent");
		JSONObject storeObj = persistentObj.getJSONObject("stores");
		JSONObject neo4jObj = storeObj.getJSONObject("Neo4j");
		persistentStore = new Neo4J_RelationStore(neo4jObj,relationTypeStore,termStore);
		
        logger.info("TermStore Building [OK]");

		
    }

    public Properties getProperties() {
        return properties;
    }


    public ReadRelationStore getInputStore() {
		return inputStore;
	}

	public WriteRelationStore getPersistentStore() {
		return persistentStore;
	}

	public MemoryTermStore getTermStore() {
        return termStore;
    } 
	

    public RelationTypeStore getRelationTypeStore() {
		return relationTypeStore;
	}

    private void addEncodedTerms(Collection<String> termList){
        for (String line : termList) {
            if (line != null && !line.isEmpty()) {
            	
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    Integer id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    if(checkName(name)) {
                        termStore.addTerm(id,name);
                    }
                }
            }
        }
    }
    
    private boolean checkName(String name) {   	
    	if(forbidden_set.contains(name.substring(0, 1))) {
    		return false;
    	}  	
    	try {
    		Integer.parseInt(name); 	
    		return false;
    	}
    	catch (NumberFormatException ex)
        {
            return true;
        }
    }
        
}
