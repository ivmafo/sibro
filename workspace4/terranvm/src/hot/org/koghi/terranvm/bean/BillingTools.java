package org.koghi.terranvm.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.koghi.terranvm.entity.Address;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.IpcAccumulated;
import org.koghi.terranvm.entity.IpcMonthly;
import org.koghi.terranvm.entity.IpcYearly;
import org.koghi.terranvm.entity.MinimunWage;
import org.koghi.terranvm.entity.ObjectOfContract;
import org.koghi.terranvm.entity.PhoneNumber;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RentableUnitContribution;
import org.koghi.terranvm.entity.Sales;
import org.koghi.terranvm.entity.SystemVariable;

@Name("billingTools")
public class BillingTools {

	public final static double MAX_DOUBLE_VALUE = 10000000000.00;
	public final static double CERO = 0.00;

	private EntityManager entityManager;
	private org.koghi.terranvm.async.Log log = new org.koghi.terranvm.async.Log(this);

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public String ipcYearlyString;
	public String ipcMonthlyString;
	public String ipcAccumalatedString;
	public String minimumWageString;

	public BillingTools(EntityManager entityManager) {
		this.entityManager = entityManager;

		MinimunWage minimumWage = new MinimunWage();
		minimumWageString = minimumWage.getMinimumWages(this.getEntityManager());

		IpcYearly ipcYaerly = new IpcYearly();
		this.ipcYearlyString = ipcYaerly.getIpcs(this.entityManager);

		IpcMonthly ipcMonthly = new IpcMonthly();
		this.ipcMonthlyString = ipcMonthly.getIpcs(this.entityManager);

		IpcAccumulated ipcAccumalated = new IpcAccumulated();
		this.ipcAccumalatedString = ipcAccumalated.getIpcs(entityManager);
	}

	/**
	 * Esta función Recibe la fecha de periodicidad de un concepto y la aumenta
	 * o la disminuye un periodo.
	 * 
	 * @param concept
	 *            Concepto a evaluar
	 * @param inputCalendar
	 *            Objeto calendar que representa la periodicidad de un concepto
	 *            en determinada fecha
	 * @param forward
	 *            Booleano que indica si se debe avanzar en el calendario
	 *            (true), de lo contrario se retrocede
	 * @return fecha de periodicidad aumentada un periodo si forward == true, de
	 *         lo contrario disminuida un periodo
	 */
	public Calendar operateConceptDate(Concept concept, Calendar inputCalendar, boolean forward) {
		int per = concept.getPeriodicity().intValue();
		if (!forward)
			per *= -1;

		switch (concept.getPeriodicityType()) {
		case Concept.DAYS_PERIOD_TYPE:
			inputCalendar.add(Calendar.DAY_OF_MONTH, per);
			break;
		case Concept.MONTHS_PERIOD_TYPE:
			inputCalendar.add(Calendar.MONTH, per);
			break;
		case Concept.YEARS_PERIOD_TYPE:
			inputCalendar.add(Calendar.YEAR, per);
			break;
		default:
			break;
		}
		return inputCalendar;
	}

	/**
	 * Function that increments the fixed value for a concept, and persists
	 * concept's change
	 * 
	 * @param concept
	 *            Concept to be incremented
	 * @param ipcYearlyString
	 *            String of IPCY
	 * @param ipcMonthlyString
	 *            String of IPCM
	 * @param ruc
	 *            RentableUnitContribution Object, can be null
	 * @param sales
	 *            Sales object for rentable unit if any
	 */
	public void incrementConcept(Concept concept, Calendar calculationDate, RentableUnitContribution ruc, boolean persist) {

		log(Level.INFO, "START Ejecución Incremento");
		try {
			if (concept.calculateValueIncreisedConcept(this.ipcYearlyString, this.ipcMonthlyString, calculationDate, ruc, this.ipcAccumalatedString, minimumWageString)) {
				if (persist) {
					entityManager.persist(concept);
				}
				log(Level.INFO, "Se ejecuto el incremento del Concepto, exitosamente");
			} else {
				log(Level.INFO, "No se ejecuto el incremento del Concepto, exitosamente");

			}
		} catch (Exception e) {
			log(Level.SEVERE, "ERROR al ejecutar el incremento");
			e.printStackTrace();
		}
		log(Level.INFO, "END Ejecución Incremento");
	}

	/**
	 * This function receives a double and returns it formatted with two decimal
	 * places
	 * 
	 * @param number
	 *            double to be formatted
	 * @return formatted double with two decimal places
	 */
	public static double formatDouble(double number) {
		Locale.setDefault(Locale.ENGLISH);
		DecimalFormat df = new DecimalFormat("#.00");
		Double temp = Double.parseDouble(df.format(number));
		return temp.doubleValue();
	}

	/**
	 * This function returns the RentableUnitContribution object for a concept.
	 * 
	 * @param concept
	 *            Concept object to be evaluated
	 * @return RentableUnitContribution object if concept has a
	 *         ContributionModule associated and its term-sheet has object
	 *         'rentable unit'. Otherwise return null
	 */
	public RentableUnitContribution getRentableContributionRate(Concept concept) {
		ProjectProperty termSheet = concept.getProjectProperty();
		/*
		 * Un concepto puede venir por modulo de contribucion de dos fomas. 1)
		 * una PH le calcula a todas la unidades arrendables del Activo. 2) PEI
		 * le calcula a un arrendatario con un concepto por modulo de
		 * contribucion. En el primer caso, RUC (el parametro de este metodo) es
		 * diferente a null, el CRON JOB no llama a este metodo cuando el Objeto
		 * de la Hoja de termino es ACTIVO ya que Hay que procesar todas la
		 * unidades arrendables asociadas al RUC. En el segundo caso, RUC es
		 * null, pero hay que verificar si la term-sheet tiene unidad arrendable
		 * asociado y el concepto va por de contribucion, que es el caso de este
		 * metodo.
		 */
		RentableUnitContribution ruc = null;
		if (concept.getContributionModule() != null && termSheet.getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT) {
			Query query = this.entityManager.createQuery("FROM RentableUnitContribution ruc WHERE ruc.contributionModule = ? AND ruc.rentableUnit = ?");
			query.setParameter(1, concept.getContributionModule());
			query.setParameter(2, termSheet.getRentableUnit());
			query.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<RentableUnitContribution> rucList = (List<RentableUnitContribution>) query.getResultList();
			if (rucList.isEmpty()) {
				log(Level.INFO, "LA Term-sheet tiene objeto 2 (Unidad arrendable) pero no existe un RentableUnitContribution asociado. Concepto " + concept.getId());
				return null;
			} else {
				ruc = rucList.get(0);
				if (ruc.getContributionRate().doubleValue() == 0) {
					log(Level.INFO, "LA Term-sheet tiene objeto 2 (Unidad arrendable) pero el RentableUnitContribution asociado tiene porcentaje 0%. Concept " + concept.getId());
					return null;
				}
			}
		}
		return ruc;

	}

