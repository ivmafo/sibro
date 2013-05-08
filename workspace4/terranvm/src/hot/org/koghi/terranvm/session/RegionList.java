package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("regionList")
public class RegionList extends EntityQuery<Region> {

	private static final String EJBQL = "select region from Region region";

	private static final String[] RESTRICTIONS = { "lower(region.name) like lower(concat(#{regionList.region.name},'%'))", "lower(region.abbreviation) like lower(concat(#{regionList.region.abbreviation},'%'))", };

	private Region region = new Region();

	public RegionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Region getRegion() {
		return region;
	}
}
