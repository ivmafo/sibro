package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.EconomicActivity;
import org.koghi.terranvm.entity.EconomicSector;

@Name("economicActivityHome")
public class EconomicActivityHome extends EntityHome<EconomicActivity> {

	@In(create = true)
	EconomicSectorHome economicSectorHome;

	public void setEconomicActivityId(String id) {
		setId(id);
	}

	public String getEconomicActivityId() {
		return (String) getId();
	}

	@Override
	protected EconomicActivity createInstance() {
		EconomicActivity economicActivity = new EconomicActivity();
		return economicActivity;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		EconomicSector economicSector = economicSectorHome.getDefinedInstance();
		if (economicSector != null) {
			getInstance().setEconomicSector(economicSector);
		}
	}

	public boolean isWired() {
		if (getInstance().getEconomicSector() == null)
			return false;
		return true;
	}

	public EconomicActivity getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<BusinessEntity> getBusinessEntities() {
		return getInstance() == null ? null : new ArrayList<BusinessEntity>(getInstance().getBusinessEntities());
	}
	
	@Override
	public String update() {
		return super.update();
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
