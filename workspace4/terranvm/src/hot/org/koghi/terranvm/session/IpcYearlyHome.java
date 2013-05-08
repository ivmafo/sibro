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
import org.koghi.terranvm.entity.IpcYearly;
import org.koghi.terranvm.entity.SystemVariable;

@Name("ipcYearlyHome")
public class IpcYearlyHome extends EntityHome<IpcYearly> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(create = true)
	SystemVariableHome systemVariableHome;

	public void setIpcYearlyId(Integer id) {
		setId(id);
	}

	public Integer getIpcYearlyId() {
		return (Integer) getId();
	}

	@Override
	protected IpcYearly createInstance() {
		IpcYearly ipcYearly = new IpcYearly();
		return ipcYearly;
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

	public IpcYearly getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<SelectItem> getYears() {
		List<Integer> years = IpcYearly.getYears();
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

	@Override
	public String persist() {
		String res = "";
		if (isValidate(this.getInstance())) {
			res = super.persist();
			updates();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Se actualizó IPCM" + this.getInstance().getYear()));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Ya existe un IPC anual con el año " + this.getInstance().getYear()));
		}
		return res;
	}

	@Override
	public String update() {
		String res = "";
		if (isValidate(this.getInstance())) {
			res = super.update();
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Ya existe un IPC anual con el año " + this.getInstance().getYear()));
		}
		return res;
	}

	private boolean isValidate(IpcYearly ipcYearly) {
		boolean res = false;
		Query query = getEntityManager().createQuery("FROM IpcYearly iy WHERE iy.year=? ");
		query.setParameter(1, ipcYearly.getYear());
		// query.setParameter(2, ipcYearly.getId());
		@SuppressWarnings("unchecked")
		List<IpcYearly> ipcYearlies = (List<IpcYearly>) query.getResultList();
		if (ipcYearlies != null && !ipcYearlies.isEmpty()) {
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

	public Float getValue(IpcYearly ipc) {
		return (ipc.getValue() != null ? ipc.getValue() * 100 : null);
	}

	public void updates() {
		BillingFunctions billing = new BillingFunctions(this.getEntityManager());
		billing.recalculateIncrements("IPCY");
		billing.calculateRetroactive(this.instance, null);
		billing.recalculateVariable(null, "IPCY");
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
