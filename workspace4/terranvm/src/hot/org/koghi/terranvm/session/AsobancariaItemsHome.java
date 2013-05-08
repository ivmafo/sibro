package org.koghi.terranvm.session;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.bean.DownloadAttachment;
import org.koghi.terranvm.bean.SiigoFunctions;
import org.koghi.terranvm.entity.AsobancariaField;
import org.koghi.terranvm.entity.AsobancariaFormat;
import org.koghi.terranvm.entity.AsobancariaItems;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.InvoiceStatus;
import org.koghi.terranvm.entity.PayFormType;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.Recover;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;

@Name("asobancariaItemsHome")
public class AsobancariaItemsHome extends EntityHome<AsobancariaItems> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<AsobancariaFormat> formats;
	private AsobancariaFormat format;
	private InputStream asobancariaData;
	private List<String[]> errors = new ArrayList<String[]>(); 
	private List<String[]> errors_x_Line = new ArrayList<String[]>(); 
	@In(create = true)
	private RecoverHome recoverHome;
	private List<Recover> recovers;
	private boolean afterProcess = false;
	private String linkSIIGO;
	private String messageError="";
	/**
	 * Lista de los Links generados por cada recaudo
	 */
	private List<PdfReader> linksPDFS = new ArrayList<PdfReader>();
	
	@In(required=false)
    public String projectFilter;
	
	public void log(Object o) {
        toLog(o == null ? "NULL" : o);
    }

    public void log() {
        toLog("");
    }

    public void toLog(Object o) {
        String prefix = " & & & & & & & " + o.toString();
        Logger.getLogger(this.getClass().toString()).log(Level.INFO, prefix);
    }
	
	public void setAsobancariaItemsId(Integer id) {
		setId(id);
	}

	public Integer getAsobancariaItemsId() {
		return (Integer) getId();
	}

	@Override
	protected AsobancariaItems createInstance() {
		AsobancariaItems asobancariaItems = new AsobancariaItems();
		return asobancariaItems;
	}
	
	public String getMessageError() {
		return messageError;
	}

	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		if (getInstance().getAsobancariaField() == null)
			return false;
		if (getInstance().getAsobancariaFormat() == null)
			return false;
		return true;
	}

	public AsobancariaItems getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@SuppressWarnings("unchecked")
	public List<AsobancariaFormat> getFormats() {
		if (formats == null) {
			Query q = getEntityManager().createQuery("FROM AsobancariaFormat");
			formats = q.getResultList();
			if (format == null && formats != null && !formats.isEmpty())
				format = formats.get(0);
		}
		return formats;
	}

	public void setFormats(List<AsobancariaFormat> formats) {
		this.formats = formats;
	}

	public AsobancariaFormat getFormat() {
		return format;
	}

	public void setFormat(AsobancariaFormat format) {
		this.format = format;
	}

	public InputStream getAsobancariaData() {
		return asobancariaData;
	}

	public void setAsobancariaData(InputStream asobancariaData) {
		this.asobancariaData = asobancariaData;

	}

	public void fileUploaded(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		if (file != null) {
			log(file.getContents());
		}
	}

	
	public void asobancariaProcess() {
		messageError = "";
		errors.clear();
		afterProcess = true;
		recovers = new ArrayList<Recover>();
		
		/*Se valida que se tenga un proyecto seleccionado para poder hacer el recaudo*/
		if (projectFilter.equals("-1")){
			messageError = "Proyecto Inválido. Por favor seleccionar un proyecto.";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Recaudo Automático", messageError));
			return;
		}
		
		if (this.format!=null){
			
			DataInputStream dataInputStream = new DataInputStream(asobancariaData);
			BufferedReader br = new BufferedReader(new InputStreamReader(dataInputStream));
			String strLine;
			List<String> lines = new ArrayList<String>();
			
			try {
				while ((strLine = br.readLine()) != null)   {
					lines.add(strLine);
				}
				dataInputStream.close();
			} catch (IOException e) {
				String[] error = {"0","Problemas al leer el archivo"};
				errors.add(error);
				e.printStackTrace();
				return;
			}
			
			try{
				List<String> headerLines = new ArrayList<String>();
				for (int i = 0; i<format.getNumberOfHeaderLines();i++){
					headerLines.add(lines.get(0));
				}
				lines.removeAll(headerLines);
			} catch (java.lang.IndexOutOfBoundsException e) {
				String[] error = {"0","Problemas al leer el encabezado del archivo"};
				errors.add(error);
				e.printStackTrace();
				return;
			}
			
			try{
				List<String> footerLines = new ArrayList<String>();
				for (int i = 0; i<format.getNumberOfFooterLines();i++){
					footerLines.add(lines.get(lines.size()-(i+1)));
				}
				lines.removeAll(footerLines);
			} catch (java.lang.IndexOutOfBoundsException e) {
				String[] error = {"0","Problemas al leer el footer del archivo"};
				errors.add(error);
				e.printStackTrace();
				return;
			}
			
			int countLines = this.format.getNumberOfHeaderLines();
			for (String line : lines) {
				countLines++;
				processAsobacariaLine(line, countLines);
				errors.addAll(errors_x_Line);
			}
			try {
				generateSIIGO();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			String[] error = {"0","No hay nigún formato seleccionado"};
			errors.add(error);
		}
	}

	/**
	 * Metodo que procesa una linea de una arcchivo de asobancaria según el tipo de archivo
	 * @param line línea a procesar
	 */
	private void processAsobacariaLine(String line, int numberLine) {
		try{
			
			errors_x_Line.clear();
			
			Recover recover = getRecover(line,numberLine);
			
			if (!existRecover(recover)){
				if (recover.getPayFormType().getId()!=PayFormType.CHECK_PAY_FORM){
					List<Invoice> invoices = getInvoiceConcept(recover,numberLine);
					try {
						selectInvoices(invoices);
						recoverHome.setInstance(recover);
						recoverHome.setInvoiceRecoverList(invoices);
						recoverHome.setInvoiceConceptsRecoverList(null);
						recoverHome.setRecoverTotalValue(recover.getValue());
						recoverHome.getInvoiceConceptsRecoverList();
						recoverHome.recoverWithPriorityAutomatic();
						recoverHome.setRecoverDate(recover.getDate());
						recoverHome.saveProcessAutomatic();
						recovers.add(recoverHome.getInstance());
						linksPDFS.add(new PdfReader(recoverHome.getLinkPDF()));
						this.getEntityManager().flush();
						recoverHome.clearDirty();
						recoverHome.clearInstance();
					} catch(RuntimeException ex){
						String[] error = {""+ numberLine,"No se pudo guardar el recaudo. Problemas de configuración."};
						errors_x_Line.add(error);
						ex.printStackTrace();
					}catch (Exception ex){
						String[] error = {""+ numberLine,"No se pudo guardar el recaudo. Problemas de configuración."};
						errors_x_Line.add(error);
						ex.printStackTrace();
					}
				}else{
					String[] error = {""+ numberLine,"El tipo de transacción es de cheque. Estos recaudos se deben ingresar de forma manual."};
					errors_x_Line.add(error);
				}
			}else{ 
				String[] error = {""+ numberLine,"Ya se ha ingresado este recaudo al sistema."};
				errors_x_Line.add(error);
			}
				
		}catch(RuntimeException ex){
			ex.printStackTrace();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Metodo que selecciona todas la invoice para ser recaudadas
	 * @param invoices
	 */
	private void selectInvoices(List<Invoice> invoices) {
		for (Invoice invoice : invoices) {
			invoice.setSelected(true);
		}		
	}

	@SuppressWarnings("unchecked")
	private List<Invoice> getInvoiceConcept(Recover recover,	int numberLine) {
		Query q = getEntityManager().createQuery("FROM BusinessEntity bu WHERE bu.idNumber = ? ");
		q.setParameter(1, recover.getPayerIdentification());
		q.setMaxResults(1);
		List<?> result = q.getResultList();
		
		
		if (result != null && !result.isEmpty()){
			BusinessEntity payer = (BusinessEntity) result.get(0);
			Query query = this.getEntityManager().createQuery("SELECT DISTINCT(inv) FROM  Invoice inv, InvoiceConcept invCon WHERE inv.approved = ? AND inv.invoiceStatus.id = ? AND invCon.invoiceConceptType != ? AND inv.id = invCon.invoice AND invCon.balance > 0 AND inv.projectProperty.project.id = ? AND inv.idNumberBilled = :nit ORDER BY inv.expeditionDate");
			query.setParameter("nit", payer.getIdNumber());
			query.setParameter(1, true);
			query.setParameter(2, InvoiceStatus.STATUS_VIGENTE);
			query.setParameter(3, InvoiceConcept.TYPE_CREDIT_NOTE);
			query.setParameter(4, Integer.valueOf(projectFilter));
			List <?> invoiceRecoverList = query.getResultList();
			return (List<Invoice>) invoiceRecoverList;
			
		}else{
			String[] error = {""+ numberLine,"No se encuentra el tercero con numero de identificación " + recover.getPayerIdentification()};
			errors_x_Line.add(error);
			throw new RuntimeException("No se encuentra el tercero con numero de identificación " + recover.getPayerIdentification());
		}
			
		
		
	}

	/**
	 * Metodo en el que devuelve recover según la línea y el formato de la clase
	 * @param line
	 * @return
	 */
	private Recover getRecover(String line, int numberLine) {
		Recover recover = new Recover();
		for (AsobancariaItems item : format.getAsobancariaItems()){
			String value = getItem(item,line, numberLine);
			switch (item.getAsobancariaField().getId()) {
			case AsobancariaField.PK_PAYER_IDENTIFICATION:
				value = cleanValue(value);
				recover.setPayerIdentification(value);
				break;
			case AsobancariaField.PK_DATE:
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				try {
					recover.setDate(dateFormat.parse(value));
				} catch (ParseException e) {
					String[] error = {""+ numberLine,"El formato de la fecha es erronea. Fecha =  "+ value};
					errors_x_Line.add(error);
					throw new RuntimeException("El formato de la fecha es erronea. Fecha =  "+ value ,e);
				}
				break;
			case AsobancariaField.PK_OFFICE:
				recover.setOffice(value);
				break;
			case AsobancariaField.PK_PAY_IDENTIFICATION:
				recover.setPayIdentification(value);
				break;
			case AsobancariaField.PK_VALUE:
				try{
				recover.setValue(getValue(value));
				} catch (NumberFormatException ex){
					String[] error = {""+ numberLine,"El formato del valor es erronea. Valor =  "+ value};
					errors_x_Line.add(error);
					throw ex;
				}
				break;
			case AsobancariaField.PK_TRANSACTION_TYPE:
				recover.setPayFormType(getTransactionType(value));
				break;

			default:
				break;
			}

		}
		return recover;
	}

	/**
	 * Método que quita espacios y ceros a las izquierda del valor
	 * @param value
	 */
	private String cleanValue(String value) {
		value = value.replace(" ", "");
		while (value.startsWith("0")){
			value = value.replaceFirst("0", "");
		}
		return value;
	}

	/**
	 * Metodo que retorna la forma de pago según la configuración del formato del archivo asobancario a partir del valor
	 * @param value 
	 * @return
	 */
	private PayFormType getTransactionType(String value) {
		if (format.getCheckIdentification().equals(value)){
			return getEntityManager().find(PayFormType.class, PayFormType.CHECK_PAY_FORM);
		}else{
			return getEntityManager().find(PayFormType.class, PayFormType.AVAIBLE_PAY_FORM);
		}
	}

	/**
	 * Metodo que retorna un double con dos decimale según el string. Este string no tiene ni puntos ni comas
	 * @param value
	 * @return
	 */
	private double getValue(String value) {
		return Double.parseDouble(value) / 100;
	}

	/**
	 * Metodo que devuelve el string de un item de la línea segun las posiciones del item
	 * item.getPos, item.getIniPos(), item.getEndPos() se esperan que se encuentren comenzando desde el 1
	 * 
	 * @param item
	 * @param line
	 */
	private String getItem(AsobancariaItems item, String line, int numberLine) {
		String value;
		if (format.getSeparator()==null || (format.getSeparator()!= null && format.getSeparator().isEmpty())){
			try{
				value = line.substring(item.getIniPos()-1, item.getEndPos());
			} catch(IndexOutOfBoundsException ex){
				String[] error = {""+ numberLine,"La configuración de la archivo asobancaria no coincide con la línea"};
				errors_x_Line.add(error);
				throw ex;
			}
			
		} else{
			try{
				value = line.split(format.getSeparator())[item.getPos()-1];
			} catch (ArrayIndexOutOfBoundsException ex){
				String[] error = {""+ numberLine,"La configuración de la archivo asobancaria no coincide con la línea"};
				errors_x_Line.add(error);
				throw ex;
			}
		}
		return value;
	}

	/**
	 * Metodo que valida si un recaudo ya a sido ingresado al sistema
	 * @param line
	 * @return
	 */
	private boolean existRecover(Recover recover) {
		Query q = getEntityManager().createQuery("FROM Recover r WHERE r.date = ? AND r.office = ? AND r.payerIdentification = ? AND r.payIdentification = ? AND r.value = ? AND r.payFormType = ?");
		
		q.setParameter(1, recover.getDate());
		q.setParameter(2, recover.getOffice());
		q.setParameter(3, recover.getPayerIdentification());
		q.setParameter(4, recover.getPayIdentification());
		q.setParameter(5, recover.getValue());
		q.setParameter(6, recover.getPayFormType());
		
		List<?> result = q.getResultList();
		if (result!=null && !result.isEmpty()){
			return true;
		}else 
			return false;
	}

	public List<String[]> getErrors() {
		return errors;
	}

	public void setErrors(List<String[]> errors) {
		this.errors = errors;
	}

	public List<Recover> getRecovers() {
		return recovers;
	}

	public void setRecovers(List<Recover> recovers) {
		this.recovers = recovers;
	}

	public boolean isAfterProcess() {
		return afterProcess;
	}

	public void setAfterProcess(boolean afterProcess) {
		this.afterProcess = afterProcess;
	}

	public String getLinkSIIGO() {
		return linkSIIGO;
	}

	public void setLinkSIIGO(String linkSIIGO) {
		this.linkSIIGO = linkSIIGO;
	}
	
	public void generateSIIGO() throws Exception{
		SiigoFunctions siigoFunctions = new SiigoFunctions(getEntityManager());
		
		if (!projectFilter.equals("-1")){
			Project project = getEntityManager().find(Project.class, Integer.valueOf(projectFilter));
			setLinkSIIGO(siigoFunctions.recoverSiggo(recovers, project));
		}else{
			throw new Exception("Proyecto Inválido. Por favor seleccionar un projecto.");
		}
	}
	
	@Override
	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
		}
	}

	public List<PdfReader> getLinksPDFS() {
		return linksPDFS;
	}

	public void setLinksPDFS(List<PdfReader> linksPDFS) {
		this.linksPDFS = linksPDFS;
	}
	
	public String ConcatenatePdf() {
		DownloadAttachment downloadAttachment = new DownloadAttachment();

		try {

			String filePDF1 = "tmp/billingResolution/recoverPDF" + this.projectFilter + ".pdf";
			String server1 = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
			String path1 = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server1);
			path1 = path1.substring(0, path1.lastIndexOf("/")) + "/";

			log("Concatenate Two PDF");

			List<PdfReader> readerList = new ArrayList<PdfReader>();

			for (PdfReader pdf : linksPDFS)
				readerList.add(pdf);

			PdfCopyFields copy = new PdfCopyFields(new FileOutputStream(path1 + filePDF1));

			for (PdfReader read : readerList)
				copy.addDocument(read);

			copy.close();

			return downloadAttachment.download(filePDF1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}