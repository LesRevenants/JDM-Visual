package Store;

public class Term {

    private int id;
    private String name;
    private boolean isMwe;
    private int weight;

    public Term(int id) {
        this.id = id;
    }

    public Term(int id, String name, boolean isMwe) {
        this.id = id;
        this.name = name;
        this.isMwe = isMwe;
    }

    public int getId() {
        return id;
    }

    public boolean isMwe() {
        return isMwe;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }


}
