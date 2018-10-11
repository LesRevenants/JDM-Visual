USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM 'file:///load.csv' AS Term
CREATE (t:Term { id : toInteger(Term.id) , name : Term.name });
