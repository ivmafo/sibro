package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.CounterConfigurationTamplate;
import org.koghi.terranvm.entity.CounterTemplateHasRetentionRateAccount;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.RetentionRate;
import org.koghi.terranvm.entity.RetentionRateAccount;
import org.koghi.terranvm.entity.TaxConfiguration;
import org.richfaces.component.html.HtmlExtendedDataTable;
import org.richfaces.model.selection.Selection;

@Name("counterConfigurationTamplateHome")
public class CounterConfigurationTamplateHome extends EntityHome<CounterConfigurationTamplate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<CounterTemplateHasRetentionRateAccount> accotuntsItems;
	private BusinessEntity biller;
	private List<BusinessEntity> businessEntities;
	private Selection selectionAccount;
	private String tableState;
	private ArrayList<Object[]> listInterestAccountsConfiguration;
	private ArrayList<Object[]> listTaxConfiguration; 

		
	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableAccountBinds;
	
	
	@In(required=false)
	    public String projectFilter;

	public BusinessEntity getBiller() {
		if(this.instance!= null && this.instance.getProjectId1() == 0) 
			this.instance.setProjectId1(Integer.parseInt(projectFilter));
		if (biller==null && this.instance != null && this.instance.getBiller() != null)   
		biller = getEntityManager().find(BusinessEntity.class, this.instance.getBiller());
		return this.biller;
	}

	public void setBiller(BusinessEntity biller) {
		if(biller!=null)
		this.instance.setBiller(biller.getId());
//		this.instance.setCounterTemplateHasRetentionRateAccounts(new ArrayList<CounterTemplateHasRetentionRateAccount>());
		this.biller = biller; 
		
	}

	public void setCounterConfigurationTamplateId(Integer id) {
		setId(id);
	}

	public Integer getCounterConfigurationTamplateId() {
		return (Integer) getId();
	}

	public ArrayList<CounterTemplateHasRetentionRateAccount> getAccotuntsItems() {
		return accotuntsItems;
	}

	public void setAccotuntsItems(ArrayList<CounterTemplateHasRetentionRateAccount> accotuntsItems) {
		this.accotuntsItems = accotuntsItems;
	}

	@Override
	protected CounterConfigurationTamplate createInstance() {
		CounterConfigurationTamplate counterConfigurationTamplate = new CounterConfigurationTamplate();
		return counterConfigurationTamplate;
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
		return true;
	}

	public CounterConfigurationTamplate getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<CounterTemplateHasRetentionRateAccount> getCounterTemplateHasRetentionRateAccounts() {
		return getInstance() == null ? null : new ArrayList<CounterTemplateHasRetentionRateAccount>(getInstance().getCounterTemplateHasRetentionRateAccounts());
	}
 
                    /*
                     * actualiza Las retentionRateAccount del template, según la cuenta que recibe por parametro
                     */
	public void updateCountItems(int accountType) {
		if(this.instance.getProjectId1() == 0)
			this.instance.setProjectId1(Integer.parseInt(projectFilter));          
			
		accotuntsItems = new ArrayList<CounterTemplateHasRetentionRateAccount>();
		Query query = getEntityManager().createQuery("SELECT businessEntity FROM Project p WHERE p.id=?");
		query.setParameter(1, this.instance.getProjectId1());
		Query q = getEntityManager().createQuery("FROM RetentionRateAccount r where r.biller = ? and r.retentionRate.id = ?");
		q.setParameter(1, query.getResultList().get(0));
		q.setParameter(2, accountType);

		List<?> accounts = q.getResultList();

		for (Object account : accounts) {
			if (account instanceof RetentionRateAccount) {
				RetentionRateAccount aux = (RetentionRateAccount) account;
				CounterTemplateHasRetentionRateAccount accountConcept = new CounterTemplateHasRetentionRateAccount();
				accountConcept.setRetentionRateAccountId(aux);
				accountConcept.setCounterConfigurationTamplate(instance);
				accotuntsItems.add(accountConcept);
			}
			
		}

	}

	
	/**
	 * Función que trae el o las entidades facturadoras que tiene el proyecto.
	 */
	public List<BusinessEntity> getBusinessEntities() {
		businessEntities = new ArrayList<BusinessEntity>();
		Query q = getEntityManager().createQuery("FROM Project p WHERE p.id = ?");
		q.setParameter(1, Integer.parseInt(projectFilter));
		Project p = (Project) q.getResultList().get(0); 
		if(p.getBusinessEntity() != null)
		businessEntities.add(p.getBusinessEntity());
		if(p.getBusinessLine().getBusinessEntity() != null)
			if(!businessEntities.contains(p.getBusinessLine().getBusinessEntity()))
			businessEntities.add(p.getBusinessLine().getBusinessEntity());
		if(this.instance.getBiller() != null)
			for (BusinessEntity businessEntity : businessEntities) {
				if(businessEntity.getId() == this.instance.getBiller())
				setBiller(businessEntity);
			}
		if(this.getBiller() == null)
			this.setBiller(businessEntities.get(0));
		return businessEntities;
	}

	public void setBusinessEntities(List<BusinessEntity> businessEntities) {
		this.businessEntities = businessEntities;
	}

	public Selection getSelectionAccount() {
		return selectionAccount;
	}

	public void setSelectionAccount(Selection selectionAccount) {
		this.selectionAccount = selectionAccount;
	}
	/**
	 * al seleccionar una cuenta contable 
	 * se actualiza el listado de counterTemplateHasRetentionRate
	 * al cambiar o seleccionar una retentionRate en la interface grafica
	 */
	public void onSelectionAccountChanged() {
		if (selectionAccount != null) {
			BillingTools b = new BillingTools(getEntityManager());
			Iterator<Object> it = selectionAccount.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				tableAccountBinds.setRowKey(key);
				if (tableAccountBinds.isRowAvailable()) {
					CounterTemplateHasRetentionRateAccount conceptAccount = (CounterTemplateHasRetentionRateAccount) tableAccountBinds.getRowData();
					RetentionRate retentionRate = conceptAccount.getRetentionRateAccountId().getRetentionRate();

					switch (retentionRate.getId()) {
					case RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE:
						this.instance.setAccountReceivable(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_BANK_ACCOUNT:
						this.instance.setAccountingAccountsRecover(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_DISCOUNT:
						this.instance.setAccountingAccountsEarlyPayment(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_INCOME_ACCOUNT:
						this.instance.setAccountingCreditAccounts(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_IVA:
						this.instance.setTax(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_TIMBRE:
						this.instance.setStamptax(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_PENALTY_OF_PORTAFOLIO:
						this.instance.setStamptax(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS:
						this.instance.setAccountingAccountingCDOD_cuentasBancos(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_INGRESOS:
						this.instance.setAccountingAccountingCDOD_cuentasIngresos(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_IVA:
						this.instance.setAccountingAccountingCDOD_cuentasIVA(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR:
						this.instance.setAccountingAccountingCDOD_cuentasXCobrar(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_CONTRARIO:
						this.instance.setAccountingAccountingCDOD_cuentasDeudoraControlContario(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_INTERESES_DEUDAS_VENCIDAS:
						this.instance.setAccountingAccountingCDOD_cuentasDeudoraInteresVencida(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_OTROSINGRESOS:
						this.instance.setAccountingAccountingCDOD_cuentasOtrosIngresos(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE:
						this.instance.setAccountingAccountingCDOD_cuentasReteFuente(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA:
						this.instance.setAccountingAccountingCDOD_cuentasReteICA(conceptAccount);
						break;
					case RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA:
						this.instance.setAccountingAccountingCDOD_cuentasReteIVA(conceptAccount);
						break;
					default:
						this.instance.setAccount(retentionRate.getId(), conceptAccount);
						break;

					}

					if (retentionRate.getSubcategoryFrom() != null && retentionRate.getSubcategoryFrom().getId() == RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS) {
						this.updateTableInterestRateAccount(conceptAccount.getRetentionRateAccountId());
					}
				}
				b.persistObject(this.instance);
			}

			selectionAccount = null;
		}
		
		
	}
	public void updateTableInterestRateAccount(RetentionRateAccount retentionRateAccount) {

		for (Object[] subcategory : this.listInterestAccountsConfiguration) {
			if (((RetentionRate) subcategory[0]).getId() == retentionRateAccount.getRetentionRate().getId()) {
				subcategory[1] = retentionRateAccount.getAccount() + " - " + retentionRateAccount.getName();
				break;
			}
		}
	}

	public String getTableState() {
		return tableState;
	}

	public void setTableState(String tableState) {
		this.tableState = tableState;
	}
	/**
	 * Método que retorna si se debe pintar Lista de Iva.
	 * 
	 * @return
	 */
	public Boolean printTaxList() {

		if(getBiller() != null){
		/* Se busca el Listado de De Responsabilidades Tributarias */
		String taxLiabilitiesBiller[] = this.getBiller().getTaxLiabilities().split(",");

		for (int i = 0; i < taxLiabilitiesBiller.length; i++) {
			if (taxLiabilitiesBiller[i].equals("23")) {
				return false;
			}
		}
		}

		return true;

	}

	@SuppressWarnings("unchecked")
	public ArrayList<Object[]> getListTaxConfiguration() {
		if(getBiller() != null){
			this.listTaxConfiguration = new ArrayList<Object[]>();
			List<TaxConfiguration> taxList = null;
				/*
				 * Se extrae configuracion de impuesto dependiendo de la
				 * configuracion del Facturador (No se conoce facturado)
				 */ 
				ProjectPropertyHome p = new ProjectPropertyHome();
				String sql = "SELECT  DISTINCT (tx) FROM TaxConfiguration tx where  tx.taxliabilitiesByBiller.id IN (" + p.toStringSepareteComma(getBiller().getTaxLiability()) + ")  ";
				sql += " AND tx.retentionRate.subcategoryFrom IS NULL";
				Query query = getEntityManager().createQuery(sql);
				taxList = (List<TaxConfiguration>) query.getResultList();

			ArrayList<Integer> IdRetention = new ArrayList<Integer>();

			for (TaxConfiguration tx : taxList) {
 
				Object obj[] = new Object[3];
				obj[0] = tx.getRetentionRate().getId();
				obj[1] = tx.getRetentionRate().getId();

				if (IdRetention.size() > 0) {
					if (IdRetention.contains(tx.getRetentionRate().getId()) == false)
						this.listTaxConfiguration.add(obj);
					IdRetention.add(tx.getRetentionRate().getId());
				} else {
					IdRetention.add(tx.getRetentionRate().getId());
					this.listTaxConfiguration.add(obj);
				} 

			}
		}
			if(listTaxConfiguration==null)
				listTaxConfiguration = new ArrayList<Object[]>();
		return this.listTaxConfiguration;
	}

	public void setListTaxConfiguration(ArrayList<Object[]> listTaxConfiguration) {
		this.listTaxConfiguration = listTaxConfiguration;
	}
	/**
	 * Metodo que arma una lista que retorna las cuentas de intereses 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Object[]> getListInterestAccountsConfiguration() {

		if (this.getBiller() != null && this.listInterestAccountsConfiguration == null) {

			this.listInterestAccountsConfiguration = new ArrayList<Object[]>();
			Query q = getEntityManager().createQuery("FROM RetentionRate rr WHERE rr.subcategoryFrom.id = ?");
			q.setParameter(1, RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS);
			List<RetentionRate> temp = q.getResultList();

			List<TaxConfiguration> temp1 = new ArrayList<TaxConfiguration>(0);

			List<RetentionRate> retentions_to_delete = new ArrayList<RetentionRate>();
			for (RetentionRate rr : temp) {
				RetentionRate tempRetentionRate = null;
				int rrID = rr.getId();
				for (TaxConfiguration taxConfiguration : temp1) {
					if ((taxConfiguration.getRetentionRate().isRetantion() && taxConfiguration.getRetentionRate().getId() == rrID) || !rr.isRetantion()) {
						tempRetentionRate = rr;
						continue;
					}
				}
				if (tempRetentionRate == null && (rr.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEIVA || rr.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEICA || rr.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_RETEFUENTE)) {
					retentions_to_delete.add(rr);

				}

			}

			for (RetentionRate retentionRate : retentions_to_delete) {
				temp.remove(retentionRate);
			}

			if (temp.isEmpty()) {
				log("No existe la categoría de cuentas contables 'CUENTAS DE ORDEN DEUDORAS' con ID " + RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS);
			} else {
				if (temp.isEmpty()) {
					log("No existen subcategorias para 'CUENTAS DE ORDEN DEUDORAS' !!");
					this.listInterestAccountsConfiguration = null;
				} else {
					for (RetentionRate subcategory : temp) {
						Object[] array = new Object[2];
						array[0] = subcategory;
						array[1] = "";
						listInterestAccountsConfiguration.add(array);

					}
					return listInterestAccountsConfiguration;
				}
			}

		} 
		return listInterestAccountsConfiguration;
	}
	
	public void log(Object o) {
		String prefix = " % % % % % % % " + (o == null ? "null" :o.toString());
		Logger.getLogger(ProjectPropertyHome.class.getName()).log(Level.INFO, prefix);
	}
	/**
	 * metodo que busca el nombre de una entidad facturadora
	 * @return Nombre
	 */
	public String nameBiller(){
		String name ="";
		Query q = getEntityManager().createNativeQuery(" SELECT name_business_entity from business_entity where id = ?");
		q.setParameter(1, this.instance.getBiller());
		name = (String)q.getResultList().get(0);
		return name;
	}
	
	
	
	

}
