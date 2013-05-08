package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.ConsecutiveAccountsBilling;

@Name("consecutiveAccountsBillingList")
public class ConsecutiveAccountsBillingList extends
		EntityQuery<ConsecutiveAccountsBilling> {

	private static final String EJBQL = "select consecutiveAccountsBilling from ConsecutiveAccountsBilling consecutiveAccountsBilling";

	private static final String[] RESTRICTIONS = {
			"lower(consecutiveAccountsBilling.suffix) like lower(concat(#{consecutiveAccountsBillingList.consecutiveAccountsBilling.suffix},'%'))",
			"lower(consecutiveAccountsBilling.prefix) like lower(concat(#{consecutiveAccountsBillingList.consecutiveAccountsBilling.prefix},'%'))",
			"lower(consecutiveAccountsBilling.siigoCode) like lower(concat(#{consecutiveAccountsBillingList.consecutiveAccountsBilling.siigoCode},'%'))", };

	private ConsecutiveAccountsBilling consecutiveAccountsBilling = new ConsecutiveAccountsBilling();

	public ConsecutiveAccountsBillingList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConsecutiveAccountsBilling getConsecutiveAccountsBilling() {
		return consecutiveAccountsBilling;
	}
}
