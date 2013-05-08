package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cityList")
public class CityList extends EntityQuery<City> {

	private static final String EJBQL = "select city from City city";

	private static final String[] RESTRICTIONS = { "lower(city.name) like lower(concat(#{cityList.city.name},'%'))", };

	private City city = new City();

	public CityList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public City getCity() {
		return city;
	}
}
