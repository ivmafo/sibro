package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.Recover;
import org.richfaces.component.html.HtmlExtendedDataTable;

@Name("portafolioPenaltyHome")
public class PortafolioPenaltyHome extends EntityHome<Recover> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable htmlInvoicePortafolioPenaltyDataTable;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable htmlInvoiceConceptPortafolioPenaltyDataTable;
	private String portafolioPenaltyListTableState;
	private String portafolioPenaltyInvoiceConceptListTableState;
	private List<Invoice> invoicePortafolioPenaltyList;
	private List<InvoiceConcept> invoiceConceptsPortafolioPenaltyList;
	private String searchName;
	private String searchId;
	private boolean selectedAnyInvoice;
	private boolean selectAllInvoices;
	private boolean selectAllInvoiceConcepts;
	private Date punishmentDate;

	@In(required = false)
	public String projectFilter;

	public void log(Object o) {
		toLog(o == null ? "NULL" : o);
	}

	public void log() {
		toLog("");
	}

	public void toLog(Object o) {
		String prefix = " + + + + + + + " + o.toString();
		Logger.getLogger(this.getClass().toString()).log(Level.INFO, prefix);
	}

	@SuppressWarnings("unchecked")
	public void searchInvoicePortafolioPenaltyList() {
		this.invoicePortafolioPenaltyList = null;
		String sentence = "";
		Integer projectId = Integer.valueOf(projectFilter);
		if (projectId != null && projectId == -1) {
			sentence = "SELECT DISTINCT(inv) FROM  Invoice inv, InvoiceConcept invCon WHERE inv.approved = ? AND inv.invoiceStatus.id = ? AND invCon.invoiceConceptType != ? AND inv.id = invCon.invoice AND invCon.balance > 0 AND lower(inv.nameBilled) like lower(:name) AND inv.idNumberBilled like :nit ORDER BY inv.expeditionDate";
		} else {
			sentence = "SELECT DISTINCT(inv) FROM  Invoice inv, InvoiceConcept invCon WHERE inv.approved = ? AND inv.invoiceStatus.id = ? AND invCon.invoiceConceptType != ? AND inv.id = invCon.invoice AND invCon.invoice.projectProperty.project = "
					+ projectId
					+ " AND invCon.balance > 0 AND lower(inv.nameBilled) like lower(:name) AND inv.idNumberBilled like :nit ORDER BY inv.expeditionDate";
		}
		Query query = this.getEntityManager().createQuery(sentence);
		query.setParameter("name", this.searchName == null ? "%%" : "%"
				+ this.searchName + "%");
		query.setParameter("nit", this.searchId == null ? "%%" : "%"
				+ this.searchId + "%");
		query.setParameter(1, true);
		query.setParameter(2, InvoiceStatus.STATUS_VIGENTE);
		query.setParameter(3, InvoiceConcept.TYPE_CREDIT_NOTE);
		this.invoicePortafolioPenaltyList = query.getResultList();

		if (this.invoicePortafolioPenaltyList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Busqueda",
							"La busqueda no arrojó resultados"));

		} else {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage("Busqueda", "Se encontraron "
							+ this.invoicePortafolioPenaltyList.size()
							+ " resultados"));
		}

	}

	public boolean searchFormEmpty() {
		if ((this.searchName == null || this.searchName.isEmpty())
				&& (this.searchId == null || this.searchId.isEmpty()))
			return true;

		return false;
	}

	public void cleanSearchForm() {
		this.searchName = null;
		this.searchId = null;
	}

	public boolean renderButtonPunishmentBills() {
		return (this.invoicePortafolioPenaltyList != null && this.invoicePortafolioPenaltyList
				.size() > 0)
				&& this.isPortafolioPenaltyInvoiceListSelectionAcceptable()
				&& this.selectedAnyInvoice;
	}

	public boolean renderButtonPunishmentConcepts() {
		return (this.invoiceConceptsPortafolioPenaltyList != null && this.invoiceConceptsPortafolioPenaltyList
				.size() > 0) && isSelectedAnyInvoiceConcept();
	}

	public boolean isPortafolioPenaltyInvoiceListSelectionAcceptable() {
		this.selectedAnyInvoice = false;
		boolean acceptable = true;
		if (this.invoicePortafolioPenaltyList != null) {
			for (Invoice inv1 : this.invoicePortafolioPenaltyList) {
				if (inv1.isSelected()) {
					selectedAnyInvoice = true;
					int clientId = inv1.getBilled().getId();
					for (Invoice inv2 : this.invoicePortafolioPenaltyList) {
						if (inv2.isSelected()
								&& clientId != inv2.getBilled().getId()) {
							acceptable = false;
						}

					}
				}
			}
		}
		return acceptable;
	}

	public boolean isSelectedAnyInvoiceConcept() {
		boolean acceptable = false;
		if (this.invoiceConceptsPortafolioPenaltyList != null) {
			for (InvoiceConcept invoiceConcept : this.invoiceConceptsPortafolioPenaltyList) {
				if (invoiceConcept.isSelected()
						&& invoiceConcept.getPenaltyDate() == null)
					return true;
			}
		}
		return acceptable;
	}

	public void resetInvoiceConceptList() {
		this.invoiceConceptsPortafolioPenaltyList = null;
		this.getInvoiceConceptsPortafolioPenaltyList();
	}

	public List<InvoiceConcept> getInvoiceConceptsPortafolioPenaltyList() {

		if (this.invoiceConceptsPortafolioPenaltyList == null
				&& this.invoicePortafolioPenaltyList != null) {
			this.invoiceConceptsPortafolioPenaltyList = new ArrayList<InvoiceConcept>();
			for (Invoice invoice : this.invoicePortafolioPenaltyList) {
				if (invoice.isSelected()) {
					List<InvoiceConcept> tempInvoiceConcepts = invoice
							.getInvoiceConcepts();
					for (InvoiceConcept invoiceConcept : tempInvoiceConcepts) {
						if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_CREDIT_NOTE)
							continue;
						this.invoiceConceptsPortafolioPenaltyList
								.add(invoiceConcept);
					}
				}
			}
		}
		return this.invoiceConceptsPortafolioPenaltyList;
	}

	/**
	 * Metodo que suma las facturas selecionadas de una lista en recaudo
	 * 
	 * @return sum
	 */
	public double sumInvoice() {
		double sum = 0.0;
		if (this.getInvoicePortafolioPenaltyList() != null) {
			for (Invoice invoice : this.getInvoicePortafolioPenaltyList()) {
				if (invoice.isSelected() == true)
					sum += invoice.totalBalance();
			}
		}
		return sum;
	}

	public void checkAllInvoices() {
		for (Invoice invoice : this.invoicePortafolioPenaltyList) {
			invoice.setSelected(this.selectAllInvoices);
		}
		this.isPortafolioPenaltyInvoiceListSelectionAcceptable();
	}

	public void checkAllInvoiceConcepts() {
		for (InvoiceConcept invoiceConcept : this.invoiceConceptsPortafolioPenaltyList) {
			if (invoiceConcept.getPenaltyDate() == null)
				invoiceConcept.setSelected(this.selectAllInvoiceConcepts);
		}
		this.isPortafolioPenaltyInvoiceListSelectionAcceptable();
	}

	public void savePortafolioPenalty() {
		if (this.invoiceConceptsPortafolioPenaltyList != null) {
			for (InvoiceConcept invoiceConcept : this.invoiceConceptsPortafolioPenaltyList) {
				if (invoiceConcept.isSelected()
						&& invoiceConcept.getPenaltyDate() == null) {
					invoiceConcept.setPenaltyDate(this.punishmentDate);
					this.persistObject(invoiceConcept);
				}
			}
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage("Castigo de Cartera", "Proceso Exitoso"));
	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public boolean persistObject(Object entity) {
		try {
			this.getEntityManager().joinTransaction();
			this.getEntityManager().persist(entity);
			this.getEntityManager().flush();
			return true;
		} catch (Exception e) {
			try {
				InvalidValue[] arr = ((InvalidStateException) e)
						.getInvalidValues();
				for (InvalidValue invalidValue : arr) {
					System.out.println(invalidValue.getPropertyName() + " "
							+ invalidValue.getValue());
				}
			} catch (Exception e2) {
			}
			log("ERROR, could not persist entity: " + entity.getClass());
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * 
	 * ___________________________________________________
	 * 
	 * 
	 * GETTERS AND SETTERS
	 * 
	 * 
	 * 
	 * ___________________________________________________
	 */

	public String getPortafolioPenaltyListTableState() {
		return portafolioPenaltyListTableState;
	}

	public void setPortafolioPenaltyListTableState(
			String portafolioPenaltyListTableState) {
		this.portafolioPenaltyListTableState = portafolioPenaltyListTableState;
	}

	public String getPortafolioPenaltyInvoiceConceptListTableState() {
		return portafolioPenaltyInvoiceConceptListTableState;
	}

	public void setPortafolioPenaltyInvoiceConceptListTableState(
			String portafolioPenaltyInvoiceConceptListTableState) {
		this.portafolioPenaltyInvoiceConceptListTableState = portafolioPenaltyInvoiceConceptListTableState;
	}

	public List<Invoice> getInvoicePortafolioPenaltyList() {
		return invoicePortafolioPenaltyList;
	}

	public void setInvoicePortafolioPenaltyList(
			List<Invoice> invoicePortafolioPenaltyList) {
		this.invoicePortafolioPenaltyList = invoicePortafolioPenaltyList;
	}

	public void setInvoiceConceptsPortafolioPenaltyList(
			List<InvoiceConcept> invoiceConceptsPortafolioPenaltyList) {
		this.invoiceConceptsPortafolioPenaltyList = invoiceConceptsPortafolioPenaltyList;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public HtmlExtendedDataTable getHtmlInvoicePortafolioPenaltyDataTable() {
		return htmlInvoicePortafolioPenaltyDataTable;
	}

	public void setHtmlInvoicePortafolioPenaltyDataTable(
			HtmlExtendedDataTable htmlInvoicePortafolioPenaltyDataTable) {
		this.htmlInvoicePortafolioPenaltyDataTable = htmlInvoicePortafolioPenaltyDataTable;
	}

	public HtmlExtendedDataTable getHtmlInvoiceConceptPortafolioPenaltyDataTable() {
		return htmlInvoiceConceptPortafolioPenaltyDataTable;
	}

	public void setHtmlInvoiceConceptPortafolioPenaltyDataTable(
			HtmlExtendedDataTable htmlInvoiceConceptPortafolioPenaltyDataTable) {
		this.htmlInvoiceConceptPortafolioPenaltyDataTable = htmlInvoiceConceptPortafolioPenaltyDataTable;
	}

	public boolean isSelectedAnyInvoice() {
		return selectedAnyInvoice;
	}

	public void setSelectedAnyInvoice(boolean selectedAnyInvoice) {
		this.selectedAnyInvoice = selectedAnyInvoice;
	}

	public boolean isSelectAllInvoices() {
		return selectAllInvoices;
	}

	public void setSelectAllInvoices(boolean selectAllInvoices) {
		this.selectAllInvoices = selectAllInvoices;
	}

	public Date getPunishmentDate() {
		if (this.punishmentDate == null)
			punishmentDate = new Date();
		return punishmentDate;
	}

	public void setPunishmentDate(Date punishmentDate) {
		this.punishmentDate = punishmentDate;
	}

	public boolean isSelectAllInvoiceConcepts() {
		return selectAllInvoiceConcepts;
	}

	public void setSelectAllInvoiceConcepts(boolean selectAllInvoiceConcepts) {
		this.selectAllInvoiceConcepts = selectAllInvoiceConcepts;
	}

	public String getState(Invoice invoice) {
		boolean aux = true;

		/*
		 * Se inicia la variable asumiendo que todo se encuentra vigente
		 */
		String res = "Vigente";

		/*
		 * Se verifica si hay al menos un invoiceconcept castigado
		 */
		for (InvoiceConcept invoiceConcept : invoice.getInvoiceConcepts()) {
			if (invoiceConcept.getPenaltyDate() != null) {
				res = "Castigo Parcial";
			} else
				aux = false;

		}

		/*
		 * aux queda en tru si todos los invoice concept estan catigados
		 */
		if (aux) {
			res = "Castigada";
		}

		return res;
	}
}
