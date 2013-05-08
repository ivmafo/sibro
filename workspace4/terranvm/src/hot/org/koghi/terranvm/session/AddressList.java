package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("addressList")
public class AddressList extends EntityQuery<Address> {

	private static final String EJBQL = "select address from Address address";

	private static final String[] RESTRICTIONS = { "lower(address.kindOfWay) like lower(concat('%',concat(#{addressList.address.kindOfWay},'%')))", "lower(address.wayLetter) like lower(concat(#{addressList.address.wayLetter},'%'))", "lower(address.wayBisLetter) like lower(concat(#{addressList.address.wayBisLetter},'%'))", "lower(address.wayEastOrSouth) like lower(concat(#{addressList.address.wayEastOrSouth},'%'))", "lower(address.numberLetter) like lower(concat(#{addressList.address.numberLetter},'%'))", "lower(address.numberEastOrSouth) like lower(concat(#{addressList.address.numberEastOrSouth},'%'))", "lower(address.other) like lower(concat('%' ,concat(#{addressList.address.other},'%')))", };

	private Address address = new Address();

	public AddressList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Address getAddress() {
		return address;
	}
}
