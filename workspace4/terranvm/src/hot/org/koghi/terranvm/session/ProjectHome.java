package org.koghi.terranvm.session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.bean.DownloadAttachment;
import org.koghi.terranvm.bean.Format_number;
import org.koghi.terranvm.bean.SiigoFunctions;
import org.koghi.terranvm.entity.Address;
import org.koghi.terranvm.entity.BillingResolution;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.BusinessLine;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.ConceptRetentionRateAccount;
import org.koghi.terranvm.entity.ConsecutiveAccountsBilling;
import org.koghi.terranvm.entity.GroupCache;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.ObjectOfContract;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectClosure;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.ProjectUser;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.RecoverClosure;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RetentionRateAccount;
import org.koghi.terranvm.entity.SystemConfiguration;
import org.koghi.terranvm.entity.User_Terranvm;
import org.richfaces.component.html.HtmlExtendedDataTable;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

@Name("projectHome")
public class ProjectHome extends EntityHome<Project> {

	/**
     * 
     */

	private static final long serialVersionUID = -2650201792391779354L;
	// private static final float HEIGHT_CELL_CONCEPT = 20F;
	private static final float HEIGHT_ONEPAGE_CONCEPT_TABLE = 140.0F;

	// variables de control de fuentes
	private static final int FONT1 = 1;
	private static final int FONT2 = 2;
	private static final int FONT3 = 3;
	private static final int FONT4 = 4;
	private static final int FONT5 = 5;
	private static final int FONT6 = 6;
	private static final int FONT7 = 7;
	private static final int FONT8 = 8;

	@In(create = true)
	BusinessEntityHome businessEntityHome;
	@In(create = true)
	BusinessLineHome businessLineHome;

	@In
	EntityManager entityManager;

	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	private User_Terranvm user;
	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	public List<SelectItem> projectsFilter;

	// PRELIQUIDACION
	private List<Invoice> invoiceList;
	private Project selectedProject;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable preliquidationTableInvoiceBind;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableConceptComparableBind;
	private String preliquidationTableInvoiceState;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable chartTableConceptBind;
	private String chartTableInvoiceConceptState;
	private String tableConceptCamparableState;
	private Invoice selectedInvoice;
	private String linkSiigoFile;
	private int historicConceptNumber;
	private String billingPeriod;
	private Calendar filterIniDateProject;
	private Calendar filterEndDateProject;
	private boolean projectCloseable;
	private Date expeditionInvoiceDate;
	private Date expirationInvoiceDate;
	private boolean allInvoices;
	private ArrayList<Object[]> conceptComparableList;
	private ArrayList<Object[]> conceptComparableListTotal;
	public static List<Integer> billingResolutionList = new ArrayList<Integer>();
	public static List<Integer> consecutiveAccountsBillingList = new ArrayList<Integer>();
	private HashMap<Concept, InvoiceConcept> hashInvoconNormal = new HashMap<Concept, InvoiceConcept>();
	private HashMap<String, InvoiceConcept> hashInvoconBulkLoad = new HashMap<String, InvoiceConcept>();
	private BillingTools tools = new BillingTools(getEntityManager());

	@In(required = false)
	public SystemConfiguration systemConfiguration = new SystemConfiguration();

	// PRELIQUIDACION

	private String errorMessageP;
	private boolean errorP;

	// NOVEDADES
	private List<ProjectProperty> listNews;
	private boolean bandera = false;
	private ProjectProperty selectedTermSheet;
	// creacion de pdf
	private List<String> listpdf;
	private String filePDF1;
	private String OrigenCarpeta;
	private String path;
	PdfWriter writer;
	private double subtotal;
	private double iva;
	private double total;
	private double totalStamptax;

	// Introduccion y
	// posicionamiento del logo 1
	// y logo 2

	@DataModel
	HashMap<String, ArrayList<String>> changeNews = new HashMap<String, ArrayList<String>>();
	// NOVEDADES

	// AGRUPACIÓN
	private List<GroupCache> groups;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableGroupsBind;
	// AGRUPACIÓN

	// Cierre de Recaudo
	private boolean recoverCloseable;
	// Cierre de Recaudo

	// formato
	NumberFormat formatter = new DecimalFormat("#,###.00");
	private Session session;

	public ConvertNumberToString convertNumberToString = new ConvertNumberToString();
	public Integer roundType;

	public Integer getRoundType() {
		if (this.instance.getRounding() != null && this.instance.getRounding() != 0)
			roundType = 1;
		if (roundType == null) {
			roundType = 0;
			this.instance.setRounding(null);
		}
		return roundType;
	}

	public void setRoundType(Integer roundType) {
		this.roundType = roundType;
	}

	public boolean withRound() {
		if (this.roundType != null) {
			if (this.roundType == 1)
				return true;
			else if (this.roundType == 0)
				return false;
		}
		return false;
	}

	public void setProjectId(Integer id) {
		setId(id);
	}

	public Integer getProjectId() {
		return (Integer) getId();
	}

	@Override
	protected Project createInstance() {
		Project project = new Project();
		return project;
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
		BusinessLine businessLine = businessLineHome.getDefinedInstance();
		if (businessLine != null) {
			getInstance().setBusinessLine(businessLine);
		}
	}

	public boolean isWired() {
		if (getInstance().getBusinessEntity() == null)
			return false;
		if (getInstance().getBusinessLine() == null)
			return false;
		return true;
	}

	public Project getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProjectProperty> getProjectProperties() {
		return getInstance() == null ? null : new ArrayList<ProjectProperty>(getInstance().getProjectProperties());
	}

	public List<RecoverClosure> getRecoverClosures() {
		return getInstance() == null ? null : new ArrayList<RecoverClosure>(getInstance().getRecoverClosures());
	}

	@Override
	public String update() {
		Project p = getEntityManager().merge(getInstance());
		new MakerCheckerHome().persistObject(getInstance(), p);
		updatedMessage();
		return "updated";
	}

	public boolean projectListInApprove(Project project) {
		return new MakerCheckerHome().isObjectInMakerChecker(project);
	}

	public void updateInstanceMaker(int makerCheckerId) {
		setInstance((Project) new MakerCheckerHome().getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}

	@SuppressWarnings("deprecation")
	@Transactional
	public void approveChange() {
		// setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}", "ApproveSuccessfully");
	}

	@SuppressWarnings("deprecation")
	public void cancelChange() {
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_cancellation}", "CancelSuccessfully");
	}

