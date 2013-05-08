package org.koghi.terranvm.session;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.async.Log;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.bean.Format_number;
import org.koghi.terranvm.entity.MinimunWage;

@Name("minimunWageHome")
public class MinimunWageHome extends EntityHome<MinimunWage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Log log = new Log(this);

	@In(create = true)
	SystemVariableHome systemVariableHome;

	public void setMinimunWageId(Integer id) {
		setId(id);
	}

	public Integer getMinimunWageId() {
		return (Integer) getId();
	}

	public Double getValue() {
		return (Double) (this.getInstance().getValue());
	}

	public void setValue(Double value) {
		this.getInstance().setValue(value);
	}

	public Integer getYear() {
		return (Integer) (this.getInstance().getYear());
	}

	public void setYear(Integer year) {
		this.getInstance().setYear(year);
	}

	public Date getDate() {
		return this.getInstance().getDate();
	}

	public void setDate(Date date) {
		this.getInstance().setDate(date);
	}

	public boolean isWired() {
		return true;
	}

	@Override
	public String update() {
		String res = "";
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "ERROR", "No se pude editar un Salario Minimo"));
		return res;
	}

	public String persist() {
		String res = "";
		System.out.println("year: " + this.getYear() + " date: " + this.getDate() + " value: " + this.getValue());
		if (isValidate(this.getInstance())) {

			res = super.persist();
			log.log.info("Se creo el salario minimo exitosamente");
			updates();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Se actualizó SM" + this.getInstance().getYear()));

		} else {
			log.log.info("Ya existe un Salario Minimo con el año");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Ya existe un Salario Minimo con el año " + this.getInstance().getYear()));

		}
		return res;
	}

	private boolean isValidate(MinimunWage minimunWage) {
		boolean res = false;
		Query query = getEntityManager().createQuery("FROM MinimunWage mw where  mw.year=? ");
		query.setParameter(1, minimunWage.getYear());
		@SuppressWarnings("unchecked")
		List<MinimunWage> minimunage = (List<MinimunWage>) query.getResultList();
		if (minimunage != null && !minimunage.isEmpty()) {
			res = false;
		} else {
			res = true;
		}
		return res;
	}

	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
		}
	}

	/**
	 * este metetodo recive la variable de salario minimo de tipo double
	 * 
	 * @retunr String devuelve el valor en formato de miles y millones
	 */
	public String valueFormat(Double value) {
		return Format_number.FormatToString3(value);
	}

	public void updates() {
		BillingFunctions billing = new BillingFunctions(this.getEntityManager());
		billing.recalculateIncrements("SM");
		billing.calculateRetroactive(this.instance, null);
		billing.recalculateVariable(null, "SM");
	}

}
