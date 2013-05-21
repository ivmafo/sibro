package org.koghi.terranvm.session;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.bean.Format_number;
import org.koghi.terranvm.bean.MailSender;
import org.koghi.terranvm.bean.SiigoFunctions;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.ConceptRetentionRateAccount;
import org.koghi.terranvm.entity.ConsecutiveCreditNotes;
import org.koghi.terranvm.entity.CreditNote;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RecoverConcept;
import org.koghi.terranvm.entity.RetentionRate;
import org.koghi.terranvm.entity.RetentionRateAccount;
import org.koghi.terranvm.entity.SystemConfiguration;
import org.koghi.terranvm.entity.User_Terranvm;
import org.richfaces.component.html.HtmlExtendedDataTable;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Name("creditNoteHome")
public class CreditNoteHome extends EntityHome<CreditNote> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(required = false)
	public String projectFilter;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable htmlRevertDataTable;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable htmlRevertInvoiceConceptDataTable;
	private String revertListTableState;
	private String revertConceptListTableState;
	private List<Invoice> invoiceRevertList;
	private List<InvoiceConcept> invoiceConceptsRevertList;
	private String searchId;
	private String searchName;
	private boolean selectAllInvoiceConcepts;
	private String facNumber;
	private Date creditNoteDate;
	private String dateValidateMessage = "";
	private org.koghi.terranvm.async.Log log = new org.koghi.terranvm.async.Log(this);
	private double ivaTotal = 0.0;
	private String creditNoteReason;

	@In(required = false)
	public List<SelectItem> projectsFilter;

	private SiigoFunctions siigoFunctions;

	private boolean fixed;

	public ConvertNumberToString convertNumberToString = new ConvertNumberToString();

	public SystemConfiguration systemConfiguration = new SystemConfiguration();

	// --------
	// private private List<InvoiceConcept> invoiceConceptCreditNotes;
	public List<InvoiceConcept> invoiceConceptsCreditNotes;

	public List<MakerChecker> creditNoteListMakerChecker;

	// --------

	//
	private boolean selectedAnyInvoice;

	/**
	 * This function prints a message in log file
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	private void log(Level level, Object message) {
		BillingTools.printLog(CreditNoteHome.class, level, message);
	}

	public boolean isSelectedAnyInvoice() {
		return selectedAnyInvoice;
	}

	public void setSelectedAnyInvoice(boolean selectedAnyInvoice) {
		this.selectedAnyInvoice = selectedAnyInvoice;
	}

	//

	public void setCreditNoteId(Integer id) {
		setId(id);
	}

	public Integer getCreditNoteId() {
		return (Integer) getId();
	}

	@Override
	protected CreditNote createInstance() {
		CreditNote creditNote = new CreditNote();
		return creditNote;
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

	public CreditNote getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public HtmlExtendedDataTable getHtmlRevertDataTable() {
		return htmlRevertDataTable;
	}

	public void setHtmlRevertDataTable(HtmlExtendedDataTable htmlRevertDataTable) {
		this.htmlRevertDataTable = htmlRevertDataTable;
	}

	public String getRevertListTableState() {
		return revertListTableState;
	}

	public void setRevertListTableState(String revertListTableState) {
		this.revertListTableState = revertListTableState;
	}

	public List<Invoice> getInvoiceRevertList() {
		return invoiceRevertList;
	}

	public void setInvoiceRevertList(List<Invoice> invoiceRevertList) {
		this.invoiceRevertList = invoiceRevertList;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	@SuppressWarnings("unchecked")
	public void searchInvoicesToRevert() {
		this.invoiceRevertList = null;
		Integer projectId = Integer.parseInt(this.projectFilter);
		String sentence = "SELECT DISTINCT(inv) FROM  Invoice inv, InvoiceConcept invCon WHERE inv.approved = ? AND inv.invoiceStatus.id = ? AND invCon.invoiceConceptType != ? AND invCon.creditNoteStatus = ?  AND inv.id = invCon.invoice AND invCon.balance > 0 AND lower(inv.nameBilled) like lower(:name) AND inv.idNumberBilled like :nit ORDER BY inv.expeditionDate";

		// Se adiciona el filtro del proyecto del mundo si este es diferente de
		// -1 (-1 es la opción de "Filtrar Todos")
		if (projectId != null && projectId != -1) {
			sentence = "SELECT DISTINCT(inv) FROM  Invoice inv, InvoiceConcept invCon WHERE inv.approved = ? AND inv.invoiceStatus.id = ? AND invCon.invoiceConceptType != ? AND invCon.creditNoteStatus = ? AND inv.id = invCon.invoice " + "AND invCon.balance > 0 AND invCon.invoice.projectProperty.project = " + projectId + "AND lower(inv.nameBilled) like lower(:name) AND inv.idNumberBilled like :nit ORDER BY inv.expeditionDate";
		}

		Query query = this.getEntityManager().createQuery(sentence);
		query.setParameter("name", this.searchName == null ? "%%" : "%" + this.searchName + "%");
		query.setParameter("nit", this.searchId == null ? "%%" : "%" + this.searchId + "%");
		query.setParameter(1, true);
		query.setParameter(2, InvoiceStatus.STATUS_VIGENTE);
		query.setParameter(3, InvoiceConcept.TYPE_CREDIT_NOTE);
		query.setParameter(4, InvoiceConcept.CREDIT_NOTE_STATUS_NONE);
		this.invoiceRevertList = query.getResultList();
		if (this.invoiceRevertList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "NOTAS CRÉDITO", "No se econtraron facturas"));

		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("NOTAS CRÉDITO", "Se encontraron " + this.invoiceRevertList.size() + " facturas"));
		}

	}

	public void cleanSearchForm() {
		this.searchName = null;
		this.searchId = null;
	}

	public HtmlExtendedDataTable getHtmlRevertInvoiceConceptDataTable() {
		return htmlRevertInvoiceConceptDataTable;
	}

	public void setHtmlRevertInvoiceConceptDataTable(HtmlExtendedDataTable htmlRevertInvoiceConceptDataTable) {
		this.htmlRevertInvoiceConceptDataTable = htmlRevertInvoiceConceptDataTable;
	}

	public String getRevertConceptListTableState() {
		return revertConceptListTableState;
	}

	public void setRevertConceptListTableState(String revertConceptListTableState) {
		this.revertConceptListTableState = revertConceptListTableState;
	}

	public void resetInvoiceConceptList() {
		this.invoiceConceptsRevertList = null;
		this.getInvoiceConceptsRevertList();
	}

	public boolean searchFormEmpty() {
		if ((this.searchName == null || this.searchName.isEmpty()) && (this.searchId == null || this.searchId.isEmpty()))
			return true;
		return false;
	}

	public void checkAllInvoices() {
		for (Invoice invoice : this.invoiceRevertList) {

			invoice.setSelected(this.selectAllInvoices);
		}
		this.isRevertInvoiceListSelectionAcceptable();
	}

	public boolean isSelectAllInvoiceConcepts() {
		return selectAllInvoiceConcepts;
	}

	public void setSelectAllInvoiceConcepts(boolean selectAllInvoiceConcepts) {
		this.selectAllInvoiceConcepts = selectAllInvoiceConcepts;
	}

	public boolean canCreateCreditNote() {
		if (this.invoiceConceptsRevertList != null) {
			for (InvoiceConcept invcon : this.invoiceConceptsRevertList) {
				if (invcon.isSelected())
					return true;
			}
		}
		return false;
	}

	public String getFacNumber() {
		return facNumber;
	}

	public void setFacNumber(String facNumber) {
		this.facNumber = facNumber;
	}

	// @Transactional(TransactionPropagationType.REQUIRED)
	// public boolean saveCreditNote(InvoiceConcept invcon) {
	//
	// try {
	// ConsecutiveCreditNotes ccn =
	// invcon.getConcept().getProjectProperty().getCreditNotes();
	// String consecutive = ccn.getMin()+"";
	// CreditNote cn = new CreditNote();
	// cn.setConsecutive(consecutive);
	// cn.setCreditNoteDate(new Date());
	// cn.setValue(invcon.getBalance());
	// invcon.setCreditNote(cn);
	// ccn.setMin(ccn.getMin()+1);
	//
	// this.getEntityManager().joinTransaction();
	// this.getEntityManager().persist(ccn);
	// this.getEntityManager().flush();
	// this.getEntityManager().joinTransaction();
	// this.getEntityManager().persist(invcon);
	// this.getEntityManager().flush();
	// FacesContext.getCurrentInstance().addMessage(null, new
	// FacesMessage("Reversión", "Reversión de concepto(s) exitosa"));
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// FacesContext.getCurrentInstance().addMessage(null, new
	// FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", e.getMessage()
	// +", Invoice Concept: "+ invcon.getId()));
	// return false;
	// }
	//
	//
	// }
	@In(required = false)
	private User_Terranvm user;

	@Transactional(TransactionPropagationType.REQUIRED)
	public void saveCreditNoteForApprove() {
		BillingTools billingTools = new BillingTools(this.getEntityManager());
		double total = 0.0;
		// String consecutive="";
		Project project = new Project();
		HashMap<Integer, InvoiceConcept> invoiceConceptWithInterestList = new HashMap<Integer, InvoiceConcept>();
		// ProjectProperty projectProperty = new ProjectProperty();

		try {
			CreditNote creditNote = new CreditNote();

			for (InvoiceConcept invcon : this.invoiceConceptsRevertList) {
				if (BillingTools.formatDouble(invcon.getRecoverValue()) > BillingTools.formatDouble(BillingTools.CERO)) {
					project = invcon.getInvoice().getProjectProperty().getProject();
					invcon.setCreditNoteStatus(InvoiceConcept.CREDIT_NOTE_STATUS_APPROVAL_PENDING);
					billingTools.persistObject(invcon);
					creditNote.getInvoiceConcepts().add(createInvoiceConcept(invcon));
					if (invcon.getInvoiceConceptParent() == null) {
						invoiceConceptWithInterestList.put(invcon.getId(), invcon);
					}
					total += invcon.getRecoverValue();
				}
			}
			for (InvoiceConcept invcon : invoiceConceptWithInterestList.values()) {
				total += interest(invcon, creditNote, billingTools);
			}

			creditNote.setCreditNoteDate(this.creditNoteDate);
			creditNote.setReason(this.ReasonCreditNote);
			creditNote.setValue(BillingTools.formatDouble(total));
			creditNote.setUserEmail(user.getMail());

			new MakerCheckerHome().persistObject(creditNote, project);
			this.ReasonCreditNote = "";
			this.creditNoteDate = new Date();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("NOTA CREDITO", "La Nota Crédito ha sido creada exitosamente"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOTA CREDITO", "Se ha generado un error al guardar"));
		}

		closeAllModal();

	}

	public double interest(InvoiceConcept invoiceConcept, CreditNote creditNote, BillingTools billingTools) {
		double total = 0.0;
		if (invoiceConcept != null && creditNote != null && billingTools != null) {
			double interestCreditNoteValue;
			int days;
			for (InvoiceConcept invCon : invoiceConcept.getInvoiceConceptChildren()) {
				boolean condition1 = invCon.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST;
				boolean condition2 = invCon.getBalance() > BillingTools.formatDouble(BillingTools.CERO);
				if (condition1 && condition2) {

					days = this.daysDifference(invCon.getIniPeriodDate(), invCon.getEndPeriodDate()) + 1;
					interestCreditNoteValue = ((invoiceConcept.getBalance() - invoiceConcept.getRecoverValue()) * invCon.getAppliedRate() * days);
					if (interestCreditNoteValue > BillingTools.formatDouble(BillingTools.CERO)) {
						invCon.setRecoverValue(BillingTools.formatDouble(Math.min(invCon.getBalance(), interestCreditNoteValue)));
						if (invCon.getInvoice().isApproved()) {
							total += interestCreditNoteValue;
							creditNote.getInvoiceConcepts().add(createInvoiceConcept(invCon));
						}
						invCon.setCreditNoteStatus(InvoiceConcept.CREDIT_NOTE_STATUS_APPROVAL_PENDING);
						billingTools.persistObject(invCon);
					}

				}
			}
		}
		return total;
	}

	// public void createInvoiceConcept(InvoiceConcept invcon)
	public InvoiceConcept createInvoiceConcept(InvoiceConcept invcon) {
		double cost = 0.0;
		double tax = 0.0;
		double retention = 0.0;
		this.siigoFunctions = new SiigoFunctions(this.getEntityManager());

		InvoiceConcept invConceptCreditNote = new InvoiceConcept();
		invConceptCreditNote = (InvoiceConcept) invcon.clone();
		invConceptCreditNote.setInvoiceConceptChildren(new ArrayList<InvoiceConcept>());
		invConceptCreditNote.setRecoverConcepts(new ArrayList<RecoverConcept>());
		invConceptCreditNote.setId(0);
		invConceptCreditNote.setInvoiceConceptType(InvoiceConcept.TYPE_CREDIT_NOTE);
		invConceptCreditNote.setInvoiceConceptParent(invcon);
		invConceptCreditNote.setDiscount(validate());
		invConceptCreditNote.setReason(ReasonCreditNote);
		invConceptCreditNote.getInvoice().getProjectProperty().getId();

		// Recupero el valor base de acuerdo al valor de la nota
		// credito
		cost = invcon.getRecoverValue();

		// calculo del iva y valor base
		if (invcon.getConcept().getTax() != null && invcon.getConcept().getTax().getValue() != null) {
			cost = invcon.getRecoverValue() / (1 + (invcon.getConcept().getTax().getValue() / 100));
			tax = (invcon.getConcept().getTax().getValue() / 100) * cost;
		}

		invConceptCreditNote.setCalculatedTax(BillingTools.formatDouble(tax));
		invConceptCreditNote.setCalculatedCost(BillingTools.formatDouble(cost));

		retention = this.siigoFunctions.getRetentionValue(invConceptCreditNote);

		// Se realiza un balance de la nota credito sumando el valor
		// del
		// concepto y su iva, y restando las retenciones.
		invConceptCreditNote.setBalance(BillingTools.formatDouble(invConceptCreditNote.getTotal() - retention));

		return invConceptCreditNote;
	}

	public boolean isRevertInvoiceListSelectionAcceptable() {
		this.selectedAnyInvoice = false;
		boolean acceptable = true;
		if (this.invoiceRevertList != null) {
			for (Invoice inv1 : this.invoiceRevertList) {
				if (inv1.isSelected()) {
					selectedAnyInvoice = true;
					int clientId = inv1.getBilled().getId();
					int projectPropertyId = inv1.getProjectProperty().getId();
					for (Invoice inv2 : this.invoiceRevertList) {
						if (inv2.isSelected() && clientId != inv2.getBilled().getId()) {
							acceptable = false;
						}
						if (inv2.isSelected() && projectPropertyId != inv2.getProjectProperty().getId()) {
							acceptable = false;
						}

					}
				}
			}
		}
		return acceptable;
	}

	public boolean renderButtonRevert() {
		return (this.invoiceRevertList != null && this.invoiceRevertList.size() > 0) && this.isRevertInvoiceListSelectionAcceptable() && this.selectedAnyInvoice;
	}

	/**
	 * Metodo que suma las facturas selecionadas de una lista en recaudo
	 * 
	 * @return sum
	 */
	public double sumInvoice() {
		double sum = 0.0;
		List<InvoiceConcept> invCon;
		if (this.getInvoiceRevertList() != null) {
			for (Invoice invoice : this.getInvoiceRevertList()) {
				if (invoice.isSelected() == true) {
					invCon = invoice.getInvoiceConcepts();
					for (InvoiceConcept invoiceConcept : invCon) {
						if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_CREDIT_NOTE)
							continue;
						if (invoiceConcept.getCreditNoteStatus() == InvoiceConcept.CREDIT_NOTE_STATUS_NONE)
							sum += Double.parseDouble(totalConcepto(invoiceConcept));

					}
				}
			}
		}
		return sum;
	}

	private boolean selectAllInvoices;

	public boolean isSelectAllInvoices() {
		return selectAllInvoices;
	}

	public void setSelectAllInvoices(boolean selectAllInvoices) {
		this.selectAllInvoices = selectAllInvoices;
	}

	private Double RecoverTotalValue;

	public Double getRecoverTotalValue() {
		if (RecoverTotalValue == null)
			return 0.00;

		return RecoverTotalValue;
	}

	public void setRecoverTotalValue(Double RecoverTotalValue) {
		if (RecoverTotalValue != null)
			this.RecoverTotalValue = RecoverTotalValue;
		else
			this.RecoverTotalValue = 0.00;
	}

	public double doubleMaxValue() {
		return BillingTools.formatDouble(BillingTools.MAX_DOUBLE_VALUE);
	}

	public boolean acceptableTotalRevertValue() {

		if (this.dateValidateMessage != null) {
			return false;
		} else if (this.fixed == true) {
			if (this.RecoverTotalValue != null && this.RecoverTotalValue > 0.00 && this.ReasonCreditNote != null && !this.ReasonCreditNote.isEmpty()) {
				return true;
			} else
				return false;
		} else if (this.fixed == false) {
			if (this.ReasonCreditNote != null && !this.ReasonCreditNote.isEmpty())
				return true;
			else
				return false;
		} else
			return false;
	}

	private double totalSelectedConcept;

	public double getTotalSelectedConcept() {
		return BillingTools.formatDouble(totalSelectedConcept);
	}

	public void setTotalSelectedConcept(double totalSelectedConcept) {
		this.totalSelectedConcept = totalSelectedConcept;
	}

	private String revertInvoiceConceptListTableState;

	public String getRevertInvoiceConceptListTableState() {
		return revertInvoiceConceptListTableState;
	}

	public void setRevertInvoiceConceptListTableState(String revertInvoiceConceptListTableState) {
		this.revertInvoiceConceptListTableState = revertInvoiceConceptListTableState;
	}

	public void setInvoiceConceptsRevertList(List<InvoiceConcept> invoiceConcepts) {
		this.invoiceConceptsRevertList = invoiceConcepts;
	}

	public List<InvoiceConcept> getInvoiceConceptsRevertList() {
		// BusinessEntity client = null;
		if (this.invoiceConceptsRevertList == null && this.invoiceRevertList != null) {
			this.totalSelectedConcept = BillingTools.CERO;
			this.invoiceConceptsRevertList = new ArrayList<InvoiceConcept>();
			for (Invoice invoice : this.invoiceRevertList) {
				if (invoice.isSelected()) {
					// client =
					// invoice.getBilled();
					List<InvoiceConcept> tempInvoiceConcepts = invoice.getInvoiceConcepts();
					for (InvoiceConcept invoiceConcept : tempInvoiceConcepts) {
						if (invoiceConcept.getBalance().doubleValue() > BillingTools.CERO && invoiceConcept.getInvoiceConceptType() != InvoiceConcept.TYPE_CREDIT_NOTE) {
							this.invoiceConceptsRevertList.add(invoiceConcept);
							invoiceConcept.setRecoverValue(BillingTools.CERO);
							this.totalSelectedConcept += BillingTools.formatDouble(invoiceConcept.getBalance().doubleValue());
						}
					}
				}
			}

		}
		return this.invoiceConceptsRevertList;
	}

	private Double allMoneyAvailable;

	public boolean isRevertComplete() {
		if (this.isFixed() == true) {
			if (this.moneyAvailable() == 0.00)
				return false;
			else
				return true;
		} else {
			return false;
		}
	}

	public double moneyAvailable() {
		return BillingTools.formatDouble(this.getAllMoneyAvailable() - this.getRevertedValue());
	}

	public boolean isMoneyAvailableGreaterThanCero() {
		if (this.moneyAvailable() >= BillingTools.formatDouble(0.0))
			return true;
		return false;
	}

	public void setAllMoneyAvailable(Double allMoneyAvailable) {
		this.allMoneyAvailable = allMoneyAvailable;
	}

	public Double getAllMoneyAvailable() {
		this.allMoneyAvailable = new Double(BillingTools.CERO);
		if (this.RecoverTotalValue != null) {
			this.allMoneyAvailable = this.RecoverTotalValue.doubleValue();
		}

		return BillingTools.formatDouble(allMoneyAvailable);
	}

	private double revertedValue;

	public double getRevertedValue() {
		this.revertedValue = BillingTools.formatDouble(BillingTools.CERO);
		if (this.invoiceConceptsRevertList != null) {
			for (InvoiceConcept invcon : this.invoiceConceptsRevertList) {
				this.revertedValue += BillingTools.formatDouble(invcon.getRecoverValue().doubleValue());
			}
		}
		return BillingTools.formatDouble(this.revertedValue);
	}

	public void setRevertedValue(double revertedValue) {
		this.revertedValue = revertedValue;
	}

	public void closeAllModal() {
		closeModal2();
		closeModal1();
	}

	public void closeModal1() {
		resetTotalRecoverValue();
	}

	public void closeModal2() {
		this.revertedValue = BillingTools.formatDouble(BillingTools.CERO);
		this.RecoverTotalValue = BillingTools.formatDouble(BillingTools.CERO);
		this.invoiceConceptsRevertList = null;
		//
		this.invoiceRevertList = null;
		this.searchInvoicesToRevert();
	}

	public void resetTotalRecoverValue() {
		this.RecoverTotalValue = BillingTools.formatDouble(BillingTools.CERO);

	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public boolean validate() {
		return fixed;
	}

	public SiigoFunctions getSiigoFunctions() {
		return siigoFunctions;
	}

	public void setSiigoFunctions(SiigoFunctions siigoFunctions) {
		this.siigoFunctions = siigoFunctions;
	}

	public void invoiceConceptParentValidation(InvoiceConcept invoiceConceptArgument) {

		if (invoiceConceptArgument != null && invoiceConceptArgument.getRecoverValue() != null && this.invoiceConceptsRevertList != null) {
			double cero = BillingTools.formatDouble(0.0);
			for (InvoiceConcept invCon : this.invoiceConceptsRevertList) {
				if (invoiceConceptArgument.getId() == invCon.getId())
					continue;

				if (invoiceConceptArgument.getInvoiceConceptParent() != null && invCon.getId() == invoiceConceptArgument.getInvoiceConceptParent().getId() && invoiceConceptArgument.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST) {
					if (invoiceConceptArgument.getRecoverValue() > cero) {
						invCon.setParented(true);
						invCon.setRecoverValue(cero);
					} else
						invCon.setParented(false);

				} else if (invCon.getInvoiceConceptParent() != null && invCon.getInvoiceConceptParent().getId() == invoiceConceptArgument.getId() && invCon.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST) {
					if (invoiceConceptArgument.getRecoverValue() > cero) {
						invCon.setParented(true);
						invCon.setRecoverValue(cero);
					} else
						invCon.setParented(false);
				} else
					invCon.setParented(false);
			}
		}
	}

	/**
	 * This function calculate the difference in days between to Date objects,
	 * no matter the order when passin arguments
	 * 
	 * @param iniDate
	 * @param endDate
	 * @return
	 */
	public int daysDifference(Date iniDate, Date endDate) {

		long diff = iniDate.getTime() - endDate.getTime();
		return Math.abs((int) (diff / (1000 * 60 * 60 * 24)));

	}

	public String attachment;

	public void createCreditNotesPDF() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		NumberFormat formatter = new DecimalFormat("#,###.00");
		double total = 0.0;
		double balance = 0.0;
		double cant = 0.0;
		String consecutive = "";
		Invoice invoice = null;
		// String name="Carpeta_PDF";

		for (InvoiceConcept invco : this.instance.getInvoiceConcepts()) {
			invoice = invco.getInvoice();
			consecutive = invco.getCreditNote().getConsecutive();

		}
		Session session = (Session) getEntityManager().getDelegate();

		org.hibernate.Query query = session.createQuery("FROM Invoice i WHERE i.id = " + invoice.getId());
		invoice = (Invoice) query.uniqueResult();

		Document document = new Document(PageSize.LETTER);

		if (invoice != null) {

			// -------CREAR CARPETA PARA ALMACENAR
			// ARCHIVOS-------------------
			org.hibernate.Query q = session.createQuery("FROM SystemConfiguration sc WHERE sc.name = '" + SystemConfiguration.Carpeta_PDF + "'");
			q.setMaxResults(1);
			systemConfiguration = (SystemConfiguration) q.uniqueResult();

			String OrigenCarpeta = systemConfiguration.getValue() + "/" + invoice.getProjectProperty().getProject().getId() + "_" + invoice.getProjectProperty().getProject().getNameProject();
			File directorio = new File(OrigenCarpeta);
			directorio.mkdir();
			// --------------------------

			// invoice en la misma hoja de terminos
			try {
				// String filePDF =
				// "tmp/creditNotes/creditNotes" +
				// invoice.getProjectProperty().getCreditNotes().getPrefix()
				// +
				// invoice.getProjectProperty().getCreditNotes().getMin()
				// +
				// invoice.getProjectProperty().getCreditNotes().getSuffix()
				// +
				// ".pdf";
				String filePDF = "tmp/creditNotes/creditNotes" + invoice.getProjectProperty().getProject().getProjectPrefix() + "-C-" + invoice.getProjectProperty().getCreditNotes().getSiigoCode() + "-" + consecutive + ".pdf";
				String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
				String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
				path = path.substring(0, path.lastIndexOf("/")) + "/";
				this.linkPDF = filePDF;
				attachment = path + filePDF;

				// --------------------------
				String filePDF1 = "";
				// filePDF1 =
				// invoice.getProjectProperty().getProject().getProjectPrefix()
				// + "-C-" +
				// invoice.getProjectProperty().getCreditNotes().getPrefix()
				// +
				// invoice.getProjectProperty().getCreditNotes().getMin()
				// +
				// invoice.getProjectProperty().getCreditNotes().getSuffix()
				// +
				// ".pdf";
				filePDF1 = invoice.getProjectProperty().getProject().getProjectPrefix() + "-C-" + invoice.getProjectProperty().getCreditNotes().getSiigoCode() + "-" + consecutive + ".pdf";
				PdfWriter.getInstance(document, new FileOutputStream(OrigenCarpeta + "/" + filePDF1));
				// --------------------------

				PdfWriter.getInstance(document, new FileOutputStream(path + filePDF));

				document.open();

				document.newPage();
				String fileIMAGE = "img/log_Terranum.jpg";
				try {
					if (invoice.getProjectProperty().isMandate() == true) {
						if (invoice.getProjectProperty().getProject().getBusinessEntity().getLogo() != null && invoice.getProjectProperty().getProject().getBusinessEntity().getLogo().length > 0) {
							Image image = Image.getInstance(invoice.getProjectProperty().getProject().getBusinessEntity().getLogo());
							image.setAbsolutePosition(50, 700);
							image.scaleToFit(150, 80);
							document.add(image);
						}
						if (invoice.getBiller().getLogo() != null && invoice.getBiller().getLogo().length > 0) {
							Image image1 = Image.getInstance(invoice.getBiller().getLogo());
							image1.setAbsolutePosition(400, 700);
							image1.scaleToFit(150, 80);
							document.add(image1);
						}
					} else {
						if (invoice.getBiller().getLogo() != null && invoice.getBiller().getLogo().length > 0) {
							Image image = Image.getInstance(invoice.getBiller().getLogo());
							image.setAbsolutePosition(50, 700);
							image.scaleToFit(150, 80);
							document.add(image);
						}
						Image image1 = Image.getInstance(path + fileIMAGE);
						image1.setAbsolutePosition(400, 700);
						image1.scaleToFit(150, 80);
						document.add(image1);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

				// Introduccion y posicionamiento del
				// logo 1 y logo 2
				// if (invoice.getBiller().getLogo() !=
				// null &&
				// invoice.getBiller().getLogo().length
				// > 0) {
				// Image image =
				// Image.getInstance(invoice.getBiller().getLogo());
				// image.setAbsolutePosition(50, 670);
				// // image.scaleAbsolute(80, 80);
				// image.scaleToFit(150, 80);
				// document.add(image);
				// }
				//
				// if (invoice.getBilled().getLogo() !=
				// null &&
				// invoice.getBilled().getLogo().length
				// > 0) {
				// Image image1 =
				// Image.getInstance(invoice.getBilled().getLogo());
				// image1.setAbsolutePosition(480, 670);
				// // image1.scaleAbsolute(80, 80);
				// image1.scaleToFit(150, 80);
				// document.add(image1);
				// }
				String fontFile = path + "Font/TAHOMA.TTF";
				BaseFont bf = BaseFont.createFont(fontFile, BaseFont.IDENTITY_H, true);

				Font font1 = new Font(bf, 7, Font.NORMAL);
				Font font2 = new Font(bf, 8, Font.BOLD);
				Font font3 = new Font(bf, 7, Font.BOLD);
				Font font4 = new Font(bf, 8, Font.NORMAL);

				document.add(new Paragraph("\n\n\n\n\n"));

				PdfPTable table1 = new PdfPTable(2);
				table1.setWidthPercentage(100);
				table1.setHorizontalAlignment(Element.ALIGN_LEFT);

				// PdfPCell cell1 = new PdfPCell(new
				// Paragraph("" +
				// invoice.getNameBiller(), font1));
				PdfPCell cell1;
				if (invoice.getProjectProperty().isMandate() == true) {

					BusinessEntity mandante = invoice.getProjectProperty().getProject().getBusinessEntity();
					cell1 = new PdfPCell(new Paragraph(mandante.getNameBusinessEntity() + "\n" + (mandante.getAddresses() != null && !mandante.getAddresses().isEmpty() ? mandante.getAddresses().iterator().next().toString() : "") + (mandante.getPhoneNumbers() != null && !mandante.getPhoneNumbers().isEmpty() ? " TEL. " + mandante.getPhoneNumbers().iterator().next().toString() : "") + "\n" + (mandante.getCity() != null ? mandante.getCity().getName() : "") + " - " + (mandante.getAddresses() != null && !mandante.getAddresses().isEmpty() ? mandante.getAddresses().iterator().next().getCity().getRegion().getCountry().getName() : "") + "\n" + (mandante.getIdType() == 31 ? ("NIT." + mandante.getIdNumber() + " - " + mandante.getVerificationNumber()) : "C.C." + mandante.getIdNumber()), font3));
				} else {
					cell1 = new PdfPCell(new Paragraph(invoice.getNameBiller() + "\n" + invoice.getAddressBiller() + " TEL. " + invoice.getProjectProperty().getPhoneNumberByPhoneBiller().getNumber() + "\n" + invoice.getCityBiller() + " - " + invoice.getProjectProperty().getBillerAddress().getCity().getRegion().getCountry().getName() + "\n" + (invoice.getBiller().getIdType() == 31 ? ("NIT." + invoice.getBiller().getIdNumber() + " - " + invoice.getBiller().getVerificationNumber()) : "C.C." + invoice.getBiller().getIdNumber()), font3));
				}
				cell1.setBorder(Rectangle.NO_BORDER);
				table1.addCell(cell1);

				// PdfPCell cell2 = new PdfPCell(new
				// Paragraph(""));
				// cell2.setBorder(Rectangle.NO_BORDER);
				// table1.addCell(cell2);
				//
				// PdfPCell cell3 = new PdfPCell(new
				// Paragraph("NIT. " +
				// invoice.getBiller().getIdNumber(),
				// font1));
				// cell3.setBorder(Rectangle.NO_BORDER);
				// table1.addCell(cell3);
				//
				// PdfPCell cell4 = new PdfPCell(new
				// Paragraph("", font1));
				// cell4.setBorder(Rectangle.NO_BORDER);
				// table1.addCell(cell4);
				//
				// PdfPCell cell5 = new PdfPCell(new
				// Paragraph("" +
				// invoice.getAddressBiller(), font1));
				// cell5.setBorder(Rectangle.NO_BORDER);
				// table1.addCell(cell5);
				//
				// PdfPCell cell6 = new PdfPCell(new
				// Paragraph("", font1));
				// cell6.setBorder(Rectangle.NO_BORDER);
				// table1.addCell(cell6);
				//
				// PdfPCell cell7 = new PdfPCell(new
				// Paragraph("" +
				// invoice.getCityBiller(), font1));
				// cell7.setBorder(Rectangle.NO_BORDER);
				// table1.addCell(cell7);

				PdfPCell cell8 = new PdfPCell(new Paragraph("NOTA CREDITO No C " + invoice.getProjectProperty().getCreditNotes().getSiigoCode() + " - " + consecutive, font2));
				cell8.setBorder(Rectangle.NO_BORDER);
				table1.addCell(cell8);

				document.add(table1);
				document.add(new Paragraph("\n"));

				// /////////////////////////////////////////////////////////////////////////////////////////////////////

				PdfPTable table6 = new PdfPTable(3);
				table6.setWidthPercentage(100);
				table6.setHorizontalAlignment(Element.ALIGN_LEFT);

				PdfPCell cell9 = new PdfPCell(new Paragraph("Señores: " + invoice.getNameBilled(), font1));
				cell9.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell9);

				PdfPCell cell10 = new PdfPCell(new Paragraph("Nit:  " + invoice.getIdNumberBilled(), font1));
				cell10.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell10);

				PdfPCell cell11 = new PdfPCell(new Paragraph("Fecha: " + dateFormat.format(this.instance.getCreditNoteDate()), font1));
				cell11.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell11);

				PdfPCell cell12 = new PdfPCell(new Paragraph("Direccion: " + invoice.getAddressBilled(), font1));
				cell12.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell12);

				PdfPCell cell13 = new PdfPCell(new Paragraph("Telefonos:  " + invoice.getPhoneBilled(), font1));
				cell13.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell13);

				PdfPCell cell14 = new PdfPCell(new Paragraph("", font1));
				cell14.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell14);

				PdfPCell cell15 = new PdfPCell(new Paragraph("Ciudad: " + invoice.getCityBilled(), font1));
				cell15.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell15);

				PdfPCell cell16 = new PdfPCell(new Paragraph("", font1));
				cell16.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell16);

				// PdfPCell cellCreditNoteReason = new PdfPCell(new
				// Paragraph("Razon de la nota crédito: " +
				// this.creditNoteReason, font4));
				// cellCreditNoteReason.setBorder(Rectangle.NO_BORDER);
				// table6.addCell(cellCreditNoteReason);

				document.add(table6);
				document.add(new Paragraph("\n"));
				document.add(new Paragraph("A continuación se describen los descuentos aplicados por motivo de: ", font3));
				document.add(new Paragraph("\n"));
				document.add(new Paragraph(this.creditNoteReason, font4));
				document.add(new Paragraph("\n"));

				// ///////////////////TITULOS DE LA
				// TABLA//////////////////////////////////////////////

				float[] colsWidth2 = { 1f, 2f, 1f };
				PdfPTable table2 = new PdfPTable(colsWidth2);
				table2.setWidthPercentage(100);
				table2.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell cell18 = new PdfPCell(new Paragraph("DOCUMENTO ", font1));
				cell18.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
				cell18.setGrayFill(0.6f);
				table2.addCell(cell18);

				PdfPCell cell19 = new PdfPCell(new Paragraph("CONCEPTO ", font1));
				cell19.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
				cell19.setGrayFill(0.6f);
				table2.addCell(cell19);

				PdfPCell cell20 = new PdfPCell(new Paragraph("VALOR ", font1));
				cell20.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
				cell20.setGrayFill(0.6f);
				table2.addCell(cell20);

				document.add(table2);

				for (InvoiceConcept lista : this.instance.getInvoiceConcepts()) {
					ivaTotal += lista.getCalculatedTax();
					float[] colsWidth = { 1f, 2f, 1f };
					PdfPTable table = new PdfPTable(colsWidth);
					table.setWidthPercentage(100);
					table.setHorizontalAlignment(Element.ALIGN_CENTER);

					// [FACTURA O CUENTA DE COBRO]
					if (invoice.getNumber() == null) {
						Phrase phrase1 = new Phrase(20, "[# Factura o Cuenta de Cobro]", font4);
						PdfPCell cell21 = new PdfPCell(new Paragraph(phrase1));
						cell21.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
						table.addCell(cell21);
					} else {
						// Phrase phrase1 =
						// new Phrase(20, ""
						// +
						// lista.getInvoice().getNumber(),
						// font4);
						Phrase phrase1 = new Phrase(20, "" + new ProjectHome().invoiceNumber(lista.getInvoice()), font4);
						PdfPCell cell21 = new PdfPCell(new Paragraph(phrase1));
						cell21.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
						table.addCell(cell21);
					}

					// [CONCEPTOS DE
					// FACTURACION]-----------Falta
					// Excedente---

					// if
					// (lista.getInvoiceConceptParent().getInvoiceConceptType()
					// == InvoiceConcept.TYPE_INTEREST) {
					ConceptHome home = new ConceptHome();

					String ivaPorcent = "";
					if (lista.getInvoiceConceptParent().getInvoiceConceptType() != InvoiceConcept.TYPE_INTEREST) {
						ivaPorcent = porcentajeIva(lista.getInvoiceConceptParent().getConcept().getTax());
					} else {
						ivaPorcent = porcentajeIva(lista.getInvoiceConceptParent().getConcept().getAccountingCDOD_cuentasIVA());
					}
					Phrase phrase2 = new Phrase(20, "" + home.nameInvoiceConcept(lista.getInvoiceConceptParent()) + ivaPorcent, font4);
					PdfPCell cell22 = new PdfPCell(new Paragraph(phrase2));
					cell22.setBorder(Rectangle.RIGHT);
					table.addCell(cell22);
					// } else {
					// Phrase phrase2 = new Phrase(20, "" +
					// lista.getConcept().getName(), font4);
					// PdfPCell cell22 = new PdfPCell(new Paragraph(phrase2));
					// cell22.setBorder(Rectangle.RIGHT);
					// table.addCell(cell22);
					// }

					// [VALOR RECAUDO ]
					Phrase phrase3 = new Phrase(20, "$" + formatter.format((lista.getRecoverValue()) - lista.getCalculatedTax()), font4);

					PdfPCell cell23 = new PdfPCell(new Paragraph(phrase3));
					cell23.setBorder(Rectangle.RIGHT);
					cell23.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
					table.addCell(cell23);

					document.add(table);

					total = total + lista.getRecoverValue();

					balance = balance + Double.parseDouble(totalConcepto(lista.getInvoiceConceptParent()));
					cant = cant + lista.getTotal();

				}

				// --------Excedente--------------------------------------------------

				float[] colsWidth3 = { 1f, 2f, 1f };
				PdfPTable table7 = new PdfPTable(colsWidth3);
				table7.setWidthPercentage(100);
				table7.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell cell22 = new PdfPCell(new Paragraph(""));
				cell22.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
				table7.addCell(cell22);

				PdfPCell newParag = new PdfPCell(new Paragraph("\n", font4));
				newParag.setBorder(Rectangle.RIGHT);
				table7.addCell(newParag);
				newParag.setBorder(Rectangle.RIGHT);
				table7.addCell(newParag);
				newParag.setBorder(Rectangle.LEFT);
				table7.addCell(newParag);

				document.add(table7);

				// --------------------------------------------------
				// [TOTAL]
				Phrase phrase4 = new Phrase(20, "TOTAL NOTA CREDITO ", font4);
				Phrase phrase5 = new Phrase(20, "$" + formatter.format(total), font4);
				float[] colsWidth1 = { 2f, 1f, 1f };
				PdfPTable table3 = new PdfPTable(colsWidth1);
				table3.setWidthPercentage(100);
				table3.setHorizontalAlignment(Element.ALIGN_CENTER);
				PdfPCell celdaSinMarco = new PdfPCell();
				celdaSinMarco.setBorder(Rectangle.TOP + Rectangle.LEFT);
				table3.addCell(celdaSinMarco);
				table3.addCell(phrase4);
				PdfPCell cellIva = new PdfPCell(phrase5);
				// cellIva.setBorder(Rectangle.TOP + Rectangle.RIGHT);
				cellIva.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
				table3.addCell(cellIva);
				// --------------------------------------------------
				// [IVA]
				Phrase phraseIva = new Phrase(20, "IVA  ", font4);
				Phrase phraseIvaValue = new Phrase(20, "$" + formatter.format(ivaTotal), font4);
				celdaSinMarco.setBorder(Rectangle.LEFT);
				table3.addCell(celdaSinMarco);
				table3.addCell(phraseIva);
				PdfPCell cell25 = new PdfPCell(phraseIvaValue);
				// cell25.setBorder(Rectangle.TOP + Rectangle.RIGHT);
				cell25.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
				table3.addCell(cell25);

				// [SALDO]

				Phrase phraseSaldo = new Phrase(20, "SALDO  CONCEPTOS", font4);
				Phrase phraseSaldoValue = new Phrase(20, "$" + formatter.format(balance), font4);
				celdaSinMarco.setBorder(Rectangle.LEFT);
				table3.addCell(celdaSinMarco);
				table3.addCell(phraseSaldo);
				cell25 = new PdfPCell(phraseSaldoValue);
				// cell25.setBorder(Rectangle.TOP + Rectangle.RIGHT);
				cell25.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
				table3.addCell(cell25);
				document.add(table3);

				// /////////////////////////TERCERA
				// PARTE DE LA
				// TABLA///////////////////////////

				Phrase phrase21 = new Phrase(20, "SON: " + convertNumberToString.convertToString(total), font4);

				PdfPTable table4 = new PdfPTable(1);
				table4.setWidthPercentage(100);
				table4.setHorizontalAlignment(Element.ALIGN_CENTER);
				table4.addCell(phrase21);
				document.add(table4);

				// //////////////////////////CUARTA
				// PARTE DE LA
				// TABLA////////////////////////////

				Phrase phrase23 = new Phrase(20, "\n  Firma de Recibido: ", font2);

				PdfPTable table5 = new PdfPTable(2);
				table5.setWidthPercentage(100);
				table5.setHorizontalAlignment(Element.ALIGN_TOP);

				table5.addCell("");
				table5.addCell(phrase23);

				document.add(table5);

				document.close();

			} catch (Exception e) {
				e.printStackTrace();
				document.close();
			}

		}
	}

	private String linkPDF;

	public String getLinkPDF() {
		return this.linkPDF;
	}

	public void setLinkPDF(String linkPDF) {
		this.linkPDF = linkPDF;
	}

	private String linkSiigo;

	public String getlinkSiigo() {
		return this.linkSiigo;
	}

	public void setlinkSiigo(String linkSiigo) {
		this.linkSiigo = linkSiigo;
	}

	private String ReasonCreditNote;

	public String getReasonCreditNote() {
		return ReasonCreditNote;
	}

	public void setReasonCreditNote(String reasonCreditNote) {
		ReasonCreditNote = reasonCreditNote;
	}

	public CreditNote getInstanceMaker(MakerChecker makerChecker) {
		Object aux = new MakerCheckerHome().getInstance(makerChecker);
		if (aux instanceof CreditNote) {
			CreditNote creditNote = (CreditNote) aux;
			return creditNote;
		}
		return null;
	}

	int makerCheckerId;
	int creditNotesId;

	public void updateInstanceMaker(int makerCheckerId) {
		setInstance((CreditNote) new MakerCheckerHome().getInstance(makerCheckerId));
		this.makerCheckerId = makerCheckerId;
		// setInstance(getEntityManager().merge(getInstance()));
	}

	public List<InvoiceConcept> getInvoiceConcepts() {
		return getInstance() == null ? null : new ArrayList<InvoiceConcept>(getInstance().getInvoiceConcepts());
	}

	@SuppressWarnings("deprecation")
	public void cancelChange() {
		HashMap<Integer, InvoiceConcept> updateStatusInvoiceConceptList = new HashMap<Integer, InvoiceConcept>();
		for (InvoiceConcept invCon : this.instance.getInvoiceConcepts()) {
			InvoiceConcept parent = invCon.getInvoiceConceptParent();
			updateStatusInvoiceConceptList.put(parent.getId(), parent);

		}

		new MakerCheckerHome().deleteMaker(makerCheckerId);
		if (!updateStatusInvoiceConceptList.isEmpty()) {
			BillingTools billingTools = new BillingTools(this.getEntityManager());
			for (InvoiceConcept parent : updateStatusInvoiceConceptList.values()) {
				// this.getEntityManager().refresh(parent);
				parent.setCreditNoteStatus(InvoiceConcept.CREDIT_NOTE_STATUS_NONE);
				billingTools.mergeObject(parent);
			}
		}
		getFacesMessages().addFromResourceBundle(Severity.INFO, "NOTAS CREDITO", "Se cancela la creación de notas credito");
	}

	@SuppressWarnings("deprecation")
	@Transactional(TransactionPropagationType.REQUIRED)
	public void approveChange() {
		try {
			BillingTools billingTools = new BillingTools(this.getEntityManager());
			Set<InvoiceConcept> invConcepts = this.instance.getInvoiceConcepts();
			ProjectProperty projectProperty = (invConcepts != null && !invConcepts.isEmpty() ? invConcepts.iterator().next().getInvoice().getProjectProperty() : new ProjectProperty());

			Session session = (Session) getEntityManager().getDelegate();
			ConsecutiveCreditNotes ccn = (ConsecutiveCreditNotes) session.get(ConsecutiveCreditNotes.class, projectProperty.getCreditNotes().getId());
			this.instance.setConsecutive(ccn.getMin() + "");
			ccn.setMin(ccn.getMin() + 1);
			billingTools.persistObject(ccn);

			billingTools.persistObject(this.getInstance());

			for (InvoiceConcept invConceptCreditNote : this.instance.getInvoiceConcepts()) {

				invConceptCreditNote.setCreditNote(this.instance);
				billingTools.persistObject(invConceptCreditNote);
				InvoiceConcept parent = invConceptCreditNote.getInvoiceConceptParent();
				parent.setCreditNoteStatus(InvoiceConcept.CREDIT_NOTE_STATUS_NONE);
				session.update(parent);
				log(Level.INFO,"Proceso de recalculación de intereses...");
				billingTools.procesarRecalculacionIntereses(parent, false, this.instance.getCreditNoteDate());
				parent.setBalance(BillingTools.formatDouble(parent.getBalance().doubleValue() - invConceptCreditNote.getBalance().doubleValue()));
				billingTools.procesarRecalculacionIntereses(parent, true, this.instance.getCreditNoteDate());
				invConceptCreditNote.setNewBalance(totalConceptoDouble(parent));
				billingTools.persistObject(parent);
				billingTools.persistObject(invConceptCreditNote);

			}

			this.creditNotesId = this.instance.getId();
			this.creditNoteReason = this.instance.getReason();
			log.log.info("el usuario: " + this.user.getNombre() + " a Aprobado correctamente la nota crédito: " + this.instance.getId());
			createCreditNotesPDF();
			SiigoFunctions siigoFunctions = new SiigoFunctions(session);
			Hibernate.initialize(getInstance());
			setlinkSiigo(siigoFunctions.creditNoteSiggo(getInstance(), projectProperty.getProject()));

			// Envio Correo Nota Credito
			String to = this.instance.getUserEmail();
			if (to != null && !to.isEmpty()) {
				MailSender mailSender = new MailSender();
				String message = "Se acaba de generar una nota crédito\n.";
				String subject = "NOTA CREDITO";
				mailSender.set(to, subject, message, attachment, "creditNote" + (new Date()).getTime() + ".pdf");
				(new Thread(mailSender)).start();// El
				// envio
				// de
				// correo
				// se
				// hace
				// en
				// un
				// nuevo
				// hilo,
				// de
				// lo
				// contrario
				// demora
				// la
				// respuesta
				// del
				// ajax
			} else {
				log(Level.INFO,"El destinatorio de la Nota Credito es NULL o vacio... NO se envia correo de notificación");
			}

			this.invoiceRevertList = null;
			closeAllModal();
			this.searchInvoicesToRevert();

			new MakerCheckerHome().deleteMaker(this.makerCheckerId);
			getFacesMessages().addFromResourceBundle(Severity.INFO, "NOTA CREDITO", "La aprobación de la Nota crédito fue exitosa");
		} catch (Exception e) {
			log(Level.SEVERE,"ERROR al aprobar la nota Credito");
			getFacesMessages().addFromResourceBundle(Severity.ERROR, "NOTA CREDITO", "La aprobación de la Nota crédito no se pudo realizar");
			e.printStackTrace();
		}
	}

	public String getButtonActionName() {
		if (this.fixed)
			return "Generar Descuentos";
		else
			return "Generar Notas Crédito";
	}

	public boolean renderNumberSpinner(InvoiceConcept invoiceConcept) {
		if (invoiceConcept.getCreditNoteStatus() == InvoiceConcept.CREDIT_NOTE_STATUS_APPROVAL_PENDING) {
			return false;
		}
		return true;
	}

	/**
	 * Esta funcion toma el balance de un invoiceConcept y retorna el valor
	 * bruto mas iva
	 * 
	 * @param invocon
	 * @return
	 */
	public String totalConcepto(InvoiceConcept invocon) {

		String baceMasIva = Format_number.FormatToString2(totalConceptoDouble(invocon));
		return baceMasIva;

	}

	public double totalConceptoDouble(InvoiceConcept invocon) {
		// se adquiere el valor de las retenciones
		if (invocon != null) {
			if (invocon.getConcept() != null) {
				Concept con = invocon.getConcept();
				Double iva = retencion(con, RetentionRate.RETENTION_RATE_IVA, getEntityManager());
				Double stamptx = retencion(con, RetentionRate.RETENTION_RATE_TIMBRE, getEntityManager());
				Double cree    = retencion(con,RetentionRate.RETENTION_RATE_RTECREE,getEntityManager());
				Double balance = invocon.getBalance();
				Double number;
				double ivamenosint;
				if (invocon.getInvoiceConceptType() != InvoiceConcept.TYPE_INTEREST) {
					Double reteIva = retencion(con, RetentionRate.RETENTION_RATE_RTEIVA, getEntityManager());
					Double reteIca = retencion(con, RetentionRate.RETENTION_RATE_RTEICA, getEntityManager());
					Double reteCree= retencion(con, RetentionRate.RETENTION_RATE_RTECREE,getEntityManager());
					Double reteFuente = retencion(con, RetentionRate.RETENTION_RATE_RTEFUENTE, getEntityManager());
					ivamenosint = (1 + iva + stamptx+ cree - (reteFuente + reteIca+reteCree+ (reteIva * iva)));

					number = balance / ivamenosint;
				} else {
					Double reteIvaInt = retencion(con, RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA, getEntityManager());
					Double reteIcaInt = retencion(con, RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA, getEntityManager());
					Double reteFuenteInt = retencion(con, RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE, getEntityManager());
					Double reteCreeInt = retencion(con, RetentionRate.RETENTION_RATE_SUBCATEGORY_RETECREE, getEntityManager());
					ivamenosint = 1 + iva + stamptx + cree - (((reteFuenteInt + reteIcaInt + reteCreeInt +(reteIvaInt * iva)) < 0) ? ((reteFuenteInt + reteIcaInt + reteCreeInt + (reteIvaInt * iva)) * (-1)) : (reteFuenteInt + reteIcaInt + reteCreeInt + (reteIvaInt * iva)));
					number = balance / ivamenosint;
				}

				return number * (1 + iva);
			}
		}
		return 0.0;
	}

	public static Double retencion(Concept con, int id, EntityManager entityManager) {
		Query q = entityManager.createQuery("FROM ConceptRetentionRateAccount crra WHERE crra.concept.id=? and crra.retentionRateAccount.retentionRate.id=?");
		q.setParameter(1, con.getId());
		q.setParameter(2, id);
		@SuppressWarnings("unchecked")
		List<ConceptRetentionRateAccount> rra = q.getResultList();
		// System.out.println();
		// if (!rra.isEmpty())
		// System.out.println("valor: " +
		// rra.get(0).getRetentionRateAccount().getValue() + " nombre: " +
		// rra.get(0).getRetentionRateAccount().getName() + " cuenta: " +
		// rra.get(0).getRetentionRateAccount().getAccount());
		// System.out.println();
		if (rra.isEmpty())
			return 0.0;
		return rra.get(0).getRetentionRateAccount().getValue() / 100;
	}

	public List<MakerChecker> creditNoteMakerChekerList() {

		if (this.creditNoteListMakerChecker == null) {
			Query q = this.getEntityManager().createQuery(getHQL());
			@SuppressWarnings("unchecked")
			List<MakerChecker> list = (List<MakerChecker>) q.getResultList();
			this.creditNoteListMakerChecker = new ArrayList<MakerChecker>();
			if (list != null && !list.isEmpty()) {
				for (MakerChecker makerChecker : list) {
					Object obj = new MakerCheckerHome().getInstance(makerChecker);
					log(Level.INFO,"++++++++++++Object Maker-Checker: " + (obj) + "+++++++++++++++++++++++++++++");
					if (obj != null) {

						this.creditNoteListMakerChecker.add(makerChecker);

					} else {
						log(Level.INFO,"***********Objeto NULL maker-checker********************");
					}
				}

			}
		}
		return this.creditNoteListMakerChecker;
	}

	private String getHQL() {
		String filter = "select makerChecker from MakerChecker makerChecker";
		if (projectFilter != null && projectsFilter != null) {
			if (projectFilter.equals("-1")) {
				String next = ",MakerCheckerXProject mcp WHERE makerChecker.className like ('" + CreditNote.class.getCanonicalName() + "') and ((mcp.makerChecker = makerChecker AND  mcp.project.id IN  (";
				boolean in = false;
				for (SelectItem item : projectsFilter) {
					if (!item.getValue().equals("-1")) {
						in = true;
						filter += next;
						next = null;
						next = ",";
						filter += item.getValue();
					}
				}
				if (in) {
					filter += ")";
				}
				filter += (") OR (SELECT COUNT(p) FROM MakerCheckerXProject p WHERE p.makerChecker = makerChecker) = 0)");
			} else {
				filter += ",MakerCheckerXProject mcp WHERE makerChecker.className like ('" + CreditNote.class.getCanonicalName() + "') and ((mcp.project.id IN (" + projectFilter + ") AND mcp.makerChecker = makerChecker) OR (SELECT COUNT(p) FROM MakerCheckerXProject p WHERE p.makerChecker = makerChecker) = 0)";
			}
		}
		return filter;
	}

	public String billedFromInvoice(MakerChecker mk) {

		String answer = "";
		CreditNote creditNote = this.getInstanceMaker(mk);
		for (InvoiceConcept invoiceConcept : creditNote.getInvoiceConcepts()) {
			answer = invoiceConcept.getInvoice().getBilled().getNameBusinessEntity();
			if (!answer.isEmpty())
				break;
		}
		return answer;
	}

	public Date getCreditNoteDate() {
		return creditNoteDate;
	}

	public void setCreditNoteDate(Date creditNoteDate) {
		this.creditNoteDate = creditNoteDate;
	}

	public void validateCreditNoteDate() {
		Calendar dateAux = Calendar.getInstance();
		dateAux.setTime(this.getCreditNoteDate());
		int periodMonth = projectClosureMonth();
		if (periodMonth != -1) {
			if (dateAux.get(Calendar.MONTH) == periodMonth) {
				setDateValidateMessage(null);
			} else {
				setDateValidateMessage("La fecha de la nota crédito no pertenece al periodo actual de facturación");
			}
		} else {
			setDateValidateMessage("Error en el periodo de facturación");
		}
	}

	private int projectClosureMonth() {
		if (!invoiceRevertList.isEmpty()) {
			Calendar today = Calendar.getInstance();
			Query q = getEntityManager().createNativeQuery("SELECT closure_date FROM project_closure WHERE project = ? order by closure_date DESC limit 1");
			q.setParameter(1, this.invoiceRevertList.get(0).getProjectProperty().getProject().getId());
			if (!q.getResultList().isEmpty()) {

				return today.get(Calendar.MONTH);

			} else {
				today.add(Calendar.MONTH, -1);
				return today.get(Calendar.MONTH);
			}
		} else
			return -1;

	}

	public String getDateValidateMessage() {
		return dateValidateMessage;
	}

	public void setDateValidateMessage(String dateValidateMessage) {

		this.dateValidateMessage = dateValidateMessage;
	}

	public String porcentajeIva(RetentionRateAccount iva) {
		String answer = "";
		if (iva != null && iva.getValue() != null && iva.getValue() != 0) {
			answer = " (IVA " + iva.getValue() + "%)";
		}
		return answer;
	}

	public Double removeIva(int tax, int cost) {
		// cost = invcon.getRecoverValue() / (1 +
		// (invcon.getConcept().getTax().getValue() / 100));
		// tax = (invcon.getConcept().getTax().getValue() / 100) * cost;

		return (double) 0;
	}

}
