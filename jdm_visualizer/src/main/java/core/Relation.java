package core;

import requeterRezo.Voisin;

public class Relation {
	
	private int id;
	private int type;
	private long x_id;
	private long y_id;
	private double weight;
	private boolean x_to_y;
	private Voisin voisin;
	
	
	public Voisin getVoisin() {
		return voisin;
	}

	public Relation(int id, int type, long x_id, long y_id, double weight) {
		super();
		this.id = id;
		this.type = type;
		this.x_id = x_id;
		this.y_id = y_id;
		this.weight = weight;
	}
	
	public Relation(long x_id, Voisin voisin,boolean x_to_y){
		this.x_id = x_id;
		this.voisin = voisin;
		this.x_to_y = x_to_y;
	}
	
	public Relation(int id, int type, long x_id, long y_id, double weight, boolean x_to_y) {
		super();
		this.id = id;
		this.type = type;
		this.x_id = x_id;
		this.y_id = y_id;
		this.weight = weight;
		this.x_to_y = x_to_y;
	}

	public int getId() {
		return id;
	}
	public int getType() {
		return type;
	}
	public long getX_id() {
		return x_id;
	}
	public long getY_id() {
		return y_id;
	}
	public double getWeight() {
		return weight;
	}
	
	

	public boolean isX_to_y() {
		return x_to_y;
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
