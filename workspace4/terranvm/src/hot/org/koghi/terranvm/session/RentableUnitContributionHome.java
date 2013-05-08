package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("rentableUnitContributionHome")
public class RentableUnitContributionHome extends EntityHome<RentableUnitContribution> {

	@In(create = true)
	RentableUnitHome rentableUnitHome;
	@In(create = true)
	ContributionModuleHome contributionModuleHome;

	public void setRentableUnitContributionId(Integer id) {
		setId(id);
	}

	public Integer getRentableUnitContributionId() {
		return (Integer) getId();
	}

	@Override
	protected RentableUnitContribution createInstance() {
		RentableUnitContribution rentableUnitContribution = new RentableUnitContribution();
		return rentableUnitContribution;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		RentableUnit rentableUnit = rentableUnitHome.getDefinedInstance();
		if (rentableUnit != null) {
			getInstance().setRentableUnit(rentableUnit);
		}
		ContributionModule contributionModule = contributionModuleHome.getDefinedInstance();
		if (contributionModule != null) {
			getInstance().setContributionModule(contributionModule);
		}
	}

	public boolean isWired() {
		if (getInstance().getRentableUnit() == null)
			return false;
		if (getInstance().getContributionModule() == null)
			return false;
		return true;
	}

	public RentableUnitContribution getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
