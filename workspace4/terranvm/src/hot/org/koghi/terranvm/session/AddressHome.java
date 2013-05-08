package org.koghi.terranvm.session;

import java.util.Iterator;
import java.util.List;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;

@Name("addressHome")
public class AddressHome extends EntityHome<Address> {

	private Country selectedCountry;
	private Region selectedRegion;
	private boolean direccionEstandar=true;
	boolean flagCountry=true;
	boolean flagRegion=true;

	public boolean isDireccionEstandar() {
		return direccionEstandar;
	}

	public void setDireccionEstandar(boolean direccionEstandar) {
		this.direccionEstandar = direccionEstandar;
	}

	public List<Country> getCountryList() {
		return this.getEntityManager().createQuery("from Country order by name").getResultList();
	}

	
	public Country getSelectedCountry() {
		if (this.instance != null && this.instance.getCity() != null && flagCountry) {
			// this.selectedRegion = this.instance.getCity().getRegion();
			this.selectedCountry = this.instance.getCity().getRegion().getCountry();
			flagCountry = false;
		}
		return selectedCountry;
	}

	public void setSelectedCountry(Country selectedCountry) {
		this.selectedCountry = selectedCountry;
		setDefaultValues();
	}

	private void setDefaultValues() {
		Iterator<Region> iterator = this.selectedCountry.getRegions().iterator();
		if (iterator.hasNext())
			selectedRegion = iterator.next();
	}

	public Region getSelectedRegion() {
		if (this.instance != null && this.instance.getCity() != null && flagRegion) {
			this.selectedRegion = this.instance.getCity().getRegion();
			this.flagRegion = false;
			// this.selectedCountry = this.selectedRegion.getCountry();
		}
		return selectedRegion;
	}

	public void setSelectedRegion(Region selectedRegion) {
		this.selectedRegion = selectedRegion;
	}

	public AddressHome() {
		super();

	}

	@In(create = true)
	BusinessEntityHome businessEntityHome;
	@In(create = true)
	RealPropertyHome realPropertyHome;

	public void setAddressId(Integer id) {
		setId(id);
	}

	public Integer getAddressId() {
		return (Integer) getId();
	}

	@Override
	protected Address createInstance() {
		Address address = new Address();
		return address;
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
		RealProperty realProperty = realPropertyHome.getDefinedInstance();
		if (realProperty != null) {
			getInstance().setRealProperty(realProperty);
		}
	}

	public boolean isWired() {
		return true;
	}

	public Address getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
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
