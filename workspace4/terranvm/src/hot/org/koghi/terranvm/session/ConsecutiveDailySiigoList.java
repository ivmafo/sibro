package org.koghi.terranvm.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.ConsecutiveDailySiigo;

import java.util.Arrays;

@Name("consecutiveDailySiigoList")
public class ConsecutiveDailySiigoList extends EntityQuery<ConsecutiveDailySiigo> {

	private static final String EJBQL = "select consecutiveDailySiigo from ConsecutiveDailySiigo consecutiveDailySiigo";

	private static final String[] RESTRICTIONS = { "lower(consecutiveDailySiigo.type) like lower(concat(#{consecutiveDailySiigoList.consecutiveDailySiigo.type},'%'))", };

	private ConsecutiveDailySiigo consecutiveDailySiigo = new ConsecutiveDailySiigo();

	public ConsecutiveDailySiigoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConsecutiveDailySiigo getConsecutiveDailySiigo() {
		return consecutiveDailySiigo;
	}
}
