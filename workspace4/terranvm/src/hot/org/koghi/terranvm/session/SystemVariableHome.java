package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.entity.IpcMonthly;
import org.koghi.terranvm.entity.IpcYearly;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.SystemVariable;

@Name("systemVariableHome")
public class SystemVariableHome extends EntityHome<SystemVariable> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(create = true)
	ProjectHome projectHome;

	public void setSystemVariableId(Integer id) {
		setId(id);
	}

	public Integer getSystemVariableId() {
		return (Integer) getId();
	}

	@Override
	public String persist() {
		String res = "";
		res = super.persist();
		updates();
		return res;
	}

	@Override
	public String update() {
		String res = "";
		res = super.update();
		updates();
		return res;
	}

	public void updates() {
		//BillingFunctions billing = new BillingFunctions(this.getEntityManager());
		//billing.calculateRetroactive(this.instance, null);
		//billing.recalculateIncrements(this.instance.getSintax());
		//billing.recalculateVariable(null, this.instance.getSintax());
	}

	@Override
	protected SystemVariable createInstance() {
		SystemVariable systemVariable = new SystemVariable();
		return systemVariable;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Project project = projectHome.getDefinedInstance();
		if (project != null) {
			getInstance().setProject(project);
		}
	}

	public SystemVariable getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<IpcMonthly> getIpcMonthlies() {
		return getInstance() == null ? null : new ArrayList<IpcMonthly>(getInstance().getIpcMonthlies());
	}

	public List<IpcYearly> getIpcYearlies() {
		return getInstance() == null ? null : new ArrayList<IpcYearly>(getInstance().getIpcYearlies());
	}

	public boolean isWired() {
		if (this.instance.getProject() == null)
			return false;
		else
			return true;
	}

	public String True_False(boolean x) {
		if (x == true)
			return "Si";
		else
			return "No";
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
