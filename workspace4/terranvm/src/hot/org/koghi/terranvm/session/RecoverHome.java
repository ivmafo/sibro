package org.koghi.terranvm.session;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.async.Log;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.bean.SiigoFunctions;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.ConsecutiveCollectionAccount;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.PayFormType;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.Recover;
import org.koghi.terranvm.entity.RecoverClosure;
import org.koghi.terranvm.entity.RecoverConcept;
import org.koghi.terranvm.entity.SystemConfiguration;
import org.koghi.terranvm.entity.User_Terranvm;
import org.richfaces.component.html.HtmlExtendedDataTable;

import com.itextpdf.text.BaseColor;
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

@Name("recoverHome")
public class RecoverHome extends EntityHome<Recover> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * MANUAL RECOVER ATRIBUTES
	 */
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable htmlRecoverDataTable;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable htmlInvoiceConceptRecoverDataTable;
	private String recoverListTableState;
	private String recoverInvoiceConceptListTableState;
	private List<Invoice> invoiceRecoverList;
	private List<InvoiceConcept> invoiceConceptsRecoverList;
	private String searchName;
	private String searchId;
	private boolean selectAllInvoices;
	private Double recoverTotalValue;
	private boolean recoverWithPriority;
	private double totalSelectedConcept;
	private boolean selectedAnyInvoice;
	private double recoveredValue;
	private Recover lastRecover;
	private Double allMoneyAvailable;
	private Date recoverDate;
	private String linkSIIGO;
	public SystemConfiguration systemConfiguration = new SystemConfiguration();
	private String lineSum;
	@SuppressWarnings("unused")
	private Log log = new Log(this);
	@In(required = false)
	private User_Terranvm user;
	
	private PayFormType payFormType;
	public ConvertNumberToString convertNumberToString = new ConvertNumberToString();

	// Mensaje que valida la fecha del recaudo
	private String dateValidateMessage;

	// Mensaje que valida la fecha del recaudo

	// id del projecto a filtrar
	@In(required = false)
	public String projectFilter;

	private String attachment;
 
	/*
	 * END MANUAL RECOVER ATRIBUTES
	 */
	public void log(Object o) {
		toLog(o == null ? "NULL" : o);
	}

	public void log() {
		toLog("");
	}

	public void toLog(Object o) {
		String prefix = " % % % % % % % " + o.toString();
		Logger.getLogger(this.getClass().toString()).log(Level.INFO, prefix);
	}

	public HtmlExtendedDataTable getHtmlRecoverDataTable() {
		return htmlRecoverDataTable;
	}

	public void setHtmlRecoverDataTable(HtmlExtendedDataTable htmlRecoverDataTable) {
		this.htmlRecoverDataTable = htmlRecoverDataTable;
	}

	public String getRecoverListTableState() {
		return recoverListTableState;
	}

	public void setRecoverListTableState(String recoverListTableState) {
		this.recoverListTableState = recoverListTableState;
	}

	public List<Invoice> getInvoiceRecoverList() {
		return invoiceRecoverList;
	}

	public void setInvoiceRecoverList(List<Invoice> invoiceRecoverList) {
		this.invoiceRecoverList = invoiceRecoverList;
	}

	public boolean isRecoverInvoiceListSelectionAcceptable() {
		this.selectedAnyInvoice = false; 
		boolean acceptable = true;
		if (this.invoiceRecoverList != null) {
			for (Invoice inv1 : this.invoiceRecoverList) {
				if (inv1.isSelected()) {
					selectedAnyInvoice = true;
					int clientId = inv1.getBilled().getId();
					for (Invoice inv2 : this.invoiceRecoverList) {
						if (inv2.isSelected() && clientId != inv2.getBilled().getId()) {
							acceptable = false;
						}

					}
				}
			}
		}
		return acceptable;
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

	@SuppressWarnings("unchecked")
	public void searchInvoiceRecoverList() {
		this.invoiceRecoverList = null;
		String sentence = "SELECT DISTINCT(inv) FROM  Invoice inv, InvoiceConcept invCon WHERE inv.approved = ? AND inv.invoiceStatus.id = ? AND invCon.invoiceConceptType != ? AND inv.id = invCon.invoice AND invCon.balance > 0 AND lower(inv.nameBilled) like lower(:name) AND inv.idNumberBilled like :nit ORDER BY inv.expeditionDate";

		// Se adiciona el filtro del proyecto del mundo si este es diferente de
		// -1 (-1 es la opción de "Filtrar Todos")
		Integer projectId = Integer.valueOf(projectFilter);
		if (projectId != null && projectId != -1) {
			sentence = "SELECT DISTINCT(inv) FROM  Invoice inv, InvoiceConcept invCon " + "WHERE inv.approved = ? AND inv.invoiceStatus.id = ? " + "AND invCon.invoiceConceptType != ? AND inv.id = invCon.invoice " + "AND invCon.balance > 0 AND invCon.invoice.projectProperty.project = " + projectId + "AND lower(inv.nameBilled) like lower(:name) AND inv.idNumberBilled like :nit ORDER BY inv.expeditionDate";
		}

		Query query = this.getEntityManager().createQuery(sentence);
		query.setParameter("name", this.searchName == null ? "%%" : "%" + this.searchName + "%");
		query.setParameter("nit", this.searchId == null ? "%%" : "%" + this.searchId + "%");
		query.setParameter(1, true);
		query.setParameter(2, InvoiceStatus.STATUS_VIGENTE);
		query.setParameter(3, InvoiceConcept.TYPE_CREDIT_NOTE);
		this.invoiceRecoverList = query.getResultList();
		if (this.invoiceRecoverList.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Busqueda", "La busqueda no arrojó resultados"));

		} else {

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Busqueda", "Se encontraron " + this.invoiceRecoverList.size() + " resultados"));
		}

	}

	public boolean searchFormEmpty() {
		if ((this.searchName == null || this.searchName.isEmpty()) && (this.searchId == null || this.searchId.isEmpty()))
			return true;

		return false;
	}

	public void cleanSearchForm() {
		this.searchName = null;
		this.searchId = null;
	}

	public void checkAllInvoices() {
		for (Invoice invoice : this.invoiceRecoverList) {
			invoice.setSelected(this.selectAllInvoices);
		}
		this.isRecoverInvoiceListSelectionAcceptable();
	}

	public boolean isSelectAllInvoices() {
		return selectAllInvoices;
	}

	public void setSelectAllInvoices(boolean selectAllInvoices) {
		this.selectAllInvoices = selectAllInvoices;
	}

	public Double getRecoverTotalValue() {
		if (recoverTotalValue == null)
			return 0.00;
		return recoverTotalValue;
	}

	public void setRecoverTotalValue(Double recoverTotalValue) {
		if (recoverTotalValue != null)
			this.recoverTotalValue = recoverTotalValue;
		else
			this.recoverTotalValue = 0.00;
	}

	public boolean acceptableTotalRecoverValue() {
		if (this.recoverTotalValue != null && this.recoverTotalValue > 0.00 && this.dateValidateMessage == null) {
			return false;
		} else if (this.surplusAvailable() == true && this.recoverTotalValue != null && this.recoverTotalValue == 0.00 && this.dateValidateMessage == null)
			return false;

		return true;
	}

	public void resetTotalRecoverValue() {
		this.recoverTotalValue = 0.00;
		this.recoverDate = null;
		this.payFormType = null;
		
	}

	public List<InvoiceConcept> getInvoiceConceptsRecoverList() {
		BusinessEntity client = null;
		if (this.invoiceConceptsRecoverList == null && this.invoiceRecoverList != null) {
			this.totalSelectedConcept = 0.00;
			this.invoiceConceptsRecoverList = new ArrayList<InvoiceConcept>();
			for (Invoice invoice : this.invoiceRecoverList) {
				if (invoice.isSelected()) {
					client = invoice.getBilled();
					List<InvoiceConcept> tempInvoiceConcepts = invoice.getInvoiceConcepts();
					for (InvoiceConcept invoiceConcept : tempInvoiceConcepts) {
						if (invoiceConcept.getBalance().doubleValue() > 0.00 && invoiceConcept.getInvoiceConceptType() != InvoiceConcept.TYPE_CREDIT_NOTE) {
							this.invoiceConceptsRecoverList.add(invoiceConcept);
							invoiceConcept.setRecoverValue(BillingTools.formatDouble(0.0));
							this.totalSelectedConcept += BillingTools.formatDouble(invoiceConcept.getBalance().doubleValue());
						}
					}
				}
			}
			if (client != null)
				this.searchSurplusValueForClient(client);
		}
		return this.invoiceConceptsRecoverList;
	}

	@SuppressWarnings("unchecked")
	public void searchSurplusValueForClient(BusinessEntity client) {
	    Project project = null; 
	    if(!this.projectFilter.equals("-1")){   
	        project = getEntityManager().find(Project.class, Integer.parseInt(projectFilter));
	    }else{
	    project = this.invoiceConceptsRecoverList.get(0).getInvoice().getProjectProperty().getProject();      
	    } 
		String sqlSentence = "SELECT DISTINCT(recover) FROM Recover recover, InvoiceConcept invcon, RecoverConcept recon WHERE recon.recover = recover.id AND recon.invoiceConcept = invcon.id  AND invcon.invoice.billed.id = ? And invcon.invoice.projectProperty.project = ? ORDER BY recover.id DESC";
		Query query = this.getEntityManager().createQuery(sqlSentence);
		query.setParameter(1, client.getId());
		query.setParameter(2, project);
		query.setMaxResults(1);
		List<Recover> tempList = query.getResultList();
		if (!tempList.isEmpty()) {
			this.lastRecover = tempList.get(0);
		} else {
			this.lastRecover = null;
		}
	}

	public void setInvoiceConceptsRecoverList(List<InvoiceConcept> invoiceConcepts) {
		this.invoiceConceptsRecoverList = invoiceConcepts;
	}

	public void resetInvoiceConceptList() {
		this.invoiceConceptsRecoverList = null;
		this.dateValidateMessage = null;
		this.getInvoiceConceptsRecoverList();
		validateRecoverDate();
	}

	public HtmlExtendedDataTable getHtmlInvoiceConceptRecoverDataTable() {
		return htmlInvoiceConceptRecoverDataTable;
	}

	public void setHtmlInvoiceConceptRecoverDataTable(HtmlExtendedDataTable htmlInvoiceConceptRecoverDataTable) {
		this.htmlInvoiceConceptRecoverDataTable = htmlInvoiceConceptRecoverDataTable;
	}

	public String getRecoverInvoiceConceptListTableState() {
		return recoverInvoiceConceptListTableState;
	}

	public void setRecoverInvoiceConceptListTableState(String recoverInvoiceConceptListTableState) {
		this.recoverInvoiceConceptListTableState = recoverInvoiceConceptListTableState;
	}

	public boolean isRecoverWithPriority() {
		return recoverWithPriority;
	}

	public void setRecoverWithPriority(boolean recoverWithPriority) {
		this.recoverWithPriority = recoverWithPriority;
	}

	public void recoverWithPriority() {
		this.recoverWithPriority = !this.recoverWithPriority;
		double part = recoverTotalValue;
		if (this.surplusAvailable()) {
			part += lastRecover.getSurplusValue();
		}

		Object[] sortList = this.invoiceConceptsRecoverList.toArray();
		Arrays.sort(sortList);
		for (Object object : sortList) {
			InvoiceConcept invcon = (InvoiceConcept) object;
			if (part > 0.00) {
				part -= BillingTools.formatDouble(invcon.getBalance());
				if (part > 0.00) {
					invcon.setRecoverValue(invcon.getBalance());
				} else {
					invcon.setRecoverValue(invcon.getBalance() + part);
					part = -1;
				}
			} else {
				invcon.setRecoverValue(0.00);
			}
		}
	}

	public void recoverWithPriorityAutomatic() {
		this.recoverWithPriority = !this.recoverWithPriority;
		double part = recoverTotalValue;
		if (this.surplusAvailable()) {
			part += lastRecover.getSurplusValue();
		}

		Object[] sortList = this.invoiceConceptsRecoverList.toArray();
		Arrays.sort(sortList);
		for (Object object : sortList) {
			InvoiceConcept invcon = (InvoiceConcept) object;
			if (this.isInvoiceConceptInterestExcluded(invcon)) {
				log("No se hace recaudo al InvoiceConcept " + invcon.getId() + ", xq en el proyecto los intereses no son obligatorios");
				continue;
			}
			if (part > 0.00) {
				part -= BillingTools.formatDouble(invcon.getBalance());
				if (part > 0.00) {
					invcon.setRecoverValue(invcon.getBalance());
				} else {
					invcon.setRecoverValue(invcon.getBalance() + part);
					part = -1;
				}
			} else {
				invcon.setRecoverValue(0.00);
			}
		}
	}

	public void recoverWithoutPriority() {
		this.recoveredValue = 0.00;
		this.recoverWithPriority = !this.recoverWithPriority;
		for (InvoiceConcept invcon : this.invoiceConceptsRecoverList) {
			invcon.setRecoverValue(0.00);
		}

	}

	public double getTotalSelectedConcept() {
		return BillingTools.formatDouble(totalSelectedConcept);
	}

	public void setTotalSelectedConcept(double totalSelectedConcept) {
		this.totalSelectedConcept = totalSelectedConcept;
	}

	public boolean isSelectedAnyInvoice() {
		return selectedAnyInvoice;
	}

	public void setSelectedAnyInvoice(boolean selectedAnyInvoice) {
		this.selectedAnyInvoice = selectedAnyInvoice;
	}

	public double getRecoveredValue() {
		this.recoveredValue = 0.00;
		if (this.invoiceConceptsRecoverList != null) {
			for (InvoiceConcept invcon : this.invoiceConceptsRecoverList) {
				this.recoveredValue += BillingTools.formatDouble(invcon.getRecoverValue().doubleValue());
			}
		}
		return BillingTools.formatDouble(recoveredValue);
	}

	public void setRecoveredValue(double recoveredValue) {
		this.recoveredValue = recoveredValue;
	}

	public boolean isRecoverComplete() {
		return (this.moneyAvailable() == 0.00) || ((Math.abs(this.getRecoveredValue() - this.getTotalSelectedConcept()) == 0.00) && this.getAllMoneyAvailable() >= this.getRecoveredValue()) ? true : false;
	}

	public void closeModal1() {
		resetTotalRecoverValue();
		dateValidateMessage = null;
	}

	public void closeModal2() {
		this.recoveredValue = 0.00;
		this.recoverTotalValue = 0.00;
		this.recoverWithPriority = false;
		this.invoiceConceptsRecoverList = null;
		//
		this.invoiceRecoverList = null;
		this.searchInvoiceRecoverList();
	}

	public void closeAllModal() {
		closeModal2();
		closeModal1();
	}

	public void saveRecover() {

		try {
			this.instance = new Recover();
			this.instance.setDate(recoverDate != null ? recoverDate : new Date());
			this.instance.setValue(BillingTools.formatDouble(this.recoverTotalValue));
			this.instance.setPayFormType(this.payFormType);
			saveProcess();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Recaudo", "El recaudo con id : " + this.getId() + "  ha sido guardado exitosamente, por el usuario: " + this.user.getNombre()));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Recaudo", "Se ha generado un error al recaudar"));
		}
	}

	public void saveProcess() {
		BillingTools billingTools = new BillingTools(getEntityManager()); 
		double surplusTemp = 0.00;
		if (this.surplusAvailable()) {
			surplusTemp += lastRecover.getSurplusValue();
			this.saveOldRecover();
		}
		surplusTemp += (this.getAllMoneyAvailable().doubleValue() - this.getRecoveredValue());
		this.instance.setSurplusValue(BillingTools.formatDouble(surplusTemp));
		List<RecoverConcept> recoverConceptList = new ArrayList<RecoverConcept>();
		for (InvoiceConcept invcon : this.invoiceConceptsRecoverList) {
			
			if (BillingTools.formatDouble(invcon.getRecoverValue()) > 0.00) {
				
				//RECALCULO DE INTERESES
				billingTools.procesarRecalculacionIntereses(invcon, false,this.recoverDate);
				
				RecoverConcept recoverConceptTemp = new RecoverConcept();
				recoverConceptTemp.setRecover(this.instance);
				recoverConceptTemp.setValue(BillingTools.formatDouble(invcon.getRecoverValue().doubleValue()));
				invcon.setBalance(BillingTools.formatDouble(invcon.getBalance().doubleValue() - invcon.getRecoverValue().doubleValue()));
			
				//RECALCULO DE INTERESES
				billingTools.procesarRecalculacionIntereses(invcon, true,this.recoverDate);
				
				recoverConceptTemp.setInvoiceConcept(invcon);
				recoverConceptList.add(recoverConceptTemp);
			}
			invcon.setSelected(false);
		}
		saveCollectionAccount();
		this.instance.setRecoverConcepts(recoverConceptList);
		this.persist();  
		createRecoverPDF();
		this.invoiceConceptsRecoverList = null;
		this.recoverDate = null;
		this.recoveredValue = 0.0;
		this.recoverTotalValue = 0.0;
		this.recoverWithPriority =  false;
		this.selectAllInvoices = false;
		checkAllInvoices();
	}

	public void saveProcessAutomatic() {
		BillingTools billingTools = new BillingTools(getEntityManager());
		double surplusTemp = 0.00;
		if (this.surplusAvailable()) {
			surplusTemp += lastRecover.getSurplusValue();
			this.saveOldRecover();
		}
		surplusTemp += (this.getAllMoneyAvailable().doubleValue() - this.getRecoveredValue());
		this.instance.setSurplusValue(BillingTools.formatDouble(surplusTemp));
		List<RecoverConcept> recoverConceptList = new ArrayList<RecoverConcept>();
		for (InvoiceConcept invcon : this.invoiceConceptsRecoverList) {

			if (this.isInvoiceConceptInterestExcluded(invcon)) {
				log("No se hace recaudo al InvoiceConcept " + invcon.getId() + ", xq en el proyecto los intereses no son obligatorios");
				continue;
			}  

			if (BillingTools.formatDouble(invcon.getRecoverValue()) > 0.00) {
				billingTools.procesarRecalculacionIntereses(invcon, false,this.recoverDate);
				RecoverConcept recoverConceptTemp = new RecoverConcept();
				recoverConceptTemp.setRecover(this.instance);
				recoverConceptTemp.setValue(BillingTools.formatDouble(invcon.getRecoverValue().doubleValue()));
				invcon.setBalance(BillingTools.formatDouble(invcon.getBalance().doubleValue() - invcon.getRecoverValue().doubleValue()));
				billingTools.procesarRecalculacionIntereses(invcon, true,this.recoverDate);
				recoverConceptTemp.setInvoiceConcept(invcon);
				recoverConceptList.add(recoverConceptTemp);
			}
			invcon.setSelected(false);
		}
		saveCollectionAccount();
		this.instance.setRecoverConcepts(recoverConceptList);
		this.persist();

		createRecoverPDF();

	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public void saveOldRecover() {
		this.lastRecover.setSurplusValue(lastRecover.getSurplusValue() * -1);
		this.getEntityManager().joinTransaction();
		this.getEntityManager().persist(this.lastRecover);
		this.getEntityManager().flush();

	}

	public double doubleMaxValue() {
		return BillingTools.formatDouble(BillingTools.MAX_DOUBLE_VALUE);
	}

	public Recover getLastRecover() {
		return lastRecover;
	}

	public void setLastRecover(Recover lastRecover) {
		this.lastRecover = lastRecover;
	}

	public String getLineSum() {
		Locale.setDefault(Locale.ENGLISH);
		DecimalFormat df = new DecimalFormat("#.00");
		this.lineSum = "__";
		String number = df.format(this.getAllMoneyAvailable());
		for (int i = 0; i < number.length(); i++) {
			this.lineSum += "_";
		}
		return lineSum;
	}

	public void setLineSum(String lineSum) {
		this.lineSum = lineSum;
	}

	public boolean surplusAvailable() {
		if (this.lastRecover != null && this.lastRecover.getSurplusValue() > 0.00 ) {   
			return true;
		}  
		return false;    
	}

	public Double getAllMoneyAvailable() {
		this.allMoneyAvailable = new Double(0.00);
		if (this.recoverTotalValue != null) {
			this.allMoneyAvailable = this.recoverTotalValue.doubleValue();
		}
		if (this.surplusAvailable()) {
			this.allMoneyAvailable += lastRecover.getSurplusValue();
		}

		return BillingTools.formatDouble(allMoneyAvailable);
	}

	public void setAllMoneyAvailable(Double allMoneyAvailable) {
		this.allMoneyAvailable = allMoneyAvailable;
	}

	public double moneyAvailable() {
		return BillingTools.formatDouble(this.getAllMoneyAvailable() - this.getRecoveredValue());
	}

	public boolean renderButtonRecover() {
		return (this.invoiceRecoverList != null && this.invoiceRecoverList.size() > 0) && this.isRecoverInvoiceListSelectionAcceptable() && this.selectedAnyInvoice;
	}

	/**
	 * Metodo que suma las facturas selecionadas de una lista en recaudo
	 * 
	 * @return sum
	 */
	public double sumInvoice() {
		double sum = 0.0;
		if (this.getInvoiceRecoverList() != null) {
			for (Invoice invoice : this.getInvoiceRecoverList()) {
				if (invoice.isSelected() == true)
					sum += invoice.totalBalance();
			}
		}
		return sum;
	}

	public PayFormType getPayFormType() {
		return payFormType;
	}

	public void setPayFormType(PayFormType payFormType) {
		this.payFormType = payFormType;
	}

	public List<PayFormType> payFormsList() {
		List<PayFormType> payFormTypes = (List<PayFormType>) new PayFormTypeList().getResultList();
		if (payFormType == null && payFormTypes != null && !payFormTypes.isEmpty()) {
			payFormType = payFormTypes.get(1);
		}
		return payFormTypes;
	}

	public Date getRecoverDate() {
		if (recoverDate == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			recoverDate = calendar.getTime();
			validateRecoverDate();
		}
		return recoverDate;
	}

	public void setRecoverDate(Date recoverDate) {
		System.out.println(recoverDate);
		this.recoverDate = recoverDate;
	}

	public String getDateValidateMessage() {
		return dateValidateMessage;
	}

	public void setDateValidateMessage(String dateValidateMessage) {
		this.dateValidateMessage = dateValidateMessage;
	}

	private int recoverCloseMonth() {
		if (invoiceRecoverList != null && !invoiceRecoverList.isEmpty()) {
			Calendar today = Calendar.getInstance();

			Query q = getEntityManager().createQuery("FROM RecoverClosure rc WHERE rc.project.id = ? ORDER BY rc.closureDate DESC");
			q.setParameter(1, this.invoiceRecoverList.get(0).getProjectProperty().getProject().getId());
			q.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<RecoverClosure> closures = q.getResultList();
			if (closures != null && !closures.isEmpty()) {
				Calendar compareDate = Calendar.getInstance();
				compareDate.setTime(closures.get(0).getClosureDate());
				if(compareDate.get(Calendar.YEAR) < today.get(Calendar.YEAR)){
					compareDate.add(Calendar.MONTH, 1);
					return compareDate.get(Calendar.MONTH);
				}			 
				else if (compareDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && compareDate.get(Calendar.MONTH) < today.get(Calendar.MONTH)) {
					compareDate.add(Calendar.MONTH, 1);
					return compareDate.get(Calendar.MONTH);
				} else if (compareDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && compareDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
					return today.get(Calendar.MONTH);
				} else
					return -1;
			} else {
				today.add(Calendar.MONTH, -1);
				return today.get(Calendar.MONTH);
			}
		} 
			return -1;

	}

	public void validateRecoverDate() {
		Calendar dateAux = Calendar.getInstance();
		dateAux.setTime(this.getRecoverDate());
		int periodMonth = recoverCloseMonth();
		if (periodMonth != -1) {
			if (dateAux.get(Calendar.MONTH) == periodMonth) {
				dateValidateMessage = null;
			} else {
				dateValidateMessage = "La fecha del recaudo no pertenece al periodo actual del recaudo";
			} 
		} else { 
			dateValidateMessage = "Error en los periodos de recaudo";
		}
	}

	public void createRecoverPDF() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat formatter = new DecimalFormat("#,###.00");
		double total = 0.0;
		double balance = 0.0;

		Invoice invoice = new Invoice();

		Document document = new Document(PageSize.LETTER);
		try {
			for (Invoice inv : this.invoiceRecoverList) {
				if (inv.isSelected()) {
					invoice = inv;
				}
			}

			// String filePDF = "tmp/recover/recover" + this.instance.getId() +
			// ".pdf";
			String filePDF = "tmp/recover/recover" + invoice.getProjectProperty().getProject().getProjectPrefix() + "-" + this.instance.getPrefixCollectionAccounts() + "-" + this.instance.getMinCollectionAccounts() + "-" + this.instance.getSuffixCollectionAccounts() + ".pdf";
			String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
			path = path.substring(0, path.lastIndexOf("/")) + "/";
//			this.linkPDF = filePDF;
			setAttachment(path + filePDF);
			String filePDF1 = "";
			// PdfWriter.getInstance(document, new FileOutputStream(path +
			// filePDF));
			// document.open();

			// --------------------------
			Query q = this.getEntityManager().createQuery("FROM SystemConfiguration sc WHERE sc.name = ?");
			q.setParameter(1, SystemConfiguration.Carpeta_PDF);
			q.setMaxResults(1);
			systemConfiguration = (SystemConfiguration) q.getSingleResult();

			String OrigenCarpeta = systemConfiguration.getValue() + "/" + invoice.getProjectProperty().getProject().getId() + "_" + invoice.getProjectProperty().getProject().getNameProject();
			File directorio = new File(OrigenCarpeta);
			directorio.mkdir();

			// filePDF1 =
			// invoice.getProjectProperty().getProject().getProjectPrefix() +
			// "-R-" + this.instance.getId() + ".pdf";
			filePDF1 = invoice.getProjectProperty().getProject().getProjectPrefix() + "-" + this.instance.getPrefixCollectionAccounts() + "-" + this.instance.getMinCollectionAccounts() + (this.instance.getSuffixCollectionAccounts()==null || this.instance.getSuffixCollectionAccounts().isEmpty() ? "-" + "R" : "-" + this.instance.getSuffixCollectionAccounts() + "-"  + "R" )+ ".pdf";
			PdfWriter.getInstance(document, new FileOutputStream(OrigenCarpeta + "/" + filePDF1));
			this.linkPDF = OrigenCarpeta + "/" + filePDF1; 
			// -------------------------- 

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + filePDF));
			document.open();

			document.newPage();

			// Introduccion y posicionamiento del logo 1 y logo 2
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
				String fileIMAGE = "img/log_Terranum.jpg";
				Image image1 = Image.getInstance(path + fileIMAGE);
				image1.setAbsolutePosition(400, 700);
				image1.scaleToFit(150, 80);
				document.add(image1);
			}

			String fontFile = path + "Font/TAHOMA.TTF";
			BaseFont bf = BaseFont.createFont(fontFile, BaseFont.IDENTITY_H, true);

			Font font1 = new Font(bf, 7, Font.NORMAL);
			Font font2 = new Font(bf, 8, Font.BOLD);
			Font font4 = new Font(bf, 8, Font.NORMAL);
			Font font5 = new Font(bf, 10, Font.NORMAL, new BaseColor(255, 255, 255));

			document.add(new Paragraph("\n\n\n"));

			
			PdfPTable table100 = new PdfPTable(2);
			table100.setWidthPercentage(100);
			table100.setHorizontalAlignment(Element.ALIGN_LEFT);

			
			//PdfPCell cell1 = new PdfPCell(new Paragraph("" + invoice.getNameBiller(), font1));
		
			
			PdfPTable table6 = new PdfPTable(1);
			table6.setTotalWidth(new float[] { 230f });
			table6.setWidthPercentage(100);
			table6.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell cell1 = new PdfPCell(new Paragraph(DatosLogo(invoice), font1));
			cell1.setBorder(Rectangle.NO_BORDER);
			table6.addCell(cell1);

			table6.writeSelectedRows(0, -1, PageSize.LETTER.getWidth() * 0.1f, PageSize.LETTER.getHeight() * 0.885f, writer.getDirectContent());

			document.add(table6);
			
			PdfPTable table1 = new PdfPTable(2);
			table1.setWidthPercentage(100);
			table1.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell cell0 = new PdfPCell(new Paragraph("", font1));
			cell0.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell0);

			PdfPCell cell2 = new PdfPCell(new Paragraph("", font4));
			cell2.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell2);
			PdfPCell cell3 = new PdfPCell(new Paragraph("", font4));
			cell3.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell3);
			PdfPCell cell8 = new PdfPCell(new Paragraph("RECIBO DE CAJA No " + this.instance.getPrefixCollectionAccounts() + " - " + this.instance.getMinCollectionAccounts() + (this.instance.getSuffixCollectionAccounts() != null && !this.instance.getSuffixCollectionAccounts().isEmpty() ? " - " + this.instance.getSuffixCollectionAccounts() : ""), font2));
			cell8.setBorder(Rectangle.NO_BORDER);
			cell8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			table1.addCell(cell8);

			PdfPCell cell9 = new PdfPCell(new Paragraph("", font4));
			cell9.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell9);

			PdfPCell cell10 = new PdfPCell(new Paragraph("", font4));
			cell10.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell10);
			PdfPCell cell66 = new PdfPCell(new Paragraph("", font4));
			cell66.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell66);
			PdfPCell cell67 = new PdfPCell(new Paragraph("\n", font4));
			cell67.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell67);
			PdfPCell cell11 = new PdfPCell(new Paragraph("Recibido de :  " + invoice.getNameBilled(), font4));
			cell11.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell11);

			PdfPCell cell12 = new PdfPCell(new Paragraph("", font1));
			cell12.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell12);
			PdfPCell cell13 = new PdfPCell(new Paragraph("Nit o CC:  " + invoice.getIdNumberBilled() + (invoice.getBilled().getIdType() == 31 ? (" - " + invoice.getBilled().getVerificationNumber()) : ""), font4));
			cell13.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell13);

			PdfPCell cell14 = new PdfPCell(new Paragraph("", font1));
			cell14.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell14);

			PdfPCell cell15 = new PdfPCell(new Paragraph("Fecha de Recaudo:  " + dateFormat.format(this.getRecoverDate()), font4));
			cell15.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell15);

			PdfPCell cell16 = new PdfPCell(new Paragraph("", font1));
			cell16.setBorder(Rectangle.NO_BORDER);
			table1.addCell(cell16);

			document.add(table1);
			document.add(new Paragraph("\n\n\n\n"));

			// ///////////////////PRIMERA PARTE DE LA
			// TABLA//////////////////////////////////////////////

			float[] colsWidth2 = { 1f, 2f, 1f };
			PdfPTable table2 = new PdfPTable(colsWidth2);
			table2.setWidthPercentage(100);
			table2.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell cell17 = new PdfPCell(new Paragraph("DOCUMENTO ", font5));
			cell17.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			cell17.setGrayFill(0.6f);
			table2.addCell(cell17);

			PdfPCell cell18 = new PdfPCell(new Paragraph("CONCEPTO ", font5));
			cell18.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			cell18.setGrayFill(0.6f);
			table2.addCell(cell18);

			PdfPCell cell19 = new PdfPCell(new Paragraph("VALOR ", font5));
			cell19.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			cell19.setGrayFill(0.6f);
			table2.addCell(cell19);

			document.add(table2);

			for (InvoiceConcept lista : this.invoiceConceptsRecoverList) {
                if (lista.getRecoverValue()==0)
                	continue;
				float[] colsWidth = { 1f, 2f, 1f };
				PdfPTable table = new PdfPTable(colsWidth);
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(Element.ALIGN_CENTER);

				// [FACTURA O CUENTA DE COBRO]
				if (invoice.getNumber() == null) {
					Phrase phrase1 = new Phrase(20, " [# Factura o Cuenta de Cobro]", font4);
					PdfPCell cell20 = new PdfPCell(new Paragraph(phrase1));
					cell20.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
					table.addCell(cell20);
				} else {
					// Phrase phrase1 = new Phrase(20, " " +
					// lista.getInvoice().getNumber(), font4);
					Phrase phrase1 = new Phrase(20, " " + new ProjectHome().invoiceNumber(lista.getInvoice()), font4);
					PdfPCell cell20 = new PdfPCell(new Paragraph(phrase1));
					cell20.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
					table.addCell(cell20);
				}

				// [CONCEPTOS DE FACTURACION]
				ConceptHome home = new ConceptHome();
					Phrase phrase2 = new Phrase(20, "" + home.nameInvoiceConcept(lista), font4);
					PdfPCell cell21 = new PdfPCell(new Paragraph(phrase2));
					cell21.setBorder(Rectangle.RIGHT); 
					table.addCell(cell21);
				

				// [VALOR RECAUDO ]
				Phrase phrase3 = new Phrase(20, "$" + formatter.format(lista.getRecoverValue()), font4);

				PdfPCell cell22 = new PdfPCell(new Paragraph(phrase3));
				cell22.setBorder(Rectangle.RIGHT);
				cell22.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
				table.addCell(cell22);

				document.add(table);

				total += BillingTools.formatDouble(lista.getRecoverValue());
				balance = balance + lista.getBalance();
			}

			// --------Excedente--------------------------------------------------

			float[] colsWidth3 = { 1f, 2f, 1f };
			PdfPTable table7 = new PdfPTable(colsWidth3);
			table7.setWidthPercentage(100);
			table7.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell cell22 = new PdfPCell(new Paragraph(""));
			cell22.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
			table7.addCell(cell22);

			PdfPCell cell23 = new PdfPCell(new Paragraph("\n\nSALDO\n ", font4));
			cell23.setBorder(Rectangle.RIGHT);
			table7.addCell(cell23);

			PdfPCell cell24 = new PdfPCell(new Paragraph("\n\n$" + formatter.format(balance), font4));
			cell24.setBorder(Rectangle.RIGHT);
			cell24.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
			table7.addCell(cell24);

			document.add(table7);

			// --------------------------------------------------

			Phrase phrase4 = new Phrase(20, "TOTAL RECAUDO  ", font4);
			// [TOTAL]
			Phrase phrase5 = new Phrase(20, "$" + formatter.format(total), font4);

			float[] colsWidth1 = { 2f, 1f, 1f };
			PdfPTable table3 = new PdfPTable(colsWidth1);
			table3.setWidthPercentage(100);
			table3.setHorizontalAlignment(Element.ALIGN_CENTER);

			table3.addCell("");
			table3.addCell(phrase4);

			PdfPCell cell25 = new PdfPCell(phrase5);
			cell25.setBorder(Rectangle.TOP + Rectangle.RIGHT);
			cell25.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
			table3.addCell(cell25);

			document.add(table3);

			// /////////////////////////TERCERA PARTE DE LA
			// TABLA///////////////////////////

			Phrase phrase21 = new Phrase(20, "SON: " + convertNumberToString.convertToString(total), font4);

			PdfPTable table4 = new PdfPTable(1);
			table4.setWidthPercentage(100);
			table4.setHorizontalAlignment(Element.ALIGN_CENTER);
			table4.addCell(phrase21);

			// //////////////////////////CUARTA PARTE DE LA
			// TABLA////////////////////////////
			Phrase phrase23 = new Phrase(20, "\n Documento: R" + this.instance.getRecoverConcepts().get(0).getInvoiceConcept().getInvoice().getProjectProperty().getConsecutiveCollectionAccount().getSiigoCode() + " - " + this.instance.getMinCollectionAccounts() + " - " + this.instance.getSuffixCollectionAccounts() + " \n Forma de Pago: " + this.instance.getPayFormType().getName() + "  \n Nombre Banco  ", font4);
			Phrase phrase24 = new Phrase(20, "\n  Firma de Recibido: ", font2);

			PdfPTable table5 = new PdfPTable(2);
			table5.setWidthPercentage(100);
			table5.setHorizontalAlignment(Element.ALIGN_TOP);

			table5.addCell(phrase23);
			table5.addCell(phrase24);

			document.add(table4);
			document.add(table5);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
			document.close();
		}

	}

	private String linkPDF = null;

	public String getLinkPDF() {
		return this.linkPDF;
	}

	public void setLinkPDF(String linkPDF) {
		this.linkPDF = linkPDF;
	}

	public void setRecoverId(Integer id) {
		setId(id);
	}

	public Integer getRecoverId() {
		return (Integer) getId();
	}

	@Override
	protected Recover createInstance() {
		Recover recover = new Recover();
		return recover;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public String currentRecover() {
		Project project = null;
		if (this.invoiceRecoverList != null) {
			for (Invoice invoice : this.invoiceRecoverList) {
				project = invoice.getProjectProperty().getProject();
			}
		}
		Calendar calendar2 = Calendar.getInstance();
		Calendar calendar = getDateCurrentRecover(project);
		String date = "";
		if (calendar == null) {
			try {
				date = RecoverClosure.MONTHS[calendar2.get(Calendar.MONTH) - 1];
			} catch (Exception e) {
				date = "Diciembre";
			}
		}

		else {
			date = RecoverClosure.MONTHS[calendar.get(Calendar.MONTH)];
		}

		return date;
	}

	private Calendar getDateCurrentRecover(Project project) {
		Calendar calendar = Calendar.getInstance();

		if (project != null) {
			Query q = this.getEntityManager().createQuery("from RecoverClosure rc WHERE rc.project = ? ORDER BY rc.closureDate DESC"); 
			q.setParameter(1, project);
			@SuppressWarnings("unchecked")
			List<RecoverClosure> recoverClosuresList = (List<RecoverClosure>) q.getResultList();
			if (recoverClosuresList != null && recoverClosuresList.size() > 0) {
				calendar.setTime(recoverClosuresList.get(0).getClosureDate());
				calendar.add(Calendar.MONTH, +1);
				return calendar;
			}
		}

		return null;
	}

	public void saveCollectionAccount() {
		if (this.getInvoiceRecoverList() != null) {
			for (Invoice invoice : this.getInvoiceRecoverList()) {
				if (invoice.isSelected() == true) {

					ConsecutiveCollectionAccount consecutiveCollectionAccount = invoice.getProjectProperty().getConsecutiveCollectionAccount();
					if (consecutiveCollectionAccount.getSuffix() != null)
						this.instance.setSuffixCollectionAccounts(consecutiveCollectionAccount.getSuffix());
					if (consecutiveCollectionAccount.getPrefix() != null)
						this.instance.setPrefixCollectionAccounts(consecutiveCollectionAccount.getPrefix());
					this.instance.setMinCollectionAccounts(consecutiveCollectionAccount.getMin());
					consecutiveCollectionAccount.setMin(consecutiveCollectionAccount.getMin() + 1);
					this.joinTransaction();
					this.getEntityManager().persist(consecutiveCollectionAccount);
					this.getEntityManager().flush();
					break;
				}

			}
		}
	}

	public String getLinkSIIGO() {
		return linkSIIGO;
	}

	public void setLinkSIIGO(String linkSIIGO) {
		this.linkSIIGO = linkSIIGO;
	}

	public void generatedSiigo(Project project) {
		Calendar currentDate = getDateCurrentRecover(project);

		Query query;
		if (currentDate == null) {
			query = this.getEntityManager().createQuery("SELECT DISTINCT(rc.recover) FROM RecoverConcept rc WHERE rc.invoiceConcept.invoice.projectProperty.project = ?");
			query.setParameter(1, project);
		} else {
			query = this.getEntityManager().createQuery("SELECT DISTINCT(rc.recover) FROM RecoverConcept rc WHERE rc.invoiceConcept.invoice.projectProperty.project = ?" + " AND  ? <= rc.recover.date AND rc.recover.date < ?)");

			query.setParameter(1, project);
			Calendar iniDate = Calendar.getInstance();
			iniDate.setTime(currentDate.getTime());
			//iniDate.add(Calendar.MONTH, 1);
			iniDate.set(Calendar.DATE, 1);
			iniDate.set(Calendar.HOUR, 0);
			iniDate.set(Calendar.MINUTE, 0);
			iniDate.set(Calendar.SECOND, 0);
			iniDate.set(Calendar.MILLISECOND, 0);
			query.setParameter(2, iniDate.getTime());

			Calendar endDate = Calendar.getInstance();
			endDate.setTime(currentDate.getTime());
			endDate.add(Calendar.MONTH, 2);
			endDate.set(Calendar.DATE, 1);
			endDate.set(Calendar.HOUR, 0);
			endDate.set(Calendar.MINUTE, 0);
			endDate.set(Calendar.SECOND, 0);
			endDate.set(Calendar.MILLISECOND, 0);
			query.setParameter(3, endDate.getTime());
		}

		@SuppressWarnings("unchecked")
		List<Recover> recovers = query.getResultList();

		SiigoFunctions siigoFunctions = new SiigoFunctions(getEntityManager());
		setLinkSIIGO(siigoFunctions.recoverSiggo(recovers, project));
	}

	public String getLabelMandatoryInterest(InvoiceConcept invoiceConcept) {

		if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && !invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			return "(Opcional)";
		} else {
			return "";
		}

	}

	public boolean isInvoiceConceptInterestExcluded(InvoiceConcept invoiceConcept) {

		boolean isInterest = invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST;
		boolean projectInterestExcluded = !invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest();
		return (isInterest && projectInterestExcluded);
	}

	private String DatosLogo(Invoice invoice) {
		String datosLogo = "";
		if (invoice.getProjectProperty().isMandate() == true) {

			BusinessEntity mandante = invoice.getProjectProperty().getProject().getBusinessEntity();
			datosLogo = mandante.getNameBusinessEntity() + "\n" + (mandante.getAddresses() != null && !mandante.getAddresses().isEmpty() ? mandante.getAddresses().iterator().next().toString() : "") + (mandante.getPhoneNumbers() != null && !mandante.getPhoneNumbers().isEmpty() ? " TEL. " + mandante.getPhoneNumbers().iterator().next().toString() : "") + "\n" + (mandante.getCity() != null ? mandante.getCity().getName() : "") + " - " + (mandante.getAddresses() != null && !mandante.getAddresses().isEmpty() ? mandante.getAddresses().iterator().next().getCity().getRegion().getCountry().getName() : "") + "\n" + (mandante.getIdType() == 31 ? ("NIT." + mandante.getIdNumber() + " - " + mandante.getVerificationNumber()) : "C.C." + mandante.getIdNumber());
		} else {
			datosLogo = invoice.getNameBiller() + "\n" + invoice.getAddressBiller() + " TEL. " + invoice.getProjectProperty().getPhoneNumberByPhoneBiller().getNumber() + "\n" + invoice.getCityBiller() + " - " + invoice.getProjectProperty().getBillerAddress().getCity().getRegion().getCountry().getName() + "\n" + (invoice.getBiller().getIdType() == 31 ? ("NIT." + invoice.getBiller().getIdNumber() + " - " + invoice.getBiller().getVerificationNumber()) : "C.C." + invoice.getBiller().getIdNumber());
		}
		return datosLogo;
	}
	
	public boolean isPdfDownloable(){
		if(this.linkPDF!=null)
			return true;
			return false;
	}


	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
