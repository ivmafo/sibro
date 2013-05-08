package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("rentableUnitList")
public class RentableUnitList extends EntityQuery<RentableUnit> {

	private static final String EJBQL = "select rentableUnit from RentableUnit rentableUnit";
	private static final String[] RESTRICTIONS = {"lower(rentableUnit.name) like lower(concat(#{rentableUnitList.rentableUnit.name},'%'))", "lower(rentableUnit.meters) like lower(concat(#{rentableUnit.rentableUnit.meters},'%'))", };
	private RentableUnit rentableUnit = new RentableUnit();
	public RentableUnitList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}
	public RentableUnit getRentableUnit() {
		return rentableUnit;
	}
}
