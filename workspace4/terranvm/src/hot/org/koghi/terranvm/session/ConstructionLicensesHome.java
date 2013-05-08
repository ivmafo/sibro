package org.koghi.terranvm.session;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.entity.City;
import org.koghi.terranvm.entity.ConstructionLicenses;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.Region;

@Name("constructionLicensesHome")
public class ConstructionLicensesHome extends EntityHome<ConstructionLicenses> {

	@In(create = true)
	CityHome cityHome;
	@In(create = true)
	RealPropertyHome realPropertyHome;
	
	private Region selectedRegion;

	public void setConstructionLicensesId(Integer id) {
		setId(id);
	}

	public Integer getConstructionLicensesId() {
		return (Integer) getId();
	}

	@Override
	protected ConstructionLicenses createInstance() {
		ConstructionLicenses constructionLicenses = new ConstructionLicenses();
		return constructionLicenses;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		City city = cityHome.getDefinedInstance();
		if (city != null) {
			getInstance().setCity(city);
		}
		RealProperty realProperty = realPropertyHome.getDefinedInstance();
		if (realProperty != null) {
			getInstance().setRealProperty(realProperty);
		}
	}

	public boolean isWired() {
		return true;
	}

	public ConstructionLicenses getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public List<Region> getSelectRegionList(){  
		return this.getEntityManager().createQuery("from Region r  where r.country.abbreviation='CO' ORDER BY r.name").getResultList();
	}
	public Region getSelectedRegion() {
		if (this.instance != null && this.instance.getCity() != null) {
			this.selectedRegion = this.instance.getCity().getRegion();
			// this.selectedCountry = this.selectedRegion.getCountry();
			// System.out.println(this.selectedRegion.getName()+" distinto de null");
		}
		return selectedRegion;
	}

	public void setSelectedRegion(Region selectedRegion) {
		this.selectedRegion = selectedRegion;
	}

}
