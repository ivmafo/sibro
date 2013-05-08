package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.BusinessLine;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.Project;

@Name("businessLineHome")
public class BusinessLineHome extends EntityHome<BusinessLine> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;
	
	@In(create = true)
	BusinessLineHome businessLineHome;
	
	private BusinessLine businessLine;
	
	public String fromTab;

	public String getFromTab() {
		return fromTab;
	}

	public void setFromTab(String fromTab) {
		this.fromTab = fromTab;
	}

	public void setBusinessLineId(Integer id) {
		setId(id);
	}

	public Integer getBusinessLineId() {
		return (Integer) getId();
	}

	@Override
	protected BusinessLine createInstance() {
		BusinessLine businessLine = new BusinessLine();
		return businessLine;
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
	}

	public boolean isWired() {
		if (getInstance().getBusinessEntity() == null)
			return false;
		return true;
	}

	public BusinessLine getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<BusinessLine> getBusinessLines() {
		return getInstance() == null ? null : new ArrayList<BusinessLine>(getInstance().getBusinessLines());
	}

	public List<Project> getProjects() {
		return getInstance() == null ? null : new ArrayList<Project>(getInstance().getProjects());
	}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance(),getInstance().getProjects().toArray());
		updatedMessage();
		return "updated";
	}
	
	public boolean businessLineListInApprove(BusinessLine businessLine){
		return new MakerCheckerHome().isObjectInMakerChecker(businessLine);
	}
	
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((BusinessLine) new MakerCheckerHome().getInstance(makerCheckerId));
		//setInstance(getEntityManager().merge(getInstance()));
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
	
	public BusinessLine getBusinessLine() {
		if (businessLine==null)
			businessLine = new BusinessLine();
		return businessLine;
	}

	public void setBusinessLine(BusinessLine businessLine) {
		this.businessLine = businessLine;
	}
	
	public void clearbusinessLine(){
		this.businessLine=new BusinessLine();
	}
	
	@Transactional
	public void persistSubLine() {
		// this.getEntityManager().joinTransaction();
		this.businessLine.setBusinessLine(instance);
		// this.getEntityManager().persist(businessLine);
		// this.getEntityManager().flush();
		this.instance.getBusinessLines().add(businessLine);
		this.clearbusinessLine();

	}
	
	public BusinessLine getInstanceMaker(MakerChecker makerChecker) {
		Object aux =  new MakerCheckerHome().getInstance(makerChecker);
		if (aux instanceof BusinessLine){
			BusinessLine businessLine = (BusinessLine)aux;
			businessLine = getEntityManager().merge(businessLine);
			return businessLine;
		}
		return null;
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
