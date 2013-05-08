package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;

@Name("retentionRateHome")
public class RetentionRateHome extends EntityHome<RetentionRate> {

	public void setRetentionRateId(Integer id) {
		setId(id);
	}

	public Integer getRetentionRateId() {
		return (Integer) getId();
	}

	@Override
	protected RetentionRate createInstance() {
		RetentionRate retentionRate = new RetentionRate();
		return retentionRate;
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

	public RetentionRate getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RetentionRateAccount> getRetentionRateAccounts() {
		return getInstance() == null ? null : new ArrayList<RetentionRateAccount>(getInstance().getRetentionRateAccounts());
	}

	public List<TaxConfiguration> getTaxConfigurations() {
		return getInstance() == null ? null : new ArrayList<TaxConfiguration>(getInstance().getTaxConfigurations());
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
