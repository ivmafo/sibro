package org.koghi.terranvm.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.CounterTemplateHasRetentionRateAccount;

import java.util.Arrays;

@Name("counterTemplateHasRetentionRateAccountList")
public class CounterTemplateHasRetentionRateAccountList extends EntityQuery<CounterTemplateHasRetentionRateAccount> {

	private static final String EJBQL = "select counterTemplateHasRetentionRateAccount from CounterTemplateHasRetentionRateAccount counterTemplateHasRetentionRateAccount";

	private static final String[] RESTRICTIONS = {};

	private CounterTemplateHasRetentionRateAccount counterTemplateHasRetentionRateAccount = new CounterTemplateHasRetentionRateAccount();

	public CounterTemplateHasRetentionRateAccountList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CounterTemplateHasRetentionRateAccount getCounterTemplateHasRetentionRateAccount() {
		return counterTemplateHasRetentionRateAccount;
	}
}
