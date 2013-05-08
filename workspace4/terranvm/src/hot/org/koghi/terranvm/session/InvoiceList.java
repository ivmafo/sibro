package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("invoiceList")
public class InvoiceList extends EntityQuery<Invoice> {

	private static final String EJBQL = "select invoice from Invoice invoice";

	private static final String[] RESTRICTIONS = { "lower(invoice.observations) like lower(concat(#{invoiceList.invoice.observations},'%'))", "lower(invoice.number) like lower(concat(#{invoiceList.invoice.number},'%'))", "lower(invoice.addressBilled) like lower(concat(#{invoiceList.invoice.addressBilled},'%'))", "lower(invoice.addressBiller) like lower(concat(#{invoiceList.invoice.addressBiller},'%'))", "lower(invoice.billingResolution) like lower(concat(#{invoiceList.invoice.billingResolution},'%'))", "lower(invoice.cityBilled) like lower(concat(#{invoiceList.invoice.cityBilled},'%'))", "lower(invoice.cityBiller) like lower(concat(#{invoiceList.invoice.cityBiller},'%'))", "lower(invoice.idNumberBilled) like lower(concat(#{invoiceList.invoice.idNumberBilled},'%'))",
			"lower(invoice.idNumberBiller) like lower(concat(#{invoiceList.invoice.idNumberBiller},'%'))", "lower(invoice.nameBilled) like lower(concat(#{invoiceList.invoice.nameBilled},'%'))", "lower(invoice.nameBiller) like lower(concat(#{invoiceList.invoice.nameBiller},'%'))", "lower(invoice.phoneBiller) like lower(concat(#{invoiceList.invoice.phoneBiller},'%'))", "lower(invoice.phoneBilled) like lower(concat(#{invoiceList.invoice.phoneBilled},'%'))", };

	private Invoice invoice = new Invoice();

	public InvoiceList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Invoice getInvoice() {
		return invoice;
	}
}
