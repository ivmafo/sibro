package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("regionHome")
public class RegionHome extends EntityHome<Region> {

	@In(create = true)
	CountryHome countryHome;

	public void setRegionId(Integer id) {
		setId(id);
	}

	public Integer getRegionId() {
		return (Integer) getId();
	}

	@Override
	protected Region createInstance() {
		Region region = new Region();
		return region;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Country country = countryHome.getDefinedInstance();
		if (country != null) {
			getInstance().setCountry(country);
		}
	}

	public boolean isWired() {
		if (getInstance().getCountry() == null)
			return false;
		return true;
	}

	public Region getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<City> getCities() {
		return getInstance() == null ? null : new ArrayList<City>(getInstance().getCities());
	}

}
