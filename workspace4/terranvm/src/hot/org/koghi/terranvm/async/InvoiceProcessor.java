package org.koghi.terranvm.async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.ObjectOfContract;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RecoverConcept;
import org.koghi.terranvm.entity.RentableUnitContribution;
import org.koghi.terranvm.entity.UsuryRate;

@Name("processor")
public class InvoiceProcessor {

	@In
	private EntityManager entityManager;
	private BillingTools billingTools;
	private Calendar billingPeriodIniDate;
	private Calendar todayLastHour;
	private Calendar todayFirstHour;

	public InvoiceProcessor(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public InvoiceProcessor() {
	}

	@Asynchronous
	@Transactional
	public Timer createEjbTimer(@Expiration Date when, @IntervalDuration Long interval) {
		this.initCronJobProcess(null, new Date(), null);
		return null;
	}

	@Asynchronous
	@Transactional
	public QuartzTriggerHandle createQuartzTimer(@Expiration Date when, @IntervalCron String interval) {
		this.initCronJobProcess(null, new Date(), null);
		return null;
	}

	@Asynchronous
	@Transactional
	public QuartzTriggerHandle createQuartzTimer2(@Expiration Date when, @IntervalCron String interval) {
		this.initCronJobProcess2(new Date());
		return null;
	}

	/**
	 * Este método inicia el proceso de intereses
	 * 
	 * @param date
	 */
	public void initCronJobProcess2(Date date) {
		try {
			cleanMemory();
			this.billingTools = new BillingTools(this.entityManager);
			Calendar today = Calendar.getInstance();
			today.setTime(date);
			int anticipatedDaysPresentMonth = this.billingTools.getAnticipatedDaysForGivenDate(today);
			this.calculateInterests(anticipatedDaysPresentMonth);
			cleanMemory();

		} catch (Exception e) {
			this.entityManager.close();
			e.printStackTrace();
		}
	}

	/**
	 * Este Metodo inicia todo el proceso de CRON-JOB
	 * 
	 * @param object
	 *            objeto de tipo Concept o ProjectProperty, puede ser NULL, de
	 *            ser asi este método llamará a la funcion Principal del
	 *            CRON-JOB. Si es diferente a NULL, entonces se invocara la
	 *            funcion que calcula un único Concepto u Hoja de Termino según
	 *            corresponda.
	 * 
	 * @param date
	 *            objeto de tipo Date, no puede ser NULL, la fecha que se pase
	 *            indica el dia en el que el CRON-JOB se debe ubicar en el
	 *            "tiempo", para ejecutar la liquidacion del concepto.
	 * 
	 */
	public void initCronJobProcess(Object object, final Date date, Invoice oldInvoice) {

		long l1 = (new Date()).getTime();
		if (object == null) {
			cleanMemory();
			System.out.println("FLUSH MODE: " + this.entityManager.getFlushMode());
		}
		try {
			process(object, date, oldInvoice);
		} catch (Exception e) {
			this.entityManager.close();
			e.printStackTrace();
			// MailSender mailSender = new MailSender();
			// mailSender.set("dvaldivieso@koghi.com", "CRON JOB ERROR " +
			// date.toString(), "\n" + e.getMessage(), null, null);
			// (new Thread(mailSender)).start();
		}
		if (object == null) {
			cleanMemory();
		}
		long l2 = (new Date()).getTime();
		System.out.println("CRON JOB TIME : " + ((l2 - l1)) + " MiliSeconds");
	}

	/**
	 * El método principal de esta clase es "main". El metodo "process" es el
	 * encargado de llamar al metodo principal. Ahora bien es importante
	 * entender que los conceptos de cobro anticipado se tienen que generar unos
	 * dias antes, estos dias de anticipación estan guardados en una tabla de la
	 * base de datos (un valor por mes). El problema radica que cuando hay un
	 * cambio de mes y los dias anticipados del nuevo mes son mayores a los dias
	 * anticipados del mes anterior, entonces se va a crear un espacio de dias
	 * en los que NO se van a calcular los conceptos de tipo anticipado.
	 * 
	 * Entonces cuando existe un cambio de mes y ocurre un incremento en los
	 * dias anticipados, se debe ejecutar el metodo "main" de esta clase N veces
	 * donde N es la diferencia entre los dias anticipados del mes anterior y
	 * los dias anticipados del mes actual. Es decir se debe ir incrementando la
	 * fecha en un dia y ejecutar "main" hasta alcanzar los dias adelantados
	 * para el mes actual.
	 * 
	 * @param concept
	 *            objeto de tipo Concept, puede ser NULL, de ser asi este método
	 *            llamará a la funcion Principal del CRON-JOB. Si es diferente a
	 *            NULL, entonces se invocara la funcion que calcula un solo
	 *            Concepto
	 * 
	 * @param date
	 *            objeto de tipo Date, no puede ser NULL, la fecha que se pase
	 *            indica el dia en el que el CRON-JOB se debe ubicar en el
	 *            "tiempo", para ejecutar la liquidacion del concepto.
	 * @throws IOException
	 * 
	 */
	private void process(Object object, final Date date, Invoice oldInvoice) throws IOException {

		Locale.setDefault(Locale.ENGLISH);
		this.billingTools = new BillingTools(this.entityManager);
		Calendar today = Calendar.getInstance();
		today.setTime(date);
		int anticipatedDaysPresentMonth = this.billingTools.getAnticipatedDaysForGivenDate(today);
		int anticipatedDaysLastMonth = 0;
		today.add(Calendar.MONTH, -1);
		anticipatedDaysLastMonth = this.billingTools.getAnticipatedDaysForGivenDate(today);
		today = Calendar.getInstance();
		today.setTime(date);
		boolean updateTermSheet = true;

		if (today.get(Calendar.DAY_OF_MONTH) == 1 && anticipatedDaysPresentMonth > anticipatedDaysLastMonth) {

			this.log(Level.INFO, "ES PRIMER DIA DE MES, SE EJECUTA CRON-JOB CON DIAS ANTICIPADOS PARA " + (anticipatedDaysPresentMonth - anticipatedDaysLastMonth) + " DIAS.");
			while (anticipatedDaysLastMonth <= anticipatedDaysPresentMonth) {
				this.log(Level.INFO, "DIA: " + (anticipatedDaysLastMonth - anticipatedDaysPresentMonth));
				this.chooseFunction(object, anticipatedDaysLastMonth, date, updateTermSheet, oldInvoice);
				anticipatedDaysLastMonth++;
				updateTermSheet = false;
				today.add(Calendar.DAY_OF_MONTH, 1);
			}

		} else {
			this.log(Level.INFO, "NO ES PRIMERO DE MES O LOS DIAS ANTICIPADOS NO SE INCREMENTAN, SE EJECUTA CRON-JOB NORMAL");
			this.chooseFunction(object, anticipatedDaysPresentMonth, date, updateTermSheet, oldInvoice);
		}

		/*
		 * 2012-12-17 @dvaldivieso - Se sacan intereses de CRONJOB. Para que sea
		 * mas liviano
		 */
		/*
		 * if (object == null) { // Si concept es NULL, quiere decir que se
		 * invoco esta funcion con // la intencion de llamar el metodo principal
		 * del CRON-JOB, por lo // que se deben calcular tambien los intereses
		 * this.calculateInterests(anticipatedDaysPresentMonth); }
		 */

	}

	private void chooseFunction(Object object, final int anticipatedDaysPresentMonth, final Date date, final boolean updateTermSheet, Invoice oldInvoice) throws IOException {

		if (object == null) {

			this.mainFunction(anticipatedDaysPresentMonth, updateTermSheet, date);

		} else if (object instanceof Concept) {

			this.calculateConceptMain(((Concept) object).getId(), anticipatedDaysPresentMonth, date, oldInvoice);

		} else if (object instanceof ProjectProperty) {

			this.calculateTermSheetMain(((ProjectProperty) object).getId(), anticipatedDaysPresentMonth, date);

		} else {
			this.log(Level.INFO, "NO ES POSIBLE EJECUTAR CRON-JOB, ya que el Objeto que se pasa por argumento no corresponde a ninguna de las Clases Esperadas");
			return;
		}
	}

	// /**
	// * Método que liquida diariamente los conceptos asociados a los hojas de
	// * Terminos Vigentes
	// *
	// * @throws IOException
	// *
	// */
	// private List<InvoiceConcept> mainFunctionDAVID(final Integer
	// anticipatedDays, boolean updateTermSheets, final Date date) throws
	// IOException {
	//
	// Log(Level.INFO,
	// "____________________________________________________________________________");
	// Log(Level.INFO,
	// "|                             CRON JOB  DAVID                               |");
	// Log(Level.INFO,
	// "|                                                                           |");
	// Log(Level.INFO,
	// "|           START  START  START  START  START  START  START  START          |");
	// Log(Level.INFO,
	// "|                                                                           |");
	// Log(Level.INFO,
	// "|                                                                           |");
	// Log(Level.INFO,
	// "|___________________________________________________________________________|");
	// Log(Level.INFO, "FECHA DE EJECUCION: " + new Date());
	// Log(Level.INFO, "DIAS ANTICIPADOS PARA COBRO ANTICIPADO " +
	// anticipatedDays + "\n");
	//
	// List<InvoiceConcept> resultInvoiceConceptList = new
	// ArrayList<InvoiceConcept>();
	// Calendar obligationStatDateLimit = Calendar.getInstance();
	// obligationStatDateLimit.add(Calendar.DAY_OF_MONTH, anticipatedDays);
	//
	//
	// StringBuilder consulta = new StringBuilder();
	// consulta.append("FROM Concept con WHERE ");
	// consulta.append("con.projectProperty");
	// consulta.append("from ProjectProperty projectProperty WHERE projectProperty.status = ? AND ? >= projectProperty.obligationsStartDate AND ? <= projectProperty.expirationDate AND projectProperty.step = ?");
	//
	//
	// Query availableTermSheetsQuery =
	// this.entityManager.createNamedQuery("projectProperty.availableTermSheets");
	// availableTermSheetsQuery.setParameter(1,
	// ProjectProperty.STATUS_APPROVED);
	// availableTermSheetsQuery.setParameter(2,
	// obligationStatDateLimit.getTime());
	// availableTermSheetsQuery.setParameter(3, new Date());
	// availableTermSheetsQuery.setParameter(4,
	// ProjectProperty.STEP_approved_public_accountant);
	//
	// @SuppressWarnings("unchecked")
	// ArrayList<ProjectProperty> termSheetList = (ArrayList<ProjectProperty>)
	// availableTermSheetsQuery.getResultList();
	//
	// if (termSheetList.isEmpty()) {
	// Log(Level.INFO, "NO EXISTEN TERM-SHEETS VIGENTES DISPONIBLES");
	// }
	// Log(Level.INFO, "Number of term-sheets " + termSheetList.size());
	//
	//
	//
	// Log(Level.INFO,
	// "____________________________________________________________________________");
	// Log(Level.INFO,
	// "|                              CRON JOB DAVID                               |");
	// Log(Level.INFO,
	// "|                                                                           |");
	// Log(Level.INFO,
	// "|               END  END  END  END  END  END  END  END  END                 |");
	// Log(Level.INFO,
	// "|                                                                           |");
	// Log(Level.INFO,
	// "|                                                                           |");
	// Log(Level.INFO,
	// "|___________________________________________________________________________|");
	// Log(Level.INFO, "");
	// return resultInvoiceConceptList;
	// }

	/**
	 * Método que liquida diariamente los conceptos asociados a los hojas de
	 * Terminos Vigentes
	 * 
	 * @throws IOException
	 * 
	 */
	private List<InvoiceConcept> mainFunction(final Integer anticipatedDays, boolean updateTermSheets, final Date date) throws IOException {

		log(Level.INFO, "____________________________________________________________________________");
		log(Level.INFO, "|                             CRON JOB                                      |");
		log(Level.INFO, "|                                                                           |");
		log(Level.INFO, "|           START  START  START  START  START  START  START  START          |");
		log(Level.INFO, "|                                                                           |");
		log(Level.INFO, "|                                                                           |");
		log(Level.INFO, "|___________________________________________________________________________|");
		log(Level.INFO, "FECHA DE EJECUCION: " + new Date());
		log(Level.INFO, "DIAS ANTICIPADOS PARA COBRO ANTICIPADO " + anticipatedDays + "\n");

		List<InvoiceConcept> resultInvoiceConceptList = new ArrayList<InvoiceConcept>();
		Calendar obligationStatDateLimit = Calendar.getInstance();
		obligationStatDateLimit.add(Calendar.DAY_OF_MONTH, anticipatedDays);

		// Query availableTermSheetsQuery =
		// this.entityManager.createNamedQuery("projectProperty.availableTermSheets");
		String consulta = "SELECT projectProperty.id from ProjectProperty projectProperty WHERE projectProperty.status = ? AND ? >= projectProperty.obligationsStartDate AND ? <= projectProperty.expirationDate AND projectProperty.step = ?";
		Query availableTermSheetsQuery = this.entityManager.createQuery(consulta);
		availableTermSheetsQuery.setParameter(1, ProjectProperty.STATUS_APPROVED);
		availableTermSheetsQuery.setParameter(2, obligationStatDateLimit.getTime());
		availableTermSheetsQuery.setParameter(3, new Date());
		availableTermSheetsQuery.setParameter(4, ProjectProperty.STEP_approved_public_accountant);

		@SuppressWarnings("unchecked")
		ArrayList<Object> termSheetList = (ArrayList<Object>) availableTermSheetsQuery.getResultList();

		if (termSheetList.isEmpty()) {
			log(Level.INFO, "NO EXISTEN TERM-SHEETS VIGENTES DISPONIBLES");
		}
		log(Level.INFO, "Number of term-sheets " + termSheetList.size());

		// for (ProjectProperty termSheet : termSheetList) {
		for (Object termSheet : termSheetList) {
			try {
				List<InvoiceConcept> tempResultList = this.calculateTermSheetMain((Integer) termSheet, anticipatedDays, date);
				if (tempResultList != null && !tempResultList.isEmpty()) {
					resultInvoiceConceptList.addAll(tempResultList);
				}
				// Ejecutar actualizacion de la TERM-sheet (prorroga automatica)
				if (updateTermSheets) {
					try {

						this.updateTermSheet((Integer) termSheet, anticipatedDays);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		log(Level.INFO, "____________________________________________________________________________");
		log(Level.INFO, "|                              CRON JOB                                     |");
		log(Level.INFO, "|                                                                           |");
		log(Level.INFO, "|               END  END  END  END  END  END  END  END  END                 |");
		log(Level.INFO, "|                                                                           |");
		log(Level.INFO, "|                                                                           |");
		log(Level.INFO, "|___________________________________________________________________________|");
		log(Level.INFO, "");
		return resultInvoiceConceptList;
	}

	private List<InvoiceConcept> calculateTermSheetMain(Integer termSheetId, final int anticipatedDays, final Date date) throws IOException {

		// ProjectProperty termSheet =
		// this.entityManager.find(ProjectProperty.class, termSheetId);

		// System.out.println();
		// System.out.println("Calcular Nueva Hoja de terminos................"
		// + termSheetId);
		/*
		 * Se ordenan los conceptos de tal forma que se calculen primero los NO
		 * dependientes
		 */
		List<InvoiceConcept> resultInvoiceConceptList = new ArrayList<InvoiceConcept>();
		// Query conceptsQuery =
		// this.entityManager.createQuery("FROM Concept concept WHERE concept.projectProperty = ? ORDER BY concept.dependent");
		// conceptsQuery.setParameter(1, termSheet);
		Query conceptsQuery = this.entityManager.createQuery("SELECT concept.id FROM Concept concept WHERE concept.projectProperty.id = ? ORDER BY concept.dependent");
		conceptsQuery.setParameter(1, termSheetId);
		@SuppressWarnings("unchecked")
		// List<Concept> concepts = conceptsQuery.getResultList();
		List<Object> concepts = conceptsQuery.getResultList();

		log(Level.INFO, "_______________________________________________");
		// Log(Level.INFO, "SIGUIENTE TERM-SHEET ID: " + termSheet.getId());
		// Log(Level.INFO, "OBJETO TERMSHEET: " +
		// termSheet.getObjectOfContract().getDescription());
		log(Level.INFO, "NUMERO DE CONCEPTOS A EVALUAR: " + concepts.size());

		// for (Concept concept : concepts) {
		for (Object concept : concepts) {
			this.calculateConceptMain((Integer) concept, anticipatedDays, date, null);
		}
		// Log(Level.INFO, "SE TERMINA DE EVALUAR TERM-SHEET (id):" +
		// termSheet.getId());
		return resultInvoiceConceptList;
	}

	private void calculateConceptMain(Integer conceptId, final int anticipatedDays, final Date date, Invoice oldInvoice) throws IOException {

		Concept concept = this.entityManager.find(Concept.class, conceptId);
		long l0 = (new Date()).getTime();
		log(Level.INFO, ". . . . . . . . . . . . . . . . . . . . . . . . . . . .");
		log(Level.INFO, "SIGUIENTE CONCEPTO A EVALUAR (id): " + concept.getId());
		this.createFilterDates(concept, anticipatedDays, date);
		ProjectProperty termSheet = concept.getProjectProperty();
		byte[] conceptSerealizable = null;

		/* SI el concepto tiene un Modulo de contribucion Asociado */
		if (concept.getContributionModule() != null && termSheet.getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY) {
			if (!basicValidations(concept)) {
				long l1 = (new Date()).getTime();
				log(Level.INFO, "    END calculateConceptMain  : " + ((l1 - l0)) + " MilliSeconds");
				return;
			}
			log(Level.INFO, "Calculating Concept with Contribution Module");

			// Se mantiene el concept original

			if (concept.getContributionModule().getRentableUnitContributions().size() == 0) {

				log(Level.INFO, "No hay valores para el modulo de contribución  con id: " + concept.getContributionModule().getId());
			}

			Query q = this.entityManager.createNativeQuery("select ruc.id from concept c inner join contribution_module cm on c.contribution_module =cm.id inner join rentable_unit_contribution ruc on ruc.contribution_module = cm.id where c.id = ?");
			q.setParameter(1, concept.getId());
			@SuppressWarnings("unchecked")
			List<Integer> rucId = q.getResultList();
			for (Integer ruc : rucId) {
				this.calculateConceptValidate(concept, ruc, conceptSerealizable, oldInvoice);
			}
		} else {
			log(Level.INFO, "Calculando concepto");
			if (!basicValidations(concept)) {
				long l1 = (new Date()).getTime();
				log(Level.INFO, "    END calculateConceptMain  : " + ((l1 - l0)) + " MilliSeconds");
				return;
			}
			RentableUnitContribution ruc = this.billingTools.getRentableContributionRate(concept);
			if (ruc != null) {
				this.calculateConceptValidate(concept, ruc.getId(), null, oldInvoice);
			} else {
				this.calculateConceptValidate(concept, 0, null, oldInvoice);
			}
		}
		if (concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_WITHOUT_CHARGE) {
			concept.setImmediatePaymentState(Concept.IMMEDIATE_PAYMENT_SET_ALREADY_CHARGED);
			this.persistObjectCRONJOB(concept);
		}

		log(Level.INFO, "SE TERMINA DE EVALUAR CONCEPTO (id):" + concept.getId());
		long l1 = (new Date()).getTime();
		System.out.println("    END calculateConceptMain  : " + ((l1 - l0)) + " MilliSeconds");
	}

	/**
	 * Esta funcion instancia la fecha 'filtro Inicial' que corresponde al
	 * primer dia del mes actual y la fecha de 'filtro final' que corresponde al
	 * primer dia del mes inmediatemente posterior al filtro de fecha inicial
	 */
	private void createFilterDates(Concept concept, final int anticipatedDays, final Date date) {

		this.todayFirstHour = Calendar.getInstance();
		this.todayFirstHour.setTime(date);
		if (concept != null && concept.isEarlyPayment()) {
			this.todayFirstHour.add(Calendar.DAY_OF_MONTH, anticipatedDays);
		}

		this.todayFirstHour.set(Calendar.HOUR_OF_DAY, 0);
		this.todayFirstHour.set(Calendar.MINUTE, 0);
		this.todayFirstHour.set(Calendar.SECOND, 0);
		this.todayFirstHour.set(Calendar.MILLISECOND, 0);

		this.todayLastHour = Calendar.getInstance();
		this.todayLastHour.setTime(date);
		if (concept != null && concept.isEarlyPayment()) {
			this.todayLastHour.add(Calendar.DAY_OF_MONTH, anticipatedDays);
		}
		this.todayLastHour.set(Calendar.HOUR_OF_DAY, 23);
		this.todayLastHour.set(Calendar.MINUTE, 59);
		this.todayLastHour.set(Calendar.SECOND, 59);
		this.todayLastHour.set(Calendar.MILLISECOND, 999);

		this.billingPeriodIniDate = Calendar.getInstance();
		if (concept != null) {
			Date projectlastClosureDate = this.billingTools.getProjectLastClosure(concept.getProjectProperty().getProject());
			if (projectlastClosureDate == null) {
				this.billingPeriodIniDate.setTime(concept.getStartDate());
			} else {
				this.billingPeriodIniDate.setTime(projectlastClosureDate);
			}
		}
		billingPeriodIniDate.set(Calendar.DAY_OF_MONTH, 1);
		this.billingPeriodIniDate.set(Calendar.HOUR_OF_DAY, 12);
		this.billingPeriodIniDate.set(Calendar.MINUTE, 0);
		this.billingPeriodIniDate.set(Calendar.SECOND, 0);
		this.billingPeriodIniDate.set(Calendar.MILLISECOND, 0);

		if (concept != null) {
			log(Level.INFO, "Concept " + concept.getId() + ", Anticipado?:" + concept.isEarlyPayment());
		}
		log(Level.INFO, "todayFirstHour         -> " + this.todayFirstHour.getTime());
		log(Level.INFO, "todayLastHour          -> " + this.todayLastHour.getTime());
		log(Level.INFO, "billingPeriodIniDate   -> " + this.billingPeriodIniDate.getTime());

	}

	/**
	 * This function evaluates if given concept can be calculated today
	 * 
	 * @param concept
	 *            Concept object to be evaluated
	 * @return boolean true if concept can be evaluated today, otherwise false
	 */
	private boolean isConceptBillAble(Concept concept) {

		if (concept.getStartDate() == null || concept.getEndDate() == null) {
			log(Level.WARNING, "La fecha final del concepto o la fecha incial del concepto son null");
			return false;
		}
		Integer periods = concept.getNumberPeriods();
		if (periods == null) {
			log(Level.WARNING, "El numero de periodos del concepto es null");
			return false;
		}

		/*
		 * Un CONCEPTO con "cobro tipo vencido" no se calcula en la fecha de
		 * incio del concepto pero si en la fecha final del concepto. Un
		 * CONCEPTO con "cobro tipo anticipado" si se calcula en la fecha de
		 * inicio de concepto pero no en la fecha final del concepto.
		 */

		Calendar conceptIniDate = Calendar.getInstance();
		conceptIniDate.setTime(concept.getStartDate());
		Calendar conceptEndDate = Calendar.getInstance();
		conceptEndDate.setTime(concept.getEndDate());

		if (concept.isEarlyPayment()) {// Si es mes anticipado
			if ((conceptIniDate.after(this.todayLastHour) || conceptEndDate.before(this.todayFirstHour))) {
				log(Level.WARNING, "IniDate: " + conceptIniDate.getTime() + ", EndDate: " + conceptEndDate.getTime() + ", Today: " + this.todayFirstHour.getTime());
				return false;
			}
		} else {// Si es mes vencido
			if ((conceptIniDate.after(this.todayFirstHour) || conceptEndDate.before(this.todayFirstHour))) {
				log(Level.WARNING, "IniDate: " + conceptIniDate.getTime() + ", EndDate: " + conceptEndDate.getTime() + ", Today: " + this.todayFirstHour.getTime());
				return false;
			}
		}

		return true;
	}

	/**
	 * Esta funcion evalua si el dia de HOY (fecha en que corre el cronjob) sin
	 * importar la hora, se cumple un PERIODO (teniendo en cuenta la
	 * periodicidad), del concepto que se recibe como parametro.
	 * 
	 * De cumplirse, la funcion principal del cronJob debera calcular un
	 * InvoiceConcept.
	 * 
	 * @param concept
	 * @return true si se cumple un periodo completo del concepto hoy
	 */
	private boolean isConceptEnableToComputeToday(Concept concept) {

		Calendar conceptStartDate = Calendar.getInstance();
		conceptStartDate.setTime(concept.getStartDate());
		Calendar conceptEndDate = Calendar.getInstance();
		conceptEndDate.setTime(concept.getEndDate());
		int per = concept.getPeriodicity().intValue();

		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.setTime(conceptStartDate.getTime());

		boolean conceptEndDateToday = (conceptEndDate.after(this.todayFirstHour) || conceptEndDate.equals(this.todayFirstHour)) && (conceptEndDate.before(this.todayLastHour) || conceptEndDate.equals(this.todayLastHour));
		boolean conceptStartDateToday = (conceptStartDate.after(this.todayFirstHour) || conceptStartDate.equals(this.todayFirstHour)) && (conceptStartDate.before(this.todayLastHour) || conceptStartDate.equals(this.todayLastHour));
		log(Level.INFO, "Concept Ini Date: " + conceptStartDate.getTime() + ", start today? " + conceptStartDateToday);
		log(Level.INFO, "Concept End Date: " + conceptEndDate.getTime() + ", end today? " + conceptEndDateToday);
		log(Level.INFO, "concept early Payment? " + concept.isEarlyPayment());
		log(Level.INFO, "concept periodicity? " + per);

		if (conceptStartDateToday && conceptEndDateToday) {
			return true;
		}
		if (concept.isEarlyPayment() && conceptEndDateToday) {
			return false;
		}
		if (!concept.isEarlyPayment() && conceptStartDateToday) {
			return false;

		}

		// if
		// (concept.getEndDateView().equals(concept.getProjectProperty().getExpirationDate()))
		// {
		// /*
		// * Este condicional, se hizo para cuando al periodicidad del
		// * concepto, no coincide con la fecha de expiracion de la hoja de
		// * terminos.
		// */
		// return true;
		// }

		while (true) {
			if ((tempCalendar.after(this.todayFirstHour) || tempCalendar.equals(this.todayFirstHour)) && (tempCalendar.before(this.todayLastHour) || tempCalendar.equals(this.todayLastHour))) {
				return true;
			}
			if (tempCalendar.after(this.todayLastHour)) {
				log(Level.INFO, "Next calculation date : " + tempCalendar.getTime());
				return false;
			}

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

	}

	private boolean isConceptAlreadyCalculatedTodayOrAfterToday(InvoiceConcept invoiceConcept) {
		Calendar lastCalculation = Calendar.getInstance();
		lastCalculation.setTime(invoiceConcept.getLastLiquidationDate());
		if (lastCalculation.get(Calendar.YEAR) >= this.todayLastHour.get(Calendar.YEAR) && lastCalculation.get(Calendar.MONTH) >= this.todayLastHour.get(Calendar.MONTH) && lastCalculation.get(Calendar.DATE) >= this.todayLastHour.get(Calendar.DATE)) {
			return true;
		}
		return false;
	}

	/**
	 * Método que hace todas las validaciones antes de invocar la funcion que
	 * hace las operaciones necesarias para calcular el concepto.
	 * 
	 * @param concept
	 *            Concepto a liquidar
	 * @param ruc
	 *            Objeto RentableUnitContribution, puede ser null
	 * @param sales
	 *            las ventas de la unidad arrendable asociadas a un tercero,
	 *            puede ser null
	 * @param ipcYearlyString
	 *            arreglo de IPC anual
	 * @param ipcMonthlyString
	 *            arreglod de IPC mensual
	 * @return true
	 * @throws IOException
	 */
	private void calculateConceptValidate(Concept concept, Integer ruc, byte[] conceptSerializable, Invoice oldInvoice) throws IOException {
		long l0 = (new Date()).getTime();
		long l1 = (new Date()).getTime();
		ProjectProperty termSheet = concept.getProjectProperty();
		int objectOfContract = termSheet.getObjectOfContract().getId();
		Invoice invoice = null;
		InvoiceConcept invoiceConcept = null;

		int rentableUnitId = 0;
		if (ruc != null && ruc != 0) {
			/*
			 * Se traen las unidades arrendables que se encuentren activas, y
			 * pertenescan a entidades del activo que no esten desactivadas,
			 * para que las hojas de términos de tipo activo con modulo de
			 * contribución no facturen los modulos de unidades arrendables, que
			 * no deben ser facturadas por su estado de inactividad.
			 */
			Query q = this.entityManager.createNativeQuery("SELECT ru.id FROM rentable_unit ru JOIN rentable_unit_contribution ruc on ruc.rentable_unit = ru.id join area a on ru.area=a.id join floor f on f.id = a.floor join construction c on c.id= f.construction WHERE  ru.deactivate = false and f.deactivate = false and c.deactivate = false and a.deactivate = false AND ruc.id= ? ");
			q.setParameter(1, ruc);
			rentableUnitId = (Integer) q.getResultList().get(0);
		}

		List<InvoiceConcept> invoiceConceptExisitingList = null;

		if (rentableUnitId == 0 && concept.getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT) {
			rentableUnitId = concept.getProjectProperty().getRentableUnit().getId();
			log(Level.INFO, "this.billingPeriodIniDate: " + this.billingPeriodIniDate.getTime());
			invoiceConceptExisitingList = this.billingTools.searchForExistingInvoiceConcept(concept, rentableUnitId, this.billingPeriodIniDate);
		}

		if (invoiceConceptExisitingList == null) {
			invoiceConceptExisitingList = this.billingTools.searchForExistingInvoiceConcept(concept, rentableUnitId, this.billingPeriodIniDate);
		}

		if (!invoiceConceptExisitingList.isEmpty()) {
			long time = new Date().getTime();
			log(Level.INFO, "START Verificación si el concepto ya se calculo: " + time);
			/*
			 * Si entro a este condiconal, es xq ya se calculo una factura para
			 * el concepto. Ó el concepto pertenece a un grupo cuya factura ya
			 * esta creada para otro concepto con el mismo grupo
			 */
			log(Level.INFO, "Verificando si concepto ya se calculo concepto ...");
			for (InvoiceConcept invoCon : invoiceConceptExisitingList) {
				if (concept.getId() == invoCon.getConcept().getId() && invoCon.getInvoice().isApproved() && this.isConceptAlreadyCalculatedTodayOrAfterToday(invoCon) && invoCon.getInvoiceConceptType() == InvoiceConcept.TYPE_NORMAL) {
					log(Level.INFO, "EL CONCEPTO YA FUE CALCULADO HOY: " + concept.getId() + ". Y la factura ya fue aprobada (fac id " + invoCon.getInvoice().getId() + ").");
					l1 = (new Date()).getTime();
					return;
				} else if (concept.getId() == invoCon.getConcept().getId() && !invoCon.getInvoice().isApproved() && this.isConceptAlreadyCalculatedTodayOrAfterToday(invoCon) && invoCon.getInvoiceConceptType() == InvoiceConcept.TYPE_NORMAL) {
					l1 = (new Date()).getTime();
					return;
				} else if (concept.getId() == invoCon.getConcept().getId() && !invoCon.getInvoice().isApproved() && !this.isConceptAlreadyCalculatedTodayOrAfterToday(invoCon) && invoCon.getInvoiceConceptType() == InvoiceConcept.TYPE_NORMAL) {
					invoiceConcept = invoCon;
					invoice = invoiceConcept.getInvoice();
					break;
				} else if (!invoCon.getInvoice().isApproved()) {
					invoice = invoCon.getInvoice();
				}
			}

			if (invoice != null) {
				log(Level.INFO, "EXISTE UN INVOICE (" + invoice.getId() + "), PARA UBICAR EL CALCULO DEL CONCEPTO " + concept.getId());
			}
			if (invoiceConcept != null) {
				log(Level.INFO, "YA EXISTE UN INVOICE-CONCEPT (" + invoiceConcept.getId() + "), PARA SOBRE-ESCRIBIR EL CALCULO DEL CONCEPTO " + concept.getId());
			}
			log(Level.INFO, "END Verificación si el concepto ya se calculo  : " + ((new Date()).getTime() - time) + " MilliSeconds");
		}

		if (invoice == null) {
			invoice = this.billingTools.updateInvoice(concept, invoice, ruc);
			if (invoice == null) {
				log(Level.INFO, "SE ABORTA CALCULACION XQ FACTURADOR y FACTURADO SON EL MISMO, o no estan definidos !!!!!!!!");
				l1 = (new Date()).getTime();
				log(Level.INFO, "FUNCTION END3 calculateConceptValidate  : " + ((l1 - l0)) + " MiliSeconds");
				return;
			}
			log(Level.INFO, "SE CREA NUEVO  INVOICE, para el concepto " + concept.getId());
		}

		invoice.setOldInvoice(oldInvoice);

		if (invoiceConcept == null) {
			invoiceConcept = new InvoiceConcept();
			log(Level.INFO, "SE CREA NUEVO  INVOICE-CONCEPT, para el concepto " + concept.getId());
		}

		invoiceConcept.setRentableUnitId(rentableUnitId);

		if (objectOfContract == ObjectOfContract.OBJECT_REALPROPERTY || objectOfContract == ObjectOfContract.OBJECT_RENTABLEUNIT) {
			invoice.setRealProperty(termSheet.getRealProperty());
		}
		l1 = (new Date()).getTime();
		log(Level.INFO, "END calculateConceptValidate  : " + ((l1 - l0)) + " MiliSeconds");
		this.calculateConcept(concept, invoiceConcept, invoice, ruc, conceptSerializable);
	}

	/**
	 * Este metodo se usa para actualizar las fechas del contrato cuando hay
	 * prorroga automatica
	 * 
	 * @param termSheet
	 */
	// private void updateTermSheet(ProjectProperty termSheet, int
	// anticipatedDays) {
	private void updateTermSheet(Integer termSheetId, int anticipatedDays) {
		ProjectProperty termSheet = this.entityManager.find(ProjectProperty.class, termSheetId);

		log(Level.INFO, "");
		log(Level.INFO, "VERIFICANDO PRORROGA AUTOMATICA TERM-SHEET " + termSheet.getId());
		try {

			createFilterDates(null, anticipatedDays, new Date());
			Calendar termSheetEndDate = Calendar.getInstance();
			termSheetEndDate.setTime(termSheet.getExpirationDate());
			boolean endDateToday = (termSheetEndDate.get(Calendar.YEAR) == this.todayFirstHour.get(Calendar.YEAR)) && termSheetEndDate.get(Calendar.MONTH) == this.todayFirstHour.get(Calendar.MONTH) && termSheetEndDate.get(Calendar.DATE) == this.todayFirstHour.get(Calendar.DATE);

			if (termSheet.isAutomaticExtension() && endDateToday && termSheet.getNumberPeriodsExtension() != 0) {
				termSheet.applyExtension();
				this.persistObjectCRONJOB(termSheet);
				log(Level.INFO, "TERM-SHEET ACTUALIZADA CON PRORROGA AUTOMATICA " + termSheet.getId());

			} else if (!termSheet.isAutomaticExtension() && endDateToday || (termSheet.isAutomaticExtension() && termSheet.getNumberPeriodsExtension() == 0) && endDateToday) {
				termSheet.setStatus(ProjectProperty.STATUS_EXPIRED);
				this.persistObjectCRONJOB(termSheet);
				log(Level.INFO, "TERM-SHEET FINALIZADA, HOY TERMINA LA VIGENCIA  " + termSheet.getId());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean basicValidations(Concept concept) {
		log(Level.INFO, "START BASIC VALIDATIONS " + concept.getId());
		long l0 = (new Date()).getTime();
		long l1 = (new Date()).getTime();

		if (!isConceptBillAble(concept)) {
			log(Level.INFO, "CONCEPTO NO VIGENTE (id): " + concept.getId());
			l1 = (new Date()).getTime();
			log(Level.INFO, "FUNCTION END BASIC VALIDATIONS : " + ((l1 - l0)) + " MilliSeconds");
			return false;
		}
		if (!isConceptEnableToComputeToday(concept)) {
			log(Level.INFO, "CONCEPTO VIGENTE, pero el periodo de liquidacion no se cumple hoy: (id)" + concept.getId());
			l1 = (new Date()).getTime();
			log(Level.INFO, "END BASIC VALIDATIONS : " + ((l1 - l0)) + " MilliSeconds");
			return false;
		}
		if (concept.getImmediatePaymentState() == Concept.IMMEDIATE_PAYMENT_SET_ALREADY_CHARGED) {
			log(Level.INFO, "CONCEPTO DE TIPO COBRO INMEDIATO YA SE LIQUIDO (id): " + concept.getId());
			l1 = (new Date()).getTime();
			log(Level.INFO, "END BASIC VALIDATIONS : " + ((l1 - l0)) + " MilliSeconds");
			return false;
		}
		l1 = (new Date()).getTime();
		log(Level.INFO, "END BASIC VALIDATIONS : " + ((l1 - l0)) + " MilliSeconds");
		return true;
	}

	/**
	 * This function calulates a concept, and persist a respective
	 * invoiceConcept and invoice
	 * 
	 * @param concept
	 *            Concept object that its being calculated
	 * @param invoiceConcept
	 *            Instance of InvoiceConcept
	 * @param invoice
	 *            Instance of Invoice
	 * @param ruc
	 *            RentableUnitContribution object, can be null
	 * @param sales
	 *            Sales Object, can be null
	 * @param ipcYearlyString
	 *            , String of IPCY values
	 * @param ipcMonthlyString
	 *            , String of IPCM values
	 * @param ipcAccumalatedString
	 *            , String of IPCA values
	 * @return
	 * @throws IOException
	 */
	private void calculateConcept(Concept concept, InvoiceConcept invoiceConcept, Invoice invoice, Integer rucs, byte[] conceptSerealizable) throws IOException {

		try {
			// System.out.println("                Function START calculateConcept Concepto");
			long l0 = (new Date()).getTime();
			Calendar iniConceptDate = Calendar.getInstance();
			iniConceptDate.setTime(this.todayFirstHour.getTime());
			iniConceptDate.set(Calendar.HOUR_OF_DAY, 0);
			iniConceptDate.set(Calendar.MINUTE, 0);
			iniConceptDate.set(Calendar.SECOND, 0);
			iniConceptDate.set(Calendar.MILLISECOND, 0);
			Calendar endConceptDate = Calendar.getInstance();
			endConceptDate.setTime(this.todayFirstHour.getTime());
			endConceptDate.set(Calendar.HOUR_OF_DAY, 0);
			endConceptDate.set(Calendar.MINUTE, 0);
			endConceptDate.set(Calendar.SECOND, 0);
			endConceptDate.set(Calendar.MILLISECOND, 0);

			byte[] conceptoSerealizable = null;
			if (conceptSerealizable == null) {
				conceptoSerealizable = BillingTools.serializeConcept(concept);
			} else
				conceptoSerealizable = conceptSerealizable;

			if (concept.isEarlyPayment()) {
				endConceptDate = this.billingTools.operateConceptDate(concept, endConceptDate, true);
				endConceptDate.add(Calendar.DAY_OF_MONTH, -1);
				// endConceptDate.add(Calendar.MONTH, +1);

			} else {
				iniConceptDate = this.billingTools.operateConceptDate(concept, iniConceptDate, false);
				iniConceptDate.add(Calendar.DAY_OF_MONTH, +1);
				// iniConceptDate.add(Calendar.MONTH,+1);
			}
			if (invoiceConcept.getIniPeriodDate() == null) {
				invoiceConcept.setIniPeriodDate(iniConceptDate.getTime());
			}
			invoiceConcept.setEndPeriodDate(endConceptDate.getTime());

			int totalDays;
			int tempDays = totalDays = 0;
			double totalInvoiceConcept = 0.0;
			int compare;
			/*
			 * Se realiza incremento si es mes anticipado y es del primer día JL
			 */
			RentableUnitContribution ruc = null;
			Query q1 = this.entityManager.createQuery("FROM RentableUnitContribution where id=?");
			q1.setParameter(1, rucs);
			if (!q1.getResultList().isEmpty())
				ruc = (RentableUnitContribution) q1.getResultList().get(0);

			Calendar iniConceptDateClone = (Calendar) iniConceptDate.clone();
			// Calendar aux = Calendar.getInstance();
			// aux.setTime(iniConceptDate.getTime());
			// aux = this.billingTools.operateConceptDate(concept,
			// iniConceptDate,
			// false);
			// if (!concept.isEarlyPayment() && concept.getIncreased() != null
			// &&
			// concept.getIncreased().getNextIncreased().compareTo(aux.getTime())
			// ==
			// 0) {
			// this.billingTools.incrementConcept(concept, iniConceptDate, ruc,
			// sales, true);
			// }
			/* END */

			log(Level.INFO, "CICLO PROPORCIONALIDAD START");
			long l2 = (new Date()).getTime();
			while ((compare = iniConceptDate.compareTo(endConceptDate)) <= 0) {
				boolean lastDay = (compare == 0);/* Last liquidation date */
				boolean isIncrement = (concept.getIncreased() != null && concept.getIncreased().getNextIncreased().compareTo(iniConceptDate.getTime()) == 0) ? true : false;
				if (isIncrement || lastDay) {
					if (isIncrement && totalDays == 0) {
						this.billingTools.incrementConcept(concept, iniConceptDate, ruc, true);
						if (lastDay) {
							tempDays++;
							Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDateClone);
							if (calculatedValue == null)
								return;
							totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
							tempDays = 1;
						}

					} else if (isIncrement && totalDays > 0 && !lastDay) {
						Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDateClone);
						if (calculatedValue == null)
							return;
						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
						tempDays = 0;
						this.billingTools.incrementConcept(concept, iniConceptDate, ruc, true);
					} else if (isIncrement && lastDay) {
						Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDateClone);
						if (calculatedValue == null)
							return;

						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
						tempDays = 1;
						this.billingTools.incrementConcept(concept, iniConceptDate, ruc, true);
						calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDateClone);
						if (calculatedValue == null)
							return;

						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());
					} else if (!isIncrement && lastDay) {
						tempDays++;
						Double calculatedValue = this.billingTools.calculateConceptValue(concept, ruc, iniConceptDateClone);
						if (calculatedValue == null)
							return;

						totalInvoiceConcept += (tempDays * calculatedValue.doubleValue());

					}

				}

				tempDays++;
				totalDays++;
				iniConceptDate.add(Calendar.DAY_OF_MONTH, +1);
			}
			long l3 = (new Date()).getTime();
			log(Level.INFO, "CICLO PROPORCIONALIDAD END: " + ((l3 - l2)) + " MilliSeconds");

			totalInvoiceConcept = totalInvoiceConcept / totalDays;

			/*
			 * Se permite la creación de invoice concepts con valor 0 para que
			 * se pueda recalcular en el momento de que el valor ya sea
			 * diferente de 0
			 */
			if (totalInvoiceConcept <= 0.01) {
				log(Level.INFO, "El calculo del concepto es igual a CERO, se cancela la creacion del concepto " + concept.getId());
				return;
			}

			invoiceConcept.setCalculatedCost(BillingTools.formatDouble(invoiceConcept.getCalculatedCost() + totalInvoiceConcept));
			double calculatedTax = 0.0;
			if (concept.getTax() != null && concept.getTax().getValue() != null) {
				calculatedTax = (concept.getTax().getValue() / 100.00) * invoiceConcept.getCalculatedCost();
			}

			// ---------CALCULO IMPUESTO AL TIMBRE SOBRE EL VALOR DEL CONCEPTO
			// SIN IVA-----------------------
			double calculateStamptax = 0.0;
			if (concept.getStamptax() != null) {
				calculateStamptax = invoiceConcept.getCalculatedCost() * (concept.getStamptax().getValue() / 100);
			}

			/*
			 * Si fue posible calcular el concepto, se debe persistir el INVOICE
			 * para poder asignarlo al InvoiceConcept
			 */
			log(Level.INFO, "PERSISTING... ");
			this.persistObjectCRONJOB(invoice);
			log(Level.INFO, "Invoice id: " + invoice.getId());

			invoiceConcept.setCalculatedTax(BillingTools.formatDouble(calculatedTax));
			invoiceConcept.setBalance(BillingTools.formatDouble(invoiceConcept.getCalculatedCost() + invoiceConcept.getCalculatedTax() + calculateStamptax));
			invoiceConcept.setInvoice(invoice);
			invoiceConcept.setConcept(concept);
			invoiceConcept.setLastLiquidationDate(this.todayFirstHour.getTime());
			invoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_NORMAL);
			invoiceConcept.setConceptSerializable(conceptoSerealizable);
			invoiceConcept.setExpressionConcept(concept.getExpression());
			invoiceConcept.setExpressionIncrement(concept.getIncreased() == null ? "" : concept.getIncreased().getExpression());

			if (concept.isSeed()) {
				invoiceConcept.setSeed(concept.isSeed());
				concept.setSeed(false);
				this.persistObjectCRONJOB(concept);
			}
			if (invoiceConcept.getId() != 0) {
				log(Level.INFO, "MERGE... ");
				this.persistObjectMerge(invoiceConcept);
				log(Level.INFO, "Invoice Concept id: " + invoiceConcept.getId());
			} else {

				log(Level.INFO, "PERSISTING... ");
				this.persistObjectCRONJOB(invoiceConcept);
				log(Level.INFO, "Invoice Concept id: " + invoiceConcept.getId());
			}
			log(Level.INFO, "Calculo exitoso del concepto: " + concept.getId() + ", invoiceConcept: " + invoiceConcept.getId() + ", invoice: " + invoice.getId());
			log(Level.INFO, "Value: " + invoiceConcept.getCalculatedCost());
			log(Level.INFO, "Tax: " + invoiceConcept.getCalculatedTax());
			log(Level.INFO, "");

			long l1 = (new Date()).getTime();
			log(Level.INFO, "FUNCTION END calculateConcept  : " + ((l1 - l0)) + " MiliSeconds");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean calculateInterests(int anticipatedDays) {
		long l0 = (new Date()).getTime();

		log(Level.INFO, "");
		log(Level.INFO, "--------------------------------");
		log(Level.INFO, "|                               |");
		log(Level.INFO, "|  CALCULATE INTEREST (START)   |");
		log(Level.INFO, "|                               |");
		log(Level.INFO, "--------------------------------");
		log(Level.INFO, "FECHA DE EJECUCION: " + (new Date()));
		createFilterDates(null, anticipatedDays, new Date());
		log(Level.INFO, "");

		String q = "FROM UsuryRate ur ORDER BY ur.id DESC";
		Query query = this.entityManager.createQuery(q);
		query.setMaxResults(1);
		@SuppressWarnings("unchecked")
		ArrayList<UsuryRate> usuryRateList = (ArrayList<UsuryRate>) query.getResultList();
		if (usuryRateList.isEmpty()) {
			log(Level.WARNING, "The are not any USURY_RATE configured, Interest Calculation Canceled");
			return false;
		}
		UsuryRate usuryRate = usuryRateList.get(0);
		Calendar usury = Calendar.getInstance();
		usury.setTime(usuryRate.getDate());
		if (todayFirstHour.get(Calendar.MONTH) / 3 != usury.get(Calendar.MONTH) / 3) {
			log(Level.INFO, "USURY DATE INVALID !!!, Interest Calculation Canceled");
			return false;
		}

		// q =
		// "FROM InvoiceConcept ic WHERE ic.concept.projectProperty.status =  ? AND ic.concept.projectProperty.step =  ? AND ic.balance > ? AND (ic.invoiceConceptType = ? OR ic.invoiceConceptType = ?) AND ic.invoice.expirationDate < ? AND ic.invoice.approved = ? AND ic.invoice.invoiceStatus.id = ?";
		q = "SELECT new InvoiceConcept(invCon.id,invCon.concept,invCon.invoice, invCon.invoiceConceptType, invCon.lastLiquidationDate, invCon.rentableUnitId, invCon.balance)  FROM InvoiceConcept invCon WHERE invCon.concept.projectProperty.status =  ? AND invCon.balance > ? AND (invCon.invoiceConceptType = ? OR invCon.invoiceConceptType = ?) AND invCon.invoice.expirationDate < ? AND invCon.invoice.approved = ? AND invCon.invoice.invoiceStatus.id = ? AND invCon.concept.interestArrears = ?";

		query = this.entityManager.createQuery(q);
		query.setParameter(1, ProjectProperty.STATUS_APPROVED);
		// query.setParameter(2,
		// ProjectProperty.STEP_approved_public_accountant);
		query.setParameter(2, 0.0);
		query.setParameter(3, InvoiceConcept.TYPE_NORMAL);
		query.setParameter(4, InvoiceConcept.TYPE_RETROACTIVE);
		query.setParameter(5, new Date());
		query.setParameter(6, true);
		query.setParameter(7, InvoiceStatus.STATUS_VIGENTE);
		query.setParameter(8, true);

		@SuppressWarnings("unchecked")
		List<InvoiceConcept> invoiceConceptExpiredList = query.getResultList();
		for (InvoiceConcept invoiceConceptExpired : invoiceConceptExpiredList) {
			// if (invoiceConceptExpired.getConcept().isInterestArrears() ==
			// true){
			long l1 = (new Date()).getTime();
			log(Level.INFO, "");
			log(Level.INFO, "START INTERES INVOICE CONCEPT EXPIRED INTERES id: " + invoiceConceptExpired.getId());
			this.calculateInvoiceConceptInterest(invoiceConceptExpired, usuryRate);
			long l2 = (new Date()).getTime();
			log(Level.INFO, "END INTERES INVOICE CONCEPT EXPIRED INTERES" + ((l2 - l1)) + " MilliSeconds");
			// }
		}

		// Log(Level.INFO, "Se guardaran " + invoiceInterestList.size() +
		// " Invoice en la base de datos");
		// persistInvoiceInterest();
		// this.entityManager.flush();
		// Log(Level.INFO, "Se guardaran " + invoiceConceptInterestList.size() +
		// " InvoiceConcept en la base de datos");
		// persistInvoiceConceptInterest();
		// this.entityManager.flush();
		log(Level.INFO, "");
		log(Level.INFO, "--------------------------------");
		log(Level.INFO, "|                               |");
		log(Level.INFO, "|  CALCULATE INTEREST (STOP)    |");
		log(Level.INFO, "|                               |");
		log(Level.INFO, "--------------------------------");
		log(Level.INFO, "");

		long l1 = (new Date()).getTime();
		// System.out.println();
		System.out.println("FUNCTION END INTERESES DE MORA " + ((l1 - l0)) + " MilliSeconds");

		return true;
	}

	private boolean calculateInvoiceConceptInterest(InvoiceConcept invoiceConceptExpired, UsuryRate usuryRate) {
		log(Level.INFO, "INVOICE CONCEPT VENCIDO " + invoiceConceptExpired.getId());
		Concept concept = invoiceConceptExpired.getConcept();
		createFilterDates(concept, 0, new Date());
		Invoice invoice = null;
		InvoiceConcept invoiceConceptInterest = null;
		/*-----------------------------------------------------*/
		{/* Verify if exist and interest created */

			int rentableUnitId = invoiceConceptExpired.getRentableUnitId();
			RentableUnitContribution ruc = null;
			if (invoiceConceptExpired.getConcept().getContributionModule() != null && rentableUnitId != 0) {
				Query q = this.entityManager.createQuery("SELECT r FROM RentableUnitContribution r, Concept c WHERE c.contributionModule = r.contributionModule AND r.rentableUnit.id=? AND c = ?");
				q.setParameter(1, rentableUnitId);
				q.setParameter(2, invoiceConceptExpired.getConcept());
				q.setMaxResults(1);
				@SuppressWarnings("unchecked")
				List<RentableUnitContribution> result = (List<RentableUnitContribution>) q.getResultList();
				if (!result.isEmpty())
					ruc = (RentableUnitContribution) q.getResultList().get(0);
			}

			List<InvoiceConcept> invoiceConceptExistingList = this.billingTools.searchForExistingInvoiceConcept(concept, rentableUnitId, this.billingPeriodIniDate);
			if (!invoiceConceptExistingList.isEmpty()) {
				/*
				 * Si entro a este condiconal, es xq ya se calculo una factura
				 * hoy para el interes. Ó simplemente el concepto del interes
				 * pertenece aun grupo cuya factura ya esta creada para otro
				 * concepto con el mismo grupo.
				 */
				log(Level.INFO, "Verificando si ya se calculo concepto de interes ... para invoiceConcept Vencido " + invoiceConceptExpired.getId());

				for (InvoiceConcept invoCon : invoiceConceptExistingList) {

					if (invoCon.getInvoiceConceptParent() != null && invoCon.getInvoiceConceptParent().getId() == invoiceConceptExpired.getId() && invoCon.getInvoice().isApproved() && invoCon.getConcept().getId() == concept.getId() && this.isConceptAlreadyCalculatedTodayOrAfterToday(invoCon) && invoCon.getRentableUnitId() == rentableUnitId && invoCon.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST) {
						log(Level.INFO, "EL CONCEPTO POR INTERESES YA FUE CALCULADO HOY: " + concept.getId() + ". Y la factura ya fue aprobada (fac id " + invoCon.getInvoice().getId() + ").");
						log(Level.INFO, "");
						return false;
					} else if (invoCon.getInvoiceConceptParent() != null && invoCon.getInvoiceConceptParent().getId() == invoiceConceptExpired.getId() && !invoCon.getInvoice().isApproved() && invoCon.getConcept().getId() == concept.getId() && this.isConceptAlreadyCalculatedTodayOrAfterToday(invoCon) && invoCon.getRentableUnitId() == rentableUnitId && invoCon.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST) {
						log(Level.INFO, "EL CONCEPTO POR INTERESES YA FUE CALCULADO HOY: " + concept.getId());
						log(Level.INFO, "");
						return false;
					} else if (invoCon.getInvoiceConceptParent() != null && invoCon.getInvoiceConceptParent().getId() == invoiceConceptExpired.getId() && !invoCon.getInvoice().isApproved() && invoCon.getConcept().getId() == concept.getId() && !this.isConceptAlreadyCalculatedTodayOrAfterToday(invoCon) && invoCon.getRentableUnitId() == rentableUnitId && invoCon.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST) {
						invoiceConceptInterest = invoCon;
						invoice = invoiceConceptInterest.getInvoice();
						break;
					} else if (!invoCon.getInvoice().isApproved()) {
						invoice = invoCon.getInvoice();
					}
				}

				if (invoiceConceptInterest != null) {
					log(Level.INFO, "YA EXISTE UN INVOICE_CONCEPT Interes PARA (invoice_concept vencido: " + invoiceConceptExpired.getId() + ")");
				}
				if (invoice != null) {
					log(Level.INFO, "EXISTE UN INVOICE ASOCIADO (Con el mismo grupo): " + invoice.getId() + ", para concepto de interes (invoice_concept vencido: " + invoiceConceptExpired.getId() + ")");
				}
			}

			if (invoice == null) {
				if (ruc != null) {
					invoice = this.billingTools.updateInvoice(concept, invoice, ruc.getId());
				} else {
					invoice = this.billingTools.updateInvoice(concept, invoice, null);
				}
				if (invoice == null) {
					log(Level.INFO, "SE ABORTA CALCULACION DE INTERESES XQ FACTURADOR y FACTURADO SON EL MISMO, o no estan definidos !!!!!!!!");
					return false;
				} else {
					this.persistObjectCRONJOB(invoice);
				}
				log(Level.INFO, "SE CREA NUEVO INVOICE PARA EL CONCEPTO DE INTERES");
			}

			boolean isUpdate = false;
			if (invoiceConceptInterest == null) {
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
				invoiceConceptInterest.setIniPeriodDate(new Date());
				invoiceConceptInterest.setConcept(concept);
				invoiceConceptInterest.setExpressionConcept(concept.getExpression());
				if (concept.getIncreased() != null) {
					invoiceConceptInterest.setExpressionIncrement(concept.getIncreased().getExpression());
				} else {
					invoiceConceptInterest.setExpressionIncrement("");
				}
				invoiceConceptInterest.setConcept(concept);
				log(Level.INFO, "SE CREA NUEVO  INVOICE CONCEPT, por conceptos de intereses. Concepto: " + concept.getId() + ", invoice_concept vencido: " + invoiceConceptExpired.getId());
			} else {
				isUpdate = true;
			}

			invoiceConceptInterest.setEndPeriodDate(new Date());
			invoiceConceptInterest.setLastLiquidationDate(new Date());
			/*-----------------------------------------------------*/

			invoiceConceptInterest.setCalculatedCost(BillingTools.formatDouble(invoiceConceptInterest.getCalculatedCost() + (invoiceConceptExpired.getBalance() * usuryRate.getValueDiary())));
			double calculatedTax = 0.0;
			if (concept.getTax() != null && concept.getTax().getValue() != null) {
				calculatedTax = (concept.getTax().getValue() / 100.00) * invoiceConceptInterest.getCalculatedCost();
			}
			invoiceConceptInterest.setCalculatedTax(BillingTools.formatDouble(calculatedTax));
			invoiceConceptInterest.setBalance(BillingTools.formatDouble(invoiceConceptInterest.getCalculatedCost() + invoiceConceptInterest.getCalculatedTax()));

			if (invoiceConceptInterest.getCalculatedCost() <= 0.01) {
				log(Level.INFO, "El calculo del interes es  CERO, se cancela la creacion del interes para " + invoiceConceptInterest.getInvoiceConceptParent().getId());
				return false;
			}
			/**
			 * Si es para actualizar se debe hacer de forma manual ya que no se
			 * trae todos los datos de la base de datos, de lo contrario se crea
			 * con el persist del entitymanager. Cambio por Rendimiento
			 */
			if (isUpdate) {
				log(Level.INFO, "MANUAL PERSIST interés....");
				Query q = this.entityManager.createQuery("UPDATE InvoiceConcept SET endPeriodDate = ?, lastLiquidationDate = ?, calculatedCost = ?, calculatedTax = ?, balance = ?  WHERE id = ?");
				q.setParameter(1, invoiceConceptInterest.getEndPeriodDate());
				q.setParameter(2, invoiceConceptInterest.getLastLiquidationDate());
				q.setParameter(3, invoiceConceptInterest.getCalculatedCost());
				q.setParameter(4, invoiceConceptInterest.getCalculatedTax());
				q.setParameter(5, invoiceConceptInterest.getBalance());
				q.setParameter(6, invoiceConceptInterest.getId());
				q.executeUpdate();
				this.entityManager.flush();
			} else {
				log(Level.INFO, "PERSIST interés....");
				this.persistObjectCRONJOB(invoiceConceptInterest);
				log(Level.INFO, "invoice concept interes id: " + invoiceConceptInterest.getId());

			}

			log(Level.INFO, "Intereses calculados exitosamente para concepto " + concept.getId() + ", invoiceConcept:" + invoiceConceptExpired.getId());
			log(Level.INFO, "");
		}

		return true;
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
		BillingTools.printLog(InvoiceProcessor.class, level, message);
	}

	// private void persistInvoice() {
	// for (Invoice invoice : invoiceList) {
	// this.entityManager.persist(invoice);
	// }
	// }
	//
	// private void persistInvoiceConcept() {
	// for (InvoiceConcept invoiceConcept : invoiceConceptList) {
	// this.entityManager.persist(invoiceConcept);
	// }
	//
	// }
	//
	// private void persistInvoiceInterest() {
	// for (Invoice invoice : invoiceInterestList) {
	// this.entityManager.persist(invoice);
	// }
	// }
	//
	// private void persistInvoiceConceptInterest() {
	// for (InvoiceConcept invoiceConcept : invoiceConceptInterestList) {
	// this.entityManager.persist(invoiceConcept);
	// }
	// }

	// private void upgradeInvoiceConceptManual2(InvoiceConcept invocon) {
	// Query q =
	// this.entityManager.createQuery("Update InvoiceConcept set calculatedTax = ?, balance = ?, invoice = ?, concept = ? , lastLiquidationDate = ?, invoiceConceptType = ?, expressionConcept = ?, expressionIncrement= ?, seed = ?, calculatedCost = ?, iniPeriodDate = ?, endPeriodDate = ? where id = ?");
	// q.setParameter(1, invocon.getCalculatedTax());
	// q.setParameter(2, invocon.getBalance());
	// q.setParameter(3, invocon.getInvoice());
	// q.setParameter(4, invocon.getConcept());
	// q.setParameter(5, invocon.getLastLiquidationDate());
	// q.setParameter(6, invocon.getInvoiceConceptType());
	// q.setParameter(7, invocon.getExpressionConcept());
	// q.setParameter(8, invocon.getExpressionIncrement());
	// q.setParameter(9, invocon.isSeed());
	// q.setParameter(10, invocon.getCalculatedCost());
	// q.setParameter(11, invocon.getIniPeriodDate());
	// q.setParameter(12, invocon.getEndPeriodDate());
	// q.setParameter(13, invocon.getId());
	// q.executeUpdate();
	// }

	private void cleanMemory() {
		try {
			this.entityManager.flush();
			System.gc();

		} catch (Exception e) {
			log(Level.SEVERE, "ERROR CRON-JOB: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This function pesist an ENTITY to the database
	 * 
	 * @param entity
	 *            Class with Entity anotation
	 * @return
	 */
	public boolean persistObjectCRONJOB(Object entity) {
		try {
			this.entityManager.joinTransaction();
			this.entityManager.persist(entity);
			this.entityManager.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This function pesist an ENTITY to the database
	 * 
	 * @param entity
	 *            Class with Entity anotation
	 * @return
	 */
	public boolean persistObjectMerge(Object entity) {
		try {
			this.entityManager.joinTransaction();
			this.entityManager.merge(entity);
			this.entityManager.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
// END CLASS