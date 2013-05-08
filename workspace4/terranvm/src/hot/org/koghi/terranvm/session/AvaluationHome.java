package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.entity.Avaluation;
import org.koghi.terranvm.entity.AvaluationType;
import org.koghi.terranvm.entity.Construction;
import org.koghi.terranvm.entity.RealProperty;

@Name("avaluationHome")
public class AvaluationHome extends EntityHome<Avaluation> {

	@In(create = true)
	RealPropertyHome realPropertyHome;
	@In(create = true)
	AvaluationTypeHome avaluationTypeHome;
	@In(create = true)
	ConstructionHome constructionHome;


	public void setAvaluationId(Integer id) {
		setId(id);
	}

	public Integer getAvaluationId() {
		return (Integer) getId();
	}

	@Override
	protected Avaluation createInstance() {
		Avaluation avaluation = new Avaluation();
		return avaluation;
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
		AvaluationType avaluationType = avaluationTypeHome.getDefinedInstance();
		if (avaluationType != null) {
			getInstance().setAvaluationType(avaluationType);
		}
		Construction construction = constructionHome.getDefinedInstance();
		if (construction != null) {
			getInstance().setConstruction(construction);
		}
	}

	public boolean isWired() {
		if (getInstance().getAvaluationType() == null)
			return false;
		return true;
	}

	public Avaluation getDefinedInstance() {
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