	/**
	 * This function returns the RentableUnitContribution object for a concept.
	 * 
	 * @param concept
	 *            Concept object to be evaluated
	 * @return RentableUnitContribution object if concept has a
	 *         ContributionModule associated and its term-sheet has object
	 *         'rentable unit'. Otherwise return null
	 */
	public RentableUnitContribution getContributionModule(Concept concept, InvoiceConcept invoiceConcept) {
		ProjectProperty termSheet = concept.getProjectProperty();
		/*
		 * Un concepto puede venir por modulo de contribucion de dos fomas. 1)
		 * una PH le calcula a todas la unidades arrendables del Activo. 2) PEI
		 * le calcula a un arrenadatario con un concepto por modulo de
		 * contribucion. En el primer caso, RUC (el parametro de este metodo) es
		 * diferente a null, En el segundo caso, RUC es null, pero hay que
		 * verificar si la term-sheet tiene unidad arrendable asociado y el
		 * concepto va por de contribucion
		 */
		RentableUnitContribution ruc = null;
		if (concept.getContributionModule() != null && (termSheet.getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT || termSheet.getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY)) {
			Query query = this.entityManager.createQuery("FROM RentableUnitContribution ruc WHERE ruc.contributionModule = ? AND ruc.rentableUnit.id = ?");
			query.setParameter(1, concept.getContributionModule());
			query.setParameter(2, invoiceConcept.getRentableUnitId());
			query.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<RentableUnitContribution> rucList = (List<RentableUnitContribution>) query.getResultList();
			if (rucList.isEmpty()) {
				log(Level.INFO, "LA Term-sheet tiene objeto 2 (Unidad arrendable) pero no existe un RentableUnitContribution asociado. Concepto " + concept.getId());
				return null;
			} else {
				ruc = rucList.get(0);
				if (ruc.getContributionRate().doubleValue() == 0) {
					log(Level.INFO, "LA Term-sheet tiene objeto 2 (Unidad arrendable) pero el RentableUnitContribution asociado tiene porcentaje 0%. Concept " + concept.getId());
					return null;
				}
			}
		}
		return ruc;

	}

	public Integer getContributionModule2(Concept concept, InvoiceConcept invoiceConcept) {
		ProjectProperty termSheet = concept.getProjectProperty();
		/*
		 * Un concepto puede venir por modulo de contribucion de dos fomas. 1)
		 * una PH le calcula a todas la unidades arrendables del Activo. 2) PEI
		 * le calcula a un arrenadatario con un concepto por modulo de
		 * contribucion. En el primer caso, RUC (el parametro de este metodo) es
		 * diferente a null, En el segundo caso, RUC es null, pero hay que
		 * verificar si la term-sheet tiene unidad arrendable asociado y el
		 * concepto va por de contribucion
		 */
		RentableUnitContribution ruc = null;
		if (concept.getContributionModule() != null && (termSheet.getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT || termSheet.getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY)) {
			Query query = this.entityManager.createQuery("FROM RentableUnitContribution ruc WHERE ruc.contributionModule = ? AND ruc.rentableUnit.id = ?");
			query.setParameter(1, concept.getContributionModule());
			query.setParameter(2, invoiceConcept.getRentableUnitId());
			query.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<RentableUnitContribution> rucList = (List<RentableUnitContribution>) query.getResultList();
			if (rucList.isEmpty()) {
				log(Level.INFO, "LA Term-sheet tiene objeto 2 (Unidad arrendable) pero no existe un RentableUnitContribution asociado. Concepto " + concept.getId());
				return null;
			} else {
				ruc = rucList.get(0);
				if (ruc.getContributionRate().doubleValue() == 0) {
					log(Level.INFO, "LA Term-sheet tiene objeto 2 (Unidad arrendable) pero el RentableUnitContribution asociado tiene porcentaje 0%. Concept " + concept.getId());
					return null;
				}
			}
		}
		return ruc.getId();

	}

	/**
	 * This function returns the RentableUnitContribution object for a given
	 * concept and a rentableUnit id.
	 * 
	 * @param concept
	 *            Concept Object
	 * @param rentableUnitId
	 *            id of rentableUnit Object
	 * @return RentableUnitContribution object (ruc) if exist a ruc with same
	 *         Rentable Unit id as param rentableUnitId. otherwise returns null
	 */
	public RentableUnitContribution getRentableContributionRate(Concept concept, int rentableUnitId) {
		if (concept.getContributionModule() == null)
			return null;
		RentableUnitContribution ruc = null;
		try {
			for (RentableUnitContribution rentableUnitContribution : concept.getContributionModule().getRentableUnitContributions()) {
				if (rentableUnitId == rentableUnitContribution.getRentableUnit().getId())
					return rentableUnitContribution;
			}

		} catch (Exception e) {
			log.log.error("No se pudo consultar el rentableUnitContribution");
			// e.printStackTrace();
		}
		return ruc;

	}

	/**
	 * This function Calculates a the value of a concept
	 * 
	 * @param concept
	 *            Concept object to be evaluated
	 * @param ruc
	 *            RentableUnitContribution object, can be null
	 * @param calculationDate
	 *            , date of calculation
	 * @param sales
	 *            Sales object, can be null
	 * @return Double if concept value calculation was sucessful, otherwise
	 *         returns null
	 */
	public Double calculateConceptValue(Concept concept, RentableUnitContribution ruc, Calendar calculationDate) {
		/* OJO aqui se calcula el valor del concepto */
		long l0 = new Date().getTime();

		log(Level.INFO, "START calculateConceptValue RHINO: ConceptId" + concept.getId() + "  datelong: " + (new Date()).getTime());
		Double calculatedValue = concept.calculateValueConceptByPlugin(ruc, calculationDate, this.ipcYearlyString, this.ipcMonthlyString, this.ipcAccumalatedString, this.entityManager, minimumWageString);
		long l1 = new Date().getTime();
		log(Level.INFO, "END calculateConceptValue RHINO ConceptId: " + concept.getId() + "  Duración: " + (l1 - l0) + " Millisegundos");

		/*
		 * Si no se pudo liquidar el concepto, Se retorna el proceso como
		 * fallido
		 */
		if (calculatedValue == null || calculatedValue.toString().equalsIgnoreCase("NaN")) {
			log(Level.SEVERE, "SE HA PRODUCIDO UN ERROR EJECUTANDO LA FORMULA DEL CONCEPTO: " + concept.getId() + " POR FAVOR VERIFICAR Y MODIFICAR CONCEPTO: " + calculatedValue);
			return null;
		} else {
			return calculatedValue;
		}
	}

