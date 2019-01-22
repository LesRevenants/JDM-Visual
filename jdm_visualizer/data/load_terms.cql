USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM 'file:///terms.csv' AS termes
CREATE (t:Term {_id : toInteger(termes.id), _name : termes.name , _cached : (case termes.cached when '1' then true else false end)});

CREATE INDEX ON :Term(id);
CREATE INDEX ON :Term(name);
