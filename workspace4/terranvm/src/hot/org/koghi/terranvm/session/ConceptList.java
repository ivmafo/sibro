package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("conceptList")
public class ConceptList extends EntityQuery<Concept> {

	private static final String EJBQL = "select concept from Concept concept";

	private static final String[] RESTRICTIONS = { "lower(concept.description) like lower(concat(#{conceptList.concept.description},'%'))","lower(concept.name) like lower(concat(#{conceptList.concept.name},'%'))", };

	private Concept concept = new Concept();

	public ConceptList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Concept getConcept() {
		return concept;
	}
}
