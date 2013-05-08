package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;

@Name("contractTypeHome")
public class ContractTypeHome extends EntityHome<ContractType> {

	public void setContractTypeId(Integer id) {
		setId(id);
	}

	public Integer getContractTypeId() {
		return (Integer) getId();
	}

	@Override
	protected ContractType createInstance() {
		ContractType contractType = new ContractType();
		return contractType;
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

	public ContractType getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProjectProperty> getProjectProperties() {
		return getInstance() == null ? null : new ArrayList<ProjectProperty>(
				getInstance().getProjectProperties());
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
