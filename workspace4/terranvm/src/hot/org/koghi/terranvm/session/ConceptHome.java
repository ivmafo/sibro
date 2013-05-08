package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.ConceptRetentionRateAccount;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RetentionRate;
import org.richfaces.model.selection.Selection;

@Name("conceptHome")
public class ConceptHome extends EntityHome<Concept> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@In(create = true)
	ProjectPropertyHome projectPropertyHome;
	
	private String mainUrl;
	
	private String periodicityTypeName;

	private Selection selectionPaymentForm;
	
	public String Messagedate="";
	

	public String getMessagedate() {
		return Messagedate;
	}

	public void setMessagedate(String messagedate) {
		Messagedate = messagedate;
	}

	public void setConceptId(Integer id) {
		setId(id);
	}

	public Integer getConceptId() {
		return (Integer) getId();
	}

	@Override
	protected Concept createInstance() {
		Concept concept = new Concept();
		return concept;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ProjectProperty projectProperty = projectPropertyHome
				.getDefinedInstance();
		if (projectProperty != null) {
			getInstance().setProjectProperty(projectProperty);
		}
		
		
	}

	public boolean isWired() {
		if (getInstance().getProjectProperty() == null)
			return false;

		return true;
	}

	public Concept getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}


	public String getMainUrl() {
		if (this.mainUrl == null || this.mainUrl.isEmpty())
			this.mainUrl = "No existe una URL principal asociada al plugin";
		return this.mainUrl;
	}

	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}

	

	public void instanceIframeUrl() {
		
	}

	public String hostURL() {
		String host = "http://";
		host += FacesContext.getCurrentInstance().getExternalContext()
				.getRequestServerName();
		host += ":"
				+ FacesContext.getCurrentInstance().getExternalContext()
						.getRequestServerPort();
		return host;
	}

	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}

	public boolean conceptListInApprove(Concept c) {
		return new MakerCheckerHome().isObjectInMakerChecker(c);
	}

	public void updateInstanceMaker(int makerCheckerId) {
		setInstance((Concept) new MakerCheckerHome()
				.getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}
	

	public String persistConcept() {
		try {
			Concept concept = getInstance();		
			List<ConceptRetentionRateAccount> getConceptRetentionRateAccounts=concept.getConceptRetentionRateAccounts();
			for (ConceptRetentionRateAccount conceptRetentionRateAccount : getConceptRetentionRateAccounts) {
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++LA cuenta es : "+conceptRetentionRateAccount.getId());
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++LA cuenta es : "+conceptRetentionRateAccount.getRetentionRateAccount().getName());
			}
			//concept.setName("ConceptoModificadoPrueba");
			setInstance(concept);
			getEntityManager().persist(getInstance());
			getEntityManager().flush();		
			return "0k";
		} catch (org.hibernate.validator.InvalidStateException ex) {
			InvalidValue[] iv = ex.getInvalidValues();
			System.out.println("****** OJO ERROR DE validacion: "+iv[0]);
			ex.printStackTrace();
			return null;

		}
	}

	@SuppressWarnings("deprecation")
	@Transactional
	public void approveChange() {
		setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();		
		new MakerCheckerHome().deleteMaker(this.getInstance());
		//approvePlugin();
		getFacesMessages().addFromResourceBundle(Severity.INFO,
				"#{messages.Successful_passage}", "ApproveSuccessfully");
	}
	
	@Transactional
	public void deleteRetenetionRateAccounts() {
		
		String query = "DELETE FROM concept_retention_rate_account a "+
							"WHERE" +
								" a.concept = ?  AND "+ 
								"a.retention_rate_account IN  ("+
									"SELECT id from retention_rate_account b WHERE " +
									"a.retention_rate_account = b.id AND " +
									"(b.retention_rate = ? " +
									"OR b.retention_rate = ? "+
									"OR b.retention_rate = ?)"+
								")";
		
		Query q = this.getEntityManager().createNativeQuery(query);
		q.setParameter(1, this.getInstance().getId());
		q.setParameter(2, RetentionRate.RETENTION_RATE_RTEFUENTE);
		q.setParameter(3, RetentionRate.RETENTION_RATE_RTEICA);
		q.setParameter(4, RetentionRate.RETENTION_RATE_RTEIVA);
		int records = q.executeUpdate();
		System.out.print("Elimando Registros de ConceptoRetentionAccount del Concepto : [Record] "+ records);
	}

	@SuppressWarnings("deprecation")
	public void cancelChange() {
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO,
				"#{messages.Successful_cancellation}", "CancelSuccessfully");
	}

	public List<SelectItem> getPeriodicityTypeItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		String[][] types = Concept.PERIODS_TYPE;
		for (int i = 0; i < types.length; i++) {
			SelectItem item = new SelectItem(Integer.getInteger(types[i][0]),
					types[i][1]);
			items.add(item);
		}
		return items;
	}

	public String getPeriodicityTypeName() {
		convertPeriodicityTypeToString();
		return periodicityTypeName;
	}

	public void setPeriodicityTypeName(String periodicityTypeName) {
		convertPeriodicityTypeToString();
		this.periodicityTypeName = periodicityTypeName;

	}

	private void convertPeriodicityTypeToString() {
		if (getInstance() != null && getInstance().getPeriodicityType() != null
				&& getInstance().getPeriodicityType() != 0) {
			String[][] types = Concept.PERIODS_TYPE;
			for (int i = 0; i < types.length; i++) {
				if (Integer.getInteger(types[i][0]) == getInstance()
						.getPeriodicityType())
					this.periodicityTypeName = types[i][1];
			}
		}
	}

	public String convertResponsibleToString() {
		if (getInstance() != null && getInstance().getResponsible() != null) {
			if (getInstance().getResponsible() == Concept.RESPONSAIBLE_LESSEE) {
				return "Arrendatario";
			} else if (getInstance().getResponsible() == Concept.RESPONSAIBLE_OWNER) {
				return "Propietario";
			}
		}
		return "";
	}

	public String convertDocumentTypeToString() {
		if (getInstance() != null && getInstance().getDocumentType() != null) {
			if (getInstance().getDocumentType() == Concept.DOCUMENT_TYPE_ACCOUNT_RECEIVABLE) {
				return "Cuenta de cobro";
			} else if (getInstance().getDocumentType() == Concept.DOCUMENT_TYPE_BILL) {
				return "Factura";
			}
		}
		return "";
	}

	public Selection getSelectionPaymentForm() {
		return selectionPaymentForm;
	}

	public void setSelectionPaymentForm(Selection selectionPaymentForm) {
		this.selectionPaymentForm = selectionPaymentForm;
	}

	public String crateMakerChecker() {
		new MakerCheckerHome().persistObject(getInstance());
		return "updated";
	}
	/**
	 * devuelve el nombre que se mostrar en las páginas y e PDF, según corresponda(Intereses de mora y retroactivos)
	 * @param concept
	 * @return
	 */
	public String  nameInvoiceConcept(InvoiceConcept concept){
		BillingTools tools = new BillingTools(getEntityManager());
		return tools.nameInvoiceConcept(concept.getInvoiceConceptType(), concept.getConcept().getName());
	}
	
}
