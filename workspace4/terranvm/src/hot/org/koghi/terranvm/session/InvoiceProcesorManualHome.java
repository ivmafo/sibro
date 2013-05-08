package org.koghi.terranvm.session;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.async.InvoiceProcessor;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.SystemConfiguration;

@Name("InvoiceProcessorManual")
public class InvoiceProcesorManualHome extends EntityHome<InvoiceStatus> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 
     */
	@In
	EntityManager entityManager;

	private int conceptId;
	private int termSheetId;
	private Date date = Calendar.getInstance().getTime();
	private SystemConfiguration systemConfiguration = new SystemConfiguration();
	// XXX private final int KINDOFTRANSACTIONRECOVER = 0;
	private final int KINDOFTRANSACTIONCREDITNOTE = 1;

	public void callExecution() {
		System.out.println("______________________________________");
		System.out.println("CRON JOB MANUAL START");
		if (date != null) {
			if (this.conceptId != 0 && this.termSheetId == 0) {
				this.executeInvoiceProcessor(Concept.class, this.conceptId, this.date);

			} else if (this.termSheetId != 0 && this.conceptId == 0) {
				this.executeInvoiceProcessor(ProjectProperty.class, this.termSheetId, this.date);

			} else if (this.termSheetId == 0 && this.conceptId == 0) {
				this.executeInvoiceProcessor(null, 0, this.date);

			}

		} else {
			System.out.println("DATE IS NULL");
		}
		System.out.println("CRON JOB MANUAL END");
		System.out.println("______________________________________");

	}

	@SuppressWarnings("unchecked")
	public void executeInvoiceProcessor(@SuppressWarnings("rawtypes") Class clase, int id, Date date) {

		InvoiceProcessor invoiceProcessor = new InvoiceProcessor(this.entityManager);
		Object object = null;

		if (id != 0 && date != null) {

			try {
				object = this.entityManager.find(clase, id);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (object == null) {

				System.out.println("NO SE ENCONTRO UN " + clase.toString() + " CON ID" + id);

			} else {

				invoiceProcessor.initCronJobProcess(object, date, null);

			}

		} else if (id == 0 && date != null) {

			invoiceProcessor.initCronJobProcess(null, date, null);

		} else {
			System.out.println("NO se puede invocar CRON JOB con las parametros dados");
		}

	}

	public int getConceptId() {
		return conceptId;
	}

	public void setConceptId(int conceptId) {
		this.conceptId = conceptId;
	}

	public int getTermSheetId() {
		return termSheetId;
	}

	public void setTermSheetId(int termSheetId) {
		this.termSheetId = termSheetId;
	}

	/**
	 * Esta función recalcula el valor que deberia tener en new balance cada uno
	 * de los invoice_concept de tipo nota crédito, que esta en elsistema hasta
	 * este momento.
	 */
	@SuppressWarnings("unchecked")
	public void newBalance() {

		System.out.println("Inicia Proceso de asignación del new balance en los invoiceConcept de tipo nota crédito");
		// esta consulta trae los id de los invoice_concept que han sufrido
		// alguna modificación en su balance
		Query q1 = getEntityManager().createNativeQuery("SELECT DISTINCT(invoice_concept) from adjustments_to_balance order by invoice_concept");
		// se recorre cada uno de los id obtenidos
		for (Object id : q1.getResultList()) {

			System.out.println("Se trae los recaudo y notas credito del invoice_concept: " + (Integer) id);

			// se traen todas las transacciones que se han realizado sobre el
			// invoice_concept que se esta evaluando
			Query q2 = getEntityManager().createNativeQuery("SELECT * from adjustments_to_balance  where invoice_concept = ? order by date DESC");
			q2.setParameter(1, (Integer) id);

			List<Object[]> adjustments = q2.getResultList();
			System.out.println("se traen: " + adjustments.size() + " datos");
			InvoiceConcept invocon = null;

			// se recorren las transacciones, en busca de la primera nota
			// crédito.
			for (Object[] objects : adjustments) {
				System.out.println((Integer) objects[3] + " id: " + (Integer) objects[0]);
				if ((Integer) objects[3] == KINDOFTRANSACTIONCREDITNOTE) {
					invocon = getEntityManager().find(InvoiceConcept.class, (Integer) objects[4]);
					break;
				}

			}
			// si el invoice_concept es != null es por que ha sufrido una
			// notacrédito
			if (invocon != null) {

				double balanceTemporal = invocon.getBalance();
				double balanceActual = invocon.getBalance();
				Double newBalance = invocon.getNewBalance();
				System.out.println("newBalance: " + newBalance);
				CreditNoteHome home = new CreditNoteHome();
				BillingTools tools = new BillingTools(getEntityManager());

				// se recorre cada una de las transacciones realizadas sobre el
				// concepto, para devolver el balance a su valor antes de que se
				// realizara el movimiento.
				for (int i = 0; i < adjustments.size(); i++) {

					// si el new_balance es diferente de null quiere decir que
					// este invoice_concept sufrio una nota crédito
					// recientemente,
					// y su valor de new balance ya fue calculado correctamente
					if (newBalance == null) {

						if ((Integer) adjustments.get(i)[3] == KINDOFTRANSACTIONCREDITNOTE) {

							// se trae el invoice_concept de tipo nota crédito.
							InvoiceConcept creditNote = getEntityManager().find(InvoiceConcept.class, adjustments.get(i)[0]);
							creditNote.setNewBalance(home.totalConceptoDouble(invocon));
							// se almacena en una variable el valor del balance
							// incrementado del invoice_concept
							balanceTemporal = invocon.getBalance();
							// se settea el balance original antes de persistir,
							// para evitar cambios a este valor.
							invocon.setBalance(balanceActual);
							tools.persistObject(creditNote);

							// se settea el balance incrementado después de
							// persistir.
							invocon.setBalance(balanceTemporal);

							System.out.println("Se realiza modificación en el invoiceConcept: " + (Integer) adjustments.get(i)[0] + " Asignando un new_balance = " + creditNote.getNewBalance());

						}

					}

					System.out.println();
					// se suma al valor del balance atual del concepto el
					// balance del invoice_concept de tipo nota crédito
					// o el valor del recaudo, dependiendo del tipo de
					// transacción que se este evaluando.
					invocon.setBalance(invocon.getBalance() + (Double) adjustments.get(i)[1]);
				}
			} else
				System.out.println("Se aborta la evaluación de este invocon ya que este solo ha sido afectado por recaudos.");
		}
	}

	public boolean showButton() {
		Query q = getEntityManager().createQuery("FROM SystemConfiguration sc WHERE sc.name = '" + SystemConfiguration.BOTON_ACTUALIZAR_NEW_BALANCE + "'");
		systemConfiguration = (SystemConfiguration) q.getResultList().get(0);
		return systemConfiguration.getValue().equals("1");
	}

	public void executeInvoiceProcessorInterest() {
		InvoiceProcessor invoiceProcessor = new InvoiceProcessor(this.entityManager);
		invoiceProcessor.initCronJobProcess2(new Date());
	}
}
