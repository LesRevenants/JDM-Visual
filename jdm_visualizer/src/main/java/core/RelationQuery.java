package core;

import java.util.Set;

public class RelationQuery {

    private int x;

    private Set<Long> term_searched;

    private boolean in,out;

    public Set<Integer> relations_searched;


    public RelationQuery(int x,Set<Long> term_searched, boolean in, boolean out, Set<Integer> relations_searched) {
        this.x = x;
        this.term_searched = term_searched;
        this.in = in;
        this.out = out;
        this.relations_searched = relations_searched;
    }
    

    public int getX() {
        return x;
    }

    public Set<Long> getTerm_searched() {
        return term_searched;
    }

    public boolean isIn() {
        return in;
    }

    public boolean isOut() {
        return out;
    }

    public Set<Integer> getRelations_searched() {
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
