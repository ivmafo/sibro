package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("retentionRateList")
public class RetentionRateList extends EntityQuery<RetentionRate> {

	private static final String EJBQL = "select retentionRate from RetentionRate retentionRate";

	private static final String[] RESTRICTIONS = { "lower(retentionRate.name) like lower(concat(#{retentionRateList.retentionRate.name},'%'))", };

	private RetentionRate retentionRate = new RetentionRate();

	public RetentionRateList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RetentionRate getRetentionRate() {
		return retentionRate;
	}
}
