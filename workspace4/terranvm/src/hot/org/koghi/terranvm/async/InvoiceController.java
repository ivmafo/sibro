package org.koghi.terranvm.async;

import java.io.Serializable;
import java.util.Calendar;

import javax.ejb.Timer;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.log.Log;

@Name("controller")
@AutoCreate
public class InvoiceController implements Serializable {

	private static final long serialVersionUID = -6332836501640042340L;

	private static final boolean USE_FREQUENCY = false;
	private static final boolean USE_CRON_EXPRESSION = true;

	private static final int FREQUENCY = 320; // in seconds (1 day = 86400)

	/*
	 * "0 0 12 * * ?" Fire at 12pm (noon) every day.
	 * 
	 * "0 15 10 ? * *" Fire at 10:15am every day.
	 * 
	 * "0 15 10 * * ?" Fire at 10:15am every day.
	 * 
	 * "0 15 10 * * ? *" Fire at 10:15am every day.
	 * 
	 * "0 15 10 * * ?" 2005 Fire at 10:15am every day during the year 2005.
	 * 
	 * "0 * 14 * * ?" Fire every minute starting at 2pm and ending at 2:59pm
	 * every day.
	 * 
	 * "0 0/5 14 * * ?" Fire every 5 minutes starting at 2pm and ending at
	 * 2:55pm, every day
	 */
	private static final String CRON_INTERVAL_1 = "0 0 1 * * ?";// CRON-JOB
																// NORMAL
	private static final String CRON_INTERVAL_2 = "0 0 3 * * ?";// CRON JOB
																	// INTERESES

	@In(create = true)
	InvoiceProcessor processor;

	@In
	InvoiceConceptUpdateSerializable processorUpdate;

	@Logger
	Log log;

	@SuppressWarnings("unused")
	private Timer timer;

	@SuppressWarnings("unused")
	private QuartzTriggerHandle quartzTriggerHandle1;

	@SuppressWarnings("unused")
	private QuartzTriggerHandle quartzTriggerHandle2;

	@SuppressWarnings("unused")
	private QuartzTriggerHandle quartzTriggerHandle3;

	public void scheduleTimer() {

		Calendar date = Calendar.getInstance();

		if (InvoiceController.USE_FREQUENCY) {
			timer = processor.createEjbTimer(date.getTime(), 1000l * InvoiceController.FREQUENCY);

		} else if (InvoiceController.USE_CRON_EXPRESSION) {

			quartzTriggerHandle1 = processor.createQuartzTimer(date.getTime(), InvoiceController.CRON_INTERVAL_1);
			quartzTriggerHandle2 = processor.createQuartzTimer2(date.getTime(), InvoiceController.CRON_INTERVAL_2);

		}
	}

}
