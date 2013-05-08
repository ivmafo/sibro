package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("countryList")
public class CountryList extends EntityQuery<Country> {

	private static final String EJBQL = "select country from Country country";

	private static final String[] RESTRICTIONS = { "lower(country.name) like lower(concat(#{countryList.country.name},'%'))", "lower(country.abbreviation) like lower(concat(#{countryList.country.abbreviation},'%'))", };

	private Country country = new Country();

	public CountryList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Country getCountry() {
		return country;
	}
}
