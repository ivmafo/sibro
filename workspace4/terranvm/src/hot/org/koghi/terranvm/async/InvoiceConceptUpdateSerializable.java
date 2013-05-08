package org.koghi.terranvm.async;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.InvoiceConcept;

@Name("processorUpdate")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class InvoiceConceptUpdateSerializable {
	@In
	private EntityManager entityManager;
	BillingTools billing;

	public InvoiceConceptUpdateSerializable() {
	}

	public InvoiceConceptUpdateSerializable(EntityManager entityManager) {

		this.entityManager = entityManager;
	}

	@Asynchronous
	@Transactional
	public QuartzTriggerHandle createQuartzTimer(@Expiration Date when, @IntervalCron String cron) {
		long l1 = System.currentTimeMillis();
		this.process();
		long l2 = System.currentTimeMillis();
		log(Level.INFO, "TIME " + (l2 - l1));
		return null;
	}

	@SuppressWarnings("static-access")
	public void process() {
		log(Level.INFO, "Inicia la actualización del serializado");
		try {
			InvoiceConcept invoiceConcept;
			billing = new BillingTools(entityManager);
			String queryInvCon = "select id from invoice_concept where concept in (19107,27813,27725,19151,19185,27759,43091,43090)";
			Query q = this.entityManager.createNativeQuery(queryInvCon);
			@SuppressWarnings("unchecked")
			List<Integer> ids = q.getResultList();
			log(Level.INFO, "tamaño de la lista: " + q.getResultList().size());
			for (Integer integer : ids) {
				log(Level.INFO, "Se analizara el invoice_concept " + integer);
				try {
					invoiceConcept = entityManager.find(InvoiceConcept.class, integer);
					invoiceConcept.setConceptSerializable(billing.serializeConcept(invoiceConcept.getConcept()));
					billing.persistObject(invoiceConcept);
					log(Level.INFO, "Concepto actualizado correctamente.");
				} catch (Exception e) {
					e.printStackTrace();
					log(Level.INFO, "Se produjo un error en el invoice concept " + integer + " posible causa: " + e.getMessage());
				}

			}
			// mailSender.set("wamaya@koghi.com",
			// "La actualización del serializable fue realizada con exito.",
			// "Actualización de serializable", null, null);
		} catch (Exception e) {
			e.printStackTrace();

			// mailSender.set("wamaya@koghi.com",
			// "La actualización del serializable fallo, debido a: "+e.getMessage(),
			// "Actualización de serializable", null, null);
		}
		// (new Thread(mailSender)).start();

	}

	/**
	 * This function prints a message in log file.
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	@Transient
	private void log(Level level, String message) {
		StringBuilder init = new StringBuilder();
		init.append(" ");
		int minLong = InvoiceConceptUpdateSerializable.class.getSimpleName().length()+level.getName().toString().length();
		if(level == Level.WARNING){
			minLong --;
		}
		while ( minLong + init.length() < 40) {
			init.append("#");
		}
		init.append(" ");
		init.append(message.toString());
		Logger.getLogger(InvoiceConceptUpdateSerializable.class.getSimpleName()).log(level, init.toString());
	}

}
