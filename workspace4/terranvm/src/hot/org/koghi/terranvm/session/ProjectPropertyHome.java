package org.koghi.terranvm.session;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.bean.DownloadAttachment;
import org.koghi.terranvm.entity.BillingResolution;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.ConcepTemplate;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.ConceptRetentionRateAccount;
import org.koghi.terranvm.entity.ConsecutiveAccountsBilling;
import org.koghi.terranvm.entity.ConsecutiveCollectionAccount;
import org.koghi.terranvm.entity.ConsecutiveCreditNotes;
import org.koghi.terranvm.entity.ContractType;
import org.koghi.terranvm.entity.ContributionModule;
import org.koghi.terranvm.entity.CounterConfigurationTamplate;
import org.koghi.terranvm.entity.CounterTemplateHasRetentionRateAccount;
import org.koghi.terranvm.entity.File;
import org.koghi.terranvm.entity.Increased;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.IpcAccumulated;
import org.koghi.terranvm.entity.IpcMonthly;
import org.koghi.terranvm.entity.IpcYearly;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.MinimunWage;
import org.koghi.terranvm.entity.ObjectOfContract;
import org.koghi.terranvm.entity.PDFFile;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RentableUnitContribution;
import org.koghi.terranvm.entity.RetentionRate;
import org.koghi.terranvm.entity.RetentionRateAccount;
import org.koghi.terranvm.entity.Sales;
import org.koghi.terranvm.entity.SystemVariable;
import org.koghi.terranvm.entity.TaxConfiguration;
import org.richfaces.component.html.HtmlExtendedDataTable;
import org.richfaces.model.selection.Selection;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

@Name("projectPropertyHome")
public class ProjectPropertyHome extends EntityHome<ProjectProperty> {

	/**
	 * login-required
	 * 
	 */

	public String messagedate = "";
	public String messagedateEndConcept = "";
	private String editMessagedateEndConcept = "";
	// public Integer consecutiveTermSheet = 0;

	public String fromTab;
	private static final long serialVersionUID = 1L;
	@In(create = true)
	BusinessEntityHome businessEntityHome;
	@In(create = true)
	ContractTypeHome contractTypeHome;
	@In(create = true)
	ProjectHome projectHome;
	@In(create = true)
	RealPropertyHome realPropertyHome;

	private Selection systemVaribleSelection;
	private Selection systemVaribleSelectionIncrease;

	private Selection systemVaribleSelectionIncreaseEdit;

	private String tableState;
	private String tableInterestRetentionRateAccountState;
	@DataModel
	private List<RetentionRateAccount> counts;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tablesSystemVAriableBind;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tablesSystemVAriableIncreaseBind;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tablesSystemVAriableBindEdit;
	private String tableStateClause;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tablesSystemVAriableIncreaseBindEdit;

	private Boolean approved;
	private Boolean draftBefore; // Bandera para saber si antes de guardar
	// la
	// hoja de terminos era un borrador
	private boolean terminated;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableClause;

	private Concept concept;
	private Increased increased;
	private Increased increasedEdit;

	private Concept conceptSelected;
	private String linkPDF;
	private String summaryUrl;
	private String mainUrl;

	private List<ContractType> contractTypeList;
	private List<ObjectOfContract> objectOfContractList;

	// CONCEPT
	private int useContributionModule;
	private List<ContributionModule> contributionModuleList;
	private boolean errorOnConceptExpresion;

	// validacion Incremento
	private boolean errorOnConceptExpresionIncreased;
	private List<RealProperty> realPropertyList;
	private List<RentableUnit> rentableUnitList;
	private List<CounterConfigurationTamplate> templateHasRetentionRateAccounts;
	private CounterConfigurationTamplate templateHasRetentionRateAccount;

	List<RetentionRateAccount> RetentionRateAccounts = new ArrayList<RetentionRateAccount>();
	// Lista de Retenciones.
	ArrayList<Object[]> listTaxConfiguration;

	// Lista de Retenciones (Subcategorias) de tipo Cuentas de Orden
	// Deudora.
	ArrayList<Object[]> listInterestAccountsConfiguration;

	// Lista de Impuesto al Timbre.
	ArrayList<Object[]> listStamptaxConfiguration;

	// Contribution Module
	private Boolean isContributionModule = false;

	// Incremento
	private boolean isInCreased;
	private boolean isInCreasedEdit;

	// Tabla de cuentas contables
	private ArrayList<ConceptRetentionRateAccount> accotuntsItems;
	private Selection selectionAccount;
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableAccountBind;

	/**
	 * variable para mostrar los valores
	 */
	NumberFormat formatter = new DecimalFormat("#,###.00");

	/**
	 * Objeto de Retencion
	 * 
	 * @return
	 */
	private RetentionRateAccount retentionRateAccount;

	private Integer retention;

	private boolean updateRetention = true;
	/**
	 * Resultado de la validacion de expression del concepto
	 */
	private String validateExpression;
	/**
	 * Resultado de la validacion de expression del Incremento del concepto
	 */
	private String validateExpressionIncreased;

	private List<Concept> conceptList;

	private List<BillingResolution> billingResolutionList;

	private List<ConsecutiveCollectionAccount> collectionAccountList;

	@In(required = false)
	public String projectFilter;

	@In(required = false)
	public List<SelectItem> projectsFilter;

	private List<Concept> conceptosSinCargaMasiva;
	private List<Integer> retentionsToAsigne = new ArrayList<Integer>();
	private List<Integer> retentionsToAsigneInteres = new ArrayList<Integer>();

	/*
	 * Variables para la manipulación de archivos en las hojas de términos
	 */
	private InputStream pdfsAttach;
	private String nameFile;
	private Integer fileSize;

	// Id del MakerChecker cuando se encuentra en aprobación
	private int makerCheckerId;

	/**
	 * This function prints a message in log file.
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	private void log(Level level, String message) {
		BillingTools.printLog( ProjectPropertyHome.class , level , message);
	}

	public boolean isErrorOnConceptExpresionIncreased() {
		return errorOnConceptExpresionIncreased;
	}

	public void setErrorOnConceptExpresionIncreased(boolean errorOnConceptExpresionIncreased) {
		this.errorOnConceptExpresionIncreased = errorOnConceptExpresionIncreased;
	}

	public String getValidateExpressionIncreased() {
		return validateExpressionIncreased;
	}

	public void setValidateExpressionIncreased(String validateExpressionIncreased) {
		this.validateExpressionIncreased = validateExpressionIncreased;
	}

	public HtmlExtendedDataTable getTablesSystemVAriableBindEdit() {
		return tablesSystemVAriableBindEdit;
	}

	public void setTablesSystemVAriableBindEdit(HtmlExtendedDataTable tablesSystemVAriableBindEdit) {
		this.tablesSystemVAriableBindEdit = tablesSystemVAriableBindEdit;
	}

	public HtmlExtendedDataTable getTablesSystemVAriableIncreaseBind() {
		return tablesSystemVAriableIncreaseBind;
	}

	public void setTablesSystemVAriableIncreaseBind(HtmlExtendedDataTable tablesSystemVAriableIncreaseBind) {
		this.tablesSystemVAriableIncreaseBind = tablesSystemVAriableIncreaseBind;
	}

	public HtmlExtendedDataTable getTablesSystemVAriableBind() {
		return tablesSystemVAriableBind;
	}

	public void setTablesSystemVAriableBind(HtmlExtendedDataTable tablesSystemVAriableBind) {
		this.tablesSystemVAriableBind = tablesSystemVAriableBind;
	}

	public String getFromTab() {
		return fromTab;
	}

	public void setFromTab(String fromTab) {
		this.fromTab = fromTab;
	}

	public Selection getSystemVaribleSelection() {
		return systemVaribleSelection;
	}

	public void setSystemVaribleSelection(Selection systemVaribleSelection) {
		this.systemVaribleSelection = systemVaribleSelection;
	}

	public Selection getSystemVaribleSelectionIncrease() {
		return systemVaribleSelectionIncrease;
	}

	public void setSystemVaribleSelectionIncrease(Selection systemVaribleSelectionIncrease) {
		this.systemVaribleSelectionIncrease = systemVaribleSelectionIncrease;
	}

	public String nameTemplate;

	public String getNameTemplate() {
		return nameTemplate;
	}

	public void setNameTemplate(String nameTemplate) {
		this.nameTemplate = nameTemplate;
	}

	public String getValidateExpression() {
		return validateExpression;
	}

	public void setValidateExpression(String validateExpression) {
		this.validateExpression = validateExpression;
	}

	public void setRetentionRateAccount(RetentionRateAccount retentionRateAccount) {
		if (this.retentionRateAccount != null) {
			this.updateRetention = false;
		}
		log(Level.INFO, "Settiando Retencion: " + retentionRateAccount.getId());
		this.retentionRateAccount = retentionRateAccount;
	}

	public void setListTaxConfiguration(ArrayList<Object[]> listTaxConfiguration) {
		this.listTaxConfiguration = listTaxConfiguration;
	}

	public void setListStamptaxConfiguration(ArrayList<Object[]> listStamptaxConfiguration) {
		this.listStamptaxConfiguration = listStamptaxConfiguration;
	}

	public Boolean getIsContributionModule() {
		// if (getIsContributionModule() != null &&
		// concept.getContributionModule().getId() != 0)
		// this.isContributionModule = new Boolean(false);
		return this.isContributionModule;

		// return isContributionModule;

	}

	public void setIsContributionModule(Boolean isContributionModule) {
		this.isContributionModule = isContributionModule;
	}

	public void setProjectPropertyId(Integer id) {
		setId(id);
	}

	public Integer getProjectPropertyId() {
		return (Integer) getId();
	}

	public boolean getIsInCreased() {
		return this.isInCreased;

	}

	public void changeisincreased() {
		this.isInCreased = this.concept.getFixedValue() != null ? this.isInCreased : false;
	}

	public void changeisincreasedEdit() {
		this.isInCreasedEdit = this.conceptSelected.getFixedValue() != null ? this.isInCreasedEdit : false;
	}

	/**
	 * Metodo que Muestra mensajes Grow de Manera generica
	 */
	public void showMessagesGrow(String type, String message) {

		if (this.conceptSelected.getFixedValue() != null) {
			this.conceptSelected.setIncreased(this.increasedEdit);
		}
		if (!this.isInCreasedEdit)
			this.conceptSelected.setIncreased(null);

		if (!this.isContributionModule) {
			this.conceptSelected.setContributionModule(null);
		}
		this.validateExpression = "";
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Concepto Editado!", "El concepto se edito correctamente, pendiente guardar esta Hoja de Terminos"));
	}

	public void setIsInCreased(boolean isInCreased) {
		if (isInCreased) {
			this.increased = new Increased();
		} else {
			this.increased = null;
		}

		this.isInCreased = isInCreased;
	}

	@Override
	protected ProjectProperty createInstance() {
		ProjectProperty projectProperty = new ProjectProperty();
		return projectProperty;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if (this.fromTab != null) {
			if (fromTab.equals("billed")) {
				BusinessEntity billed = businessEntityHome.getDefinedInstance();
				if (billed != null) {
					getInstance().setBilled(billed);
				}
			} else if (fromTab.equals("biller")) {
				BusinessEntity biller = businessEntityHome.getDefinedInstance();
				if (biller != null) {
					getInstance().setBiller(biller);
				}
			}
		}
		ContractType contractType = contractTypeHome.getDefinedInstance();
		if (contractType != null) {
			getInstance().setContractType(contractType);
		}
		Project project = projectHome.getDefinedInstance();
		if (project != null) {
			getInstance().setProject(project);
		}
		RealProperty realProperty = realPropertyHome.getDefinedInstance();
		if (realProperty != null) {
			getInstance().setRealProperty(realProperty);
		}

		// Se actualiza según el combo
		List<ContractType> list = this.getContractTypeList();
		if (list != null && list.size() > 0 && this.getInstance().getContractType() == null)
			this.getInstance().setContractType(list.get(0));
		realPropertyList();
		if (this.rentableUnitList != null && this.rentableUnitList.size() > 0 && this.getInstance().getRentableUnit() == null && this.instance.getObjectOfContract() != null && this.instance.getObjectOfContract().getId() == 2 && this.getInstance().getContractType() != null && this.getInstance().getContractType().getId() == ContractType.LEASE_TYPE)
			this.getInstance().setRentableUnit(this.rentableUnitList.get(0));

		if (draftBefore == null)
			draftBefore = (getInstance().getStatus() == ProjectProperty.STATUS_DRAFT);
	}

	public boolean isWired() {
		if ((this.instance.getObligationsStartDate() != null && this.instance.getSubscriptionDate() != null) && this.instance.getObligationsStartDate().before(this.instance.getSubscriptionDate()) == true)
			return false;
		if (getInstance().getObjectOfContract().getId() != 1 && getInstance().getBilled() == null)
			return false;
		if (getInstance().getBiller() == null)
			return false;
		if (getInstance().getProject() == null)
			return false;
		if (this.instance.getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT) {
			if (this.instance.getRentableUnit() == null)
				return false;
		}
		return true;
	}

	public ProjectProperty getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Concept> getConcepts() {
		return getInstance() == null ? null : getInstance().getConcepts();
	}

	// public void createTermSheetPDF() {
	public String createTermSheetPDF() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		if (this.instance != null) {
			Document document = new Document(PageSize.LETTER);
			try {
				String filePDF = "tmp/termSheet/term_sheet" + this.instance.getId() + ".pdf";
				// String fileIMAGE2 =
				// "img/pie_Terranum.jpg";
				// String fileIMAGE =
				// "img/log_Terranum.jpg";

				String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
				String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
				path = path.substring(0, path.lastIndexOf("/")) + "/";
				this.linkPDF = filePDF;

				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + filePDF));
				com.itextpdf.text.List mainList = new com.itextpdf.text.List(true, 30);
				com.itextpdf.text.List subList = new com.itextpdf.text.List(false, 15);

				document.setMargins(40, 46, 160, 80);
				// Rectangle rct = new Rectangle(40, 46,
				// 160, 60);
				// Definimos un nombre y un tamaño para
				// el PageBox los nombres
				// posibles son: “crop”, “trim”, “art”
				// and “bleed”.
				// writer.setBoxSize("art", rct);
				HeaderFooter event = new HeaderFooter(this.instance);
				writer.setPageEvent(event);
				document.open();
				document.newPage();
				document.addTitle("TERRANVM INMOBILIARIA");

				document.addCreator("Koghi I.T");
				document.addAuthor("Koghi I.T");

				// document.add(new
				// Paragraph("Hoja de Términos    " +
				// this.instance.getId(), new
				// Font(FontFamily.HELVETICA, 17,
				// Font.BOLD, new BaseColor(0, 0, 0))));
				if (this.instance.getConsecutiveTermsSheet() != null)
					document.add(new Paragraph("Consecutivo Hoja de Terminos No : " + this.instance.getProject().getProjectPrefix() + "-" + this.instance.getConsecutiveTermsSheet()));
				else
					document.add(new Paragraph("Consecutivo Hoja de Terminos No : " + this.instance.getProject().getProjectPrefix()));

				document.add(new Paragraph("Version No : " + this.instance.getVersion()));

				// document.add(new Paragraph("\n" +
				// ((this.instance.getSubjectContrat()
				// == null) ? "" :
				// this.instance.getSubjectContrat())));
				StyleSheet styleSheet = new StyleSheet();
				// styleSheet.loadStyle("ol", "leading",
				// "16,0");
				createHTMLTemp(this.instance.getSubjectContrat(), path + "tmp/termSheet/htmlTmp.html");
				ArrayList<Element> p = HTMLWorker.parseToList(new FileReader(path + "tmp/termSheet/htmlTmp.html"), styleSheet);
				for (int k = 0; k < p.size(); ++k) {
					document.add(p.get(k));
				}

				document.newPage();
				document.add(new Paragraph("ANEXOS", new Font(FontFamily.HELVETICA, 17, Font.BOLD, new BaseColor(0, 0, 0))));
				document.add(new Paragraph("Tipo de Contrato: " + this.instance.getContractType().getName(), new Font(FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(0, 0, 0))));

				document.add(new Paragraph(" "));

				subList = new com.itextpdf.text.List(false, 15);
				mainList.add(new ListItem("Información del Cliente"));
				log(Level.INFO,".............................................................. INFORMACION DEL CLIENTE");
				subList.add("Razón Social: " + (this.instance.getBilled() == null ? " " : this.instance.getBilled().getNameBusinessEntity()));
				subList.add("NIT: " + (this.instance.getBilled() == null ? " " : this.instance.getBilled().getIdNumber()) + " - " + this.instance.getBilled().getVerificationNumber());
				subList.add("Direccion: " + (this.instance.getBilledAddress() == null ? " " : this.instance.getBilledAddress().toString()));
				subList.add("Telefono: " + (this.instance.getPhoneNumber() == null ? " " : this.instance.getPhoneNumber().getNumber()));

