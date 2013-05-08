package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.Area;

@Name("areaList")
public class AreaList extends EntityQuery<Area> {

	private static final String EJBQL = "select area from Area area";

	private static final String[] RESTRICTIONS = { "lower(area.name) like lower(concat(#{areaList.area.name},'%'))", };

	private Area area = new Area();

	public AreaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Area getArea() {
		return area;
	}
}