	/**
	 * This function returns a Sales Object of a RentableUnit for a given
	 * period, when evaluating a term-sheet's concept
	 * 
	 * @param concept
	 *            Concept Object
	 * @param ruc
	 *            RentableUnitContribution Object, can be null when concept does
	 *            not have a contribution module, but term-sheet's object is
	 *            'rentable Unit'
	 * @param year
	 *            int Year of period
	 * @param month
	 *            int Month of period
	 * @return Object Sales for given period if any, otherwise returns null
	 */
	public Sales getSales(Concept concept, RentableUnitContribution ruc, int year, int month) {
		/*
		 * TRAER VENTAS DE LA UNIDAD ARRENDABLE SI APLICA
		 */
		try {

			ProjectProperty ht = concept.getProjectProperty();
			RentableUnit rentableUnit = null;
			if (ruc != null) {
				rentableUnit = ruc.getRentableUnit();
			} else if (ht.getRentableUnit() != null && ht.getObjectOfContract().getId() == ObjectOfContract.OBJECT_RENTABLEUNIT) {
				rentableUnit = ht.getRentableUnit();
			}

			Sales sales = null;
			if (rentableUnit != null) {
				Query query = this.entityManager.createQuery("FROM Sales sales WHERE sales.salesPeriod.realProperty = ? AND sales.salesPeriod.year = ? AND sales.salesPeriod.month = ? AND sales.rentableUnit = ?");
				query.setParameter(1, rentableUnit.getArea().getFloor().getConstruction().getRealProperty());
				query.setParameter(2, year);
				query.setParameter(3, month);
				query.setParameter(4, rentableUnit);
				query.setMaxResults(1);
				@SuppressWarnings("unchecked")
				List<Sales> salesList = (List<Sales>) query.getResultList();
				if (!salesList.isEmpty()) {
					sales = salesList.get(0);
				}

			}

			if (sales != null) {
				this.log(Level.INFO, "Las ventas (SALES) de la unidad arrendable (" + rentableUnit.getId() + "), son " + sales.getId() + ", valor: " + sales.getValue() + ", ( Concepto " + concept.getId() + ")");
			} else {
				this.log(Level.INFO, "No hay ventas (SALES) para el concepto " + concept.getId());
			}
			return sales;
		} catch (Exception e) {
			e.printStackTrace();
			return new Sales();
		}

	}

	public Sales getSales(Concept concept, Integer rentableUnit, int year, int month) {
		/*
		 * TRAER VENTAS DE LA UNIDAD ARRENDABLE SI APLICA
		 */
		Query q = this.entityManager.createNativeQuery("select rp.id from rentable_unit ru inner join area a on ru.area = a.id inner join floor f on f.id= a.floor inner join construction con on con.id= f.construction inner join real_property rp on rp.id=con.real_property where ru.id = ?");
		q.setParameter(1, rentableUnit);
		Integer realProperty = (Integer) q.getResultList().get(0);
		Sales sales = null;
		if (rentableUnit != null) {
			Query query = this.entityManager.createQuery("FROM Sales sales WHERE sales.salesPeriod.realProperty = ? AND sales.salesPeriod.year = ? AND sales.salesPeriod.month = ? AND sales.rentableUnit.id = ?");
			query.setParameter(1, realProperty);
			query.setParameter(2, year);
			query.setParameter(3, month);
			query.setParameter(4, rentableUnit);
			query.setMaxResults(1);
			@SuppressWarnings("unchecked")
			List<Sales> salesList = (List<Sales>) query.getResultList();
			if (!salesList.isEmpty()) {
				sales = salesList.get(0);
			}

		}

		if (sales != null) {
			this.log(Level.INFO, "Las ventas (SALES) de la unidad arrendable (" + rentableUnit + "), son " + sales.getId() + ", valor: " + sales.getValue() + ", ( Concepto " + concept.getId() + ")");
		} else {
			this.log(Level.INFO, "No hay ventas (SALES) para el concepto " + concept.getId());
		}
		return sales;

	}

