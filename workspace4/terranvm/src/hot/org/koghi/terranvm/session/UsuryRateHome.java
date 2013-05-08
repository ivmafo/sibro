package org.koghi.terranvm.session;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.async.Log;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.entity.UsuryRate;


@Name("usuryRateHome")
public class UsuryRateHome extends EntityHome<UsuryRate> {
	
	public Date showdate;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6341542115898500508L;
	private Log log = new Log(this);

	public void setUsuryRateId(Integer id) {
		setId(id);
	}

	public Integer getUsuryRateId() {
		return (Integer) getId();
	}

	@Override
	protected UsuryRate createInstance() {
		UsuryRate usuryRate = new UsuryRate();
		return usuryRate;
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

	public UsuryRate getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public void persistDataBase(){
		
		try {
			this.persist();
			this.instance.setRateUsed(false);
			Query q = getEntityManager().createNativeQuery("INSERT INTO history_usury_rate (value,date,rate_used) values(?,?,?)");
			q.setParameter(1, getInstance().getValue());
			q.setParameter(2, getInstance().getDate());
			q.setParameter(3, getInstance().getRateUsed());
			q.executeUpdate();
			joinTransaction();
			
			BillingFunctions billingFunctions = new BillingFunctions(this.getEntityManager());
			if(billingFunctions.recalculateInterests(this.instance)){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Tasa de Interés", "Generación de intereses exitosa"));
				//this.instance.setRateUsed(true); 
			}
			else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Tasa de Interés", "Error al recalcular intereses, inténtelo de nuevo"));
			}
			
		} catch (Exception e) {
			try {
				InvalidValue[] arr = ((InvalidStateException) e).getInvalidValues();
				for (InvalidValue invalidValue : arr) {
					log.log.error(invalidValue.getPropertyName() + " " + invalidValue.getValue());
				}
			} catch (Exception e2) {
			}
			e.printStackTrace();
		}
		
	}
	

	public boolean validate()
	{
		if(quarterDateValidate()==true)
		{
			if(this.instance.getRateUsed() == false)
				return false;
			else
				return true;
		}
		else 
			return false;
	}
	
	
	
	
	public boolean quarterDateValidate()
	{   
		//fecha ultimo ingreso trimestral taza usura
        Calendar calendar7 = Calendar.getInstance();
        calendar7.set(2000, 01, 01);
		
		// fecha actual
		Calendar calendar = Calendar.getInstance();
		
		Query q = this.getEntityManager().createQuery("from UsuryRate ur ORDER BY ur.date");
		@SuppressWarnings("unchecked")
		List<UsuryRate> usuryRatesList = (List<UsuryRate>) q.getResultList();
		if(usuryRatesList.size() > 0)
		{
			for (UsuryRate usuryRate : usuryRatesList) 
			{
				calendar7.setTime(usuryRate.getDate());

			}
		}
		if(calendar.get(Calendar.MONTH)/3 == calendar7.get(Calendar.MONTH)/3 )
			return true;
		else
			return false;
	}
	
	
	public String quarterName()
	{
		String Month="";
		
		//fecha ultimo ingreso trimestral taza usura
        Calendar calendar7 = Calendar.getInstance();
        calendar7.set(2000, 01, 01);
		
		// fecha actual
		Calendar calendar = Calendar.getInstance();
		
		Query q = this.getEntityManager().createQuery("from UsuryRate ur ORDER BY ur.date");
		List<UsuryRate> usuryRatesList = (List<UsuryRate>) q.getResultList();
		if(usuryRatesList.size() > 0)
		{
			for (UsuryRate usuryRate : usuryRatesList) 
			{
				calendar7.setTime(usuryRate.getDate());

			}
			
			if(calendar7.get(Calendar.MONTH)/3 == 0)
			{
				Month="Enero-Marzo";
			}else if(calendar7.get(Calendar.MONTH)/3 == 1)
			{
				Month="Abril-Junio";
			}else if(calendar7.get(Calendar.MONTH)/3 == 2)
			{
				Month="Julio-Septiembre";
			}else if(calendar7.get(Calendar.MONTH)/3 == 3)
			{
				Month="Octubre-Diciembre";
			}
			
		}
		else
			Month="";
			
		return Month;
	}
	
	
	public boolean quarterExist()
	{
		Calendar calendar = Calendar.getInstance();
		String quarter = "";
		quarter = quarterName();
		boolean exist=false;
		
		if(calendar.get(Calendar.MONTH)/3==0 && quarter=="Enero-Marzo")
		{
			exist = true;
		}else if(calendar.get(Calendar.MONTH)/3==1 && quarter=="Abril-Junio")
		{
			exist = true;
		}else if(calendar.get(Calendar.MONTH)/3==2 && quarter=="Julio-Septiembre")
		{
			exist = true;
		}else if(calendar.get(Calendar.MONTH)/3==3 && quarter=="Octubre-Diciembre")
		{
			exist = true;
		}
		return exist;
	}
	
	
	
	
	public Integer returnId()
	{
		Calendar calendar = Calendar.getInstance();
		int id = 0;
		
		Query q = this.getEntityManager().createQuery("from UsuryRate ur");
		@SuppressWarnings("unchecked")
		List<UsuryRate> usuryRatesList = (List<UsuryRate>) q.getResultList();
		if(usuryRatesList.size() > 0)
		{
			for (UsuryRate usuryRate : usuryRatesList) {
				if(calendar.before(usuryRate.getDate()) == false)
				{
					calendar.setTime(usuryRate.getDate());
					id = usuryRate.getId();
					
				}
			}
		}
		return id == 0 ? null : id;
	}
	
	public boolean validateDate()
	{
		Calendar calendar = Calendar.getInstance();
		
		if(this.instance.getDate() == calendar.getTime())
			return false;
		else
			return true;
	}
	
	
	
	public Date getShowdate() {
		return showdate;
	}
	public void setShowdate(Date showdate) {
		this.showdate = showdate;
	}

	
	public void renewUsuryRateDate() {
		this.instance.setDate(this.showdate);
	}
	
	@Override
	public String persist() {
		renewUsuryRateDate();
		return super.persist();
	}
	
	@Override
	public String update() {
		renewUsuryRateDate();
		return super.update();
	}
	
	
	
	public void Datetwo()
	{
		Calendar calendar = Calendar.getInstance();
		this.showdate = calendar.getTime();
		
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
