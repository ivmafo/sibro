package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.bean.MailSender;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.ConsecutiveCreditNotes;
import org.koghi.terranvm.entity.CreditNote;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectClosure;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RecoverConcept;
import org.koghi.terranvm.entity.User_Terranvm;
import org.richfaces.component.html.HtmlExtendedDataTable;
import org.richfaces.model.selection.Selection;

@Name("invoiceHome")
public class InvoiceHome extends EntityHome<Invoice> {

	public static final int STATUS_VIGENTE = InvoiceStatus.STATUS_VIGENTE;
	public static final int STATUS_SOLICITUD_DE_REVERSION = InvoiceStatus.STATUS_SOLICITUD_DE_REVERSION;
	public static final int STATUS_REVERSION_APROBADA = InvoiceStatus.STATUS_REVERSION_APROBADA;
	public static final int STATUS_REVERSADA = InvoiceStatus.STATUS_REVERSADA;
	public static final int STATUS_FACTURAS_NO_APROBADAS = InvoiceStatus.STATUS_FACTURAS_NO_APROBADAS;
	@In
	Authenticator authenticator;

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	@In(create = true)
	BusinessEntityHome businessEntityHome;
	@In(create = true)
	ProjectPropertyHome projectPropertyHome;

	// REVERSED INVOICE
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableInvoiceBind;
	private List<InvoiceStatus> invoiceStatusList;
	private InvoiceStatus selectedInvoiceStatus;
	private Selection selectionTableInvoice;
	private List<Invoice> invoiceList;
	private String invoiceListTableState;
	private Invoice selectedInvoice;
	@In(required = false)
	public String projectFilter;
	private Project project;
	private String reversionNote;
	private boolean allInvoices;
	private Calendar filterIniDateProject;
	private Calendar filterEndDateProject;
	private String billingPeriod;

	// REVERSED INVOICE

