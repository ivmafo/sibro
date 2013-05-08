package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.ConsecutiveCollectionAccount;

@Name("consecutiveCollectionAccountHome")
public class ConsecutiveCollectionAccountHome extends EntityHome<ConsecutiveCollectionAccount> {

	@In(create = true)
	BusinessEntityHome businessEntityHome;

	public void setConsecutiveCollectionAccountId(Integer id) {
		setId(id);
	}

	public Integer getConsecutiveCollectionAccountId() {
		return (Integer) getId();
	}

	@Override
	protected ConsecutiveCollectionAccount createInstance() {
		ConsecutiveCollectionAccount consecutiveCollectionAccount = new ConsecutiveCollectionAccount();
		return consecutiveCollectionAccount;
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

	public ConsecutiveCollectionAccount getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
