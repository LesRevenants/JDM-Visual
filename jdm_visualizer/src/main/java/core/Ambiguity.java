package core;

import java.util.LinkedList;

/**
 * organe (anatomie) -> { id(organe (anatomie), id(orgna)e, { id(anatomie) }
 * @author rcolin
 *
 */

public class Ambiguity {
	
	int ambigiousTermId;
	int rootTermId;
	LinkedList<Integer> refTermIds;
	
	
	public Ambiguity(int ambigiousTermId, int rootTermId, LinkedList<Integer> refTermIds) {
		super();
		this.ambigiousTermId = ambigiousTermId;
		this.rootTermId = rootTermId;
		this.refTermIds = refTermIds;
	}


	public Integer getAmbigiousTermId() {
		return ambigiousTermId;
	}


	public Integer getRootTermId() {
		return rootTermId;
	}


	public LinkedList<Integer> getRefTermIds() {
		return refTermIds;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ambigiousTermId;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ambiguity other = (Ambiguity) obj;
		if (ambigiousTermId != other.ambigiousTermId)
			return false;
		return true;
	}


	
	
	
	
	
	
	
	

}
