package org.koghi.terranvm.bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.ConceptRetentionRateAccount;
import org.koghi.terranvm.entity.ConsecutiveCreditNotes;
import org.koghi.terranvm.entity.ConsecutiveDailySiigo;
import org.koghi.terranvm.entity.CreditNote;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.ObjectOfContract;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.Recover;
import org.koghi.terranvm.entity.RecoverConcept;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RetentionRate;
import org.koghi.terranvm.entity.RetentionRateAccount;
import org.koghi.terranvm.entity.SystemConfiguration;
import org.koghi.terranvm.entity.TaxConfiguration;
import org.koghi.terranvm.session.ConceptHome;

public class SiigoFunctions {

	private Integer sequence = 0;
	private EntityManager entityManager;
	private Session session;
	private org.koghi.terranvm.entity.File file;
	private ConceptHome home = new ConceptHome();

	public SystemConfiguration systemConfiguration = new SystemConfiguration();
	public ConsecutiveDailySiigo consecutiveDailySiigo = new ConsecutiveDailySiigo();

	public SiigoFunctions(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public SiigoFunctions(Session session) {
		this.session = session;
	}

	private String getHeader() {

		StringBuilder header = new StringBuilder();
		header.append("TIPO MOVIMIENTO,COMPROBANTE MOVIMIENTO,NUMERO MOVIMIENTO,SECUENCIA,FECHA DOCUMENTO,ESTADO MOVIMIENTO,IDENTIFICACION TERCERO,SUCURSAL TERCERO,CODIGO CUENTA CONTABLE,CODIGO LINEA PRODUCTO,CODIGO GRUPO PRODUCTO,CODIGO PRODUCTO,CODIGO GRUPO ACTIVO FIJO,CODIGO ACTIVO FIJO,CODIGO CENTRO COSTO,CODIGO SUBCENTRO COSTO,CODIGO BODEGA,CODIGO UBICACION,CODIGO MONEDA,TASA CAMBIO,CODIGO VENDEDOR,CODIGO PAIS,CODIGO CIUDAD,CODIGO ZONA,CODIGO COBRADOR,CODIGO EQUIPO,CODIGO OPERARIO,NUMERO CHEQUE,DESCRIPCION MOVIMIENTO,DEBITO - CREDITO,VALOR,VALOR MONEDA EXTRANJERA,VALOR BASE RETENCION,CANTIDAD,TIPO COMPROBANTE CRUCE,COMPROBANTE CRUCE,NUMERO COMPROBANTE CRUCE,VENCIMIENTO COMPROBANTE CRUCE,FECHA VENCIMIENTO COMPROBANTE CRUCE,");
		header.append("CODIGO FORMA PAGO,CODIGO BANCO,TIPO PEDIDO CRUCE	,COMPROBANTE PEDIDO CRUCE,NUMERO PEDIDO CRUCE,");
		header.append("SECUENCIA PEDIDO CRUCE,TIPO FACTURA CRUCE,COMPROBANTE FACTURA CRUCE,NUMERO FACTURA CRUCE,SECUENCIA FACTURA CRUCE,");
		header.append("	COMPROBANTE PROVEEDOR,NUMERO COMPROBANTE PROVEEDOR,FECHA COMPROBANTE PROVEEDOR,INDICAR TIPO MOVIMIENTO,");
		header.append("	USA DATOS NOMINA,VARIABLE NOMINA,PERIODO NOMINA,CODIGO CONCEPTO NOMINA,VALOR IMPOCONSUMO,");
		header.append("VALOR IMPOCONSUMO MONEDA EXTRANJERA,CANTIDAD CONVERSION,CODIGO FACTOR CONVERSION,FACTOR CONVERSION USADO,");
		header.append("VALOR UNITARIO SIN IVA,VALOR UNITARIO SIN IVA EXTRANJERA,VALOR UNITARIO CON IVA,VALOR UNITARIO CON IVA EXTRANJERA,");
		header.append("COBRA RETENCION,PORCENTAJE RETENCION ITEM,VALOR RETENCION ITEM,VALOR RETENCION ITEM EXTRANJERA	,");
		header.append("COBRA IVA,PORCENTAJE IVA ITEM,VALOR IVA ITEM,VALOR IVA ITEM EXTRANJERA,FECHA OPTIMISTA,FECHA PESIMISTA,");
		header.append("UTILIZA AIU,BASE IVA AIU,BASE IVA AIU EXTRANJERA,NUMERO VECES DEPRECIAR,CODIGO CAJA MENOR,NUMERO PUNTOS CAJERO,");
		header.append("	CANTIDAD SEGUNDA UNIDAD,CANTIDAD ALTERNA SEGUNDA UNIDAD,METODO DEPRECIACION,VALOR IMPUESTO DEPORTE,VALOR IMPUESTO DEPORTE EXTRANJERA,");
		header.append("PORCENTAJE TOTAL DESCUENTO ITEM,TOTAL DESCUENTO ITEM,TOTAL DESCUENTO ITEM EXTRANJERA,PORCENTAJE TOTAL CARGO ITEM,");
		header.append("	TOTAL CARGO ITEM,TOTAL CARGO ITEM EXTRANJERA,DRAWBACK,SECUENCIA TRANSACCION,AUTORIZACION IMPRENTA,");
		header.append(" TIPO COMPROBANTE PROVEEDOR,CODIGO CREDITO TRIBUTARIO,UTILIZA IVA GRAVADO COA,FECHA DETRACCION,PROVEEDOR ");

		return header.toString();
	}

	/**
	 * Metodo que retorna un archivo de Interfaz Siigo, con el listado de
	 * facturas enviados
	 * 
	 * @param invoiceList
	 *            Listado de Invoice
	 * @return Link de descarga
	 */

	public String invoiceSiggo(List<Invoice> invoiceList, Project poject) {

		// ------------CREACION CARPETA DE ALMACENAMIENTO ARCHIVOS
		// SIIGO----------------------
		Query q = entityManager.createQuery("FROM SystemConfiguration sc WHERE sc.name = ?");
		q.setParameter(1, SystemConfiguration.Carpeta_SIIGO);
		q.setMaxResults(1);
		systemConfiguration = (SystemConfiguration) q.getSingleResult();

		String OrigenCarpeta = systemConfiguration.getValue() + "/" + poject.getId() + "_" + poject.getNameProject();
		File directorio = new File(OrigenCarpeta);
		directorio.mkdir();

		// --------------------------

		/**
		 * Se crea Variable de Fecha de calculo
		 */
		Calendar cal = Calendar.getInstance();
		/**
		 * Se crea Consulta consecutivo diario siigo
		 */
		Query q1 = entityManager.createQuery("FROM ConsecutiveDailySiigo cds WHERE cds.date = ? AND cds.project = ? AND cds.type = ? ");
		q1.setParameter(1, cal.getTime());
		q1.setParameter(2, poject.getId());
		q1.setParameter(3, "F");
		q1.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<ConsecutiveDailySiigo> consecutiveListSiigo = (List<ConsecutiveDailySiigo>) q1.getResultList();

		Integer Consecutive = 0;

		if (consecutiveListSiigo.size() > 0) {
			consecutiveDailySiigo = consecutiveListSiigo.get(0);
			consecutiveDailySiigo.setConsecutive(consecutiveDailySiigo.getConsecutive() + 1);
			Consecutive = consecutiveDailySiigo.getConsecutive();
			// Consecutive = consecutiveListSiigo.get(0).getConsecutive();
			entityManager.joinTransaction();
			entityManager.persist(consecutiveDailySiigo);
			entityManager.flush();
		} else {
			consecutiveDailySiigo.setProject(poject.getId());
			consecutiveDailySiigo.setDate(cal.getTime());
			consecutiveDailySiigo.setConsecutive(1);
			consecutiveDailySiigo.setType("F");
			entityManager.joinTransaction();
			entityManager.persist(consecutiveDailySiigo);
			entityManager.flush();
			Consecutive = consecutiveDailySiigo.getConsecutive();
		}

		// -------------------------------------------------

		/**
		 * Se Crea Ruta de descarga
		 */
		String fileRelativePath = OrigenCarpeta + "/" + cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH) + 1) + cal.get(Calendar.DAY_OF_MONTH) + "-Interface-" + Consecutive + "-F" + ".txt";
		/**
		 * Se crea Archivo en Servidor
		 */
		String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
		path = path.substring(0, path.lastIndexOf("/")) + "/";

