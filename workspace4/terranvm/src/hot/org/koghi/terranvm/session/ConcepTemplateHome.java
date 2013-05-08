package org.koghi.terranvm.session;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.koghi.terranvm.entity.*;
import org.richfaces.component.html.HtmlExtendedDataTable;
import org.richfaces.model.selection.Selection;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;




@Name("concepTemplateHome")
public class ConcepTemplateHome extends EntityHome<ConcepTemplate> {

	public void setConcepTemplateId(Integer id) {
		setId(id);
	}

	public Integer getConcepTemplateId() {
		return (Integer) getId();
	}

	@Override
	protected ConcepTemplate createInstance() {
		ConcepTemplate concepTemplate = new ConcepTemplate();
		return concepTemplate;
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

	public ConcepTemplate getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	
	
	private Selection systemVaribleSelection;
	public Selection getSystemVaribleSelection() {
		return systemVaribleSelection;
	}
	public void setSystemVaribleSelection(Selection systemVaribleSelection) {
		this.systemVaribleSelection = systemVaribleSelection;
	}
	
	private String tableState;
	public String getTableState() {
		return tableState;
	}
	public void setTableState(String tableState) {
		this.tableState = tableState;
	}
	
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tablesSystemVAriableBind;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tablesSystemVAriableBindEdit;
	
	
	
	
	public HtmlExtendedDataTable getTablesSystemVAriableBindEdit() {
		return tablesSystemVAriableBindEdit;
	}
	public void setTablesSystemVAriableBindEdit(HtmlExtendedDataTable tablesSystemVAriableBindEdit) {
		this.tablesSystemVAriableBindEdit = tablesSystemVAriableBindEdit;
	}

	public HtmlExtendedDataTable getTablesSystemVAriableBind() {
		return tablesSystemVAriableBind;
	}
	public void setTablesSystemVAriableBind(HtmlExtendedDataTable tablesSystemVAriableBind) {
		this.tablesSystemVAriableBind = tablesSystemVAriableBind;
	}
	
	
	/**
	 * Metodo que agrega a la foruma del concepto la variable seleccionada
	 */
	public void onSelectionChangedSystemVariable() {
		if (systemVaribleSelection != null) {
			System.out.println("Selected keys: " + systemVaribleSelection);
			Iterator<Object> it = systemVaribleSelection.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				System.out.println("key: " + key);
				System.out.println("tablePaymentFormBind state" + tableState);
				tablesSystemVAriableBind.setRowKey(key);
				if (tablesSystemVAriableBind.isRowAvailable()) {
					String expression = this.instance.getExpression() == null ? "" : this.instance.getExpression();
					expression += ((SystemVariable) tablesSystemVAriableBind.getRowData()).getSintax();
					this.instance.setExpression(expression);
					this.validateExpression();
					this.systemVaribleSelection = null;
					break;
				}
			}

		} else {
			System.out.println("No selectionLegalRepresentative is set.");
		}

	}
	
	/**
	 * Metodo que valida si el concepto ingresado, Tiene un sintaxis correcta
	 */
	public void validateExpression()
	{
		/**
		 * Se Inicializan variables de Incrementos
		 */
		IpcYearly ipcYaerly = new IpcYearly();
		String ipcYearlyString = ipcYaerly.getIpcs(this.getEntityManager());

		IpcMonthly ipcMonthly = new IpcMonthly();
		String ipcMonthlyString = ipcMonthly.getIpcs(this.getEntityManager());
		/**
		 * Se verifica si Tiene Venta la Hoja de Termino
		 */
		Sales sales = null;
		if (this.getRetRentableUnit() != null) {
			Query q = this.getEntityManager().createQuery("from Sales sales where sales.rentableUnit.id = ? and salesPeriod.year = ? and salesPeriod.month = ? ");
			q.setParameter(1, this.getRetRentableUnit().getId());
			q.setParameter(2, Calendar.getInstance().get(Calendar.YEAR));
			q.setParameter(3, Calendar.getInstance().get(Calendar.MONTH));
			List<Sales> saleList = (List<Sales>) q.getResultList();
			if (saleList.size() > 0) {
				sales = saleList.get(0);
			}

		}
	
	}

	public RentableUnit retRentableUnit;
	
	public RentableUnit getRetRentableUnit() {
		return retRentableUnit;
	}

	public void setRetRentableUnit(RentableUnit retRentableUnit) {
		this.retRentableUnit = retRentableUnit;
	}
	
	
	public String True_False(boolean x)
	{
		if(x==true)
			return "Si";
		else
			return "No";
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
