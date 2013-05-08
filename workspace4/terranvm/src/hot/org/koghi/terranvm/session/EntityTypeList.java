package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("entityTypeList")
public class EntityTypeList extends EntityQuery<EntityType> {

	private static final String EJBQL = "select entityType from EntityType entityType";

	private static final String[] RESTRICTIONS = {};

	private EntityType entityType = new EntityType();

	public EntityTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EntityType getEntityType() {
		return entityType;
	}
}
