package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.PayFormType;

@Name("payFormTypeList")
public class PayFormTypeList extends EntityQuery<PayFormType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String EJBQL = "select payFormType from PayFormType payFormType";

	private static final String[] RESTRICTIONS = { "lower(payFormType.name) like lower(concat(#{payFormTypeList.payFormType.name},'%'))", };

	private PayFormType payFormType = new PayFormType();

	public PayFormTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PayFormType getPayFormType() {
		return payFormType;
	}
}
