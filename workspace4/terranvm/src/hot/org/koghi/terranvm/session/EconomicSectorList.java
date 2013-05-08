package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("economicSectorList")
public class EconomicSectorList extends EntityQuery<EconomicSector> {

	private static final String EJBQL = "select economicSector from EconomicSector economicSector";

	private static final String[] RESTRICTIONS = { "lower(economicSector.name) like lower(concat(#{economicSectorList.economicSector.name},'%'))", "lower(economicSector.description) like lower(concat(#{economicSectorList.economicSector.description},'%'))", };

	private EconomicSector economicSector = new EconomicSector();

	public EconomicSectorList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EconomicSector getEconomicSector() {
		return economicSector;
	}
}
