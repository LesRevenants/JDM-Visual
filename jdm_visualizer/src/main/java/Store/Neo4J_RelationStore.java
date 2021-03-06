package Store;

import core.Relation;
import core.FilteredQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.json.JSONObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
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

    /**
     * The main entry point to Neo4j graph management
     */
    private GraphDatabaseService graph;
    
    private Label termLabel = Label.label("Term");
    
    
    public static final String delete_all_relationship = " MATCH ()-[r]->() delete r";
//    public static final String count_all_relationship = "MATCH (n)-[r]->() RETURN COUNT(r)";
   
    /**
     * Trie which associate a NEO4J_RELATION_LABEL to his corresponding name as string
     * Used to access to the good {@link NEO4J_RELATION_LABEL} from a relation encoded as string
     */
    private PatriciaTrie<NEO4J_RELATION_LABEL> namesToRelationshipTypes;
    
    private PatriciaTrie<String> neo4jToJdmRelationNames;
    
    private HashMap<Integer,NEO4J_RELATION_LABEL> idToRelationshipTypes;

    private TermStore termStore;
    
    private RelationTypeStore relationTypeStore;
    
    /**
     * Buffer used to store temporarily a list of relation. Used for minimize number
     * of Neo4j transaction
     */
    private LinkedList<Relation> relationBuffer;
    
    /**
     * The max number of relation into {@link relationBuffer}
     */
    private static final int MAX_BATCH_SIZE = 65536;
   
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
        
        namesToRelationshipTypes = new PatriciaTrie<>();
        idToRelationshipTypes = new HashMap<>();
        neo4jToJdmRelationNames = new PatriciaTrie<>();
        
        for(String r_name : relationTypeStore.getNames()) {
        	String neo4j_r_name = r_name.replaceAll("-", "_").toUpperCase();
        	neo4j_r_name = neo4j_r_name.replaceAll(">","_");
        	NEO4J_RELATION_LABEL label = NEO4J_RELATION_LABEL.valueOf(neo4j_r_name);
        	
            namesToRelationshipTypes.put(r_name, label);
            idToRelationshipTypes.put(relationTypeStore.getId(r_name), label);
            neo4jToJdmRelationNames.put(neo4j_r_name, r_name);
        }  
        
        this.termStore = termStore;
        this.relationTypeStore = relationTypeStore;
        relationBuffer = new LinkedList<>();
    
    }
    

    /**
     * 
     * @param query
     * @return
     * @throws Exception
     */
    
    public Map<Integer, ArrayList<Relation>> query(FilteredQuery query) throws Exception {
		long x = query.getX();   
		String x_name = termStore.getTermName((int) x);
		if(x_name == null) {
			return null;
		}
		
		 HashMap<Integer, ArrayList<Relation>> allRelations = new HashMap<>();
		 Set<Integer> relations_searched = query.getRelations_searched();
		 Set<Long> terms_searched = query.getTerm_searched();
		
		 boolean in = query.isIn();
		 boolean isOut = query.isOut();
		  
		 try ( Transaction tx = graph.beginTx() ){   		
			Node xNode = graph.findNode(termLabel,"id",query.getX());
			if(xNode != null) {
				 query(xNode,in,isOut,relations_searched,terms_searched,allRelations);
			}
			tx.success();    	
		 }
		 return allRelations;
}
    
    /**
     * 
     * @param xNode
     * @param in
     * @param out
     * @param relations_searched
     * @param terms_searched
     * @param allRelations
     */
    private void query(Node xNode,boolean in, boolean out,
    		Set<Integer> relations_searched, 	
    		Set<Long> terms_searched,  		
    		HashMap<Integer, ArrayList<Relation>> allRelations) {
    	
    	boolean are_relation_filtered = ! (relations_searched == null || relations_searched.isEmpty());
    	if(in) {
    		if(are_relation_filtered) {
    			NEO4J_RELATION_LABEL[] labels = new NEO4J_RELATION_LABEL[relations_searched.size()];
    			int i=0;
    			for(Integer relationId : relations_searched) {
    				NEO4J_RELATION_LABEL label = idToRelationshipTypes.get(relationId);
    				labels[i++] = label;
    			}
				query(xNode, false, terms_searched, relations_searched,xNode.getRelationships(Direction.INCOMING,labels).iterator(), allRelations);
    		}
    		else {
    		
        		query(xNode, false, terms_searched, relations_searched,xNode.getRelationships(Direction.INCOMING).iterator(), allRelations);	
    		}
    	}
    	if(out) {   		
    		if(are_relation_filtered) {
    			NEO4J_RELATION_LABEL[] labels = new NEO4J_RELATION_LABEL[relations_searched.size()];
    			int i=0;
    			for(Integer relationId : relations_searched) {
    				NEO4J_RELATION_LABEL label = idToRelationshipTypes.get(relationId);
    				labels[i++] = label;
    			}
				query(xNode, true, terms_searched, relations_searched,xNode.getRelationships(Direction.OUTGOING,labels).iterator(), allRelations);
    		}
    		else {
    			query(xNode,true, terms_searched,relations_searched, xNode.getRelationships(Direction.OUTGOING).iterator(), allRelations);	
    		} 		
    	}
    }
    
    /**
     * 
     * @param xNode
     * @param is_x_to_y_relation
     * @param terms_searched
     * @param relations_searched
     * @param relationsIt
     * @param allRelations
     */
    private void query(Node xNode,boolean is_x_to_y_relation,
    		Set<Long> terms_searched,
    		Set<Integer> relations_searched, 
    		Iterator<Relationship> relationsIt,
    		HashMap<Integer, ArrayList<Relation>> allRelations) {
    	
    	  boolean are_y_terms_filtered = ! (terms_searched == null || terms_searched.isEmpty());	
    	  Long xId = (long) xNode.getProperty("id");
    	  
    	  while(relationsIt.hasNext()) {
    		  
  			Relationship relationship = relationsIt.next();
  			String r_name = relationship.getType().name();
  			String jdm_relation_name = neo4jToJdmRelationNames.get(r_name);
  			Integer relation_type = relationTypeStore.getId(jdm_relation_name);
  			
  			Node yNode = relationship.getEndNode();
			Long yId = (Long) yNode.getProperty("id");
			
			if(!are_y_terms_filtered ||  terms_searched.contains(yId)){
				allRelations.putIfAbsent(relation_type,new ArrayList<>());
				
				Relation r = is_x_to_y_relation ?
  					new Relation(0,relation_type,xId,yId,0) :  // change x and y relation according to order
                  	new Relation(0,relation_type,yId,xId,0);
                allRelations.get(relation_type).add(r);        
			}		      
  		 }
    }
    
    public int getMax_size() {
        return max_size;
    }
    	
	/**
	 * Add a list of relations to Neo4j DB
	 * You should  use this method only if you need to ensure 
	 * that each relation insertion is a success before inserting new terms
	 * Using this method to insert a big list of relation is the best direction to bad performance ! 
	 * @param relations : a Collection of @See{Relation}
	 */
    public void addRelation(Relation relation) {   
    	try ( Transaction tx = graph.beginTx() ){  		
    		Node x_node = graph.findNode(termLabel,"id",relation.getX_id());
        	Node y_node = graph.findNode(termLabel,"id",relation.getY_id()); 	

        	if(x_node != null && y_node != null) {
        		String r_name = relationTypeStore.getName(relation.getType()); // get relation name into Neo4j
            	RelationshipType r_type = namesToRelationshipTypes.get(r_name); // get corresponding ENUM which has r_name as name
        		Relationship relationship = x_node.createRelationshipTo(y_node,r_type);
            	relationship.setProperty("weight",relation.getWeight());  
        	}       		   
    	    tx.success();
    	}

    }

   
    public void delete(Relation relation) {

    }

