package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("realPropertyUseTypeList")
public class RealPropertyUseTypeList extends EntityQuery<RealPropertyUseType> {

	private static final String EJBQL = "select realPropertyUseType from RealPropertyUseType realPropertyUseType";

	private static final String[] RESTRICTIONS = { "lower(realPropertyUseType.name) like lower(concat(#{realPropertyUseTypeList.realPropertyUseType.name},'%'))", "lower(realPropertyUseType.description) like lower(concat(#{realPropertyUseTypeList.realPropertyUseType.description},'%'))", };

	private RealPropertyUseType realPropertyUseType = new RealPropertyUseType();

	public RealPropertyUseTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RealPropertyUseType getRealPropertyUseType() {
		return realPropertyUseType;
	}
}
