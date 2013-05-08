package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("countryHome")
public class CountryHome extends EntityHome<Country> {

	public void setCountryId(Integer id) {
		setId(id);
	}

	public Integer getCountryId() {
		return (Integer) getId();
	}

	@Override
	protected Country createInstance() {
		Country country = new Country();
		return country;
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

	public Country getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Region> getRegions() {
		return getInstance() == null ? null : new ArrayList<Region>(getInstance().getRegions());
	}

}
