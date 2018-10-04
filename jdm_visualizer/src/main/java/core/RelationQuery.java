package core;

import java.util.Set;

public class RelationQuery {

    private String x;

    private Set<String> term_searched;

    private boolean in,out;

    public Set<String> relations_searched;


    public RelationQuery(String x,Set<String> term_searched, boolean in, boolean out, Set<String> relations_searched) {
        this.x = x;
        this.term_searched = term_searched;
        this.in = in;
        this.out = out;
        this.relations_searched = relations_searched;
    }


    public String getX() {
        return x;
    }

    public Set<String> getTerm_searched() {
        return term_searched;
    }

    public boolean isIn() {
        return in;
    }

    public boolean isOut() {
        return out;
    }

    public Set<String> getRelations_searched() {
        return relations_searched;
    }

    @Override
    public String toString() {
        return "RelationQuery{" +
                "x='" + x + '\'' +
                ", term_searched=" + term_searched +
                ", in=" + in +
                ", out=" + out +
                ", relations_searched=" + relations_searched +
                '}';
    }
}
