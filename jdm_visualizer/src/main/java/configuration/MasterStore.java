package configuration;

import Store.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class MasterStore {

    public final static String
                            ENTRIES_KEY = "ENTRIES",
                            MWE_ENTRIES_KEY ="MWE_ENTRIES",
                            IS_UPDATE_ENTRIES_KEY = "IS_UPDATE_ENTRIES",
                            IS_UPDATE_MWE_ENTRIES_KEY = "IS_UPDATE_MWE_ENTRIES",
                            RELATION_TYPES_ENTRIES_KEY = "LAST_OUTPUT_STATS",
                            MAX_RELATION_IN_DB_KEY = "MAX_RELATION_IN_DB ";

    private Properties properties;
    private TermStore termStore;
    private ReadRelationStore inputStore;
    private WriteRelationStore persistentStore;
    private RelationTypeStore relationTypeStore;
 
    final static Logger logger = Logger.getLogger("JDM_RelationStore");

    public MasterStore(Properties properties) throws IOException {
        this.properties = properties;
        termStore = new MemoryTermStore();
        inputStore = new JDM_RelationStore();   
        persistentStore = new Neo4J_RelationStore(Integer.parseInt(properties.getProperty(MAX_RELATION_IN_DB_KEY)),properties);
        build();
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

	public TermStore getTermStore() {
        return termStore;
    } 
	

    public RelationTypeStore getRelationTypeStore() {
		return relationTypeStore;
	}

	private void build() throws IOException {

        String entries_path = properties.getProperty(ENTRIES_KEY);
        String mwe_entries_path = properties.getProperty(MWE_ENTRIES_KEY);
        String relation_types_path = properties.getProperty(RELATION_TYPES_ENTRIES_KEY);
        
        Boolean entries_update = Boolean.parseBoolean(properties.getProperty(IS_UPDATE_ENTRIES_KEY));
        Boolean mwe_entries_update = Boolean.parseBoolean(properties.getProperty(IS_UPDATE_MWE_ENTRIES_KEY));

        if(! entries_update){
            List<String> entries = Files.readAllLines(Paths.get(entries_path),StandardCharsets.ISO_8859_1);
            addEncodedTerms(entries);
        }
        if(! mwe_entries_update){
            List<String> mwe_entries = Files.readAllLines(Paths.get(mwe_entries_path),StandardCharsets.ISO_8859_1);
            addEncodedTerms(mwe_entries);
        }
        List<String> relation_types = Files.readAllLines(Paths.get(relation_types_path),StandardCharsets.ISO_8859_1);
        relationTypeStore = new MemoryRelationTypeStore(relation_types);

        logger.info("TermStore Building [OK]");
    }

    private void addEncodedTerms(Collection<String> termList){
        for (String line : termList) {
            if (line != null && !line.isEmpty()) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    Integer id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    termStore.addTerm(id,name);
                }
            }
        }
    }
}
