package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("contactTypeHome")
public class ContactTypeHome extends EntityHome<ContactType> {

	public void setContactTypeId(Integer id) {
		setId(id);
	}

	public Integer getContactTypeId() {
		return (Integer) getId();
	}

	@Override
	protected ContactType createInstance() {
		ContactType contactType = new ContactType();
		return contactType;
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

	public ContactType getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Contact> getContacts() {
		return getInstance() == null ? null : new ArrayList<Contact>(getInstance().getContacts());
	}

}
