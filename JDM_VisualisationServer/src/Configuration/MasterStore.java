package Configuration;

import Store.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class MasterStore {

    public final static String
                            ENTRIES_KEY = "ENTRIES",
                            MWE_ENTRIES_KEY ="MWE_ENTRIES",
                            IS_UPDATE_ENTRIES_KEY = "IS_UPDATE_ENTRIES",
                            IS_UPDATE_MWE_ENTRIES_KEY = "IS_UPDATE_MWE_ENTRIES",
                            MAX_RELATION_IN_DB_KEY = "MAX_RELATION_IN_DB ";

    private Properties properties;
    private TermStore termStore;
    private JDM_RelationStore jdmStore;
    private Neo4J_RelationStore neo4J_relationStore;

    public MasterStore(Properties properties) throws IOException {
        this.properties = properties;
        termStore = new MemoryTermStore();
        jdmStore = new JDM_RelationStore();
        neo4J_relationStore = new Neo4J_RelationStore(Integer.parseInt(properties.getProperty(MAX_RELATION_IN_DB_KEY)));
        build();
    }

    public Properties getProperties() {
        return properties;
    }

    public JDM_RelationStore getJDM_RelationStore() {
        return jdmStore;
    }

    public TermStore getTermStore() {
        return termStore;
    }

    private void build() throws IOException {

        String entries_path = properties.getProperty(ENTRIES_KEY);
        String mwe_entries_path = properties.getProperty(MWE_ENTRIES_KEY);
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