	/**
	 * This function prints a message in log file
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	private void log(Level level, Object message) {
		BillingTools.printLog(InvoiceHome.class, level, message);
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setInvoiceId(Integer id) {
		setId(id);
	}

	public Integer getInvoiceId() {
		return (Integer) getId();
	}

	@Override
	protected Invoice createInstance() {
		Invoice invoice = new Invoice();
		return invoice;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		BusinessEntity businessEntityByBilled = businessEntityHome.getDefinedInstance();
		if (businessEntityByBilled != null) {
			getInstance().setBilled(businessEntityByBilled);
		}
		BusinessEntity businessEntityByBiller = businessEntityHome.getDefinedInstance();
		if (businessEntityByBiller != null) {
			getInstance().setBiller(businessEntityByBiller);
		}
		ProjectProperty projectProperty = projectPropertyHome.getDefinedInstance();
		if (projectProperty != null) {
			getInstance().setProjectProperty(projectProperty);
		}

	}

	public boolean isWired() {
		if (getInstance().getBilled() == null)
			return false;
		if (getInstance().getBiller() == null)
			return false;
		if (getInstance().getProjectProperty() == null)
			return false;
		if (getInstance().getInvoiceStatus() == null)
			return false;
		return true;
	}

	public Invoice getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<InvoiceConcept> getInvoiceConcepts() {
		return getInstance() == null ? null : new ArrayList<InvoiceConcept>(getInstance().getInvoiceConcepts());
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceStatus> getInvoiceStatusList() {
		if (this.invoiceStatusList == null) {
			Query query = this.getEntityManager().createQuery("FROM InvoiceStatus InSt ORDER BY InSt.id");
			List<InvoiceStatus> todosStatus = (List<InvoiceStatus>) query.getResultList();
			this.invoiceStatusList = new ArrayList<InvoiceStatus>();

			for (InvoiceStatus invoiceStatus : todosStatus) {
				switch (invoiceStatus.getId()) {
				case InvoiceStatus.STATUS_VIGENTE:
					if (this.authenticator.validateShowFunction("InvoiceList.xhtml", InvoiceStatus.PERMISSION_VIGENTE))
						this.invoiceStatusList.add(invoiceStatus);
					break;
				case InvoiceStatus.STATUS_SOLICITUD_DE_REVERSION:
					if (this.authenticator.validateShowFunction("InvoiceList.xhtml", InvoiceStatus.PERMISSION_SOLICITUD_REVERSION))
						this.invoiceStatusList.add(invoiceStatus);
					break;
				case InvoiceStatus.STATUS_REVERSION_APROBADA:
					if (this.authenticator.validateShowFunction("InvoiceList.xhtml", InvoiceStatus.PERMISSION_REVERSION_APROBADA))
						this.invoiceStatusList.add(invoiceStatus);
					break;
				case InvoiceStatus.STATUS_REVERSADA:
					if (this.authenticator.validateShowFunction("InvoiceList.xhtml", InvoiceStatus.PERMISSION_REVERSADA))
						this.invoiceStatusList.add(invoiceStatus);
					break;
				case InvoiceStatus.STATUS_FACTURAS_NO_APROBADAS:
					if (this.authenticator.validateShowFunction("InvoiceList.xhtml", InvoiceStatus.PERMISSION_FACTURAS_NO_APROBADAS))
						this.invoiceStatusList.add(invoiceStatus);
					break;

				default:
					break;
				}

			}

		}
		if (!this.invoiceStatusList.isEmpty())
			this.selectedInvoiceStatus = this.invoiceStatusList.get(0);
		return this.invoiceStatusList;
	}

	public void setInvoiceStatusList(List<InvoiceStatus> invoiceStatusList) {
		this.invoiceStatusList = invoiceStatusList;
	}

	public InvoiceStatus getSelectedInvoiceStatus() {
		return selectedInvoiceStatus;
	}

	public void setSelectedInvoiceStatus(InvoiceStatus selectedInvoiceStatus) {
		this.selectedInvoiceStatus = selectedInvoiceStatus;
	}

	public HtmlExtendedDataTable getTableInvoiceBind() {
		return tableInvoiceBind;
	}

	public void setTableInvoiceBind(HtmlExtendedDataTable tableInvoiceBind) {
		this.tableInvoiceBind = tableInvoiceBind;
	}

	@SuppressWarnings("unchecked")
	public List<Invoice> getInvoiceList() {
		log(Level.INFO, "getter Lista de facturas para reversar");
		
		if (this.invoiceList == null && this.selectedInvoiceStatus != null) {
			
			
			try {
				int projectId = 0;
				this.project = null;
				projectId = Integer.parseInt(this.projectFilter);
				if (projectId > 0) {
					Query query = this.getEntityManager().createQuery("FROM Project pr WHERE pr.id = ?");
					query.setParameter(1, projectId);
					List<Project> projects = query.getResultList();
					if (!projects.isEmpty())
						this.project = projects.get(0);
					this.calculateProjectFilterDates(this.project);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			this.invoiceList = new ArrayList<Invoice>();
			boolean approved = true;
			if (this.selectedInvoiceStatus.getId() == InvoiceStatus.STATUS_FACTURAS_NO_APROBADAS)
				approved = false;
			if (this.project != null) {

				Query query;
				if (this.selectedInvoiceStatus.getId() != InvoiceHome.STATUS_REVERSADA) {
					query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.invoiceStatus = ? AND inv.projectProperty.project = ? AND inv.approved = ?  AND 0 = ( SELECT COUNT (roc.id) FROM RecoverConcept roc WHERE roc.invoiceConcept.invoice=inv) AND inv.creationDate BETWEEN ? AND ? AND 0 = ( SELECT COUNT (ic.id) FROM InvoiceConcept ic WHERE ic.invoice=inv AND ic.invoiceConceptType=?)");
					query.setParameter(1, this.selectedInvoiceStatus);
					query.setParameter(2, this.project);
					query.setParameter(3, approved);
					query.setParameter(4, this.filterIniDateProject.getTime());
					query.setParameter(5, this.filterEndDateProject.getTime());
					query.setParameter(6, InvoiceConcept.TYPE_CREDIT_NOTE);
				} else {
					query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.invoiceStatus = ? AND inv.projectProperty.project = ? AND inv.approved = ?  AND 0 = ( SELECT COUNT (roc.id) FROM RecoverConcept roc WHERE roc.invoiceConcept.invoice=inv) AND inv.creationDate BETWEEN ? AND ? ");
					query.setParameter(1, this.selectedInvoiceStatus);
					query.setParameter(2, this.project);
					query.setParameter(3, approved);
					query.setParameter(4, this.filterIniDateProject.getTime());
					query.setParameter(5, this.filterEndDateProject.getTime());
				}
				log(Level.INFO, "Ejecutando consulta Lista de facturas para reversar");
				this.invoiceList = (List<Invoice>) query.getResultList();
			} else {

				Query query;
				if (this.selectedInvoiceStatus.getId() != InvoiceHome.STATUS_REVERSADA) {

					query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.invoiceStatus = ? AND inv.approved = ? AND 0 = ( SELECT COUNT (roc.id) FROM RecoverConcept roc WHERE roc.invoiceConcept.invoice=inv) AND 0 = ( SELECT COUNT (ic.id) FROM InvoiceConcept ic WHERE ic.invoice=inv AND ic.invoiceConceptType=?)");
					query.setParameter(1, this.selectedInvoiceStatus);
					query.setParameter(2, approved);
					query.setParameter(3, InvoiceConcept.TYPE_CREDIT_NOTE);
				} else {

					query = this.getEntityManager().createQuery("FROM Invoice inv WHERE inv.invoiceStatus = ? AND inv.approved = ? AND 0 = ( SELECT COUNT (roc.id) FROM RecoverConcept roc WHERE roc.invoiceConcept.invoice=inv)");
					query.setParameter(1, this.selectedInvoiceStatus);
					query.setParameter(2, approved);
				}

				log(Level.INFO, "Ejecutando consulta Lista de facturas para reversar 2");
				this.invoiceList = (List<Invoice>) query.getResultList();
			}
		}
		return invoiceList;
	}

	public void setInvoiceList(List<Invoice> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public String getInvoiceListTableState() {
		return invoiceListTableState;
	}

	public void setInvoiceListTableState(String invoiceListTableState) {
		this.invoiceListTableState = invoiceListTableState;
	}

	public Selection getSelectionTableInvoice() {
		return selectionTableInvoice;
	}

	public void setSelectionTableInvoice(Selection selectionTableInvoice) {
		this.selectionTableInvoice = selectionTableInvoice;
	}

	public void newSearch() {
		this.invoiceList = null;
	}

	public void newReversion() {
		this.selectedInvoice = null;
	}

	public boolean isReversionBlockAvailable() {

		for (Invoice inv : this.invoiceList)
			if (inv.isSelected())
				return true;
		return false;
	}

	public boolean isButtonReversionBlockAvailable() {
		if (this.selectedInvoiceStatus != null) {
			if ((this.selectedInvoiceStatus.getId() == STATUS_VIGENTE || this.selectedInvoiceStatus.getId() == STATUS_SOLICITUD_DE_REVERSION) && isReversionBlockAvailable())
				return true;
		}

		return false;
	}

	public void saveReversionNote() {
		InvoiceStatus is = new InvoiceStatus();
		is.setId(STATUS_SOLICITUD_DE_REVERSION);
		this.selectedInvoice.setInvoiceStatus(is);
		this.instance = this.selectedInvoice;
		this.persist();
		this.invoiceList.remove(this.selectedInvoice);
		this.quitAllSelection();
	}

	public void saveReversionNoteBlock() {
		InvoiceStatus is = new InvoiceStatus();
		is.setId(STATUS_SOLICITUD_DE_REVERSION);
		List<Integer> removeList = new ArrayList<Integer>();

		for (Invoice inv : this.invoiceList) {
			if (inv.isSelected()) {
				inv.setInvoiceStatus(is);
				inv.setNote(this.reversionNote);
				this.instance = inv;
				this.instance.setId(inv.getId());
				this.persist();
				removeList.add(inv.getId());

			}

		}
		for (Integer invId : removeList) {
			for (int i = 0; i < this.invoiceList.size(); i++) {
				if (this.invoiceList.get(i).getId() == invId) {
					this.invoiceList.remove(i);
					break;
				}
			}

		}
		this.reversionNote = null;
		this.quitAllSelection();
	}

	public void quitAllSelection() {
		for (Invoice inv : this.invoiceList) {
			inv.setSelected(false);
		}
	}

	public Invoice getSelectedInvoice() {
		return selectedInvoice;
	}

	public void setSelectedInvoice(Invoice selectedInvoice) {
		this.selectedInvoice = selectedInvoice;
	}

	public void instanceSelectedInvoice(Invoice invoice) {
		this.newReversion();
		this.selectedInvoice = invoice;
	}

	public void approveReversion(boolean approveReversion) {
		if (approveReversion) {
			InvoiceStatus is = new InvoiceStatus();
			is.setId(STATUS_REVERSION_APROBADA);
			this.selectedInvoice.setInvoiceStatus(is);
			String to = getMails(selectedInvoice);
			if (to != null) {
				MailSender mailSender = new MailSender();
				String message = "FACTURA REVERSADA";
				String subject = "ATENCION!!! Se acaba de realizar una reversión:\n";
				subject += "\nProyecto: " + this.selectedInvoice.getProjectProperty().getProject();
				subject += "\nCliente: " + this.selectedInvoice.getBilled().getNameBusinessEntity();
				subject += "\nFactura Número: " + this.selectedInvoice.getNumber();
				mailSender.set(to, message, subject, null, null);
				(new Thread(mailSender)).start();
			} else {
				log(Level.INFO, "NO SE ENCONTRARON DIRECCIONES DE CORREO ELECTRONICO PARA NOTIFICAR LA REVERSION");
			}

		} else {
			InvoiceStatus is = new InvoiceStatus();
			is.setId(STATUS_VIGENTE);
			this.selectedInvoice.setInvoiceStatus(is);
			this.selectedInvoice.setNote(null);

		}
		this.instance = this.selectedInvoice;
		this.instance.setId(this.selectedInvoice.getId());
		this.persist();
		this.invoiceList.remove(this.selectedInvoice);
	}

	private String getMails(Invoice invoice) {
		log(Level.INFO, invoice);
		log(Level.INFO, invoice.getProjectProperty());
		log(Level.INFO, invoice.getProjectProperty().getProject());
		Project project = invoice.getProjectProperty().getProject();
		Query query = this.getEntityManager().createQuery("SELECT pu.user from ProjectUser pu WHERE pu.project = ? AND pu.user.role.id = ?");
		query.setParameter(1, project);
		query.setParameter(2, 5);
		List<?> list = query.getResultList();

		String res = "";
		for (Object object : list) {
			User_Terranvm user = (User_Terranvm) object;
			res += (user.getMail() != null && !user.getMail().isEmpty() ? user.getMail() + "," : "");
		}
		if (res.isEmpty())
			return null;
		return res.substring(0, res.length() - 1);
	}

	public void approveReversionBlock(boolean approveReversion) {
		List<Integer> removeList = new ArrayList<Integer>();

		for (Invoice inv : this.invoiceList) {

			if (inv.isSelected() && approveReversion) {
				InvoiceStatus is = new InvoiceStatus();
				is.setId(STATUS_REVERSION_APROBADA);
				inv.setInvoiceStatus(is);
				inv.setNote(this.reversionNote);
				String to = getMails(inv);
				if (to != null) {
					MailSender mailSender = new MailSender();
					String message = "FACTURA REVERSADA";
					String subject = "ATENCION!!! Se acaba de realizar una reversión:\n";
					subject += "\nProyecto: " + inv.getProjectProperty().getProject();
					subject += "\nCliente: " + inv.getBilled().getNameBusinessEntity();
					subject += "\nFactura Número: " + inv.getNumber();
					mailSender.set(to, message, subject, null, null);
					(new Thread(mailSender)).start();// El envio de correo se
														// hace
														// en un nuevo hilo, de
														// lo
														// contrario demora la
														// respuesta del ajax
				} else {
					log(Level.INFO, "NO SE ENCONTRARON DIRECCIONES DE CORREO ELECTRONICO PARA NOTIFICAR LA REVERSION");
				}
				this.instance = inv;
				this.instance.setId(inv.getId());
				this.persist();
				removeList.add(inv.getId());

			} else if (inv.isSelected() && !approveReversion) {
				InvoiceStatus is = new InvoiceStatus();
				is.setId(STATUS_VIGENTE);
				inv.setInvoiceStatus(is);
				inv.setNote(null);
				this.instance = inv;
				this.instance.setId(inv.getId());
				this.persist();
				removeList.add(inv.getId());
			}

		}
		for (Integer invId : removeList) {
			for (int i = 0; i < this.invoiceList.size(); i++) {
				if (this.invoiceList.get(i).getId() == invId) {
					this.invoiceList.remove(i);
					break;
				}
			}

		}
		this.reversionNote = null;
		this.quitAllSelection();

	}

	public void recalculationBlock(boolean approveRecalculation) {
		log(Level.INFO, "");
		List<Integer> removeList = new ArrayList<Integer>();
		BillingFunctions recalculation = new BillingFunctions(this.getEntityManager());
		for (Invoice inv : this.invoiceList) {

			if (inv.isSelected() && approveRecalculation) {

				if (recalculation.recalculateReversedInvoice(inv)) {
					this.generateWholeCreditNotesForInvoice(inv);
					removeList.add(inv.getId());
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Reversión", "La factura " + inv.getNumber() + " se reversó exitosamente"));
				}

			} else if (inv.isSelected() && !approveRecalculation) {
				InvoiceStatus is = new InvoiceStatus();
				is.setId(STATUS_VIGENTE);
				inv.setInvoiceStatus(is);
				inv.setNote(null);
				this.instance = inv;
				this.instance.setId(inv.getId());
				this.persist();
				removeList.add(inv.getId());
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Reversión", "La reversión de La factura " + inv.getNumber() + " se canceló"));
			}

		}
		for (Integer invId : removeList) {
			for (int i = 0; i < this.invoiceList.size(); i++) {
				if (this.invoiceList.get(i).getId() == invId) {
					this.invoiceList.remove(i);
					break;
				}
			}

		}
		this.reversionNote = null;
		this.quitAllSelection();

	}

	public void recalculation(boolean approveRecalculation) {
		BillingFunctions recalculation = new BillingFunctions(this.getEntityManager());
		boolean recalculated = false;
		if (approveRecalculation && recalculation.recalculateReversedInvoice(this.selectedInvoice)) {
			recalculated = true;
			this.generateWholeCreditNotesForInvoice(this.selectedInvoice);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Reversión", "La factura " + this.selectedInvoice.getNumber() + " se reversó exitosamente"));

		} else {
			InvoiceStatus is = new InvoiceStatus();
			is.setId(STATUS_VIGENTE);
			this.selectedInvoice.setInvoiceStatus(is);
			this.selectedInvoice.setNote(null);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Reversión", "La reversión de La factura " + this.selectedInvoice.getNumber() + " se canceló"));

		}
		System.out.println("invoice: " + this.selectedInvoice.getId());
		this.instance = this.selectedInvoice;
		this.instance.setId(this.selectedInvoice.getId());
		System.out.println("instancia: " + this.instance.getId());
		this.persist();
		if (recalculated) {
			this.invoiceList.remove(this.selectedInvoice);
		}

	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public void generateWholeCreditNotesForInvoice(Invoice invoice) {
		log(Level.INFO, "Generando Notas Credito Totales para el invoice: " + invoice.getId());

		// //-----------------------------------------------------------------------------------------
		String consecutive = "";
		Calendar calendar = Calendar.getInstance();

		try {
			CreditNote cn = new CreditNote();

			ConsecutiveCreditNotes ccn = invoice.getProjectProperty().getCreditNotes();
			consecutive = ccn.getMin() + "";
			cn.setConsecutive(consecutive);
			cn.setCreditNoteDate(calendar.getTime());
			cn.setReason("Reversion de Factura.  " + invoice.getNote());
			cn.setValue(invoice.totalBalance());

			for (InvoiceConcept invoiceConcept : invoice.getInvoiceConcepts()) {

				if (invoiceConcept.getInvoiceConceptType() != InvoiceConcept.TYPE_CREDIT_NOTE) {

					InvoiceConcept invoiceConceptCreditNote = new InvoiceConcept();
					invoiceConceptCreditNote = (InvoiceConcept) invoiceConcept.clone();
					invoiceConceptCreditNote.setId(0);
					invoiceConceptCreditNote.setInvoiceConceptChildren(new ArrayList<InvoiceConcept>());
					invoiceConceptCreditNote.setRecoverConcepts(new ArrayList<RecoverConcept>());
					invoiceConceptCreditNote.setInvoiceConceptType(InvoiceConcept.TYPE_CREDIT_NOTE);
					invoiceConceptCreditNote.setInvoiceConceptParent(invoiceConcept);
					invoiceConceptCreditNote.setInvoice(invoice);
					invoiceConceptCreditNote.setIniPeriodDate(new Date());
					invoiceConceptCreditNote.setEndPeriodDate(new Date());
					invoiceConceptCreditNote.setLastLiquidationDate(new Date());
					invoiceConcept.setBalance(0.0);

					// invConceptCreditNote.setCreditNote(this.instance);
					invoiceConceptCreditNote.setCreditNote(cn);

					this.getEntityManager().joinTransaction();
					this.getEntityManager().persist(invoiceConcept);
					this.getEntityManager().flush();
					this.getEntityManager().joinTransaction();
					this.getEntityManager().persist(invoiceConceptCreditNote);
					this.getEntityManager().flush();
					log(Level.INFO, "Se genera Notas Credito con Id: " + invoiceConceptCreditNote.getId() + ", para el invoiceConcept: " + invoiceConcept.getId());

				}

			}

			this.getEntityManager().joinTransaction();
			this.getEntityManager().persist(ccn);
			this.getEntityManager().flush();

			this.getEntityManager().joinTransaction();
			this.getEntityManager().persist(cn);
			this.getEntityManager().flush();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Nota Credito", "La Nota Credito ha sido creada exitosamente"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nota Credito", "Se ha generado un error al crear la nota credito"));
		}

		// //-----------------------------------------------------------------------------------------

		// for (InvoiceConcept invoiceConcept : invoice.getInvoiceConcepts()) {
		//
		// if (invoiceConcept.getInvoiceConceptType() !=
		// InvoiceConcept.TYPE_CREDIT_NOTE) {
		//
		// InvoiceConcept invoiceConceptCreditNote = new InvoiceConcept();
		// invoiceConceptCreditNote = (InvoiceConcept) invoiceConcept.clone();
		// invoiceConceptCreditNote.setId(0);
		// invoiceConceptCreditNote.setInvoiceConceptChildren(new
		// ArrayList<InvoiceConcept>());
		// invoiceConceptCreditNote.setRecoverConcepts(new
		// ArrayList<RecoverConcept>());
		// invoiceConceptCreditNote.setInvoiceConceptType(InvoiceConcept.TYPE_CREDIT_NOTE);
		// invoiceConceptCreditNote.setInvoiceConceptParent(invoiceConcept);
		// invoiceConceptCreditNote.setInvoice(invoice);
		// invoiceConceptCreditNote.setIniPeriodDate(new Date());
		// invoiceConceptCreditNote.setEndPeriodDate(new Date());
		// invoiceConceptCreditNote.setLastLiquidationDate(new Date());
		// invoiceConcept.setBalance(0.0);
		// this.getEntityManager().joinTransaction();
		// this.getEntityManager().persist(invoiceConcept);
		// this.getEntityManager().flush();
		// this.getEntityManager().joinTransaction();
		// this.getEntityManager().persist(invoiceConceptCreditNote);
		// this.getEntityManager().flush();
		// log(Level.INFO,"Se genera Notas Credito con Id: " +
		// invoiceConceptCreditNote.getId() + ", para el invoiceConcept: " +
		// invoiceConcept.getId());
		//
		// }
		//
		// }

	}

	public String getReversionNote() {
		return reversionNote;
	}

	public void setReversionNote(String reversionNote) {
		this.reversionNote = reversionNote;
	}

	public boolean isAllInvoices() {
		return allInvoices;
	}

	public void setAllInvoices(boolean allInvoices) {
		this.allInvoices = allInvoices;
	}

	public void checkAllInvoices() {
		for (Invoice inv : this.invoiceList) {
			inv.setSelected(this.allInvoices);

		}
	}

	public void calculateProjectFilterDates(Project project) {
		log(Level.INFO, "=================================================================================================================== ");
		Query query = this.getEntityManager().createQuery("from ProjectClosure pc WHERE pc.project = ? ORDER BY pc.closureDate DESC");
		query.setParameter(1, project);
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
		// La fecha filtro inicial se configura al inicio del ultimo periodo
		// no cerrado para el proyecto
		this.filterIniDateProject.set(Calendar.DATE, 1);
		this.filterIniDateProject.set(Calendar.HOUR_OF_DAY, 0);
		this.filterIniDateProject.set(Calendar.MINUTE, 0);
		this.filterIniDateProject.set(Calendar.SECOND, 0);
		// La fecha filtro final se configura justo un mes despues de la
		// fecha filtro inicial
		firstBillingDate.add(Calendar.MONTH, +1);
		this.filterEndDateProject = Calendar.getInstance();
		this.filterEndDateProject.setTime(firstBillingDate.getTime());
		this.filterEndDateProject.set(Calendar.DATE, 1);
		this.filterEndDateProject.set(Calendar.HOUR_OF_DAY, 0);
		this.filterEndDateProject.set(Calendar.MINUTE, 0);
		this.filterEndDateProject.set(Calendar.SECOND, 0);
		this.filterEndDateProject.add(Calendar.DATE, -1);

		log(Level.INFO, "Fecha Final " + this.filterEndDateProject);

		String[] months = new String[12];
		months[0] = "ENERO";
		months[1] = "FEBRERO";
		months[2] = "MARZO";
		months[3] = "ABRIL";
		months[4] = "MAYO";
		months[5] = "JUNIO";
		months[6] = "JULIO";
		months[7] = "AGOSTO";
		months[8] = "SEPTIMEBRE";
		months[9] = "OCTUBRE";
		months[10] = "NOVIMEBRE";
		months[11] = "DICIEMBRE";

		this.setBillingPeriod(months[this.filterIniDateProject.get(Calendar.MONTH)]);

		log(Level.INFO, "*** FILTER DATE PROJECT ***");
		log(Level.INFO, "filter Ini Date Project: " + this.filterIniDateProject.getTime());
		log(Level.INFO, "filter End Date Project: " + this.filterEndDateProject.getTime());
		log(Level.INFO, "PERIOD: " + this.billingPeriod);

	}

	public String getBillingPeriod() {
		if (billingPeriod == null)
			billingPeriod = "";
		return billingPeriod;
	}

	public void setBillingPeriod(String billingPeriod) {
		this.billingPeriod = billingPeriod;
	}

}
