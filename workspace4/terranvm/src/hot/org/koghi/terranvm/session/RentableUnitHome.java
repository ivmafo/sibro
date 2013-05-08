package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.Address;
import org.koghi.terranvm.entity.Area;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.Construction;
import org.koghi.terranvm.entity.Floor;
import org.koghi.terranvm.entity.PhoneNumber;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RentableUnitType;
import org.richfaces.component.html.HtmlExtendedDataTable;
import org.richfaces.model.selection.Selection;

@Name("rentableUnitHome")
public class RentableUnitHome extends EntityHome<RentableUnit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(create = true)
	RentableUnitHome rentableUnitHome;
	@In(create = true)
	AreaHome areaHome;

	private boolean validForm = true;
	private RealProperty selectedRealProperty;
	private Construction selectedConstruction;
	private Floor selectedFloor;
	private RealProperty selectRealProperty;
	private List<RentableUnit> rentableUnitList;
	private RentableUnit selectedRentableUnit;

	@In(required = false)
	public String projectFilter;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableRentableUnit;

	private Selection selectionTableRentableUnit;
	private String rentableUnitListTableState;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableOwnerBind;
	private Selection selectionOwner;
	private String tableStateOwner;
	private List<BusinessEntity> owners;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableLesseeBind;
	private Selection selectionLessee;
	private String tableStateLessee;
	@DataModel
	private List<BusinessEntity> lessees;

	private List<Address> addressOwner;
	private List<PhoneNumber> phoneNumberOwner;

	private List<Address> addressLessee;
	private List<PhoneNumber> phoneNumbersLessee;

	public void setRentableUnitId(Integer id) {
		setId(id);
	}

	public Integer getRentableUnitId() {
		return (Integer) getId();
	}

	@Override
	protected RentableUnit createInstance() {
		RentableUnit rentableUnit = new RentableUnit();
		return rentableUnit;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Area area = areaHome.getDefinedInstance();
		if (area != null) {
			getInstance().setArea(area);
		}
	}

	public boolean isWired() {
		if (getInstance().getArea() == null)
			return false;
		return true;
	}

	public RentableUnit getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RentableUnit> getRentableUnits() {
		return getInstance() == null ? null : new ArrayList<RentableUnit>(getInstance().getRentableUnits());
	}

	public void canPersist() {

		Area[] areas = this.instance.getArea().getRentableUnits().toArray(new Area[] {});
		double totalArea = 0;
		for (int i = 0; i < areas.length; i++) {
			totalArea += areas[i].getArea() != null ? areas[i].getArea() : 0;
		}
		if (totalArea + (this.instance.getMeters() != null ? this.instance.getMeters() : 0) <= (this.instance.getArea().getArea() != null ? this.instance.getArea().getArea() : 0)) {
			this.persist();
			this.validForm = true;
		} else {
			this.validForm = false;
		}

	}

	public boolean isValidForm() {
		return validForm;
	}

	public void setValidForm(boolean validForm) {
		this.validForm = validForm;
	}

	public List<RealProperty> getRealProperties() {
		Query q = this.getEntityManager().createQuery("select rp from RealProperty rp");
		@SuppressWarnings("unchecked")
		List<RealProperty> l = (List<RealProperty>) q.getResultList();
		return l;
	}

	public RealProperty getSelectedRealProperty() {
		return selectedRealProperty;
	}

	public void setSelectedRealProperty(RealProperty selectedRealProperty) {
		if (instance == null)
			this.instance.setArea(null);
		this.selectedConstruction = null;
		this.selectedFloor = null;
		if (!selectedRealProperty.getConstructions().isEmpty())
			setSelectedConstruction(selectedRealProperty.getConstructions().toArray(new Construction[0])[0]);
		this.selectedRealProperty = selectedRealProperty;
	}

	public Construction getSelectedConstruction() {
		return selectedConstruction;
	}

	public void setSelectedConstruction(Construction selectedConstruction) {
		this.instance.setArea(null);
		this.selectedFloor = null;
		if (!selectedConstruction.getFloors().isEmpty())
			setSelectedFloor(selectedConstruction.getFloors().toArray(new Floor[0])[0]);
		this.selectedConstruction = selectedConstruction;
	}

	public Floor getSelectedFloor() {
		return selectedFloor;
	}

	public void setSelectedFloor(Floor selectedFloor) {
		this.instance.setArea(null);
		if (!selectedFloor.getAreas().isEmpty())
			this.instance.setArea(selectedFloor.getAreas().toArray(new Area[0])[0]);

		this.selectedFloor = selectedFloor;
	}

	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}

	public boolean rentableUnitListInApprove(RentableUnit rentableUnit) {
		return new MakerCheckerHome().isObjectInMakerChecker(rentableUnit);
	}

	public void updateInstanceMaker(int makerCheckerId) {
		setInstance((RentableUnit) new MakerCheckerHome().getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}

	@SuppressWarnings("deprecation")
    @Transactional
	public void approveChange() {
		setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		this.getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}", "ApproveSuccessfully");
	}

	@SuppressWarnings("deprecation")
    public void cancelChange() {
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_cancellation}", "CancelSuccessfully");
	}

	@SuppressWarnings("unchecked")
    public List<RentableUnitType> getRentableUnitType() {
		List<RentableUnitType> type = this.getEntityManager().createQuery("from RentableUnitType order by type").getResultList();
		if (type.size() > 0 && getInstance().getRentableUnitType() == null)
			getInstance().setRentableUnitType(type.get(0));
		return type;
	}

	public RealProperty getSelectRealProperty() {
		return selectRealProperty;
	}

	public void setSelectRealProperty(RealProperty selectRealProperty) {
		this.selectRealProperty = selectRealProperty;
	}

	public void instanceSelectedRentableUnit(RentableUnit selectedRentableUnit) {
		this.selectedRentableUnit = selectedRentableUnit;
	}

	@SuppressWarnings("unchecked")
    public List<RealProperty> getRealproperties() {
		if (Integer.parseInt(projectFilter) != -1) {
			Query q = this.getEntityManager().createNativeQuery("select RP.* from real_property RP,project_has_realproperty PHP where PHP.realproperty=RP.id and PHP.project = ?", RealProperty.class);
			q.setParameter(1, Integer.parseInt(projectFilter));
			List<RealProperty> m = (List<RealProperty>) q.getResultList();
			if (m != null && !m.isEmpty() && this.selectRealProperty == null) {
				this.selectRealProperty = m.get(0);
			}
			return m;
		} else {
			Query q = this.getEntityManager().createQuery("from RealProperty");
			List<RealProperty> m = (List<RealProperty>) q.getResultList();
			if (m != null && !m.isEmpty() && this.selectRealProperty == null) {
				this.selectRealProperty = m.get(0);
			}
			return m;
		}
	}

	@SuppressWarnings("unchecked")
    public List<RentableUnit> getRentableUnitList() {
		if (this.rentableUnitList == null && this.selectRealProperty != null) {
			Query query = this.getEntityManager().createQuery("FROM RentableUnit ru WHERE ru.area.floor.construction.realProperty = ?");
			query.setParameter(1, this.selectRealProperty);
			this.rentableUnitList = (List<RentableUnit>) query.getResultList();
		}
		return rentableUnitList;
	}

	public void setRentableUnitList(List<RentableUnit> rentableUnitList) {
		this.rentableUnitList = rentableUnitList;
	}

	public HtmlExtendedDataTable getTableRentableUnit() {
		return tableRentableUnit;
	}

	public void setTableRentableUnit(HtmlExtendedDataTable tableRentableUnit) {
		this.tableRentableUnit = tableRentableUnit;
	}

	public Selection getSelectionTableRentableUnit() {
		return selectionTableRentableUnit;
	}

	public void setSelectionTableRentableUnit(Selection selectionTableRentableUnit) {
		this.selectionTableRentableUnit = selectionTableRentableUnit;
	}

	public String getRentableUnitListTableState() {
		return rentableUnitListTableState;
	}

	public void setRentableUnitListTableState(String rentableUnitListTableState) {
		this.rentableUnitListTableState = rentableUnitListTableState;
	}

	public void newSearch() {
		this.rentableUnitList = null;
	}

	public RentableUnit getSelectedRentableUnit() {
		return selectedRentableUnit;
	}

	public void setSelectedRentableUnit(RentableUnit selectedRentableUnit) {
		this.selectedRentableUnit = selectedRentableUnit;
	}

	public HtmlExtendedDataTable getTableOwnerBind() {
		return tableOwnerBind;
	}

	public void setTableOwnerBind(HtmlExtendedDataTable tableOwnerBind) {
		this.tableOwnerBind = tableOwnerBind;
	}

	public Selection getSelectionOwner() {
		return selectionOwner;
	}

	public void setSelectionOwner(Selection selectionOwner) {
		this.selectionOwner = selectionOwner;
	}

	public String getTableStateOwner() {
		return tableStateOwner;
	}

	public void setTableStateOwner(String tableStateOwner) {
		this.tableStateOwner = tableStateOwner;
	}

	@SuppressWarnings("rawtypes")
    public void onSelectionOwnerChanged() {
		if (selectionOwner != null) {
			System.out.println("Selected keys: ");
			Iterator it = selectionOwner.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				System.out.println("key: " + key);
				System.out.println("tableOwnerBind state" + tableStateOwner);
				tableOwnerBind.setRowKey(key);
				if (tableOwnerBind.isRowAvailable()) {
					if (selectedRentableUnit == null)
						selectedRentableUnit = new RentableUnit();
					selectedRentableUnit.setBusinessEntityByOwner((BusinessEntity) tableOwnerBind.getRowData());
				}
			}
		} else {
			System.out.println("No SelectionOwnerChanged is set.");
		}
	}

	@SuppressWarnings("unchecked")
    public List<BusinessEntity> getOwners() {
		if (this.owners == null) {
			owners = new ArrayList<BusinessEntity>();
			try {
				Query q = this.getEntityManager().createQuery("SELECT  DISTINCT(businessEntity) FROM BusinessEntity businessEntity, EntityType entityType WHERE businessEntity.id=entityType.businessEntity AND entityType.businessEntityType=2");
				List<BusinessEntity> l = (List<BusinessEntity>) q.getResultList();
				Iterator<BusinessEntity> iterator = l.iterator();
				while (iterator.hasNext()) {
					BusinessEntity elem = ((BusinessEntity) iterator.next());
					owners.add(elem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return owners;
		} else {
			return owners;
		}
	}

	public void setOwners(List<BusinessEntity> owners) {
		this.owners = owners;
	}

	public HtmlExtendedDataTable getTableLesseeBind() {
		return tableLesseeBind;
	}

	public void setTableLesseeBind(HtmlExtendedDataTable tableLesseeBind) {
		this.tableLesseeBind = tableLesseeBind;
	}

	public Selection getSelectionLessee() {
		return selectionLessee;
	}

	public void setSelectionLessee(Selection selectionLessee) {
		this.selectionLessee = selectionLessee;
	}

	public String getTableStateLessee() {
		return tableStateLessee;
	}

	public void setTableStateLessee(String tableStateLessee) {
		this.tableStateLessee = tableStateLessee;
	}

	@SuppressWarnings("rawtypes")
    public void onSelectionLesseeChanged() {
		if (selectionLessee != null) {
			System.out.println("Selected keys: ");
			Iterator it = selectionLessee.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				System.out.println("key: " + key);
				System.out.println("tableLesseeBind state" + tableStateLessee);
				tableLesseeBind.setRowKey(key);
				if (tableLesseeBind.isRowAvailable()) {
					if (selectedRentableUnit == null)
						selectedRentableUnit = new RentableUnit();
					selectedRentableUnit.setBusinessEntityByLessee((BusinessEntity) tableLesseeBind.getRowData());
				}
			}
		} else {
			System.out.println("No SelectionOwnerChanged is set.");
		}
	}

	@SuppressWarnings("unchecked")
    public List<BusinessEntity> getLessees() {
		if (this.lessees == null) {
			lessees = new ArrayList<BusinessEntity>();
			try {
				Query q = this.getEntityManager().createQuery("SELECT  DISTINCT(businessEntity) FROM BusinessEntity businessEntity, EntityType entityType WHERE businessEntity.id=entityType.businessEntity AND entityType.businessEntityType=3");
				List<BusinessEntity> l = (List<BusinessEntity>) q.getResultList();
				Iterator<BusinessEntity> iterator = l.iterator();
				while (iterator.hasNext()) {
					BusinessEntity elem = ((BusinessEntity) iterator.next());
					lessees.add(elem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return lessees;
		} else {
			return lessees;
		}
	}

	public void setLessees(List<BusinessEntity> lessees) {
		this.lessees = lessees;
	}

	public void updateRentableUnit() {
		// System.out.println("......................................********"+selectedRentableUnit.getBusinessEntityByOwner().getId());
		this.setInstance(this.selectedRentableUnit);
		super.update();
		cleanPanelOwnerAndLesee();
	}

	public void cleanPanelOwnerAndLesee() {
		this.selectionLessee = null;
		this.selectionOwner = null;
	}

	public void cancelAsignmentAction() {
		getEntityManager().refresh(this.selectedRentableUnit);
		cleanPanelOwnerAndLesee();
	}

	public List<Address> getAddressOwner() {
		if (this.addressOwner == null && this.selectedRentableUnit != null && this.selectedRentableUnit.getBusinessEntityByOwner() != null) {
			Query q = this.getEntityManager().createQuery("from Address cons where cons.businessEntity.id=?");
			q.setParameter(1, this.selectedRentableUnit.getBusinessEntityByOwner().getId());
			@SuppressWarnings("unchecked")
			List<Address> addressOwner = (List<Address>) q.getResultList();
			if (addressOwner != null && !addressOwner.isEmpty()) {
				this.selectedRentableUnit.setAddressByAddressOwner(addressOwner.get(0));

			}
			if (addressOwner == null || addressOwner.isEmpty()) {
				this.selectedRentableUnit.setAddressByAddressOwner(null);
			}
			return addressOwner;
		} else
			return addressOwner;

	}

	public List<PhoneNumber> getPhoneNumberOwner() {
		if (this.phoneNumberOwner == null && this.selectedRentableUnit != null && this.selectedRentableUnit.getBusinessEntityByOwner() != null) {
			Query q = this.getEntityManager().createQuery("from PhoneNumber cons where cons.businessEntity.id=?");
			q.setParameter(1, this.selectedRentableUnit.getBusinessEntityByOwner().getId());
			@SuppressWarnings("unchecked")
			List<PhoneNumber> phoneNumberOwner = (List<PhoneNumber>) q.getResultList();
			if (phoneNumberOwner != null && !phoneNumberOwner.isEmpty()) {
				this.selectedRentableUnit.setPhoneNumberByPhonenumbeOwner(phoneNumberOwner.get(0));

			}
			if (phoneNumberOwner == null || phoneNumberOwner.isEmpty()) {
				this.selectedRentableUnit.setPhoneNumberByPhonenumbeOwner(null);
			}
			return phoneNumberOwner;
		} else
			return phoneNumberOwner;
	}

	public void setPhoneNumberOwner(List<PhoneNumber> phoneNumberOwner) {
		this.phoneNumberOwner = phoneNumberOwner;
	}

	public List<Address> getAddressLessee() {
		if (this.addressLessee == null && this.selectedRentableUnit != null && this.selectedRentableUnit.getBusinessEntityByLessee() != null) {
			Query q = this.getEntityManager().createQuery("from Address cons where cons.businessEntity.id=?");
			q.setParameter(1, this.selectedRentableUnit.getBusinessEntityByLessee().getId());
			@SuppressWarnings("unchecked")
			List<Address> addressLessee = (List<Address>) q.getResultList();
			if (addressLessee != null && !addressLessee.isEmpty()) {
				this.selectedRentableUnit.setAddressByAddressLessee(addressLessee.get(0));
			}
			if (addressLessee == null || addressLessee.isEmpty()) {
				this.selectedRentableUnit.setAddressByAddressLessee(null);
			}
			return addressLessee;
		} else
			return addressLessee;
	}

	public void setAddressLessee(List<Address> addressLessee) {
		this.addressLessee = addressLessee;
	}

	public List<PhoneNumber> getPhoneNumbersLessee() {
		if (this.phoneNumbersLessee == null && this.selectedRentableUnit != null && this.selectedRentableUnit.getBusinessEntityByLessee() != null) {
			Query q = this.getEntityManager().createQuery("from PhoneNumber cons where cons.businessEntity.id=?");
			q.setParameter(1, this.selectedRentableUnit.getBusinessEntityByLessee().getId());
			@SuppressWarnings("unchecked")
			List<PhoneNumber> phoneNumbersLessee = (List<PhoneNumber>) q.getResultList();
			if (phoneNumbersLessee != null && !phoneNumbersLessee.isEmpty()) {
				this.selectedRentableUnit.setPhoneNumberByPhonenumberLessee(phoneNumbersLessee.get(0));

			}
			if (phoneNumbersLessee == null || phoneNumbersLessee.isEmpty()) {
				this.selectedRentableUnit.setPhoneNumberByPhonenumberLessee(null);
			}
			return phoneNumbersLessee;
		} else
			return phoneNumbersLessee;

	}

	public void setPhoneNumbersLessee(List<PhoneNumber> phoneNumbersLessee) {
		this.phoneNumbersLessee = phoneNumbersLessee;
	}

	public void setAddressOwner(List<Address> addressOwner) {
		this.addressOwner = addressOwner;
	}

	public boolean disableButton() {
		if (this.selectedRentableUnit != null && this.selectedRentableUnit.getBusinessEntityByOwner() != null) {
			if (this.selectedRentableUnit.getBusinessEntityByLessee() != null && this.selectedRentableUnit.getAddressByAddressLessee() == null) {
				return true;
			}

			if (this.selectedRentableUnit.getBusinessEntityByLessee() != null && this.selectedRentableUnit.getPhoneNumberByPhonenumberLessee() == null) {
				return true;
			}
			if (this.selectedRentableUnit.getAddressByAddressOwner() == null) {
				return true;

			}
			if (this.selectedRentableUnit.getPhoneNumberByPhonenumbeOwner() == null) {
				return true;
			}
			return false;
		} else
			return true;

	}

	public void cleanLesses() {
		selectedRentableUnit.setBusinessEntityByLessee(null);
	}
	
}