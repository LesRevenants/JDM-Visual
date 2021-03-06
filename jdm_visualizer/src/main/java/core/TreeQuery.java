package core;

import java.util.HashMap;
import java.util.Set;

/**
 * Special of type of query (x, { (r1,{t11,t12,t1k}), (r2,{t21, t22, t2l }) } )
 * example : 
 * 		?x r_isa {animal,voiture}
 *      ?x r_pos {adv,nom}
 *      ?x r_association { requin, tortue, autoroute }
 *
 */
public class TreeQuery extends Query{
	
	/** List of terms associated to each relation type */
	private HashMap<Long,Set<Long>> patterns;

	
	public TreeQuery(int x, boolean in, boolean out,HashMap<Long,Set<Long>> patterns) {
		super(x, in, out);
		this.patterns = patterns;
	}

	public HashMap<Long,Set<Long>> getPatterns() {
		return patterns;
	}
	
	

	
	
}
