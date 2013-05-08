package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("concepTemplateList")
public class ConcepTemplateList extends EntityQuery<ConcepTemplate> {

	private static final String EJBQL = "select concepTemplate from ConcepTemplate concepTemplate";

	private static final String[] RESTRICTIONS = { "lower(concepTemplate.name) like lower(concat(#{concepTemplateList.concepTemplate.name},'%'))", "lower(concepTemplate.printDescription) like lower(concat(#{concepTemplateList.concepTemplate.printDescription},'%'))", "lower(concepTemplate.expression) like lower(concat(#{concepTemplateList.concepTemplate.expression},'%'))", };

	private ConcepTemplate concepTemplate = new ConcepTemplate();

	public ConcepTemplateList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConcepTemplate getConcepTemplate() {
		return concepTemplate;
	}
}
