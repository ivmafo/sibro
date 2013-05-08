package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("economicActivityList")
public class EconomicActivityList extends EntityQuery<EconomicActivity> {

	private static final String EJBQL = "select economicActivity from EconomicActivity economicActivity";

	private static final String[] RESTRICTIONS = { "lower(economicActivity.name) like lower(concat(#{economicActivityList.economicActivity.name},'%'))", "lower(economicActivity.description) like lower(concat(#{economicActivityList.economicActivity.description},'%'))", "lower(economicActivity.code) like lower(concat(#{economicActivityList.economicActivity.code},'%'))", };

	private EconomicActivity economicActivity = new EconomicActivity();

	public EconomicActivityList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EconomicActivity getEconomicActivity() {
		return economicActivity;
	}
}
