package core;

public class Relation {
	
	private int id;
	private String type;
	private String x_id;
	private String y_id;
	private int weight;
	
	
	public Relation(int id, String type, String x_id, String y_id, int weight) {
		super();
		this.id = id;
		this.type = type;
		this.x_id = x_id;
		this.y_id = y_id;
		this.weight = weight;
	}
	
	public int getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public String getX_id() {
		return x_id;
	}
	public String getY_id() {
		return y_id;
	}
	public int getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Relation [id=");
		builder.append(id);
		builder.append(", type=");
		builder.append(type);
		builder.append(", x_id=");
		builder.append(x_id);
		builder.append(", y_id=");
		builder.append(y_id);
		builder.append(", weight=");
		builder.append(weight);
		builder.append("]");
		return builder.toString();
	}
		
	
	
	

}
