package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("businessEntityContactHome")
public class BusinessEntityContactHome extends EntityHome<BusinessEntityContact> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;
	@In(create = true)
	ContactHome contactHome;

	public void setBusinessEntityContactId(Integer id) {
		setId(id);
	}

	public Integer getBusinessEntityContactId() {
		return (Integer) getId();
	}

	@Override
	protected BusinessEntityContact createInstance() {
		BusinessEntityContact businessEntityContact = new BusinessEntityContact();
		return businessEntityContact;
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
		if (getInstance().getBusinessEntity() == null)
			return false;
		if (getInstance().getContact() == null)
			return false;
		return true;
	}

	public BusinessEntityContact getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
