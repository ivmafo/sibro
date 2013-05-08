package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;

@Name("phoneNumberHome")
public class PhoneNumberHome extends EntityHome<PhoneNumber> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;
	@In(create = true)
	ContactHome contactHome;

	public void setPhoneNumberId(Integer id) {
		setId(id);
	}

	public Integer getPhoneNumberId() {
		return (Integer) getId();
	}

	@Override
	protected PhoneNumber createInstance() {
		PhoneNumber phoneNumber = new PhoneNumber();
		return phoneNumber;
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
		Contact contact = contactHome.getDefinedInstance();
		if (contact != null) {
			getInstance().setContact(contact);
		}
	}

	public boolean isWired() {
		return true;
	}

	public PhoneNumber getDefinedInstance() {
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
