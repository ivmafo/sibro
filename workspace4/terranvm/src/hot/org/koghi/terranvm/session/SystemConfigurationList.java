package org.koghi.terranvm.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.SystemConfiguration;

import java.util.Arrays;

@Name("systemConfigurationList")
public class SystemConfigurationList extends EntityQuery<SystemConfiguration> {

	private static final String EJBQL = "select systemConfiguration from SystemConfiguration systemConfiguration";

	private static final String[] RESTRICTIONS = { "lower(systemConfiguration.name) like lower(concat(#{systemConfigurationList.systemConfiguration.name},'%'))", "lower(systemConfiguration.value) like lower(concat(#{systemConfigurationList.systemConfiguration.value},'%'))", };

	private SystemConfiguration systemConfiguration = new SystemConfiguration();

	public SystemConfigurationList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SystemConfiguration getSystemConfiguration() {
		return systemConfiguration;
	}
}
