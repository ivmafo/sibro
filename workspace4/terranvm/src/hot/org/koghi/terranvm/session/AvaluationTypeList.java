package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.AvaluationType;

@Name("avaluationTypeList")
public class AvaluationTypeList extends EntityQuery<AvaluationType> {

	private static final String EJBQL = "select avaluationType from AvaluationType avaluationType";

	private static final String[] RESTRICTIONS = { "lower(avaluationType.name) like lower(concat(#{avaluationTypeList.avaluationType.name},'%'))", };

	private AvaluationType avaluationType = new AvaluationType();

	public AvaluationTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AvaluationType getAvaluationType() {
		return avaluationType;
	}
}
