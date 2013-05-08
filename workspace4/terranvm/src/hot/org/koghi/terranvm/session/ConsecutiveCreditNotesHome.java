package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.ConsecutiveCreditNotes;

@Name("consecutiveCreditNotesHome")
public class ConsecutiveCreditNotesHome extends
		EntityHome<ConsecutiveCreditNotes> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;

	public void setConsecutiveCreditNotesId(Integer id) {
		setId(id);
	}

	public Integer getConsecutiveCreditNotesId() {
		return (Integer) getId();
	}

	@Override
	protected ConsecutiveCreditNotes createInstance() {
		ConsecutiveCreditNotes consecutiveCreditNotes = new ConsecutiveCreditNotes();
		return consecutiveCreditNotes;
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

	public ConsecutiveCreditNotes getDefinedInstance() {
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
