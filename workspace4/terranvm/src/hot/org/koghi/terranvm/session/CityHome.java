package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("cityHome")
public class CityHome extends EntityHome<City> {

	@In(create = true)
	RegionHome regionHome;

	public void setCityId(Integer id) {
		setId(id);
	}

	public Integer getCityId() {
		return (Integer) getId();
	}

	@Override
	protected City createInstance() {
		City city = new City();
		return city;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Region region = regionHome.getDefinedInstance();
		if (region != null) {
			getInstance().setRegion(region);
		}
	}

	public boolean isWired() {
		if (getInstance().getRegion() == null)
			return false;
		return true;
	}

	public City getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Address> getAddresses() {
		return getInstance() == null ? null : new ArrayList<Address>(getInstance().getAddresses());
	}

}
