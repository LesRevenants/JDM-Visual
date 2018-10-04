package Store;

import RequeterRezo.Terme;
import core.Relation;
import core.RelationQuery;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;


import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Neo4J_RelationStore implements ReadRelationStore,WriteRelationStore{


    private int max_size;
    private Driver neo4jDriver;
    final static Logger logger = Logger.getLogger("Neo4J_RelationStore");
    
    public static final String SERVER_URI_KEY = "SERVER_URI", USER_KEY = "USER", PASSWORD_KEY = "PASSWORD";
    

    public Neo4J_RelationStore(int max_size,Properties prop) {
        this.max_size = max_size;
        String db_server = prop.getProperty(SERVER_URI_KEY);
        String user = prop.getProperty(USER_KEY);
        String pwd = prop.getProperty(PASSWORD_KEY);
        neo4jDriver = GraphDatabase.driver(db_server,AuthTokens.basic(user, pwd));
        logger.info("Neo4J server access [OK]");
    }


    @Override
    public Map<String, ArrayList<Terme>> query(RelationQuery query) throws Exception {
        return null;
    }

    @Override
    public int getMax_size() {
        return max_size;
    }

    @Override
    public void add(Relation relation) {

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


}