	/**
	 * This function creates or updates an Invoice Object for given Concept
	 * 
	 * @param concept
	 *            Concept object
	 * @param invoice
	 *            Invoice onject
	 * @param ruc
	 *            RentableUnitContribution Object, can be null
	 * @return Invoice Object, a new Invoice for given concept if invoice param
	 *         was null, otherwise an updated Invoice object
	 */
	public Invoice updateInvoice(Concept concept, Invoice invoice, Integer ruc) {
		ProjectProperty termSheet = concept.getProjectProperty();
		if (invoice == null) {
			invoice = new Invoice();
			invoice.setCreationDate(new Date());
			invoice.setApproved(false);
			InvoiceStatus ins = null;
			{
				ins = new InvoiceStatus();
				ins.setId(InvoiceStatus.STATUS_VIGENTE);
			}
			invoice.setInvoiceStatus(ins);
			invoice.setProjectProperty(termSheet);
			invoice.setExpeditionDate(new Date());
			invoice.setExpirationDate(termSheet.getExpirationDate());
		}
		invoice.setBiller(termSheet.getBiller());
		invoice.setIdNumberBiller(termSheet.getBiller().getIdNumber());
		invoice.setNameBiller(termSheet.getBiller().getNameBusinessEntity());

		invoice.setAddressBiller(termSheet.getBillerAddress().toString());
		invoice.setPhoneBiller(termSheet.getPhoneNumberByPhoneBiller().getNumber());
		invoice.setCityBiller(termSheet.getBillerAddress().getCity().getName());

		if (termSheet.getRealProperty() != null) {
			invoice.setRealProperty(termSheet.getRealProperty());
		}

		if (termSheet.getBilled() != null) {

			invoice.setBilled(termSheet.getBilled());
			invoice.setIdNumberBilled(termSheet.getBilled().getIdNumber());
			invoice.setNameBilled(termSheet.getBilled().getNameBusinessEntity());
			invoice.setAddressBilled(termSheet.getBilledAddress().toString());
			invoice.setPhoneBilled(termSheet.getPhoneNumber().getNumber());
			invoice.setCityBilled(termSheet.getBilledAddress().getCity().getName());

		} else if (termSheet.getBilled() == null && ruc != null) {
			/*
			 * Si la hoja de teminos no tiene asignado un tercero FACTURADO,
			 * entonces es necesario que en la factura el facturado sea el
			 * arrendatario de la unidad arrendable y en su defecto el
			 * propietario. Si el facturado y facturador son el mismo entonce no
			 * se debe generar factura.
			 */
			BusinessEntity tercero = null;
			Address address = null;
			PhoneNumber phoneNumber = null;
			Query q = this.entityManager.createQuery("SELECT ruc.rentableUnit FROM RentableUnitContribution ruc where ruc.id=?");
			q.setParameter(1, ruc);
			RentableUnit rentableUnit = (RentableUnit) q.getResultList().get(0);
			if (concept.getResponsible().intValue() == Concept.RESPONSAIBLE_LESSEE && rentableUnit.getBusinessEntityByLessee() != null) {
				tercero = rentableUnit.getBusinessEntityByLessee();
				address = rentableUnit.getAddressByAddressLessee();
				phoneNumber = rentableUnit.getPhoneNumberByPhonenumberLessee();

			} else {
				tercero = rentableUnit.getBusinessEntityByOwner();
				address = rentableUnit.getAddressByAddressOwner();
				phoneNumber = rentableUnit.getPhoneNumberByPhonenumbeOwner();
			}
			if (tercero != null) {
				invoice.setBilled(tercero);
				invoice.setIdNumberBilled(tercero.getIdNumber());
				invoice.setNameBilled(tercero.getNameBusinessEntity());
				invoice.setAddressBilled(address.toString());
				invoice.setPhoneBilled(phoneNumber.getNumber());
				invoice.setCityBilled(address.getCity().getName());
			}
		}

		/*
		 * Si se cumple el siguiente condicional, es xq el facturado y el
		 * facturador son el mismo, en este caso se debe abortar el CRON JOB
		 */
		if (invoice.getBiller() == null || invoice.getBilled() == null || invoice.getBiller().getId() == invoice.getBilled().getId()) {
			return null;
		}
		invoice.setGroupNumber(concept.getGroupNumber());
		invoice.setDocumentType(concept.getDocumentType());
		return invoice;
	}

	/**
	 * This function return a list of InvoiceConcept in a period of time for a
	 * given concept
	 * 
	 * @param concept
	 *            Concept object, invoiceConcept from returned list must be
	 *            associated with this concept
	 * @param rentableUnitId
	 *            integer representing rentable unit id. 0 if none
	 * @param iniCalendar
	 *            Calendar object initial period
	 * @param endCalendar
	 *            Calendar object end period
	 * @return List of InvoiceConcept, if no invoicefound return an empty list
	 */
	public List<InvoiceConcept> searchForExistingInvoiceConcept(Concept concept, int rentableUnitId, Calendar iniCalendar) {

		// String queryInvCon =
		// "FROM InvoiceConcept invCon WHERE invCon.concept.projectProperty = ? AND invCon.invoice.creationDate >= ? AND invCon.invoice.creationDate <= ? AND invCon.invoice.groupNumber = ? AND invCon.invoice.documentType = ? AND invCon.rentableUnitId = ? AND invCon.invoice.approved = ? AND  invCon.invoice.invoiceStatus.id = ?";
		// String queryInvCon =
		// "FROM InvoiceConcept invCon WHERE invCon.concept.projectProperty = ? AND invCon.invoice.creationDate >= ? AND invCon.invoice.creationDate <= ? AND invCon.invoice.groupNumber = ? AND invCon.invoice.documentType = ? AND invCon.rentableUnitId = ? AND  invCon.invoice.invoiceStatus.id = ?";
		// 27468 29469 45343
		log(Level.INFO, "		START searchForExistingInvoiceConcept");
		long l0 = (new Date()).getTime();
		List<InvoiceConcept> list = new ArrayList<InvoiceConcept>();
		Query q = this.entityManager
				.createNativeQuery("select invoicecon0_.id as col_0_0_, invoicecon0_.concept as col_1_0_, invoicecon0_.invoice as col_2_0_,  invoicecon0_.invoice_concept_type as col_3_0_,  invoicecon0_.last_liquidation_date as col_4_0_, invoicecon0_.calculated_cost as col_5_0_, invoicecon0_.balance as col_6_0_, invoicecon0_.calculated_tax as col_7_0_,  invoicecon0_.rentable_unit_id as col_8_0_, invoicecon0_.invoice_concept_parent as col_9_0_   from     public.invoice_concept invoicecon0_  inner join    public.concept concept1_   on invoicecon0_.concept=concept1_.id    inner join    public.invoice invoice2_  on invoicecon0_.invoice=invoice2_.id cross  join  public.concept concept3_ cross  join  public.invoice invoice4_  where invoicecon0_.concept=concept3_.id  and invoicecon0_.invoice=invoice4_.id  and concept3_.project_property=?  and invoicecon0_.last_liquidation_date>=?   and invoice4_.group_number=?    and invoice4_.document_type=?  and invoicecon0_.rentable_unit_id=?  and invoice4_.invoice_status=?");
		q.setParameter(1, concept.getProjectProperty());
		q.setParameter(2, iniCalendar.getTime());
		// q.setParameter(3, endCalendar.getTime());
		q.setParameter(3, concept.getGroupNumber());
		q.setParameter(4, concept.getDocumentType());
		q.setParameter(5, rentableUnitId);
		// q.setParameter(7, false);
		q.setParameter(6, InvoiceStatus.STATUS_VIGENTE);
		List<?> list2 = q.getResultList();
		log(Level.INFO, list.size() + " " + list2.size());
		for (int i = 0; i < list2.size(); i++) {
			Object[] a = (Object[]) list2.get(i);
			Concept concept6 = this.entityManager.find(Concept.class, a[1]);
			Invoice invoice = this.entityManager.find(Invoice.class, a[2]);
			InvoiceConcept invoiceConceptParent = null;
			if (a[9] != null)
				invoiceConceptParent = this.entityManager.find(InvoiceConcept.class, a[9]);

			InvoiceConcept invoiceConcept = new InvoiceConcept((Integer) a[0], concept6, invoice, (Short) a[3], (Date) a[4], (Double) a[5], (Double) a[6], (Double) a[7], invoiceConceptParent, (Integer) a[8]);
			list.add(invoiceConcept);
		}

		long l1 = (new Date()).getTime();
		log(Level.INFO, "		END searchForExistingInvoiceConcept  : " + ((l1 - l0)) + " MilliSeconds");
		return list;

	}

