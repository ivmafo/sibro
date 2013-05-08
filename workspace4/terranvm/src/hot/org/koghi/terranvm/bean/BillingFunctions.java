package org.koghi.terranvm.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.koghi.terranvm.async.InvoiceProcessor;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.IpcAccumulated;
import org.koghi.terranvm.entity.IpcMonthly;
import org.koghi.terranvm.entity.IpcYearly;
import org.koghi.terranvm.entity.MinimunWage;
import org.koghi.terranvm.entity.ProjectClosure;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.RecoverConcept;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RentableUnitContribution;
import org.koghi.terranvm.entity.Sales;
import org.koghi.terranvm.entity.UsuryRate;

@Name("billingFunctions")
public class BillingFunctions implements Runnable {

	public static final int OPTION_RETROACTIVE = 1;

	@In(value = "#{facesContext}")
	private FacesContext facesContext;
	private EntityManager entityManager;
	private Calendar filterIniDateProject;
	private Calendar todayFirstHour;
	private Calendar todayLastHour;
	private int option;
	private BillingTools billingTools;

	public BillingFunctions() {
		Locale.setDefault(Locale.ENGLISH);
	}

	public BillingFunctions(EntityManager entityManager, int option) {
		Locale.setDefault(Locale.ENGLISH);
		this.entityManager = entityManager;
		this.billingTools = new BillingTools(entityManager);
		this.option = option;

	}

	public BillingFunctions(EntityManager entityManager) {
		Locale.setDefault(Locale.ENGLISH);
		this.entityManager = entityManager;
		this.billingTools = new BillingTools(entityManager);

	}

	public void run() {
		switch (this.option) {
		case 1:
			// this.calculateIPCRetroactive(ipc object);
			break;

		default:
			break;
		}

	}

	/**
	 * Esta función se llama para recalcular un concepto. Cuando se reversa una
	 * factura se pasa el INVOICE que fue reversado. Para marcar la nueva
	 * factura como el remplazo. El resto debe pasar nul
	 * 
	 * @param concept
	 * @param oldInvoice
	 */
	@SuppressWarnings("unchecked")
	public void recalculateConcept(Concept concept, Invoice oldInvoice) {
		this.calculateProjectFilterDates(concept);
		Query q = this.getEntityManager().createQuery("Select ic.id FROM InvoiceConcept ic WHERE ic.lastLiquidationDate >= ? AND ic.concept = ? AND ic.invoice.approved=? AND ic.invoiceConceptType = ? AND ic.invoice.invoiceStatus.id = ?");
		q.setParameter(1, this.filterIniDateProject.getTime());
		q.setParameter(2, concept);
		q.setParameter(3, false);
		q.setParameter(4, InvoiceConcept.TYPE_NORMAL);
		q.setParameter(5, InvoiceStatus.STATUS_VIGENTE);
		List<Integer> invoiceConceptsIdsList = (List<Integer>) q.getResultList();

		/*
		 * SI la lista esta vacia, quiere decir que el CRONJOB no ha calculado
		 * el concepto, lo que quiere decir alguna de estas tres opciones: 1)
		 * que el concepto no es vigente. 2) que el concepto es vigente pero no
		 * ha llegado el dia del mes en que se calcula el concepto. 3) que el
		 * concepto es nuevo y la fecha de incio es mayor a HOY y el CRON-JOB no
		 * lo ha calculado aun.
		 */
		boolean conceptCalculatedThisPeriod = false;
		if (invoiceConceptsIdsList.isEmpty()) {
			log(Level.INFO, "CONCEPTO " + concept.getId() + " no tiene Invoice_concepts");
		} else {
			conceptCalculatedThisPeriod = true;
		}
		/*
		 * La consulta de INVOICE-CONCEPTS por lo general trae un solo
		 * resultado, sin embargo cuando se usan modulos de contribucion la
		 * lista puede ser de mayor tamaño. (Un solo concepto le factura a
		 * muchas unidades arrendables)
		 */
		List<Integer> invoicesIdsList = new ArrayList<Integer>();
		for (Integer invoConId : invoiceConceptsIdsList) {
			InvoiceConcept invoiceConceptTemp = this.entityManager.find(InvoiceConcept.class, invoConId);
			if (concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_ALREADY_CHARGED && !invoiceConceptTemp.getInvoice().isApproved()) {
				concept.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_SET_WITHOUT_CHARGE);
			}
			invoicesIdsList.add(invoiceConceptTemp.getInvoice().getId());
			log(Level.INFO, "Se elmina el invoice_concept: " + invoConId);
			this.billingTools.removeObject(invoiceConceptTemp);
		}
		for (Integer invoiceId : invoicesIdsList) {
			Invoice invoiceTemp = this.entityManager.find(Invoice.class, invoiceId);
			this.entityManager.refresh(invoiceTemp);
			if (invoiceTemp.getInvoiceConcepts().isEmpty()) {
				log(Level.INFO, "Se elmina el invoice: " + invoiceId);
				this.billingTools.removeObject(invoiceTemp);
			}
		}

