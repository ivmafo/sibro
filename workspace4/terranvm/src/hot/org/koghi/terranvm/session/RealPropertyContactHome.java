package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("realPropertyContactHome")
public class RealPropertyContactHome extends EntityHome<RealPropertyContact> {

	@In(create = true)
	ContactHome contactHome;
	@In(create = true)
	RealPropertyHome realPropertyHome;

	public void setRealPropertyContactId(Integer id) {
		setId(id);
	}

	public Integer getRealPropertyContactId() {
		return (Integer) getId();
	}

	@Override
	protected RealPropertyContact createInstance() {
		RealPropertyContact realPropertyContact = new RealPropertyContact();
		return realPropertyContact;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Contact contact = contactHome.getDefinedInstance();
		if (contact != null) {
			getInstance().setContact(contact);
		}
		RealProperty realProperty = realPropertyHome.getDefinedInstance();
		if (realProperty != null) {
			getInstance().setRealProperty(realProperty);
		}
	}

	public boolean isWired() {
		if (getInstance().getContact() == null)
			return false;
		if (getInstance().getRealProperty() == null)
			return false;
		return true;
	}

	public RealPropertyContact getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
