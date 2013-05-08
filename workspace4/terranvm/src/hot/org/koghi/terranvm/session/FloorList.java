package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("floorList")
public class FloorList extends EntityQuery<Floor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select floor from Floor floor";

	private static final String[] RESTRICTIONS = {  };

	private Floor floor = new Floor();

	public FloorList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Floor getFloor() {
		return floor;
	}
}
