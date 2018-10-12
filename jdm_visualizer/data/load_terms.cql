USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM 'file:///load_terms.csv' AS termes
CREATE (t:Term {id : toInteger(termes.id), name : termes.name});

CREATE INDEX ON :Term(id);
CREATE INDEX ON :Term(name);