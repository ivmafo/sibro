package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("contributionModuleHome")
public class ContributionModuleHome extends EntityHome<ContributionModule> {

	@In(create = true)
	RealPropertyHome realPropertyHome;

	public void setContributionModuleId(Integer id) {
		setId(id);
	}

	public Integer getContributionModuleId() {
		return (Integer) getId();
	}

	@Override
	protected ContributionModule createInstance() {
		ContributionModule contributionModule = new ContributionModule();
		return contributionModule;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		RealProperty realProperty = realPropertyHome.getDefinedInstance();
		if (realProperty != null) {
			getInstance().setRealProperty(realProperty);
		}
	}

	public boolean isWired() {
		if (getInstance().getRealProperty() == null)
			return false;
		return true;
	}

	public ContributionModule getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RentableUnitContribution> getRentableUnitContributions() {
		return getInstance() == null ? null : new ArrayList<RentableUnitContribution>(getInstance().getRentableUnitContributions());
	}

}
