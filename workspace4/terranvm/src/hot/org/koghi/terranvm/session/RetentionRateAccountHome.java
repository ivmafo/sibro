package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.ConceptRetentionRateAccount;
import org.koghi.terranvm.entity.RetentionRate;
import org.koghi.terranvm.entity.RetentionRateAccount;
import org.richfaces.component.html.HtmlExtendedDataTable;

@Name("retentionRateAccountHome")
public class RetentionRateAccountHome extends EntityHome<RetentionRateAccount> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @In(create = true)
    RetentionRateHome retentionRateHome;

    @In(required = false)
    @Out(required = false)
    private HtmlExtendedDataTable htmlBusinessEntitiesRRADataTable;
    @In(required = false)
    @Out(required = false)
    private HtmlExtendedDataTable htmlRetetentionRateAccountsRRADataTable;

    private String businessEntitiesListTableState;
    private String retetentionRateAccountsListTableState;
    private List<BusinessEntity> businessEntitiesList;
    private List<RetentionRateAccount> retentionRateAccountsList;
    private String searchName;
    private String searchId;
    private BusinessEntity businessEntity;
    private boolean toogleOpened1;
    private boolean toogleOpened2;
    private List<RetentionRate> retentionRates;
    private List<SelectItem> natures;
    private RetentionRate category;
    private RetentionRate subcategory;
    /**
     * 0 add, 1 edit
     */
    private int operation;

    public void log(Object o) {
        String prefix = " = = = = = = " + o.toString();
        Logger.getLogger(RetentionRateAccountHome.class.getName()).log(Level.INFO, prefix);
    }

    private List<RetentionRate> RetentionRateList = null;

    public void setRetentionRateAccountId(Integer id) {
        setId(id);
    }

    public Integer getRetentionRateAccountId() {
        return (Integer) getId();
    }

    @Override
    protected RetentionRateAccount createInstance() {
        RetentionRateAccount retentionRateAccount = new RetentionRateAccount();
        return retentionRateAccount;
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

    public RetentionRateAccount getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }

    public List<ConceptRetentionRateAccount> getConceptRetentionRateAccounts() {
        return getInstance() == null ? null : new ArrayList<ConceptRetentionRateAccount>(getInstance().getConceptRetentionRateAccounts());
    }

    @SuppressWarnings("unchecked")
    public void searchRRAList() {
        if (this.businessEntity != null) {
            Query query = this.getEntityManager().createQuery("FROM  RetentionRateAccount rra WHERE rra.biller = ?");
            query.setParameter(1, this.businessEntity);
            this.retentionRateAccountsList = query.getResultList();
            if (this.retentionRateAccountsList.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Búsqueda", "No se encontraron cuentas contables para " + this.businessEntity.getNameBusinessEntity()));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Búsqueda", "Exiten " + this.retentionRateAccountsList.size() + " cuentas contables para " + this.businessEntity.getNameBusinessEntity()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Búsqueda", "Entidad de Negocio no definida"));
        }
    }

    public void instanceBusinessEntity(BusinessEntity businessEntity) {
        toogleOpened1 = false;
        toogleOpened2 = true;
        this.operation = 0;
        this.retentionRateAccountsList = null;
        this.businessEntity = businessEntity;
    }

    public void instanceRRA(RetentionRateAccount rra) {
        this.initRRA();
        this.operation = 1;
        this.setInstance(rra);
    }

    public void newRRA() {
        if (this.businessEntity != null) {
            this.initRRA();
            this.operation = 0;
            this.instance = new RetentionRateAccount();
            this.instance.setBiller(this.businessEntity);
            this.instance.setNatureBilling(' ');
            this.instance.setNatureRecover(' ');
            this.instance.setNatureCreditNote(' ');
            this.instance.setValue(0.0);
            if (!this.retentionRates.isEmpty())
                this.category = retentionRates.get(0);

        } else {
            log("No se puede inicializar una nueva RetentionRateAcccount xq la entidad de negocio es null");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Entidad de Negocio no definida"));
        }
    }

    public void initRRA() {
        this.instance = null;
        this.subcategory = null;
        this.instanceCategories();
        this.instanceNatures();

    }

    @SuppressWarnings("unchecked")
    public void instanceCategories() {
        if (this.retentionRates == null) {
            this.retentionRates = new ArrayList<RetentionRate>();
            Query query = this.getEntityManager().createQuery("FROM  RetentionRate rr WHERE rr.subcategoryFrom IS NULL");
            retentionRates = query.getResultList();
            if (retentionRates.isEmpty()) {
                log("La lista RetentionRate es vacia, no se puede incializar combo de categorias");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Lista de categorias esta vacía"));
            }
        }
    }

    public void instanceNatures() {
        if (this.natures == null) {
            this.natures = new ArrayList<SelectItem>();
            for (String[] strings : RetentionRateAccount.NATURES) {
                SelectItem si = new SelectItem(strings[0] + 7, strings[1]);
                natures.add(si);
            }
        }
    }

    public boolean addEditDisabled() {
        if (this.instance == null)
            return true;
        if (this.category == null)
            return true;
        if (!this.category.getSubcategories().isEmpty() && this.subcategory == null)
            return true;
        if (this.instance.getName() == null || this.instance.getName().isEmpty())
            return true;
        if (this.instance.getAccount() == null || this.instance.getAccount().isEmpty())
            return true;
        if (this.isPercentValueRendered() && (this.instance.getValue() == null || this.instance.getValue() == 0.0))
            return true;

        return false;
    }

    public void addEditRRA() {
        if (this.instance != null) {
            this.instance.setRetentionRate(this.category);
            if (this.subcategory != null) {
                this.instance.setRetentionRate(this.subcategory);
            }
            if (!this.instance.getRetentionRate().isRetantion())
                this.instance.setValue(null);
            if (!this.isBankDescriptionRendered())
                this.instance.setDescription(null);
            this.persist();
            if (this.operation == 0)
                this.retentionRateAccountsList.add(this.instance);
            this.category = null;
            this.subcategory = null;
        } else {
            log("Se intenta guardar una cuenta contable nula");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se intenta guardar una cuenta contable nula"));
        }
    }

    @SuppressWarnings("unchecked")
    public boolean canDeleteRRA() {
        if (this.instance != null && this.instance.getId() != 0 && this.operation == 1) {
            List<Long> size = new ArrayList<Long>();
            Query query = this.getEntityManager().createQuery("SELECT COUNT(crra.id) FROM  ConceptRetentionRateAccount crra WHERE crra.retentionRateAccount = ?");
            query.setParameter(1, this.instance);
            size = query.getResultList();
            int relations = Integer.parseInt(size.get(0) + "");
            if (relations == 0)
                return true;
        }
        return false;
    }

    public void deleteRRA() {
        this.retentionRateAccountsList.remove(this.instance);
        this.remove();
    }

    /*
     * _______________________________________________________________
     * 
     * GETTERS AND SETTERS
     * ______________________________________________________________
     * 
     * 
     * 
     * 
     * -
     */

    @SuppressWarnings("unchecked")
    public List<RetentionRate> getRetentionRateList() {
        if (this.RetentionRateList == null) {
            Query q = this.getEntityManager().createQuery("from RetentionRate  ct");
            this.RetentionRateList = (List<RetentionRate>) q.getResultList();
        }

        return RetentionRateList;
    }

    public void setRetentionRateList(List<RetentionRate> retentionRateList) {
        RetentionRateList = retentionRateList;
    }

    public RetentionRateHome getRetentionRateHome() {
        return retentionRateHome;
    }

    public void setRetentionRateHome(RetentionRateHome retentionRateHome) {
        this.retentionRateHome = retentionRateHome;
    }

    public String getBusinessEntitiesListTableState() {
        return businessEntitiesListTableState;
    }

    public void setBusinessEntitiesListTableState(String businessEntitiesListTableState) {
        this.businessEntitiesListTableState = businessEntitiesListTableState;
    }

    public String getRetetentionRateAccountsListTableState() {
        return retetentionRateAccountsListTableState;
    }

    public void setRetetentionRateAccountsListTableState(String retetentionRateAccountsListTableState) {
        this.retetentionRateAccountsListTableState = retetentionRateAccountsListTableState;
    }

    @SuppressWarnings("unchecked")
    public List<BusinessEntity> getBusinessEntitiesList() {
        if (this.businessEntitiesList == null) {
            toogleOpened1 = true;
            toogleOpened2 = false;
            Query query = this.getEntityManager().createQuery("FROM  BusinessEntity be WHERE be.isBiller = ?");
            query.setParameter(1, true);
            this.businessEntitiesList = query.getResultList();
            if (this.businessEntitiesList.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Busqueda", "No se encontraron terceros (facturadores)"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Busqueda", "Se encontraron " + this.businessEntitiesList.size() + " terceros facturadores"));
            }
        }
        return businessEntitiesList;
    }

    public void setBusinessEntitiesList(List<BusinessEntity> businessEntitiesList) {
        this.businessEntitiesList = businessEntitiesList;
    }

    public List<RetentionRateAccount> getRetentionRateAccountsList() {
        return retentionRateAccountsList;
    }

    public void setRetentionRateAccountsList(List<RetentionRateAccount> retentionRateAccountsList) {
        this.retentionRateAccountsList = retentionRateAccountsList;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public HtmlExtendedDataTable getHtmlBusinessEntitiesRRADataTable() {
        return htmlBusinessEntitiesRRADataTable;
    }

    public void setHtmlBusinessEntitiesRRADataTable(HtmlExtendedDataTable htmlBusinessEntitiesRRADataTable) {
        this.htmlBusinessEntitiesRRADataTable = htmlBusinessEntitiesRRADataTable;
    }

    public HtmlExtendedDataTable getHtmlRetetentionRateAccountsRRADataTable() {
        return htmlRetetentionRateAccountsRRADataTable;
    }

    public void setHtmlRetetentionRateAccountsRRADataTable(HtmlExtendedDataTable htmlRetetentionRateAccountsRRADataTable) {
        this.htmlRetetentionRateAccountsRRADataTable = htmlRetetentionRateAccountsRRADataTable;
    }

    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }

    public boolean isToogleOpened1() {
        return toogleOpened1;
    }

    public void setToogleOpened1(boolean toogleOpened1) {
        this.toogleOpened1 = toogleOpened1;
    }

    public boolean isToogleOpened2() {
        return toogleOpened2;
    }

    public void setToogleOpened2(boolean toogleOpened2) {
        this.toogleOpened2 = toogleOpened2;
    }

    public List<RetentionRate> getRetentionRates() {
        return retentionRates;
    }

    public void setRetentionRates(List<RetentionRate> retentionRates) {
        this.retentionRates = retentionRates;
    }

    public List<SelectItem> getNatures() {
        return natures;
    }

    public void setNatures(List<SelectItem> natures) {
        this.natures = natures;
    }

    public int getBankId() {
        return RetentionRate.RETENTION_RATE_BANK_ACCOUNT;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getButtonLabel() {
        return this.operation == 0 ? "Agregar" : "Guardar";
    }

    public RetentionRate getCategory() {

        if (this.instance != null && this.instance.getRetentionRate() != null) {
            if (this.instance.getRetentionRate().getSubcategoryFrom() == null) {
                this.category = this.instance.getRetentionRate();
            } else {
                this.category = this.instance.getRetentionRate().getSubcategoryFrom();
            }
        }

        return this.category;
    }

    public void setCategory(RetentionRate raRetentionRate) {

        this.category = raRetentionRate;
    }

    public RetentionRate getSubCategory() {  
        if (this.subcategory == null && this.instance != null && this.instance.getRetentionRate() != null) {  
            if (this.instance.getRetentionRate().getSubcategoryFrom() != null) {
                this.subcategory = this.instance.getRetentionRate();
            } else {
                this.subcategory = null;
            } 
        }

        return this.subcategory;
    }

    public void setSubCategory(RetentionRate raRetentionRate) {
	    this.subcategory = raRetentionRate;
    }

    public List<RetentionRate> getSubCategories() {
        if (this.category != null) {
            if (this.getCategory().getSubcategories().isEmpty()) {
                this.subcategory = null;
                return null;
            } else {
                if (this.instance.getRetentionRate() != null && this.instance.getRetentionRate().getSubcategoryFrom() != null && this.getSubCategory() == null){
                    this.subcategory = this.instance.getRetentionRate();
                }
                else if (this.getSubCategory() == null){
                    this.subcategory = this.category.getSubcategories().get(0);
                }
                return this.category.getSubcategories();
            }
        } else {
            return null;
        }
    }

    public boolean isBankDescriptionRendered() {

        if (this.getCategory() != null && this.getCategory().getId() == RetentionRate.RETENTION_RATE_BANK_ACCOUNT) {
            return true;
        } else if (this.subcategory != null && this.subcategory.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isPercentValueRendered() {

        if (this.getCategory() != null && this.getCategory().isRetantion()) {
            return true;
        } else if (this.category != null && this.getCategory().getId() == RetentionRate.RETENTION_RATE_MEMORANDUM_ACCOUNTS && this.getSubCategory()!=null && this.getSubCategory().isRetantion()){
            return true;
        }
	        
        return false; 
    }
    
    public boolean isCodePayRendered() {

    	 if (this.category != null && this.category.getId() == RetentionRate.RETENTION_RATE_ACCOUNTS_RECEIVABLE) {
            return true;
        } else if (this.subcategory != null && this.subcategory.getId() == RetentionRate.RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR) {
            return true;
        } else {
            return false;
        }
    }

    public String getCategoryName() {
        return this.getCategory() == null ? "" : this.getCategory().getName();
    }

    public String getSubCategoryName() {
        return this.getSubCategory() == null ? "" : this.getSubCategory().getName();
    }

}
