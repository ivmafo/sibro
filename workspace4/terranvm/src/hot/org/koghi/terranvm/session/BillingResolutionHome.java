package org.koghi.terranvm.session;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.BillingResolution;
import org.koghi.terranvm.entity.BillingType;
import org.koghi.terranvm.entity.BusinessEntity;

@Name("billingResolutionHome")
public class BillingResolutionHome extends EntityHome<BillingResolution> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;
	private String messageLabel;
	private String messageLabel1;
	private String messageLabel2;
	

	public String getMessageLabel1() {
		return messageLabel1;
	}

	public void setMessageLabel1(String messageLabel1) {
		this.messageLabel1 = messageLabel1;
	}

	public String getMessageLabel2() {
		return messageLabel2;
	}

	public void setMessageLabel2(String messageLabel2) {
		this.messageLabel2 = messageLabel2;
	}

	public String getMessageLabel() {
		return messageLabel;
	}

	public void setMessageLabel(String messageLabel) {
		this.messageLabel = messageLabel;
	}
	
	public void billingResolutionMaxValidation(){
		int validation=this.getInstance().getMax();
		if(validation<this.getInstance().getMin()){
			this.messageLabel="El número máximo debe ser mayor al número mínimo";	
		}
		else{
			this.messageLabel="";
		}
	
	}
	
	public void billingResolutionMinValidation(){
		int validation1=this.getInstance().getMin();
		if(validation1>this.getInstance().getMax()){
			this.messageLabel1="El número mínimo debe ser menor al número máximo";		
			}
		else{
			this.messageLabel1="";
		}
		
	}
	
	public void billingResolutionCurrentValidation(){
		int validation1=this.getInstance().getMin();
		int validation2=this.getInstance().getMax();
		if(validation1 <= this.getInstance().getCurrent() && validation2 >=  this.getInstance().getCurrent()){
			this.messageLabel2="";
		}
		else{
			this.messageLabel2="El número actual no esta en el intervalo del minimo y el maximo";	
		}
//		System.out.println();
	}
	

	public void setBillingResolutionId(Integer id) {
		setId(id);
	}

	public Integer getBillingResolutionId() {
		return (Integer) getId();
	}

	@Override
	protected BillingResolution createInstance() {
		BillingResolution billingResolution = new BillingResolution();
		return billingResolution;
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

	public BillingResolution getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		String res ="";
		if (validation())
					res = super.persist();
		else{
			res = "fail";
			getFacesMessages().addFromResourceBundle(Severity.ERROR, "No se puede guardar la resolucion de facturacion verificar los datos ingresados", "ApproveSuccessfully");
		}
		return res;
	}
	
	@Override
	public String update() {
		String res ="";
		if (validation())
					res = super.update();
		else{
			res = "fail";
			getFacesMessages().addFromResourceBundle(Severity.ERROR, "No se puede guardar la resolucion de facturacion verificar los datos ingresados", "ApproveSuccessfully");
		}
		return res;
	}
	
	public boolean validation() {
		messageLabel="";
		messageLabel1="";
		messageLabel2="";
		
		
		billingResolutionCurrentValidation();
		billingResolutionMaxValidation();
		billingResolutionMinValidation();
		if (messageLabel!=null && messageLabel.isEmpty() 
				&& messageLabel1!=null && messageLabel1.isEmpty()
				&& messageLabel2!= null && messageLabel2.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void validationResolution(){
		messageLabel="";
		messageLabel1="";
		messageLabel2="";
		billingResolutionCurrentValidation();
		billingResolutionMaxValidation();
		billingResolutionMinValidation();
		
	}
	
	public List<BillingType> getBillingType() {
		List<BillingType> types = this.getEntityManager().createQuery("from BillingType").getResultList();
		if (types.size()>0 && getInstance().getBillingType()==null)
			getInstance().setBillingType(types.get(0));
		return types;
	}
	
public void calculateEndDate(){
	Date calculateEndDate = null;
	Calendar s = Calendar.getInstance();
	s.setTime(this.getInstance().getResolutionDate());
	s.add(Calendar.YEAR, 2);
	calculateEndDate =s.getTime();
	this.instance.setEndDate(calculateEndDate);
	
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
