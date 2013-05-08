package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("contributionModuleList")
public class ContributionModuleList extends EntityQuery<ContributionModule> {

	private static final String EJBQL = "select contributionModule from ContributionModule contributionModule";

	private static final String[] RESTRICTIONS = { "lower(contributionModule.name) like lower(concat(#{contributionModuleList.contributionModule.name},'%'))", };

	private ContributionModule contributionModule = new ContributionModule();

	public ContributionModuleList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ContributionModule getContributionModule() {
		return contributionModule;
	}
}