		// File file = new File(path + fileRelativePath);
		File file = new File(fileRelativePath);
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
			output.write(this.getHeader());
			output.newLine();
			/* Se recorre las facturas */
			for (int i = 0; i < invoiceList.size(); i++) {
				/* Se obtiene una Facura de la Lista */
				Invoice inv = invoiceList.get(i);
				/* Si la factura es aprobado se procesa y se envia a Siigo */
				if (inv.isApproved()) {
					/* Se Recorre los Conceptos de la Factura */

					for (int j = 0; j < inv.getInvoiceConcepts().size(); j++) {
						if (inv.getInvoiceConcepts().get(j).getInvoiceConceptType() == InvoiceConcept.TYPE_NORMAL || inv.getInvoiceConcepts().get(j).getInvoiceConceptType() == InvoiceConcept.TYPE_RETROACTIVE || inv.getInvoiceConcepts().get(j).getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST) {
							ArrayList<String[]> cols = invoiceConcept(inv.getInvoiceConcepts().get(j), 1);
							if (inv.getInvoiceConcepts().size() - 1 == j)
								sequence = 0;
							/* Se recorre el resultado */
							Iterator<String[]> it = cols.iterator();

							while (it.hasNext()) {

								String[] obj = (String[]) it.next();
								try {
									log(Level.INFO, "Lineas  facturación - invoiceConcept " + inv.getInvoiceConcepts().get(j).getId() + ": " + Arrays.toString(obj));
									output.write(this.getLines(obj));
									output.newLine();
								} catch (Exception e) {
									log(Level.SEVERE, "Error generando lineas de facturación  " + e.getMessage());
								}
							}
						}
					}
				}
				/* Se Recorre los Conceptos de la Factura */
				if (inv.getOldInvoice() != null && !inv.getOldInvoice().equals("")) {
					Invoice reversed_invoice = inv.getOldInvoice();

					if (reversed_invoice.getInvoiceConcepts().isEmpty())
						sequence = 0;

					for (int j = 0; j < reversed_invoice.getInvoiceConcepts().size(); j++) {
						// sequence = 0;
						if (reversed_invoice.getInvoiceConcepts().get(j).getInvoiceConceptType() == InvoiceConcept.TYPE_CREDIT_NOTE) {
							ArrayList<String[]> cols = invoiceConceptCn(reversed_invoice.getInvoiceConcepts().get(j), 3, null);
							/* Se recorre el resultado */
							Iterator<String[]> it = cols.iterator();
							while (it.hasNext()) {
								String[] obj = (String[]) it.next();
								try {
									log(Level.INFO, "Lineas facturación (reversadas) - invoiceConcept " + reversed_invoice.getInvoiceConcepts().get(j).getId() + ": " + Arrays.toString(obj));
									output.write(this.getLines(obj));
									output.newLine();
								} catch (Exception e) {
									log(Level.SEVERE, "Error generando lineas de facturación (reversadas) " + e.getMessage());
								}
							}
						}
					}

				}
			}

			output.flush();
			output.close();

