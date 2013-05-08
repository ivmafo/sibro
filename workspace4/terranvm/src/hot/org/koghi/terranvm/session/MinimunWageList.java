package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.MinimunWage;

@Name("minimunWageList")
public class MinimunWageList extends EntityQuery<MinimunWage>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String EJBQL =  "from MinimunWage minimunWage";
	
	private static final String[] RESTRICTIONS = {};
	
	private MinimunWage minimunWage = new MinimunWage();
	
	public MinimunWageList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MinimunWage getMinumunWage() {
		return minimunWage;
	}


}
