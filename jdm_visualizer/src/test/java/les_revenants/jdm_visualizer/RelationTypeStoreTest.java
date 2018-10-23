package les_revenants.jdm_visualizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import Store.NEO4J_RELATION_LABEL;
import Store.RelationTypeStore;
import configuration.MasterStore;

public class RelationTypeStoreTest {

	public static MasterStore masterStore;
	
//	 @BeforeAll
//	 static void setUp() throws IOException, SQLException {
//		 masterStore = new MasterStore("data/config.json");
//	 }
//	 
//	 
//	 @Test
//	    public void testRelationTypeStore() {
//	    	RelationTypeStore store = masterStore.getRelationTypeStore();
//	    	Collection<Integer> ids = store.getIds();
//	    	Collection<String> names = store.getNames();
//	    	assert(ids.size() == 143);
//	    	assert(NEO4J_RELATION_LABEL.values().length == ids.size());
//	    	assert(names.size() == ids.size());      	
//	    }
	
	@Test
	public void testRelationTypeCreation() throws IOException {
		RelationTypeStore.buildRelationEntries("data/relation_types.txt", "data/relation_ids.txt","data/relations.txt");
	}
	 
}