//    public long countTotalRelationships() {
//    	long size = 0;
//    	try(Transaction tx = graph.beginTx()){
//    		Result result = graph.execute(count_all_relationship);
//    		Map<String,Object> row = result.next();
//    		size = row.get(key)
//    		tx.success();    
//    	}
//    }

	/**
	 * Add a list of relations to Neo4j DB
	 * You shoud prefer this method because batch inserting is applied, which allow 
	 * noteworthy insert speed improvement
	 * You should use flush when you finish insertion in order to ensure
	 * that all relations are inserted into DB
	 * @param relations : a Collection of @See{Relation}
	 */
	public void insert(Collection<Relation> relations) {  
		
		if(relations.size() <= MAX_BATCH_SIZE) {			
			relationBuffer.addAll(relations);	
			if(relationBuffer.size() >= MAX_BATCH_SIZE) {				
				insertRelations();	
				relationBuffer.clear();
			}	
		}
		else {
			int i=0;
			Iterator<Relation> it = relations.iterator();
			while(it.hasNext()) {
				Relation relation = it.next();
				relationBuffer.add(relation);
				i++;
				if(i == MAX_BATCH_SIZE) {
					insertRelations();
					relationBuffer.clear();
					i=0;
				}
			}
			
		}
	}
	
	public void insert(Map<Integer, ArrayList<Relation>> relationMap) {
		for(Integer r_type : relationMap.keySet()) {
			insert(relationMap.get(r_type));
		}
	}
	
	
	
	private void insertRelations() {
		try ( Transaction tx = graph.beginTx() ){
    		
    		for(Relation relation : relationBuffer) {
    			Node x_node = graph.findNode(termLabel,"_id",relation.getX_id());
            	Node y_node = graph.findNode(termLabel,"_id",relation.getY_id()); 
            	
            	if(x_node != null && y_node != null) {
            		String r_name = relationTypeStore.getName(relation.getType()); // get relation name into Neo4j
                	RelationshipType r_type = namesToRelationshipTypes.get(r_name); // get correspondig ENUM which has r_name as name              	
            		Relationship relationship = x_node.createRelationshipTo(y_node,r_type);
                	relationship.setProperty("weight",relation.getWeight());   	  
            	}   
            	else {
//            		System.out.println("NEO4J:"+termStore.getTermName((int) relation.getX_id())
//            			+":"+termStore.getTermName((int) relation.getY_id()));
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
	  logger.info("Reseting all relationship [OK]");
				
	}

	/**
	 * Flush all relation into @See{relationBuffer} into DB
	 */
	public void flush() {
		if(! relationBuffer.isEmpty()) {
			insertRelations();
			relationBuffer.clear();
		}		
	}
	
	public boolean isFlushed() {
		return relationBuffer.isEmpty();
	}
	
    public void insertNodes() {
    	
    	Instant t1 = Instant.now();
    	
		Map<String,Integer> map = termStore.getTermIndex();
    	Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();   	
		int BATCH_SIZE = 65536*8;
    	int n = map.size() % BATCH_SIZE == 0 ? map.size()/BATCH_SIZE : (map.size()/BATCH_SIZE)+1;
//    	
//    	IndexManager index;
//    	Index<Node> termIndex;
    	
//    	try ( Transaction tx = graph.beginTx() ){  
//    		index = graph.index();
//            termIndex = index.forNodes(termLabel.toString());
//            tx.success();
//    	}   	
        
    	for(int i=0;i<n && it.hasNext();i++) {
    		try ( Transaction tx = graph.beginTx() ){  
    			
    			for(int j=0;j<BATCH_SIZE && it.hasNext();j++) {
    				Map.Entry<String, Integer> pair = it.next();
    				Node node = graph.createNode(termLabel);
        			node.setProperty("_id",pair.getValue());
        			node.setProperty("_name", pair.getKey());
//        			termIndex.add(node, "_name", node.getProperty("_name"));
//        			termIndex.add(node, "_id", node.getProperty("_id"));
    			}
    			tx.success();
    		}
    		logger.info(BATCH_SIZE*(i+1)+" nodes created ");
    	}
    	
    	long ellapsedMs = Duration.between(t1, Instant.now()).toMillis();
    	logger.info(map.size()+" terms inserted [time="+ellapsedMs+"ms");
    	
    }
    
    public void insertRelationship(String dataDir){
    	
    	File dir = new File(dataDir);
    	if(! dir.isDirectory()) {
    		logger.severe("Error "+dataDir+" is not a directory");
    	}
    	File [] relationshipFiles = dir.listFiles(new FilenameFilter() {
    	    @Override
    	    public boolean accept(File dir, String name) {
    	        return name.endsWith(".csv");
    	    }
    	});
    	
    	int BATCH_SIZE = 65536;
    	HashMap<Integer,ArrayList<Integer>> conflicts = termStore.getConflicts();
    		
    	for(File file : relationshipFiles){
    		try {
    			
    			Instant t1 = Instant.now();   			
    			Integer rTypeId = Integer.parseInt(file.getName().split("\\.")[0]);	
    			
				BufferedReader buffReader = new BufferedReader(new FileReader(file));
				/**
				 * get all lines from file and skip first line corresponding to csv header
				 * #FixMe : read file by using buffer without storing whole file into memory
				 */
				List<String> lines = buffReader.lines().skip(1).collect(Collectors.toCollection(ArrayList::new)); 
				int n=lines.size() % BATCH_SIZE == 0 ? lines.size()/BATCH_SIZE : (lines.size()/BATCH_SIZE)+1;
				
				for(int i=0; i<n;i++) { // read the lines and write BATCH_SIZE by BATCH_SIZE relation into DB
					int k = i*BATCH_SIZE;
					try ( Transaction tx = graph.beginTx() ){  
						
						for(int j=k;j<k+BATCH_SIZE && j<lines.size();j++) {
							
							String line = lines.get(j);						
							String[] parts = line.split(",");
							int x_id = Integer.parseInt(parts[0]);
							int y_id = Integer.parseInt(parts[1]);
							short weight = Short.parseShort(parts[2]);
							
							Node xNode = graph.findNode(termLabel, "_id", x_id);
							Node yNode = graph.findNode(termLabel, "_id", y_id);
							if(xNode == null) {
								ArrayList<Integer> otherIds = conflicts.get(x_id);
								if(otherIds != null) {
									for(int idx=0 ;idx<otherIds.size() && xNode == null; idx++) {
										xNode = graph.findNode(termLabel, "_id", otherIds.get(idx));
									}									
								}
							}
							if(yNode == null) {
								ArrayList<Integer> otherIds = conflicts.get(y_id);
								if(otherIds != null) {
									for(int idx=0 ;idx<otherIds.size() && yNode == null; idx++) {
										yNode = graph.findNode(termLabel, "_id", otherIds.get(idx));
									}									
								}
							}
							if(xNode != null && yNode != null) {
								RelationshipType relationshipType = idToRelationshipTypes.get(rTypeId);
								Relationship relationship = xNode.createRelationshipTo(yNode,relationshipType);
								relationship.setProperty("weight", weight);
							}
							else {
								System.out.println(x_id+":"+xNode+":"+y_id+yNode);
							}
							tx.success();
							
						}
					}	
					
				}
				
				buffReader.close();
				long ellapsedTime = Duration.between(t1,Instant.now()).toMillis()/1000;
				System.out.println(rTypeId+".csv "+lines.size()+" relationship inserted [OK] time="+ellapsedTime+"s");
						
			} catch (IOException e) {
				e.printStackTrace();
			}
    			
    	}
    	
    	
    }
    
	
}
