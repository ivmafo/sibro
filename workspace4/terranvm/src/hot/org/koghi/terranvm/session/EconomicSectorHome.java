package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("economicSectorHome")
public class EconomicSectorHome extends EntityHome<EconomicSector> {

	public void setEconomicSectorId(Integer id) {
		setId(id);
	}

	public Integer getEconomicSectorId() {
		return (Integer) getId();
	}

	@Override
	protected EconomicSector createInstance() {
		EconomicSector economicSector = new EconomicSector();
		return economicSector;
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

	public EconomicSector getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<EconomicActivity> getEconomicActivities() {
		return getInstance() == null ? null : new ArrayList<EconomicActivity>(getInstance().getEconomicActivities());
	}

}
