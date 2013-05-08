package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.SegmentStage;

@Name("segmentStageHome")
public class SegmentStageHome extends EntityHome<SegmentStage> {

	@In(create = true)
	RealPropertyHome realPropertyHome;

	public void setSegmentStageId(Integer id) {
		setId(id);
	}

	public Integer getSegmentStageId() {
		return (Integer) getId();
	}

	@Override
	protected SegmentStage createInstance() {
		SegmentStage segmentStage = new SegmentStage();
		return segmentStage;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		RealProperty realProperty = realPropertyHome.getDefinedInstance();
		if (realProperty != null) {
			getInstance().setRealProperty(realProperty);
		}
	}

	public boolean isWired() {
		if (getInstance().getRealProperty() == null)
			return false;
		return true;
	}

	public SegmentStage getDefinedInstance() {
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
