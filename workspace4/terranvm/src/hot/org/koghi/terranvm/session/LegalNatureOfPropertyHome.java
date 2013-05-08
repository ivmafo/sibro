package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("legalNatureOfPropertyHome")
public class LegalNatureOfPropertyHome extends EntityHome<LegalNatureOfProperty> {

	public void setLegalNatureOfPropertyId(Integer id) {
		setId(id);
	}

	public Integer getLegalNatureOfPropertyId() {
		return (Integer) getId();
	}

	@Override
	protected LegalNatureOfProperty createInstance() {
		LegalNatureOfProperty legalNatureOfProperty = new LegalNatureOfProperty();
		return legalNatureOfProperty;
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

	public LegalNatureOfProperty getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RealProperty> getRealProperties() {
		return getInstance() == null ? null : new ArrayList<RealProperty>(getInstance().getRealProperties());
	}
	
	@Override
	public String update() {
		this.getEntityManager().flush();
        getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}", "ApproveSuccessfully");
        return "updated";
	}
	
	public boolean legalNatureOfPropertyListInApprove(LegalNatureOfProperty legalNatureOfProperty){
		return new MakerCheckerHome().isObjectInMakerChecker(legalNatureOfProperty);
	}
	
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((LegalNatureOfProperty) new MakerCheckerHome().getInstance(makerCheckerId));
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
