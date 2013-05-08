package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("businessEntityTypeHome")
public class BusinessEntityTypeHome extends EntityHome<BusinessEntityType> {
	
	public void setBusinessEntityTypeId(Integer id) {
		setId(id);
	}

	public Integer getBusinessEntityTypeId() {
		return (Integer) getId();
	}

	@Override
	protected BusinessEntityType createInstance() {
		BusinessEntityType businessEntityType = new BusinessEntityType();
		return businessEntityType;
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

	public BusinessEntityType getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<BusinessEntity> getEntities() {
		return getInstance() == null ? null : new ArrayList<BusinessEntity>(getInstance().getEntities());
	}
	
	public List<BusinessEntityType> getTipos(){
		Query q =this.getEntityManager().createQuery("from BusinessEntityType be");
		List<BusinessEntityType> l = (List<BusinessEntityType>)q.getResultList();
		
		return l;
		}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}
	
	public boolean businessentitytypeListInApprove(BusinessEntityType businesentitytype){
		return new MakerCheckerHome().isObjectInMakerChecker(businesentitytype);
	}
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((BusinessEntityType) new MakerCheckerHome().getInstance(makerCheckerId));
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