	/**
	 * This function pesist an ENTITY to the database
	 * 
	 * @param entity
	 *            Class with Entity anotation
	 * @return
	 */
	@Transactional(TransactionPropagationType.REQUIRED)
	public boolean persistObject(Object entity) {
		try {
			this.entityManager.joinTransaction();
			this.entityManager.persist(entity);
			this.entityManager.flush();
			return true;
		} catch (Exception e) {
			try {
				InvalidValue[] arr = ((InvalidStateException) e).getInvalidValues();
				for (InvalidValue invalidValue : arr) {
					log(Level.INFO, invalidValue.getPropertyName() + " " + invalidValue.getValue());
				}
			} catch (Exception e2) {
			}
			log(Level.SEVERE, "ERROR, could not persist entity: " + entity.getClass() + " - " + entity);
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * This function prints a message in log file
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	private void log(Level level, String message) {
		BillingTools.printLog(BillingTools.class, level, message);
	}

	/**
	 * This function print LOGS for multiple Classes
	 * 
	 * @param level
	 * @param Functionality
	 * @param message
	 */
	public static void printLog(Class<?> className, Level level, Object message) {
		StringBuilder init = new StringBuilder();
		init.append(" ");
		int minLong = className.getSimpleName().length() + level.getName().toString().length();
		if (level == Level.WARNING || level == Level.SEVERE) {
			minLong--;
		}
		while (minLong + init.length() < 40) {
			init.append("#");
		}
		init.append(" ");
		init.append(message.toString());
		Logger.getLogger(className.getSimpleName()).log(level, init.toString());
	}

	/**
	 * Metodo que convierte un objeto a un arreglo de bytes
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] objectToBytes(Object object) throws IOException {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		ObjectOutputStream oos;

		oos = new ObjectOutputStream(byteArray);
		oos.writeObject(object);
		byte[] res = byteArray.toByteArray();
		oos.close();
		return res;
	}

	/**
	 * Metodo que convierte un arreglo de bytes a un objeto
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
		ObjectInputStream ois;
		ois = new ObjectInputStream(byteArray);
		Object answer = ois.readObject();
		ois.close();
		return answer;
	}

	/**
	 * Métdo para pasar un concepto serealizado actualizando los datos
	 * necesarios para ser usado en calculos del concepto
	 * 
	 * @param bytes
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Concept getConceptFromBytes(byte[] bytes, EntityManager entityManager) throws IOException, ClassNotFoundException {
		Concept concept = (Concept) bytesToObject(bytes);
		concept.setProjectProperty(entityManager.find(ProjectProperty.class, concept.getProjectProperty().getId()));
		return concept;
	}

	/**
	 * Método que llama los gets necesarios para ser usados despúes y que
	 * devuelve el concepto serealizado y
	 * 
	 * @param concept
	 * @throws IOException
	 */
	public static byte[] serializeConcept(Concept concept) throws IOException {
		if (concept.getIncreased() != null) {
			concept.getIncreased().getNextIncreased();
		}

		if (concept.getProjectProperty() != null && concept.getProjectProperty().getProject() != null && concept.getProjectProperty().getProject().getSystemVariablesList() != null) {
			for (SystemVariable variable : concept.getProjectProperty().getProject().getSystemVariablesList()) {
				variable.getValue();
			}
		}

		ProjectProperty aux = concept.getProjectProperty();
		concept.setProjectProperty(new ProjectProperty(aux.getId()));
		byte[] res = objectToBytes(concept);
		concept.setProjectProperty(aux);
		return res;
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

		Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
		c1.setTime(iniDate);
		c2.setTime(endDate);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c2.set(Calendar.MINUTE, 0);
		c1.set(Calendar.MILLISECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		iniDate = c1.getTime();
		endDate = c2.getTime();

		long diff = iniDate.getTime() - endDate.getTime();
		return Math.abs((int) (diff / (1000 * 60 * 60 * 24)));

	}

	/**
	 * Este método retorna el número de dias que se deben anticipar los calculos
	 * para los conceptos anticipados
	 * 
	 * @param calendar
	 *            instancia de un Objecto Calendar
	 * @return Int, el numero de dias que se debe adelantar el calculo de los
	 *         conceptos de cobro anticipado, de acuerdo al mes que se haya
	 *         pasado en el Celendar.
	 */
	public int getAnticipatedDaysForGivenDate(Calendar calendar) {

		String queryInvCon = "Select days FROM days_for_early_payment WHERE id = ?";
		Query q = this.entityManager.createNativeQuery(queryInvCon);
		q.setParameter(1, calendar.get(Calendar.MONTH));
		q.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<Object> tempList = q.getResultList();
		return Integer.parseInt((tempList.get(0)) + "");

	}

	/**
	 * Este metodo imprime el nombre y el valor de cada uno de los atributos que
	 * se pasan como argumento.
	 */
	public void printObjectInstance(Object object) {
		this.log(Level.INFO, ToStringBuilder.reflectionToString(object));
	}

	/**
	 * This function pesist an ENTITY to the database
	 * 
	 * @param entity
	 *            Class with Entity anotation
	 * @return true if the process was succesfull
	 */
	@Transactional(TransactionPropagationType.REQUIRED)
	public boolean removeObject(Object entity) {
		try {
			this.entityManager.joinTransaction();
			this.entityManager.remove(entity);
			this.entityManager.flush();
			return true;
		} catch (Exception e) {
			this.printObjectInstance(entity);
			try {
				InvalidValue[] arr = ((InvalidStateException) e).getInvalidValues();
				for (InvalidValue invalidValue : arr) {
					log(Level.INFO, invalidValue.getPropertyName() + " " + invalidValue.getValue());
				}
			} catch (Exception e2) {
			}
			log(Level.SEVERE, "ERROR, could not remove entity: " + entity.getClass());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This function remove an ENTITY from database
	 * 
	 * @param entity
	 *            Class with Entity anotation
	 * @return true if the process was succesfull
	 */
	@Transactional(TransactionPropagationType.REQUIRED)
	public boolean mergeObject(Object entity) {
		try {
			this.entityManager.joinTransaction();
			this.entityManager.merge(entity);
			this.entityManager.flush();
			return true;
		} catch (Exception e) {
			this.printObjectInstance(entity);
			try {
				InvalidValue[] arr = ((InvalidStateException) e).getInvalidValues();
				for (InvalidValue invalidValue : arr) {
					log(Level.INFO, invalidValue.getPropertyName() + " " + invalidValue.getValue());
				}
			} catch (Exception e2) {
			}
			log(Level.SEVERE, "ERROR, could not merge entity: " + entity.getClass());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Este metodo crea un Invoice Concept basico, sin persistir en la base de
	 * datos, el invoice asociado tampoco esta creado en la base de datos Los
	 * campos Date son iguales a la fecha de hoy, los campos numericos
	 * calculados son iguales a CERO, los campos que dependan del concepto se
	 * instanciaran segun corresponda.
	 * 
	 * @param concept
	 *            Concepto base para crear el invoiceConcept
	 * @param ruc
	 *            instancia de RentableUnitContribution puede ser NULL
	 * @return
	 */
	public InvoiceConcept createABasicInvoiceConcept(Concept concept, RentableUnitContribution ruc) {

		int rentableUnitId = 0;
		if (ruc != null) {
			rentableUnitId = ruc.getRentableUnit().getId();
		}

		Invoice invoice = this.updateInvoice(concept, null, null);
		InvoiceConcept invoiceConcept = new InvoiceConcept();
		invoiceConcept.setInvoice(invoice);
		invoiceConcept.setConcept(concept);
		invoiceConcept.setLastLiquidationDate(new Date());
		invoiceConcept.setCalculatedCost(0.0);
		invoiceConcept.setCalculatedTax(0.0);
		invoiceConcept.setBalance(0.0);
		invoiceConcept.setIniPeriodDate(new Date());
		invoiceConcept.setEndPeriodDate(new Date());
		invoiceConcept.setRentableUnitId(rentableUnitId);
		invoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_NORMAL);
		invoiceConcept.setExpressionConcept(concept.getExpression());
		if (concept.getIncreased() != null) {
			invoiceConcept.setExpressionIncrement(concept.getIncreased().getExpression());
		} else {
			invoiceConcept.setExpressionIncrement("");
		}
		return invoiceConcept;
	}

	
	/**
	 * Esta funcion recibe un Proyecto como argumento y devuelve la fecha del
	 * ultimo cierre de facturacion.
	 * 
	 * @param project
	 *            Objeto de tipo Project del cual se quiere saber la ultima
	 *            fecha de cierre de facturacion
	 * @return Un objeto de tipo Date, devuelve NULL si el projecto no ha sido
	 *         cerrado nunca.
	 */
	public Date getProjectLastClosure(Project project) {
		Query query = this.entityManager.createQuery("SELECT pc.closureDate FROM ProjectClosure pc WHERE pc.project = ? ORDER BY pc.closureDate DESC");
		query.setParameter(1, project);
		query.setMaxResults(1);
		if (query.getResultList().isEmpty()) {
			return null;
		} else {
			return (Date) query.getResultList().get(0);
		}
	}

	public Date getProjectLastClosure(Integer project) {
		Query query = this.entityManager.createQuery("SELECT pc.closureDate FROM ProjectClosure pc WHERE pc.project.id = ? ORDER BY pc.closureDate DESC");
		query.setParameter(1, project);
		query.setMaxResults(1);
		if (query.getResultList().isEmpty()) {
			return null;
		} else {
			return (Date) query.getResultList().get(0);
		}
	}

	/**
	 * Esta funcion tiene como objetivo crear un invoiceConcept, en ceros y lo
	 * persiste en la base de datos
	 * 
	 * @param concept
	 *            Concepto para el cual se van a generar InvoiceConcepts
	 */
	public void createInvoiceConceptForImmediatePayment(Concept concept) {

		if (concept.getContributionModule() != null && concept.getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_REALPROPERTY) {
			for (RentableUnitContribution ruc : concept.getContributionModule().getRentableUnitContributions()) {
				InvoiceConcept invoiceConcept = this.createABasicInvoiceConcept(concept, ruc);
				this.persistObject(invoiceConcept.getInvoice());
				this.persistObject(invoiceConcept);
			}
		} else {
			RentableUnitContribution ruc = this.getRentableContributionRate(concept);
			InvoiceConcept invoiceConcept = this.createABasicInvoiceConcept(concept, ruc);
			this.persistObject(invoiceConcept.getInvoice());
			this.persistObject(invoiceConcept);
		}

	}

	/**
	 * Esta funcion recibe un Concepto como argumento y devuelve la fecha del
	 * ultimo invoice_concept calculado, que sea de tipo NORMAL y pertenezca a
	 * una factura Aprobada.
	 * 
	 * @param concept
	 *            Objeto de tipo Concept del cual se quiere saber la fecha de la
	 *            ultima liquidacion
	 * @return Un objeto de tipo Date, devuelve NULL si el concepto no se ha
	 *         calculado aun por primera vez
	 */

	@SuppressWarnings("unchecked")
	public Date getlastLiquidationDateFromThisConcept(Concept concept) {
		List<Date> lastLiquidation;
		Query q = this.entityManager.createNativeQuery("SELECT last_liquidation_date from invoice_concept where id= (select max(id) from invoice_concept ic2 where ic2.credit_note_status=0 and ic2.invoice_concept_type=? and concept=?)");
		q.setParameter(1, InvoiceConcept.TYPE_NORMAL);
		q.setParameter(2, concept);
		lastLiquidation = q.getResultList();
		if (lastLiquidation.size() > 0)
			return lastLiquidation.get(0);
		return null;
	}

	/**
	 * calcula los intereses de mora generados entre un rango de fechas, con un
	 * balance determinado.
	 * 
	 * @param iniPeriod
	 * @param endPeriod
	 * @param balance
	 * @return valor correspondiente a los intereses de mora
	 */
	@SuppressWarnings("unchecked")
	public double recalculateInterest(Date iniPeriod, Date endPeriod, Double balance) {
		double answer = 0.0;
		double porcen = 0.0;
		log(Level.INFO, iniPeriod + " & " + endPeriod);
		Query q = getEntityManager().createNativeQuery("SELECT * from history_usury_rate order by date desc limit 2");
		List<Object[]> answerQuery = q.getResultList();

		for (Object[] objects : answerQuery) {
			log(Level.INFO, "usuryRate.getDate(): " + objects[2] + " calcularrango(iniPeriod): " + calcularrango(iniPeriod));
			if (calcularrango((Date) objects[2]) == calcularrango(iniPeriod)) {
				porcen = (Double) objects[1];
				log(Level.INFO, "Entro y el valor es: " + (Double) objects[1]);
			}
		}
		Calendar ini = Calendar.getInstance();
		ini.setTime(iniPeriod);
		int i = 1;
		log(Level.INFO, "usuryRate.getValue(): " + porcen);
		while (calcularrango(ini.getTime()) != calcularrango(endPeriod)) {
			log(Level.INFO, i + " " + ini.getTime());
			answer += balance * getValueDiary(porcen);
			ini.add(Calendar.DAY_OF_MONTH, +1);
			i++;
			log(Level.INFO, "dia: " + i);
		}
		porcen = 0.0;
		for (Object[] objects : answerQuery) {
			log(Level.INFO, "usuryRate.getDate(): " + objects[2] + " calcularrango(iniPeriod): " + calcularrango(iniPeriod));
			if (calcularrango((Date) objects[2]) == calcularrango(endPeriod)) {
				porcen = (Double) objects[1];
				log(Level.INFO, "Entro y el valor es: " + (Double) objects[1]);
			}
		}

		log(Level.INFO, "hasta aqui se han calculado: " + answer);
		log(Level.INFO, "usuryRate.getValue(): " + porcen);
		if (porcen == 0.0)
			log.log.error("No hay una tasa de usura registrada para el periodo a evaluar o el valor de este es 0.0");
		int diff = daysDifference(ini.getTime(), endPeriod) + 1;
		log(Level.INFO, "diff: " + diff);
		answer += balance * getValueDiary(porcen) * diff;
		log(Level.INFO, "getValueDiary(porcen)" + getValueDiary(porcen));
		log(Level.INFO, "answer= " + answer);
		return BillingTools.formatDouble(answer);
	}

	/**
	 * calcula el trimestre en el que se encuentra una fecha.
	 * 
	 * @param usuryRate
	 * @return
	 */
	private int calcularrango(Date usuryRate) {
		Calendar iniCalendar = Calendar.getInstance();
		Calendar usuryCalendar = Calendar.getInstance();
		usuryCalendar.setTime(usuryRate);
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
		return iniCalendar.get(Calendar.MONTH);
	}

	/***
	 * pasa una tasa de usura periodica Anual a diaria
	 * 
	 * @param getValue
	 * @return
	 */
	private double getValueDiary(double getValue) {
		double a = new Double(1);
		double b = new Double(360);
		double p = new Double(a / b);
		return (Math.pow(1 + getValue, p)) - 1;
	}

	/**
	 * metodo que busca los intereses de mora asociados a un determinado
	 * InvoiceConcept, que hayan sido calculados por ultima vez en una fecha
	 * superior a la recibida como parametro.
	 * 
	 * @param invoiceConcept
	 * @param fechaRecaudo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<InvoiceConcept> interesesSegunInvoiceConcept(InvoiceConcept invoiceConcept, Date fechaRecaudo) {
		List<InvoiceConcept> listToReturn = new ArrayList<InvoiceConcept>();
		Query q = getEntityManager().createNativeQuery("SELECT ic.* from invoice_concept ic JOIN invoice i ON i.id = ic.invoice where ic.invoice_concept_parent = ? and i.approved = false and last_liquidation_date >= ?  and ic.invoice_concept_type = ? ORDER BY last_liquidation_date ASC", InvoiceConcept.class);
		q.setParameter(1, invoiceConcept.getId());
		q.setParameter(2, fechaRecaudo);
		q.setParameter(3, InvoiceConcept.TYPE_INTEREST);
		listToReturn = q.getResultList();
		return listToReturn;
	}

	/**
	 * metodo que recibe un invoiceConcept, y a este le suma o le resta el valor
	 * de intereses que se generaron entre la fecha que se recibe por parametro
	 * y el día actual.
	 * 
	 * @param invcon
	 * @param sumar
	 * @param recoverDate
	 * @return
	 */
	public List<InvoiceConcept> procesarRecalculacionIntereses(InvoiceConcept invcon, boolean sumar, Date recoverDate) {

		log.log.info(invcon + " " + recoverDate);
		List<InvoiceConcept> interestList;
		interestList = interesesSegunInvoiceConcept(invcon, recoverDate);
		log.log.info("el tamaño de la lista de tipo Interes de mora es de: " + interestList.size());
		if (!interestList.isEmpty()) {
			log.log.info("El invoiceConcept recaudado tiene intereses de mora. Inicia recalculación de Intereses");
			for (int i = 0; i < interestList.size(); i++) {

				Calendar calendaRecover = Calendar.getInstance(), calendarInvoCon = Calendar.getInstance();
				calendaRecover.setTime(recoverDate);
				calendarInvoCon.setTime(interestList.get(i).getIniPeriodDate());

				// if (daysDifference(calendaRecover.getTime(),
				// calendarInvoCon.getTime()) == 0) {
				// invcon.setIniPeriodDate(calendarInvoCon.getTime());//QUE ES
				// ESTO
				// }

				double nuevoValorTemporalInteres = 0.0;

				if (calendaRecover.after(calendarInvoCon)) {
					log.log.info("this.getRecoverDate(): " + recoverDate + " interestList.get(i).getLastLiquidationDate(): " + interestList.get(i).getLastLiquidationDate() + " invcon.getBalance(): " + invcon.getBalance());
					nuevoValorTemporalInteres = recalculateInterest(recoverDate, interestList.get(i).getLastLiquidationDate(), invcon.getBalance());
				} else {
					log.log.info("invcon.getIniPeriodDate(): " + invcon.getIniPeriodDate() + " interestList.get(i).getLastLiquidationDate(): " + interestList.get(i).getLastLiquidationDate() + " invcon.getBalance(): " + invcon.getBalance());
					nuevoValorTemporalInteres = recalculateInterest(calendarInvoCon.getTime(), interestList.get(i).getLastLiquidationDate(), invcon.getBalance());
				}
				log.log.info("Se relaiza un incremento/decremento de: " + nuevoValorTemporalInteres);
				double newCalculatedCost = nuevoValorTemporalInteres;
				double newBalance = 0.0;
				double newCalculatedTax = 0.0;
				double taxUsed = 0.0;
				if (invcon.getConcept().getTax() != null && invcon.getConcept().getTax().getValue() != null) {
					taxUsed = BillingTools.formatDouble((invcon.getConcept().getTax().getValue() / 100.00));
				}
				log.log.info("taxUsed: " + taxUsed);
				boolean invoiceConceptDeleted = false;
				newBalance = taxUsed != 0.0 ? newCalculatedCost * (1 + taxUsed) : newCalculatedCost;
				newCalculatedTax = taxUsed != 0.0 ? newCalculatedCost * (taxUsed) : 0.0;
				// newBalance = Format_number.Format(newBalance);
				log(Level.INFO, "newBalance" + newBalance);
				if (sumar) {
					newCalculatedCost = interestList.get(i).getCalculatedCost() + newCalculatedCost;
					newBalance += interestList.get(i).getBalance();
					newCalculatedTax = interestList.get(i).getCalculatedTax() + newCalculatedTax;
					log.log.info("AAA: newCalculatedTax: " + newCalculatedTax + " newCalculatedCost: " + newCalculatedCost + " (newBalance - newCalculatedCost): " + (newBalance - newCalculatedCost) + " interestList.get(i).getCalculatedTax() + (newBalance - newCalculatedCost): " + (interestList.get(i).getCalculatedTax() + (newBalance - newCalculatedCost)));
					if (newBalance < 0.01) {
						boolean shouldIDeleteInvoice = false;
						if (interestList.get(i).getInvoice().getInvoiceConcepts().size() == 1) {
							shouldIDeleteInvoice = true;
						}
						InvoiceConcept invocon = interestList.get(i);
						log.log.info("El nuevo balance de este Intéres es = 0.0, se procede a eliminar el invoiceConcept de tipo intéres con id: " + interestList.get(i).getId());
						Query q = getEntityManager().createNativeQuery("DELETE FROM invoice_concept where id = ?");
						q.setParameter(1, interestList.get(i).getId());
						q.executeUpdate();
						this.entityManager.joinTransaction();
						invoiceConceptDeleted = true;
						if (shouldIDeleteInvoice) {
							log.log.info("La Invoice queda sin invoiceConcept, se procede a eliminar la invoice");
							Query q2 = getEntityManager().createNativeQuery("DELETE FROM invoice where id = ?");
							q2.setParameter(1, interestList.get(i).getInvoice().getId());
							q2.executeUpdate();
							this.entityManager.joinTransaction();
						}
						interestList.remove(invocon);

					} else {
						interestList.get(i).setBalance(BillingTools.formatDouble(newBalance));
						if (invcon.getBalance() == 0) {
							// cambiar la fecha del invoice_concept de tipo
							// interes
							interestList.get(i).setEndPeriodDate(calendaRecover.getTime());
						}
						log.log.info("Intereses Recalculados Exitosamente para el invoiceConcept de tipo intéres con id: " + interestList.get(i).getId() + " el nuevo balance es: " + interestList.get(i).getBalance());
						persistObject(interestList.get(i));
					}
				} else {
					newBalance = (interestList.get(i).getBalance() - newBalance) < 0.0 ? 0.0 : interestList.get(i).getBalance() - newBalance;
					// Math.Max ( 0.0 , (interestList.get(i).getBalance() -
					// newBalance) );
					// newBalance = interestList.get(i).getBalance() -
					// newBalance;

					newCalculatedCost = (interestList.get(i).getCalculatedCost() - newCalculatedCost) < 0.0 ? 0.0 : interestList.get(i).getCalculatedCost() - newCalculatedCost;
					// Math.Max ( 0.0 , (interestList.get(i).getCalculatedCost()
					// - newCalculatedCost) );
					// newCalculatedCost =
					// interestList.get(i).getCalculatedCost() -
					// newCalculatedCost;

					// newCalculatedTax = newCalculatedCost * taxUsed;

					if (interestList.get(i).getCalculatedTax() != 0 && interestList.get(i).getCalculatedTax() > (newBalance - newCalculatedCost)) {
						newCalculatedTax = interestList.get(i).getCalculatedTax() - newCalculatedTax;
					} else {
						newCalculatedTax = 0.0;
					}

				}
				log(Level.INFO, "newBalance: " + newBalance + " newCalculatedCost: " + newCalculatedCost + " newCalculatedTax: " + newCalculatedTax);
				if (!invoiceConceptDeleted) {
					interestList.get(i).setBalance(BillingTools.formatDouble(newBalance));
					interestList.get(i).setCalculatedCost(BillingTools.formatDouble(newCalculatedCost));
					interestList.get(i).setCalculatedTax(BillingTools.formatDouble(newCalculatedTax));
					persistObject(invcon);
				}
				// log.log.info("nuevo Balance: " +
				// interestList.get(i).getBalance());
			}
		}
		return interestList;
	}

	/**
	 * forma el nombre a mostrar de un invoiceConcept, dependiendo del
	 * tipo(intereses, retroactivos, normales)
	 * 
	 * @param invoiceConceptType
	 * @param nameConcept
	 * @return nombre concepto(modificado)
	 */
	public String nameInvoiceConcept(int invoiceConceptType, String nameConcept) {
		String name = "";
		if (invoiceConceptType == InvoiceConcept.TYPE_INTEREST)
			name = "Interes de Mora(" + nameConcept + ")";
		else if (invoiceConceptType == InvoiceConcept.TYPE_RETROACTIVE)
			name = "Retroactivo(" + nameConcept + ")";
		else
			name = nameConcept;
		return name;
	}
	// public static void main(String[] args) {
	// BillingTools tools = new BillingTools();
	// Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
	// c1.set(Calendar.DAY_OF_MONTH, 20);
	// c2.set(Calendar.DAY_OF_MONTH, 20);
	// c2.add(Calendar.DAY_OF_MONTH, +1);
	// c1.set(Calendar.HOUR_OF_DAY, 0);
	// c2.set(Calendar.HOUR_OF_DAY, 0);
	// c1.set(Calendar.MINUTE, 0);
	// c2.set(Calendar.MINUTE, 0);
	// c1.set(Calendar.MILLISECOND, 0);
	// c2.set(Calendar.MILLISECOND, 0);
	// log(Level.INFO,c1.getTime());
	// log(Level.INFO,c2.getTime());
	// log(Level.INFO,tools.daysDifference(c1.getTime(), c2.getTime()));
	//
	// }
}