			this.file = FileTools.createSIIGOFileInstance(file);
			log(Level.INFO, "FILE SIIGO CREATED TJ2: " + fileRelativePath);
		} catch (IOException e) {
			log(Level.INFO, "Error Generando Archivo SIIGO: " + e);
			e.printStackTrace();
		}

		return fileRelativePath;
	}

	/**
	 * Retorna una linea pa ser ingresada a SIGGO.
	 * 
	 * @param line
	 * @return
	 */
	private String getLines(String line[]) {
		String lineSiigo = "";
		for (int i = 1; i <= 101; i++) {
			if (i == 1) {
				lineSiigo = line[i];
			} else {
				lineSiigo += "," + line[i];
			}

		}
		return lineSiigo;
	}

	/**
	 * Método que inicializa Un arreglo
	 * 
	 * @return
	 */
	private String[] initializeLineSiigo() {

		String line[] = new String[102];
		for (int i = 0; i < 102; i++) {
			line[i] = "0";
		}
		return line;
	}

	/*
	 * Método que Retorna si la retencion se puede cobrar, dependiendo si le
	 * facturado y facturador cumplen las condiciones establecidas.
	 * 
	 * @param retention retencion
	 * 
	 * @param billed facturado
	 * 
	 * @param biller facturador
	 * 
	 * @return
	 */
	private Boolean isValidRetention(RetentionRate retention, BusinessEntity billed, BusinessEntity biller) {

		// List<TaxConfiguration> taxconfiguration =
		// retention.getTaxConfigurations();
		Iterator<TaxConfiguration> taxconfigurationIt = retention.getTaxConfigurations().iterator();
		log(Level.INFO, "*************BILLED: " + billed.getTaxLiability() + "***BILLER:" + biller.getTaxLiability());
		while (taxconfigurationIt.hasNext()) {

			TaxConfiguration taxConf = (TaxConfiguration) taxconfigurationIt.next();
			/*
			 * Si en la retencion esta configurada la calidad tributara del
			 * facturador y le facturador
			 */
			if (billed.getTaxLiability().contains(taxConf.getTaxliabilitiesByBilled().getId()) && biller.getTaxLiability().contains(taxConf.getTaxliabilitiesByBiller().getId())) {

				/*
				 * Si el impuesto es reteICa se verifica que el facturador y el
				 * facturador sean de la misma ciudad
				 */
				if (retention.getId() == RetentionRate.RETENTION_RATE_RTEICA && biller.getCity() == billed.getCity()) {

					return true;
				} else {
					/*
					 * Si la condicion anterios no se cumple, se verifica que el
					 * impuesto no sea reteICa, en caso de ser Asi se retorna
					 * efectiva
					 */
					if (retention.getId() != RetentionRate.RETENTION_RATE_RTEICA && retention.isRetantion()) {

						return true;
					} else {
						return false;
					}
				}
			}

		}
		/*
		 * Se retorna False, La retencion no esta configurada En la Matriz!!(Se
		 * borro la información de la tabla)
		 */
		return false;

	}

	/**
	 * Métod que retorna una lista de retenciones asociadas a un invoice concept
	 * 
	 * @param invoiceConcept
	 * @return
	 */
	private List<ConceptRetentionRateAccount> getRetentionList(InvoiceConcept invoiceConcept) {
		/* Se recoreen las Retenciones del concepto */
		List<ConceptRetentionRateAccount> retentionList = invoiceConcept.getConcept().getConceptRetentionRateAccounts();
		List<ConceptRetentionRateAccount> aux = new ArrayList<ConceptRetentionRateAccount>();

		for (ConceptRetentionRateAccount retention : retentionList) {
			/*
			 * Se realiza validación del facturado y el facturador para
			 * verificar su Naturaleza jurídica y asi garantizar que puede
			 * utilizar los conceptos
			 */
			Boolean resultValidate = false;
			if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && retention.getRetentionRateAccount().getRetentionRate().getSubcategoryFrom() != null) {
				if (retention.getRetentionRateAccount().getRetentionRate().getSubcategoryFrom() != null && retention.getRetentionRateAccount().getRetentionRate().getSubcategoryFrom().getId() == RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS) {
					resultValidate = this.isValidRetention(retention.getRetentionRateAccount().getRetentionRate(), invoiceConcept.getInvoice().getBilled(), invoiceConcept.getInvoice().getBiller());
				}
			} else if (invoiceConcept.getInvoiceConceptType() != InvoiceConcept.TYPE_INTEREST && retention.getRetentionRateAccount().getRetentionRate().getSubcategoryFrom() == null) {
				resultValidate = this.isValidRetention(retention.getRetentionRateAccount().getRetentionRate(), invoiceConcept.getInvoice().getBilled(), invoiceConcept.getInvoice().getBiller());
			}
			log(Level.INFO, "Resultado de la Validacion: " + resultValidate);
			if (resultValidate) {
				aux.add(retention);
			}
		}

		return aux;
	}

	/**
	 * Retorna el valor de las retenciones asociadas a un concepto
	 * 
	 * @param invoiceConcept
	 *            InvoiceConcept que se le calcule el valor de las retenciones.
	 * @return
	 */
	public Double getRetentionValue(InvoiceConcept invoiceConcept) {

		List<ConceptRetentionRateAccount> retentionList = this.getRetentionList(invoiceConcept);
		Double value = 0.0;
		for (ConceptRetentionRateAccount retention : retentionList) {
			Double calcultedCost = 0.0;
			if (retention.getRetentionRateAccount().getRetentionRate().getId() == RetentionRate.RETENTION_RATE_RTEIVA) {
				calcultedCost = invoiceConcept.getCalculatedTax();
			} else {
				calcultedCost = invoiceConcept.getCalculatedCost();
			}
			Double ret = retention.getRetentionRateAccount().getValue().doubleValue();
			value = value + (calcultedCost * (ret / 100));
		}
		return value;
	}

	/**
	 * Método que Retorna arraylist con las retenciones asociadas a un invoice
	 * concept.
	 * 
	 * @param invoiceConcept
	 *            invoiceConcept
	 * @param conceptLine
	 *            copia de lineas de conceptos.
	 * @param nature
	 *            , entero q indica si es una operacion de Facturacion (1),
	 *            Recaudo(2), NotaCredito(3)
	 * @return
	 */
	private ArrayList<String[]> getLineRetentionConceptSiggo(InvoiceConcept invoiceConcept, String conceptLine[], int nature, RetentionRateAccount accountLine1) {

		ArrayList<String[]> lines = new ArrayList<String[]>();
		Double valorFactura = 0.0;

		valorFactura = getRetentionRateAccountNature(accountLine1, nature).startsWith(RetentionRateAccount.NATURALEZA_DEBITO + "") ? (valorFactura - invoiceConcept.getCalculatedCost()) : (valorFactura + invoiceConcept.getCalculatedCost());

		/* Se recoreen las Retenciones del concepto */
		List<ConceptRetentionRateAccount> retentionList = this.getRetentionList(invoiceConcept);

		Object[] sortList = retentionList.toArray();
		Arrays.sort(sortList);
		for (Object object : sortList) {
			ConceptRetentionRateAccount retention = (ConceptRetentionRateAccount) object;

			/* Se aumenta la sequencia */
			sequence = sequence + 1;
			String line[] = conceptLine.clone();
			// line[3] = invoiceConcept.getCreditNote().getConsecutive();
			line[4] = String.valueOf(sequence);
			/* Se modifica cuenta Contable */
			line[9] = retention.getRetentionRateAccount().getAccount();
			line[30] = this.getRetentionRateAccountNature(retention.getRetentionRateAccount(), nature);
			// line[30] =
			// retention.getRetentionRateAccount().getAccount().startsWith("1")
			// ? "D" : "C";
			/*
			 * Se verifica si la retención es ReteIva, en caso de ser asi el
			 * valor de la retenecion eso sobre el Iva
			 */
			Double calcultedCost = 0.0;
			if (retention.getRetentionRateAccount().getRetentionRate().getId() == RetentionRate.RETENTION_RATE_RTEIVA || retention.getRetentionRateAccount().getRetentionRate().getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA) {
				calcultedCost = invoiceConcept.getCalculatedTax();
				log(Level.INFO, "Valor Retenecion ReteIva: " + invoiceConcept.getCalculatedTax());
			} else {
				calcultedCost = invoiceConcept.getCalculatedCost();
			}
			Double ret = retention.getRetentionRateAccount().getValue().doubleValue();
			Double value = calcultedCost * (ret / 100);
			/* Se determina afectacion de valor de factura */
			valorFactura = getRetentionRateAccountNature(retention.getRetentionRateAccount(), nature).startsWith(RetentionRateAccount.NATURALEZA_DEBITO + "") ? (valorFactura - value) : (valorFactura + value);
			log(Level.INFO, "Valor Retenecion: " + value + " CC: " + invoiceConcept.getCalculatedTax() + " rt: " + ret);
			line[31] = Format_number.FormatToString(value);
			/* Campo 38, Vencimiento comprobante de cruce, secuencia */
			line[38] = line[4];
			/* Campo 41, Codigo Forma de Pago, No se tiene */
			line[40] = String.valueOf(retention.getRetentionRateAccount().getAccountsReceivableCode());
			lines.add(line);
		}

		/* Se ingresa el Iva a la Intefaz Siigo */
		if (invoiceConcept.getConcept().getTax() != null && invoiceConcept.getConcept().getTax().getValue() != 0) {

			RetentionRateAccount tax = invoiceConcept.getConcept().getTax();
			if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST) {
				tax = invoiceConcept.getConcept().getAccountingCDOD_cuentasIVA();
			}

			String iva[] = conceptLine.clone();
			sequence = sequence + 1;
			iva[4] = String.valueOf(sequence);
			iva[9] = tax.getAccount();
			iva[30] = this.getRetentionRateAccountNature(tax, nature);
			// iva[30] =
			// invoiceConcept.getConcept().getTax().getAccount().startsWith("1")
			// ? "D" : "C";
			Double ret = tax.getValue().doubleValue();
			Double value = invoiceConcept.getCalculatedTax();
			log(Level.INFO, "Valor Del TIMBRE: " + value + " CC: " + invoiceConcept.getCalculatedCost() + " rt: " + ret);
			valorFactura = getRetentionRateAccountNature(tax, nature).startsWith(RetentionRateAccount.NATURALEZA_DEBITO + "") ? (valorFactura - value) : (valorFactura + value);
			iva[31] = Format_number.FormatToString(value);
			/* Campo 38, campo ------, secuencia */
			iva[38] = iva[4];
			/* Campo 41, Codigo Forma de Pago, No se tiene */
			iva[40] = String.valueOf(tax.getAccountsReceivableCode());
			lines.add(iva);
		} else if (invoiceConcept.getConcept().getTax() == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES DE IVA NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO " + invoiceConcept.getConcept().getId());
		}

		// //////////////////////////
		/* Se ingresa el Timbre a la Intefaz Siigo */
		// if (invoiceConcept.getConcept().getStamptax() != null &&
		// invoiceConcept.getConcept().getStamptax().getValue() != 0) {
		if (invoiceConcept.getConcept().getStamptax() != null && invoiceConcept.getInvoiceConceptType() != InvoiceConcept.TYPE_INTEREST) {

			String timbre[] = conceptLine.clone();
			sequence = sequence + 1;
			timbre[4] = String.valueOf(sequence);
			timbre[9] = invoiceConcept.getConcept().getStamptax().getAccount();
			timbre[30] = this.getRetentionRateAccountNature(invoiceConcept.getConcept().getStamptax(), nature);
			// timbre[30] =
			// invoiceConcept.getConcept().getStamptax().getAccount().startsWith("1")
			// ? "D" : "C";
			Double ret = invoiceConcept.getConcept().getStamptax().getValue().doubleValue();
			// -----FALTA VALIDAR EL VALOR DEL IMPUESTO AL TIMBRE DENTRO DE LA
			// FACTURACION DE UN CONCEPTO, CUANDO SE HAGA ESTO SE DEBE
			// REEMPLAZAR EL VALRO EN LAS LINEAS DE ABAJO
			// calculateStamptax = invoiceConcept.getCalculatedCost() *
			// (concept.getStamptax().getValue()/100) ;
			Double value = invoiceConcept.getCalculatedCost() * (invoiceConcept.getConcept().getStamptax().getValue() / 100);
			log(Level.INFO, "Valor Del IVA: " + value + " CC: " + invoiceConcept.getCalculatedCost() + " rt: " + ret);
			valorFactura = getRetentionRateAccountNature(invoiceConcept.getConcept().getStamptax(), nature).startsWith(RetentionRateAccount.NATURALEZA_DEBITO + "") ? (valorFactura - value) : (valorFactura + value);
			timbre[31] = Format_number.FormatToString(value);
			/* Campo 38, campo ------, secuencia */
			timbre[38] = timbre[4];
			/* Campo 41, Codigo Forma de Pago, No se tiene */
			timbre[40] = String.valueOf(invoiceConcept.getConcept().getStamptax().getAccountsReceivableCode());
			lines.add(timbre);
		} else if (invoiceConcept.getConcept().getStamptax() == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES DE IMPUESTO AL TIMBRE NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO" + invoiceConcept.getConcept().getId());
		}

		// //////////////////////////

		/**
		 * 
		 * Se agrega Linea de valor de la factura
		 */

		RetentionRateAccount accountReceivable = invoiceConcept.getConcept().getAccountReceivable();
		if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			accountReceivable = invoiceConcept.getConcept().getAccountingCDOD_cuentasXCobrar();
		} else if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && !invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			accountReceivable = invoiceConcept.getConcept().getAccountingCDOD_cuentasDeudoraControlInteresVencida();
		}

		// Se valida que la cuenta exista
		if (accountReceivable == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO " + invoiceConcept.getConcept().getId());
		} else {

			String totalFactura[] = conceptLine.clone();
			sequence = sequence + 1;
			invoiceConcept.setSiigoBillingSequence(sequence);
			// if(invoiceConcept.getSiigoBillingSequenceToCreditNote() == null
			// || invoiceConcept.getSiigoBillingSequenceToCreditNote() == 0)
			// invoiceConcept.setSiigoBillingSequenceToCreditNote(sequence);
			totalFactura[4] = String.valueOf(sequence);

			if (entityManager != null)
				entityManager.refresh(invoiceConcept.getConcept());
			else
				session.refresh(invoiceConcept.getConcept());

			totalFactura[9] = accountReceivable.getAccount();
			totalFactura[30] = this.getRetentionRateAccountNature(accountReceivable, nature);
			// totalFactura[30] =
			// invoiceConcept.getConcept().getAccountReceivable().getAccount().startsWith("1")
			// ? "D" : "C";
			valorFactura = Math.abs(valorFactura);
			totalFactura[31] = Format_number.FormatToString(valorFactura);
			/* Campo 38, ---, secuencia */
			totalFactura[38] = totalFactura[4];
			totalFactura[40] = String.valueOf(accountReceivable.getAccountsReceivableCode());
			lines.add(totalFactura);
		}

		/* Se agrega el Valor del balance al invoice Concept */
		invoiceConcept.setBalance(valorFactura);
		this.persistObject(invoiceConcept);
		/**
		 * DEBUGGGG
		 */
		log(Level.INFO, "Valor de la retencion: " + this.getRetentionValue(invoiceConcept));
		return lines;

	}

	private void persistObject(InvoiceConcept invoice) {
		try {
			if (this.entityManager != null) {
				this.entityManager.joinTransaction();
				this.entityManager.persist(invoice);
				this.entityManager.flush();
			} else {
				this.session.persist(invoice);
				this.session.flush();
			}

		} catch (Exception e) {
			Logger.getLogger(BillingTools.class.getName()).log(Level.SEVERE, "SIGGO" + "ERROR, could not persist entity: " + invoice.getClass());
		}
	}

	/**
	 * Método que retorna lineas de Siggo por concepto
	 * 
	 * @param invoiceConcept
	 * @param nature
	 *            , entero q indica si es una operacion de Facturacion (1),
	 *            Recaudo(2), NotaCredito(3)
	 * @return
	 */
	private ArrayList<String[]> invoiceConcept(InvoiceConcept invoiceConcept, int nature) {

		/* Se extrae la cuenta a usar */
		RetentionRateAccount account = invoiceConcept.getConcept().getAccountingCreditAccounts();
		if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			account = invoiceConcept.getConcept().getAccountingCDOD_cuentasIngresos();
		} else if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && !invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			account = invoiceConcept.getConcept().getAccountingCDOD_cuentasDeudoraControlContario();
		}

		// Se valida que la cuenta exista
		if (account == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES PRINCIPALES NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO" + invoiceConcept.getConcept().getId());
			return new ArrayList<String[]>(0);
		}

		/* Se crea Format de Fecha */
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		/* Objeto donde se guarda Lienas del concepto */
		ArrayList<String[]> conceptLines = new ArrayList<String[]>();
		/* Se Crea Linea del InvoiceConcept */
		String conceptLine[] = this.initializeLineSiigo();

		/* Campo 1 de Siggo Tipo de Documento */
		/**
		 * Se obtiene Tipo de Documento No se TIENE en cuenta Notas Creditos,
		 * Solo aplica facturas reversadas
		 */
		switch (invoiceConcept.getInvoice().getDocumentType()) {
		case 0:
			conceptLine[1] = "F";
			break;/* Tipo de Documento Facura */
		case 1:
			conceptLine[1] = "L";
			break;/* Tipo de Documento Cuenta de Cobro */
		}
		/* Campo 2 Comprobante de Pago */
		/**
		 * Se verifica Tipo de documento. Queda Pendiente Resolucion de Cuentas
		 * de Cobros.
		 */
		switch (invoiceConcept.getInvoice().getDocumentType()) {
		case 0:
			conceptLine[2] = invoiceConcept.getInvoice().getProjectProperty().getBillingResolution().getSiigoCode();
			break;/* Facuras */
		case 1:
			conceptLine[2] = invoiceConcept.getInvoice().getProjectProperty().getAcountsBilling().getSiigoCode();
			break;/* Cuentas de Cobros(Temp, no esta funcionando) */
		}
		/* Campo 3 Consecutivo de Facturacion */
		/**/
		// conceptLine[3] =
		// this.formatBillNumber(invoiceConcept.getInvoice().getNumber());
		conceptLine[3] = invoiceConcept.getInvoice().getNumber();
		/* Campo 4 Secuencia (Número de Conceptos asociados a la fcaturacion) */
		sequence++;
		conceptLine[4] = String.valueOf(sequence);
		if (invoiceConcept.getSiigoBillingSequenceToCreditNote() == null || invoiceConcept.getSiigoBillingSequenceToCreditNote() == 0)
			invoiceConcept.setSiigoBillingSequenceToCreditNote(sequence);
		/* Campo 5 Fecha del documento */
		conceptLine[5] = formater.format(invoiceConcept.getInvoice().getExpeditionDate());
		/* Campo 6 Estado del Movimiento HARD CODE 4 */
		conceptLine[6] = "4";
		/* Campo 7 Identificacion del Tercero */
		conceptLine[7] = invoiceConcept.getInvoice().getIdNumberBilled();
		/*
		 * Campo 10 Código Cuenta Contable del Concepto (Deberia estar guardado
		 * en el invoice??) es la Cuenta Credito
		 */
		conceptLine[9] = account.getAccount();
		/*
		 * Campo 15 Centro de Costo (Si la HT es de Servicio toma el de la
		 * misma, si no toma el de la Unidad Arrendable
		 */
		if (invoiceConcept.getInvoice().getProjectProperty().getObjectOfContract().getId() == 3) // Hoja
																									// de
																									// termino
																									// de
																									// tipo
																									// Servivio
			conceptLine[15] = this.getInvoiceConceptCostCenter(invoiceConcept);
		else if (invoiceConcept.getInvoice().getProjectProperty().getObjectOfContract() == null)
			conceptLine[15] = "";
		else
			conceptLine[15] = this.getInvoiceConceptCostCenter(invoiceConcept);

		// conceptLine[15] =
		// invoiceConcept.getInvoice().getProjectProperty().getProject().getCostCenterProject();
		/* Campo 19 Código de Moneda, HARD CODE */
		conceptLine[19] = "1";
		/* Campo 22 Código del Pais, HARD CODE */
		conceptLine[21] = "1";
		/* Campo 23 Código de Ciudad del facturador */
		// conceptLine[22] =
		// (invoiceConcept.getInvoice().getBiller().getCity().getSiigoCode() ==
		// null ? "0" :
		// invoiceConcept.getInvoice().getBiller().getCity().getSiigoCode());
		/*
		 * Campo 23 Código de Ciudad, No es Hard Code, debe tener un codigo de
		 * ciudad donde se radica la factura(Faltante)
		 */
		conceptLine[22] = "1";

		/* Campo 30, Descripcion del concepto a facturar */
		conceptLine[25] = "1";

		/*
		 * Campo 30, Nombre del concepto tal y como aparece en las factuas o
		 * cuenta de cobro
		 */
		// conceptLine[29] = invoiceConcept.getConcept().getPrintDescription();

		conceptLine[29] = home.nameInvoiceConcept(invoiceConcept);

		int ooc = invoiceConcept.getInvoice().getProjectProperty().getObjectOfContract().getId();
		if (ooc == ObjectOfContract.OBJECT_REALPROPERTY || ooc == ObjectOfContract.OBJECT_RENTABLEUNIT) {
			RentableUnit idRentableUnit;
			if (entityManager != null) {
				idRentableUnit = entityManager.find(RentableUnit.class, invoiceConcept.getRentableUnitId());
			} else {
				idRentableUnit = (RentableUnit) session.get(RentableUnit.class, invoiceConcept.getRentableUnitId());

			}
			if (idRentableUnit != null) {

				conceptLine[29] = home.nameInvoiceConcept(invoiceConcept) + "-" + idRentableUnit.getName();
			}
		}

		/* Campo 31, Especificación si el moviiento es credito o debito */
		conceptLine[30] = this.getRetentionRateAccountNature(account, nature);
		// conceptLine[30] =
		// (invoiceConcept.getConcept().getAccountingCreditAccounts().getAccount().substring(0).equals("1")
		// ? "D" : "C");
		/* Campo 32, Valor del Concepto */
		conceptLine[31] = Format_number.FormatToString(invoiceConcept.getCalculatedCost());
		/* Campo 36 Comprobante Cruce */
		/* Si es factura o cuenta de cobro */
		if (invoiceConcept.getInvoice().getDocumentType() < 2) {
			conceptLine[35] = conceptLine[1];
			conceptLine[36] = conceptLine[2];
			conceptLine[37] = conceptLine[3];
			conceptLine[38] = conceptLine[4];
		}

		/* Campo 40, Fecha de vencimiento comprobante de la factura */
		conceptLine[39] = formater.format(invoiceConcept.getInvoice().getExpirationDate());
		/* Campo 41, Codigo Forma de Pago, No se tiene */

		conceptLine[40] = String.valueOf(account.getAccountsReceivableCode());
		/* Campo 53, Fecha creación interfaz siigo */
		conceptLine[52] = conceptLine[5];
		/* Campo 77 Fecha Optimista */
		conceptLine[75] = conceptLine[5];
		/* Campo 78 Fecha Pesimista */
		conceptLine[76] = conceptLine[5];

		/* Se termina de LLenar Concepto, Se guarda informacion de la retenecion */
		conceptLines.add(conceptLine);
		/* Se itera ArrayList de Retenciones */
		/* Se itera ArrayList de Retenciones */
		ArrayList<String[]> retention = this.getLineRetentionConceptSiggo(invoiceConcept, conceptLine, nature, account);
		conceptLines.addAll(retention);

		return conceptLines;
	}

	/**
	 * Método que retorna lineas de Siggo por concepto
	 * 
	 * @param invoiceConcept
	 * @param nature
	 *            , entero q indica si es una operacion de Facturacion (1),
	 *            Recaudo(2), NotaCredito(3)
	 * @return
	 */
	private ArrayList<String[]> invoiceConceptCn(InvoiceConcept invoiceConcept, int nature, CreditNote creditNote) {

		/* Se extrae la cuenta a usar */
		RetentionRateAccount account = invoiceConcept.getConcept().getAccountingCreditAccounts();
		if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			account = invoiceConcept.getConcept().getAccountingCDOD_cuentasIngresos();
		} else if (invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && !invoiceConcept.getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			account = invoiceConcept.getConcept().getAccountingCDOD_cuentasDeudoraControlContario();
		} else if (invoiceConcept.getInvoiceConceptType() != InvoiceConcept.TYPE_INTEREST && invoiceConcept.getDiscount() != null && invoiceConcept.getDiscount() == true)
			account = invoiceConcept.getConcept().getAccountingAccountsEarlyPayment();

		// Se valida que la cuenta exista
		if (account == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO (RetentionRate 8)" + invoiceConcept.getConcept().getId());
			return new ArrayList<String[]>(0);
		}

		/* Se crea Format de Fecha */
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		/* Objeto donde se guarda Lienas del concepto */
		ArrayList<String[]> conceptLines = new ArrayList<String[]>();
		/* Se Crea Linea del InvoiceConcept */
		String conceptLine[] = this.initializeLineSiigo();

		/* Campo 1 de Siggo Tipo de Documento */
		conceptLine[1] = "C";

		ConsecutiveCreditNotes ccn = invoiceConcept.getInvoice().getProjectProperty().getCreditNotes();

		refreshEntity(invoiceConcept.getInvoice().getProjectProperty().getCreditNotes());

		/* Campo 2 Comprobante de Pago (No esta) */
		conceptLine[2] = ccn.getSiigoCode();

		/* Campo 3 Consecutivo de Notas Creditos (No esta) */
		/**/
		conceptLine[3] = invoiceConcept.getCreditNote().getConsecutive();
		/* Campo 4 Secuencia (Número de Conceptos asociados a la fcaturacion) */
		sequence++;
		conceptLine[4] = String.valueOf(sequence);
		/* Campo 5 Fecha del documento de la factura */
		if (creditNote != null)
			conceptLine[5] = formater.format(creditNote.getCreditNoteDate());
		else
			conceptLine[5] = formater.format(new Date());
		/* Campo 6 Estado del Movimiento HARD CODE 4 */
		conceptLine[6] = "4";
		/* Campo 7 Identificacion del Tercero */
		conceptLine[7] = invoiceConcept.getInvoice().getIdNumberBilled();
		/*
		 * Campo 10 Código Cuenta Contable del Concepto (Deberia estar guardado
		 * en el invoice??) es la Cuenta Credito
		 */
		/*---Definicion de la cuenta cuando el InvoiceConcept de tipo nota credito es por descuento o por nota credito---*/
		conceptLine[9] = account.getAccount();

		/*
		 * Campo 15 Centro de Costo (Si la HT es de Servicio toma el de la
		 * misma, si no toma el de la Unidad Arrendable
		 */

		if (invoiceConcept.getInvoice().getProjectProperty().getObjectOfContract() == null)
			conceptLine[15] = "";
		else
			conceptLine[15] = this.getInvoiceConceptCostCenter(invoiceConcept);

		// conceptLine[15] =
		// invoiceConcept.getInvoice().getProjectProperty().getProject().getCostCenterProject();
		/* Campo 19 Código de Moneda, HARD CODE */
		conceptLine[19] = "1";
		/* Campo 21 Código del vendedor , HARD CODE */
		conceptLine[21] = "1";
		/* Campo 22 Código del Pais, HARD CODE */
		conceptLine[22] = "1";
		/* Campo 23 Código de Ciudad del facturador */
		refreshEntity(invoiceConcept.getInvoice().getBiller());
		refreshEntity(invoiceConcept.getInvoice().getBiller().getCity());

		conceptLine[23] = (invoiceConcept.getInvoice().getBiller().getCity().getSiigoCode() == null ? "0" : invoiceConcept.getInvoice().getBiller().getCity().getSiigoCode());
		/*
		 * Campo 23 Código de Ciudad, No es Hard Code, debe tener un codigo de
		 * ciudad donde se radica la factura(Faltante)
		 */
		// conceptLine[22] = "1";

		/* Campo 25 Código del Cobrador, HARD CODE */
		conceptLine[25] = "1";

		/* Campo 30, Descripcion del concepto a facturar */
		// conceptLine[29] = invoiceConcept.getConcept().getPrintDescription();
		if (invoiceConcept.getInvoice().getProjectProperty().getObjectOfContract().getId() == ObjectOfContract.OBJECT_SERVICE) {

			conceptLine[29] = home.nameInvoiceConcept(invoiceConcept.getInvoiceConceptParent());
		} else {
			RentableUnit idRentableUnit;
			if (entityManager != null) {
				idRentableUnit = entityManager.find(RentableUnit.class, invoiceConcept.getRentableUnitId());
			} else {
				idRentableUnit = (RentableUnit) session.get(RentableUnit.class, invoiceConcept.getRentableUnitId());

			}
			String name = idRentableUnit == null?"":idRentableUnit.getName();
			conceptLine[29] = home.nameInvoiceConcept(invoiceConcept.getInvoiceConceptParent()) + "-" + name;
		}

		/* Campo 31, Especificación si el moviiento es credito o debito *//*---Definicion de la cuenta cuando el InvoiceConcept de tipo nota credito es por descuento o por nota credito---*/
		conceptLine[30] = this.getRetentionRateAccountNature(account, nature);
		// conceptLine[30] =
		// (invoiceConcept.getConcept().getAccountingAccountsEarlyPayment().getAccount().substring(0).equals("1")
		// ? "C" : "D");

		/* Campo 32, Valor del Concepto */
		conceptLine[31] = Format_number.FormatToString(invoiceConcept.getCalculatedCost());

		/* Campo 36 Comprobante Cruce */
		/* Si es factura o cuenta de cobro */
		if (invoiceConcept.getInvoice().getDocumentType() < 2) {
			switch (invoiceConcept.getInvoice().getDocumentType()) {
			case 0:
				conceptLine[35] = "F";
				break;/* Tipo de Documento Facura */
			case 1:
				conceptLine[35] = "L";
				break;/* Tipo de Documento Cuenta de Cobro */
			}
			/* Campo 2 Comprobante de Pago */
			/**
			 * Se verifica Tipo de documento. Queda Pendiente Resolucion de
			 * Cuentas de Cobros.
			 */
			switch (invoiceConcept.getInvoice().getDocumentType()) {
			case 0:
				if (this.entityManager != null)
					this.entityManager.refresh(invoiceConcept.getInvoice().getProjectProperty().getBillingResolution());
				else if (this.session != null)
					this.session.refresh(invoiceConcept.getInvoice().getProjectProperty().getBillingResolution());

				conceptLine[36] = invoiceConcept.getInvoice().getProjectProperty().getBillingResolution().getSiigoCode();
				break;/* Facuras */
			case 1:
				conceptLine[36] = invoiceConcept.getInvoice().getProjectProperty().getAcountsBilling().getSiigoCode();
				break;/* Cuentas de Cobros(Temp, no esta funcionando) */
			}

			conceptLine[37] = invoiceConcept.getInvoice().getNumber();
			if (invoiceConcept.getSiigoBillingSequenceToCreditNote() == null || invoiceConcept.getSiigoBillingSequenceToCreditNote() == 0)
				invoiceConcept.setSiigoBillingSequenceToCreditNote(sequence);

			conceptLine[38] = "" + invoiceConcept.getSiigoBillingSequence();

		}
		/* Campo 40, Fecha de vencimiento comprobante de la factura */
		conceptLine[39] = formater.format(invoiceConcept.getInvoice().getExpeditionDate());
		/* Campo 41, Codigo Forma de Pago, No se tiene */

		conceptLine[40] = String.valueOf(account.getAccountsReceivableCode());
		// conceptLine[41] = (invoiceConcept.getConcept().getPaymentForm() ==
		// null) ? "0" :
		// String.valueOf(invoiceConcept.getConcept().getPaymentForm().getId());

		/* Campo 53, Misma Fecha de la creacion de la factura */
		conceptLine[52] = conceptLine[5];
		/* Campo 77 Fecha Optimista */
		conceptLine[75] = conceptLine[5];
		/* Campo 78 Fecha Pesimista */
		conceptLine[76] = conceptLine[5];

		/* Liena 102 Proveedor */
		// conceptLine[101] = "0";
		/* Se termina de LLenar Concepto, Se guarda informacion de la retenecion */
		conceptLines.add(conceptLine);
		/* Se itera ArrayList de Retenciones */
		/* Se itera ArrayList de Retenciones */
		refreshEntity(invoiceConcept.getConcept());
		ArrayList<String[]> retention = this.getLineRetentionConceptSiggo(invoiceConcept, conceptLine, nature, account);
		conceptLines.addAll(retention);

		return conceptLines;
	}

	private void refreshEntity(Object entity) {
		if (this.entityManager != null)
			this.entityManager.refresh(entity);
		else
			this.session.refresh(entity);
	}

	/**
	 * Metodo que retorna un archivo de Interfaz Siigo, con el listado de
	 * recaudos enviados
	 * 
	 * @param recoverList
	 *            Listado de recaudos
	 * @return Link de descarga
	 */
	public String recoverSiggo(List<Recover> recoverList, Project project) {
		// ------------CREACION CARPETA DE ALMACENAMIENTO ARCHIVOS
		// SIIGO----------------------
		Query q = entityManager.createQuery("FROM SystemConfiguration sc WHERE sc.name = ?");
		q.setParameter(1, SystemConfiguration.Carpeta_SIIGO);
		q.setMaxResults(1);
		systemConfiguration = (SystemConfiguration) q.getSingleResult();

		String OrigenCarpeta = systemConfiguration.getValue() + "/" + project.getId() + "_" + project.getNameProject();
		File directorio = new File(OrigenCarpeta);
		directorio.mkdir();

		// --------------------------

		/**
		 * Se crea Variable de Fecha de calculo
		 */
		Calendar cal = Calendar.getInstance();
		/**
		 * Se crea Consulta consecutivo diario siigo
		 */
		Query q1 = entityManager.createQuery("FROM ConsecutiveDailySiigo cds WHERE cds.date =? AND cds.project = ? AND cds.type = ? ");
		q1.setParameter(1, cal.getTime());
		q1.setParameter(2, project.getId());
		q1.setParameter(3, "R");
		q1.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<ConsecutiveDailySiigo> consecutiveListSiigo = (List<ConsecutiveDailySiigo>) q1.getResultList();

		Integer Consecutive = 0;

		if (consecutiveListSiigo.size() > 0) {
			consecutiveDailySiigo = consecutiveListSiigo.get(0);
			consecutiveDailySiigo.setConsecutive(consecutiveDailySiigo.getConsecutive() + 1);
			Consecutive = consecutiveDailySiigo.getConsecutive();
			entityManager.joinTransaction();
			entityManager.persist(consecutiveDailySiigo);
			entityManager.flush();
		} else {
			consecutiveDailySiigo.setProject(project.getId());
			consecutiveDailySiigo.setDate(cal.getTime());
			consecutiveDailySiigo.setConsecutive(1);
			consecutiveDailySiigo.setType("R");
			entityManager.joinTransaction();
			entityManager.persist(consecutiveDailySiigo);
			entityManager.flush();
			Consecutive = consecutiveDailySiigo.getConsecutive();
		}
		// -------------------------------------------------

		/**
		 * Se Crea Ruta de descarga
		 */
		// String fileRelativePath = "tmp/siigo/pre-recovers(" + (project !=
		// null ? project.getId() : -1) + ")_" + cal.get(Calendar.YEAR) + "-" +
		// cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) +
		// ".txt";
		String fileRelativePath = OrigenCarpeta + "/" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DAY_OF_MONTH) + "-Interface-" + Consecutive + "-R" + ".txt";
		/**
		 * Se crea Archivo en Servidor
		 */
		String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
		path = path.substring(0, path.lastIndexOf("/")) + "/";
		// File file = new File(path + fileRelativePath);
		File file = new File(fileRelativePath);
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
			output.write(this.getHeader());
			output.newLine();
			/* Se recorre las facturas */
			for (int i = 0; i < recoverList.size(); i++) {
				/* Se obtiene una Facura de la Lista */
				Recover recover = recoverList.get(i);

				sequence = 0;
				for (RecoverConcept recoverConcept : recover.getRecoverConcepts()) {
					log(Level.INFO, "getLinesRecover para recover " + recoverConcept.getId() + ", invoiceConcept " + recoverConcept.getInvoiceConcept().getId());
					ArrayList<String[]> cols = getLinesRecover(recoverConcept);

					/* Se recorre el resultado */
					Iterator<String[]> it = cols.iterator();
					while (it.hasNext()) {

						String[] obj = (String[]) it.next();
						try {
							log(Level.INFO, "Lines recaudo InvoiceConcept " + recoverConcept.getInvoiceConcept().getId() + ": " + Arrays.toString(obj));
							output.write(this.getLines(obj));
							output.newLine();
						} catch (Exception e) {
							log(Level.SEVERE, "Error obteniendo lineas de recaudo " + e.getMessage());
						}
					}

				}
			}
			output.flush();
			output.close();

			this.file = FileTools.createSIIGOFileInstance(file);

			log(Level.INFO, "FILE SIIGO CREATED JL: " + path + fileRelativePath);
		} catch (IOException e) {
			log(Level.INFO, "Error Generando Archivo SIIGO: " + e);
			e.printStackTrace();
		}

		return fileRelativePath;
	}

	/**
	 * Metodo que retorna un archivo de Interfaz Siigo, con el listado de
	 * invoiceConpcets por notas credito enviadas
	 * 
	 * @param creditNote
	 *            Nota credito con la lista de invoiceconcept tipo nota credito
	 * @return Link de descarga
	 */
	public String creditNoteSiggo(CreditNote creditNote, Project project) {
		// ------------CREACION CARPETA DE ALMACENAMIENTO ARCHIVOS
		// SIIGO----------------------
		if (entityManager == null) {
			org.hibernate.Query q = session.createQuery("FROM SystemConfiguration sc WHERE sc.name = '" + SystemConfiguration.Carpeta_SIIGO + "'");
			// q1.setParameter(1, SystemConfiguration.Carpeta_SIIGO);
			q.setMaxResults(1);
			systemConfiguration = (SystemConfiguration) q.uniqueResult();
		} else {
			Query q = this.entityManager.createQuery("FROM SystemConfiguration sc WHERE sc.name = ?");
			q.setParameter(1, SystemConfiguration.Carpeta_SIIGO);
			q.setMaxResults(1);
			systemConfiguration = (SystemConfiguration) q.getSingleResult();
		}

		String OrigenCarpeta = systemConfiguration.getValue() + "/" + project.getId() + "_" + project.getNameProject();
		File directorio = new File(OrigenCarpeta);
		directorio.mkdir();

		// --------------------------
		/**
		 * Se crea Variable de Fecha de calculo
		 */
		Calendar cal = Calendar.getInstance();
		cal.setTime(creditNote.getCreditNoteDate());
		/**
		 * Se crea Consulta consecutivo diario siigo
		 */
		org.hibernate.Query q1 = session.createQuery("FROM ConsecutiveDailySiigo cds WHERE cds.date = '" + cal.getTime() + "' AND cds.project = " + project.getId() + " AND cds.type = 'C'");
		// org.hibernate.Query q1 =
		// session.createQuery("FROM ConsecutiveDailySiigo cds WHERE cds.date =? AND cds.project = ? AND cds.type = ? ");
		// q1.setParameter(1, cal.getTime());
		// q1.setParameter(2, project.getId());
		// q1.setParameter(3, "C");
		q1.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<ConsecutiveDailySiigo> consecutiveListSiigo = (List<ConsecutiveDailySiigo>) q1.list();

		Integer Consecutive = 0;

		if (consecutiveListSiigo.size() > 0) {
			consecutiveDailySiigo = consecutiveListSiigo.get(0);
			consecutiveDailySiigo.setConsecutive(consecutiveDailySiigo.getConsecutive() + 1);
			Consecutive = consecutiveDailySiigo.getConsecutive();
			session.persist(consecutiveDailySiigo);
			session.flush();
		} else {
			consecutiveDailySiigo.setProject(project.getId());
			consecutiveDailySiigo.setDate(cal.getTime());
			consecutiveDailySiigo.setConsecutive(1);
			consecutiveDailySiigo.setType("C");
			session.persist(consecutiveDailySiigo);
			session.flush();
			Consecutive = consecutiveDailySiigo.getConsecutive();
		}

		// -------------------------------------------------

		/**
		 * Se Crea Ruta de descarga
		 */
		// String fileRelativePath = "tmp/siigo/pre-recovers(" + (project !=
		// null ? project.getId() : -1) + ")_" + cal.get(Calendar.YEAR) + "-" +
		// cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) +
		// ".txt";
		String fileRelativePath = OrigenCarpeta + "/" + cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DAY_OF_MONTH) + "-Interface-" + Consecutive + "-C" + ".txt";
		/**
		 * Se crea Archivo en Servidor
		 */
		String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
		path = path.substring(0, path.lastIndexOf("/")) + "/";
		// File file = new File(path + fileRelativePath);
		File file = new File(fileRelativePath);
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
			output.write(this.getHeader());
			output.newLine();
			/* Se recorre los InvoiceConcept */
			sequence = 0;
			for (InvoiceConcept invoiceConcept : creditNote.getInvoiceConcepts()) {
				ArrayList<String[]> cols = invoiceConceptCn(invoiceConcept, 3, creditNote);

				/* Se recorre el resultado */
				Iterator<String[]> it = cols.iterator();
				int contador = 0;
				while (it.hasNext()) {

					String[] obj = (String[]) it.next();
					try {
						log(Level.INFO, "Lines creditNote - invoiceConcept " + invoiceConcept.getId() + ": " + Arrays.toString(obj));
						int aux = invoiceConcept.getInvoiceConceptParent().getSiigoBillingSequenceToCreditNote() == null ? 1 + contador : invoiceConcept.getInvoiceConceptParent().getSiigoBillingSequenceToCreditNote() + contador;
						obj[38] = "" + aux;
						output.write(this.getLines(obj));
						output.newLine();
						contador++;

					} catch (Exception e) {
						log(Level.SEVERE, "Error generando lineas para creditNote - invoiceConcept " + invoiceConcept.getId());
					}
				}

			}

			output.flush();
			output.close();

		} catch (IOException e) {
			log(Level.INFO, "Error Generando Archivo SIIGO: " + e);
			e.printStackTrace();
		}

		return fileRelativePath;
	}

	private ArrayList<String[]> getLinesRecover(RecoverConcept recoverConcept) {
		/* Objeto donde se guarda Lienas del concepto */
		ArrayList<String[]> conceptLines = new ArrayList<String[]>();

		String[] firstLine = getFirstRecoverLine(recoverConcept, 2);
		// Adición de la primera línea del recaudo
		conceptLines.add(firstLine);
		// Adición de la segunda línea del recaudo
		conceptLines.add(getSecondRecoverLine(recoverConcept, 2));

		if (recoverConcept.getInvoiceConcept().getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && !recoverConcept.getInvoiceConcept().getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			conceptLines.add(getThirdRecoverLine(firstLine, recoverConcept, 2));
			conceptLines.add(getFourthRecoverLine(firstLine, recoverConcept, 2));
		}

		return conceptLines;
	}

	/**
	 * Método que devuelva el primer tipo de linea para un recaudo de un
	 * concepto teniendo el rocverConcept pasado por parametro
	 * 
	 * @param recoverConcept
	 * @param nature
	 *            , entero q indica si es una operacion de Facturacion (1),
	 *            Recaudo(2), NotaCredito(3)
	 * @return
	 */
	private String[] getFirstRecoverLine(RecoverConcept recoverConcept, int nature) {
		/* Se extrae la cuenta a usar */
		RetentionRateAccount account = recoverConcept.getInvoiceConcept().getConcept().getAccountReceivable();
		if (recoverConcept.getInvoiceConcept().getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && !recoverConcept.getInvoiceConcept().getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			account = recoverConcept.getInvoiceConcept().getConcept().getAccountingCDOD_cuentasOtrosIngresos();
		} else if (recoverConcept.getInvoiceConcept().getPenaltyDate() != null) {
			account = recoverConcept.getInvoiceConcept().getConcept().getAccountingPenaltyPortafolio();
		} else if (recoverConcept.getInvoiceConcept().getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST && recoverConcept.getInvoiceConcept().getConcept().getProjectProperty().getProject().isMandatoryInterest()) {
			account = recoverConcept.getInvoiceConcept().getConcept().getAccountingCDOD_cuentasXCobrar();
		}

		// Se valida que la cuenta exista
		if (account == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO (RetentionRate 6)" + recoverConcept.getInvoiceConcept().getConcept().getId());
			return new String[0];
		}

		/* Se crea Format de Fecha */
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

		/* Se Crea Linea del Recover */
		String recoverLine[] = this.initializeLineSiigo();
		/* Campo 1 de Siggo Tipo de Documento */

		/**
		 * Se especifica el tipo de documento del recaudo
		 */
		recoverLine[1] = "R";

		/* Campo 2 Comprobante de Pago */
		/**
		 * Se verifica Tipo de documento. Queda Pendiente el consecutivo de
		 * recauddo
		 */
		recoverLine[2] = recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getConsecutiveCollectionAccount().getSiigoCode();

		/* Campo 3 Consecutivo de recaudo */
		recoverLine[3] = recoverConcept.getRecover().getMinCollectionAccounts() + "";
		/* Campo 4 Secuencia (Número de recaudos asociados a la fcaturacion) */
		sequence++;
		recoverLine[4] = String.valueOf(sequence);

		/* Campo 5 Fecha del documento de la factura */
		recoverLine[5] = formater.format(recoverConcept.getRecover().getDate());
		/* Campo 6 Estado del Movimiento HARD CODE 4 */
		recoverLine[6] = "4";
		/* Campo 7 Identificacion del Tercero */
		recoverLine[7] = recoverConcept.getInvoiceConcept().getInvoice().getIdNumberBilled();
		/*
		 * Campo 9 Código Cuenta Contable del Concepto (Deberia estar guardado
		 * en el invoice??) es la Cuenta Credito
		 */
		recoverLine[9] = account.getAccount();

		/* Campo 15 Centro de Costo (Deberia estar guardado en el invoice??) */
		if (recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getObjectOfContract().getId() == 3) // Hoja
																														// de
																														// termino
																														// de
																														// tipo
																														// Servivio
			recoverLine[15] = this.getInvoiceConceptCostCenter(recoverConcept.getInvoiceConcept());
		else if (recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getObjectOfContract() == null)
			recoverLine[15] = "";
		else
			recoverLine[15] = this.getInvoiceConceptCostCenter(recoverConcept.getInvoiceConcept());

		// recoverLine[15] =
		// recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getProject().getCostCenterProject();
		/* Campo 19 Código de Moneda, HARD CODE */
		recoverLine[19] = "1";
		/* Campo 21 Código del Vendedor, HARD CODE */
		recoverLine[21] = "1";
		/* Campo 22 Código del Pais, HARD CODE */
		recoverLine[22] = "1";
		/* Campo 23 Código de Ciudad del facturador */
		recoverLine[23] = (recoverConcept.getInvoiceConcept().getInvoice().getBiller().getCity().getSiigoCode() == null ? "0" : recoverConcept.getInvoiceConcept().getInvoice().getBiller().getCity().getSiigoCode());
		// /*
		// * Campo 23 Código de Ciudad, No es Hard Code, debe tener un codigo de
		// * ciudad donde se radica la factura(Faltante)
		// */
		// recoverLine[23] = "1";
		/*
		 * Campo 25 Código del Cobrador, HARD CODE
		 */
		recoverLine[25] = "1";

		/* Campo 29, Descripcion del concepto a recaudar */
		// recoverLine[29] =
		// recoverConcept.getInvoiceConcept().getConcept().getPrintDescription();
		if (recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getObjectOfContract().getId() == 3) {
			recoverLine[29] = home.nameInvoiceConcept(recoverConcept.getInvoiceConcept());
		} else {

			recoverLine[29] = home.nameInvoiceConcept(recoverConcept.getInvoiceConcept()) + "-" + recoverConcept.getInvoiceConcept().getConcept().getProjectProperty().getRentableUnit().getName();
		}
		/* Campo 30, Especificación si el moviiento es credito o debito */
		recoverLine[30] = this.getRetentionRateAccountNature(account, nature);
		// recoverLine[30] =
		// (recoverConcept.getInvoiceConcept().getConcept().getAccountReceivable().getAccount().substring(0).equals("1")
		// ? "D" : "C");
		/* Campo 31, Valor del Concepto */
		recoverLine[31] = Format_number.FormatToString(recoverConcept.getValue());

		/* Campo 36 Comprobante Cruce */
		/* Si es factura o cuenta de cobro */
		if (recoverConcept.getInvoiceConcept().getInvoice().getDocumentType() < 2) {
			/**
			 * Se obtiene Tipo de Documento No se TIENE en cuenta Notas
			 * Creditos, Solo aplica facturas reversadas
			 */
			switch (recoverConcept.getInvoiceConcept().getInvoice().getDocumentType()) {
			case 0:
				recoverLine[35] = "F";
				break;/* Tipo de Documento Facura */
			case 1:
				recoverLine[35] = "L";
				break;/* Tipo de Documento Cuenta de Cobro */
			}

			/**
			 * Se verifica Tipo de documento. Queda Pendiente Resolucuin de
			 * Cuentas de Cobros.
			 */
			switch (recoverConcept.getInvoiceConcept().getInvoice().getDocumentType()) {
			case 0:
				recoverLine[36] = recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getBillingResolution().getSiigoCode();
				break;/* Facuras */
			case 1:
				recoverLine[36] = recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getAcountsBilling().getSiigoCode();
				break;
			}

			// recoverLine[37] =
			// this.formatBillNumber(recoverConcept.getInvoiceConcept().getInvoice().getNumber())
			// ;
			recoverLine[37] = recoverConcept.getInvoiceConcept().getInvoice().getNumber();

			// Consecutivo de secuencia en la facturación, HARD CODE, Esta mal
			if (recoverConcept.getInvoiceConcept().getSiigoBillingSequence() != null)
				recoverLine[38] = recoverConcept.getInvoiceConcept().getSiigoBillingSequence().toString();
			/* Campo 39, Fecha de vencimiento comprobante de la factura */
			recoverLine[39] = recoverLine[5];
		}

		recoverLine[40] = String.valueOf(account.getAccountsReceivableCode());
		/*
		 * Campo 52, Fecha Comprobante Proveedor , Misma Fecha de la creacion
		 * del recaudo
		 */
		recoverLine[52] = recoverLine[5];
		/* Campo 75 Fecha Optimista, Misma Fecha de la creacion del recaudo */
		recoverLine[75] = recoverLine[5];
		/* Campo 76 Fecha Pesimista, Misma Fecha de la creacion del recaudo */
		recoverLine[76] = recoverLine[5];

		return recoverLine;
	}

	/**
	 * Método que devuelva el primer tipo de linea para un recaudo de un
	 * concepto teniendo el rocverConcept pasado por parametro (Cuenta a usar el
	 * cuenta de bancos)
	 * 
	 * @param recoverConcept
	 * @param nature
	 *            , entero q indica si es una operacion de Facturacion (1),
	 *            Recaudo(2), NotaCredito(3)
	 * @return
	 */
	private String[] getSecondRecoverLine(RecoverConcept recoverConcept, int nature) {
		/* Se extrae la cuenta a usar */
		RetentionRateAccount account = recoverConcept.getInvoiceConcept().getConcept().getAccountingAccountsRecover();

		// Se valida que la cuenta exista
		if (account == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES DE BANCOS NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO" + recoverConcept.getInvoiceConcept().getConcept().getId());
			return new String[0];
		}

		/* Se crea Format de Fecha */
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

		/* Se Crea Linea del Recover */
		String recoverLine[] = this.initializeLineSiigo();
		/* Campo 1 de Siggo Tipo de Documento */

		/**
		 * Se especifica el tipo de documento del recaudo
		 */
		recoverLine[1] = "R";

		/* Campo 2 Comprobante de Pago */
		/**
		 * Se verifica Tipo de documento. Queda Pendiente el consecutivo de
		 * recauddo
		 */
		recoverLine[2] = recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getConsecutiveCollectionAccount().getSiigoCode();

		/* Campo 3 Consecutivo de recaudo */
		recoverLine[3] = recoverConcept.getRecover().getMinCollectionAccounts() + "";
		/* Campo 4 Secuencia (Número de recaudos asociados a la fcaturacion) */
		sequence++;
		recoverLine[4] = String.valueOf(sequence);

		/* Campo 5 Fecha del documento de la factura */
		recoverLine[5] = formater.format(recoverConcept.getRecover().getDate());
		/* Campo 6 Estado del Movimiento HARD CODE 4 */
		recoverLine[6] = "4";
		/* Campo 7 Identificacion del Tercero */
		recoverLine[7] = recoverConcept.getInvoiceConcept().getInvoice().getIdNumberBilled();
		/*
		 * Campo 9 Código Cuenta Contable del Concepto (Deberia estar guardado
		 * en el invoice??) es la Cuenta Credito
		 */
		recoverLine[9] = account.getAccount();

		/* Campo 15 Centro de Costo (Deberia estar guardado en el invoice??) */
		if (recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getObjectOfContract().getId() == 3) // Hoja
																														// de
																														// termino
																														// de
																														// tipo
																														// Servivio
			recoverLine[15] = this.getInvoiceConceptCostCenter(recoverConcept.getInvoiceConcept());
		else if (recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getObjectOfContract() == null)
			recoverLine[15] = "";
		else
			recoverLine[15] = this.getInvoiceConceptCostCenter(recoverConcept.getInvoiceConcept());

		// recoverLine[15] =
		// recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getProject().getCostCenterProject();
		/* Campo 19 Código de Moneda, HARD CODE */
		recoverLine[19] = "1";
		/* Campo 21 Código del Vendedor, HARD CODE */
		recoverLine[21] = "1";
		/* Campo 22 Código del Pais, HARD CODE */
		recoverLine[22] = "1";
		/* Campo 23 Código de Ciudad del facturador */
		recoverLine[23] = (recoverConcept.getInvoiceConcept().getInvoice().getBiller().getCity().getSiigoCode() == null ? "0" : recoverConcept.getInvoiceConcept().getInvoice().getBiller().getCity().getSiigoCode());
		// /*
		// * Campo 23 Código de Ciudad, No es Hard Code, debe tener un codigo de
		// * ciudad donde se radica la factura(Faltante)
		// */
		// recoverLine[23] = "1";
		/*
		 * Campo 25 Código del Cobrador, HARD CODE
		 */
		recoverLine[25] = "1";
		/*
		 * Campo 29, Descripcion de Movimiento Nombre del concepto como aparece
		 * en la factura
		 */
		// recoverLine[29] =
		// recoverConcept.getInvoiceConcept().getConcept().getPrintDescription();
		if (recoverConcept.getInvoiceConcept().getInvoice().getProjectProperty().getObjectOfContract().getId() == 3) {

			recoverLine[29] = home.nameInvoiceConcept(recoverConcept.getInvoiceConcept());
		} else {

			recoverLine[29] = home.nameInvoiceConcept(recoverConcept.getInvoiceConcept()) + "-" + recoverConcept.getInvoiceConcept().getConcept().getProjectProperty().getRentableUnit().getName();
		}

		/* Campo 30, Especificación si el moviiento es credito o debito */
		recoverLine[30] = this.getRetentionRateAccountNature(account, nature);
		// recoverLine[30] = (recoverLine[9].substring(0).equals("1") ? "D" :
		// "C");
		/* Campo 31, Valor del Concepto */
		recoverLine[31] = Format_number.FormatToString(recoverConcept.getValue());

		/* Campo 36 Comprobante Cruce */
		/* Si es factura o cuenta de cobro */
		if (recoverConcept.getInvoiceConcept().getInvoice().getDocumentType() < 2) {

			recoverLine[35] = recoverLine[1];

			recoverLine[36] = recoverLine[2];

			// recoverLine[37] =
			// this.formatBillNumber(recoverConcept.getInvoiceConcept().getInvoice().getNumber())
			// ;
			recoverLine[37] = recoverLine[3];
			// Consecutivo de secuencia en la facturación, HARD CODE, Esta mal
			recoverLine[38] = recoverLine[4];
			/* Campo 39, Fecha de vencimiento comprobante de la factura */
			recoverLine[39] = recoverLine[5];
		}

		/*
		 * Campo 52, Fecha Comprobante Proveedor , Misma Fecha de la creacion
		 * del recaudo
		 */
		recoverLine[52] = recoverLine[5];
		/* Campo 75 Fecha Optimista, Misma Fecha de la creacion del recaudo */
		recoverLine[75] = recoverLine[5];
		/* Campo 76 Fecha Pesimista, Misma Fecha de la creacion del recaudo */
		recoverLine[76] = recoverLine[5];

		return recoverLine;
	}

	private String[] getFourthRecoverLine(String[] firstLine, RecoverConcept recoverConcept, int nature) {
		RetentionRateAccount account = recoverConcept.getInvoiceConcept().getConcept().getAccountingCDOD_cuentasDeudoraControlContario();
		// Se valida que la cuenta exista
		if (account == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES DE DEUDORAS CONTROL CONTRARIO PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO" + recoverConcept.getInvoiceConcept().getConcept().getId());
			return new String[0];
		}

		String[] recoverLine = firstLine.clone();
		recoverLine[9] = account.getAccount();
		recoverLine[30] = this.getRetentionRateAccountNature(account, nature);
		return recoverLine;
	}

	private String[] getThirdRecoverLine(String[] firstLine, RecoverConcept recoverConcept, int nature) {
		RetentionRateAccount account = recoverConcept.getInvoiceConcept().getConcept().getAccountingCDOD_cuentasDeudoraControlInteresVencida();
		// Se valida que la cuenta exista
		if (account == null) {
			log(Level.INFO, "NO SE ENCUENTRA CONFIGURADA LAS CUENTAS CONTABLES DE DEUDORAS CONTROL INTERECES VENCIDA NECESARIAS PARA GENERAR LA INTERFAZ DE SIIGO DEL CONCEPTO" + recoverConcept.getInvoiceConcept().getConcept().getId());
			return new String[0];
		}

		String[] recoverLine = firstLine.clone();
		recoverLine[30] = this.getRetentionRateAccountNature(account, nature);
		return recoverLine;
	}

	/**
	 * Metodo que guarda el archivo siigo previamente generado.
	 * 
	 * @return -1 si no existe ningún archivo por guardar de lo contrario
	 *         retorna 0
	 */
	public int saveFile() {
		if (file == null)
			return -1;
		if (entityManager != null)
			entityManager.persist(file);
		else
			session.persist(file);
		try {
			if (entityManager != null)
				entityManager.flush();
			else
				session.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
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
	 * @param rra
	 *            , RetentionRateAccount Object
	 * @param nature
	 *            , entero q indica si es una operacion de Facturacion (1),
	 *            Recaudo(2), NotaCredito(3)
	 */
	public String getRetentionRateAccountNature(RetentionRateAccount rra, int nature) {
		switch (nature) {
		case 1:
			return rra.getNatureBilling() + "";
		case 2:
			return rra.getNatureRecover() + "";
		case 3:
			return rra.getNatureCreditNote() + "";
		default:
			return null;
		}
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
		BillingTools.printLog(SiigoFunctions.class, level, message);
	}

}
