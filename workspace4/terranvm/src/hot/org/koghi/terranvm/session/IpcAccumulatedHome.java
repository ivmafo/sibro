package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.entity.IpcAccumulated;
import org.koghi.terranvm.entity.IpcMonthly;
import org.koghi.terranvm.entity.SystemVariable;

@Name("ipcAccumulatedHome")
public class IpcAccumulatedHome extends EntityHome<IpcAccumulated> {

	// @In(create = true)
	// SystemVariableHome systemVariableHome;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(create = true)
	SystemVariableHome systemVariableHome;

	public void setIpcAccumulatedId(Integer id) {
		setId(id);
	}

	public Integer getIpcAccumulatedId() {
		return (Integer) getId();
	}

	@Override
	protected IpcAccumulated createInstance() {
		IpcAccumulated ipcAccumulated = new IpcAccumulated();
		return ipcAccumulated;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		SystemVariable systemVariable = systemVariableHome.getDefinedInstance();
		if (systemVariable != null) {
			getInstance().setSystemVariable(systemVariable);
		}
	}

	public boolean isWired() {
		return true;
	}

	public IpcAccumulated getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<SelectItem> getMonths() {
		String[] months = IpcAccumulated.MONTHS;
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (int i = 0; i < months.length; i++) {
			SelectItem item = new SelectItem();
			item.setValue(new Integer(i));
			item.setLabel(months[i]);
			items.add(item);
		}

		if (this.instance.getMonthly() == null && items.size() > 0) {
			this.instance.setMonthly((Integer) items.get(0).getValue());
		}
		return items;
	}

	public List<SelectItem> getYears() {
		List<Integer> years = IpcAccumulated.getYears();
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (int i = 0; i < years.size(); i++) {
			SelectItem item = new SelectItem();
			item.setValue(years.get(i));
			item.setLabel(years.get(i) + "");
			items.add(item);
		}

		if (this.instance.getYear() == null && items.size() > 0) {
			this.instance.setYear((Integer) items.get(0).getValue());
		}
		return items;
	}

	public String getMonth(Integer mounth) {
		if (mounth != null)
			return IpcAccumulated.MONTHS[mounth];
		return null;
	}

	@Override
	public String persist() {
		String res = "";
		if (isValidate(this.getInstance())) {
			res = super.persist();
			updates();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Se actualizó IPCA" + this.getInstance().getYear()));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Ya existe un IPCA  con el año " + this.getInstance().getYear() + " y mes " + this.getMonth(this.getInstance().getMonthly())));
		}
		return res;
	}

	@Override
	public String update() {
		String res = "";
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "ERROR", "No se pude editar un IPC"));
		return res;
	}

	private boolean isValidate(IpcAccumulated ipcAccumulated) {
		boolean res = false;
		Query query = getEntityManager().createQuery("FROM IpcAccumulated ia where ia.monthly=? AND ia.year=? AND ia.id <> ?");
		query.setParameter(1, ipcAccumulated.getMonthly());
		query.setParameter(2, ipcAccumulated.getYear());
		query.setParameter(3, ipcAccumulated.getId());
		@SuppressWarnings("unchecked")
		List<IpcAccumulated> ipcMonthlies = (List<IpcAccumulated>) query.getResultList();
		if (ipcMonthlies != null && !ipcMonthlies.isEmpty()) {
			res = false;
		} else {
			res = true;
		}

		return res;
	}

	public Float getValue() {
		return (this.getInstance().getValue() != null ? this.getInstance().getValue() * 100 : null);
	}

	public void setValue(Float value) {
		this.getInstance().setValue(value / 100);
	}

	public Float getValue(IpcMonthly ipc) {
		return (ipc.getValue() != null ? ipc.getValue() * 100 : null);
	}

	public void updates() {
		BillingFunctions billing = new BillingFunctions(this.getEntityManager());
		billing.recalculateIncrements("IPCA");
		billing.calculateRetroactive(this.instance, null);
		billing.recalculateVariable(null, "IPCA");
	}

	@Override
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
}