				if (this.instance.getContractType().getId() == 1) {
					subList.add("Fecha de inicio de obligaciones: " + (this.instance.getObligationsStartDate() == null ? "" : dateFormat.format(this.instance.getObligationsStartDate())));
				}
				subList.add("Prórroga automática: " + (this.instance.isAutomaticExtension() ? "Si" : "No"));
				mainList.add(subList);

				// if
				// (this.instance.getContractType().getId()
				// ==
				// ContractType.LEASE_TYPE &&
				// this.instance.getRealProperty() !=
				// null) {
				if (this.instance.getObjectOfContract().getId() != ObjectOfContract.OBJECT_SERVICE && this.instance.getRealProperty() != null) {
					subList = new com.itextpdf.text.List(false, 15);
					mainList.add(new ListItem("Información del Inmueble"));
					String nameRealProperty = "";
					nameRealProperty += this.instance.getRealProperty().getNameProperty();

					if (this.instance.getRentableUnit() != null) {
						nameRealProperty += "    Nombre Unidad Arrendable: " + this.instance.getRentableUnit().getName();
					}
					subList.add("Nombre: " + nameRealProperty);
					subList.add("Fecha inicio de modificaciones: " + (this.instance.getStartDateChanges() == null ? " " : (dateFormat.format(this.instance.getStartDateChanges()))));
					subList.add("Destinación: " + (this.instance.getPurpose() == null ? "" : (this.instance.getPurpose())));

					mainList.add(subList);
				}

				if (this.instance.getSubscriptionDate() != null) {
					subList = new com.itextpdf.text.List(false, 15);
					mainList.add(new ListItem("Fecha de Inicio del Contrato"));
					subList.add(dateFormat.format(this.instance.getSubscriptionDate()) + "");
					mainList.add(subList);
				}

				if (this.instance.getExpirationDate() != null) {
					subList = new com.itextpdf.text.List(false, 15);
					mainList.add(new ListItem("Fecha de Caducidad del Contrato"));
					subList.add(dateFormat.format(this.instance.getExpirationDate()) + "");
					mainList.add(subList);
				}

				document.add(mainList);
				Paragraph tituloConceptos = new Paragraph("5.     Conceptos :");
				document.add(tituloConceptos);
				com.itextpdf.text.List conceptList = new com.itextpdf.text.List(false, 15);

				for (Concept concept : this.getInstance().getConcepts()) {

					document.add(new Paragraph("        -  Nombre:  " + concept.getName()));
					document.add(new Paragraph("        -  Descripcion: " + concept.getPrintDescription() == null ? " " : concept.getPrintDescription()));
					document.add(new Paragraph("        -  Formula:  " + concept.getExpression()));
					document.add(new Paragraph("        -  Numero Periodos: " + concept.getNumberPeriods()));
					document.add(new Paragraph("        -  Periodicidad:  " + concept.getPeriodicity() + " " + (concept.getPeriodicityType() != null ? Concept.PERIODS_TYPE[concept.getPeriodicityType() - 1][1] : "")));
					document.add(new Paragraph("        -  Valor:     $" + this.getConceptValue(concept)));
					document.add(new Paragraph("        -  Fecha Inicio:  " + dateFormat.format(concept.getStartDate())));
					document.add(new Paragraph("        -  Fecha finalizacion:  " + dateFormat.format(concept.getEndDateView())));
					if (concept.isEarlyPayment())
						document.add(new Paragraph("        -  Cobro Anticipado:  Si"));
					else
						document.add(new Paragraph("        -  Cobro Anticipado:  No"));
					if (concept.getStamptax() != null)
						document.add(new Paragraph("        -  Impuesto al Timbre: Si "));
					else
						document.add(new Paragraph("        -  Impuesto al Timbre: No "));

					if (concept.isInterestArrears())
						document.add(new Paragraph("        -  Interes de Mora:  Si"));
					else
						document.add(new Paragraph("        -  Interes de Mora:  No"));
					if (concept.getTax() != null)
						document.add(new Paragraph("        -  IVA:  " + concept.getTax().getName()));
					else
						document.add(new Paragraph("        -  IVA:  N/A"));

					document.add(new Paragraph("\n\n"));
				}

