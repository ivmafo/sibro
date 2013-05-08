package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("realPropertyContactList")
public class RealPropertyContactList extends EntityQuery<RealPropertyContact> {

	private static final String EJBQL = "select realPropertyContact from RealPropertyContact realPropertyContact";

	private static final String[] RESTRICTIONS = {};

	private RealPropertyContact realPropertyContact = new RealPropertyContact();

	public RealPropertyContactList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RealPropertyContact getRealPropertyContact() {
		return realPropertyContact;
	}
}
