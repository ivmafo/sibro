package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("legalNatureOfPropertyList")
public class LegalNatureOfPropertyList extends EntityQuery<LegalNatureOfProperty> {

	private static final String EJBQL = "select legalNatureOfProperty from LegalNatureOfProperty legalNatureOfProperty";

	private static final String[] RESTRICTIONS = { "lower(legalNatureOfProperty.description) like lower(concat(#{legalNatureOfPropertyList.legalNatureOfProperty.description},'%'))", "lower(legalNatureOfProperty.name) like lower(concat(#{legalNatureOfPropertyList.legalNatureOfProperty.name},'%'))", };

	private LegalNatureOfProperty legalNatureOfProperty = new LegalNatureOfProperty();

	public LegalNatureOfPropertyList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public LegalNatureOfProperty getLegalNatureOfProperty() {
		return legalNatureOfProperty;
	}
}
