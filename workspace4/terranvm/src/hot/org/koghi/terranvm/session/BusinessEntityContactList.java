package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("businessEntityContactList")
public class BusinessEntityContactList extends EntityQuery<BusinessEntityContact> {

	private static final String EJBQL = "select businessEntityContact from BusinessEntityContact businessEntityContact";

	private static final String[] RESTRICTIONS = {};

	private BusinessEntityContact businessEntityContact = new BusinessEntityContact();

	public BusinessEntityContactList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public BusinessEntityContact getBusinessEntityContact() {
		return businessEntityContact;
	}
}
