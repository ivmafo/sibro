package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;

@Name("entityTypeHome")
public class EntityTypeHome extends EntityHome<EntityType> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;
	@In(create = true)
	BusinessEntityTypeHome businessEntityTypeHome;

	public void setEntityTypeId(Integer id) {
		setId(id);
	}

	public Integer getEntityTypeId() {
		return (Integer) getId();
	}

	@Override
	protected EntityType createInstance() {
		EntityType entityType = new EntityType();
		return entityType;
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
		BusinessEntityType businessEntityType = businessEntityTypeHome.getDefinedInstance();
		if (businessEntityType != null) {
			getInstance().setBusinessEntityType(businessEntityType);
		}
	}

	public boolean isWired() {
		if (getInstance().getBusinessEntity() == null)
			return false;
		if (getInstance().getBusinessEntityType() == null)
			return false;
		return true;
	}

	public EntityType getDefinedInstance() {
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
