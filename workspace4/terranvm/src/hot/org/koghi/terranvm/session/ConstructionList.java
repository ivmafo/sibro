package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("constructionList")
public class ConstructionList extends EntityQuery<Construction> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select construction from Construction construction";

	private static final String[] RESTRICTIONS = { "lower(construction.name) like lower(concat(#{constructionList.construction.name},'%'))", "lower(construction.description) like lower(concat(#{constructionList.construction.description},'%'))", };

	private Construction construction = new Construction();

	public ConstructionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Construction getConstruction() {
		return construction;
	}
}
