package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("contactHome")
public class ContactHome extends EntityHome<Contact> {

	@In(create = true)
	ContactTypeHome contactTypeHome;

	public void setContactId(Integer id) {
		setId(id);
	}

	public Integer getContactId() {
		return (Integer) getId();
	}

	@Override
	protected Contact createInstance() {
		Contact contact = new Contact();
		return contact;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ContactType contactType = contactTypeHome.getDefinedInstance();
		if (contactType != null) {
			getInstance().setContactType(contactType);
		}
	}

	public boolean isWired() {
		if (getInstance().getContactType() == null)
			return false;
		return true;
	}

	public Contact getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RealPropertyContact> getRealPropertyContacts() {
		return getInstance() == null ? null : new ArrayList<RealPropertyContact>(getInstance().getRealPropertyContacts());
	}

	public List<BusinessEntityContact> getBusinessEntityContacts() {
		return getInstance() == null ? null : new ArrayList<BusinessEntityContact>(getInstance().getBusinessEntityContacts());
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return getInstance() == null ? null : new ArrayList<PhoneNumber>(getInstance().getPhoneNumbers());
	}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}
	
	public boolean contactListInApprove(Contact contact){
		return new MakerCheckerHome().isObjectInMakerChecker(contact);
	}
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((Contact) new MakerCheckerHome().getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}
	
	@Transactional
	public void approveChange(){
		setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}","ApproveSuccessfully");
	}
	
	public void cancelChange(){
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_cancellation}","CancelSuccessfully");
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