				document.add(conceptList);
				document.close();
			} catch (Exception e) {
				e.printStackTrace();
				document.close();
			}
		}
		DownloadAttachment downloadAttachment = new DownloadAttachment();
		return downloadAttachment.download(this.linkPDF);
	}

	public static void createHTMLTemp(String subjectContrat, String path) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(path);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(subjectContrat);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public String getLinkPDF() {

		return this.linkPDF;
	}

	public void setLinkPDF(String linkPDF) {
		this.linkPDF = linkPDF;
	}

	// public void createTermSheetPDF2(ProjectProperty termsheet) {
	public String createTermSheetPDF2(ProjectProperty termsheet) {
		if (termsheet != null) {
			this.setInstance(termsheet);
			// this.createTermSheetPDF();
			return this.createTermSheetPDF();
		}
		return "";
	}

	@RequestParameter
	public Integer idTermshee;

	public String createTermSheetPDF2() {
		ProjectProperty termsheet = getEntityManager().find(ProjectProperty.class, idTermshee);
		if (termsheet != null) {
			this.setInstance(termsheet);
			// this.createTermSheetPDF();
			return this.createTermSheetPDF();
		}
		return "";
	}

	@Override
	public String update() {
		String disabledMessage = this.disabledSaveTermSheetButton();
		if (this.objectOfContractList != null) {

			if (this.instance.getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY) {
				this.getInstance().setBilled(null);
			} else {
			}
		}
		if (!disabledMessage.isEmpty()) {
			@SuppressWarnings("deprecation")
			FacesMessages facesMessages = getFacesMessages();
			facesMessages.addFromResourceBundle(Severity.ERROR, "Falta completar la hoja de terminos", "");
			return "ERROR";
		}

		String res = "failed";
		this.concept = null;
		this.conceptSelected = null;
		this.instance.setConcepts(this.conceptList);
		if (getInstance().getStatus() == ProjectProperty.STATUS_APPROVED && this.getInstance().getStep() == 0) {
			new MakerCheckerHome().persistObject(getInstance(), getInstance().getProject());
			updatedMessage();
			res = "updated";
		} else if (getInstance().getStatus() == ProjectProperty.STATUS_DRAFT) {
			refreshFileAttachment();
			res = super.update();
		} else if ((getInstance().getStatus() == ProjectProperty.STATUS_APPROVED && this.getInstance().getStep() == 1)) {
			refreshFileAttachment();
			super.update();
			res = "updateCounter";
		} else if ((getInstance().getStatus() == ProjectProperty.STATUS_TERMINATED)) {
			new MakerCheckerHome().persistObject(getInstance(), getInstance().getProject());
			updatedMessage();
			res = "updated";
		}

		// Se actualizan conceptos para evitar excepción no session
		// en la
		// interfaz de ProjectProperty.xhtml
		setInstance(getEntityManager().find(ProjectProperty.class, getInstance().getId()));
		((Session) (getEntityManager().getDelegate())).refresh(getInstance());
		conceptList = null;
		for (Concept concept : getInstance().getConcepts()) {
			if (concept.getId() != 0)
				((Session) (getEntityManager().getDelegate())).refresh(concept);
		}

		return res;
	}

	/*
	 * @Override public String persist() { try{ ProjectProperty termSheet =
	 * getInstance(); //log(Level.INFO, "Elementos: "+termSheet.);
	 * termSheet.setStatus(ProjectProperty.STATUS_DRAFT);
	 * log(Level.INFO, "****** KKKClausulas: "+termSheet.getClauses());
	 * setInstance(termSheet); //return "persisted"; return super.persist();
	 * }catch(org.hibernate.validator.InvalidStateException ex){ InvalidValue[]
	 * iv= ex.getInvalidValues();
	 * //log(Level.INFO, "****** OJO ERROR DE validacio: "+iv[1]); ex.printStackTrace();
	 * return null;
	 * 
	 * } }
	 */

	@Override
	public String persist() {
		try {
			ProjectProperty termSheet = getInstance();
			termSheet.setStatus(ProjectProperty.STATUS_DRAFT);
			setInstance(termSheet);
			return super.persist();

		} catch (org.hibernate.validator.InvalidStateException ex) {
			InvalidValue[] iv = ex.getInvalidValues();
			log(Level.INFO, "****** OJO ERROR DE validacion: " + iv[0]);
			ex.printStackTrace();
			return null;

		}
	}

	public String StateDraft(ProjectProperty termsheet) {
		this.setInstance(termsheet);
		if (this.instance.getStatus() == 1)
			return "true";
		else
			return "false";
	}

	public String StateApprove(ProjectProperty termsheet) {
		this.setInstance(termsheet);
		if (this.instance.getStatus() == 2)
			return "true";
		else
			return "false";
	}

	public String StateTerminated(ProjectProperty termsheet) {
		this.setInstance(termsheet);
		if (this.instance.getStatus() == 3)
			return "true";
		else
			return "false";
	}

	public String StateTerminate(ProjectProperty termsheet) {
		this.setInstance(termsheet);
		if (this.instance.getStatus() == 4)
			return "true";
		else
			return "false";
	}

	public String checkCommercial(ProjectProperty termsheet) {
		this.setInstance(termsheet);
		if (this.instance.getStatus() == 2 || (new MakerCheckerHome().isObjectInMakerChecker(termsheet)))
			return "and";
		else
			return "or";
	}

	public String checkManager(ProjectProperty termsheet) {
		this.setInstance(termsheet);
		if (this.instance.getStatus() == ProjectProperty.STATUS_APPROVED && !(new MakerCheckerHome().isObjectInMakerChecker(termsheet))) {
			return "and";
		} else {
			return "or";
		}
	}

	public String checkAccountant(ProjectProperty termsheet) {
		this.setInstance(termsheet);

		if (this.instance.getStep() == 0 && this.projectPropertyListInApprove(termsheet) == false && this.instance.getStatus() == ProjectProperty.STATUS_APPROVED) {
			return "and";
		} else if (this.projectPropertyListInApprove(termsheet) == false) {
			return "or";
		} else
			return "";

	}

	public Boolean getDraftBefore() {
		return draftBefore;
	}

	public void setDraftBefore(Boolean draftBefore) {
		this.draftBefore = draftBefore;
	}

	public boolean isApproved() {
		if (approved == null) {
			if (getInstance().getStatus() == ProjectProperty.STATUS_APPROVED)
				approved = true;
			else
				approved = false;
		}

		return approved;
	}

	public void approveTermSheet() {

		if (approved) {
			getInstance().setStatus(ProjectProperty.STATUS_APPROVED);
		} else {
			getInstance().setStatus(ProjectProperty.STATUS_DRAFT);
		}
	}

	public boolean isDraft() {
		if (getInstance().getStatus() == ProjectProperty.STATUS_DRAFT) {
			this.getInstance().setVersion(0);
			return true;
		} else
			return false;
	}

	public boolean isTerminated() {
		if (getInstance().getStatus() == ProjectProperty.STATUS_TERMINATED) {
			this.terminated = true;
			this.approved = false;
		}

		else
			this.terminated = false;

		return this.terminated;
	}

	public boolean isTerminate() {
		if (getInstance().getStatus() == ProjectProperty.STATUS_EXPIRED)
			return true;

		else
			return false;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	public void approve() {
		ProjectProperty termSheet = getInstance();
		termSheet.setStatus(ProjectProperty.STATUS_APPROVED);
		setInstance(termSheet);
		setApproved(true);
	}

	public void terminate() {
		ProjectProperty termSheet = getInstance();
		termSheet.setStatus(ProjectProperty.STATUS_TERMINATED);
		setTerminated(true);
	}

	public boolean projectPropertyListInApprove(ProjectProperty projectProperty) {
		return (new MakerCheckerHome().isObjectInMakerChecker(projectProperty) || (projectProperty.getStep() == 1));
	}

	public void updateInstanceMaker(int makerCheckerId) {
		setInstance((ProjectProperty) new MakerCheckerHome().getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
		this.makerCheckerId = makerCheckerId;
	}

	public void incrementVersionConsecutive() {
		if (this.getInstance().getVersion() != null) {
			this.instance.setVersion(this.instance.getVersion() + 1);
		}
	}

	@SuppressWarnings("deprecation")
	public void approveChange() {
		getEntityManager().clear();
		if (makerCheckerId > 0) {
			setInstance((ProjectProperty) new MakerCheckerHome().getInstance(makerCheckerId));
			setInstance(getEntityManager().merge(getInstance()));
		}
		refreshFileAttachment();
		// setInstance(getEntityManager().merge(getInstance()));

		this.getInstance().setStep(1);
		this.incrementVersionConsecutive();
		this.instance.setConsecutiveTermsSheet(assingConsecutive());
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}", "ApproveSuccessfully");
	}

	public void approveCounter() {
		log(Level.INFO, "Aprobacion de Contador: " + this.getInstance());
		this.getInstance().setStep(0);
		refreshFileAttachment();
		super.update();

		List<ConceptRetentionRateAccount> listtodelete = Concept.RetentionToDelete;
		for (ConceptRetentionRateAccount retentionRateAccount : listtodelete) {
			Query q = getEntityManager().createQuery("DELETE FROM ConceptRetentionRateAccount WHERE id = ?");
			q.setParameter(1, retentionRateAccount.getId());
			q.executeUpdate();
		}
		/*
		 * RECALCULACION DE CONCEPTOS, DESPUES DE LA APROBACION DEL CONTADOR
		 */
		BillingFunctions recalculation = new BillingFunctions(this.getEntityManager());
		for (Concept concept : this.instance.getConcepts()) {
			recalculation.recalculateConcept(concept,null);
		}
	}

	public void cancelChange() {
		new MakerCheckerHome().deleteMaker(this.getInstance());
		@SuppressWarnings("deprecation")
		FacesMessages facesMessages = getFacesMessages();
		facesMessages.addFromResourceBundle(Severity.INFO, "#{messages.Successful_cancellation}", "CancelSuccessfully");
	}

	@SuppressWarnings("unchecked")
	public List<ContractType> getContractTypeList() {
		if (this.contractTypeList == null) {

			Query q = this.getEntityManager().createQuery("from ContractType ct");
			this.contractTypeList = (List<ContractType>) q.getResultList();
			return this.contractTypeList;

		} else {

			return this.contractTypeList;
		}
	}

	public void setContractTypeList(List<ContractType> contractTypeList) {
		this.contractTypeList = contractTypeList;

	}

	@SuppressWarnings("unchecked")
	public List<ObjectOfContract> getObjectOfContractList() {
		if (this.objectOfContractList == null) {

			Query q = this.getEntityManager().createQuery("from ObjectOfContract ct");
			this.objectOfContractList = (List<ObjectOfContract>) q.getResultList();
		}

		if (getInstance().getObjectOfContract() == null && this.objectOfContractList != null)
			getInstance().setObjectOfContract(this.objectOfContractList.get(0));

		return this.objectOfContractList;

	}

	public void setObjectOfContractList(List<ObjectOfContract> objectOfContractList) {
		this.objectOfContractList = objectOfContractList;
	}

	public Increased getIncreased() {
		return increased;
	}

	public void setIncreased(Increased increased) {
		this.increased = increased;
	}

	public Concept getConcept() {
		if (this.concept == null) {
			this.restartConcept();
		}
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public Concept getConceptSelected() {
		if (this.conceptSelected == null) {
			this.conceptSelected = new Concept();
		}
		return conceptSelected;
	}

	public void setConceptSelected(Concept conceptSelected) {
		this.templateHasRetentionRateAccount = null;
		this.errorOnConceptExpresion = false;
		this.validateExpression = "";
		this.listTaxConfiguration = null;
		this.conceptSelected = conceptSelected;
		this.getListTaxConfiguration();
		if (this.conceptSelected.getIncreased() != null) {
			this.isInCreasedEdit = true;
		}
	}

	public void instanceSelectedConcept(Concept conceptSelected) {
		this.setConceptSelected(conceptSelected);
		this.conceptSelected.setLastModification(Calendar.getInstance().getTime());
		if (this.conceptSelected.getIncreased() != null) {
			this.increasedEdit = this.conceptSelected.getIncreased();
			this.isInCreasedEdit = true;
		} else {
			this.increasedEdit = new Increased();
			this.isInCreasedEdit = false;
		}
		if (this.conceptSelected.getContributionModule() != null) {
			this.isContributionModule = true;
		} else {
			this.isContributionModule = false;
		}

	}

	/**
	 * Metodo que inserta Un concepto asociado a una hoja de Termino
	 */
	public void addConcept() {
		try {
			this.conceptosSinCargaMasiva = null;
			/*
			 * Se verifica si se configuro incremento, para asiganrselo al
			 * concepto
			 */
			if (this.isInCreased && this.concept.getFixedValue() != null) {
				this.concept.setIncreased(this.increased);
			} else {
				this.getConcept().setIncreased(null);
			}
			if (!this.isContributionModule) {
				this.concept.setContributionModule(null);
			}
			this.concept.setLastModification(Calendar.getInstance().getTime());
			/*
			 * Se verifica si el metodo tiene en la formula concepto, para
			 * guardalo como dependiente
			 */
			boolean cantConcept = this.getConcept().getExpression().contains("CONCEPT[");
			if (cantConcept) {
				this.getConcept().setDependent(true);
			} else {
				this.getConcept().setDependent(false);
			}
			calculateGroup();
			this.conceptList.add(this.concept);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Nuevo Concepto", "El concepto se agrego correctamente, pendiente guardar esta Hoja de Terminos"));
			this.concept = null; // Cuando se asigna NULL, al
			// llamar nuevamente
			// al getter del
			// Concept se
			// reinicia el
			// concepto
		} catch (org.hibernate.validator.InvalidStateException ex) {
			System.out.print("Error de Persistencia: " + ex.getInvalidValues()[0]);

		}
	}

	/**
	 * Metodo que valida si el concepto ingresado, Tiene un sintaxis correcta
	 */
	public void validateExpression() {

		/**
		 * Se Inicializan variables de Incrementos
		 */

		IpcYearly ipcYaerly = new IpcYearly();
		String ipcYearlyString = ipcYaerly.getIpcs(this.getEntityManager());

		IpcMonthly ipcMonthly = new IpcMonthly();
		String ipcMonthlyString = ipcMonthly.getIpcs(this.getEntityManager());

		IpcAccumulated ipcAccumalated = new IpcAccumulated();
		String ipcAccumalatedString = ipcAccumalated.getIpcs(this.getEntityManager());

		MinimunWage minimumWage = new MinimunWage();
		String minimumWageString = minimumWage.getMinimumWages(this.getEntityManager());
		/**
		 * Se verifica si Tiene Venta la Hoja de Termino
		 */
		
		/**
		 * Se valida que la expresión Solo tenga un Solo IPC.
		 */
		Integer cantIpc = 0;

		cantIpc = concept.getExpression().indexOf("IPCY[YEAR") == -1 ? cantIpc : (cantIpc + 1);
		cantIpc = concept.getExpression().indexOf("IPCM[MONTH") == -1 ? cantIpc : (cantIpc + 1);
		cantIpc = concept.getExpression().indexOf("IPCA[MONTH") == -1 ? cantIpc : (cantIpc + 1);
		// cantIpc = concept.getExpression().indexOf("SM[MONTH") == -1 ? cantIpc
		// : (cantIpc + 1);
		log(Level.INFO, "IPCYC:" + cantIpc + "-(" + concept.getExpression() + ")");

		if (cantIpc <= 1) {

			/**
			 * Se verifica si el concepto Tiene Modulo de Contribucion, En este
			 * Caso se valida con un RentableUnitContribution dummy debido a que
			 * en este caso el Valor del concepto aplica a todos las unidades
			 * arrendables asociadas al modulo de Contribucion
			 */
			if (!this.isContributionModule) {
				this.getConcept().setDependent(true);
				Double value = this.getConcept().calculateValueConceptByPlugin(null, Calendar.getInstance(), ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, this.getEntityManager(), minimumWageString);
				if (value == null) {
					this.errorOnConceptExpresion = true;
					this.validateExpression = "Formula Ingresada No es Correcta";
				} else {
					this.errorOnConceptExpresion = false;
					this.validateExpression = " Calculo de Formula es: " + formatter.format(value);
					log(Level.INFO, "***valor de la formula: " + value);
				}
			} else {
				// log(Level.INFO, "Unidad Arrendable: " +
				// this.instance.getRentableUnit().getId());
				/**
				 * Se búsca un rentable_unit_contribution de una Hoja de termino
				 * de Arrendamiento
				 */
				RentableUnitContribution rentable = null;
				if (this.instance.getObjectOfContract().getId() == 2) {
					Query q = this.getEntityManager().createQuery("from RentableUnitContribution where rentableUnit.id=? and contributionModule.id = ?");
					q.setParameter(1, this.instance.getRentableUnit().getId());
					q.setParameter(2, this.getConcept().getContributionModule().getId());
					@SuppressWarnings("unchecked")
					List<RentableUnitContribution> rucList = (List<RentableUnitContribution>) q.getResultList();
					if (rucList.size() > 0) {
						rentable = rucList.get(0);
					}
				}

				if (rentable == null) {
					rentable = new RentableUnitContribution();
					rentable.setContributionRate(0.0);
				}
				this.getConcept().setDependent(true);
				Double value = this.getConcept().calculateValueConceptByPlugin(rentable, Calendar.getInstance(), ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, this.getEntityManager(), minimumWageString);
				if (value == null) {
					this.errorOnConceptExpresion = true;
					this.validateExpression = "Formula Ingresada No es Correcta(MC)";
				} else {
					this.errorOnConceptExpresion = false;
					this.validateExpression = " Calculo de Formula es: " + formatter.format(value);
					log(Level.INFO, "***valor de la formula: " + value);
				}

			}
		} else {
			this.errorOnConceptExpresion = true;
			this.validateExpression = "La Formula Ingresada Tiene Multiples IPC'S ";
		}
		this.getConcept().setDependent(false);
	}

	/**
	 * Metodo que valida si el concepto ingresado, Tiene un sintaxis correcta
	 */
	public void IsvalidateExpressionIncreased() {

		/**
		 * Se Inicializan variables de Incrementos
		 */

		IpcYearly ipcYaerly = new IpcYearly();
		String ipcYearlyString = ipcYaerly.getIpcs(this.getEntityManager());

		IpcMonthly ipcMonthly = new IpcMonthly();
		String ipcMonthlyString = ipcMonthly.getIpcs(this.getEntityManager());

		IpcAccumulated ipcAccumalated = new IpcAccumulated();
		String ipcAccumalatedString = ipcAccumalated.getIpcs(this.getEntityManager());

		MinimunWage minimumWage = new MinimunWage();
		String minimumWageString = minimumWage.getMinimumWages(this.getEntityManager());
		/**
		 * Se verifica si Tiene Venta la Hoja de Termino
		 */
		Sales sales = null;
		if (this.instance.getRentableUnit() != null) {
			Query q = this.getEntityManager().createQuery("from Sales sales where sales.rentableUnit.id = ? and salesPeriod.year = ? and salesPeriod.month = ? ");
			q.setParameter(1, this.instance.getRentableUnit().getId());
			q.setParameter(2, Calendar.getInstance().get(Calendar.YEAR));
			q.setParameter(3, Calendar.getInstance().get(Calendar.MONTH));
			@SuppressWarnings("unchecked")
			List<Sales> saleList = (List<Sales>) q.getResultList();
			if (saleList.size() > 0) {
				sales = saleList.get(0);
			}

		}
		Integer cantIpc = 0;
		cantIpc = this.increased.getExpression().indexOf("IPCY[YEAR") == -1 ? cantIpc : (cantIpc + 1);
		cantIpc = this.increased.getExpression().indexOf("IPCM[MONTH") == -1 ? cantIpc : (cantIpc + 1);
		cantIpc = this.increased.getExpression().indexOf("IPCA[MONTH") == -1 ? cantIpc : (cantIpc + 1);
		log(Level.INFO, "IPCYC:" + cantIpc + "-(" + concept.getExpression() + ")");

		if (cantIpc <= 1) {
			/**
			 * Se verifica si el concepto Tiene Modulo de Contribucion, En este
			 * Caso se valida con un RentableUnitContribution dummy debido a que
			 * en este caso el Valor del concepto aplica a todos las unidades
			 * arrendables asociadas al modulo de Contribucion
			 */
			if (!this.isContributionModule) {
				log(Level.INFO, concept.getProjectProperty().toString());
				Boolean result = this.getConcept().validateIncreisedConcept(ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, Calendar.getInstance(), null, sales, this.increased.getExpression(), minimumWageString);

				if (!result) {
					this.errorOnConceptExpresionIncreased = true;
					this.validateExpressionIncreased = "Formula Ingresada No es Correcta";
				} else {
					this.errorOnConceptExpresionIncreased = false;
					this.validateExpressionIncreased = " Calculo de Formula es correcta: ";
				}
			} else {
				/**
				 * Se búsca un rentable_unit_contribution
				 */
				RentableUnitContribution rentable = null;
				if (this.instance.getObjectOfContract().getId() == 2) {
					Query q = this.getEntityManager().createQuery("from RentableUnitContribution where rentableUnit.id=? and contributionModule.id = ?");
					q.setParameter(1, this.instance.getRentableUnit().getId());
					q.setParameter(2, this.getConcept().getContributionModule().getId());
					@SuppressWarnings("unchecked")
					List<RentableUnitContribution> rucList = (List<RentableUnitContribution>) q.getResultList();
					if (rucList.size() > 0) {
						rentable = rucList.get(0);
					}
				}

				if (rentable == null) {
					rentable = new RentableUnitContribution();
					rentable.setContributionRate(0.0);
				}
				Boolean result = this.getConcept().validateIncreisedConcept(ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, Calendar.getInstance(), rentable, sales, this.increased.getExpression(), minimumWageString);
				if (!result) {
					this.errorOnConceptExpresionIncreased = true;
					this.validateExpressionIncreased = "Formula Ingresada No es Correcta";
				} else {
					this.errorOnConceptExpresionIncreased = false;
					this.validateExpressionIncreased = "La Formula Ingresada es Correcta";
				}
			}
		} else {
			this.errorOnConceptExpresionIncreased = true;
			this.validateExpressionIncreased = "Formula Ingresada Contiene Multiples IPC'S";
		}

	}

	/**
	 * Metodo que valida si el concepto ingresado, Tiene un sintaxis correcta
	 */
	public void validateExpressionConceptSelect() {
		/**
		 * Se Inicializan variables de Incrementos
		 */

		IpcYearly ipcYaerly = new IpcYearly();
		String ipcYearlyString = ipcYaerly.getIpcs(this.getEntityManager());

		IpcMonthly ipcMonthly = new IpcMonthly();
		String ipcMonthlyString = ipcMonthly.getIpcs(this.getEntityManager());

		IpcAccumulated ipcAccumalated = new IpcAccumulated();
		String ipcAccumalatedString = ipcAccumalated.getIpcs(this.getEntityManager());

		MinimunWage minimumWage = new MinimunWage();
		String minimumWageString = minimumWage.getMinimumWages(this.getEntityManager());
		Integer cantIpc = 0;

		cantIpc = conceptSelected.getExpression().indexOf("IPCY[YEAR") == -1 ? cantIpc : (cantIpc + 1);
		cantIpc = conceptSelected.getExpression().indexOf("IPCM[MONTH") == -1 ? cantIpc : (cantIpc + 1);
		cantIpc = conceptSelected.getExpression().indexOf("IPCA[MONTH") == -1 ? cantIpc : (cantIpc + 1);
		log(Level.INFO, "IPCYC:" + cantIpc + "-(" + concept.getExpression() + ")");

		if (cantIpc <= 1) {
			/**
			 * Se verifica si el concepto Tiene Modulo de Contribucion, En este
			 * Caso se valida con un RentableUnitContribution dummy debido a que
			 * en este caso el Valor del concepto aplica a todos las unidades
			 * arrendables asociadas al modulo de Contribucion
			 */
			if (conceptSelected.getContributionModule() == null) {
				Double value = conceptSelected.calculateValueConceptByPlugin(null, Calendar.getInstance(), ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, this.getEntityManager(), minimumWageString);
				if (value == null) {
					this.errorOnConceptExpresion = true;
					this.validateExpression = "Formula Ingresada No es Correcta";
				} else {
					this.errorOnConceptExpresion = false;
					this.validateExpression = " Calculo de Formula es: " + formatter.format(value);
				}
			} else {
				/**
				 * Se búsca un rentable_unit_contribution
				 */
				RentableUnitContribution rentable = null;
				if (this.instance.getObjectOfContract().getId() == 2) {
					Query q = this.getEntityManager().createQuery("from RentableUnitContribution where rentableUnit.id=? and contributionModule.id = ?");
					q.setParameter(1, this.instance.getRentableUnit().getId());
					q.setParameter(2, this.getConcept().getContributionModule().getId());
					@SuppressWarnings("unchecked")
					List<RentableUnitContribution> rucList = (List<RentableUnitContribution>) q.getResultList();
					if (rucList.size() > 0) {
						rentable = rucList.get(0);
					}
				}

				if (rentable == null) {
					rentable = new RentableUnitContribution();
					rentable.setContributionRate(0.0);
				}
				Double value = conceptSelected.calculateValueConceptByPlugin(rentable, Calendar.getInstance(), ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, this.getEntityManager(), minimumWageString);
				if (value == null) {
					this.errorOnConceptExpresion = true;
					this.validateExpression = "Formula Ingresada No es Correcta(MC)";
				} else {
					this.errorOnConceptExpresion = false;
					this.validateExpression = "La Formula Ingresada es Correcta (No se Muestra valor, Se generan Multiples valores) ";
				}

			}
		} else {
			this.errorOnConceptExpresion = true;
			this.validateExpression = "Formula Ingresada Contiene Multiples IPC'S";
		}
		if (!this.errorOnConceptExpresion) {
			/*
			 * Se verifica si el metodo tiene en la formula concepto, para
			 * guardalo como dependiente
			 */
			boolean cantConcept = conceptSelected.getExpression().contains("CONCEPT");
			log(Level.INFO, "Cantidad2 de conceptos:" + cantConcept + "-Formula:" + conceptSelected.getExpression());
			if (cantConcept == true) {
				conceptSelected.setDependent(true);
			} else {
				conceptSelected.setDependent(false);
			}
		}

	}

	/**
	 * Metodo que valida si el concepto ingresado, Tiene un sintaxis correcta
	 */
	public String getConceptValue(Concept conceptValidate) {

		String result = "";
		/**
		 * Se Inicializan variables de Incrementos
		 */
		IpcYearly ipcYaerly = new IpcYearly();
		String ipcYearlyString = ipcYaerly.getIpcs(this.getEntityManager());

		IpcMonthly ipcMonthly = new IpcMonthly();
		String ipcMonthlyString = ipcMonthly.getIpcs(this.getEntityManager());

		IpcAccumulated ipcAccumalated = new IpcAccumulated();
		String ipcAccumalatedString = ipcAccumalated.getIpcs(this.getEntityManager());

		MinimunWage minimumWage = new MinimunWage();
		String minimumWageString = minimumWage.getMinimumWages(this.getEntityManager());
		/**
		 * Se valida que la expresión Solo tenga un Solo IPC.
		 */
		Boolean isIpcYear, isIpcMonthly, isIpcAcummalate;

		isIpcYear = conceptValidate.getExpression().indexOf("IPCY[YEAR") == -1 ? false : true;
		isIpcMonthly = conceptValidate.getExpression().indexOf("IPCM[MONTH") == -1 ? false : true;
		isIpcAcummalate = conceptValidate.getExpression().indexOf("IPCA[MONTH") == -1 ? false : true;
		log(Level.INFO, "IPCY:" + isIpcYear + "-(" + conceptValidate.getExpression() + ")" + "-IPCM:" + isIpcMonthly + "-IPCA:" + isIpcAcummalate);

		/**
		 * Se verifica si el concepto Tiene Modulo de Contribucion, En este Caso
		 * se valida con un RentableUnitContribution dummy debido a que en este
		 * caso el Valor del concepto aplica a todos las unidades arrendables
		 * asociadas al modulo de Contribucion
		 */
		if (conceptValidate.getContributionModule() == null) {
			Double value = conceptValidate.calculateValueConceptByPlugin(null, Calendar.getInstance(), ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, this.getEntityManager(), minimumWageString);
			if (value == null) {
				result = "NA";
			} else {
				result = "" + formatter.format(value);
			}
		} else {
			/**
			 * Se búsca un rentable_unit_contribution
			 */
			RentableUnitContribution rentable = null;
			if (this.instance.getRentableUnit() != null && this.getConcept().getContributionModule() != null) {
				Query q = this.getEntityManager().createQuery("from RentableUnitContribution where rentableUnit.id=? and contributionModule.id = ?");
				q.setParameter(1, this.instance.getRentableUnit().getId());
				q.setParameter(2, this.getConcept().getContributionModule().getId());
				@SuppressWarnings("unchecked")
				List<RentableUnitContribution> rucList = (List<RentableUnitContribution>) q.getResultList();
				if (rucList.size() > 0) {
					rentable = rucList.get(0);
				}
			}

			if (rentable == null) {
				rentable = new RentableUnitContribution();
				rentable.setContributionRate(0.0);
			}
			Double value = conceptValidate.calculateValueConceptByPlugin(rentable, Calendar.getInstance(), ipcYearlyString, ipcMonthlyString, ipcAccumalatedString, this.getEntityManager(), minimumWageString);
			if (value == null) {
				result = "NA";
			} else {
				result = "TS-ACTIVO-" + conceptValidate.getId();
			}

		}
		return result;
	}

	/**
	 * Metodo que edita Un Concepto de Una Hoja de Termino
	 */
	public void editConcept() {
		List<ConceptRetentionRateAccount> conceptRetentionRateAccounts = new ArrayList<ConceptRetentionRateAccount>();
		if (!isInCreased && this.instance.getStep() != ProjectProperty.STEP_pending_public_accountant_approval)
			conceptSelected.setIncreased(null);
		for (RetentionRateAccount retention : this.RetentionRateAccounts) {
			/* Se la retencion no fue configurada */
			if (!retention.getName().equals("-")) {
				ConceptRetentionRateAccount cnew = new ConceptRetentionRateAccount();
				cnew.setRetentionRateAccount(retention);
				cnew.setConcept(this.conceptSelected);
				conceptRetentionRateAccounts.add(cnew);
				conceptSelected.setRetention(cnew);

				log(Level.INFO, " Rt2: " + cnew.getRetentionRateAccount().getId());
			}
		}

		ConceptHome conceptHome = new ConceptHome();
		conceptHome.setInstance(this.conceptSelected);
		conceptHome.persistConcept();

		this.updateRetention = true;

		cleanConceptSelected();

	}

	private void cleanConceptSelected() {
		this.conceptSelected = null;
		this.retentionRateAccount = null;
	}

	/**
	 * Método que calcula el grupo inicial de la instancia del concepto de la
	 * clase
	 */
	private void calculateGroup() {
		int documentType = concept.getDocumentType();
		int count = 0;

		for (Concept items : this.instance.getConcepts()) {
			if (items.getDocumentType() != null) {
				if (items.getDocumentType() == documentType) {
					count++;
				}
			}
		}

		concept.setGroupNumber(count + 1);
	}

	public boolean isConceptDisabled() {
		if (this.getConcept() == null)
			return true;
		if (this.getConcept().getName() == null || this.getConcept().getName().isEmpty() || this.concept.getName().length() > 255)
			return true;
		if (this.getConcept().getPrintDescription() != null && this.concept.getPrintDescription().length() > 255)
			return true;
		if (this.concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_NOT_SET) {
			if (this.getConcept().getStartDate() == null) {
				return true;
			}
			if (this.getConcept().getPeriodicity() == null) {
				return true;
			}
			if (this.getConcept().getPeriodicityType() == null) {
				return true;
			}
			if (this.instance.getObligationsStartDate() != null && this.getConcept().getStartDate() != null && this.instance.getObligationsStartDate().after(this.getConcept().getStartDate())) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Fecha", "La fecha de inicialización del concepto no es valida"));
				return true;
			}
			if (this.instance.getExpirationDate() != null && this.getConcept().getEndDateView() != null && this.instance.getExpirationDate().before(this.getConcept().getEndDateView())) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Fecha", "La fecha de finalización del concepto no es valida"));
				return true;
			}
		}
		if (this.getConcept().getExpression() == null || this.getConcept().getExpression().isEmpty())
			return true;

		if (this.isInCreased && this.increased != null && this.concept.getFixedValue() != null && !this.getImmediatePaymentConceptCreation()) {
			log(Level.INFO, "Increased: " + this.increased.getPeriodicity());
			if (this.increased.getPeriodicity() == null)
				return true;
			if (this.increased.getExpression() == null || this.increased.getExpression().isEmpty())
				return true;
			if (this.increased.getNextIncreased() == null)
				return true;
			if (this.errorOnConceptExpresionIncreased) {
				return true;
			}
		}
		if (this.isInstanceObjectOfContractRealProperty()) {
			if (this.getConcept().getContributionModule() == null)
				return true;
		}
		return false;
	}

	public boolean isConceptEditButtonDisabled() {
		if (this.conceptSelected == null)
			return true;
		if (this.conceptSelected.getName() == null || this.conceptSelected.getName().isEmpty() || this.conceptSelected.getName().length() > 255)
			return true;
		if (this.conceptSelected.getPrintDescription() != null && this.conceptSelected.getPrintDescription().length() > 255)
			return true;
		if (this.conceptSelected.getStartDate() == null)
			return true;
		if (this.conceptSelected.getPeriodicity() == null)
			return true;
		if (this.conceptSelected.getPeriodicityType() == null)
			return true;
		if (this.conceptSelected.getExpression() == null || this.conceptSelected.getExpression().isEmpty())
			return true;

		if (this.instance.getObligationsStartDate() != null && this.conceptSelected.getStartDate() != null && this.instance.getObligationsStartDate().after(this.conceptSelected.getStartDate())) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Fecha", "La fecha de inicialización del concepto no es valida"));
			return true;
		}
		if (this.instance.getExpirationDate() != null && this.conceptSelected.getEndDateView() != null && this.instance.getExpirationDate().before(this.conceptSelected.getEndDateView())) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Fecha", "La fecha de finalización del concepto no es valida"));
			return true;
		}

		if (this.isInCreasedEdit && this.increasedEdit != null && this.conceptSelected.getFixedValue() != null && !this.getImmediatePaymentConceptEdit()) {
			if (this.increasedEdit.getPeriodicity() == null || this.increasedEdit.getPeriodicity() == 0)
				return true;
			if (this.increasedEdit.getExpression() == null || this.increasedEdit.getExpression().isEmpty())
				return true;
			if (this.increasedEdit.getNextIncreased() == null)
				return true;
		}
		if (this.isInstanceObjectOfContractRealProperty()) {
			if (this.conceptSelected.getContributionModule() == null)
				return true;
		}
		return false;
	}

	public boolean isConceptEditDisabled() {

		return true;
	}

	public String getMainUrl() {
		return this.mainUrl;
	}

	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}

	public void destroy() {
		this.concept = null;

	}

	public void restartConcept() {

		this.concept = new Concept();
		this.concept.setCostCenterType(1);
		this.concept.setProjectProperty(this.instance);
		this.isInCreased = false;
		this.increased = new Increased();
		this.template = new ConcepTemplate();
		this.isContributionModule = false;
		this.validateExpression = "";
		this.validateExpressionIncreased = "";
		if (this.isInstanceObjectOfContractRealProperty())
			this.isContributionModule = true;
		// this.renewConceptEndDate();

	}

	public void invokeWebService() {
		try {

			URL plugin = new URL("http://192.168.1.41:8080/terranvm/seam/resource/rest/contract/customer/256");
			URLConnection plug = plugin.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(plug.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				log(Level.INFO, inputLine);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSummaryUrl() {
		if (this.summaryUrl == null || this.summaryUrl.isEmpty())
			this.summaryUrl = "No existe una URL de resumen asociada al plugin";
		return summaryUrl;
	}

	public void setSummaryUrl(String summaryUrl) {
		this.summaryUrl = summaryUrl;
	}

	public void viewConceptSummary(Concept c) {

	}

	public void viewConceptMain(Concept c) {

	}

	public String hostURL() {
		String host = "http://";
		host += FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		host += ":" + FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort();
		return host;
	}

	public String getTableState() {
		return tableState;
	}

	public void setTableState(String tableState) {
		this.tableState = tableState;
	}

	public List<RetentionRateAccount> getPaymentForms() {

		if (this.counts == null) {
			counts = new ArrayList<RetentionRateAccount>();
			try {
				Query q = this.getEntityManager().createQuery("from RetentionRateAccount pf where pf.biller = ? and pf.retentionRate.id=?");
				q.setParameter(1, getInstance().getBiller());
				q.setParameter(2, RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE);
				@SuppressWarnings("unchecked")
				List<RetentionRateAccount> l = (List<RetentionRateAccount>) q.getResultList();
				Iterator<RetentionRateAccount> iterator = l.iterator();
				while (iterator.hasNext()) {
					RetentionRateAccount elem = ((RetentionRateAccount) iterator.next());
					counts.add(elem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return counts;
		} else {
			return counts;
		}
	}

	/**
	 * Lista las variables del Sistema
	 * 
	 * @return
	 */
	public List<SystemVariable> getSystemVariable() {

		List<SystemVariable> list = new ArrayList<SystemVariable>();
		try {
			Query q;
			if (this.instance.getProject() != null) {
				q = this.getEntityManager().createQuery("from SystemVariable sv where sv.project = ? or sv.project is null");
				q.setParameter(1, this.instance.getProject());
				@SuppressWarnings("unchecked")
				List<SystemVariable> l = (List<SystemVariable>) q.getResultList();
				Iterator<SystemVariable> iterator = l.iterator();
				while (iterator.hasNext()) {
					SystemVariable elem = ((SystemVariable) iterator.next());
					list.add(elem);
				}
			} else {
				return list;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public void setPaymentForms(List<RetentionRateAccount> paymentForms) {
		this.counts = paymentForms;
	}

	/**
	 * Metodo que agrega a la formula del incremento la variable seleccionada
	 */
	public void onSelectionChangedSystemVariableIncrease() {
		if (systemVaribleSelectionIncrease != null) {
			log(Level.INFO, "Selected keys: " + systemVaribleSelectionIncrease);
			Iterator<Object> it = systemVaribleSelectionIncrease.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				log(Level.INFO, "key: " + key);
				log(Level.INFO, "tablePaymentFormBind state" + tableState);
				tablesSystemVAriableIncreaseBind.setRowKey(key);
				if (tablesSystemVAriableIncreaseBind.isRowAvailable()) {
					String expression = increased.getExpression() == null ? "" : increased.getExpression();
					expression += ((SystemVariable) tablesSystemVAriableIncreaseBind.getRowData()).getSintax();
					increased.setExpression(expression);
					this.IsvalidateExpressionIncreased();
					this.systemVaribleSelectionIncrease = null;
					break;
				}
			}

		} else {
			log(Level.INFO, "No selectionLegalRepresentative is set.");
		}

	}

	/**
	 * Metodo que agrega a la formula del concepto la variable seleccionada
	 */
	public void onSelectionChangedSystemVariable() {
		if (systemVaribleSelection != null) {
			log(Level.INFO, "Selected keys: " + systemVaribleSelection);
			Iterator<Object> it = systemVaribleSelection.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				log(Level.INFO, "key: " + key);
				log(Level.INFO, "tablePaymentFormBind state" + tableState);
				tablesSystemVAriableBind.setRowKey(key);
				if (tablesSystemVAriableBind.isRowAvailable()) {
					String expression = concept.getExpression() == null ? "" : concept.getExpression();
					expression += ((SystemVariable) tablesSystemVAriableBind.getRowData()).getSintax();
					concept.setExpression(expression);
					this.validateExpression();
					this.systemVaribleSelection = null;
					break;
				}
			}

		} else {
			log(Level.INFO, "No selectionLegalRepresentative is set.");
		}

	}

	/**
	 * Metodo que agrega al concepto editado la variable seleccionada
	 */
	public void onSelectionChangedSystemVariableEdit() {
		if (systemVaribleSelection != null) {
			log(Level.INFO, "Selected keys Concept Edit: " + systemVaribleSelection);
			Iterator<Object> it = systemVaribleSelection.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				log(Level.INFO, "key: " + key);
				log(Level.INFO, "tablePaymentFormBind state" + tableState);
				tablesSystemVAriableBindEdit.setRowKey(key);
				if (tablesSystemVAriableBindEdit.isRowAvailable()) {

					String expression = conceptSelected.getExpression() == null ? "" : conceptSelected.getExpression();
					conceptSelected.setExpression("");
					expression += ((SystemVariable) tablesSystemVAriableBindEdit.getRowData()).getSintax();
					log(Level.INFO, "new Expression Concept Edit: " + expression);
					conceptSelected.setExpression(expression);
					this.validateExpressionConceptSelect();
					this.systemVaribleSelection = null;
					break;

				}
			}
		} else {
			log(Level.INFO, "No selectionLegalRepresentative is set.");
		}
	}

	/**
	 * Metodo que agrega a la formula del incremento la variable seleccionada
	 */
	public void onSelectionChangedSystemVariableIncreaseEdit() {
		increased = null;
		if (systemVaribleSelectionIncrease != null) {
			log(Level.INFO, "Selected keys: " + systemVaribleSelectionIncrease);
			Iterator<Object> it = systemVaribleSelectionIncrease.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				log(Level.INFO, "key: " + key);
				log(Level.INFO, "tablePaymentFormBind state" + tableState);
				tablesSystemVAriableIncreaseBindEdit.setRowKey(key);
				if (tablesSystemVAriableIncreaseBindEdit.isRowAvailable()) {
					String expression = increasedEdit.getExpression() == null ? "" : increasedEdit.getExpression();
					expression += ((SystemVariable) tablesSystemVAriableIncreaseBindEdit.getRowData()).getSintax();
					increasedEdit.setExpression(expression);
					increased = increasedEdit;
					this.IsvalidateExpressionIncreased();
					this.systemVaribleSelectionIncrease = null;
					increased = null;
					break;
				}
			}

		} else {
			increased = increasedEdit;
			this.IsvalidateExpressionIncreased();
			log(Level.INFO, "No selectionLegalRepresentative is set.");
		}

	}

	/**
	 * Metodo que agreaga a Lista de Reteneciones del concepto que tipo de
	 * retencion se selecciono
	 * 
	 * @param name
	 * @param position
	 */
	public void onSelectionChangedRetention(RetentionRateAccount retentionInput) {

		RetentionRateAccount delete = null;

		for (RetentionRateAccount retention : this.RetentionRateAccounts) {
			if (retentionInput != null) {
				if (retention.getRetentionRate().getId() == retentionInput.getRetentionRate().getId()) {
					delete = retention;
					// this.RetentionRateAccounts.remove(retention);
				}
			}
		}
		if (delete != null) {
			log(Level.INFO, "Eliminando Retencion seleccionada:" + delete.getId());
			this.RetentionRateAccounts.remove(delete);
		}

		this.updateRetention = false;

		this.RetentionRateAccounts.add(retentionInput);

	}

	public List<SelectItem> groupNumbersConcepts(Concept concept) {
		int documentType = concept.getDocumentType();
		int billCount = 0;
		int accountReceivableCount = 0;
		List<SelectItem> result = new ArrayList<SelectItem>();
		for (Concept items : conceptnotbulkload(getConceptList())) {
			if (items.getDocumentType() != null) {
				if (items.getDocumentType() == Concept.DOCUMENT_TYPE_BILL) {
					billCount++;
				} else if (items.getDocumentType() == Concept.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE) {
					accountReceivableCount++;
				}
			}
		}

		if (documentType == Concept.DOCUMENT_TYPE_BILL) {

			for (int i = 0; i < billCount; i++) {
				result.add(new SelectItem(i + 1, "Factura No." + (i + 1)));
			}
		} else if (documentType == Concept.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE) {
			for (int i = 0; i < accountReceivableCount; i++) {
				result.add(new SelectItem(i + 1, "Cuenta de Cobro No." + (i + 1)));
			}
		}
		return result;
	}

	public void getMyInstance() {
		getInstance();
	}

	public void saveConceptsAgrouping() {
		// ConceptHome conceptHome = new ConceptHome();
		// for (Concept concept : this.getInstance().getConcepts())
		// {
		// MakerChecker makerChecker = new
		// MakerCheckerHome().getMakerChecker(concept);
		// if (makerChecker == null) {
		// conceptHome.setInstance(concept);
		// conceptHome.crateMakerChecker();
		// } else
		// new
		// MakerCheckerHome().updateMakerChackerObject(makerChecker,
		// concept);
		// }
	}

	public boolean agroupingDisable(Concept concept) {
		if (concept.getDocumentType() != null && (concept.getDocumentType() == Concept.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE || concept.getDocumentType() == Concept.DOCUMENT_TYPE_BILL))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<RealProperty> realPropertyList() {
		this.rentableUnitList = null;

		if (this.realPropertyList == null) {

			Query q;
			if (Integer.parseInt(projectFilter) == -1) {
				q = this.getEntityManager().createQuery("from RealProperty rp WHERE rp.step = ? AND RP.deactivate = ? AND RP.pendingApprove = ?");
				q.setParameter(1, RealProperty.STEP_APPROVED);
				q.setParameter(2, false);
				q.setParameter(3, false);
			} else {
				q = this.getEntityManager().createNativeQuery("select RP.* from real_property RP,project_has_realproperty PHP where PHP.realproperty=RP.id and PHP.project = ? AND RP.step = ? AND RP.deactivate = ?  AND RP.pending_approve = ?", RealProperty.class);
				q.setParameter(1, Integer.parseInt(projectFilter));
				q.setParameter(2, RealProperty.STEP_APPROVED);
				q.setParameter(3, false);
				q.setParameter(4, false);
			}
			this.realPropertyList = (List<RealProperty>) q.getResultList();
			if (this.instance.getRealProperty() != null && this.instance.getRealProperty().isPendingApprove() && !this.realPropertyList.contains(this.instance.getRealProperty())) {

				this.realPropertyList.add(this.instance.getRealProperty());
			}
		}
		if (this.realPropertyList != null && !this.realPropertyList.isEmpty()) {
			if (this.instance.getRealProperty() == null && this.instance.getObjectOfContract() != null && (this.instance.getObjectOfContract().getId() == 2 || this.instance.getObjectOfContract().getId() == 1)) {
				this.instance.setRealProperty(this.realPropertyList.get(0));
			}
		}

		rentableUnitList();
		return this.realPropertyList;
	}

	public List<RentableUnit> rentableUnitList() {
		if (this.rentableUnitList == null) {
			String consulta = "select ru.id from rentable_unit ru join area a on ru.area=a.id join floor f on f.id = a.floor join construction c on c.id= f.construction join real_property rp on rp.id=c.real_property where rp.id = ? and ru.id not in (select ru.id from rentable_unit ru join project_property pp on ru.id = pp.rentable_unit join area a on ru.area=a.id join floor f on f.id = a.floor join construction c on c.id= f.construction join real_property rp on rp.id=c.real_property where rp.id=? and (pp.status not in (?,?)) and  (pp.id!=? and pp.expiration_date > ?))  and ru.deactivate = false and f.deactivate = false and c.deactivate = false and a.deactivate = false and ru.pending_approve = false";
			if (this.getInstance().getRealProperty() != null) {
				Query q = this.getEntityManager().createNativeQuery(consulta);
				q.setParameter(1, this.getInstance().getRealProperty().getId());
				q.setParameter(2, this.getInstance().getRealProperty().getId());
				q.setParameter(3, ProjectProperty.STATUS_EXPIRED);
				q.setParameter(4, ProjectProperty.STATUS_TERMINATED);
				q.setParameter(5, this.instance.getId());
				q.setParameter(6, new Date());
				this.rentableUnitList = new ArrayList<RentableUnit>();
				try {
					for (int i = 0; i < q.getResultList().size(); i++) {
						this.rentableUnitList.add(getEntityManager().find(RentableUnit.class, q.getResultList().get(i)));
					}

					if (this.instance.getRentableUnit() != null && !this.rentableUnitList.contains(this.instance.getRentableUnit())) {

						this.rentableUnitList.add(this.instance.getRentableUnit());
					}
					if (this.instance.getRentableUnit() == null && !this.rentableUnitList.isEmpty())

						this.getInstance().setRentableUnit(this.rentableUnitList.get(0));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return this.rentableUnitList;

	}

	public int getUseContributionModule() {
		return useContributionModule;
	}

	public void setUseContributionModule(int useContributionModule) {
		this.useContributionModule = useContributionModule;
	}

	public List<ContributionModule> getContributionModuleList() {
		return contributionModuleList;
	}

	public void setContributionModuleList(List<ContributionModule> contributionModuleList) {
		this.contributionModuleList = contributionModuleList;
	}

	public List<MakerChecker> projectPropertyStepList() {

		Query q = this.getEntityManager().createQuery(getHQL());
		@SuppressWarnings("unchecked")
		List<MakerChecker> list = (List<MakerChecker>) q.getResultList();
		List<MakerChecker> projectPropertylist = new ArrayList<MakerChecker>();
		if (list != null && !list.isEmpty()) {
			for (MakerChecker makerChecker : list) {
				Object obj = new MakerCheckerHome().getInstance(makerChecker);
				log(Level.INFO, "++++++++++++Object Maker-Checker: " + (obj) + "+++++++++++++++++++++++++++++");
				if (obj != null) {
					getEntityManager().merge(obj);
					int res = -1;
					if (obj instanceof ProjectProperty) {
						res = ((ProjectProperty) obj).getStep();
						if (res == 0) {
							projectPropertylist.add(makerChecker);
						}
					} else {
						projectPropertylist.add(makerChecker);
					}
				} else {
					log(Level.INFO,"***********Objeto NULL maker-checker********************");
				}
			}

		}
		return projectPropertylist;
	}

	private String getHQL() {
		String filter = "select makerChecker from MakerChecker makerChecker";
		if (projectFilter != null && projectsFilter != null) {
			if (projectFilter.equals("-1")) {
				String next = ",MakerCheckerXProject mcp WHERE makerChecker.className like ('" + ProjectProperty.class.getCanonicalName() + "') and ((mcp.makerChecker = makerChecker AND  mcp.project.id IN  (";
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
				filter += ",MakerCheckerXProject mcp WHERE makerChecker.className like ('" + ProjectProperty.class.getCanonicalName() + "') and ((mcp.project.id IN (" + projectFilter + ") AND mcp.makerChecker = makerChecker) OR (SELECT COUNT(p) FROM MakerCheckerXProject p WHERE p.makerChecker = makerChecker) = 0)";
			}
		}
		return filter;
	}

	public HtmlExtendedDataTable getTableClause() {
		return tableClause;
	}

	public void setTableClause(HtmlExtendedDataTable tableClause) {
		this.tableClause = tableClause;
	}

	/*
	 * Metodo Que Retorna Una Lista de Tipos de Periodos 1->Dias 2->Meses
	 * 3->Años
	 */
	private List<SelectItem> getPeriodicityTypeItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		/* Se agrega Dia */
		SelectItem itemDia = new SelectItem(1, "Dias");
		items.add(itemDia);
		/* Se agrega meses */
		SelectItem itemMes = new SelectItem(2, "Meses");
		items.add(itemMes);
		/* Se agrega año */
		SelectItem itemAño = new SelectItem(3, "Año");
		items.add(itemAño);

		return items;
	}

	/**
	 * Metodo que Retorna un listado de Tipos de Peridodicidad asociado a la
	 * extencion en la Hoja de Termino
	 * 
	 * @return
	 */
	public List<SelectItem> getPeriodicityTypeItemsExtensionProjectProperty() {

		List<SelectItem> itemsExtension = this.getPeriodicityTypeItems();
		/*
		 * Si el elemento Es nulo (Se ingresa por primera vez) se inicializa
		 * periodo en 1
		 */
		if (instance.getPeriodicityType() == null) {
			instance.setPeriodicityType(1);
		}
		return itemsExtension;
	}

	/**
	 * Metodo que Retorna un listado de Tipos de Peridodicidad asociado a la
	 * Hoja de Termino
	 * 
	 * @return
	 */
	public List<SelectItem> getPeriodicityTypeItemsProjectProperty() {

		List<SelectItem> itemsProperties = this.getPeriodicityTypeItems();
		/*
		 * Si el elemento Es nulo (Se ingresa por primera vez) se inicializa
		 * periodo en 1
		 */
		if (instance.getPeriodicityType() == null) {
			instance.setPeriodicityType(1);
		}

		return itemsProperties;
	}

	/**
	 * Metodo que Retorna un listado de Tipos de Peridodicidad asociado al
	 * concepto
	 * 
	 * @return
	 */
	public List<SelectItem> getPeriodicityTypeItemsConcept() {

		List<SelectItem> itemsConcepts = this.getPeriodicityTypeItems();
		/*
		 * Si el elemento Es nulo (Se ingresa por primera vez) se inicializa
		 * periodo en 1
		 */
		if (this.getConcept() != null && this.getConcept().getPeriodicityType() == null) {
			this.getConcept().setPeriodicityType(1);
		}

		if (this.getConceptSelected() != null && this.getConceptSelected().getPeriodicityType() == null) {
			this.getConceptSelected().setPeriodicityType(1);
		}

		if (this.increasedEdit != null && this.increasedEdit.getPeriodicityType() == 0) {
			this.increasedEdit.setPeriodicityType(1);
		}

		if (this.increased != null && this.increased.getPeriodicityType() == 0) {
			this.increased.setPeriodicityType(1);
		}

		return itemsConcepts;
	}

	public List<ConcepTemplate> getConcepTemplateList() {
		ConcepTemplateList Template = new ConcepTemplateList();
		Template.getResultList().add(0, new ConcepTemplate());
		ConcepTemplateList Template1 = new ConcepTemplateList();
		Template1 = Template;

		return Template1.getResultList();
	}

	public void FillTemplate() {
		if (template != null) {
			this.getConcept().setName(template.getName());
			this.getConcept().setPrintDescription(template.getPrintDescription());
			this.getConcept().setExpression(template.getExpression());
			this.getConcept().setEarlyPayment(template.getIsEarlyPayment());
		}
	}

	public ConcepTemplate template;

	public ConcepTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ConcepTemplate template) {
		this.template = template;
	}

	/**
	 * Método que Lista Modulos de Contributión Asociados a un Activo
	 * 
	 * @return
	 */
	public List<ContributionModule> contributionModuleList() {
		if (instance != null && instance.getRealProperty() != null) {
			Query q = this.getEntityManager().createQuery("from ContributionModule where realProperty =  ?");
			q.setParameter(1, instance.getRealProperty());

			@SuppressWarnings("unchecked")
			List<ContributionModule> list = (List<ContributionModule>) q.getResultList();
			if (list != null && !list.isEmpty()) {
				if (this.getConcept().getContributionModule() == null) {
					this.getConcept().setContributionModule((list.get(0)));
				}
				if (this.getConceptSelected().getContributionModule() == null) {
					this.getConceptSelected().setContributionModule((list.get(0)));

				}
			}

			return list;
		}
		return null;

	}

	/**
	 * Método que retorna si se debe pintar Lista de Iva.
	 * 
	 * @return
	 */
	public Boolean printTaxList() {

		/* Se busca el Listado de De Responsabilidades Tributarias */
		String taxLiabilitiesBiller[] = this.instance.getBiller().getTaxLiabilities().split(",");

		for (int i = 0; i < taxLiabilitiesBiller.length; i++) {
			if (taxLiabilitiesBiller[i].equals("23")) {
				return false;
			}
		}

		return true;

	}

	/**
	 * Método que Lista los tipos de Ivas Existentes.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RetentionRateAccount> taxList() {
		Query q = this.getEntityManager().createQuery(" FROM RetentionRateAccount r where r.retentionRate.id =  ? ");
		q.setParameter(1, RetentionRate.RETENTION_RATE_IVA);

		return (List<RetentionRateAccount>) q.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<RetentionRateAccount> stamptaxList() {
		Query qw = this.getEntityManager().createQuery(" FROM RetentionRateAccount r where r.retentionRate.id =  ? ");
		qw.setParameter(1, RetentionRate.RETENTION_RATE_TIMBRE);

		return (List<RetentionRateAccount>) qw.getResultList();

	}

	public String getTableStateClause() {
		return tableStateClause;
	}

	public void setTableStateClause(String tableStateClause) {
		this.tableStateClause = tableStateClause;
	}

	/**
	 * Método que Lista las retenciones que se pueden asociar al concepto.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Object[]> getListTaxConfiguration() {

		if (this.conceptSelected != null && this.listTaxConfiguration == null) {

			this.listTaxConfiguration = new ArrayList<Object[]>();
			List<TaxConfiguration> taxList = null;
			if (getInstance().getBilled() != null) {
				List<Integer> taxLiabilityBiller = new ArrayList<Integer>();
				/**
				 * Se verifia si la HT, es por mandato en ese caso las
				 * responsabilidades tributarias se Toma del proyecto.
				 */

				if (getInstance().isMandate()) {
					log(Level.INFO,"La HT es por Mandato, Se toman Responsabilidades tributarias del proyecto");
					taxLiabilityBiller = getInstance().getProject().getBusinessEntity().getTaxLiability();
				} else {
					taxLiabilityBiller = getInstance().getBiller().getTaxLiability();
				}
				/*
				 * Se extrae configuracion de impuesto dependiendo de la
				 * configuracion Tributaria del Facturado y el Facturador
				 */

				String sql = "SELECT  DISTINCT (tx)  FROM TaxConfiguration tx where (tx.taxliabilitiesByBilled.id IN (" + toStringSepareteComma(getInstance().getBilled().getTaxLiability()) + ")  " + "and tx.taxliabilitiesByBiller.id IN (" + toStringSepareteComma(taxLiabilityBiller) + ") ) ";
				sql += " and   (case when tx.retentionRate.id <> ? then 1  ELSE (CASE when ? =  ? then 1 else 0 end) end) = 1   ";
				sql += " AND tx.retentionRate.subcategoryFrom IS NULL";
				Query query = getEntityManager().createQuery(sql);
				query.setParameter(1, RetentionRate.RETENTION_RATE_RTEICA);
				query.setParameter(2, ((getInstance().getBilled().getCity() == null) ? 0 : getInstance().getBilled().getCity().getId()));
				query.setParameter(3, ((getInstance().getBiller().getCity() == null) ? 0 : getInstance().getBiller().getCity().getId()));

				log(Level.INFO, "Billed: " + taxLiabilityBiller + " - Biller: " + getInstance().getBiller().getTaxLiability());

				taxList = (List<TaxConfiguration>) query.getResultList();

			} else {

				/*
				 * Se extrae configuracion de impuesto dependiendo de la
				 * configuracion el Facturador (No se conoce facturado) En el
				 * Doucumento a Siggo se Expecifica que tipo de Renteneciones
				 * tienen que hacerse
				 */
				String sql = "SELECT  DISTINCT (tx) FROM TaxConfiguration tx where  tx.taxliabilitiesByBiller.id IN (" + toStringSepareteComma(getInstance().getBiller().getTaxLiability()) + ")  ";
				sql += " AND tx.retentionRate.subcategoryFrom IS NULL";
				Query query = getEntityManager().createQuery(sql);
				taxList = (List<TaxConfiguration>) query.getResultList();

			}

			ArrayList<Integer> IdRetention = new ArrayList<Integer>();

			for (TaxConfiguration tx : taxList) {

				Object obj[] = new Object[3];
				obj[0] = tx.getRetentionRate().getId();
				obj[1] = tx.getRetentionRate().getId();

				if (IdRetention.size() > 0) {
					if (IdRetention.contains(tx.getRetentionRate().getId()) == false)
						this.listTaxConfiguration.add(obj);
					IdRetention.add(tx.getRetentionRate().getId());
				} else {
					IdRetention.add(tx.getRetentionRate().getId());
					this.listTaxConfiguration.add(obj);
				}

				/*
				 * Se verifica si el concepto tiene asociada esa retencion
				 */
				// Query q =
				// getEntityManager().createQuery("FROM RetentionRate rr WHERE rr.subcategoryFrom.id != ? and rr.retantion=true");
				// q.setParameter(1,
				// RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS);
				// List<RetentionRate>temp=q.getResultList();
			}
		}

		return this.listTaxConfiguration;

	}

	public String toStringSepareteComma(List<Integer> taxLiabilityBilled) {
		String res = "";
		String separator = "";
		for (Integer idTaxLiability : taxLiabilityBilled) {
			res += separator + idTaxLiability;
			separator = ",";
		}
		return res;
	}

	public String getMessageDate() {
		return messagedate;
	}

	public void setMessageDate(String messageDate) {
		this.messagedate = messageDate;
	}

	public void ObligationsStartDateValidation() {
		this.messagedate = "";
		this.instance.getEndDate();
		if (this.instance.getSubscriptionDate() != null && this.instance.getObligationsStartDate() != null) {
			if (this.instance.getObligationsStartDate().before(this.instance.getSubscriptionDate()) == true) {
				this.messagedate = "La fecha de inicio de obligaciones debe ser mayor o igual que la fecha de subscripcion";
			}
		}

		/*
		 * La siguiente validacion se hace, para evitar que el concepto se
		 * configure como COBRO INMEDIATO. Ya que si la fecha de inicio de
		 * obligaciones es mayor a la fecha de hoy, no se debe permitir crear un
		 * concepto con cobro inmediato.
		 */
		if (this.instance != null && this.concept != null && this.instance.getObligationsStartDate() != null) {
			Calendar obliCalendar = Calendar.getInstance();
			obliCalendar.setTime(this.instance.getObligationsStartDate());
			Calendar today = Calendar.getInstance();
			if (obliCalendar.compareTo(today) > 0) {
				this.concept.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_NOT_SET);
			}
		}
	}

	public String getMessagedateEndConcept() {
		ConceptEndDateValidation();
		return messagedateEndConcept;
	}

	public void setMessagedateEndConcept(String messagedateEndConcept) {
		this.messagedateEndConcept = messagedateEndConcept;
	}

	public void ConceptEndDateValidation() {
		if (this.instance.getExpirationDate() != null && this.getConcept().getEndDateView() != null) {
			Calendar sumday = Calendar.getInstance();
			sumday.setTime(this.instance.getExpirationDate());
			sumday.add(Calendar.DAY_OF_MONTH, 1);

			if (sumday.getTime().before(this.getConcept().getEndDateView()) == true) {
				this.messagedateEndConcept = "La fecha de finalizacion del concepto debe ser menor o igual que la fecha de Caducidad de la hoja de termino";
			} else {
				this.messagedateEndConcept = "";
			}
		}
	}

	/**
	 * Lista que tipos de retenciones estan asociadoas a una retenecion
	 * 
	 * @param retentationRate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RetentionRateAccount> getRetentionRateAccounts(Object retentationRate) {

		Query query = getEntityManager().createQuery("FROM RetentionRateAccount WHERE retentionRate.id = ? and biller = ?");
		query.setParameter(1, (Integer) retentationRate);
		query.setParameter(2, getInstance().getBiller());
		List<RetentionRateAccount> temp = query.getResultList();
		if (!temp.isEmpty()) {
			this.RetentionRateAccounts.add(temp.get(0));
		}
		return temp;
	}

	/**
	 * Setea el tipo de retencion que se esta pintadando en la tabla, Para
	 * retornar los retenciones asociadas a ese tipo
	 * 
	 * @param retentionRateAccount
	 * @return
	 */
	public Object settRetenetionRateAccount(Object retentionRate) {

		retention = (Integer) retentionRate;

		return retentionRate;
	}

	/**
	 * Retorna el nombre de la retencion
	 * 
	 * @param retentionRate
	 * @return
	 */
	public String getNameRetenetionRate(Object retentionRate) {
		retentionsToAsigne.add((Integer) retentionRate);
		Query query = getEntityManager().createQuery("FROM RetentionRate WHERE id = ?  ");
		query.setParameter(1, (Integer) retention);
		@SuppressWarnings("unchecked")
		List<RetentionRate> retentionRateList = (List<RetentionRate>) query.getResultList();
		return retentionRateList.get(0).getName();

	}

	/**
	 * Retorna la retencion asociada a un un tipo de retencion
	 * 
	 * @return
	 */
	public RetentionRateAccount getRetentionRateAccount() {

		if (this.retention != null && this.conceptSelected != null && this.retention > 0 && this.updateRetention) {

			Query query = getEntityManager().createQuery("FROM ConceptRetentionRateAccount WHERE concept.id = ? and retentionRateAccount.retentionRate.id = ? ");
			query.setParameter(1, this.conceptSelected.getId());

			query.setParameter(2, retention);

			@SuppressWarnings("unchecked")
			List<ConceptRetentionRateAccount> listRta = (List<ConceptRetentionRateAccount>) query.getResultList();
			for (ConceptRetentionRateAccount rta : listRta) {
				if (!rta.getRetentionRateAccount().getName().equals("-")) {

					RetentionRateAccount delete = null;
					for (RetentionRateAccount retention : this.RetentionRateAccounts) {
						if (rta.getRetentionRateAccount() != null) {
							if (retention.getRetentionRate().getId() == rta.getRetentionRateAccount().getRetentionRate().getId()) {
								delete = retention;
								// this.RetentionRateAccounts.remove(retention);
							}
						}
					}
					if (delete != null) {
						log(Level.INFO, "Elimando Retencion: " + rta.getRetentionRateAccount().getId());
						this.RetentionRateAccounts.remove(delete);
					}

					this.retentionRateAccount = rta.getRetentionRateAccount();

					this.RetentionRateAccounts.add(rta.getRetentionRateAccount());
				}

				return rta.getRetentionRateAccount();
			}

		}
		/* Temporal */
		// log(Level.INFO, "Retornando Retencion0: "+this.retentionRateAccount.getId());
		return this.retentionRateAccount;
	}

	public ConceptRetentionRateAccount getConceptRetentionRateAccount(Integer retention) {

		Query query = getEntityManager().createQuery("FROM ConceptRetentionRateAccount WHERE concept.id = ? and retentionRateAccount.retentionRate.id = ? ");
		query.setParameter(1, this.conceptSelected.getId());
		query.setParameter(2, retention);
		query.setMaxResults(1);
		List<?> listRta = query.getResultList();
		if (!listRta.isEmpty())
			return (ConceptRetentionRateAccount) listRta.get(0);
		else
			return null;
	}

	public void showvalueConceptSelect() {
		log(Level.INFO, "Valor de la expression:  " + conceptSelected.getExpression());
	}

	public void refreshConcept() {
		cleanAccount(conceptSelected);
		getEntityManager().refresh(conceptSelected);
		this.validateExpression = " ";
		cleanConceptSelected();
	}

	/**
	 * Limpia las cuentas que tiene id 0 en el concepto
	 * 
	 * @param concept
	 */
	private void cleanAccount(Concept concept) {
		List<ConceptRetentionRateAccount> accounts = concept.getConceptRetentionRateAccounts();
		List<ConceptRetentionRateAccount> accountsRemove = new ArrayList<ConceptRetentionRateAccount>();
		for (ConceptRetentionRateAccount account : accounts) {
			if (account.getId() == 0)
				accountsRemove.add(account);
		}

		for (ConceptRetentionRateAccount account : accountsRemove) {
			accounts.remove(account);
		}
	}

	public boolean conceptsaved() {
		if (conceptSelected != null && conceptSelected.getId() != 0) {
			return false;
		}
		return true;
	}

	public void termSheetTemplate() {
		if (this.instance.getSubjectContrat() == null) {
			this.instance.setSubjectContrat("");
		} else
			this.instance.setSubjectContrat(this.instance.getSubjectContrat());

	}

	static class HeaderFooter extends PdfPageEventHelper {

		private ProjectProperty projectProperty;

		public HeaderFooter(ProjectProperty projectProperty) {
			this.projectProperty = projectProperty;
		}

		public void onEndPage(PdfWriter writer, Document document) {

			String fileIMAGE2 = "img/pie_Terranum.jpg";

			String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
			path = path.substring(0, path.lastIndexOf("/")) + "/";

			PdfContentByte cbhead = null;

			try {

				/**
				 * Tabla de la información debajo de los logos de la factura
				 **/
				String fontFile = path + "Font/TAHOMA.TTF";
				BaseFont bf = BaseFont.createFont(fontFile, BaseFont.IDENTITY_H, true);

				Font font1 = new Font(bf, 7, Font.NORMAL);
				PdfPTable table6 = new PdfPTable(2);
				table6.setTotalWidth(new float[] { 230f, 300f });
				table6.setWidthPercentage(100);
				table6.setHorizontalAlignment(Element.ALIGN_LEFT);

				PdfPCell cell1;

				if (projectProperty.isMandate() == true) {

					BusinessEntity mandante = projectProperty.getProject().getBusinessEntity();
					cell1 = new PdfPCell(new Paragraph(mandante.getNameBusinessEntity() + "\n" + (mandante.getAddresses() != null && !mandante.getAddresses().isEmpty() ? mandante.getAddresses().iterator().next().toString() : "") + (mandante.getPhoneNumbers() != null && !mandante.getPhoneNumbers().isEmpty() ? mandante.getPhoneNumbers().iterator().next().toString() : "") + "\n" + (mandante.getCity() != null ? mandante.getCity().getName() : "") + " - " + (mandante.getAddresses() != null && !mandante.getAddresses().isEmpty() ? mandante.getAddresses().iterator().next().getCity().getRegion().getCountry().getName() : "") + "\n" + mandante.getIdNumber() + (mandante.getIdType() == 31 ? (" - " + mandante.getVerificationNumber()) : ""), font1));
				} else {
					cell1 = new PdfPCell(new Paragraph(projectProperty.getBiller().getNameBusinessEntity() + "\n" + projectProperty.getBillerAddress().toString() + projectProperty.getPhoneNumberByPhoneBiller().getNumber() + "\n" + projectProperty.getBillerAddress().getCity().getName() + " - " + projectProperty.getBillerAddress().getCity().getRegion().getCountry().getName() + "\n" + projectProperty.getBiller().getIdNumber() + (projectProperty.getBiller().getIdType() == 31 ? (" - " + projectProperty.getBiller().getVerificationNumber()) : ""), font1));
				}

				cell1.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell1);

				PdfPCell cell2 = new PdfPCell();
				cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				cell2.setBorder(Rectangle.NO_BORDER);
				table6.addCell(cell2);

				table6.writeSelectedRows(0, -1, PageSize.LETTER.getWidth() * 0.1f, PageSize.LETTER.getHeight() * 0.885f, writer.getDirectContent());

				if (projectProperty.isMandate() == true) {
					if (projectProperty.getProject().getBusinessEntity().getLogo() != null && projectProperty.getProject().getBusinessEntity().getLogo().length > 0) {
						Image image = Image.getInstance(projectProperty.getProject().getBusinessEntity().getLogo());
						image.setAbsolutePosition(50, 700);
						image.scaleToFit(150, 80);
						document.add(image);
					}
				} else {
					if (projectProperty.getBiller().getLogo() != null && projectProperty.getBiller().getLogo().length > 0) {
						Image image = Image.getInstance(projectProperty.getBiller().getLogo());
						image.setAbsolutePosition(50, 700);
						image.scaleToFit(150, 80);
						document.add(image);
					}
				}

				// Imagen Pie de Pagina
				Image image2 = Image.getInstance(path + fileIMAGE2);
				image2.scaleToFit(500, 500);
				image2.setAbsolutePosition(0, 0);
				image2.setAlignment(Image.ALIGN_CENTER);

				cbhead = writer.getDirectContent();
				PdfTemplate tp1 = cbhead.createTemplate(500, 250); // el
				// área
				// destinada
				// para
				// el
				// encabezado
				tp1.addImage(image2);

				cbhead.addTemplate(tp1, 40, 15);// posición
				// del
				// témplate
				// derecha
				// y
				// abajo

			} catch (BadElementException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			// Phrase headPhraseImg = new Phrase(cbhead + "",
			// FontFactory.getFont(FontFactory.TIMES_ROMAN, 7,
			// Font.NORMAL));

		}
	}

	public List<SelectItem> getPrioritysItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		String[][] types = Concept.PRIORITYS;
		for (int i = 0; i < types.length; i++) {
			SelectItem item = new SelectItem(Integer.valueOf(types[i][0]), types[i][1]);
			items.add(item);
			if (i == 0 && this.conceptSelected != null && this.conceptSelected.getPriority() == 0) {
				this.conceptSelected.setPriority(Integer.valueOf(types[i][0]));
			}
		}
		return items;
	}

	public Integer assingConsecutive() {

		return this.instance.getProject().getProjectProperties().size();
	}

	public void renewConceptEndDate() {
		if (concept != null) {
			concept.setStartDate(getInstance().getSubscriptionDate());
			concept.setPeriodicity(getInstance().getPeriodicity() != null ? new Long(getInstance().getPeriodicity()) : null);
			concept.setPeriodicityType(getInstance().getPeriodicityType());
			concept.setNumberPeriods(1);
		}
	}

	public void selectBiller() {

		if (this.instance.getProject() != null) {
			if (this.instance.isMandate() == true) {
				this.instance.setBiller(this.instance.getProject().getBusinessLine().getBusinessEntity());
			} else if (this.instance.isMandate() == false) {
				this.instance.setBiller(this.instance.getProject().getBusinessEntity());
			}
		}

	}

	public boolean isErrorOnConceptExpresion() {
		return errorOnConceptExpresion;
	}

	public void setErrorOnConceptExpresion(boolean errorOnConceptExpresion) {
		this.errorOnConceptExpresion = errorOnConceptExpresion;
	}

	@SuppressWarnings("unchecked")
	public List<Concept> getConceptList() {

		if (this.instance != null && this.conceptList == null && this.instance.getId() != 0) {
			Query q = getEntityManager().createQuery("FROM Concept con where con.projectProperty=? order by last_modification ASC");
			q.setParameter(1, this.instance);
			this.conceptList = q.getResultList();
		}
		return conceptList;
	}

	public void setConceptList(List<Concept> conceptList) {
		this.conceptList = conceptList;
	}

	public Increased getIncreasedEdit() {
		return increasedEdit;
	}

	public void setIncreasedEdit(Increased increasedEdit) {
		this.increasedEdit = increasedEdit;
	}

	public boolean isIsInCreasedEdit() {
		return isInCreasedEdit;
	}

	public void setIsInCreasedEdit(boolean isInCreasedEdit) {
		if (isInCreasedEdit) {
			this.increasedEdit = new Increased();
		} else {
			this.increasedEdit = null;
		}
		this.isInCreasedEdit = isInCreasedEdit;
	}

	public List<BillingResolution> getBillingResolutionList() {
		if (this.billingResolutionList == null) {
			if (this.instance.getBiller().getBillingResolutions() != null) {
				this.billingResolutionList = this.instance.getBiller().getBillingResolutions();
				if (!this.billingResolutionList.isEmpty() && this.instance.getBillingResolution() == null)
					this.instance.setBillingResolution(this.billingResolutionList.get(0));
			}
		}
		return billingResolutionList;
	}

	public void setBillingResolutionList(List<BillingResolution> billingResolutionList) {
		this.billingResolutionList = billingResolutionList;
	}

	public List<ConsecutiveCollectionAccount> getCollectionAccountList() {
		if (collectionAccountList == null) {
			if (this.instance.getBiller().getConsecutiveCollectionAccounts() != null) {
				this.collectionAccountList = this.instance.getBiller().getConsecutiveCollectionAccounts();
				if (!this.collectionAccountList.isEmpty() && this.instance.getConsecutiveCollectionAccount() == null)
					this.instance.setConsecutiveCollectionAccount(this.collectionAccountList.get(0));
			}

		}
		return collectionAccountList;
	}

	public void setCollectionAccountList(List<ConsecutiveCollectionAccount> collectionAccountList) {
		this.collectionAccountList = collectionAccountList;
	}

	public Set<ConsecutiveCreditNotes> consecutiveCreditNoteses() {
		Set<ConsecutiveCreditNotes> creditNotes = this.getInstance().getBiller().getConsecutiveCreditNoteses();

		if (creditNotes != null && !creditNotes.isEmpty() && this.getInstance().getCreditNotes() == null) {
			this.getInstance().setCreditNotes(creditNotes.iterator().next());
		}
		return creditNotes;
	}

	public Set<ConsecutiveAccountsBilling> consecutiveAccountsBillings() {
		Set<ConsecutiveAccountsBilling> accountsBillings = this.getInstance().getBiller().getConsecutiveAccountsBillings();
		if (accountsBillings != null && !accountsBillings.isEmpty() && this.getInstance().getAcountsBilling() == null) {
			this.getInstance().setAcountsBilling(accountsBillings.iterator().next());
		}
		return accountsBillings;

	}

	public boolean validationExtension() {
		if (this.instance.isAutomaticExtension() == true)
			return true;
		else
			return false;
	}

	public ProjectProperty getInstanceMaker(MakerChecker makerChecker) {
		Object aux = new MakerCheckerHome().getInstance(makerChecker);
		if (aux instanceof ProjectProperty) {
			ProjectProperty projectProperty = (ProjectProperty) aux;
			projectProperty = getEntityManager().merge(projectProperty);
			return projectProperty;
		}
		return null;
	}

	// ---------------------------------------------------------------
	private List<InvoiceConcept> invoiceConcepts;

	public List<InvoiceConcept> getInvoiceConcepts() {
		if (invoiceConcepts == null)
			serachInvoiceCreditNotes();
		return invoiceConcepts;
	}

	public void setInvoiceConcepts(List<InvoiceConcept> invoiceConcepts) {
		this.invoiceConcepts = invoiceConcepts;
	}

	@SuppressWarnings("unchecked")
	public void serachInvoiceCreditNotes() {
		this.invoiceConcepts = null;
		Query query = this.getEntityManager().createQuery("SELECT  DISTINCT(invCo) FROM ProjectProperty pp, InvoiceConcept invCo WHERE  pp.id = ? AND  invCo.invoiceConceptType = ?  AND  pp.id = invCo.invoice.projectProperty.id ");
		query.setParameter(1, this.instance.getId());
		query.setParameter(2, InvoiceConcept.TYPE_CREDIT_NOTE);
		this.invoiceConcepts = query.getResultList();
	}

	public String discount(InvoiceConcept invoiceConcept) {
		if (invoiceConcept.getDiscount() != null && invoiceConcept.getDiscount() == true)
			return "Descuento";
		else
			return "Nota Credito";
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

	public HtmlExtendedDataTable getTablesSystemVAriableIncreaseBindEdit() {
		return tablesSystemVAriableIncreaseBindEdit;
	}

	public void setTablesSystemVAriableIncreaseBindEdit(HtmlExtendedDataTable tablesSystemVAriableIncreaseBindEdit) {
		this.tablesSystemVAriableIncreaseBindEdit = tablesSystemVAriableIncreaseBindEdit;
	}

	public Selection getSystemVaribleSelectionIncreaseEdit() {
		return systemVaribleSelectionIncreaseEdit;
	}

	public void setSystemVaribleSelectionIncreaseEdit(Selection systemVaribleSelectionIncreaseEdit) {
		this.systemVaribleSelectionIncreaseEdit = systemVaribleSelectionIncreaseEdit;
	}

	public boolean checkService() {
		boolean serviceSelected = false;
		if (getInstance().getObjectOfContract() != null) {
			if (this.getInstance().getObjectOfContract().getId() == 3) // 3
				// =
				// Servicios
				serviceSelected = true;
			else
				serviceSelected = false;
		}
		return serviceSelected;
	}

	TreeMap<Integer, String> namesRealProperty = new TreeMap<Integer, String>();
	private String validatelenght;

	@Transactional(TransactionPropagationType.REQUIRED)
	public String getNameRealProperty(ProjectProperty projectProperty) {
		if (projectProperty.getRealProperty() != null) {
			Integer idRealProperty = projectProperty.getRealProperty().getId();
			if (namesRealProperty.containsKey(idRealProperty)) {
				return namesRealProperty.get(idRealProperty);
			} else {
				Query q = getEntityManager().createNativeQuery("SELECT r.name_property FROM real_property r WHERE r.id = ?");
				q.setParameter(1, idRealProperty);
				q.setMaxResults(1);
				List<?> names = q.getResultList();

				for (Object name : names) {
					String aux = (String) name;
					return aux;
				}
			}
		}
		return "";
	}

	public RetentionRateAccount getPaymentForm() {
		if (conceptSelected != null && conceptSelected.getAccountReceivable() != null)
			return getEntityManager().find(RetentionRateAccount.class, conceptSelected.getAccountReceivable().getId());
		return null;
	}

	public ArrayList<ConceptRetentionRateAccount> getAccotuntsItems() {
		return accotuntsItems;
	}

	public void setAccotuntsItems(ArrayList<ConceptRetentionRateAccount> accotuntsItems) {
		this.accotuntsItems = accotuntsItems;
	}

	public Selection getSelectionAccount() {
		return selectionAccount;
	}

	public void setSelectionAccount(Selection selectionAccount) {
		this.selectionAccount = selectionAccount;
	}

	public void updateCountItems(int accountType) {
		accotuntsItems = new ArrayList<ConceptRetentionRateAccount>();

		Query q = getEntityManager().createQuery("FROM RetentionRateAccount r where r.biller = ? and r.retentionRate.id = ?");
		q.setParameter(1, conceptSelected.getProjectProperty().getBiller());
		q.setParameter(2, accountType);

		List<?> accounts = q.getResultList();

		for (Object account : accounts) {
			if (account instanceof RetentionRateAccount) {
				RetentionRateAccount aux = (RetentionRateAccount) account;
				ConceptRetentionRateAccount accountConcept = new ConceptRetentionRateAccount();
				accountConcept.setConcept(conceptSelected);
				accountConcept.setRetentionRateAccount(aux);
				accotuntsItems.add(accountConcept);
			}
		}

	}

	public int getIdAccountReceivable() {
		return RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE;
	}

	public int getIdAccountBank() {
		return RetentionRate.RETENTION_RATE_BANK_ACCOUNT;
	}

	public int getIdAccountDiscuont() {
		return RetentionRate.RETENTION_RATE_DISCOUNT;
	}

	public int getIdAccountIncome() {
		return RetentionRate.RETENTION_RATE_INCOME_ACCOUNT;
	}

	public int getIdTax() {
		return RetentionRate.RETENTION_RATE_IVA;
	}

	public int getIdStamptax() {
		return RetentionRate.RETENTION_RATE_TIMBRE;
	}

	public int getIdPenaltyPortafolio() {
		return RetentionRate.RETENTION_RATE_PENALTY_OF_PORTAFOLIO;
	}

	public void onSelectionAccountChanged() {
		if (selectionAccount != null) {
			Iterator<Object> it = selectionAccount.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				tableAccountBind.setRowKey(key);

				if (tableAccountBind.isRowAvailable()) {
					ConceptRetentionRateAccount conceptAccount = (ConceptRetentionRateAccount) tableAccountBind.getRowData();
					RetentionRate retentionRate = conceptAccount.getRetentionRateAccount().getRetentionRate();

					switch (retentionRate.getId()) {
					case RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE:
						setAccountReceivable(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_BANK_ACCOUNT:
						setAccountingAccountsRecover(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_DISCOUNT:
						setAccountingAccountsEarlyPayment(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_INCOME_ACCOUNT:
						setAccountingCreditAccounts(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_IVA:
						setTax(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_TIMBRE:
						setStamptax(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_PENALTY_OF_PORTAFOLIO:
						setStamptax(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS:
						setAccountingAccountingCDOD_cuentasBancos(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_INGRESOS:
						setAccountingAccountingCDOD_cuentasIngresos(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_IVA:
						setAccountingAccountingCDOD_cuentasIVA(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR:
						setAccountingAccountingCDOD_cuentasXCobrar(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_CONTRARIO:
						setAccountingAccountingCDOD_cuentasDeudoraControlContario(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_INTERESES_DEUDAS_VENCIDAS:
						setAccountingAccountingCDOD_cuentasDeudoraInteresVencida(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_OTROSINGRESOS:
						setAccountingAccountingCDOD_cuentasOtrosIngresos(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE:
						setAccountingAccountingCDOD_cuentasReteFuente(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA:
						setAccountingAccountingCDOD_cuentasReteICA(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA:
						setAccountingAccountingCDOD_cuentasReteIVA(conceptAccount);
						break;
					default:
						conceptSelected.setAccount(retentionRate.getId(), conceptAccount);
						break;

					}

					if (retentionRate.getSubcategoryFrom() != null && retentionRate.getSubcategoryFrom().getId() == RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS) {
						this.updateTableInterestRateAccount(conceptAccount.getRetentionRateAccount());
					}
				}
			}

			selectionAccount = null;
		}
	}

	public void updateTableInterestRateAccount(RetentionRateAccount retentionRateAccount) {

		for (Object[] subcategory : this.listInterestAccountsConfiguration) {
			if (((RetentionRate) subcategory[0]).getId() == retentionRateAccount.getRetentionRate().getId()) {
				subcategory[1] = retentionRateAccount.getAccount() + " - " + retentionRateAccount.getName();
				break;
			}
		}
	}

	public List<SelectItem> getCostCenterTypes() {
		List<SelectItem> costCenterTypes = new ArrayList<SelectItem>();
		for (String[] strings : Concept.COST_CENTER_TYPES) {
			if (strings[0].equals(Concept.COST_CENTER_TYPE_RENTABLEUNIT + "") && this.instance.getObjectOfContract().getId() != ObjectOfContract.OBJECT_REALPROPERTY && this.instance.getObjectOfContract().getId() != ObjectOfContract.OBJECT_RENTABLEUNIT)
				continue;
			SelectItem si = new SelectItem(strings[0], strings[1]);
			costCenterTypes.add(si);
		}
		return costCenterTypes;
	}

	public boolean validateAccounts(Concept concept) {
		if (concept.getAccountingAccountsRecover() == null) {
			return false;
		} else if (concept.getAccountingCreditAccounts() == null) {
			return false;
		} else if (concept.getAccountReceivable() == null) {
			return false;
		}

		return true;
	}

	public boolean validateAccountsXConcept() {
		for (Concept concept : getInstance().getConcepts()) {
			if (!validateAccounts(concept)) {
				return false;
			}
		}
		return true;
	}

	public List<File> getFiles() {
		return getInstance() == null ? null : new ArrayList<File>(getInstance().getFiles());
	}

	public Integer ObjectOfContract() {
		return ObjectOfContract.OBJECT_SERVICE;
	}

	public InputStream getPdfsAttach() {
		return pdfsAttach;
	}

	public void setPdfsAttach(InputStream pdfsAttach) {
		this.pdfsAttach = pdfsAttach;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public void attachProcess() {

		try {
			if (nameFile != null && nameFile.endsWith(".pdf")) {
				PDFFile attachFile = new PDFFile();

				attachFile.setProjectProperty(this.getInstance());
				attachFile.setDate(Calendar.getInstance().getTime());

				byte[] bFile = new byte[fileSize];
				pdfsAttach.read(bFile);
				attachFile.setData(bFile);
				attachFile.setName(nameFile);

				this.getInstance().getFiles().add(attachFile);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Adjuntar Archivos", "Se adjunto el PDF \"" + nameFile + "\" exitosamente"));
			} else
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adjuntar Archivos", "No se puede adjuntar archivos que no sean de tipo PDF"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String downloadPDFAttachment(PDFFile pdfFile) {
		DownloadAttachment downloadAttachment = new DownloadAttachment();
		return downloadAttachment.download(pdfFile.getData(), pdfFile.getName());
	}

	public String deletePDFAttachment(PDFFile pdfFile) {
		this.getInstance().getFiles().remove(pdfFile);
		return null;
	}

	private void refreshFileAttachment() {
		List<File> files = this.getFiles();
		if (files.isEmpty()) {
			Query q = getEntityManager().createNativeQuery("DELETE FROM file WHERE file.project_property = ?");
			q.setParameter(1, this.getInstance());
			q.executeUpdate();
		} else {
			StringBuffer sentence = new StringBuffer("DELETE FROM file WHERE file.project_property = ? AND NOT file.id IN (");
			String separator = "";
			for (File file : files) {
				sentence.append(separator + file.getId());
				separator = ",";
			}
			sentence.append(")");
			Query q = getEntityManager().createNativeQuery(sentence.toString());
			q.setParameter(1, this.getInstance());
			q.executeUpdate();
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object[]> getListInterestAccountsConfiguration() {

		if (this.conceptSelected != null && this.listInterestAccountsConfiguration == null) {

			this.listInterestAccountsConfiguration = new ArrayList<Object[]>();
			Query q = getEntityManager().createQuery("FROM RetentionRate rr WHERE rr.subcategoryFrom.id = ?");
			q.setParameter(1, RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS);
			List<RetentionRate> temp = q.getResultList();

			List<TaxConfiguration> temp1 = new ArrayList<TaxConfiguration>(0);

			if (getInstance().getBilled() != null) {
				List<Integer> taxLiabilityBiller = new ArrayList<Integer>();
				/**
				 * Se verifia si la HT, es por mandato en ese caso las
				 * responsabilidades tributarias se Toma del proyecto.
				 */

				if (getInstance().isMandate()) {
					log(Level.INFO,"La HT es por Mandato, Se toman Responsabilidades tributarias del proyecto");
					taxLiabilityBiller = getInstance().getProject().getBusinessEntity().getTaxLiability();
				} else {
					taxLiabilityBiller = getInstance().getBiller().getTaxLiability();
				}

				String sql = "FROM TaxConfiguration tx WHERE  (tx.taxliabilitiesByBilled.id IN (" + toStringSepareteComma(getInstance().getBilled().getTaxLiability()) + ")  " + "and tx.taxliabilitiesByBiller.id IN (" + toStringSepareteComma(taxLiabilityBiller) + ")) ";
				sql += " AND   (CASE WHEN tx.retentionRate.id <> ? THEN 1  ELSE (CASE WHEN ? =  ? THEN 1 ELSE 0 END) END) = 1  ";
				q = getEntityManager().createQuery(sql);
				q.setParameter(1, RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA);
				q.setParameter(2, ((getInstance().getBilled().getCity() == null) ? 0 : getInstance().getBilled().getCity().getId()));
				q.setParameter(3, ((getInstance().getBiller().getCity() == null) ? 0 : getInstance().getBiller().getCity().getId()));

				temp1 = q.getResultList();
			}

			List<RetentionRate> retentions_to_delete = new ArrayList<RetentionRate>();
			for (RetentionRate rr : temp) {
				RetentionRate tempRetentionRate = null;
				int rrID = rr.getId();
				for (TaxConfiguration taxConfiguration : temp1) {
					if ((taxConfiguration.getRetentionRate().isRetantion() && taxConfiguration.getRetentionRate().getId() == rrID) || !rr.isRetantion()) {
						tempRetentionRate = rr;
						continue;
					}
				}
				if (tempRetentionRate == null && (rr.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA || rr.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA || rr.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE)) {
					retentions_to_delete.add(rr);

				}

			}

			for (RetentionRate retentionRate : retentions_to_delete) {
				temp.remove(retentionRate);
			}

			if (temp.isEmpty()) {
				log(Level.INFO, "No existe la categoría de cuentas contables 'CUENTAS DE ORDEN DEUDORAS' con ID " + RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS);
			} else {
				if (temp.isEmpty()) {
					log(Level.INFO, "No existen subcategorias para 'CUENTAS DE ORDEN DEUDORAS' !!");
					this.listInterestAccountsConfiguration = null;
				} else {
					for (RetentionRate subcategory : temp) {
						Object[] array = new Object[2];
						array[0] = subcategory;
						array[1] = "";
						listInterestAccountsConfiguration.add(array);
						retentionsToAsigneInteres.add(subcategory.getId());

					}
					return listInterestAccountsConfiguration;
				}
			}

		}
		return listInterestAccountsConfiguration;
	}

	public void setListInterestAccountsConfiguration(ArrayList<Object[]> listInterestAccountsConfiguration) {
		this.listInterestAccountsConfiguration = listInterestAccountsConfiguration;
	}

	public String getTableInterestRetentionRateAccountState() {
		return tableInterestRetentionRateAccountState;
	}

	public void setTableInterestRetentionRateAccountState(String tableInterestRetentionRateAccountState) {
		this.tableInterestRetentionRateAccountState = tableInterestRetentionRateAccountState;
	}

	public void clean_account(int accountConcept) {
		conceptSelected.cleanAccount(accountConcept);
	}

	public boolean getImmediatePaymentConceptCreation() {
		if (this.concept != null && this.concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_WITHOUT_CHARGE) {
			return true;
		}
		return false;
	}

	public void setImmediatePaymentConceptCreation(boolean immediatePayment) {
		if (this.concept != null && immediatePayment) {
			this.concept.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_SET_WITHOUT_CHARGE);
			this.concept.setStartDate(new Date());
			this.concept.setPeriodicity(1L);
			this.concept.setPeriodicityType(Concept.DAYS_PERIOD_TYPE);
			this.concept.setNumberPeriods(1);
			this.concept.setEarlyPayment(true);
		} else if (this.concept != null && !immediatePayment) {
			this.concept.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_NOT_SET);
		}
	}

	public boolean getImmediatePaymentConceptEdit() {
		if (this.conceptSelected != null && this.conceptSelected.getImmediatePaymentState() > Concept.IMMEDIATE_PAYMENT_NOT_SET) {
			return true;
		}
		return false;
	}

	public void setImmediatePaymentConceptEdit(boolean immediatePayment) {
		if (this.conceptSelected != null && immediatePayment) {
			this.conceptSelected.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_SET_WITHOUT_CHARGE);
			this.conceptSelected.setStartDate(new Date());
			this.conceptSelected.setPeriodicity(1L);
			this.conceptSelected.setPeriodicityType(Concept.DAYS_PERIOD_TYPE);
			this.conceptSelected.setNumberPeriods(1);
			this.conceptSelected.setEarlyPayment(true);
		} else if (this.conceptSelected != null && !immediatePayment) {
			this.conceptSelected.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_NOT_SET);
		}
	}

	public boolean isImmediatePaymentDisabled() {
		if (this.instance != null && this.instance.getObligationsStartDate() != null) {
			Calendar obliCalendar = Calendar.getInstance();
			obliCalendar.setTime(this.instance.getObligationsStartDate());
			Calendar today = Calendar.getInstance();
			if (obliCalendar.compareTo(today) > 0) {
				return true;
			}
		}
		return false;
	}

	public String disabledSaveTermSheetButton() {

		if (!this.messagedate.isEmpty()) {
			return "Holaaa";// this.messagedate;
		}

		return "";// Si no hay errores de retorna un string vacio
	}

	public boolean isInstanceObjectOfContractRealProperty() {
		if (this.instance != null && this.instance.getObjectOfContract() != null && this.instance.getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY)
			return true;
		return false;
	}

	public ObjectOfContract getObjectOfContract() {
		if (this.instance != null)
			return this.instance.getObjectOfContract();
		return null;
	}

	public void setObjectOfContract(ObjectOfContract objectOfContract) {
		this.instance.setObjectOfContract(objectOfContract);
		if (this.isInstanceObjectOfContractRealProperty()) {
			this.isContributionModule = true;
		}
		this.checkObjetOfContract();
	}

	public List<Concept> conceptnotbulkload(List<Concept> Concepts) {
		if (this.conceptosSinCargaMasiva == null && this.instance.getId() > 0) {
			this.conceptosSinCargaMasiva = new ArrayList<Concept>();
			for (Concept concept : Concepts) {
				if (!concept.getBulkLoad())
					this.conceptosSinCargaMasiva.add(concept);
			}

		}

		return this.conceptosSinCargaMasiva;
	}

	public void validatelenghtsub() {
		if (this.concept != null && this.concept.getPrintDescription() != null && this.concept.getPrintDescription().length() > 255) {
			this.setValidatelenght("Por favor digite una descripción de impresión menor a 255 caracteres");
		}

	}

	public void setValidatelenght(String validatelenght) {
		this.validatelenght = validatelenght;
	}

	public String getValidatelenght() {
		return validatelenght;
	}

	public boolean isPrintDescriptionOk() {
		if (this.concept != null && this.concept.getPrintDescription() != null && this.concept.getPrintDescription().length() > 255) {
			return true;
		}
		return false;
	}

	public boolean isNameLenghtOK() {
		if (this.concept != null && this.concept.getName() != null && this.concept.getName().length() > 255) {
			return true;
		}
		return false;
	}

	public void validateLenghtName() {
		if (this.concept != null && this.concept.getName() != null && this.concept.getName().length() > 255) {
			this.setValidatelenght("Por favor digite un nombre menor a 255 caracteres");
		}

	}

	public void validatelenghtsubEdit() {
		if (this.conceptSelected != null && this.conceptSelected.getPrintDescription() != null && this.conceptSelected.getPrintDescription().length() > 255) {
			this.setValidatelenght("Por favor digite una descripción de impresión menor a 255 caracteres");
		}

	}

	public void setValidatelenghtEdit(String validatelenght) {
		this.validatelenght = validatelenght;
	}

	public String getValidatelenghtEdit() {
		return validatelenght;
	}

	public boolean isPrintDescriptionOkEdit() {
		if (this.conceptSelected != null && this.conceptSelected.getPrintDescription() != null && this.conceptSelected.getPrintDescription().length() > 255) {
			return true;
		}
		return false;
	}

	public boolean isNameLenghtOKEdit() {
		if (this.conceptSelected != null && this.conceptSelected.getName() != null && this.conceptSelected.getName().length() > 255) {
			return true;
		}
		return false;
	}

	public void validateLenghtNameEdit() {
		if (this.conceptSelected != null && this.conceptSelected.getName() != null && this.conceptSelected.getName().length() > 255) {
			this.setValidatelenght("Por favor digite un nombre menor a 255 caracteres");
		}

	}

	public void ConceptEditEndDateValidation() {
		if (this.instance.getExpirationDate() != null && this.conceptSelected.getEndDateView() != null) {
			Calendar sumday = Calendar.getInstance();
			sumday.setTime(this.instance.getExpirationDate());
			sumday.add(Calendar.DAY_OF_MONTH, 1);

			if (sumday.getTime().before(this.getConceptSelected().getEndDateView()) == true) {
				this.editMessagedateEndConcept = "La fecha de finalizacion del concepto debe ser menor o igual que la fecha de Caducidad de la hoja de termino";
			} else {
				this.editMessagedateEndConcept = "";
			}
		}
	}

	public String getEditMessagedateEndConcept() {
		ConceptEditEndDateValidation();
		return editMessagedateEndConcept;
	}

	public boolean validateExpiration() {
		if (this.instance.getStatus() == ProjectProperty.STATUS_EXPIRED || this.instance.getStatus() == ProjectProperty.STATUS_TERMINATED) {
			return false;
		}
		return true;
	}

	public boolean isValidateImmediatePaymentState(Concept concept) {

		if (concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_ALREADY_CHARGED) {
			return true;
		}
		return false;
	}

	public void setLastModificationDate(Date lastModification) {
		this.conceptSelected.setLastModification(Calendar.getInstance().getTime());
	}

	public Date getLastModificationDate() {
		return this.conceptSelected.getLastModification();
	}

	public boolean getshowPeriod(Concept con) {
		log(Level.INFO,"getshowPeriod" + con.isShowBilledPeriod());
		return con.isShowBilledPeriod();
	}

	public void setshowPeriod(Concept con, boolean showBilledPeriod) {
		log(Level.INFO,"Setter " + showBilledPeriod);
		con.setShowBilledPeriod(showBilledPeriod);
	}

	public void setRealPropertyList(List<RealProperty> realPropertyList) {
		this.realPropertyList = realPropertyList;
	}

	public List<RealProperty> getRealPropertyList() {
		return realPropertyList;
	}

	public void checkObjetOfContract() {

		if (this.instance.getObjectOfContract().getId() != ObjectOfContract.OBJECT_RENTABLEUNIT)
			this.instance.setRentableUnit(null);

		if (this.instance.getObjectOfContract().getId() == ObjectOfContract.OBJECT_SERVICE) {
			this.instance.setRealProperty(null);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CounterConfigurationTamplate> getTemplateHasRetentionRateAccounts() {
		Query q = getEntityManager().createQuery("FROM CounterConfigurationTamplate WHERE biller = ? AND projectId1 = ?");
		q.setParameter(1, this.instance.getBiller().getId());
		q.setParameter(2, this.instance.getProject().getId());
		templateHasRetentionRateAccounts = q.getResultList();
		return templateHasRetentionRateAccounts;
	}

	public void setTemplateHasRetentionRateAccounts(List<CounterConfigurationTamplate> templateHasRetentionRateAccounts) {
		this.templateHasRetentionRateAccounts = templateHasRetentionRateAccounts;
	}

	public CounterConfigurationTamplate getTemplateHasRetentionRateAccount() {
		return templateHasRetentionRateAccount;
	}

	public void setTemplateHasRetentionRateAccount(CounterConfigurationTamplate templateHasRetentionRateAccount) {

		this.templateHasRetentionRateAccount = templateHasRetentionRateAccount;
		log(Level.INFO,"Se setea un template");
		conceptSelected.setConceptRetentionRateAccounts(new ArrayList<ConceptRetentionRateAccount>());
		if (templateHasRetentionRateAccount != null)
			setCounterTemplateToConcept();
	}

	public void setCounterTemplateToConcept() {
		log(Level.INFO,"this.conceptSelected: " + this.conceptSelected + " templateHasRetentionRateAccount: " + templateHasRetentionRateAccount);
		if (this.conceptSelected != null && this.conceptSelected.getId() != 0 && templateHasRetentionRateAccount != null) {
			log(Level.INFO,"Entro");
			List<CounterTemplateHasRetentionRateAccount> x = this.templateHasRetentionRateAccount.getCounterTemplateHasRetentionRateAccounts();
			log(Level.INFO,x.size()+"");
			log(Level.INFO,this.conceptSelected.getId()+"");
			for (CounterTemplateHasRetentionRateAccount counterTemplateHasRetentionRateAccount : x) {
				if (this.conceptSelected.isInterestArrears() && counterTemplateHasRetentionRateAccount.getRetentionRateAccountId().getRetentionRate().getSubcategoryFrom() != null && retentionsToAsigneInteres.contains(counterTemplateHasRetentionRateAccount.getRetentionRateAccountId().getRetentionRate().getId())) {
					log(Level.INFO,"El tamaño de la lista intereses es : " + retentionsToAsigneInteres.size());
					asignarRRA(counterTemplateHasRetentionRateAccount);
					continue;
				} else if (retentionsToAsigne.contains(counterTemplateHasRetentionRateAccount.getRetentionRateAccountId().getRetentionRate().getId())) {
					asignarRRA(counterTemplateHasRetentionRateAccount);
					continue;
				} else
					log(Level.INFO,"No se agrega la cuenta: " + counterTemplateHasRetentionRateAccount.getRetentionRateAccountId().getName() + " por que pertenece al tipo: " + counterTemplateHasRetentionRateAccount.getRetentionRateAccountId().getRetentionRate().getName() + " y este no de debe ser agregado en esta conf billed biller");
			}
		} else {
			log(Level.INFO,"No entro ");
		}

	}

	public void asignarRRA(CounterTemplateHasRetentionRateAccount counterTemplateHasRetentionRateAccount) {
		log(Level.INFO,"RRA: " + counterTemplateHasRetentionRateAccount.getRetentionRateAccountId().getName());
		RetentionRateAccount rr = counterTemplateHasRetentionRateAccount.getRetentionRateAccountId();
		log(Level.INFO,"conceptSelected:" + this.conceptSelected.getId());
		log(Level.INFO,"RetentionRate:" + rr.getRetentionRate().getId());
		Query q = getEntityManager().createQuery("FROM ConceptRetentionRateAccount cr WHERE cr.concept = ? and cr.retentionRateAccount.retentionRate = ?");
		q.setParameter(1, this.conceptSelected);
		q.setParameter(2, rr.getRetentionRate());
		if (!q.getResultList().isEmpty()) {
			ConceptRetentionRateAccount account = (ConceptRetentionRateAccount) q.getResultList().get(0);
			account.setRetentionRateAccount(rr);
			conceptSelected.getConceptRetentionRateAccounts().add(account);
			log(Level.INFO,"Ya existia un crra para la rra: " + rr.getName() + " Se realiza actualización");
		} else {
			ConceptRetentionRateAccount account = new ConceptRetentionRateAccount();
			account.setConcept(conceptSelected);
			account.setRetentionRateAccount(rr);
			conceptSelected.getConceptRetentionRateAccounts().add(account);
			log(Level.INFO,"Se creo un nuevo crra. para la cuenta: " + rr.getName());
		}
	}

	// public void setCounterTemplateToConcept(int kingOfRetention) {
	// if (conceptSelected != null && conceptSelected.getId() != 0 &&
	// templateHasRetentionRateAccount != null) {
	// log(Level.INFO,"Entro");
	// List<CounterTemplateHasRetentionRateAccount> x =
	// this.templateHasRetentionRateAccount.getCounterTemplateHasRetentionRateAccounts();
	// log(Level.INFO,x.size());
	// log(Level.INFO,this.conceptSelected.getId());
	// for (CounterTemplateHasRetentionRateAccount
	// counterTemplateHasRetentionRateAccount : x) {
	// log(Level.INFO,"fu " +
	// counterTemplateHasRetentionRateAccount.getRetentionRateAccountId());
	// RetentionRateAccount rr =
	// counterTemplateHasRetentionRateAccount.getRetentionRateAccountId();
	// if (rr.getRetentionRate().getId() == kingOfRetention) {
	// log(Level.INFO,"1");
	// log(Level.INFO,this.conceptSelected.getId());
	// log(Level.INFO,rr.getRetentionRate().getId());
	// Query q =
	// getEntityManager().createQuery("FROM ConceptRetentionRateAccount cr WHERE cr.concept = ? and cr.retentionRateAccount.retentionRate = ?");
	// q.setParameter(1, this.conceptSelected);
	// q.setParameter(2, rr.getRetentionRate());
	// log(Level.INFO,"hasta qui va bien");
	// if (!q.getResultList().isEmpty()) {
	// ConceptRetentionRateAccount account = (ConceptRetentionRateAccount)
	// q.getResultList().get(0);
	// account.setRetentionRateAccount(rr);
	// log(Level.INFO,"No se creo un nuevo crra.");
	// } else {
	// ConceptRetentionRateAccount account = new ConceptRetentionRateAccount();
	// account.setConcept(conceptSelected);
	// account.setRetentionRateAccount(rr);
	// conceptSelected.getConceptRetentionRateAccounts().add(account);
	// log(Level.INFO,"Se creo un nuevo crra.");
	// }
	// }
	// }
	// }
	//
	// }

	public void setAccountReceivable(ConceptRetentionRateAccount accountReceivable) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE, accountReceivable);
	}

	public RetentionRateAccount getAccountReceivable() {
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE);
	}

	public void setAccountingAccountsRecover(ConceptRetentionRateAccount accountBank) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_BANK_ACCOUNT, accountBank);
	}

	public RetentionRateAccount getAccountingAccountsRecover() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_BANK_ACCOUNT);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_BANK_ACCOUNT);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_BANK_ACCOUNT);
	}

	@Transient
	public RetentionRateAccount getAccountingAccountsEarlyPayment() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_DISCOUNT);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_DISCOUNT);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_DISCOUNT);
	}

	@Transient
	public void setAccountingAccountsEarlyPayment(ConceptRetentionRateAccount accountDiscount) {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE);
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_DISCOUNT, accountDiscount);
	}

	@Transient
	public RetentionRateAccount getAccountingCreditAccounts() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_INCOME_ACCOUNT);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_INCOME_ACCOUNT);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_INCOME_ACCOUNT);
	}

	@Transient
	public void setAccountingCreditAccounts(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_INCOME_ACCOUNT, accountIncome);
	}

	@Transient
	public RetentionRateAccount getAccountingPenaltyPortafolio() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_PENALTY_OF_PORTAFOLIO);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_PENALTY_OF_PORTAFOLIO);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_PENALTY_OF_PORTAFOLIO);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasBancos() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasBancos(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasIngresos() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_INGRESOS);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_INGRESOS);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_INGRESOS);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasIngresos(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_INGRESOS, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasIVA() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_IVA);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_IVA);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_IVA);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasIVA(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_IVA, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasXCobrar() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasXCobrar(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasDeudoraControlContario() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_CONTRARIO);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_CONTRARIO);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_CONTRARIO);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasDeudoraControlContario(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_CONTRARIO, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasDeudoraControlInteresVencida() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_INTERESES_DEUDAS_VENCIDAS);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_INTERESES_DEUDAS_VENCIDAS);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_INTERESES_DEUDAS_VENCIDAS);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasDeudoraInteresVencida(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_INTERESES_DEUDAS_VENCIDAS, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasOtrosIngresos() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_OTROSINGRESOS);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_OTROSINGRESOS);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_OTROSINGRESOS);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasOtrosIngresos(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_OTROSINGRESOS, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasReteFuente() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasReteFuente(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasReteICA() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasReteICA(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA, accountIncome);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public RetentionRateAccount getAccountingCDOD_cuentasReteIVA() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA);
		return this.conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA);
	}

	/**
	 * SUBCATEGORIA DE Cuentas De Orden Deudora - CDOD-
	 * 
	 */
	@Transient
	public void setAccountingAccountingCDOD_cuentasReteIVA(ConceptRetentionRateAccount accountIncome) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA, accountIncome);
	}

	@Transient
	public RetentionRateAccount getTax() {
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_IVA);
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_IVA);
		return conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_IVA);
	}

	public void setTax(ConceptRetentionRateAccount tax) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_IVA, tax);
	}

	@Transient
	public RetentionRateAccount getStamptax() {
		retentionsToAsigne.add(RetentionRate.RETENTION_RATE_TIMBRE);
		// setCounterTemplateToConcept(RetentionRate.RETENTION_RATE_TIMBRE);
		return conceptSelected.searchAccount(RetentionRate.RETENTION_RATE_TIMBRE);
	}

	public void setStamptax(ConceptRetentionRateAccount Stamptax) {
		conceptSelected.setAccount(RetentionRate.RETENTION_RATE_TIMBRE, Stamptax);
	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public String getNameRentableUnit(ProjectProperty projectProperty) {
		if (projectProperty.getRentableUnit() != null) {
			return projectProperty.getRentableUnit().getName();
		}
		return "";
	}

	public void clearRentableUnit() {
		getInstance().setRentableUnit(null);
	}

}