	public List<Invoice> getInvoiceList() {
		if (this.invoiceList == null && this.selectedProject != null) {
			preliquidationTableInvoiceState = null;
			this.invoiceList = new ArrayList<Invoice>();

			this.calculateProjectFilterDates(this.selectedProject);

			StringBuilder sql = new StringBuilder();

			sql.append("SELECT inv ");
			/* Subtotal de la factura */
			sql.append(",(SELECT SUM(ic.calculatedCost) from InvoiceConcept ic where ic.invoice = inv.id AND ic.invoiceConceptType != ? ) AS subtotal");

			/* IVA de la factura */
			sql.append(",(SELECT SUM(ic.calculatedTax) from InvoiceConcept ic where ic.invoice = inv.id AND ic.invoiceConceptType != ? ) AS iva ");

			sql.append("FROM Invoice inv ");
			sql.append("WHERE inv.projectProperty.project = ? AND inv.creationDate >= ? AND inv.creationDate < ?  AND inv.approved = ? ");

			/*
			 * ORDENAMIENTO POR NOMBRE DEL FACTURADO Y TIPO DE DOCUMENTO
			 */
			sql.append("ORDER BY inv.nameBilled");

			Query query = this.getEntityManager().createQuery(sql.toString());
			query.setParameter(1, InvoiceConcept.TYPE_CREDIT_NOTE);
			query.setParameter(2, InvoiceConcept.TYPE_CREDIT_NOTE);
			query.setParameter(3, this.selectedProject);
			query.setParameter(4, this.filterIniDateProject.getTime());
			query.setParameter(5, this.filterEndDateProject.getTime());
			query.setParameter(6, false);
			log(Level.INFO, "Selected Project " + selectedProject.getId());
			log(Level.INFO, "Ini date " + this.filterIniDateProject.getTime());
			log(Level.INFO, "End date " + this.filterEndDateProject.getTime());
			log(Level.INFO, "Lista de facturas " + sql);
			List<?> result = (List<?>) query.getResultList();
			for (Object value : result) {
				Object[] val = (Object[]) value;
				Invoice inv = (Invoice) val[0];
				if (val[1] != null)
					inv.setSubtotal(Double.valueOf(val[1].toString()));
				if (val[2] != null)
					inv.setIva(Double.valueOf(val[2].toString()));
				this.invoiceList.add(inv);
			}
			log(Level.INFO, "Fin de Consulta");
			log(Level.INFO, "El proyecto " + this.selectedProject.getId() + " tiene " + invoiceList.size() + " facturas pendientes por aprobar");
		}

		return invoiceList;
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoiceListApprove() {
		if (this.selectedProject != null) {

			this.invoiceList = new ArrayList<Invoice>();
			log(Level.INFO, "Buscando facturas aprobadas");

			this.calculateProjectFilterDates(this.selectedProject);

			Query query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.projectProperty.project = ? AND inv.creationDate >= ? AND inv.creationDate <= ?  AND inv.approved = ? AND inv.invoiceStatus.id <> ? AND inv.approved = true ORDER BY inv.documentType , inv.number");
			query.setParameter(1, this.selectedProject);
			query.setParameter(2, this.filterIniDateProject.getTime());
			query.setParameter(3, this.filterEndDateProject.getTime());
			query.setParameter(4, true);
			query.setParameter(5, InvoiceStatus.STATUS_REVERSADA);

			this.invoiceList = query.getResultList();

		}

		if (this.invoiceList != null && this.invoiceList.isEmpty() && this.selectedProject != null) {
			log(Level.INFO, "**************************************************************** El proyecto " + this.selectedProject.getId() + " no tiene facturas vigentes aprobadas");
			log(Level.INFO, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^list size " + invoiceList.size());
		}
		if (this.invoiceList != null && this.invoiceList.isEmpty()) {

		}

		return invoiceList;
	}

	public void calculateProjectFilterDates(Project project) {

		log(Level.INFO, "calculateProjectFilterDates project ID" + project.getId());
		Query query = this.getEntityManager().createQuery("from ProjectClosure pc WHERE pc.project = " + project.getId() + " ORDER BY pc.closureDate DESC");
		query.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<ProjectClosure> list = (List<ProjectClosure>) query.getResultList();
		Calendar firstBillingDate = Calendar.getInstance();
		if (list.isEmpty()) {
			firstBillingDate.add(Calendar.MONTH, -1);
		} else {
			ProjectClosure pc = list.get(0);
			Calendar lastClosure = Calendar.getInstance();
			lastClosure.setTime(pc.getClosureDate());
			while (true) {
				if (lastClosure.get(Calendar.YEAR) == firstBillingDate.get(Calendar.YEAR) && lastClosure.get(Calendar.MONTH) == firstBillingDate.get(Calendar.MONTH))
					break;
				else {
					firstBillingDate.add(Calendar.MONTH, -1);
				}
			}
		}

		this.filterIniDateProject = Calendar.getInstance();
		this.filterIniDateProject.setTime(firstBillingDate.getTime());
		// La fecha filtro inicial se configura al inicio del ultimo
		// periodo
		// no cerrado para el proyecto
		this.filterIniDateProject.set(Calendar.DATE, 1);
		this.filterIniDateProject.set(Calendar.HOUR_OF_DAY, 0);
		this.filterIniDateProject.set(Calendar.MINUTE, 0);
		this.filterIniDateProject.set(Calendar.SECOND, 0);
		// La fecha filtro final se configura justo un mes despues
		// de la
		// fecha filtro inicial
		firstBillingDate.add(Calendar.MONTH, +1);
		this.filterEndDateProject = Calendar.getInstance();
		this.filterEndDateProject.setTime(firstBillingDate.getTime());
		this.filterEndDateProject.set(Calendar.DATE, 1);
		this.filterEndDateProject.set(Calendar.HOUR_OF_DAY, 0);
		this.filterEndDateProject.set(Calendar.MINUTE, 0);
		this.filterEndDateProject.set(Calendar.SECOND, 0);
		// this.filterEndDateProject.add(Calendar.DATE, -1);

		String[] months = new String[12];
		months[0] = "ENERO";
		months[1] = "FEBRERO";
		months[2] = "MARZO";
		months[3] = "ABRIL";
		months[4] = "MAYO";
		months[5] = "JUNIO";
		months[6] = "JULIO";
		months[7] = "AGOSTO";
		months[8] = "SEPTIEMBRE";
		months[9] = "OCTUBRE";
		months[10] = "NOVIEMBRE";
		months[11] = "DICIEMBRE";

		this.setBillingPeriod(months[this.filterIniDateProject.get(Calendar.MONTH)]);

		log(Level.INFO, "********************************************FILTER DATE PROJECT *******************************************");
		log(Level.INFO, "filter Ini Date Project: " + this.filterIniDateProject.getTime());
		log(Level.INFO, "filter End Date Project: " + this.filterEndDateProject.getTime());
		log(Level.INFO, "PERIOD: " + this.billingPeriod);

	}

	public boolean isInvoiceListApproveAble() {
		if (this.invoiceList != null && !this.invoiceList.isEmpty()) {
			for (Invoice inv : this.invoiceList) {
				if (inv.isSelected())
					return true;
			}
		}
		return false;
	}

	public void reGetInvoiceList() {
		this.invoiceList = null;
		this.getInvoiceList();
	}

	public void setInvoiceList(List<Invoice> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public Project getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
	}

	public ProjectProperty getSelectedTermSheet() {
		return selectedTermSheet;
	}

	public void setSelectedTermSheet(ProjectProperty selectedTermSheet) {
		this.selectedTermSheet = selectedTermSheet;
	}

	public void instanceTermSheet(ProjectProperty p) {
		this.setSelectedTermSheet(p);
	}

	public void instanceProject(Project p) {
		this.setLinkSiigoFile(null);
		this.setSelectedProject(p);
		this.getInvoiceList();
		this.projectCloseable = this.projectCalculateCloseable();
		this.recoverCloseable = this.recoverCalculateCloseable();
	}

	public void instanceInvoice(Invoice i) {
		destroyModal2();
		this.setSelectedInvoice(i);
	}

	public void destroyModal1() {
		this.selectedProject = null;
		this.invoiceList = null;
		this.conceptComparableList = null;
		this.conceptComparableListTotal = null;
		this.setAllInvoices(false);
		this.billingPeriod = null;
		this.projectCloseable = false;
		this.recoverCloseable = false;
		this.bandera = false;
		listNews = new ArrayList<ProjectProperty>();
	}

	public void destroyModal2() {
		this.selectedInvoice = null;
		this.billingPeriod = null;
		this.projectCloseable = false;
		this.recoverCloseable = false;
		this.bandera = false;
		listNews = new ArrayList<ProjectProperty>();
	}

	public void destroyModal3() {
		this.selectedProject = null;
		this.projectCloseable = true;

		this.recoverCloseable = false;

		this.bandera = false;
		listNews = new ArrayList<ProjectProperty>();
	}

	public Invoice getSelectedInvoice() {
		return selectedInvoice;
	}

	public void setSelectedInvoice(Invoice selectedInvoice) {
		conceptComparableList = null;
		this.selectedInvoice = selectedInvoice;
	}

	public ArrayList<Object[]> conceptHistoricList(InvoiceConcept invoiceConcept) {

		Query query = null;
		if (invoiceConcept.getInvoiceConceptParent() == null) {
			query = this.getEntityManager().createQuery("SELECT invoiceConcept.calculatedCost, invoiceConcept.calculatedTax, invoiceConcept.lastLiquidationDate, invoiceConcept.id FROM InvoiceConcept invoiceConcept, Invoice invoice WHERE invoice.id = invoiceConcept.invoice.id AND invoiceConcept.concept = ? AND invoiceConcept.invoiceConceptType = ? AND invoiceConcept.invoiceConceptParent IS NULL ORDER BY invoice.expeditionDate DESC");
		} else {
			query = this.getEntityManager().createQuery("SELECT invoiceConcept.calculatedCost, invoiceConcept.calculatedTax, invoiceConcept.lastLiquidationDate, invoiceConcept.id FROM InvoiceConcept invoiceConcept, Invoice invoice WHERE invoice.id = invoiceConcept.invoice.id AND invoiceConcept.concept = ? AND invoiceConcept.invoiceConceptType = ? AND invoiceConcept.invoiceConceptParent = ? ORDER BY invoice.expeditionDate DESC");
			query.setParameter(3, invoiceConcept.getInvoiceConceptParent());
		}
		query.setParameter(1, invoiceConcept.getConcept());
		query.setParameter(2, invoiceConcept.getInvoiceConceptType());
		query.setMaxResults(this.historicConceptNumber);
		@SuppressWarnings("unchecked")
		ArrayList<Object[]> array = (ArrayList<Object[]>) query.getResultList();
		log(Level.INFO, array.toString());
		return array;
	}

	public String formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	/*
	 * Este metodo recibe un invoice y devuelve un string concatenando el
	 * prefijo, numero y sufijo del consecutivo una factura o el consecutivo de
	 * una cuenta de cobro
	 */

	public String invoiceNumber(Invoice invoice) {

		String number = "";
		if (invoice.getNumber() != null && !invoice.getNumber().isEmpty()) {

			number = (invoice.getPrefix() != null && !invoice.getPrefix().isEmpty() ? invoice.getPrefix() + " - " + invoice.getNumber() : invoice.getNumber());
			number += (invoice.getSuffix() != null && !invoice.getSuffix().isEmpty() ? " - " + invoice.getSuffix() : "");
		}

		return number;
	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public void saveInvoiceAprovedList() {

		this.getEntityManager().joinTransaction();
		int facs = 0;

		for (Invoice invoice : this.invoiceList) {
			if (validateInvoiceBeforeApprove(invoice)) {
				for (int i = 0; i < invoice.getInvoiceConcepts().size(); i++) {
					InvoiceConcept invCon = invoice.getInvoiceConcepts().get(i);
					if (invCon.getCalculatedCost() == 0.0)
						invoice.getInvoiceConcepts().remove(i);
				}

				if (invoice.isSelected()) {
					facs++;
					invoice.setApproved(invoice.isSelected());

					try {
						if (invoice.getDocumentType() == Invoice.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE) {
							ConsecutiveAccountsBilling consecutiveAccountsBilling = invoice.getProjectProperty().getAcountsBilling();
							try {

								Boolean flag = consecutiveAccountsBillingList.contains(consecutiveAccountsBilling.getId());
								while (flag) {
									flag = consecutiveAccountsBillingList.contains(consecutiveAccountsBilling.getId());
								}

								log(Level.INFO, Thread.currentThread().getName() + " " + user.getNombre() + " Esta realizando la aprobación");
								consecutiveAccountsBillingList.add(consecutiveAccountsBilling.getId());

								this.getEntityManager().refresh(consecutiveAccountsBilling);
								invoice.setNumber(consecutiveAccountsBilling.getMin() + "");
								invoice.setPrefix(consecutiveAccountsBilling.getPrefix());
								invoice.setSuffix(consecutiveAccountsBilling.getSuffix());
								invoice.setExpeditionDate(this.expeditionInvoiceDate);
								invoice.setExpirationDate(this.expirationInvoiceDate);
								consecutiveAccountsBilling.setMin(consecutiveAccountsBilling.getMin() + 1);
								getEntityManager().persist(consecutiveAccountsBilling);
								consecutiveAccountsBillingList.remove((Integer) consecutiveAccountsBilling.getId());
								log(Level.INFO, Thread.currentThread().getName() + " " + user.getNombre() + " Termina la Aprobación y libera los recursos. ");
							} catch (Exception e) {
								e.printStackTrace();
								consecutiveAccountsBillingList.remove((Integer) consecutiveAccountsBilling.getId());
							}

						} else {
							BillingResolution billingResolution = invoice.getProjectProperty().getBillingResolution();
							try {

								Boolean flag = billingResolutionList.contains(billingResolution.getId());
								while (flag) {
									flag = billingResolutionList.contains(billingResolution.getId());
								}
								log(Level.INFO, Thread.currentThread().getName() + " " + user.getNombre() + " Esta realizando la aprobación");

								billingResolutionList.add(billingResolution.getId());

								this.getEntityManager().refresh(billingResolution);
								invoice.setNumber(billingResolution.getCurrent() + "");
								invoice.setPrefix(billingResolution.getPrefix());
								invoice.setExpeditionDate(this.expeditionInvoiceDate);
								invoice.setExpirationDate(this.expirationInvoiceDate);
								billingResolution.setCurrent(billingResolution.getCurrent() + 1);
								getEntityManager().persist(billingResolution);
								billingResolutionList.remove((Integer) billingResolution.getId());
								log(Level.INFO, Thread.currentThread().getName() + " " + user.getNombre() + " Termina la Aprobación y libera los recursos. ");
							} catch (Exception e) {
								e.printStackTrace();
								billingResolutionList.remove((Integer) billingResolution.getId());
							}

						}

						this.joinTransaction();
						this.getEntityManager().persist(invoice);
						this.getEntityManager().flush();
					} catch (org.hibernate.validator.InvalidStateException exe) {
						consecutiveAccountsBillingList = new ArrayList<Integer>();
						billingResolutionList = new ArrayList<Integer>();
						exe.printStackTrace();
						InvalidValue[] arr = exe.getInvalidValues();
						for (InvalidValue invalidValue : arr) {
							log(Level.INFO, invalidValue.getPropertyName() + " " + invalidValue.getValue());
						}
					}

				}
			}
		}
		boolean siigo = false;
		if (!this.invoiceList.isEmpty()) {
			generateSiigoFile();
			siigo = true;
		}
		this.expirationInvoiceDate = null;
		this.expeditionInvoiceDate = null;
		this.reGetInvoiceList();
		if (siigo) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Contabilidad", "Se generó interfaz contable"));
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Preliquidación", "Se aprobaron exitosamente " + facs + " facturas"));
	}

	public boolean isAllInvoices() {
		return allInvoices;
	}

	public void setAllInvoices(boolean allInvoices) {
		this.allInvoices = allInvoices;
	}

	public void checkAllInvoices(boolean check) {
		for (int i = 0; i < this.invoiceList.size(); i++) {
			if (!this.invoiceList.get(i).isBillingError()) {
				this.invoiceList.get(i).setSelected(check);
			}
		}
	}

	/**
	 * Método que genera el Archivo de Siigo.
	 */
	public void generateSiigoFile() {

		SiigoFunctions sfunction = new SiigoFunctions(this.getEntityManager());
		this.setLinkSiigoFile(sfunction.invoiceSiggo(this.invoiceList, this.selectedProject));

	}

	public List<ProjectProperty> getListNews() {

		if (selectedProject != null && !bandera) {
			listNews = new ArrayList<ProjectProperty>();
			List<Integer> ids = new ArrayList<Integer>();
			bandera = true;
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			Calendar startDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();

			startDate.add(Calendar.MONTH, -1);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			startDate.set(Calendar.HOUR, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			startDate.set(Calendar.MILLISECOND, 0);

			Calendar endAux = Calendar.getInstance();
			endAux = (Calendar) startDate.clone();
			endAux.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			endDate.clear();
			endDate.set(endAux.get(Calendar.YEAR), endAux.get(Calendar.MONTH), endAux.get(Calendar.DAY_OF_MONTH));
			log(Level.INFO, "Fecha inicial de consulta:" + startDate.getTime() + " - Fecha final de consulta: " + endDate);

			@SuppressWarnings("unchecked")
			List<Object[]> revisions = auditReader.createQuery().forRevisionsOfEntity(ProjectProperty.class, false, false).addOrder(AuditEntity.revisionNumber().desc()).add(AuditEntity.property("project").eq(selectedProject)).add(AuditEntity.property("status").eq(ProjectProperty.STATUS_APPROVED)).getResultList();

			Calendar dateAux = Calendar.getInstance();

			calculateProjectFilterDates(selectedProject);
			for (Object[] objects : revisions) {
				if (objects.length == 3 && objects[0] instanceof ProjectProperty && objects[1] instanceof DefaultRevisionEntity && objects[2] instanceof RevisionType && objects[2].toString().equals("MOD")) {

					ProjectProperty aux = (ProjectProperty) objects[0];
					DefaultRevisionEntity revision = (DefaultRevisionEntity) objects[1];
					dateAux.setTime(revision.getRevisionDate());

					if (((dateAux.after(this.filterIniDateProject) || dateAux.equals(this.filterIniDateProject))) && dateAux.before(this.filterEndDateProject)) {
						if (!ids.contains(aux.getId())) {
							listNews.add(aux);
							ids.add(aux.getId());
						}
					} else {
						break;
					}

				}
			}

		}
		return listNews;
	}

	public void setListNews(List<ProjectProperty> listNews) {
		this.listNews = listNews;
	}

	public void changesTermSheet(ProjectProperty termSheet) {
		ProjectProperty currentTermSheet = entityManager.find(termSheet.getClass(), termSheet.getId());
		HashMap<String, ArrayList<String>> changeOrder = new HashMap<String, ArrayList<String>>();
		if (currentTermSheet != null) {
			ArrayList<Object[]> changes = getChanges(currentTermSheet, ProjectProperty.class);
			Iterator<Entry<String, String>> iterator = ProjectProperty.getFieldString().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> e = iterator.next();
				ArrayList<String> changesAux = getChangesXClass(changes, e.getKey());
				if (changesAux != null && changesAux.size() > 0) {
					changeOrder.put(ProjectProperty.getFieldString().get(e.getKey()), changesAux);
				}
			}
		}
		changeNews = changeOrder;
	}

	private ArrayList<String> getChangesXClass(ArrayList<Object[]> changes, String fieldName) {
		ArrayList<String> res = new ArrayList<String>();
		for (Object[] change : changes) {
			if (change.length == 2 && change[0].equals(fieldName)) {
				res.add((String) change[1]);
			}
		}
		return res;
	}

	// lista de clases que se deben comparar
	private static final LinkedList<Class<?>> COMPARATE_TYPES = new LinkedList<Class<?>>();
	{
		COMPARATE_TYPES.add(Address.class);
		COMPARATE_TYPES.add(Integer.class);
		COMPARATE_TYPES.add(String.class);
		COMPARATE_TYPES.add(Date.class);
		COMPARATE_TYPES.add(int.class);
		COMPARATE_TYPES.add(short.class);
		COMPARATE_TYPES.add(boolean.class);
	}

	private ArrayList<Object[]> getChanges(Object object, Class<?> clase) {
		ArrayList<Object[]> result = new ArrayList<Object[]>();

		try {
			Method getId = object.getClass().getMethod("getId", new Class[0]);

			AuditReader auditReader = AuditReaderFactory.get(entityManager);

			@SuppressWarnings("unchecked")
			List<Object[]> revisions = auditReader.createQuery().forRevisionsOfEntity(clase, false, false).addOrder(AuditEntity.revisionNumber().asc()).add(AuditEntity.property("project").eq(selectedProject)).add(AuditEntity.property("status").eq(ProjectProperty.STATUS_APPROVED)).add(AuditEntity.id().eq(getId.invoke(object, new Object[0]))).getResultList();

			// Nombres del los atributos que tiene cambios
			LinkedList<String> comparatesAux = new LinkedList<String>();
			for (Object[] objects : revisions) {
				if (objects.length == 3 && objects[2].toString().equals("MOD")) {
					Object instance = objects[0];
					Field[] fields = instance.getClass().getDeclaredFields();

					for (int i = 0; i < fields.length; i++) {
						Field aux = fields[i];
						String firstChar = aux.getName().substring(0, 1).toUpperCase();
						String nameField = aux.getName().replaceFirst(aux.getName().substring(0, 1), firstChar);
						try {
							Method method = instance.getClass().getMethod("get" + nameField, new Class[0]);
							if (COMPARATE_TYPES.contains(aux.getType())) {
								Object objAux = method.invoke(instance, new Object[0]);
								objAux = changeIfNull(objAux);
								Object objCompare = method.invoke(object, new Object[0]);
								objCompare = changeIfNull(objCompare);

								if (COMPARATE_TYPES.contains(aux.getType()) && !objCompare.toString().equals(objAux.toString())) { // Comparación
									// con
									// tipos.
									// Si
									// son
									// diferentes
									// se
									// toma
									// como
									// un
									// cambio
									Object[] item = { aux.getName(), objAux.toString() };
									result.add(item);
									if (!comparatesAux.contains(aux.getName())) {
										comparatesAux.add(aux.getName());
										Object[] itemAux = { aux.getName(), objCompare.toString() + " (Valor Actual)" };
										result.add(itemAux);
									}
								}

							}
						} catch (NoSuchMethodException e) {
							// En caso
							// de que
							// no
							// encuentré
							// el
							// metodo
							// get de
							// dicho
							// atributo
							// se
							// continua
							// con el
							// siguiente
							// atributo
							continue;
						}
					}
					// se remplaza por el cambio
					// actual para que en la
					// siguiente
					// iteración compara el cambio
					// actual un exactamente el
					// anterior
					object = instance;
				}
			}
		} catch (AuditException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return result;
	}

	private Object changeIfNull(Object objAux) {
		if (objAux == null) {
			return new String("Este campo se encuentra vacio");
		} else if (objAux != null && objAux.toString() == null)
			return new String("Este campo se encuentra vacio");
		return objAux;
	}

	public String getLinkSiigoFile() {
		return linkSiigoFile;
	}

	public void setLinkSiigoFile(String linkSiigoFile) {
		this.linkSiigoFile = linkSiigoFile;
	}

	public int getHistoricConceptNumber() {
		if (this.historicConceptNumber == 0)
			this.historicConceptNumber = 1;
		return historicConceptNumber;
	}

	public void setHistoricConceptNumber(int historicConceptNumber) {
		this.historicConceptNumber = historicConceptNumber;
	}

	public boolean isProjectCloseable() {
		return this.projectCloseable;
	}

	public boolean projectCalculateCloseable() {

		this.getInvoiceList();
		Calendar today = Calendar.getInstance();
		if (this.filterIniDateProject.get(Calendar.YEAR) < today.get(Calendar.YEAR) || this.filterIniDateProject.get(Calendar.MONTH) < today.get(Calendar.MONTH)) {
			return true;
		}
		return false;
	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public void closeProject() {
		log(Level.INFO, "closeProject START");
		log(Level.INFO, "Cerrando periodo de facturación para proyecto: " + this.selectedProject.getId());
		ProjectClosure projectClosure = new ProjectClosure();
		projectClosure.setProject(this.selectedProject);
		projectClosure.setClosureDate(new Date());

		this.getEntityManager().joinTransaction();
		this.getEntityManager().persist(projectClosure);
		this.getEntityManager().flush();

		Query query = this.getEntityManager().createQuery("FROM InvoiceStatus");
		@SuppressWarnings("unchecked")
		List<InvoiceStatus> invoiceStatusList = (List<InvoiceStatus>) query.getResultList();
		InvoiceStatus noBilled = null;
		for (InvoiceStatus invs : invoiceStatusList)
			if (invs.getId() == 5) {
				noBilled = invs;
				break;
			}

		for (Invoice inv : this.invoiceList) {
			if (!inv.isApproved()) {
				inv.setInvoiceStatus(noBilled);
				this.getEntityManager().persist(inv);
			}
		}
		this.getEntityManager().flush();

		this.instance = this.selectedProject;
		this.instance.getProjectClosures().add(projectClosure);
		this.persist();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SeamResourceBundle.getBundle().getString("closeProject"), "Se cerr&oacute; " + this.billingPeriod + " exitosamente"));
		this.destroyModal1();
		log(Level.INFO, "closeProject END");
	}

	public int calculateUnApprovedInvoices() {
		int notApproved = 0;
		this.getInvoiceList();
		if (this.invoiceList == null)
			return 0;
		for (Invoice inv : this.invoiceList) {
			if (!inv.isApproved())
				notApproved++;
		}
		return notApproved;
	}

	public String getBillingPeriod() {
		if (this.billingPeriod == null)
			return "";
		return billingPeriod;
	}

	public void setBillingPeriod(String billingPeriod) {
		this.billingPeriod = billingPeriod;
	}

	public Calendar getFilterIniDateProject() {
		return filterIniDateProject;
	}

	public void setFilterIniDateProject(Calendar filterIniDateProject) {
		this.filterIniDateProject = filterIniDateProject;
	}

	public Calendar getFilterEndDateProject() {
		return filterEndDateProject;
	}

	public void setFilterEndDateProject(Calendar filterEndDateProject) {
		this.filterEndDateProject = filterEndDateProject;
	}

	public Date getExpirationInvoiceDate() {
		return expirationInvoiceDate;
	}

	public void setExpirationInvoiceDate(Date expirationInvoiceDate) {
		this.expirationInvoiceDate = expirationInvoiceDate;
	}

	public Date getExpeditionInvoiceDate() {
		if (this.expeditionInvoiceDate == null)
			expeditionInvoiceDate = new Date();
		return expeditionInvoiceDate;
	}

	public void setExpeditionInvoiceDate(Date expeditionInvoiceDate) {
		this.expeditionInvoiceDate = expeditionInvoiceDate;
	}

	public boolean validateApprovePreliquidationsDate() {
		log(Level.INFO, "INIcio de validacion validateApprovePreliquidationsDate");
		if (this.expeditionInvoiceDate != null && this.expirationInvoiceDate != null) {
			Calendar calTemp1 = Calendar.getInstance();
			calTemp1.setTime(this.expeditionInvoiceDate);
			calTemp1.set(Calendar.HOUR_OF_DAY, 0);
			calTemp1.set(Calendar.MINUTE, 0);
			calTemp1.set(Calendar.SECOND, 0);
			calTemp1.set(Calendar.MILLISECOND, 0);
			Calendar calTemp2 = Calendar.getInstance();
			calTemp2.setTime(this.expirationInvoiceDate);
			calTemp2.set(Calendar.HOUR_OF_DAY, 0);
			calTemp2.set(Calendar.MINUTE, 0);
			calTemp2.set(Calendar.SECOND, 0);
			calTemp2.set(Calendar.MILLISECOND, 0);
			if (calTemp2.before(calTemp1))
				return false;
		}
		return true;
	}

	public List<GroupCache> getGroups() {

		if (groups == null) {
			groups();
		}
		return groups;
	}

	public void setGroups(List<GroupCache> groups) {
		this.groups = groups;
	}

	@SuppressWarnings("unchecked")
	public List<GroupCache> groups() {
		List<GroupCache> groupsPossible = new ArrayList<GroupCache>();
		if (this.selectedProject != null && this.groups == null) {
			List<Invoice> invoices = getCurrentInvoice(this.selectedProject);
			List<Integer> billedIds = new ArrayList<Integer>();
			String period = getPeriod(this.selectedProject);

			// Guarda las facturas que tiene un billed en
			// donde se encuentra el
			// id de un billed (el key del treemap) y una
			// arreglo de facturas
			// (el value en el treemap)
			TreeMap<Integer, List<Invoice>> billedBillings = new TreeMap<Integer, List<Invoice>>();
			/** Agrupación por Tercero **/
			for (Invoice invoice : invoices) {
				if (invoice.getBilled() != null && invoice.getBilled().getId() != 0 && billedIds.contains(invoice.getBilled().getId())) {
					List<Invoice> billedInvoices = billedBillings.get(invoice.getBilled().getId());
					billedInvoices.add(invoice);
					billedBillings.put(invoice.getBilled().getId(), billedInvoices);
				} else if (invoice.getBilled() != null && invoice.getBilled().getId() != 0 && !billedIds.contains(invoice.getBilled().getId())) {
					List<Invoice> billedInvoices = new ArrayList<Invoice>();
					billedInvoices.add(invoice);
					billedBillings.put(invoice.getBilled().getId(), billedInvoices);
					billedIds.add(invoice.getBilled().getId());
				}
			}

			/** Agrupación por Activo **/
			Set<Integer> keys = billedBillings.keySet();
			for (Integer key : keys) {
				List<Invoice> billedInvoices = billedBillings.get(key);
				// Lista que contiene los ids de los
				// activos que ya fueron
				// procesados para el facturado KEY
				List<Integer> realPropertyIdsProcessed = new ArrayList<Integer>();
				// Lista que contiene los ids de los
				// entes facturadores que ya
				// fueron procesados para el facturado
				// KEY
				List<Integer> billerIdsProcessed = new ArrayList<Integer>();
				for (Invoice invoice : billedInvoices) {
					if (invoice.getRealProperty() != null && invoice.getBiller() != null) {
						if (!realPropertyIdsProcessed.contains(invoice.getRealProperty().getId()) || !billerIdsProcessed.contains(invoice.getBiller().getId())) {
							if (!realPropertyIdsProcessed.contains(invoice.getRealProperty().getId())) {
								realPropertyIdsProcessed.add(invoice.getRealProperty().getId());
							}
							if (!billerIdsProcessed.contains(invoice.getBiller().getId())) {
								billerIdsProcessed.add(invoice.getBiller().getId());
							}
							Query query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.biller.id=? AND inv.billed.id=? AND inv.realProperty.id=? AND inv.invoiceStatus.id=? AND inv.documentType = ?");
							query.setParameter(1, invoice.getBiller().getId());
							query.setParameter(2, key);
							query.setParameter(3, invoice.getRealProperty().getId());
							// Asegura
							// que el
							// estado
							// de la
							// hoja de
							// términos
							// sea
							// 1, es
							// decir
							// en
							// estado
							// vigente
							query.setParameter(4, 1);
							query.setParameter(5, Concept.DOCUMENT_TYPE_BILL);
							List<Invoice> invoicesGroup = (List<Invoice>) query.getResultList();
							if (invoicesGroup.size() > 1) {

								GroupCache groupCache = new GroupCache();
								Query cacheQuery = this.getEntityManager().createQuery("FROM GroupCache gc WHERE gc.biller=? AND gc.billed=? AND gc.realProperty=?");
								cacheQuery.setParameter(1, invoice.getBiller().getId());
								cacheQuery.setParameter(2, key);
								cacheQuery.setParameter(3, invoice.getRealProperty().getId());
								List<GroupCache> cache = (List<GroupCache>) cacheQuery.getResultList();

								if (cache.size() > 0) {
									groupCache = cache.get(0);
									groupCache.setBilled(key);
									groupCache.setBiller(invoice.getBiller().getId());
									groupCache.setBillingCount(invoicesGroup.size());

									if (!groupCache.getBillingPeriod().equals(period)) {
										groupCache.setGroupedIntoPeriod(false);
										groupCache.setBillingPeriod(period);
									}

									groupCache.setRealProperty(invoice.getRealProperty().getId());
								} else {
									groupCache.setBilled(key);
									groupCache.setBiller(invoice.getBiller().getId());
									groupCache.setBillingCount(invoicesGroup.size());
									groupCache.setBillingPeriod(period);
									groupCache.setGrouped(false);
									groupCache.setRealProperty(invoice.getRealProperty().getId());
								}
								groupsPossible.add(groupCache);
							}
						}
					}
				}
			}
			groups = groupsPossible;
			String query = "FROM GroupCache gc WHERE gc.billingPeriod=?";
			String next = " AND gc.id NOT IN (";
			boolean in = false;
			List<Integer> params = new ArrayList<Integer>();
			for (GroupCache group : groups) {
				if (group.getId() != 0) {
					in = true;
					query += next;
					next = null;
					next = ",";
					query += group.getId();
					if (!params.contains(group.getId())) {
						params.add(group.getId());
					}
				}
			}

			if (in) {
				query += ")";
			}

			Query cacheQuery = this.getEntityManager().createQuery(query);
			cacheQuery.setParameter(1, period);

			groups.addAll((List<GroupCache>) cacheQuery.getResultList());

		}
		return groupsPossible;
	}

	/**
	 * Método que retorna el periodo actual de facturación
	 * 
	 * @return period
	 */
	private String getPeriod(Project project) {
		this.calculateProjectFilterDates(project);
		String[] months = new String[12];
		months[0] = "ENERO";
		months[1] = "FEBRERO";
		months[2] = "MARZO";
		months[3] = "ABRIL";
		months[4] = "MAYO";
		months[5] = "JUNIO";
		months[6] = "JULIO";
		months[7] = "AGOSTO";
		months[8] = "SEPTIEMBRE";
		months[9] = "OCTUBRE";
		months[10] = "NOVIEMBRE";
		months[11] = "DICIEMBRE";

		return months[this.filterIniDateProject.get(Calendar.MONTH)];
	}

	/**
	 * Método que retorna las facturas vigentes para el periodo de facturación
	 * actual
	 * 
	 * @return Facturas vigentes en el periodo actual de facturación
	 */
	@SuppressWarnings("unchecked")
	public List<Invoice> getCurrentInvoice(Project project) {
		this.calculateProjectFilterDates(project);
		Query query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.projectProperty.project = ? AND inv.creationDate >= ? AND inv.creationDate < ?  AND inv.approved = ? AND inv.invoiceStatus.id=?");
		query.setParameter(1, project);
		query.setParameter(2, this.filterIniDateProject.getTime());
		query.setParameter(3, this.filterEndDateProject.getTime());
		query.setParameter(4, false);
		query.setParameter(5, 1);

		return (List<Invoice>) query.getResultList();
	}

	public String billedNameGroup(GroupCache groupCache) {
		return this.getEntityManager().find(BusinessEntity.class, groupCache.getBilled()).getNameBusinessEntity();
	}

	public String billerNameGroup(GroupCache groupCache) {
		return this.getEntityManager().find(BusinessEntity.class, groupCache.getBiller()).getNameBusinessEntity();
	}

	public String billedIdNumberGroup(GroupCache groupCache) {
		return this.getEntityManager().find(BusinessEntity.class, groupCache.getBilled()).getIdNumber();
	}

	public String billerIdNumberGroup(GroupCache groupCache) {
		return this.getEntityManager().find(BusinessEntity.class, groupCache.getBiller()).getIdNumber();
	}

	public String realPropertyNameGroup(GroupCache groupCache) {
		return this.getEntityManager().find(RealProperty.class, groupCache.getRealProperty()).getNameProperty();
	}

	public Set<Address> billedAddressGroup(GroupCache groupCache) {
		return this.getEntityManager().find(BusinessEntity.class, groupCache.getBilled()).getAddresses();
	}

	public void groupAction() {
		grouping();
		desgrouping();
		cleanGroupModalPanel();
	}

	@Transactional
	private void grouping() {
		EntityManager entityManager = getEntityManager();
		if (this.groups != null) {
			for (GroupCache group : this.groups) {
				if (group.isGrouped() && !group.isGroupedIntoPeriod()) {
					Query query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.biller.id=? AND inv.billed.id=? AND inv.realProperty.id=? AND inv.invoiceStatus.id=? AND inv.documentType = ? AND inv.approved = ? AND inv.creationDate BETWEEN ? and ?");
					query.setParameter(1, group.getBiller());
					query.setParameter(2, group.getBilled());
					query.setParameter(3, group.getRealProperty());
					// Asegura que el estado de la
					// hoja de términos sea 1, es
					// decir en estado vigente
					query.setParameter(4, 1);
					query.setParameter(5, Concept.DOCUMENT_TYPE_BILL);
					query.setParameter(6, false);
					query.setParameter(7, this.filterIniDateProject.getTime());
					query.setParameter(8, this.filterEndDateProject.getTime());
					@SuppressWarnings("unchecked")
					List<Invoice> invoicesGroup = (List<Invoice>) query.getResultList();
					System.out.println("Se agruparan " + invoicesGroup.size() + "Invoice Para el facturador: " + group.getBiller() + " y el facturado: " + group.getBilled());
					Invoice newInvoice = null;
					List<InvoiceConcept> invoiceConceptsAll = new ArrayList<InvoiceConcept>();
					for (Invoice invoice : invoicesGroup) {
						List<InvoiceConcept> invoiceConcepts = invoice.getInvoiceConcepts();
						invoiceConceptsAll.addAll(invoiceConcepts);
						if (newInvoice == null) {
							System.out.println("Se crea un nuevo invoice Para realizar la agrupación");
							newInvoice = new Invoice();
							newInvoice.setInvoiceStatus(invoice.getInvoiceStatus());
							newInvoice.setProjectProperty(invoice.getProjectProperty());
							newInvoice.setExpeditionDate(invoice.getExpeditionDate());
							newInvoice.setExpirationDate(invoice.getExpirationDate());
							newInvoice.setBiller(invoice.getBiller());
							newInvoice.setIdNumberBiller(invoice.getBiller().getIdNumber());
							newInvoice.setNameBiller(invoice.getBiller().getNameBusinessEntity());
							newInvoice.setAddressBiller(invoice.getAddressBiller());
							newInvoice.setPhoneBiller(invoice.getPhoneBiller());
							newInvoice.setCityBiller(invoice.getCityBiller());
							newInvoice.setBilled(invoice.getBilled());
							newInvoice.setIdNumberBilled(invoice.getBilled().getIdNumber());
							newInvoice.setNameBilled(invoice.getBilled().getNameBusinessEntity());
							newInvoice.setAddressBilled(group.getAddress().toString());
							newInvoice.setPhoneBilled(invoice.getPhoneBilled());
							newInvoice.setCityBilled(group.getAddress().getCity().getName());
							// -1
							// indica
							// que la
							// factura
							// esta
							// agrupada
							// por
							// tercero
							newInvoice.setGroupNumber(-1);
							newInvoice.setDocumentType(Concept.DOCUMENT_TYPE_BILL);
							newInvoice.setRealProperty(invoice.getRealProperty());
							newInvoice.setApproved(false);
							newInvoice.setCreationDate(invoice.getCreationDate());
						}
						entityManager.remove(invoice);
					}
					if (newInvoice != null) {
						System.out.println("Se adicionan invoiceConcept a la factura agrupada por tercero.");
						entityManager.persist(newInvoice);
						for (InvoiceConcept invoiceConcept : invoiceConceptsAll) {
							invoiceConcept.setInvoice(newInvoice);
							entityManager.persist(invoiceConcept);
						}
						newInvoice.setInvoiceConcepts(invoiceConceptsAll);
					}
					group.setGroupedIntoPeriod(group.isGrouped());
					entityManager.persist(group);
					entityManager.flush();
				}
			}

		}

	}

	@Transactional
	public void desgrouping() {
		EntityManager entityManager = getEntityManager();
		if (this.groups != null) {
			for (GroupCache group : this.groups) {
				if (!group.isGrouped() && group.isGroupedIntoPeriod()) {
					Query query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.biller.id=? " + "AND inv.billed.id=? AND inv.realProperty.id=? " + "AND inv.invoiceStatus.id=? AND inv.documentType = ?" + "AND inv.groupNumber = ?");

					query.setParameter(1, group.getBiller());
					query.setParameter(2, group.getBilled());
					query.setParameter(3, group.getRealProperty());
					// Asegura que el estado de la
					// hoja de términos sea 1, es
					// decir en estado vigente
					query.setParameter(4, 1);
					query.setParameter(5, Concept.DOCUMENT_TYPE_BILL);
					// -1 indica que la factura
					// esta agrupada por tercero
					query.setParameter(6, -1);
					@SuppressWarnings("unchecked")
					List<Invoice> invoicesGroup = (List<Invoice>) query.getResultList();

					List<InvoiceConcept> invoiceConceptsAll = new ArrayList<InvoiceConcept>();
					for (Invoice invoice : invoicesGroup) {
						invoiceConceptsAll.addAll(invoice.getInvoiceConcepts());
					}

					for (InvoiceConcept invoiceConcept : invoiceConceptsAll) {
						this.calculateProjectFilterDates(invoiceConcept.getConcept().getProjectProperty().getProject());
						Query q = this.entityManager.createQuery("FROM InvoiceConcept invCon WHERE invCon.concept.projectProperty = ? " + "AND invCon.invoice.groupNumber = ? AND invCon.invoice.documentType = ? " + "AND invCon.invoice.creationDate >= ? AND invCon.invoice.creationDate <= ?  " + "AND invCon.invoice.approved = ? AND invCon.invoice.invoiceStatus.id=?");
						q.setParameter(1, invoiceConcept.getConcept().getProjectProperty());
						q.setParameter(2, invoiceConcept.getConcept().getGroupNumber());
						q.setParameter(3, invoiceConcept.getConcept().getDocumentType());
						q.setParameter(4, this.filterIniDateProject.getTime());
						q.setParameter(5, this.filterEndDateProject.getTime());
						q.setParameter(6, false);
						// Asegura que el
						// estado de la hoja
						// de términos sea
						// 1,
						// es decir en
						// estado vigente
						q.setParameter(7, 1);
						q.setMaxResults(1);
						@SuppressWarnings("unchecked")
						List<InvoiceConcept> invoiceConceptList = (List<InvoiceConcept>) q.getResultList();
						if (!invoiceConceptList.isEmpty()) {
							invoiceConcept.setInvoice(invoiceConceptList.get(0).getInvoice());
						} else {
							Invoice newInvoice = new Invoice();
							ProjectProperty termSheet = invoiceConcept.getConcept().getProjectProperty();
							InvoiceStatus ins = null;
							{
								ins = new InvoiceStatus();
								ins.setId(1);
							}

							newInvoice.setInvoiceStatus(ins);
							newInvoice.setProjectProperty(invoiceConcept.getConcept().getProjectProperty());
							newInvoice.setExpeditionDate(termSheet.getSubscriptionDate());
							newInvoice.setExpirationDate(termSheet.getExpirationDate());
							newInvoice.setBiller(termSheet.getBiller());
							newInvoice.setIdNumberBiller(termSheet.getBiller().getIdNumber());
							newInvoice.setNameBiller(termSheet.getBiller().getNameBusinessEntity());
							newInvoice.setAddressBiller(termSheet.getBillerAddress().toString());
							newInvoice.setPhoneBiller(termSheet.getPhoneNumber().getNumber());
							newInvoice.setCityBiller(termSheet.getBillerAddress().getCity().getName());
							newInvoice.setBilled(termSheet.getBilled());
							newInvoice.setIdNumberBilled(termSheet.getBilled().getIdNumber());
							newInvoice.setNameBilled(termSheet.getBilled().getNameBusinessEntity());
							newInvoice.setAddressBilled(termSheet.getBilledAddress().toString());
							newInvoice.setPhoneBilled(termSheet.getPhoneNumber().getNumber());
							newInvoice.setCityBilled(termSheet.getBilledAddress().getCity().getName());
							// -1
							// indica
							// que la
							// factura
							// esta
							// agrupada
							// por
							// tercero
							newInvoice.setGroupNumber(invoiceConcept.getConcept().getGroupNumber());
							newInvoice.setDocumentType(invoiceConcept.getConcept().getDocumentType());
							newInvoice.setRealProperty(termSheet.getRealProperty());
							newInvoice.setApproved(false);
							newInvoice.setCreationDate(invoiceConcept.getInvoice().getCreationDate());
							entityManager.persist(newInvoice);

							invoiceConcept.setInvoice(newInvoice);
							entityManager.persist(newInvoice);
						}
					}

					for (Invoice invoice : invoicesGroup) {
						entityManager.remove(invoice);
					}

					group.setGroupedIntoPeriod(group.isGrouped());
					entityManager.persist(group);
					entityManager.flush();
				}
			}
		}

	}

	public void cleanGroupModalPanel() {
		this.groups = null;
		this.selectedProject = null;
		destroyModal1();
	}

	public HtmlExtendedDataTable getTableGroupsBind() {
		return tableGroupsBind;
	}

	public void setTableGroupsBind(HtmlExtendedDataTable tableGroupsBind) {
		this.tableGroupsBind = tableGroupsBind;
	}

	private String linkPDF;

	@RequestParameter
	public Integer idProject;

	public String createBillingResolutionPDF2() {
		log(Level.INFO, "createBillingResolutionPDF2 start");
		Project proj = getEntityManager().find(Project.class, idProject);
		if (proj != null) {
			String tempLog;
			this.setSelectedProject(proj);
			if (!this.openModalPanelInvoicesActionApprove(proj)) {
				tempLog = "No se generó PDF, es probable que el proyecto no tenga facturas aprobadas";
				log(Level.INFO, tempLog);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("PDF", tempLog));
				return "";
			}
			String tempRESUTL = this.createPDF();
			tempLog = "El PDF de facturas aprobadas se generó exitosamente";
			log(Level.INFO, tempLog);
			return tempRESUTL;
		} else {
			String tempLog = "No se genero PDF, el proyecto está indefinido";
			log(Level.INFO, tempLog);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("PDF", tempLog));
			return "";
		}
	}

	/**
	 * Esta función se encarga de crear el nombre del pdf y verificar si el pdf
	 * existe. Si la factura esta NO APROBADO lo creara asi exista pero si esta
	 * APROBADO no lo creara si existe.
	 * 
	 * @param bill
	 *            es el invoice al cual se le generara el pdf
	 * @return retorna true si se puede crear el pdf, de lo contario false
	 */
	public boolean namedPDF(Invoice bill) {

		SimpleDateFormat dateFormatPDF = new SimpleDateFormat("dd-MM-yyyy");
		// --------------------------
		Query q = this.entityManager.createNativeQuery("select * from system_configuration where name = ?", SystemConfiguration.class);
		q.setParameter(1, SystemConfiguration.Carpeta_PDF);
		q.setMaxResults(1);
		systemConfiguration = (SystemConfiguration) q.getSingleResult();

		OrigenCarpeta = systemConfiguration.getValue() + "/" + this.selectedProject.getId() + "_" + this.selectedProject.getNameProject();
		File directorio = new File(OrigenCarpeta);
		directorio.mkdir();
		// --------------------------
		// List<String> listpdf = new ArrayList<String>();

		String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
		path = path.substring(0, path.lastIndexOf("/")) + "/";
		// --------------------------
		filePDF1 = "";
		filePDF1 += this.selectedProject.getProjectPrefix();
		filePDF1 += bill.getDocumentType() == Concept.DOCUMENT_TYPE_BILL ? "-F-" : "-L-";
		filePDF1 += bill.getNumber() == null ? "NO_APROBADA_" + bill.getId() : bill.getNumber();
		filePDF1 += "_" + dateFormatPDF.format(bill.getExpeditionDate()) + (bill.getDocumentType() == Invoice.DOCUMENT_TYPE_BILL ? bill.getNumber() + "_Origins" : "") + ".pdf";
		// --------------------------
		listpdf.add(OrigenCarpeta + "/" + filePDF1);

		String verifyExistens = bill.getDocumentType() == Invoice.DOCUMENT_TYPE_BILL ? filePDF1.replace("Origins", "Original") : filePDF1;
		if (!filePDF1.contains("NO_APROBADA_") && (new File(OrigenCarpeta + "/" + verifyExistens).exists())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Este método se encarga de preguntar si el invoice es de tipo factura o
	 * cuenta de cobro y dependiendo del resultado llama al método que crea cada
	 * uno de estos
	 * 
	 * @return
	 */
	public String createPDF() {
		try {
			listpdf = new ArrayList<String>();
			if (this.selectedProject != null && this.invoiceList != null && !this.invoiceList.isEmpty()) {
				for (Invoice bill : this.invoiceList) {
					subtotal = 0.0;
					iva = 0.0;
					total = 0.0;
					totalStamptax = 0.0;
					Document document = new Document(PageSize.LETTER);
					String consecutive = "";

					if (namedPDF(bill)) {
						if (bill.getDocumentType() == Invoice.DOCUMENT_TYPE_BILL) {
							this.createBillPDF(bill, document, consecutive);
							log(Level.INFO, "________________________________ FACTURA " + bill.getNumber());

						} else {
							this.createAccountReceivable(bill, document, consecutive);
							log(Level.INFO, "________________________________ CUENTA DE COBRO " + bill.getNumber());

						}
					} else {
						continue;
					}
					document.close();
					writer.close();
				}
				return ConcatenatePdf(listpdf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Este método se encarga de crear el pdf de tipo cuenta de cobro y
	 * adicionar la infomación exclusiva para la cuenta de cobro
	 * 
	 * @param bill
	 *            es el invoice a generar el pdf
	 * @param document
	 *            es el pdf el cual se editara
	 * @param consecutive
	 *            es un numero que identifica el pdf
	 * @return
	 */
	public String createAccountReceivable(Invoice bill, Document document, String consecutive) {

		try {
			if (bill.getNumber() != null && bill.getDocumentType() == Invoice.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE) {
				consecutive = "CUENTA DE COBRO No " + this.invoiceNumber(bill);
			} else if (bill.getNumber() == null && bill.getDocumentType() == Invoice.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE) {
				consecutive = "CUENTA DE COBRO No ";
			}
			// generar primera parte del pdf
			List<String> lineSize = new ArrayList<String>();
			lineSize.add("\n\n");
			lineSize.add("\n\n\n");
			document = createFragmentInformationClient(bill, document, consecutive, lineSize, new ArrayList<PdfPCell>());
			if (bill.getProjectProperty().getAcountsBilling() != null && bill.getDocumentType() == Concept.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE)
				document.add(new Paragraph(""));
			else if (bill.getProjectProperty().getAcountsBilling() == null && bill.getDocumentType() == Concept.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE)
				document.add(new Paragraph(""));

			// genera segunda parte del pdf
			List<Float> sizeTable = new ArrayList<Float>();
			sizeTable.add(HEIGHT_ONEPAGE_CONCEPT_TABLE);
			sizeTable.add(15.0f);
			document = createFragmentConcepts(bill, "", document, sizeTable);

			// crear tabla total
			Phrase phrase19 = new Phrase(20, " TOTAL", this.selectedFont(FONT2));
			// [TOTAL]
			Phrase phrase20 = new Phrase(20, "" + formatter.format(total), this.selectedFont(FONT4));

			PdfPCell cell24 = new PdfPCell(new Paragraph(phrase19));
			cell24.setBorder(Rectangle.RIGHT);

			PdfPCell cell25 = new PdfPCell(new Paragraph(phrase20));
			cell25.setBorder(Rectangle.NO_BORDER);
			cell25.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

			List<PdfPCell> cellList = new ArrayList<PdfPCell>();
			cellList.add(cell24);
			cellList.add(cell25);

			// genera tercera parte del pdf
			document = this.createFragmentFinal(bill, document, cellList, "\n\n\n\n\n");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Este método crea el pdf de tipo factura llamando a los métodos necesarios
	 * y adicionando la infomación exclusiva para la factura
	 * 
	 * @param bill
	 *            es el invoice a generar el pdf
	 * @param document
	 *            es el pdf el cual se editara
	 * @param consecutive
	 *            es un numero que identifica el pdf
	 * @return vacio
	 */
	@SuppressWarnings("static-access")
	public String createBillPDF(Invoice bill, Document document, String consecutive) {
		try {
			// pregunta si el consecutivo es null para asignarlo o no al pdf
			if (bill.getNumber() != null) {
				consecutive = "FACTURA DE VENTA No " + this.invoiceNumber(bill);
			} else if (bill.getNumber() == null) {
				consecutive = "FACTURA DE VENTA No   ";
			}
			Format_number format = new Format_number();
			// crea la primera parte del pdf
			List<String> lineSize = new ArrayList<String>();
			lineSize.add("\n");
			lineSize.add("\n");

			/*
			 * 2012-11-30 @dvaldivieso - Si la hoja de terminos de la factura es
			 * por mandato se pone la ciudad del facturador. Es decir la ciudad
			 * del tercero que está asociada a la linea de negocio del proyecto
			 * de la hoja de terminos. Si no es por mandato, también se pone la
			 * ciudad del facturador que en este caso sería la ciudad del
			 * tercero del proyecto de la hoja de terminos
			 */
			PdfPCell cellExpeditionPlace;
			if (bill.getProjectProperty().isMandate()) {

				cellExpeditionPlace = new PdfPCell(new Paragraph("Lugar de Creación: " + " " + bill.getProjectProperty().getProject().getBusinessLine().getBusinessEntity().getCity().getName(), this.selectedFont(FONT6)));// ciudad
			} else {
				cellExpeditionPlace = new PdfPCell(new Paragraph("Lugar de Creación: " + " " + bill.getProjectProperty().getProject().getBusinessEntity().getCity().getName(), this.selectedFont(FONT6)));// ciudad

			}
			// PdfPCell cellExpeditionPlace = new PdfPCell(new
			// Paragraph("Lugar de creación", this.selectedFont(FONT6))); //
			// codigo a reemplazr por ciudad facturador
			cellExpeditionPlace.setBorder(Rectangle.NO_BORDER);

			PdfPCell cellNul = new PdfPCell(new Paragraph("", this.selectedFont(FONT6)));
			cellNul.setBorder(Rectangle.NO_BORDER);

			List<PdfPCell> listCells = new ArrayList<PdfPCell>();
			listCells.add(cellExpeditionPlace);
			listCells.add(cellNul);

			document = createFragmentInformationClient(bill, document, consecutive, lineSize, listCells);
			String resolution = "IMPRESO  POR  " + bill.getBiller().getNameBusinessEntity().toUpperCase() + "  Nit  No.  " + format.FormatToString4(Double.parseDouble(bill.getBiller().getIdNumber())) + " - " + bill.getProjectProperty().getBillingResolution().getBusinessEntity().getVerificationNumber() + "  MEDIANTE  RESOLUCIÓN  DE  FACTURACIÓN  AUTORIZADA  DIAN  No.  " + bill.getProjectProperty().getBillingResolution().getDian_Number() + " \nDEL  " + bill.getProjectProperty().getBillingResolution().getResolutionDate() + "  DEL RANGO " + bill.getProjectProperty().getBillingResolution().getPrefix() + "-" + bill.getProjectProperty().getBillingResolution().getMin() + "   AL   " + bill.getProjectProperty().getBillingResolution().getPrefix() + "-"
					+ bill.getProjectProperty().getBillingResolution().getMax();

			// crea la segunda parte del pdf
			List<Float> sizeTable = new ArrayList<Float>();
			sizeTable.add(130f);
			sizeTable.add(5.0f);
			document = createFragmentConcepts(bill, resolution, document, sizeTable);

			// [VALOR BRUTO]
			Phrase phrase15 = new Phrase(20, "       SUBTOTAL \n ", this.selectedFont(FONT2));
			Phrase phrase16 = new Phrase(20, formatter.format(subtotal), this.selectedFont(FONT4));

			// [VALOR IVA]
			Phrase phrase17 = new Phrase(20, "       IVA \n ", this.selectedFont(FONT2));
			Phrase phrase18 = new Phrase(20, formatter.format(iva), this.selectedFont(FONT4));

			// [TOTAL]
			Phrase phrase19 = new Phrase(20, "       TOTAL  ", this.selectedFont(FONT2));
			Phrase phrase20 = new Phrase(20, formatter.format(total), this.selectedFont(FONT4));

			PdfPCell cell20 = new PdfPCell(new Paragraph(phrase15));
			cell20.setPaddingRight(0);

			PdfPCell cell21 = new PdfPCell(new Paragraph(phrase16));
			cell21.setBorder(Rectangle.BOTTOM);
			cell21.setPaddingRight(15);
			cell21.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

			PdfPCell cell22 = new PdfPCell(new Paragraph(phrase17));
			cell22.setBorder(Rectangle.RIGHT + Rectangle.BOTTOM);

			PdfPCell cell23 = new PdfPCell(new Paragraph(phrase18));
			cell23.setPaddingRight(15);
			cell23.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			cell23.setBorder(Rectangle.BOTTOM);

			PdfPCell cell24 = new PdfPCell(new Paragraph(phrase19));
			cell24.setBorder(Rectangle.RIGHT);

			PdfPCell cell25 = new PdfPCell(new Paragraph(phrase20));
			cell25.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			cell25.setBorder(Rectangle.NO_BORDER);
			cell25.setPaddingRight(15);
			List<PdfPCell> cellList = new ArrayList<PdfPCell>();
			cellList.add(cell20);
			cellList.add(cell21);
			cellList.add(cell22);
			cellList.add(cell23);
			cellList.add(cell24);
			cellList.add(cell25);

			// crea la tercera y ultima parte del pdf
			document = createFragmentFinal(bill, document, cellList, "");

			// agregar tabla firma autorizada
			PdfPTable tableFirm = new PdfPTable(1);
			tableFirm.setWidthPercentage(100);
			tableFirm.setHorizontalAlignment(Element.ALIGN_TOP);

			PdfPCell cellFirm = new PdfPCell(new Phrase(20, "FIRMA AUTORIZADA DEL EMISOR", this.selectedFont(FONT3)));
			cellFirm.setFixedHeight(20);
			cellFirm.setGrayFill(0.6f);
			cellFirm.setVerticalAlignment(Element.ALIGN_MIDDLE);
			tableFirm.addCell(cellFirm);

			PdfPCell cellEmtpy = new PdfPCell(new Phrase());
			cellEmtpy.setFixedHeight(20);
			cellEmtpy.setPadding(6);
			tableFirm.addCell(cellEmtpy);

			// tabla observaciones 2
			PdfPTable table5 = new PdfPTable(1);
			table5.setWidthPercentage(100);
			table5.setHorizontalAlignment(Element.ALIGN_CENTER);

			/*
			 * 2012-11-23 @jrojas - Cuando la hoja de termino esta configurada
			 * por mandato, se debe poner la observación2 del mandante de la
			 * hoja de terminos. Si la hoja de terminos no es por mandato se
			 * debe poner la observación2 de la entidad facturadora. Es decir
			 * siempre se debe usar la observación2 del tercero asociado al
			 * proyecto de la hoja de terminos. Ver javaDoc
			 * ProjectProperty.isMandate()
			 */
			PdfPCell cellPlace = new PdfPCell(new Paragraph(bill.getProjectProperty().getProject().getBusinessEntity().getBillerObservation2(), this.selectedFont(FONT6)));

			cellPlace.setBorder(Rectangle.NO_BORDER);
			table5.addCell(cellPlace);
			// se agrega la tabla firma y observaciones al documento
			document.add(new Paragraph(" ", this.selectedFont(1)));
			document.add(tableFirm);
			document.add(new Paragraph(" ", this.selectedFont(1)));
			document.add(table5);
			document.add(new Paragraph(" ", this.selectedFont(1)));

			// tabla recibido/aceptado
			PdfPTable tableInformacion = new PdfPTable(3);
			tableInformacion.setWidths(new float[] { 50, 1, 50 });
			tableInformacion.getDefaultCell().setPadding(0);
			tableInformacion.setWidthPercentage(100);
			tableInformacion.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableInformacion.setSpacingAfter(30);

			// tabla recibido por
			PdfPTable tableRecibido = new PdfPTable(new float[] { 2, 10 });
			tableRecibido.getDefaultCell().setPaddingRight(0);

			// tabla aceptado por
			PdfPTable tableAceptado = new PdfPTable(new float[] { 2, 10 });
			tableAceptado.getDefaultCell().setPaddingRight(0);

			// creacion de celdas para la tabla recibido/aceptado

			String underline = "_______________________________________________";

			PdfPCell cellRecibido = new PdfPCell(new Paragraph("Recibido Por:", selectedFont(FONT4)));
			cellRecibido.setBorder(Rectangle.NO_BORDER);
			cellRecibido.setColspan(2);
			cellRecibido.setPaddingRight(0);
			cellRecibido.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

			PdfPCell cellAceptado = new PdfPCell(new Paragraph("Aceptado Por:", selectedFont(FONT4)));
			cellAceptado.setBorder(Rectangle.NO_BORDER);
			cellAceptado.setColspan(2);
			cellAceptado.setPaddingRight(0);
			cellAceptado.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

			PdfPCell cellVacia = new PdfPCell(new Paragraph("", selectedFont(FONT4)));
			cellVacia.setBorder(Rectangle.NO_BORDER);

			PdfPCell cellNombre = new PdfPCell(new Paragraph("Nombre ", selectedFont(FONT4)));
			cellNombre.setPaddingRight(0);
			cellNombre.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cellNombre.setBorder(Rectangle.NO_BORDER);

			PdfPCell cellUnderline = new PdfPCell(new Paragraph(underline, selectedFont(FONT4)));
			cellUnderline.setBorder(Rectangle.NO_BORDER);
			cellUnderline.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

			PdfPCell cellCC = new PdfPCell(new Paragraph("C.C. ", selectedFont(FONT4)));
			cellCC.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cellCC.setBorder(Rectangle.NO_BORDER);
			cellCC.setPaddingRight(0);

			PdfPCell cellFirma = new PdfPCell(new Paragraph("Firma ", selectedFont(FONT4)));
			cellFirma.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cellFirma.setBorder(Rectangle.NO_BORDER);
			cellFirma.setPaddingRight(0);

			PdfPCell cellFecha = new PdfPCell(new Paragraph("Fecha ", selectedFont(FONT4)));
			cellFecha.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cellFecha.setBorder(Rectangle.NO_BORDER);
			cellFecha.setPaddingRight(0);

			// adicion de celdas a la tabla recibido
			tableRecibido.addCell(cellRecibido);
			tableRecibido.addCell(cellNombre);
			tableRecibido.addCell(cellUnderline);
			tableRecibido.addCell(cellCC);
			tableRecibido.addCell(cellUnderline);
			tableRecibido.addCell(cellFirma);
			tableRecibido.addCell(cellUnderline);
			tableRecibido.addCell(cellFecha);
			tableRecibido.addCell(cellVacia);

			// adicion de celdas a la tabla aceptado
			tableAceptado.addCell(cellAceptado);
			tableAceptado.addCell(cellNombre);
			tableAceptado.addCell(cellUnderline);
			tableAceptado.addCell(cellCC);
			tableAceptado.addCell(cellUnderline);
			tableAceptado.addCell(cellFirma);
			tableAceptado.addCell(cellUnderline);
			tableAceptado.addCell(cellFecha);
			tableAceptado.addCell(cellVacia);

			// adicion de tablas recibido y aceptado a la tabla
			// recibidos/aceptado
			tableInformacion.addCell(tableRecibido);
			tableInformacion.addCell(cellVacia);
			tableInformacion.addCell(tableAceptado);

			document.add(tableInformacion);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Este método se encarga de crear la primara parte del pdf el cual incluye
	 * la información que se encuentra debajo de los logos superiores y la
	 * informacion del cliente como nombre, nit etc.
	 * 
	 * @param bill
	 *            es el InvoiceConcept
	 * @param document
	 *            es el pdf con la tablas que adicionadas
	 * @param consecutive
	 *            es el numero consecutivo para el pdf
	 * @return
	 */
	public Document createFragmentInformationClient(Invoice bill, Document document, String consecutive, List<String> lineSize, List<PdfPCell> listExpeditionPlace) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(OrigenCarpeta + "/" + filePDF1));
			// ------------------------------------------------------------------------------------------
			document.setMargins(40, 46, 120, 60);
			HeaderFooter event = new HeaderFooter(bill);
			writer.setPageEvent(event);
			document.open();
			// for (Invoice bill :
			// invoiceList) {

			document.newPage();

			// Introduccion y
			// posicionamiento del logo 1
			// y logo 2

			document.add(new Paragraph(lineSize.get(0), selectedFont(FONT5)));

			/**
			 * Tabla de la información debajo de los logos de la factura
			 **/
			PdfPTable table6 = new PdfPTable(2);
			table6.setTotalWidth(new float[] { 230f, 300f });
			table6.setWidthPercentage(100);
			table6.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell cell1;

			/*
			 * if (bill.getProjectProperty().isMandate() == true) {
			 * 
			 * BusinessEntity mandante =
			 * bill.getProjectProperty().getProject().getBusinessEntity(); cell1
			 * = new PdfPCell(new Paragraph(mandante.getNameBusinessEntity() +
			 * "\n" + (mandante.getAddresses() != null &&
			 * !mandante.getAddresses().isEmpty() ?
			 * mandante.getAddresses().iterator().next().toString() : "") +
			 * (mandante.getPhoneNumbers() != null &&
			 * !mandante.getPhoneNumbers().isEmpty() ? " TEL. " +
			 * mandante.getPhoneNumbers().iterator().next().toString() : "") +
			 * "\n" + (mandante.getCity() != null ? mandante.getCity().getName()
			 * : "") + " - " + (mandante.getAddresses() != null &&
			 * !mandante.getAddresses().isEmpty() ?
			 * mandante.getAddresses().iterator
			 * ().next().getCity().getRegion().getCountry().getName() : "") +
			 * "\n" + (mandante.getIdType() == 31 ? ("NIT." +
			 * mandante.getIdNumber() + " - " +
			 * mandante.getVerificationNumber()) : "C.C." +
			 * mandante.getIdNumber()), this.selectedFont(FONT1))); } else {
			 * cell1 = new PdfPCell(new Paragraph(bill.getNameBiller() + "\n" +
			 * bill.getAddressBiller() + " TEL. " +
			 * bill.getProjectProperty().getPhoneNumberByPhoneBiller
			 * ().getNumber() + "\n" + bill.getCityBiller() + " - " +
			 * bill.getProjectProperty
			 * ().getBillerAddress().getCity().getRegion()
			 * .getCountry().getName() + "\n" + (bill.getBiller().getIdType() ==
			 * 31 ? ("NIT." + bill.getBiller().getIdNumber() + " - " +
			 * bill.getBiller().getVerificationNumber()) : "C.C." +
			 * bill.getBiller().getIdNumber()), this.selectedFont(FONT1))); }
			 */

			/*
			 * El siguiente bloque de código es para mostrar la información que
			 * sale debajo del logo del lado izquierdo. Cuando la hoja de terminos
			 * es por Mandato, se debe traer la info del Mandante y cuando la
			 * hoja de terminos NO es por mandato se trae la info del
			 * facturador. Ver JAVADOC de ProjectProperty.isMandate()
			 */
			ProjectProperty pp = bill.getProjectProperty();
			StringBuilder logoInfo = new StringBuilder("");
			if (pp.getProject() != null && pp.getProject().getBusinessEntity() != null) {
				logoInfo.append(pp.getProject().getBusinessEntity().getNameBusinessEntity() + "\n");
				if (pp.getProject().getBusinessEntity().getAddresses().size() > 0) {
					logoInfo.append(pp.getProject().getBusinessEntity().getAddresses().iterator().next().toString());
				}
				if (pp.getProject().getBusinessEntity().getPhoneNumbers().size() > 0) {
					logoInfo.append(" TEL. ").append(pp.getProject().getBusinessEntity().getPhoneNumbers().iterator().next().toString());
				}
				logoInfo.append("\n");
				if (pp.getProject().getBusinessEntity().getCity() != null) {
					logoInfo.append(pp.getProject().getBusinessEntity().getCity().getName()).append(" - ").append(pp.getProject().getBusinessEntity().getCity().getRegion().getCountry().getName()).append("\n");
				}
				logoInfo.append(pp.getProject().getBusinessEntity().getIdType() == 31 ? "NIT." + pp.getProject().getBusinessEntity().getIdNumber() + " - " + pp.getProject().getBusinessEntity().getVerificationNumber() : "C.C." + pp.getProject().getBusinessEntity().getIdNumber());
			}
			cell1 = new PdfPCell(new Paragraph(logoInfo.toString(), this.selectedFont(FONT1)));
			cell1.setBorder(Rectangle.NO_BORDER);
			table6.addCell(cell1);

			
			
			PdfPCell cell2 = new PdfPCell(new Paragraph("\n" + consecutive, this.selectedFont(FONT7)));
			cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell2.setBorder(Rectangle.NO_BORDER);
			table6.addCell(cell2);

			// tabla de informacion de billed
			PdfPTable table5 = new PdfPTable(2);
			table5.setWidthPercentage(100);
			table5.setHorizontalAlignment(Element.ALIGN_CENTER);

			if (listExpeditionPlace != null && !listExpeditionPlace.isEmpty()) {
				for (PdfPCell pdfPCell : listExpeditionPlace) {
					table5.addCell(pdfPCell);
				}
			}

			PdfPCell cell10 = new PdfPCell(new Paragraph("Fecha de Expedicion: " + dateFormat.format(bill.getExpeditionDate()), this.selectedFont(FONT6)));
			cell10.setBorder(Rectangle.NO_BORDER);
			table5.addCell(cell10);

			PdfPCell cell11 = new PdfPCell(new Paragraph("Fecha de Vencimiento: " + dateFormat.format(bill.getExpirationDate()), this.selectedFont(FONT6)));
			cell11.setBorder(Rectangle.NO_BORDER);
			table5.addCell(cell11);

			PdfPCell cell12 = new PdfPCell(new Paragraph("Señores:  " + bill.getNameBilled(), this.selectedFont(FONT6)));
			cell12.setBorder(Rectangle.NO_BORDER);
			table5.addCell(cell12);

			PdfPCell cell13 = new PdfPCell(new Paragraph("Nit o CC:  " + bill.getIdNumberBilled() + (bill.getBilled().getIdType() == 31 ? (" - " + bill.getBilled().getVerificationNumber()) : ""), this.selectedFont(FONT6)));
			cell13.setBorder(Rectangle.NO_BORDER);
			table5.addCell(cell13);

			PdfPCell cell14 = new PdfPCell(new Paragraph("Domicilio:  " + bill.getAddressBilled(), this.selectedFont(FONT6)));
			cell14.setBorder(Rectangle.NO_BORDER);
			table5.addCell(cell14);

			PdfPCell cell15 = new PdfPCell(new Paragraph("Telefono:  " + bill.getPhoneBilled() + "   Ciudad:  " + bill.getCityBilled(), this.selectedFont(FONT6)));
			cell15.setBorder(Rectangle.NO_BORDER);
			table5.addCell(cell15);

			document.add(table5);
			document.add(new Paragraph(lineSize.get(1), this.selectedFont(FONT8)));

			table6.writeSelectedRows(0, -1, PageSize.LETTER.getWidth() * 0.1f, PageSize.LETTER.getHeight() * 0.885f, writer.getDirectContent());
			/**
			 * END Tabla de la información debajo de los logos de la factura
			 **/

		} catch (Exception e) {
			e.printStackTrace();
			document.close();
		}
		return document;
	}

	/**
	 * Este método crea la segunda parte del pdf el cual es la tabla donde se
	 * listan los conceptos y el valor
	 * 
	 * @param bill
	 *            es el InvoiceConcept
	 * @param resolution
	 *            es la resolucion por la cual se crea la factura
	 * @param document
	 *            es el pdf con la tablas que adicionadas
	 * @return retorna el document
	 */
	public Document createFragmentConcepts(Invoice bill, String resolution, Document document, List<Float> sizeTable) {
		try {
			document.add(new Paragraph("\n" + resolution, this.selectedFont(FONT5)));

			// /////////PRIMERA
			// PARTE DE LA
			// TABLA////////////////////////
			document.add(new Paragraph("\n\n", this.selectedFont(FONT8)));

			List<InvoiceConcept> listGroupedInvoiceConcept = groupInvoiceConceptInterest(bill.getInvoiceConcepts());
			int nConcepts = listGroupedInvoiceConcept.size();
			float totalHeight = 0f;
			PdfPTable table = new PdfPTable(new float[] { 3f, 1f });
			table.setTotalWidth(770.0f);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);// Code
			// 3

			PdfPCell cell16 = new PdfPCell(new Paragraph("POR CONCEPTO DE:", this.selectedFont(FONT3)));
			cell16.setGrayFill(0.6f);
			cell16.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell cell17 = new PdfPCell(new Paragraph("VALOR:", this.selectedFont(FONT3)));
			cell17.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell17.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell17.setFixedHeight(20);
			cell17.setGrayFill(0.6f);

			table.addCell(cell16);
			table.addCell(cell17);

			// PdfPCell lastCell1 = new PdfPCell();
			// lastCell1.setBorder(PdfPCell.BOTTOM | PdfPCell.RIGHT
			// | PdfPCell.LEFT);
			// lastCell1.setFixedHeight(1);
			// table.addCell(lastCell1);
			// table.addCell(lastCell1);

			for (int i = 0; i < nConcepts; i++) {

				InvoiceConcept invConPDF = listGroupedInvoiceConcept.get(i);

				boolean showInterests = invConPDF.getConcept().getProjectProperty().getProject().isMandatoryInterest();
				int invoiceConceptType = invConPDF.getInvoiceConceptType();
				if (!showInterests && invoiceConceptType == InvoiceConcept.TYPE_INTEREST) {
					continue;
					// Se debe pasar al siguiente concepto ya que el concepto
					// actual
					// esta configurado para que NO se muestren intereses
				}
				if (invoiceConceptType == InvoiceConcept.TYPE_CREDIT_NOTE) {
					continue;
					// Se debe pasar al siguiente concepto ya que el concepto
					// actual es de tipo nota credito y estos conceptos no deben
					// aparecer en la facturación.
				}

				Phrase phrase12 = new Phrase(20, getStringConcept(invConPDF), selectedFont(FONT4));
				Paragraph paragraph = new Paragraph(phrase12);
				if (invConPDF.getInvoiceConceptType() == InvoiceConcept.TYPE_NORMAL) {
					Phrase phraseDescripction1 = new Phrase(20, ((invConPDF.getConcept().getPrintDescription() != null || (invConPDF.getConcept().getPrintDescription() != null && !invConPDF.getConcept().getPrintDescription().isEmpty())) ? "\n" + invConPDF.getConcept().getPrintDescription() : ""), selectedFont(FONT5));
					paragraph.add(phraseDescripction1);
				}

				if (invConPDF.getConcept().getStamptax() != null) {
					Phrase phraseStampax = new Phrase(20, "\nImpuesto al Timbre ", selectedFont(FONT4));
					paragraph.add(phraseStampax);
				}
				PdfPCell cell56 = new PdfPCell(paragraph);
				cell56.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
				cell56.setPaddingTop(sizeTable.get(1));
				cell56.setPaddingRight(15.0f);
				cell56.setPaddingLeft(15.0f);
				table.addCell(cell56);

				double calculateStamptax = 0.0;

				if (invConPDF.getConcept().getStamptax() != null) {
					calculateStamptax = invConPDF.getCalculatedCost() * (invConPDF.getConcept().getStamptax().getValue() / 100);
					Phrase phrase13 = new Phrase(20, "" + formatter.format(invConPDF.getCalculatedCost()) + "\n" + calculateStamptax, selectedFont(FONT4));
					PdfPCell cell57 = new PdfPCell(new Paragraph(phrase13));
					cell57.setBorder(Rectangle.RIGHT);
					cell57.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
					cell57.setPaddingTop(sizeTable.get(1));
					cell57.setPaddingRight(15.0f);
					cell57.setPaddingLeft(15.0f);

					table.addCell(cell57);
					totalHeight += table.getRowHeight(i + 2);

				} else {
					Phrase phrase13 = new Phrase(20, "" + formatter.format(invConPDF.getCalculatedCost()), selectedFont(FONT4));
					PdfPCell cell57 = new PdfPCell(new Paragraph(phrase13));
					cell57.setBorder(Rectangle.RIGHT);
					cell57.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
					cell57.setPaddingTop(sizeTable.get(1));
					cell57.setPaddingRight(15.0f);
					cell57.setPaddingLeft(15.0f);
					table.addCell(cell57);
					totalHeight += table.getRowHeight(i + 2);

				}
				iva += invConPDF.getCalculatedTax();
				subtotal += invConPDF.getCalculatedCost();
				totalStamptax += calculateStamptax;
			}

			// table.setHeaderRows(2);
			// table.setFooterRows(1);
			total = iva + subtotal + totalStamptax;

			totalHeight = table.getTotalHeight();

			// table.setExtendLastRow(true);
			if (totalHeight < sizeTable.get(0)) {
				PdfPCell lastCell = new PdfPCell(new Paragraph(""));

				lastCell.setFixedHeight(sizeTable.get(0) - totalHeight);
				lastCell.setBorder(PdfPCell.LEFT | PdfPCell.RIGHT);
				table.addCell(lastCell);
				table.addCell(lastCell);
			}

			document.add(table);

		} catch (Exception e) {
			document.close();
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * Este método crea la tabla de observación y total, el valor de la factura
	 * en letras y observaciones
	 * 
	 * @param bill
	 *            es el InvoiceConcept
	 * @param document
	 *            es el pdf con las tablas que se le han adicionado
	 * @param cellList
	 *            es una lista de de celdas para adicionar en la tabla del tota.
	 * @return
	 */
	public Document createFragmentFinal(Invoice bill, Document document, List<PdfPCell> cellList, String linebreaker) {

		try {
			Phrase phrase14 = new Phrase(20, (bill.getProjectProperty().getProject().getBusinessEntity().getBillerObservation() != null ? bill.getProjectProperty().getProject().getBusinessEntity().getBillerObservation() : ""), this.selectedFont(4));
			Phrase phrase40 = new Phrase(20, (bill.getBiller().getBillerObservation() != null ? bill.getBiller().getBillerObservation() : ""), this.selectedFont(4));
			PdfPCell cellob1 = new PdfPCell(phrase14);
			cellob1.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cellob1.setPadding(10);
			PdfPCell cellob2 = new PdfPCell(phrase40);
			cellob2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cellob2.setPadding(10);

			float[] colsWidth1 = { 5f, 3f };
			PdfPTable table1 = new PdfPTable(colsWidth1);
			table1.getDefaultCell().setPadding(0);
			table1.setWidthPercentage(100);
			table1.setHorizontalAlignment(Element.ALIGN_CENTER); // Code
			// 3

			float[] colsWidth2 = { 2.5f, 5f };
			PdfPTable table2 = new PdfPTable(colsWidth2);
			table2.getDefaultCell().setPaddingRight(0);

			for (PdfPCell f : cellList) {
				table2.addCell(f);
			}

			if (bill.getProjectProperty().isMandate() == true) {
				float[] colsWid = { 1f };
				PdfPTable table55 = new PdfPTable(colsWid);
				table55.addCell(cellob1);
				table1.addCell(table55);
			} else {
				float[] colsWid = { 1f };
				PdfPTable table55 = new PdfPTable(colsWid);
				table55.addCell(cellob2);
				table1.addCell(table55);
			}

			table1.addCell(table2);

			document.add(table1);

			Phrase phrase21 = new Phrase(20, "SON: " + convertNumberToString.convertToString(total), this.selectedFont(4));

			PdfPTable table3 = new PdfPTable(1);
			table3.setWidthPercentage(100);
			table3.setHorizontalAlignment(Element.ALIGN_CENTER);
			PdfPCell cellson = new PdfPCell(phrase21);
			cellson.setFixedHeight(20);
			cellson.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table3.addCell(cellson);

			// //////////////CUARTA
			// PARTE DE LA
			// TABLA, OBSERVACIONES//////////////

			PdfPTable table4 = new PdfPTable(1);
			table4.setWidthPercentage(100);
			table4.setHorizontalAlignment(Element.ALIGN_TOP);

			PdfPCell cell18 = new PdfPCell(new Paragraph("       OBSERVACIONES", this.selectedFont(3)));

			cell18.setFixedHeight(20);
			cell18.setGrayFill(0.6f);
			cell18.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table4.addCell(cell18);

			if (bill.getProjectProperty().getBillingResolution() != null && bill.getProjectProperty().getBillingResolution().getObservation() != null) {
				String Prtdesciption = "";

				/*
				 * 2012-11-30 Se valida con condicionales ya que está generando
				 * NULLPOINTER
				 */
				// if ( tempRetentionRateAccount != null &&
				// tempRetentionRateAccount.getDescription() != null) {
				// //if
				// (bill.getInvoiceConcepts().get(0).getConcept().getConceptRetentionRateAccounts().get(0).getConcept().getAccountingAccountsRecover().getDescription()
				// != null) {
				// Prtdesciption =
				// bill.getInvoiceConcepts().get(0).getConcept().getConceptRetentionRateAccounts().get(0).getConcept().getAccountingAccountsRecover().getDescription()
				// + linebreaker;
				// }

				List<InvoiceConcept> tempInvoList = bill.getInvoiceConcepts();
				if (tempInvoList != null && !tempInvoList.isEmpty()) {
					List<ConceptRetentionRateAccount> tempCRRA = tempInvoList.get(0).getConcept().getConceptRetentionRateAccounts();

					if (tempCRRA != null && !tempCRRA.isEmpty()) {
						RetentionRateAccount tempRetentionRateAccount = tempCRRA.get(0).getConcept().getAccountingAccountsRecover();
						if (tempRetentionRateAccount != null && tempRetentionRateAccount.getDescription() != null) {
							Prtdesciption = tempRetentionRateAccount.getDescription() + linebreaker;
						}
					}
				}
				Phrase phrase23 = new Phrase(20, bill.getProjectProperty().getBillingResolution().getObservation() + Prtdesciption, this.selectedFont(4));

				PdfPCell cellobservaciones = new PdfPCell(phrase23);

				// cellobservaciones.setFixedHeight(60);
				cellobservaciones.setPadding(6);
				table4.addCell(cellobservaciones);
			} else {
				String Prtdesciption = "";
				List<InvoiceConcept> tempInvoList = bill.getInvoiceConcepts();
				if (tempInvoList != null && !tempInvoList.isEmpty()) {
					List<ConceptRetentionRateAccount> tempCRRA = tempInvoList.get(0).getConcept().getConceptRetentionRateAccounts();

					if (tempCRRA != null && !tempCRRA.isEmpty()) {
						RetentionRateAccount tempRetentionRateAccount = tempCRRA.get(0).getConcept().getAccountingAccountsRecover();
						if (tempRetentionRateAccount != null && tempRetentionRateAccount.getDescription() != null) {
							Prtdesciption = tempRetentionRateAccount.getDescription() + linebreaker;
						}
					}
				}

				Phrase phrase23 = new Phrase(20, Prtdesciption, this.selectedFont(4));
				PdfPCell cellobservaciones = new PdfPCell(phrase23);
				// cellobservaciones.setFixedHeight(100);
				cellobservaciones.setPadding(6);
				table4.addCell(cellobservaciones);
				log(Level.INFO, Prtdesciption);
			}

			// /////////////////////////////////////////
			document.add(new Paragraph(" ", this.selectedFont(1)));
			document.add(table3);
			document.add(new Paragraph(" ", this.selectedFont(1)));
			document.add(table4);

		} catch (Exception e) {
			e.printStackTrace();
			document.close();
		}

		return document;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private String getStringConcept(InvoiceConcept lista) {
		String res = "";

		if (lista.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && lista.getConcept().isShowBilledPeriod()) {
			Calendar c = Calendar.getInstance();
			Calendar c1 = Calendar.getInstance();
			c.setTime(lista.getIniPeriodDate());
			c1.setTime(lista.getLastLiquidationDate());
			res += "Interes De Mora(" + lista.getConcept().getName() + " Del " + c.get(Calendar.DAY_OF_MONTH) + "/" + showMonth(c.getTime(), lista.getDiscountMounths()) + " AL " + c1.get(Calendar.DAY_OF_MONTH) + "/" + showMonth(c1.getTime(), lista.getDiscountMounths()) + ")";
		} else
			res += tools.nameInvoiceConcept(lista.getInvoiceConceptType(), lista.getConcept().getName());

		if (lista.getInvoice().getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT)
			res += " " + lista.getInvoice().getProjectProperty().getRentableUnit().getName();
		else if (lista.getInvoice().getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY) {
			RentableUnit nameRentableUnit;

			if (entityManager != null) {
				nameRentableUnit = entityManager.find(RentableUnit.class, lista.getRentableUnitId());
			} else {
				nameRentableUnit = (RentableUnit) session.get(RentableUnit.class, lista.getRentableUnitId());
			}
			res += " " + nameRentableUnit.getName();
		}
		if (res.isEmpty())
			res += lista.getConcept().getName();
		if (lista.getConcept().isShowBilledPeriod() && lista.getInvoiceConceptType() != InvoiceConcept.TYPE_INTEREST) {
			if (lista.getConcept().getImmediatePaymentState() != Concept.IMMEDIATE_PAYMENT_NOT_SET) {

				Query q = getEntityManager().createNativeQuery("SELECT closure_date FROM project_closure pc where pc.project=? ORDER BY closure_date DESC");
				q.setParameter(1, lista.getInvoice().getProjectProperty().getProject());
				List<Date> projectClosureList = q.getResultList();
				Calendar c = Calendar.getInstance();
				c.setTime(projectClosureList.get(0));
				Query query = getEntityManager().createNativeQuery("SELECT days FROM days_for_early_payment where id=?");
				query.setParameter(1, c.get(Calendar.MONTH) + 1);
				int days1, days2;
				days1 = (Integer) query.getResultList().get(0);
				days2 = lista.getIniPeriodDate().getDay();
				if (days2 > days1)
					res += " - " + showMonth(lista.getIniPeriodDate(), lista.getConcept().getDiscountMounths());
				else
					res += " - " + showMonth(lista.getIniPeriodDate(), lista.getConcept().getDiscountMounths() + 1);

			} else {
				res += " - " + showMonth(lista.getIniPeriodDate(), lista.getConcept().getDiscountMounths());
			}
			lista.setBilledPeriod(showMonth(lista.getIniPeriodDate(), lista.getConcept().getDiscountMounths()));
			lista.setDiscountMounths(lista.getConcept().getDiscountMounths());
		}
		return res;

	}

	public String ConcatenatePdf(List<String> listPdf) {
		DownloadAttachment downloadAttachment = new DownloadAttachment();
		try {

			String filePDF1 = "tmp/billingResolution/billing_resolution" + this.selectedProject.getId() + ".pdf";
			String server1 = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
			String path1 = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server1);
			path1 = path1.substring(0, path1.lastIndexOf("/")) + "/";
			this.linkPDF = filePDF1;

			log(Level.INFO, "Concatenate Two PDF");

			FileOutputStream fileOutputStream = new FileOutputStream(path1 + filePDF1);
			PdfCopyFields copy = new PdfCopyFields(fileOutputStream);
			for (String pdf : listPdf) {
				try {

					if (pdf.contains("_Origins") && !pdf.contains("NO_APROBADA_")) {
						String pathCopia = pdf;
						pathCopia = pathCopia.replace("Origins", "COPIA");
						try {
							String verifyExistences = pdf.replace("Origins", "ORIGINAL");

							if (!new File(verifyExistences).exists()) {
								stampPDF(pdf, pathCopia, false);
								stampPDF(pdf, pdf, true);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (pdf.contains("Origins")) {
								pdf = pdf.replaceAll("Origins", "Original");
							}
							PdfReader reader = new PdfReader(pdf);
							copy.addDocument(reader);
							PdfReader reader2 = new PdfReader(pathCopia);
							copy.addDocument(reader2);

						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						PdfReader read = new PdfReader(pdf);
						copy.addDocument(read);
					}

				} catch (Exception e) {
					log(Level.INFO, "no se pudo generar la copia del pdf por: " + e.getMessage());
					e.printStackTrace();
				}
			}
			copy.close();
			fileOutputStream.close();
			return downloadAttachment.download(this.linkPDF);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public String getLinkPDF() {
		return this.linkPDF;
	}

	public void setLinkPDF(String linkPDF) {
		this.linkPDF = linkPDF;
	}

	static class HeaderFooter extends PdfPageEventHelper {

		Invoice inv;

		public HeaderFooter(Invoice bill) {
			inv = bill;
		}

		public void onEndPage(PdfWriter writer, Document document) {

			String fileIMAGE = "img/log_Terranum.jpg";
			String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);

			path = path.substring(0, path.lastIndexOf("/")) + "/";
			// Encabezado
			try {
				if (inv.getProjectProperty().isMandate() == true) {
					if (inv.getProjectProperty().getProject().getBusinessEntity().getLogo() != null && inv.getProjectProperty().getProject().getBusinessEntity().getLogo().length > 0) {
						Image image = Image.getInstance(inv.getProjectProperty().getProject().getBusinessEntity().getLogo());
						image.setAbsolutePosition(50, 700);
						image.scaleToFit(150, 80);
						document.add(image);
					}
					if (inv.getBiller().getLogo() != null && inv.getBiller().getLogo().length > 0) {
						Image image1 = Image.getInstance(inv.getBiller().getLogo());
						image1.setAbsolutePosition(400, 700);
						image1.scaleToFit(150, 80);
						document.add(image1);
					}
				} else {
					if (inv.getBiller().getLogo() != null && inv.getBiller().getLogo().length > 0) {
						Image image = Image.getInstance(inv.getBiller().getLogo());
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

			// Pie
			int boxWidth = (int) (PageSize.LETTER.getWidth() / 3);
			int boxHeight = 60;

			try {
				String fontFile = path + "Font/TAHOMA.TTF";
				BaseFont bf = BaseFont.createFont(fontFile, BaseFont.IDENTITY_H, true);
				Font font1 = new Font(bf, 7, Font.NORMAL);
				String firstColumn = "";
				String secondColumn = "";
				String thirdColumn = "";

				firstColumn += "ORIGINAL: " + (inv.getBiller().getOriginal() != null ? inv.getBiller().getOriginal() : "");
				firstColumn += "\nCOPIA 1: " + (inv.getBiller().getFirstCopy() != null ? inv.getBiller().getFirstCopy() : "");
				firstColumn += "\nCOPIA 2: " + (inv.getBiller().getSecondCopy() != null ? inv.getBiller().getSecondCopy() : "");

				secondColumn += inv.getBiller().getNameBusinessEntity();
				secondColumn += "\n" + "DIRECCION: " + inv.getAddressBiller() + "- TELEFONO: " + inv.getProjectProperty().getPhoneNumberByPhoneBiller().getNumber();
				secondColumn += "\n" + inv.getBiller().getCity().getName();

				thirdColumn += (inv.getRealProperty() != null ? inv.getRealProperty().getNameProperty() : "");
				ColumnText ct0 = new ColumnText(writer.getDirectContent());
				ct0.setText(new Phrase(firstColumn, font1));
				ct0.setLeading(8f);
				ct0.setIndent(40f);
				ct0.setFollowingIndent(40f);
				ct0.setSimpleColumn(0, 0, boxWidth, boxHeight);

				ColumnText ct1 = new ColumnText(writer.getDirectContent());
				ct1.setText(new Phrase(secondColumn, font1));
				ct1.setLeading(8f);
				ct1.setIndent(10f);
				ct1.setFollowingIndent(10f);
				ct1.setSimpleColumn(boxWidth, 0, boxWidth * 2, boxHeight);

				ColumnText ct2 = new ColumnText(writer.getDirectContent());
				ct2.setText(new Phrase(thirdColumn, font1));
				ct2.setLeading(8f);
				ct2.setIndent(10f);
				ct2.setFollowingIndent(10f);
				ct2.setRightIndent(40f);
				ct2.setSimpleColumn(boxWidth * 2, 0, boxWidth * 3, boxHeight);

				ct0.go();
				ct1.go();
				ct2.go();

			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public String messageprefix = "";

	public String getMessageprefix() {
		return messageprefix;
	}

	public void setMessageprefix(String messageprefix) {
		this.messageprefix = messageprefix;
		;
	}

	public void prefixExist() {

		ProjectList project = new ProjectList();
		Integer y = 0;

		for (Project projectObj : project.getResultList()) {
			if (projectObj.getProjectPrefix().equals(this.instance.getProjectPrefix())) {
				y = y + 1;
			}
		}
		if (y > 1)
			this.messageprefix = "El prefijo ingresado ya existe";
		else
			this.messageprefix = "";
	}

	public String getErrorMessageP() {
		return errorMessageP;
	}

	public void setErrorMessageP(String errorMessageP) {
		this.errorMessageP = errorMessageP;
	}

	public boolean isErrorP() {
		return errorP;
	}

	public void setErrorP(boolean errorP) {
		this.errorP = errorP;
	}

	/**
	 * Método que valida si se puede aprobar una factura o Cuenta de Cobro
	 * 
	 * @param invoiceSelected
	 */
	public void validateInvoices(Invoice invoiceSelected) {
		if (invoiceSelected.getDocumentType() == Invoice.DOCUMENT_TYPE_BILL) {
			this.setErrorP(!billinResolutionValidate(invoiceSelected));
			if (isErrorP()) {
				if (!invoiceSelected.isSelected()) {
					invoiceSelected.setBillingError(isErrorP());
					if (isErrorP() == true)
						invoiceSelected.setMessajeError("Verificar fecha de vencimiento y número maximo, o si  existe una resolución de facturación configurada.");
				}

			}
			setErrorBilling(isErrorP(), invoiceSelected, this.getInvoiceList(), "Verificar fecha de vencimiento y número máximo, o si  existe una resolución de facturación configurada.");
		} else if (invoiceSelected.getDocumentType() == Invoice.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE) {
			this.setErrorP(!acountReceivableValidate(invoiceSelected));
			if (isErrorP()) {
				if (!invoiceSelected.isSelected()) {
					invoiceSelected.setMessajeError("Verificar si existe una cuenta de cobro configurada.");
					if (isErrorP() == true)
						invoiceSelected.setBillingError(isErrorP());
				}
			}
		}
	}

	/**
	 * Metodo que modifica las resoluciones de la factura según el error que
	 * pasa por parametro
	 * 
	 * @param error
	 * @param invoiceSelected
	 * @param invoiceList2
	 */
	private void setErrorBilling(boolean error, Invoice invoiceSelected, List<Invoice> invoiceList, String Message) {
		BillingResolution resolutionSelected = invoiceSelected.getProjectProperty().getBillingResolution();
		for (Invoice invoice : invoiceList) {
			BillingResolution resolutionCurrent = invoice.getProjectProperty().getBillingResolution();
			if (resolutionSelected != null && resolutionCurrent != null && resolutionCurrent.getId() != 0 && resolutionCurrent.getId() == resolutionSelected.getId() && !invoice.isSelected() && invoice.getDocumentType() == Invoice.DOCUMENT_TYPE_BILL) {
				invoice.setBillingError(error);
				if (isErrorP() == true)
					invoice.setMessajeError(Message);

			}
		}
	}

	/**
	 * metodo que determina si se pueda asignar una resolución de facturación a
	 * una factura
	 * 
	 * @param invoiceSelected
	 */
	public boolean billinResolutionValidate(Invoice invoiceSelected) {
		if (invoiceSelected.getProjectProperty().getBillingResolution() == null) {
			return false;
		}
		boolean outForDateValidation = billingResolutionOutForDateValidation(invoiceSelected.getProjectProperty().getBillingResolution());
		boolean enough = enoughForAllBillins(invoiceSelected, this.getInvoiceList());

		return outForDateValidation && enough;
	}

	/**
	 * Método que verifica si la resolución alcanza para todas las facturas
	 * 
	 * @param invoiceSelected
	 * @return true si la resolución alcanza para todas las facturas, de los
	 *         contrario retorna false
	 */
	private boolean enoughForAllBillins(Invoice invoiceSelected, List<Invoice> invoices) {
		BillingResolution resolutionSelected = invoiceSelected.getProjectProperty().getBillingResolution();
		int count = 0;
		for (Invoice invoice : invoices) {
			BillingResolution resolutionCurrent = invoice.getProjectProperty().getBillingResolution();
			if (resolutionCurrent != null && resolutionCurrent.getId() != 0 && resolutionCurrent.getId() == resolutionSelected.getId() && invoice.isSelected()) {
				count++;
			}
		}
		if (resolutionSelected.getCurrent() + count > resolutionSelected.getMax()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * metodo que determina si se pueda asignar una Cuenta de cobro a una
	 * factura
	 * 
	 * @param invoiceSelected
	 */
	public boolean acountReceivableValidate(Invoice invoiceSelected) {
		if (invoiceSelected.getProjectProperty().getAcountsBilling() == null) {
			return false;
		} else
			return true;
		// boolean outForDateValidation =
		// billingResolutionOutForDateValidation(invoiceSelected.getProjectProperty().getBillingResolution());
		// boolean enough = enoughForAllBillins(invoiceSelected,
		// this.getInvoiceList());

		// return outForDateValidation && enough;
		// return enough;
	}

	/**
	 * Método que valida si la resolución de facturación no esta vencias
	 * 
	 * @param billingResolution
	 * @return true si la resolución no esta vencida, de lo contrario retorna
	 *         false
	 */
	private boolean billingResolutionOutForDateValidation(BillingResolution billingResolution) {

		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MILLISECOND, 0);

		Calendar resolutionEndDate = Calendar.getInstance();
		resolutionEndDate.setTime(billingResolution.getEndDate());
		resolutionEndDate.set(Calendar.HOUR, 0);
		resolutionEndDate.set(Calendar.MINUTE, 0);
		resolutionEndDate.set(Calendar.SECOND, 0);
		resolutionEndDate.set(Calendar.MILLISECOND, 0);

		if (currentDate.after(resolutionEndDate)) {
			return false;
		} else {
			return true;
		}
	}

	public void openModalPanelInvoicesAction(Project project) {
		this.destroyModal1();
		this.instanceProject(project);
		List<Invoice> invoices = getInvoiceList();
		for (Invoice invoice : invoices) {
			validateInvoices(invoice);
		}
	}

	public boolean openModalPanelInvoicesActionApprove(Project project) {
		this.instanceProject(project);
		List<Invoice> invoices = getInvoiceListApprove();
		if (invoices == null || invoices.isEmpty()) {
			return false;
		}
		for (Invoice invoice : invoices) {
			validateInvoices(invoice);
		}
		return true;
	}

	public HtmlExtendedDataTable getTableConceptComparableBind() {
		return tableConceptComparableBind;
	}

	public void setTableConceptComparableBind(HtmlExtendedDataTable tableConceptComparableBind) {
		this.tableConceptComparableBind = tableConceptComparableBind;
	}

	public String getTableConceptCamparableState() {
		return tableConceptCamparableState;
	}

	public void setTableConceptCamparableState(String tableConceptCamparableState) {
		this.tableConceptCamparableState = tableConceptCamparableState;
	}

	public ArrayList<Object[]> getConceptComparableListPerInvoice() {

		if ((this.conceptComparableList == null || (this.conceptComparableList != null && this.conceptComparableList.isEmpty())) && this.invoiceList != null) {
			this.conceptComparableList = new ArrayList<Object[]>();

			if (this.selectedInvoice == null)
				return this.conceptComparableList;

			List<InvoiceConcept> invConTempList = this.selectedInvoice.getInvoiceConcepts();
			for (InvoiceConcept invoiceConcept : invConTempList) {
				InvoiceConcept invConOlder = null;
				Concept concept = invoiceConcept.getConcept();

				String consulta = "FROM InvoiceConcept ic WHERE ic.concept = ? AND ic.invoiceConceptType = ? AND ic.invoice.approved = ?  ORDER BY ic.invoice.expeditionDate DESC";
				Query query = this.getEntityManager().createQuery(consulta);
				query.setParameter(1, concept);
				query.setParameter(2, InvoiceConcept.TYPE_NORMAL);
				query.setParameter(3, true);

				query.setMaxResults(2);
				@SuppressWarnings("unchecked")
				List<InvoiceConcept> tempResultList = (List<InvoiceConcept>) query.getResultList();
				if (tempResultList.size() == 2) {/*
												 * Si el tamaño es 2, quiere
												 * decir que no es la primera
												 * vez que se calcula el
												 * concepto
												 */
					invConOlder = tempResultList.get(0);
				}

				RentableUnit rentableUnit = null;

				if (invoiceConcept.getRentableUnitId() > 0) {
					consulta = "FROM RentableUnit ru WHERE ru.id = ?";
					query = this.getEntityManager().createQuery(consulta);
					// se pasa el id de la unidad
					// arrendable solo en caso de
					// que esta este asociada a la
					// hoja de términos del
					// invoice concept que se esta
					// evaluando WA
					query.setParameter(1, invoiceConcept.getInvoice().getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT ? invoiceConcept.getRentableUnitId() : 0);
					query.setMaxResults(1);
					List<?> result = query.getResultList();
					if (result.size() > 0) {/*
											 * Si el tamaño es 2, quiere decir
											 * que no es la primera vez que se
											 * calcula el concepto
											 */
						rentableUnit = (RentableUnit) result.get(0);
					}
				}

				String[] array = new String[10];
				array[0] = this.selectedInvoice.getBilled().getNameBusinessEntity();
				array[1] = (rentableUnit != null ? rentableUnit.getName() : "N.A");
				array[3] = concept.getName();
				array[5] = "$ " + formatter.format(invoiceConcept.getCalculatedCost()) + "";
				array[7] = concept.getFixedValue() == null ? "Valor Variable" : "$ " + formatter.format(concept.getFixedValue()) + "";
				array[8] = concept.getFixedValue() == null ? "N.A" : "$ " + formatter.format(Math.abs(concept.getFixedValue() - invoiceConcept.getCalculatedCost())) + "";

				if (invConOlder != null) {
					array[4] = "$ " + formatter.format(invConOlder.getCalculatedCost()) + "";
					array[6] = "$ " + formatter.format(Math.abs(invConOlder.getCalculatedCost() - invoiceConcept.getCalculatedCost())) + "";
				} else {
					array[4] = "N.A";
					array[6] = "N.A";
				}

				if (rentableUnit != null) {
					/*
					 * se aigna el estado Asiggnada en caso de que la unidad
					 * arrendable siga asociada a la hoja de términos, y esta
					 * este vigente.
					 */
					if (invoiceConcept.getInvoice().getProjectProperty().getStatus() != ProjectProperty.STATUS_EXPIRED || invoiceConcept.getInvoice().getProjectProperty().getStatus() != ProjectProperty.STATUS_TERMINATED && invoiceConcept.getInvoice().getProjectProperty().getRentableUnit() != null)
						array[2] = "Asignada";
					else
						array[2] = "Vacante";
				} else {
					array[2] = "N.A";
				}
				array[9] = this.getInvoiceConceptCostCenter(invoiceConcept);

				this.conceptComparableList.add(array);
			}

		}

		return conceptComparableList;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object[]> getConceptComparableList() {

		if ((this.conceptComparableListTotal == null || (this.conceptComparableListTotal != null && this.conceptComparableListTotal.isEmpty())) && this.invoiceList != null) {
			this.conceptComparableListTotal = new ArrayList<Object[]>();

			String listaIdInvoice = "";
			for (Invoice invoice : this.invoiceList) {
				if (listaIdInvoice.isEmpty())
					listaIdInvoice += invoice.getId();
				else
					listaIdInvoice += "," + invoice.getId();
			}
			if (!listaIdInvoice.isEmpty()) {

				String consulta = "select be.name_business_entity as cliente, ru.name as unidad_arrendable, own.id as owner,c.name as concepto, " + "(select last.calculated_cost from invoice_concept last where last.ini_period_date < ic.ini_period_date and last.concept = c.id and last.invoice_concept_type = ic.invoice_concept_type limit 1) as last_month," + " ic.calculated_cost as current_month, c.fixed_value," + "(case c.cost_center_type when 1 then (select p.cost_center_project from project p inner join project_property pp on pp.id = c.project_property where pp.project=p.id limit 1)" + "when 2 then (select cast(uar.cost_center as varchar) from rentable_unit uar where uar.id=ic.rentable_unit_id limit 1)"
						+ "when 3 then (select cast(con.cost_center as varchar) from concept con where con.id=c.id limit 1)" + "end) as cost_center, ic.invoice_concept_type as ic_type from invoice_concept ic "

						+ " inner join invoice i on i.id = ic.invoice" + " inner join business_entity be on i.billed = be.id" + " inner join concept c on c.id = ic.concept" + " left outer join rentable_unit ru on ru.id = ic.rentable_unit_id" + " left outer join business_entity own on own.id = ru.owner " + " where invoice in(" + listaIdInvoice + ")";
				Query query = getEntityManager().createNativeQuery(consulta);
				List<Object[]> listaObejtosComparar = query.getResultList();

				for (Object[] objects : listaObejtosComparar) {
					double last_liquidation = (objects[4] == null) ? 0 : (Double.valueOf(objects[4].toString()));

					String[] array = new String[10];
					array[0] = objects[0].toString();
					array[1] = (objects[1] != null ? objects[1].toString() : "N.A");
					array[2] = (objects[2] != null ? "Asignada" : "Vacante");
					array[3] = tools.nameInvoiceConcept(Integer.valueOf(objects[8].toString()), objects[3].toString());
					array[4] = "$ " + formatter.format(last_liquidation);
					array[5] = "$ " + formatter.format(objects[5]) + "";
					array[6] = "$ " + formatter.format(Math.abs(Double.valueOf(last_liquidation) - Double.valueOf(objects[5].toString()))) + "";
					array[7] = objects[6] == null ? "Valor Variable" : "$ " + formatter.format(objects[6]) + "";
					array[8] = objects[6] == null ? "N.A" : "$ " + formatter.format(Math.abs(Double.valueOf(objects[6].toString()) - Double.valueOf(objects[5].toString()))) + "";
					array[9] = objects[7].toString();
					this.conceptComparableListTotal.add(array);
				}
			}
			// List<RentableUnit> rentableUnits2 = new
			// ArrayList<RentableUnit>();
			if (this.selectedProject == null)
				return this.conceptComparableListTotal;

			StringBuilder stringBuilder = new StringBuilder("(");
			for (RealProperty rp : this.selectedProject.getProjectRealProperty()) {
				if (rp.getStep() == RealProperty.STEP_APPROVED) {
					stringBuilder.append(rp.getId());
					stringBuilder.append(",");
				}
			}
			String in;
			List<RentableUnit> rentableUnits3 = new ArrayList<RentableUnit>();
			if (this.selectedProject.getProjectRealProperty() != null && !this.selectedProject.getProjectRealProperty().isEmpty()) {
				in = stringBuilder.substring(0, stringBuilder.lastIndexOf(",")) + ")";
				Query q = this.entityManager.createNativeQuery("select ru.* FROM rentable_unit ru join area a on ru.area = a.id join floor f on a.id = f.id join construction c on f.construction = c.id join real_property rp on c.real_property = rp.id WHERE rp.id IN " + in, RentableUnit.class);
				rentableUnits3 = q.getResultList();
			}

			for (int i = 0; i < rentableUnits3.size(); i++) {
				String consulta1 = "FROM InvoiceConcept ic WHERE ic.rentableUnitId = ?  AND ic.invoice.approved = ? AND ic.invoiceConceptType = ? OR ic.invoiceConceptType = ? ORDER BY ic.invoice.expeditionDate DESC";
				Query query1 = this.getEntityManager().createQuery(consulta1);
				query1.setParameter(1, rentableUnits3.get(i).getId());
				query1.setParameter(2, true);
				query1.setParameter(3, InvoiceConcept.TYPE_NORMAL);
				query1.setParameter(4, InvoiceConcept.TYPE_INTEREST);

				query1.setMaxResults(1);
				List<InvoiceConcept> tempResultList1 = (List<InvoiceConcept>) query1.getResultList();

				String[] array = new String[10];
				array[1] = rentableUnits3.get(i).getName();
				array[2] = "Vacante";
				array[5] = "0";

				// validar si el invoice es del periodo
				// actual de facturacion,
				// si no aplica lo del else. N.A

				// String period =
				// getPeriod(this.selectedProject);
				// && this.billingPeriod.equals(period)

				if (!tempResultList1.isEmpty()) {
					array[0] = tempResultList1.get(0).getInvoice().getBilled().getNameBusinessEntity();
					array[3] = tools.nameInvoiceConcept(tempResultList1.get(0).getInvoiceConceptType(), tempResultList1.get(0).getConcept().getName());
					array[7] = tempResultList1.get(0).getConcept().getFixedValue() == null ? "Valor Variable" : "$ " + formatter.format(tempResultList1.get(0).getConcept().getFixedValue()) + "";
					array[8] = tempResultList1.get(0).getConcept().getFixedValue() == null ? "N.A" : "$ " + formatter.format(Math.abs(tempResultList1.get(0).getConcept().getFixedValue() - tempResultList1.get(0).getCalculatedCost())) + "";
					array[4] = "$ " + formatter.format(tempResultList1.get(0).getCalculatedCost()) + "";
					array[6] = "$ " + formatter.format(tempResultList1.get(0).getCalculatedCost()) + "";
					array[9] = this.getInvoiceConceptCostCenter(tempResultList1.get(0));
				} else {
					array[0] = "N.A";
					array[3] = "N.A";
					array[4] = "$ " + 0 + "";
					array[6] = "$ " + 0 + "";
					array[7] = "N.A";
					array[8] = "N.A";
					array[9] = "N.A";
				}

				this.conceptComparableListTotal.add(array);

			}
		}

		return conceptComparableListTotal;
	}

	public void setConceptComparableList(ArrayList<Object[]> conceptComparableList) {
		this.conceptComparableList = conceptComparableList;
	}

	/**
	 * Este metodo retorna el centro de costo de un invoiceConcept
	 */
	public String getInvoiceConceptCostCenter(InvoiceConcept invoiceConcept) {

		String costCenter = "";

		switch (invoiceConcept.getConcept().getCostCenterType()) {

		case Concept.COST_CENTER_TYPE_PROJECT:
			costCenter = invoiceConcept.getConcept().getProjectProperty().getProject().getCostCenterProject();
			break;

		case Concept.COST_CENTER_TYPE_RENTABLEUNIT:
			if (invoiceConcept.getConcept().getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT) {
				costCenter = invoiceConcept.getConcept().getProjectProperty().getRentableUnit().getCostCenter() + "";
			} else if (invoiceConcept.getConcept().getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY) {
				String q = "FROM RentableUnit ru WHERE ru.id = ?";
				Query query = this.entityManager.createQuery(q);
				query.setParameter(1, invoiceConcept.getRentableUnitId());

				@SuppressWarnings("unchecked")
				List<RentableUnit> rentableUnits = query.getResultList();
				if (rentableUnits.size() == 1) {
					costCenter = rentableUnits.get(0).getCostCenter() + "";
				}
			}
			break;

		case Concept.COST_CENTER_TYPE_CONCEPT:
			costCenter = invoiceConcept.getConcept().getCostCenter() + "";
			break;

		default:
			costCenter = "0";
			break;
		}

		return costCenter;
	}

	/**
	 * Método que crear un nuevo proyecto y se lo asigna al usuario admin
	 * 
	 * @return
	 */
	@Transactional
	public String persistProject() {
		EntityManager entityManager = getEntityManager();
		String res = this.persist();
		ProjectUser projectUser = new ProjectUser();
		projectUser.setProject(this.getInstance());

		projectUser.setUser(user);
		entityManager.persist(projectUser);
		entityManager.flush();

		user.getProjectUsers().add(this.getInstance());

		SelectItem item = new SelectItem();
		item.setValue(this.getInstance().getId() + "");
		item.setLabel(this.getInstance().getNameProject());
		projectsFilter.add(item);

		return res;
	}

	public boolean isRecoverCloseable() {
		return recoverCloseable;
	}

	public void setRecoverCloseable(boolean recoverCloseable) {
		this.recoverCloseable = recoverCloseable;
	}

	private boolean recoverCalculateCloseable() {
		Calendar today = Calendar.getInstance();

		Query q = getEntityManager().createQuery("FROM RecoverClosure rc WHERE rc.project = ? ORDER BY rc.closureDate DESC");
		q.setParameter(1, this.selectedProject);
		q.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<RecoverClosure> closures = q.getResultList();
		if (closures != null && !closures.isEmpty()) {
			Calendar compareDate = Calendar.getInstance();
			compareDate.setTime(closures.get(0).getClosureDate());
			compareDate.add(Calendar.MONTH, +1);
			if (compareDate.get(Calendar.YEAR) < today.get(Calendar.YEAR))
				return true;
			else if (compareDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && compareDate.get(Calendar.MONTH) < today.get(Calendar.MONTH)) {
				return true;
			}
		} else
			return true;

		return false;
	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public void closeRecover() {
		Calendar nextRecover = recoverCloseDate();
		if (nextRecover != null) {
			RecoverClosure recoverClosure = new RecoverClosure();
			recoverClosure.setProject(this.selectedProject);
			recoverClosure.setDate(new Date());
			recoverClosure.setMonth(nextRecover.get(Calendar.MONTH));
			recoverClosure.setClosureDate(nextRecover.getTime());

			this.getEntityManager().joinTransaction();
			this.getEntityManager().persist(recoverClosure);
			this.getEntityManager().flush();

			this.getEntityManager().flush();

			this.instance = this.selectedProject;
			this.instance.getRecoverClosures().add(recoverClosure);
			this.persist();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SeamResourceBundle.getBundle().getString("closeProject"), "Se cerr&oacute; " + this.billingPeriod + " exitosamente"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SeamResourceBundle.getBundle().getString("closeProject"), "El periodo de cierre es inv&aacute;lido"));
		}

		this.destroyModal1();
	}

	private Calendar recoverCloseDate() {
		Calendar today = Calendar.getInstance();

		Query q = getEntityManager().createQuery("FROM RecoverClosure rc WHERE rc.project.id = ? ORDER BY rc.closureDate DESC");
		q.setParameter(1, this.selectedProject.getId());
		q.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<RecoverClosure> closures = q.getResultList();
		if (closures != null && !closures.isEmpty()) {
			Calendar compareDate = Calendar.getInstance();
			compareDate.setTime(closures.get(0).getClosureDate());
			if (compareDate.get(Calendar.YEAR) < today.get(Calendar.YEAR)) {
				compareDate.add(Calendar.MONTH, 1);
				return compareDate;
			} else if (compareDate.get(Calendar.MONTH) < today.get(Calendar.MONTH)) {
				compareDate.add(Calendar.MONTH, 1);
				return compareDate;
			} else if (compareDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
				return today;
			} else
				return null;
		} else {
			today.add(Calendar.MONTH, -1);
			return today;
		}

	}

	public String getRecoverPeriod() {
		if (this.selectedProject != null) {
			Calendar nextRecover = recoverCloseDate();
			int period = nextRecover != null ? nextRecover.get(Calendar.MONTH) : -1;
			if (period != -1) {
				return RecoverClosure.MONTHS[period];
			} else {
				return "";
			}
		}
		return "";
	}

	public Project getInstanceMaker(MakerChecker makerChecker) {
		Object aux = new MakerCheckerHome().getInstance(makerChecker);
		if (aux instanceof Project) {
			Project project = (Project) aux;
			project = getEntityManager().merge(project);
			return project;
		}
		return null;
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

	public HtmlExtendedDataTable getPreliquidationTableInvoiceBind() {
		return preliquidationTableInvoiceBind;
	}

	public void setPreliquidationTableInvoiceBind(HtmlExtendedDataTable preliquidationTableInvoiceBind) {
		this.preliquidationTableInvoiceBind = preliquidationTableInvoiceBind;
	}

	public String getPreliquidationTableInvoiceState() {
		return preliquidationTableInvoiceState;
	}

	public void setPreliquidationTableInvoiceState(String preliquidationTableInvoiceState) {
		this.preliquidationTableInvoiceState = preliquidationTableInvoiceState;
	}

	/**
	 * Recibe la fecha inicio del concepto y la cantidad de meses que debe
	 * devolverse el sistema para imprimir en la factura el periodo facturado.
	 * 
	 * @param iniPeriod
	 * @param monthLess
	 * @return mes/año
	 */
	@SuppressWarnings("deprecation")
	public String showMonth(Date iniPeriod, int monthLess) {
		String mes = "Ene,Feb,Mar,Abr,May,Jun,Jul,Ago,Sep,Oct,Nov,Dic";
		String[] meses = mes.split(",");
		Calendar dateToReturn = Calendar.getInstance();
		dateToReturn.setTime(iniPeriod);
		int year = 0;
		dateToReturn.add(Calendar.MONTH, -monthLess);
		year = dateToReturn.getTime().getYear() + 1900;
		return (String) meses[dateToReturn.getTime().getMonth()] + "/" + year;
	}

	/**
	 * Metodo que itera sobre cada uno de los InvoiceConcept de una lista, y los
	 * analiza dependiendo del tipo, si es un concepto de carga masiva, este se
	 * agregara a un hash<String,InvoiceConcept> donde el key sera el nombre del
	 * concepto, y si es un Concepto de Interes a un Concepto normal de la
	 * aplicación, se agregara a un hash<Concept,InvoiceConcept> donde el key
	 * sera el concepto de la invoiceConcept, en caso de que al iterar por la
	 * lista ya existe un hash con el mismo key, se sumaran los balance,
	 * calculated_cost, calculated_tax...
	 */
	public List<InvoiceConcept> groupInvoiceConceptInterest(List<InvoiceConcept> originalList) {
		// logs.log.debug("Entra a groupInvoiceConceptInterest con una lista de tamaño: "
		// + originalList.size() + " para la factura: " +
		// originalList.get(0).getInvoice().getId());
		List<InvoiceConcept> listgroupedInvoice = new ArrayList<InvoiceConcept>();
		for (int i = 0; i < originalList.size(); i++) {
			InvoiceConcept invocon = (InvoiceConcept) originalList.get(i).clone();
			// logs.log.debug("se esta evaluando el invoiceConcept: " +
			// originalList.get(i).getId());
			if (invocon.getInvoiceConceptType() != InvoiceConcept.TYPE_INTEREST) {
				// logs.log.debug("Esta InvoiceConcept no es de Tipo Intéres");
				listgroupedInvoice.add(invocon);
			} else if (invocon.getInvoiceConceptParent().getConcept().getBulkLoad()) {
				// logs.log.debug("Este InvoiceConcept es de tipo intéres de un concepto de carga masiva.");
				if (!invocon.getConcept().getName().endsWith("*"))
					invocon.getConcept().setName(invocon.getConcept().getName() + "*");
				InvoiceConcept invocon2 = hashInvoconBulkLoad.get(invocon.getConcept().getName()) == null ? new InvoiceConcept() : hashInvoconBulkLoad.get(invocon.getConcept().getName());
				// logs.log.debug("se listaran los key de los has creados hasta el momento.");
				// for(Entry<?, InvoiceConcept> hashIterarted :
				// hashInvoconBulkLoad.entrySet()){
				// //logs.log.debug(hashIterarted.getKey());
				// InvoiceConcept inv = hashIterarted.getValue();
				// //logs.log.debug("este contiene un invocon con los siguientes datos: "
				// + inv.getBalance() + " " + inv.getCalculatedCosts() + " " +
				// inv.getCalculatedTax() + inv.getConcept());
				// }
				// System.out.println("elinvocon que se trae del hash es: " +
				// hashInvoconBulkLoad.get(invocon.getConcept().getName()) +
				// "para esto se buscon con el key: " +
				// invocon.getConcept().getName());
				// se llama al metodo evaluatorInvoiceConcept
				evaluatorInvoiceConcept(invocon, invocon2);
				hashInvoconBulkLoad.put(invocon2.getConcept().getName(), invocon2);

			} else {
				// logs.log.debug("Este InvoiceConcept No es de tipo intéres de un concepto de carga masiva.");
				InvoiceConcept invocon2 = hashInvoconNormal.get(invocon.getConcept()) == null ? new InvoiceConcept() : hashInvoconNormal.get(invocon.getConcept());
				// logs.log.debug("se listaran los key de los has creados hasta el momento.");
				// for(Entry<?, InvoiceConcept> hashIterarted :
				// hashInvoconBulkLoad.entrySet()){
				// //logs.log.debug(hashIterarted.getKey());
				// InvoiceConcept inv = hashIterarted.getValue();
				// //logs.log.debug("este contiene un invocon con los siguientes datos: "
				// + inv.getBalance() + " " + inv.getCalculatedCosts() + " " +
				// inv.getCalculatedTax() + inv.getConcept());
				// }
				// System.out.println("elinvocon que se trae del hash es: " +
				// hashInvoconNormal.get(invocon.getConcept()) +
				// "para esto se buscon con el key: " + invocon.getConcept() +
				// " y el nombre del concepto es: " +
				// invocon.getConcept().getName());
				// logs.log.debug("el invocon es: " +
				// hashInvoconNormal.get(invocon.getConcept()));
				evaluatorInvoiceConcept(invocon, invocon2);
				hashInvoconNormal.put(invocon.getConcept(), invocon2);
			}
		}
		// logs.log.debug("hasta este momento la lista a retornar es de tamaño: "
		// + listgroupedInvoice.size());
		fromHastToList(hashInvoconBulkLoad, listgroupedInvoice);
		// logs.log.debug("despùes del primer fromHastToList, la lista a retornar es de tamaño: "
		// + listgroupedInvoice.size());
		fromHastToList(hashInvoconNormal, listgroupedInvoice);
		hashInvoconBulkLoad.clear();
		hashInvoconNormal.clear();
		return listgroupedInvoice;
	}

	/**
	 * Evalua dos InvoiceConcept, y setea al invocon2, los valores de balance,
	 * calculated_cost, calculated_tax... en caso de que el segundo
	 * invoiceConcept no tenga un concepto asociado (sea un nuevo
	 * InvoiceConcept), en caso contrario se suman estos valores a los ya
	 * existentes.
	 * 
	 * @param invocon
	 * @param invocon2
	 * @return InvoiceConcept con los valores actualizados del balance,
	 *         calculated_cost, calculated_tax...
	 */
	private InvoiceConcept evaluatorInvoiceConcept(InvoiceConcept invocon, InvoiceConcept invocon2) {
		// se declaran las variables que contendran los valores del
		// invoiceConcept, que van a ser setteados o sumados a los del invocon
		// que se guardara en el hash.
		Double oldBalance, oldCalculatedCost, oldCalculatedTax;
		// se obtienen los valores del invoiceConcept, que van a ser setteados o
		// sumados a los del invocon que se guardara en el hash.
		oldBalance = invocon.getBalance();
		oldCalculatedCost = invocon.getCalculatedCost();
		oldCalculatedTax = invocon.getCalculatedTax();
		// logs.log.debug("Este Invocon tiene asignado el concepto: " +
		// invocon2.getConcept());
		if (invocon2.getConcept() == null) {
			// logs.log.debug("se crea un nuevo invocon: " + oldBalance);
			/*
			 * si entra a este condicional es por que se creo un nuevo
			 * InvoiceConcept asì que se settean los valores que necesitamos del
			 * InvoiceConcept, y se agrega al hashMap.
			 */
			invocon2.setIniPeriodDate(invocon.getIniPeriodDate());
			invocon2.setLastLiquidationDate(invocon.getLastLiquidationDate());
			invocon2.setInvoice(invocon.getInvoice());
			invocon2.setConcept(invocon.getConcept());
			invocon2.setBalance(oldBalance);
			invocon2.setCalculatedCost(oldCalculatedCost);
			invocon2.setCalculatedTax(oldCalculatedTax);
			invocon2.setInvoiceConceptType(invocon.getInvoiceConceptType());
			invocon2.setInvoiceConceptParent(invocon.getInvoiceConceptParent());
		} else {
			// logs.log.debug("ya existe un invocon se sumaran los datos: " +
			// oldBalance);
			/*
			 * si entra a este condicional es por que ya existe un hash con el
			 * mismo key asì que se settean la suma de los valores que ya tenia
			 * el invoiceConcept del hash, mas los del invoiceConcept de la
			 * Lista.
			 */
			if (invocon2.getIniPeriodDate().after(invocon.getIniPeriodDate()))
				invocon2.setIniPeriodDate(invocon.getIniPeriodDate());
			if (invocon.getLastLiquidationDate().after(invocon2.getLastLiquidationDate()))
				invocon2.setLastLiquidationDate(invocon.getLastLiquidationDate());
			invocon2.setBalance(invocon2.getBalance() + oldBalance);
			invocon2.setCalculatedCost(invocon2.getCalculatedCost() + oldCalculatedCost);
			invocon2.setCalculatedTax(invocon2.getCalculatedTax() + oldCalculatedTax);
		}
		// logs.log.debug("oldBalance: " + oldBalance);
		// logs.log.debug("oldCalculatedCost" + oldCalculatedCost);
		// logs.log.debug("oldCalculatedTax" + oldCalculatedTax);
		// logs.log.debug("newBalance: " + invocon2.getBalance());
		// logs.log.debug("newCalculatedCost" + invocon2.getCalculatedCost());
		// logs.log.debug("newCalculatedTax" + invocon2.getCalculatedTax());

		return invocon2;
	}

	/**
	 * Función que recorre un hash<?,InvoiceConcept>, y devuelve una lista de
	 * InvoiceConcept.
	 * 
	 * @param hash
	 * @return List<InvoiceConcept>
	 */
	public List<InvoiceConcept> fromHastToList(HashMap<?, InvoiceConcept> hash, List<InvoiceConcept> listToReturn) {
		// logs.log.debug("fromHastToList Start: ");
		for (Entry<?, InvoiceConcept> hashIterarted : hash.entrySet()) {
			listToReturn.add(hashIterarted.getValue());
		}
		// logs.log.debug("el nuevo tamaño de la lista que sera utilizada para la creación de pdf es: "
		// + listToReturn.size());
		return listToReturn;
	}

	public String nombreConceptoParaComparacionConceptos(InvoiceConcept invoiceConcept) {
		return tools.nameInvoiceConcept(invoiceConcept.getInvoiceConceptType(), invoiceConcept.getConcept().getName());
	}

	/**
	 * Este método crea una fuente del tamaño que se el indique
	 * 
	 * @param selected
	 *            es el tamaño de la fuente, el valor esta entre 1 y 8
	 * @return retorna un objeto de tipo Font con la fuente deseada
	 */
	public Font selectedFont(int selected) {
		try {
			String fontFile = path + "Font/TAHOMA.TTF";
			BaseFont bf = BaseFont.createFont(fontFile, BaseFont.IDENTITY_H, true);
			switch (selected) {
			case 1: {
				Font font1 = new Font(bf, 7, Font.NORMAL);
				return font1;
			}

			case 2: {
				Font font2 = new Font(bf, 8, Font.BOLD);
				return font2;
			}
			case 3: {
				Font font3 = new Font(bf, 8, Font.BOLD, new BaseColor(255, 255, 255));
				return font3;
			}
			case 4: {
				Font font4 = new Font(bf, 8, Font.NORMAL);
				return font4;
			}
			case 5: {
				Font font5 = new Font(bf, 7, Font.NORMAL);
				return font5;
			}
			case 6: {
				Font font6 = new Font(bf, 9, Font.NORMAL);
				return font6;
			}
			case 7: {

				Font font7 = new Font(bf, 11, Font.BOLD);
				return font7;
			}
			case 8: {

				Font font8 = new Font(bf, 5, Font.BOLD);
				return font8;
			}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Manipula el PDF que se encuentre en la ruta src y le agrega la palabra
	 * Original o Copia, y lo almacena en la ruta de destino dest
	 * 
	 * @param src
	 *            ruta donde se encuentra el pdf Original
	 * @param dest
	 *            ruta donde se encuentra el PDF resultante
	 * @param isOriginal
	 *            indica si el pdf es Original o copia
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void stampPDF(String src, String dest, boolean isOriginal) throws IOException, DocumentException {
		if (isOriginal)
			dest = dest.replaceAll("Origins", "Original");
		PdfReader reader = new PdfReader(src);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest), '\0', true);
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			PdfContentByte canvas = stamper.getOverContent(i);
			if (isOriginal) {
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase("ORIGINAL", selectedFont(FONT5)), 290, 20, 0);
			} else {
				ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase("COPIA", selectedFont(FONT5)), 290, 20, 0);
			}
			ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(i + " De " + reader.getNumberOfPages(), selectedFont(FONT1)), 541, 20, 0);
		}
		stamper.close();
		if (isOriginal) {
			File fichero = new File(src);
			fichero.delete();
		}
	}

	public boolean validateInvoiceBeforeApprove(Invoice bill) {

		boolean stillHasInvoiceConcept = true;
		for (InvoiceConcept invoCon : bill.getInvoiceConcepts()) {
			if (invoCon.getCalculatedCost() == 0.00) {

				Query q = getEntityManager().createNativeQuery("DELETE FROM invoice_concept where id = ?");
				q.setParameter(1, invoCon.getId());
				getEntityManager().joinTransaction();
				q.executeUpdate();
				q = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM invoice_concept where invoice = ?");
				q.setParameter(1, bill.getId());
				BigInteger countInvoCon = (BigInteger) q.getResultList().get(0);
				if (countInvoCon.intValue() == 0) {
					stillHasInvoiceConcept = false;
					Query q2 = getEntityManager().createNativeQuery("DELETE FROM invoice where id = ?");
					q2.setParameter(1, bill.getId());
					q2.executeUpdate();
					getEntityManager().joinTransaction();
					break;
				}
			}
		}
		return stillHasInvoiceConcept;

	}

	public HtmlExtendedDataTable getChartTableConceptBind() {
		return chartTableConceptBind;
	}

	public void setChartTableConceptBind(HtmlExtendedDataTable chartTableConceptBind) {
		this.chartTableConceptBind = chartTableConceptBind;
	}

	public String getChartTableInvoiceConceptState() {
		return chartTableInvoiceConceptState;
	}

	public void setChartTableInvoiceConceptState(String chartTableInvoiceConceptState) {
		this.chartTableInvoiceConceptState = chartTableInvoiceConceptState;
	}

	/**
	 * This function prints a message in log file.
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	private void log(Level level, String message) {
		BillingTools.printLog(ProjectHome.class, level, message);
	}

}