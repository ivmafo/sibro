package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.Sales;
import org.koghi.terranvm.entity.SalesPeriod;
import org.richfaces.component.html.HtmlExtendedDataTable;

@Name("salesHome")
public class SalesHome extends EntityHome<Sales> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(create = true)
	RentableUnitHome rentableUnitHome;

	@In(required = false)
	public String projectFilter;

	// Ventas
	private Integer monthSelected;
	private Integer yearSelected;
	private SalesPeriod salesPeriod;
	private List<Sales> saleses;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableSalesBind;
	private String salesTableState;
	private RealProperty selectRealProperty;
	private List<RentableUnit> rentableUnitList;

	public void setSalesId(Integer id) {
		setId(id);
	}

	public Integer getSalesId() {
		return (Integer) getId();
	}

	@Override
	protected Sales createInstance() {
		Sales sales = new Sales();
		return sales;
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

	}

	public boolean isWired() {
		if (getInstance().getRentableUnit() == null)
			return false;
		if (getInstance().getSalesPeriod() == null)
			return false;
		return true;
	}

	public Sales getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public RealProperty getSelectRealProperty() {
		return selectRealProperty;
	}

	public void setSelectRealProperty(RealProperty selectRealProperty) {
		this.selectRealProperty = selectRealProperty;
	}

	@SuppressWarnings("unchecked")
	public List<RealProperty> getRealproperties() {
		List<RealProperty> m = new ArrayList<RealProperty>(0);
		if (projectFilter != null && projectFilter.equals("-1")) {
			Query q = this.getEntityManager().createQuery("FROM RealProperty RP ORDER BY RP.nameProperty");
			m = (List<RealProperty>) q.getResultList();

		} else if (projectFilter != null) {
			Query q = this.getEntityManager().createNativeQuery("SELECT rp.* FROM real_property rp, project_has_realproperty prp WHERE prp.realproperty = rp.id AND prp.project = " + projectFilter + " ORDER BY rp.name_property", RealProperty.class);
			m = (List<RealProperty>) q.getResultList();
		}

		if (m != null && !m.isEmpty() && this.selectRealProperty == null) {
			this.selectRealProperty = m.get(0);
		}
		return m;
	}

	@SuppressWarnings("unchecked")
	public List<RentableUnit> getRentableUnitList() {
		if ((this.rentableUnitList == null || this.rentableUnitList.isEmpty()) && this.selectRealProperty != null && this.selectRealProperty.getId() > 0) {
			Query query = this.getEntityManager().createQuery("FROM RentableUnit ru WHERE ru.area.floor.construction.realProperty = ?");
			query.setParameter(1, this.selectRealProperty);
			this.rentableUnitList = (List<RentableUnit>) query.getResultList();
		}
		return rentableUnitList;
	}

	public void setRentableUnitList(List<RentableUnit> rentableUnitList) {
		this.rentableUnitList = rentableUnitList;
	}

	public List<SelectItem> getMonths() {
		String[] months = SalesPeriod.MONTHS;
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (int i = 0; i < months.length; i++) {
			SelectItem item = new SelectItem();
			item.setValue(new Integer(i));
			item.setLabel(months[i]);
			items.add(item);
		}

		if (monthSelected == null && items.size() > 0) {
			monthSelected = (Integer) items.get(0).getValue();
		}
		return items;
	}

	public List<SelectItem> getYears() {
		List<Integer> years = SalesPeriod.getYears();
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (int i = 0; i < years.size(); i++) {
			SelectItem item = new SelectItem();
			item.setValue(years.get(i));
			item.setLabel(years.get(i) + "");
			items.add(item);
		}

		if (yearSelected == null && items.size() > 0) {
			yearSelected = (Integer) items.get(0).getValue();
		}
		return items;
	}

	public Integer getMonthSelected() {
		return monthSelected;
	}

	public void setMonthSelected(Integer monthSelected) {
		this.monthSelected = monthSelected;
	}

	public Integer getYearSelected() {
		return yearSelected;
	}

	public void setYearSelected(Integer yearSelected) {
		this.yearSelected = yearSelected;
	}

	public List<Sales> getSaleses() {
		if (this.yearSelected != null && this.monthSelected != null && this.saleses == null) {
			saleses = new ArrayList<Sales>();
			try {
				for (SalesPeriod salesPeriodAux : this.getSelectRealProperty().getSalesPeriods()) {
					if (salesPeriodAux.getYear() == this.yearSelected.intValue() && salesPeriodAux.getMonth() == this.monthSelected.intValue()) {
						saleses = salesPeriodAux.getSaleses();
						break;
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

			if (saleses != null && !saleses.isEmpty()) {
				salesPeriod = saleses.get(0).getSalesPeriod();
			} else {
				rentableUnitList = null;
				salesPeriod = new SalesPeriod();
				salesPeriod.setYear(this.yearSelected);
				salesPeriod.setMonth(this.monthSelected);
				salesPeriod.setRealProperty(this.selectRealProperty);

				List<RentableUnit> rentableUnits = this.getRentableUnitList();
				try {
					for (RentableUnit rentableUnit : rentableUnits) {
						Sales sales = new Sales();
						sales.setRentableUnit(rentableUnit);
						sales.setSalesPeriod(salesPeriod);
						sales.setBusinessEntity(sales.getRentableUnit().getBusinessEntityByLessee());
						saleses.add(sales);
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
				salesPeriod.setSaleses(saleses);

			}
		}

		return saleses;
	}

	public void setSaleses(List<Sales> saleses) {
		this.saleses = saleses;
	}

	@Transactional
	public void salesSave() {
		if (this.yearSelected != null && this.monthSelected != null && salesPeriod != null) {
			List<SalesPeriod> salesPeriods = (this.getSelectRealProperty().getSalesPeriods() != null ? this.getSelectRealProperty().getSalesPeriods() : new ArrayList<SalesPeriod>());
			if (salesPeriods.isEmpty()) {
				salesPeriods.add(salesPeriod);
			} else {
				// Bandera para saber si existe this.salesPeriod en la lista
				// salesPeriods
				boolean flagIn = false;
				for (SalesPeriod salesPeriod : salesPeriods) {
					if (salesPeriod.getId() != 0 && salesPeriod.getId() == this.salesPeriod.getId()) {
						salesPeriod.setMonth(this.salesPeriod.getMonth());
						salesPeriod.setYear(this.salesPeriod.getYear());
						salesPeriod.setRealProperty(this.salesPeriod.getRealProperty());
						flagIn = true;
						break;
					}
				}
				if (!flagIn) {
					salesPeriods.add(this.salesPeriod);
				}
			}
		}
		// Session session = (Session) getEntityManager().getDelegate();
		this.getEntityManager().persist((this.selectRealProperty));
		this.getEntityManager().flush();
		if (getInstance() != null && selectRealProperty != null) {

			/*
			 * 2012-12-10 @dvaldivieso Según definiciónes ya no se debe cobrar
			 * retroactivo de ventas. Se debe solo recalcular los valores de
			 * facturas no aprobadas
			 */
			// retroactivo();
			recalculo(this.selectRealProperty);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Se actualizaron las ventas"));

		}
	}

	@SuppressWarnings("unused")
	private boolean retroactivo() {
		BillingFunctions billing = new BillingFunctions(this.getEntityManager());
		return billing.calculateRetroactive(this.instance, (selectRealProperty != null ? selectRealProperty.getId() : null));
	}

	private boolean recalculo(RealProperty realProperty) {
		BillingFunctions billing = new BillingFunctions(this.getEntityManager());
		return billing.recalculateVariable(realProperty, "VTAS");
	}

	public HtmlExtendedDataTable getTableSalesBind() {
		return tableSalesBind;
	}

	public void setTableSalesBind(HtmlExtendedDataTable tableSalesBind) {
		this.tableSalesBind = tableSalesBind;
	}

	public String getSalesTableState() {
		return salesTableState;
	}

	public void setSalesTableState(String salesTableState) {
		this.salesTableState = salesTableState;
	}

	public void cleanSalesTeable() {
		this.saleses = null;
	}

	public void newSearch() {
		this.saleses = null;
	}

	@Override
	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
		}
	}

	public String nameRentableUnitDesactivate(String name) {
		return name + " (Desactivado)";

	}
}
