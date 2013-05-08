package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("retentionRateAccountList")
public class RetentionRateAccountList extends EntityQuery<RetentionRateAccount> {

	private static final String EJBQL = "select retentionRateAccount from RetentionRateAccount retentionRateAccount";

	private static final String[] RESTRICTIONS = { "lower(retentionRateAccount.name) like lower(concat(#{retentionRateAccountList.retentionRateAccount.name},'%'))", "lower(retentionRateAccount.account) like lower(concat(#{retentionRateAccountList.retentionRateAccount.account},'%'))", };

	private RetentionRateAccount retentionRateAccount = new RetentionRateAccount();

	public RetentionRateAccountList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RetentionRateAccount getRetentionRateAccount() {
		return retentionRateAccount;
	}
}
