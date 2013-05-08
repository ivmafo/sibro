package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.ConsecutiveAccountsBilling;

@Name("consecutiveAccountsBillingHome")
public class ConsecutiveAccountsBillingHome extends
		EntityHome<ConsecutiveAccountsBilling> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;

	public void setConsecutiveAccountsBillingId(Integer id) {
		setId(id);
	}

	public Integer getConsecutiveAccountsBillingId() {
		return (Integer) getId();
	}

	@Override
	protected ConsecutiveAccountsBilling createInstance() {
		ConsecutiveAccountsBilling consecutiveAccountsBilling = new ConsecutiveAccountsBilling();
		return consecutiveAccountsBilling;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		BusinessEntity businessEntity = businessEntityHome.getDefinedInstance();
		if (businessEntity != null) {
			getInstance().setBusinessEntity(businessEntity);
		}
	}

	public boolean isWired() {
		if (getInstance().getBusinessEntity() == null)
			return false;
		return true;
	}

	public ConsecutiveAccountsBilling getDefinedInstance() {
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