		if (concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_WITHOUT_CHARGE || (conceptCalculatedThisPeriod && concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_ALREADY_CHARGED)) {
			/*
			 * Si el concepto esta configurado como cobro inmediato, y el
			 * contador aprobo los cambios, se debe poner la fecha de inicio del
			 * concepto igual a la de hoy con periodicidad diaria y numero de
			 * periodos uno, para que asi el metodo de recalculacion efectue un
			 * unico cobro.
			 */
			Calendar inmediateDate = Calendar.getInstance();
			inmediateDate.add(Calendar.DAY_OF_MONTH, this.billingTools.getAnticipatedDaysForGivenDate(Calendar.getInstance()));
			concept.setStartDate(inmediateDate.getTime());
			concept.setPeriodicity(1L);
			concept.setPeriodicityType(Concept.DAYS_PERIOD_TYPE);
			concept.setNumberPeriods(1);
			concept.setEarlyPayment(true);
			this.billingTools.persistObject(concept);
		}
		this.recalculateConceptWithCronJob(concept, oldInvoice);

	}

	/**
	 * Esta funcion es el reemplazo de la antigua funcion de recalculacion, la
	 * diferencia radica en que ahora esta funcion no se encargara de la logica
	 * de calculacion, sino que invocara la funcion del CRON-JOB que hace esta
	 * operacion.
	 * 
	 * Esta funcion recibe un Concepto que se quiere Recalcular.
	 * 
	 * 
	 * @param conceptRecalculateList
	 * @param type
	 *            Para reversion 0 (CERO), para el resto 1 (UNO)
	 * @return
	 */
	public void recalculateConceptWithCronJob(Concept concept, Invoice oldInvoice) {

		long l1 = System.currentTimeMillis();
		InvoiceProcessor invoiceProcessor = new InvoiceProcessor(this.entityManager);

		log(Level.INFO, "");
		log(Level.INFO, "__________________________________________________");
		log(Level.INFO, "|                                                 |");
		log(Level.INFO, "|     RECALCULATE CONCEPT FUNCTION (START)        |");
		log(Level.INFO, "|_________________________________________________|");
		log(Level.INFO, "Concept ID: " + concept.getId());

		Calendar projectLastClosureDate = Calendar.getInstance();
		Query q = this.entityManager.createNativeQuery(" select p.id from concept c inner join project_property pp on c.project_property = pp.id inner join project p on pp.project=p.id where c.id=?");
		q.setParameter(1, concept.getId());
		Integer idProject = (Integer) q.getResultList().get(0);
		Date tempDate = this.billingTools.getProjectLastClosure(idProject);

		if (tempDate == null) {
			projectLastClosureDate.setTime(concept.getStartDate());
		} else {
			projectLastClosureDate.setTime(tempDate);
			projectLastClosureDate.set(Calendar.DAY_OF_MONTH, 1);
			log(Level.INFO, "SE va a evaluar el día: " + projectLastClosureDate.getTime());
		}
		Calendar today = Calendar.getInstance();
		log(Level.INFO, "PROJECT LAST CLOSURE DATE: " + projectLastClosureDate.getTime());
		log(Level.INFO, "FECHA REAL: " + today.getTime());

		int per = concept.getPeriodicity().intValue();
		int type = concept.getPeriodicityType();

		/*
		 * 2012-12-12 @dvaldivieso - Se inicia el ciclo en la fecha donde hay
		 * que calcular el concepto que es igual o superior a la última fecha de
		 * cierre del proyecto.
		 */
		Calendar tempCalendar = this.nextCalculationForConcept(concept, projectLastClosureDate);
		int anticipatedDays;
		if (concept.isEarlyPayment()) {
			anticipatedDays = this.billingTools.getAnticipatedDaysForGivenDate(today);
			today.add(Calendar.DAY_OF_MONTH, anticipatedDays);
		}

		/*
		 * 2012-12-12 @dvaldivieso - Se restan los días anticipados, ya que
		 * CRON-JOB los tiene en cuenta para calcular un CONCEPTO
		 */
		log(Level.INFO, "FECHA INICIO CICLO: " + tempCalendar.getTime() + ", Máxima fecha de recalculo: " + today.getTime());
		today.set(Calendar.HOUR_OF_DAY, 23);
		today.set(Calendar.MINUTE, 59);
		today.set(Calendar.SECOND, 59);
		today.set(Calendar.MILLISECOND, 999);

		while (tempCalendar.compareTo(today) <= 0) {

			Calendar temp = (Calendar) tempCalendar.clone();
			if (concept.isEarlyPayment()) {
				anticipatedDays = this.billingTools.getAnticipatedDaysForGivenDate(tempCalendar);
				if ((temp.get(Calendar.DAY_OF_MONTH) - anticipatedDays) <= 0) {
					Calendar x = (Calendar) temp.clone();
					x.add(Calendar.MONTH, -1);
					anticipatedDays = this.billingTools.getAnticipatedDaysForGivenDate(x);
				}
				log(Level.INFO, "Dias anticipados: " + anticipatedDays + ", TempCal: " + temp.getTime());
				temp.add(Calendar.DAY_OF_MONTH, -1 * anticipatedDays);
			}
			log(Level.INFO, "Recalculando concepto para: " + tempCalendar.getTime() + ",  CRONJOB date: " + temp.getTime());
			invoiceProcessor.initCronJobProcess(concept, temp.getTime(), oldInvoice);

			switch (type) {
			case Concept.DAYS_PERIOD_TYPE:
				tempCalendar.add(Calendar.DAY_OF_MONTH, per);
				break;
			case Concept.MONTHS_PERIOD_TYPE:
				tempCalendar.add(Calendar.MONTH, per);
				break;
			case Concept.YEARS_PERIOD_TYPE:
				tempCalendar.add(Calendar.YEAR, per);
				break;
			default:
				break;
			}
		}

		log(Level.INFO, "Concept ID: " + concept.getId());
		log(Level.INFO, "__________________________________________________");
		log(Level.INFO, "|                                                 |");
		log(Level.INFO, "|        SE FINALIZA RECALCULACION DE CONCEPTO    |");
		log(Level.INFO, "|_________________________________________________|");
		log(Level.INFO, "");
		long l2 = System.currentTimeMillis();
		log(Level.INFO, "RECALCULATE TIME SPENT  (MILLISECONDS): " + (l2 - l1));
	}

	/**
	 * Esta función evalua la próxima fecha de calculación de un concepto
	 * después o igual a una fecha dada
	 * 
	 * @param ini
	 * @return
	 */
	public Calendar nextCalculationForConcept(Concept concept, Calendar ini) {

		int per = concept.getPeriodicity().intValue();

		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTime(concept.getStartDate());
		tempCalendar.set(Calendar.HOUR_OF_DAY, 12);
		tempCalendar.set(Calendar.MINUTE, 0);
		tempCalendar.set(Calendar.SECOND, 0);
		tempCalendar.set(Calendar.MILLISECOND, 0);

		ini.set(Calendar.HOUR_OF_DAY, 12);
		ini.set(Calendar.MINUTE, 0);
		ini.set(Calendar.SECOND, 0);
		ini.set(Calendar.MILLISECOND, 0);

		while (tempCalendar.compareTo(ini) < 0) {

			switch (concept.getPeriodicityType()) {
			case Concept.DAYS_PERIOD_TYPE:
				tempCalendar.add(Calendar.DAY_OF_MONTH, per);
				break;
			case Concept.MONTHS_PERIOD_TYPE:
				tempCalendar.add(Calendar.MONTH, per);
				break;
			case Concept.YEARS_PERIOD_TYPE:
				tempCalendar.add(Calendar.YEAR, per);
				break;
			default:
				break;
			}

		}

		return tempCalendar;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Este metodo calcula la fecha de incio del proyecto desde e
	 */
	public void calculateProjectFilterDates(Concept concept) {

		Query query = this.getEntityManager().createQuery("from ProjectClosure pc WHERE pc.project = ? ORDER BY pc.closureDate DESC");
		query.setParameter(1, concept.getProjectProperty().getProject());
		query.setMaxResults(1);
		List<ProjectClosure> list = (List<ProjectClosure>) query.getResultList();
		Calendar firstBillingDate = Calendar.getInstance();
		if (list.isEmpty()) {
			firstBillingDate.setTime(concept.getStartDate());
		} else {
			ProjectClosure pc = list.get(0);
			firstBillingDate.setTime(pc.getClosureDate());
		}

		this.todayFirstHour = Calendar.getInstance();
		this.todayFirstHour.set(Calendar.HOUR_OF_DAY, 0);
		this.todayFirstHour.set(Calendar.MINUTE, 0);
		this.todayFirstHour.set(Calendar.SECOND, 0);
		this.todayFirstHour.set(Calendar.MILLISECOND, 0);

		this.todayLastHour = Calendar.getInstance();
		this.todayLastHour.set(Calendar.HOUR_OF_DAY, 23);
		this.todayLastHour.set(Calendar.MINUTE, 59);
		this.todayLastHour.set(Calendar.SECOND, 59);
		this.todayLastHour.set(Calendar.MILLISECOND, 999);

		this.filterIniDateProject = Calendar.getInstance();
		this.filterIniDateProject.setTime(firstBillingDate.getTime());
		/*
		 * La fecha filtro inicial se configura al inicio del ultimo periodo no
		 * cerrado para el proyecto
		 */
		this.filterIniDateProject.set(Calendar.DAY_OF_MONTH, 1);
		this.filterIniDateProject.set(Calendar.HOUR_OF_DAY, 12);
		this.filterIniDateProject.set(Calendar.MINUTE, 0);
		this.filterIniDateProject.set(Calendar.SECOND, 0);
		this.filterIniDateProject.set(Calendar.MILLISECOND, 0);

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

		log(Level.INFO, "::::::::::::::::::::::::: Fecha inicio periodo: " + this.filterIniDateProject.getTime());
		log(Level.INFO, "::::::::::::::::::::::::: Today first hour: " + this.todayFirstHour.getTime());
		log(Level.INFO, "::::::::::::::::::::::::: Today last hour: " + this.todayLastHour.getTime());

	}

	public FacesContext getFacesContext() {
		return facesContext;
	}

	public void setFacesContext(FacesContext facesContext) {
		this.facesContext = facesContext;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * This function recalculates all invoiceConcepts from given invoice
	 * 
	 * @param invoice
	 * @return
	 */
	public boolean recalculateReversedInvoice(Invoice invoice) {
		log(Level.INFO, "------------------------------------------------------------------------------: ");
		log(Level.INFO, "Reversando Invoice: " + invoice.getId());
		log(Level.INFO, "------------------------------------------------------------------------------: ");

		try {
			HashMap<Integer, Concept> conceptsToRevertList = new HashMap<Integer, Concept>();
			for (InvoiceConcept invoiceConcept : invoice.getInvoiceConcepts()) {
				invoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_REVERSED);
				this.billingTools.persistObject(invoiceConcept);
				this.entityManager.refresh(invoiceConcept);
				conceptsToRevertList.put(invoiceConcept.getConcept().getId(), invoiceConcept.getConcept());
			}

			InvoiceStatus is = new InvoiceStatus();
			is.setId(InvoiceStatus.STATUS_REVERSADA);
			invoice.setInvoiceStatus(is);
			this.billingTools.persistObject(invoice);
			this.entityManager.refresh(invoice);

			for (Concept con : conceptsToRevertList.values()) {
				if (con.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_ALREADY_CHARGED) {
					con.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_SET_WITHOUT_CHARGE);
					this.billingTools.persistObject(con);
					log(Level.INFO, "Se marca el concepto como cobro inmediato sin cobrar " + con.getId() + " esta en: " + con.getImmediatePaymentState());
				}
				this.recalculateConcept(con, invoice);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * This function recalculates all concepts (invoices not aprooved) from all
	 * term-sheets wich are asociated with given RealProperty object
	 * 
	 * @param realproperty
	 *            RealProeperty object
	 * @return true if all concepts were recalculated sucessfully, otherwise
	 *         false
	 */
	public boolean recalculateRealProperty(Integer realproperty) {
		long l0 = System.currentTimeMillis();
		try {

			this.log(Level.INFO, "");
			this.log(Level.INFO, "------------------------------------------");
			this.log(Level.INFO, "| REAL PROPERTY RECALCULATION (START)    |");
			this.log(Level.INFO, "------------------------------------------");
			this.log(Level.INFO, "");

			Query query = this.entityManager.createQuery("SELECT id FROM Concept concept WHERE concept.projectProperty.realProperty.id = ? AND concept.projectProperty.status = ? AND ? >= concept.projectProperty.obligationsStartDate AND ? <= concept.projectProperty.expirationDate AND concept.projectProperty.step = ?");
			query.setParameter(1, realproperty);
			query.setParameter(2, ProjectProperty.STATUS_APPROVED);
			query.setParameter(3, new Date());
			query.setParameter(4, new Date());
			query.setParameter(5, ProjectProperty.STEP_approved_public_accountant);
			@SuppressWarnings("unchecked")
			List<Integer> conceptList = (List<Integer>) query.getResultList();
			this.log(Level.INFO, "RECALCULANDO ACTIVO: " + realproperty);
			this.log(Level.INFO, "NUMERO DE CONCEPTOS A RECALCULAR: " + conceptList.size());
			for (Integer concepts : conceptList) {
				this.log(Level.INFO, "(RECALCULANDO ACTIVO " + realproperty + ")");
				Concept concept = getEntityManager().find(Concept.class, concepts);
				this.recalculateConcept(concept, null);
			}
			this.log(Level.INFO, "-----------------------------------------");
			this.log(Level.INFO, "|   REAL PROPERTY RECALCULATION (END)   |");
			this.log(Level.INFO, "-----------------------------------------");
			this.log(Level.INFO, "Activo " + realproperty + " - TIEMPO TOTAL (MILLISECONDS): " + (System.currentTimeMillis() - l0));
			this.log(Level.INFO, "");

			return true;
		} catch (Exception e) {
			this.log(Level.SEVERE, "Error al recalcular el activo " + realproperty + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Este método se encarga de recalcular los conceptos que usen la variables
	 * que se pasa como argumetno en la expresión para determinado ACTIVO.
	 * 
	 * @param RealProperty
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean recalculateVariable(RealProperty realProperty, String variable) {

		long time0 = System.currentTimeMillis();
		log(Level.INFO, "");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "|      RECALCULO DE VARIABLE START   |");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "FECHA REAL: " + (new Date()));
		log(Level.INFO, "VARIABLE: " + variable);

		if (realProperty != null) {
			log(Level.INFO, "ACTIVO: (" + realProperty.getId() + ") " + realProperty.getNameProperty());
		}

		try {

			StringBuilder sql = new StringBuilder();
			sql.append("FROM Concept concept WHERE ");
			sql.append("concept.projectProperty.status = ? AND ");
			sql.append("concept.projectProperty.obligationsStartDate <= ? AND ");
			sql.append("concept.projectProperty.expirationDate >= ? AND ");
			sql.append("concept.projectProperty.step = ? AND ");
			sql.append("concept.expression LIKE '%" + variable + "%'");
			if (realProperty != null) {
				sql.append(" AND concept.projectProperty.realProperty.id = ?");
			}

			Query query = this.entityManager.createQuery(sql.toString());
			query.setParameter(1, ProjectProperty.STATUS_APPROVED);
			query.setParameter(2, new Date());
			query.setParameter(3, new Date());
			query.setParameter(4, ProjectProperty.STEP_approved_public_accountant);
			if (realProperty != null) {
				query.setParameter(5, realProperty.getId());
			}

			List<Concept> conceptList = query.getResultList();

			log(Level.INFO, "____________________________________________________________________________");
			log(Level.INFO, "Cantidad de conceptos con " + variable + " en expresion: " + conceptList.size());

			for (Concept concept : conceptList) {
				this.recalculateConcept(concept, null);
			}

			sql = new StringBuilder();
			sql.append("FROM Concept concept WHERE ");
			sql.append("concept.projectProperty.status = ? AND ");
			sql.append("concept.projectProperty.obligationsStartDate <= ? AND ");
			sql.append("concept.projectProperty.expirationDate >= ? AND ");
			sql.append("concept.projectProperty.step = ? AND ");
			sql.append("concept.increased.expression LIKE '%" + variable + "%'");
			if (realProperty != null) {
				sql.append(" AND concept.projectProperty.realProperty.id = ?");
			}

			query = this.entityManager.createQuery(sql.toString());
			query.setParameter(1, ProjectProperty.STATUS_APPROVED);
			query.setParameter(2, new Date());
			query.setParameter(3, new Date());
			query.setParameter(4, ProjectProperty.STEP_approved_public_accountant);
			if (realProperty != null) {
				query.setParameter(5, realProperty.getId());
			}

			conceptList = query.getResultList();

			log(Level.INFO, "____________________________________________________________________________");
			log(Level.INFO, "Cantidad de conceptos con " + variable + " en incremento: " + conceptList.size());

			for (Concept concept : conceptList) {
				this.recalculateConcept(concept, null);
			}

		} catch (Exception e) {
			log(Level.SEVERE, "No se pudo recalcular " + variable + " " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "|      RECALCULO DE VARIABLE END     |");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "VARIABLE: " + variable);
		log(Level.INFO, "TIEMPO TOTAL MILISEGUNDOS: " + (System.currentTimeMillis() - time0));
		log(Level.INFO, "");
		return true;

	}

	/**
	 * Recalcula los incrementos que fueron calculados en el periodo de
	 * facturación de algún invoice_concept de tipo normal. Y que la expresion
	 * del incremento contenga la cadena que se pasa como argumeto. Esta pensado
	 * inicialmente para conceptos que solo tienen IPC* o SM en la formula del
	 * incremento, ya que no se pasa RentableUnitContribution RUC. (Si el
	 * incremento dependiera de VENTAS o UNIDAD ARRENDABLE se calcularía mal -
	 * CRON JOB si usa RUC - Al momento de hacer este método TERRANVM indicó que
	 * no habían incrementos con estas variables, se verificó por base de datos)
	 * 
	 * @param variable
	 * @return
	 */
	public boolean recalculateIncrements(String variable) {

		long time0 = System.currentTimeMillis();
		log(Level.INFO, "");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "|      RECALCULATE INCREMENTS START  |");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "FECHA REAL: " + (new Date()));
		log(Level.INFO, "VARIABLE:" + variable);
		if (this.billingTools == null) {
			this.billingTools = new BillingTools(entityManager);
		}

		try {

			/*
			 * 2013-01-30 @dvaldivieso: Se restringe la recalculación de
			 * incrementos al periodo que corresponde la variable que viene como
			 * argumento
			 */
			Calendar iniPeriod = Calendar.getInstance();
			iniPeriod.set(Calendar.HOUR_OF_DAY, 0);
			iniPeriod.set(Calendar.MINUTE, 0);
			iniPeriod.set(Calendar.MILLISECOND, 0);

			if (variable.equalsIgnoreCase("IPCY")) {
				iniPeriod.set(Calendar.MONTH, 0);
				iniPeriod.set(Calendar.DAY_OF_MONTH, 1);

			} else if (variable.equalsIgnoreCase("IPCM")) {
				iniPeriod.set(Calendar.DAY_OF_MONTH, 1);

			} else if (variable.equalsIgnoreCase("IPCA")) {
				iniPeriod.set(Calendar.DAY_OF_MONTH, 1);

			} else if (variable.equalsIgnoreCase("SM")) {
				iniPeriod.set(Calendar.MONTH, 0);
				iniPeriod.set(Calendar.DAY_OF_MONTH, 1);

			} else {
				log(Level.SEVERE, "WARNING!!!! Bad Variable");
				return false;
			}

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT DISTINCT(IC.concept) FROM InvoiceConcept IC WHERE ");
			sql.append("IC.invoice.invoiceStatus.id = ? AND ");
			sql.append("IC.invoiceConceptType = ? AND ");
			sql.append("IC.lastLiquidationDate >= ? AND ");
			sql.append("IC.concept.increased.lastCalculated >= IC.iniPeriodDate AND ");
			sql.append("IC.expressionIncrement LIKE '%" + variable + "%' ");

			Query query = this.entityManager.createQuery(sql.toString());
			query.setParameter(1, InvoiceStatus.STATUS_VIGENTE);
			query.setParameter(2, InvoiceConcept.TYPE_NORMAL);
			query.setParameter(3, iniPeriod.getTime());

			@SuppressWarnings("unchecked")
			List<Concept> conceptList = query.getResultList();
			for (Concept concept : conceptList) {
				Double value = concept.getValueIncreisedConcept(this.billingTools.ipcYearlyString, this.billingTools.ipcMonthlyString, Calendar.getInstance(), null, this.billingTools.ipcAccumalatedString, this.billingTools.minimumWageString);
				if (value != null) {
					log(Level.INFO, "(INCREASED) concept " + concept.getId() + ", Old Fixed value: " + concept.getFixedValue() + ", new Fixed Value: " + value);
					concept.setFixedValue(BillingTools.formatDouble(value));
					this.billingTools.persistObject(concept);
				}
			}

		} catch (Exception e) {
			log(Level.SEVERE, "Ocurrió un error en el recalculo de incrementos" + e.getMessage());
			e.printStackTrace();
			return false;
		}

		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "|      RECALCULATE INCREMENTS END    |");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "VARIABLE:" + variable);
		log(Level.INFO, "TIEMPO TOTAL MILISEGUNDOS: " + (System.currentTimeMillis() - time0));
		log(Level.INFO, "");
		return true;

	}

	public boolean calculateRetroactive(Object object, final Integer idRealProperty) {

		long time0 = System.currentTimeMillis();
		log(Level.INFO, "");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "|      RETROACTIVOS START            |");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "FECHA REAL: " + (new Date()));
		log(Level.INFO, "OBJECT:" + object);

		try {

			String ipcLABEL = "RETROACTIVO";
			Integer year = null;
			Integer month = null;
			String keyword = null;

			if (object instanceof IpcYearly) {
				year = ((IpcYearly) object).getYear().intValue();
				ipcLABEL += " IPCY " + year;
				keyword = "IPCY[";
			} else if (object instanceof IpcMonthly) {
				year = ((IpcMonthly) object).getYear().intValue();
				month = ((IpcMonthly) object).getMonthly().intValue();
				ipcLABEL += " IPCM " + year + " " + IpcMonthly.MONTHS[month];
				keyword = "IPCM[";
			} else if (object instanceof IpcAccumulated) {
				year = ((IpcAccumulated) object).getYear().intValue();
				month = ((IpcAccumulated) object).getMonthly().intValue();
				ipcLABEL += " IPCA " + year + " " + IpcAccumulated.MONTHS[month];
				keyword = "IPCA[";
			} else if (object instanceof Sales) {
				year = ((Sales) object).getSalesPeriod().getYear();
				month = ((Sales) object).getSalesPeriod().getMonth();
				ipcLABEL += " VTAS ";
				keyword = "VTAS[";
			} else if (object instanceof MinimunWage) {
				ipcLABEL += " SM ";
				keyword = "SM[";
				year = ((MinimunWage) object).getYear() - 1;

			} else {
				log(Level.SEVERE, "WARNING!!!! Bad Object");
				return false;
			}

			Calendar iniCronJobCalendar = Calendar.getInstance();
			iniCronJobCalendar.set(Calendar.DAY_OF_MONTH, 1);
			iniCronJobCalendar.set(Calendar.HOUR_OF_DAY, 0);
			iniCronJobCalendar.set(Calendar.MINUTE, 0);
			iniCronJobCalendar.set(Calendar.SECOND, 0);
			iniCronJobCalendar.set(Calendar.MILLISECOND, 0);

			Calendar endCronJobCalendar = Calendar.getInstance();
			endCronJobCalendar.set(Calendar.HOUR_OF_DAY, 23);
			endCronJobCalendar.set(Calendar.MINUTE, 59);
			endCronJobCalendar.set(Calendar.SECOND, 59);
			endCronJobCalendar.set(Calendar.MILLISECOND, 999);

			Calendar iniIPCDate = Calendar.getInstance();
			if (year != null && month == null) {
				iniIPCDate.set(Calendar.YEAR, year.intValue());
				iniIPCDate.add(Calendar.YEAR, 1);
				iniIPCDate.set(Calendar.MONTH, Calendar.JANUARY);

			} else if (year != null && month != null) {
				iniIPCDate.set(Calendar.YEAR, year.intValue());
				iniIPCDate.set(Calendar.MONTH, month.intValue());
				iniIPCDate.add(Calendar.MONTH, 1);
			}

			iniIPCDate.set(Calendar.DAY_OF_MONTH, 1);
			iniIPCDate.set(Calendar.HOUR_OF_DAY, 0);
			iniIPCDate.set(Calendar.MINUTE, 0);
			iniIPCDate.set(Calendar.SECOND, 0);
			iniIPCDate.set(Calendar.MILLISECOND, 0);

			HashMap<String, Concept> incrementedConcepts = new HashMap<String, Concept>();
			String sentence = "";

			if (idRealProperty == null) {
				sentence = "SELECT DISTINCT(invoiceConcept) FROM InvoiceConcept invoiceConcept WHERE  invoiceConcept.invoice.invoiceStatus.id = ? AND invoiceConcept.concept.projectProperty.expirationDate >= ? AND invoiceConcept.iniPeriodDate >= ?  AND (invoiceConcept.expressionConcept LIKE '%" + keyword + "%' OR invoiceConcept.expressionIncrement LIKE '%" + keyword + "%' ) AND invoiceConcept.concept.projectProperty.status = ? AND invoiceConcept.invoiceConceptType = ? AND invoiceConcept.invoice.approved = ?  ORDER BY invoiceConcept.lastLiquidationDate ASC ";
			} else {
				sentence = "SELECT DISTINCT(invoiceConcept) FROM InvoiceConcept invoiceConcept, RentableUnit ru WHERE  invoiceConcept.invoice.invoiceStatus.id = ? AND invoiceConcept.concept.projectProperty.expirationDate >= ? AND invoiceConcept.iniPeriodDate >= ?  AND (invoiceConcept.expressionConcept LIKE '%" + keyword + "%' OR invoiceConcept.expressionIncrement LIKE '%" + keyword + "%' ) AND invoiceConcept.concept.projectProperty.status = ? AND invoiceConcept.invoiceConceptType = ?  AND invoiceConcept.rentableUnitId = ru.id AND invoiceConcept.invoice.approved = ? AND ru.area.floor.construction.realProperty.id = " + idRealProperty + " ORDER BY invoiceConcept.lastLiquidationDate ASC ";
			}

			log(Level.INFO, sentence);

			Query query = this.getEntityManager().createQuery(sentence);
			query.setParameter(1, InvoiceStatus.STATUS_VIGENTE);
			query.setParameter(2, new Date());
			query.setParameter(3, iniIPCDate.getTime());
			query.setParameter(4, ProjectProperty.STATUS_APPROVED);
			query.setParameter(5, InvoiceConcept.TYPE_NORMAL);
			query.setParameter(6, true);
			@SuppressWarnings("unchecked")
			List<InvoiceConcept> invoiceConceptList = (List<InvoiceConcept>) query.getResultList();

			this.log(Level.INFO, "");
			this.log(Level.INFO, ipcLABEL + " (START)");
			this.log(Level.INFO, "");
			this.log(Level.INFO, ipcLABEL);
			this.log(Level.INFO, ipcLABEL + ", Period to calculate after " + iniIPCDate.getTime());
			this.log(Level.INFO, "CRON JOB PERIOD " + iniCronJobCalendar.getTime() + " - " + endCronJobCalendar.getTime());
			this.log(Level.INFO, "Number of InvoiceConcept to evaluate: " + invoiceConceptList.size());

			for (InvoiceConcept invoiceConcept : invoiceConceptList) {
				if (invoiceConcept.getConceptSerializable() == null) {
					this.log(Level.INFO, "No se pudo obtener el serializado para invoice_concept " + invoiceConcept.getId() + ". Se aborta retroactivo.");
					continue;
				}
				try {
					Invoice invoice = invoiceConcept.getInvoice();

					Concept concept = invoiceConcept.getConcept();

					log(Level.INFO, "NEXT: invoiceConcept " + invoiceConcept.getId() + ", Invoice " + invoice.getId() + ", concept " + concept.getId() + ", term-sheet " + concept.getProjectProperty().getId() + ", project " + concept.getProjectProperty().getProject().getId());
					String key = "" + concept.getId();
					if (!incrementedConcepts.containsKey(key)) {
						this.billingTools.persistObject(concept);
						incrementedConcepts.put(key, invoiceConcept.getConcept());
					}

					RentableUnitContribution ruc = this.billingTools.getRentableContributionRate(concept, invoiceConcept.getRentableUnitId());

					/*
					 * Se dejo de pasar OLD_CONCEPT (El concepto serializado del
					 * invoice-concept), ya que tenía muchos problemas con
					 * incrementos. Además invoice_concept queda con expression
					 * con la que se calculó y existe la tabla de históricos del
					 * concepto.
					 */
					Double newConceptValue = this.calculateConceptRetroactive(concept, invoiceConcept, ruc);

					if (newConceptValue == null) {
						log(Level.INFO, "SEVERE: Error calculating retroactive, invoiceConcept " + invoiceConcept.getId() + ", Invoice " + invoice.getId() + ", concepto " + concept.getId() + ", term-sheet " + concept.getProjectProperty().getId() + ", project " + concept.getProjectProperty().getProject().getId());
						continue;
					}
					List<InvoiceConcept> children = invoiceConcept.getInvoiceConceptChildren();
					double otherRetroactivesValue = 0.0;
					for (InvoiceConcept childInvoiceConcept : children) {
						if (childInvoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_RETROACTIVE)
							otherRetroactivesValue += childInvoiceConcept.getCalculatedCost();
						/*
						 * In case IPCY and/or IPCM and/or IPCA are use in the
						 * same expresion
						 */
					}

					log(Level.INFO, "Concept " + concept.getId() + " value calculated sucessfully (Old value " + invoiceConcept.getCalculatedCost() + ")");
					log(Level.INFO, "New Value: " + newConceptValue.doubleValue());
					log(Level.INFO, "Other retroactives values " + otherRetroactivesValue);

					double retroactiveValue = BillingTools.formatDouble(newConceptValue.doubleValue() - (invoiceConcept.getCalculatedCost() + otherRetroactivesValue));
					log(Level.INFO, invoiceConcept.getCalculatedCost() + " - (" + newConceptValue.doubleValue() + " + " + otherRetroactivesValue + ") = " + retroactiveValue);

					if (retroactiveValue <= 0.01) {
						log(Level.INFO, "WARNING: The increment for InvoiceConcept " + invoiceConcept.getId() + ", Invoice " + invoice.getId() + ", concepto " + concept.getId() + ", term-sheet " + concept.getProjectProperty().getId() + ", project " + concept.getProjectProperty().getProject().getId() + " was stopped because retroactive is <= 0 (ZERO) !!");
						continue;
					}

					Invoice newInvoice = null;
					InvoiceConcept newInvoiceConcept = (InvoiceConcept) invoiceConcept.clone();
					int rentableUnitId = 0;
					if (ruc != null) {
						rentableUnitId = ruc.getRentableUnit().getId();
					}
					List<InvoiceConcept> invoiceConceptExistingList = this.billingTools.searchForExistingInvoiceConcept(concept, rentableUnitId, iniCronJobCalendar);
					if (!invoiceConceptExistingList.isEmpty()) {
						newInvoice = null;
						for (InvoiceConcept invoCon : invoiceConceptExistingList) {
							if (!invoCon.getInvoice().isApproved() && invoCon.getInvoiceConceptType() == InvoiceConcept.TYPE_NORMAL) {
								newInvoice = invoCon.getInvoice();
								break;
							}
						}
					}

					if (ruc != null) {
						newInvoice = this.billingTools.updateInvoice(concept, newInvoice, ruc.getId());
					} else {
						newInvoice = this.billingTools.updateInvoice(concept, newInvoice, null);
					}

					newInvoiceConcept.setId(0);
					newInvoiceConcept.setInvoice(newInvoice);
					newInvoiceConcept.setInvoiceConceptParent(invoiceConcept);
					newInvoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_RETROACTIVE);
					newInvoiceConcept.setIniPeriodDate(iniCronJobCalendar.getTime());
					newInvoiceConcept.setEndPeriodDate(endCronJobCalendar.getTime());

					if (concept.isEarlyPayment()) {
						Calendar cDate = Calendar.getInstance();
						int days = billingTools.getAnticipatedDaysForGivenDate(cDate);
						cDate.add(Calendar.DAY_OF_MONTH, +days);
						newInvoiceConcept.setLastLiquidationDate(cDate.getTime());
					} else {
						newInvoiceConcept.setLastLiquidationDate(new Date());
					}

					newInvoiceConcept.setCalculatedCost(BillingTools.formatDouble(retroactiveValue));
					newInvoiceConcept.setInvoiceConceptChildren(new ArrayList<InvoiceConcept>());
					newInvoiceConcept.setRecoverConcepts(new ArrayList<RecoverConcept>());

					double calculatedTax = 0.0;
					if (concept.getTax() != null && concept.getTax().getValue() != null) {
						calculatedTax = (concept.getTax().getValue() / BillingTools.formatDouble(100)) * newInvoiceConcept.getCalculatedCost();
					}

					invoiceConcept.setExpressionConcept(concept.getExpression());
					invoiceConcept.setExpressionIncrement(concept.getIncreased() == null ? "" : concept.getIncreased().getExpression());
					newInvoiceConcept.setCalculatedTax(BillingTools.formatDouble(calculatedTax));
					newInvoiceConcept.setBalance(BillingTools.formatDouble(newInvoiceConcept.getCalculatedCost() + newInvoiceConcept.getCalculatedTax()));

					if (!concept.isSeed()) {
						concept.setFixedValue(concept.getFixedValue());
						this.billingTools.persistObject(concept);
					}
					this.billingTools.persistObject(newInvoice);
					this.billingTools.persistObject(newInvoiceConcept);
					log(Level.INFO, ipcLABEL + ". Created new Retroactive InvoiceConcept " + newInvoiceConcept.getId() + ", with invoice " + newInvoice.getId());
				} catch (Exception e) {
					log(Level.INFO, "Error retroactivos: " + e.getMessage());
					e.printStackTrace();
				}
				log(Level.INFO, "EntityManager.flush...");
				this.entityManager.flush();

			}

		} catch (Exception e) {
			log(Level.SEVERE, "Ocurrió un error en el calculo de retroactivos " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "|      RETROACTIVOS END              |");
		log(Level.INFO, "--------------------------------------");
		log(Level.INFO, "TIEMPO TOTAL MILISEGUNDOS: " + (System.currentTimeMillis() - time0));
		log(Level.INFO, "");
		return true;

	}

	/**
	 * This function calulates the new value for a concept
	 * 
	 * @param concept
	 *            Concept object that its being calculated
	 * @param invoiceConcept
	 *            Instance of InvoiceConcept
	 * @param invoice
	 *            Instance of Invoice
	 * @param ruc
	 *            RentableUnitContribution object, can be null. It depends on
	 *            Term-sheet and Concept configuration with Contribution Module
	 * @return
	 */
	public Double calculateConceptRetroactive(Concept oldConcept, InvoiceConcept invoiceConcept, RentableUnitContribution ruc) {

		try {
			Calendar iniConceptDate = Calendar.getInstance();
			Calendar endConceptDate = Calendar.getInstance();
			iniConceptDate.setTime(invoiceConcept.getIniPeriodDate());
			endConceptDate.setTime(invoiceConcept.getEndPeriodDate());

			int totalDays;
			int tempDays = totalDays = 0;
			double totalInvoiceConcept = 0.0;
			int compare;

			/*
			 * Se actualiza unidad arrendable para que saque las ventas
			 * correspondientes
			 */
			Query q = entityManager.createQuery("SELECT r FROM RentableUnit r, ProjectProperty pp WHERE pp.id = ? and pp.rentableUnit = r.id");
			q.setParameter(1, oldConcept.getProjectProperty().getId());

			List<?> result = q.getResultList();

			if (result != null && !result.isEmpty() && result.get(0) instanceof RentableUnit)
				oldConcept.getProjectProperty().setRentableUnit((RentableUnit) result.get(0));

			/* END */

			/*
			 * Se realiza incremento si es mes anticipado y es del primer día JL
			 */
			if (this.billingTools == null) {
				this.billingTools = new BillingTools(entityManager);
			}

			Calendar auxDate = Calendar.getInstance();
			auxDate = this.billingTools.operateConceptDate(oldConcept, (Calendar) iniConceptDate.clone(), false);

			if (!oldConcept.isEarlyPayment() && oldConcept.getIncreased() != null && oldConcept.getIncreased().getNextIncreased().compareTo(auxDate.getTime()) == 0) {
				this.billingTools.incrementConcept(oldConcept, iniConceptDate, ruc, true);
			}
			/* END */
			iniConceptDate.setTime(invoiceConcept.getIniPeriodDate());
			while ((compare = iniConceptDate.compareTo(endConceptDate)) <= 0) {

				boolean lastDay = (compare == 0);/* Last liquidation date */
				boolean isIncrement = (oldConcept.getIncreased() != null && oldConcept.getIncreased().getNextIncreased().compareTo(iniConceptDate.getTime()) == 0) ? true : false;
				try {
					System.out.println("INCREASED-----------> " + oldConcept.getIncreased().getNextIncreased() + ", " + oldConcept.getIncreased().getLastCalculated() + ", " + oldConcept.getIncreased().getExpression() + ", " + oldConcept.getIncreased().getId());
				} catch (Exception e) {
				}
				if (isIncrement || lastDay) {

					if (isIncrement && totalDays == 0) {
						this.billingTools.incrementConcept(oldConcept, iniConceptDate, ruc, false);

						Concept aux = entityManager.find(Concept.class, oldConcept.getId()); // Current
																								// concept
						aux.setFixedValue(oldConcept.getFixedValue());
						aux.getIncreased().setLastCalculated(oldConcept.getIncreased().getLastCalculated());
						aux.getIncreased().setNextIncreased(oldConcept.getIncreased().getNextIncreased());
						entityManager.persist(aux);

						if (lastDay) {
							tempDays++;
							Double calculatedValue = this.billingTools.calculateConceptValue(oldConcept, ruc, iniConceptDate);
							if (calculatedValue == null)
								return null;
							totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
							tempDays = 1;
						}
					} else if (isIncrement && totalDays > 0 && !lastDay) {
						Double calculatedValue = this.billingTools.calculateConceptValue(oldConcept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
						tempDays = 0;

						this.billingTools.incrementConcept(oldConcept, iniConceptDate, ruc, false);
						Concept aux = entityManager.find(Concept.class, oldConcept.getId()); // Current
																								// concept
						aux.setFixedValue(oldConcept.getFixedValue());
						aux.getIncreased().setLastCalculated(oldConcept.getIncreased().getLastCalculated());
						aux.getIncreased().setNextIncreased(oldConcept.getIncreased().getNextIncreased());
						entityManager.persist(aux);

					} else if (isIncrement && lastDay) {
						Double calculatedValue = this.billingTools.calculateConceptValue(oldConcept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
						tempDays = 1;
						this.billingTools.incrementConcept(oldConcept, iniConceptDate, ruc, false);

						Concept aux = entityManager.find(Concept.class, oldConcept.getId()); // Current
																								// concept
						aux.setFixedValue(oldConcept.getFixedValue());
						aux.getIncreased().setLastCalculated(oldConcept.getIncreased().getLastCalculated());
						aux.getIncreased().setNextIncreased(oldConcept.getIncreased().getNextIncreased());
						entityManager.persist(aux);

						calculatedValue = this.billingTools.calculateConceptValue(oldConcept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
					} else if (!isIncrement && lastDay) {

						tempDays++;
						Double calculatedValue = this.billingTools.calculateConceptValue(oldConcept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
					}

				}

				tempDays++;
				totalDays++;
				iniConceptDate.add(Calendar.DAY_OF_MONTH, +1);
			}

			totalInvoiceConcept = totalInvoiceConcept / totalDays;
			if (totalInvoiceConcept <= 0.0) {
				log(Level.WARNING, "The calculated value for Concept its <= 0 (ZERO) " + oldConcept.getId());
				return null;
			}

			return new Double(BillingTools.formatDouble(totalInvoiceConcept));

		} catch (Exception e) {
			this.log(Level.SEVERE, "ERROR, An error ocurred when calculating concept " + oldConcept.getId());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Esta función calcula el valor de un concepto. Se actualiza el incremento
	 * con la siguiente fecha a incrementar según el rango de fechas ingresadas
	 * 
	 * @param concept
	 *            Concept object that its being calculated
	 * @param iniConceptDate
	 *            start date for calculat
	 * @param endConceptDate
	 *            end date for calculate
	 * @param ruc
	 *            RentableUnitContribution object, can be null. It depends on
	 *            Term-sheet and Concept configuration with Contribution Module
	 * @return
	 */
	public Double recalculateValueConcept(Concept concept, Calendar iniConceptDate, Calendar endConceptDate, RentableUnitContribution ruc) {

		try {
			int totalDays;
			int tempDays;
			tempDays = totalDays = 0;
			double totalInvoiceConcept = 0.0;
			int compare;

			/*
			 * Se realiza incremento si es mes anticipado y es del primer día JL
			 */
			// Calendar auxDate = Calendar.getInstance();
			// auxDate.setTime(iniConceptDate.getTime());
			// auxDate = this.billingTools.operateConceptDate(concept, auxDate,
			// false);

			// if (!concept.isEarlyPayment() && concept.getIncreased() != null
			// &&
			// concept.getIncreased().getNextIncreased().compareTo(auxDate.getTime())
			// == 0) {
			// this.billingTools.incrementConcept(concept, iniConceptDate, ruc,
			// sales, true);
			// }
			/* END */

			while ((compare = iniConceptDate.compareTo(endConceptDate)) <= 0) {

				boolean lastDay = (compare == 0);/* Last liquidation date */
				boolean isIncrement = (concept.getIncreased() != null && concept.getIncreased().getNextIncreased().compareTo(iniConceptDate.getTime()) == 0) ? true : false;

				if (isIncrement) {
					this.log(Level.INFO, "INCREMENTO: " + iniConceptDate.getTime());
				}
				if (isIncrement || lastDay) {

					if (isIncrement && totalDays == 0) {
						this.billingTools.incrementConcept(concept, iniConceptDate, ruc, false);

						Concept aux = entityManager.find(Concept.class, concept.getId()); // Current
																							// concept
						aux.setFixedValue(concept.getFixedValue());
						aux.getIncreased().setLastCalculated(concept.getIncreased().getLastCalculated());
						aux.getIncreased().setNextIncreased(concept.getIncreased().getNextIncreased());
						entityManager.persist(aux);

						if (lastDay) {
							tempDays++;
							Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDate);
							if (calculatedValue == null)
								return null;
							totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
							tempDays = 1;
						}
					} else if (isIncrement && totalDays > 0 && !lastDay) {
						Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
						tempDays = 0;

						this.billingTools.incrementConcept(concept, iniConceptDate, ruc, false);
						Concept aux = entityManager.find(Concept.class, concept.getId()); // Current
																							// concept
						aux.setFixedValue(concept.getFixedValue());
						aux.getIncreased().setLastCalculated(concept.getIncreased().getLastCalculated());
						aux.getIncreased().setNextIncreased(concept.getIncreased().getNextIncreased());
						entityManager.persist(aux);

					} else if (isIncrement && lastDay) {
						Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
						tempDays = 1;
						this.billingTools.incrementConcept(concept, iniConceptDate, ruc, false);

						Concept aux = entityManager.find(Concept.class, concept.getId()); // Current
																							// concept
						aux.setFixedValue(concept.getFixedValue());
						aux.getIncreased().setLastCalculated(concept.getIncreased().getLastCalculated());
						aux.getIncreased().setNextIncreased(concept.getIncreased().getNextIncreased());
						entityManager.persist(aux);

						calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
					} else if (!isIncrement && lastDay) {
						tempDays++;
						Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDate);
						if (calculatedValue == null)
							return null;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
					}

				}

				tempDays++;
				totalDays++;
				iniConceptDate.add(Calendar.DAY_OF_MONTH, +1);
			}

			totalInvoiceConcept = totalInvoiceConcept / totalDays;
			if (totalInvoiceConcept <= 0.0) {
				log(Level.WARNING, "The calculated value for Concept its <= 0 (ZERO) " + concept.getId());
				return null;
			}

			return new Double(BillingTools.formatDouble(totalInvoiceConcept));

		} catch (Exception e) {
			this.log(Level.SEVERE, "ERROR, An error ocurred when calculating concept " + concept.getId());
			e.printStackTrace();
			return null;
		}
	}

	public int getOption() {
		return option;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public boolean recalculateInterests(UsuryRate usuryRate) {

		log(Level.INFO, "");
		log(Level.INFO, "");
		log(Level.INFO, "RECALCULATION INTEREST ( START )");
		log(Level.INFO, "");
		log(Level.INFO, "");

		Calendar iniCronJobCalendar = Calendar.getInstance();
		iniCronJobCalendar.set(Calendar.DAY_OF_MONTH, 1);
		iniCronJobCalendar.set(Calendar.HOUR_OF_DAY, 0);
		iniCronJobCalendar.set(Calendar.MINUTE, 0);
		iniCronJobCalendar.set(Calendar.SECOND, 0);
		iniCronJobCalendar.set(Calendar.MILLISECOND, 0);

		Calendar endCronJobCalendar = Calendar.getInstance();
		endCronJobCalendar.add(Calendar.MONTH, 1);
		endCronJobCalendar.set(Calendar.DAY_OF_MONTH, 1);
		endCronJobCalendar.add(Calendar.DAY_OF_MONTH, -1);
		endCronJobCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endCronJobCalendar.set(Calendar.MINUTE, 59);
		endCronJobCalendar.set(Calendar.SECOND, 59);
		endCronJobCalendar.set(Calendar.MILLISECOND, 999);

		Calendar iniCalendar = Calendar.getInstance();
		Calendar usuryCalendar = Calendar.getInstance();
		usuryCalendar.setTime(usuryRate.getDate());
		int usuryMonth = usuryCalendar.get(Calendar.MONTH);
		if (usuryMonth / 3 == 0) {
			iniCalendar.set(Calendar.MONTH, Calendar.JANUARY);
		} else if (usuryMonth / 3 == 1) {
			iniCalendar.set(Calendar.MONTH, Calendar.APRIL);
		} else if (usuryMonth / 3 == 2) {
			iniCalendar.set(Calendar.MONTH, Calendar.JULY);
		} else {
			iniCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
		}
		iniCalendar.set(Calendar.DAY_OF_MONTH, 1);
		log(Level.INFO, "USURY RATE DATE " + iniCalendar.getTime());
		log(Level.INFO, "USURY RATE (3 months period) " + usuryRate.getUsuryValue());
		log(Level.INFO, "USURY RATE (daily) " + usuryRate.getValueDiary());

		String q = "FROM InvoiceConcept ic WHERE ic.concept.projectProperty.status =  ? AND ic.concept.projectProperty.step =  ? AND ic.balance > ? AND (ic.invoiceConceptType = ? OR ic.invoiceConceptType = ?) AND ic.invoice.expirationDate >= ? AND ic.invoice.expirationDate <= ? AND ic.invoice.approved = ? AND ic.concept.interestArrears = ?";
		Query query = this.entityManager.createQuery(q);
		query.setParameter(1, ProjectProperty.STATUS_APPROVED);
		query.setParameter(2, ProjectProperty.STEP_approved_public_accountant);
		query.setParameter(3, 0.0);
		query.setParameter(4, InvoiceConcept.TYPE_NORMAL);
		query.setParameter(5, InvoiceConcept.TYPE_RETROACTIVE);
		query.setParameter(6, iniCalendar.getTime());
		query.setParameter(7, new Date());
		query.setParameter(8, true);
		query.setParameter(9, true);
		@SuppressWarnings("unchecked")
		List<InvoiceConcept> invoiceConceptExpiredList = query.getResultList();
		if (invoiceConceptExpiredList.isEmpty())
			log(Level.INFO, "There is not any InvoiceConcept to recalculate");

		log(Level.INFO, "Number of invoice concept to evaluate: " + invoiceConceptExpiredList.size());
		for (InvoiceConcept invoiceConceptExpired : invoiceConceptExpiredList) {

			try {

				this.recalculateInvoiceConceptInterest(invoiceConceptExpired, usuryRate, iniCronJobCalendar, endCronJobCalendar);
				usuryRate.setRateUsed(true);
				this.entityManager.persist(usuryRate);
			} catch (Exception e) {
				log(Level.SEVERE, "ERROR al recalcular interes InvoiceConcept(" + invoiceConceptExpired.getId() + ")" + e.getMessage());
			}

		}

		log(Level.INFO, "");
		log(Level.INFO, "");
		log(Level.INFO, "RECALCULATION INTEREST ( END )");
		log(Level.INFO, "");
		log(Level.INFO, "");

		return true;
	}

	private boolean recalculateInvoiceConceptInterest(InvoiceConcept invoiceConceptExpired, UsuryRate usuryRate, Calendar iniCronJob, Calendar endCronJob) {

		Concept concept = invoiceConceptExpired.getConcept();
		Invoice invoiceExpired = invoiceConceptExpired.getInvoice();
		Invoice invoice = null;
		InvoiceConcept invoiceConceptInterest = null;
		/*-----------------------------------------------------*/
		{/* Verify if exist and interest created */
			Query q1 = this.getEntityManager().createNativeQuery("select ruc.id from concept c inner join contribution_module cm on c.contribution_module =cm.id inner join rentable_unit_contribution ruc on ruc.contribution_module = cm.id where c.id= ? ");
			q1.setParameter(1, concept.getId());
			Integer ruc = null;

			@SuppressWarnings("unchecked")
			List<Integer> idRUCtempList = q1.getResultList();
			if (!idRUCtempList.isEmpty()) {
				ruc = idRUCtempList.get(0);
			}
			int rentableUnitId = 0;
			if (ruc != null) {
				Query q = this.getEntityManager().createNativeQuery("select ru.id from rentable_unit ru inner join rentable_unit_contribution ruc on ruc.rentable_unit = ru.id where ruc.id= ?");
				q.setParameter(1, ruc);
				rentableUnitId = (Integer) q.getResultList().get(0);
			}
			List<InvoiceConcept> invoiceConceptExistingList = this.billingTools.searchForExistingInvoiceConcept(concept, rentableUnitId, iniCronJob);
			if (!invoiceConceptExistingList.isEmpty()) {
				invoice = invoiceConceptExistingList.get(0).getInvoice();
			}
			/*-----------------------------------------------------*/
			invoice = this.billingTools.updateInvoice(concept, invoice, ruc);
			if (invoice == null) {
				log(Level.INFO, "EL FACTURADO Y EL FACTURADOR SON EL MISMO PARA EL CONCEPTO: " + concept.getId() + ")");
				return false;
			}

			log(Level.INFO, "SE CREA NUEVO INVOICE_CONCEPT Interes PARA (invoice_concept vencido: " + invoiceConceptExpired.getId() + ")");
			invoiceConceptInterest = new InvoiceConcept();
			invoiceConceptInterest = (InvoiceConcept) invoiceConceptExpired.clone();
			invoiceConceptInterest.setId(0);
			invoiceConceptInterest.setInvoiceConceptChildren(new ArrayList<InvoiceConcept>());
			invoiceConceptInterest.setRecoverConcepts(new ArrayList<RecoverConcept>());
			invoiceConceptInterest.setInvoiceConceptType(InvoiceConcept.TYPE_INTEREST);
			invoiceConceptInterest.setAppliedRate(usuryRate.getValueDiary());
			invoiceConceptInterest.setInvoiceConceptParent(invoiceConceptExpired);
			invoiceConceptInterest.setInvoice(invoice);
			invoiceConceptInterest.setCalculatedCost(0.0);
			Calendar iniCalendar = Calendar.getInstance();
			iniCalendar.setTime(invoiceExpired.getExpirationDate());
			iniCalendar.add(Calendar.DAY_OF_MONTH, 1);
			invoiceConceptInterest.setIniPeriodDate(iniCalendar.getTime());
			invoiceConceptInterest.setEndPeriodDate(new Date());
			invoiceConceptInterest.setLastLiquidationDate(new Date());

			log(Level.INFO, "SE CREA NUEVO  INVOICE, por conceptos de intereses. Concepto: " + concept.getId() + ", invoice_concept vencido: " + invoiceConceptExpired.getId());
			/*-----------------------------------------------------*/
			int expiredInvoiceConceptDaysNumber = this.billingTools.daysDifference(invoiceExpired.getExpirationDate(), new Date());
			log(Level.INFO, "Number of days to calculate interest: " + expiredInvoiceConceptDaysNumber);
			double recalculatedInterest = expiredInvoiceConceptDaysNumber * invoiceConceptExpired.getBalance() * usuryRate.getValueDiary();
			if (recalculatedInterest <= 0.0) {
				log(Level.INFO, "El calculo de intereses es CERO, se cancela la creacion de intereses");
				return false;
			}
			log(Level.INFO, "Interest Value: " + recalculatedInterest);
			invoiceConceptInterest.setCalculatedCost(BillingTools.formatDouble(recalculatedInterest));
			double calculatedTax = 0.0;
			if (concept.getTax() != null && concept.getTax().getValue() != null) {
				calculatedTax = (concept.getTax().getValue() / BillingTools.formatDouble(100)) * invoiceConceptInterest.getCalculatedCost();
			}
			invoiceConceptInterest.setCalculatedTax(BillingTools.formatDouble(calculatedTax));
			invoiceConceptInterest.setBalance(BillingTools.formatDouble(invoiceConceptInterest.getCalculatedCost() + invoiceConceptInterest.getCalculatedTax()));
			this.billingTools.persistObject(invoice);
			this.billingTools.persistObject(invoiceConceptInterest);
			log(Level.INFO, "Intereses recalculados exitosamente para concepto " + concept.getId() + ", invoiceConcept:" + invoiceConceptExpired.getId());
			log(Level.INFO, "");
		}

		return true;
	}

	/**
	 * This function prints a message in log file
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	private void log(Level level, Object message) {
		BillingTools.printLog(BillingFunctions.class, level, message);
	}

}