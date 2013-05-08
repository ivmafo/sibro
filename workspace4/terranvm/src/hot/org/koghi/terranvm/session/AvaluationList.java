package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("avaluationList")
public class AvaluationList extends EntityQuery<Avaluation> {

	private static final String EJBQL = "select avaluation from Avaluation avaluation";

	private static final String[] RESTRICTIONS = {};

	private Avaluation avaluation = new Avaluation();

	public AvaluationList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Avaluation getAvaluation() {
		return avaluation;
	}
}
