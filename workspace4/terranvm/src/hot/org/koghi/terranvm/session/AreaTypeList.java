package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.AreaType;

import java.util.Arrays;

@Name("areaTypeList")
public class AreaTypeList extends EntityQuery<AreaType> {

	private static final String EJBQL = "select areaType from AreaType areaType";

	private static final String[] RESTRICTIONS = { "lower(areaType.name) like lower(concat(#{areaTypeList.areaType.name},'%'))", };

	private AreaType areaType = new AreaType();

	public AreaTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AreaType getAreaType() {
		return areaType;
	}
}
