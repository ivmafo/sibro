package org.koghi.terranvm.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.entity.SystemConfiguration;

@Name("systemConfigurationHome")
public class SystemConfigurationHome extends EntityHome<SystemConfiguration> {

	public void setSystemConfigurationId(Integer id) {
		setId(id);
	}

	public Integer getSystemConfigurationId() {
		return (Integer) getId();
	}

	@Override
	protected SystemConfiguration createInstance() {
		SystemConfiguration systemConfiguration = new SystemConfiguration();
		return systemConfiguration;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public SystemConfiguration getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
		}
	}
}
