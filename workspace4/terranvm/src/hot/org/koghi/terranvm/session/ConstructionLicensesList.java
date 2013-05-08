package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.ConstructionLicenses;

@Name("constructionLicensesList")
public class ConstructionLicensesList extends EntityQuery<ConstructionLicenses> {

	private static final String EJBQL = "select constructionLicenses from ConstructionLicenses constructionLicenses";

	private static final String[] RESTRICTIONS = {
			"lower(constructionLicenses.number) like lower(concat(#{constructionLicensesList.constructionLicenses.number},'%'))",
			"lower(constructionLicenses.type) like lower(concat(#{constructionLicensesList.constructionLicenses.type},'%'))",
			"lower(constructionLicenses.description) like lower(concat(#{constructionLicensesList.constructionLicenses.description},'%'))", };

	private ConstructionLicenses constructionLicenses = new ConstructionLicenses();

	public ConstructionLicensesList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConstructionLicenses getConstructionLicenses() {
		return constructionLicenses;
	}
}
