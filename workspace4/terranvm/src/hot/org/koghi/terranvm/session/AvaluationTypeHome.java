package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.entity.Avaluation;
import org.koghi.terranvm.entity.AvaluationType;

@Name("avaluationTypeHome")
public class AvaluationTypeHome extends EntityHome<AvaluationType> {

	public void setAvaluationTypeId(Integer id) {
		setId(id);
	}

	public Integer getAvaluationTypeId() {
		return (Integer) getId();
	}

	@Override
	protected AvaluationType createInstance() {
		AvaluationType avaluationType = new AvaluationType();
		return avaluationType;
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

	public AvaluationType getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Avaluation> getAvaluations() {
		return getInstance() == null ? null : new ArrayList<Avaluation>(getInstance().getAvaluations());
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
