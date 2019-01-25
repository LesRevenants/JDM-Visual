package core;

/**
 * Class which store information about a query
 * @author user
 *
 */
public abstract class Query {
	
	/** the x word */
	protected int x; 
	
	protected String definition;
	
	

	/** indicate if incoming and/or outcoming relations must be selected or not */
	protected boolean in,out; 
	
	public Query(int x, boolean in, boolean out) {
		super();
		this.x = x;
		this.in = in;
		this.out = out;
	}

	public int getX() {
		return x;
	}

	public boolean isIn() {
		return in;
	}

	public boolean isOut() {
		return out;
	}
	
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getDefinition() {
		return definition;
	}
	
	
	
	
	
	
}
