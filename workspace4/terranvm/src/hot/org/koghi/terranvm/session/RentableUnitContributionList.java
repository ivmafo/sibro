package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("rentableUnitContributionList")
public class RentableUnitContributionList extends EntityQuery<RentableUnitContribution> {

	private static final String EJBQL = "select rentableUnitContribution from RentableUnitContribution rentableUnitContribution";

	private static final String[] RESTRICTIONS = {};

	private RentableUnitContribution rentableUnitContribution = new RentableUnitContribution();

	public RentableUnitContributionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RentableUnitContribution getRentableUnitContribution() {
		return rentableUnitContribution;
	}
}
