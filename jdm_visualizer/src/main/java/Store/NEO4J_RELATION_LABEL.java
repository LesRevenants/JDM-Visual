package Store;

import org.neo4j.graphdb.RelationshipType;

public enum NEO4J_RELATION_LABEL implements RelationshipType {
	
	R_ASSOCIATED,
	R_RAFF_SEM,
	R_RAFF_MORPHO,
	R_DOMAIN,
	R_POS,
	R_SYN,
	R_ISA,
	R_ANTO,
	R_HYPO,
	R_HAS_PART,
	R_HOLO,
	R_LOCUTION,
	R_FLPOT,
	R_AGENT,
	R_PATIENT,
	R_LIEU,
	R_INSTR,
	R_CARAC,
	R_DATA,
	R_LEMMA,
	R_HAS_MAGN,
	R_HAS_ANTIMAGN,
	R_FAMILY,
	R_CARAC_1,
	R_AGENT_1,
	R_INSTR_1,
	R_PATIENT_1,
	R_DOMAIN_1,
	R_LIEU_1,
	R_CHUNK_PRED,
	R_LIEU_ACTION,
	R_ACTION_LIEU,
	R_SENTIMENT,
	R_ERROR,
	R_MANNER,
	R_MEANING,
	R_INFOPOT,
	R_TELIC_ROLE,
	R_AGENTIF_ROLE,
	R_VERBE_ACTION,
	R_ACTION_VERBE,
	R_CONSEQ,
	R_CAUSATIF,
	R_ADJ_VERBE,
	R_VERBE_ADJ,
	R_CHUNK_SUJET,
	R_CHUNK_OBJET,
	R_CHUNK_LOC,
	R_CHUNK_INSTR,
	R_TIME,
	R_OBJECT_MATER,
	R_MATER_OBJECT,
	R_SUCCESSEUR_TIME,
	R_MAKE,
	R_PRODUCT_OF,
	R_AGAINST,
	R_AGAINST_1,
	R_IMPLICATION,
	R_QUANTIFICATEUR,
	R_MASC,
	R_FEM,
	R_EQUIV,
	R_MANNER_1,
	R_AGENTIVE_IMPLICATION,
	R_HAS_INSTANCE,
	R_VERB_REAL,
	R_CHUNK_HEAD,
	R_SIMILAR,
	R_SET_ITEM,
	R_ITEM_SET,
	R_PROCESSUS_AGENT,
	R_VARIANTE,
	R_SYN_STRICT,
	R_IS_SMALLER_THAN,
	R_IS_BIGGER_THAN,
	R_ACCOMP,
	R_PROCESSUS_PATIENT,
	R_VERB_PPAS,
	R_COHYPO,
	R_VERB_PPRE,
	R_DER_MORPHO,
	R_HAS_AUTEUR,
	R_HAS_PERSONNAGE,
	R_CAN_EAT,
	R_HAS_ACTORS,
	R_DEPLAC_MODE,
	R_HAS_INTERPRET,
	R_COLOR,
	R_CIBLE,
	R_SYMPTOMES,
	R_PREDECESSEUR_TIME,
	R_DIAGNOSTIQUE,
	R_PREDECESSEUR_SPACE,
	R_SUCCESSEUR_SPACE,
	R_SOCIAL_TIE,
	R_TRIBUTARY,
	R_SENTIMENT_1,
	R_LINKED_WITH,
	R_FONCTEUR,
	R_COMPARISON,
	R_BUT,
	R_BUT_1,
	R_OWN,
	R_OWN_1,
	R_VERB_AUX,
	R_PREDECESSEUR_LOGIC,
	R_SUCCESSEUR_LOGIC,
	R_ISA_INCOMPATIBLE,
	R_INCOMPATIBLE,
	R_NODE2RELNODE,
	R_REQUIRE,
	R_IS_INSTANCE_OF,
	R_IS_CONCERNED_BY,
	R_COMPL_AGENT,
	R_BENEFICIAIRE,
	R_DESCEND_DE,
	R_DOMAIN_SUBST,
	R_PROP,
	R_ACTIV_VOICE,
	R_MAKE_USE_OF,
	R_IS_USED_BY,
	R_ADJ_NOMPROP,
	R_NOMPROP_ADJ,
	R_ADJ_ADV,
	R_ADV_ADJ,
	R_HOMOPHONE,
	R_POTENTIAL_CONFUSION,
	R_CONCERNING,
	R_ADJ_NOM,
	R_NOM_ADJ,
	R_TRANSLATION,
	R_LINK,
	R_COOCCURRENCE,
	R_AKI,
	R_WIKI,
	R_ANNOTATION_EXCEPTION,
	R_ANNOTATION,
	R_INHIB,
	R_PREV,
	R_SUCC,
	R_TERMGROUP,
	R_RAFF_SEM_1,
	R_LEARNING_MODEL,
}

