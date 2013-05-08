package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("phoneNumberList")
public class PhoneNumberList extends EntityQuery<PhoneNumber> {

	private static final String EJBQL = "select phoneNumber from PhoneNumber phoneNumber";

	private static final String[] RESTRICTIONS = { "lower(phoneNumber.number) like lower(concat(#{phoneNumberList.phoneNumber.number},'%'))", "lower(phoneNumber.indicative) like lower(concat(#{phoneNumberList.phoneNumber.indicative},'%'))", "lower(phoneNumber.extension) like lower(concat(#{phoneNumberList.phoneNumber.extension},'%'))", };

	private PhoneNumber phoneNumber = new PhoneNumber();

	public PhoneNumberList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}
}
