package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("businessEntityTypeList")
public class BusinessEntityTypeList extends EntityQuery<BusinessEntityType> {

	private static final String EJBQL = "select businessEntityType from BusinessEntityType businessEntityType";

	private static final String[] RESTRICTIONS = { "lower(businessEntityType.nameBusinessEntityType) like lower(concat(#{businessEntityTypeList.businessEntityType.nameBusinessEntityType},'%'))", "lower(businessEntityType.description) like lower(concat(#{businessEntityTypeList.businessEntityType.description},'%'))", };

	private BusinessEntityType businessEntityType = new BusinessEntityType();

	public BusinessEntityTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public BusinessEntityType getBusinessEntityType() {
		return businessEntityType;
	}
}
