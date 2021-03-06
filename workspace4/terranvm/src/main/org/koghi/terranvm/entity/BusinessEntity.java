
package org.koghi.terranvm.entity;

// Generated 05-ene-2011 12:42:23 by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.Email;
import org.hibernate.validator.NotNull;

/**
 * BusinessEntity generated by hbm2java
 */
@Audited
@Entity
@Table(name = "business_entity", schema = "public")
public class BusinessEntity implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6288977829090240499L;
	private int id;
	private EconomicActivity economicActivity;
	private short idType;
	private String idNumber;
	private int verificationNumber;
	private String nameBusinessEntity;
	private String tradeName;
	private String email;
	private String legalEntityType;
	private String imagePath;
	private String billerObservation;
	private boolean isBiller;
	private byte[] logo;
	private String firstCopy;
	private String original;
	private String secondCopy;
	private Set<PhoneNumber> phoneNumbers = new HashSet<PhoneNumber>(0);
	private Set<Address> addresses = new HashSet<Address>(0);
	private Set<Project> projects = new HashSet<Project>(0);
	private List<BusinessEntityType> entityTypes = new ArrayList<BusinessEntityType>();
	private Set<BusinessLine> businessLines = new HashSet<BusinessLine>(0);
	private Set<BusinessEntityContact> businessEntityContacts = new HashSet<BusinessEntityContact>(0);
	private Set<ConsecutiveCreditNotes> consecutiveCreditNoteses = new HashSet<ConsecutiveCreditNotes>(0);
	private Set<ConsecutiveAccountsBilling> consecutiveAccountsBillings = new HashSet<ConsecutiveAccountsBilling>(0);
	private List<BillingResolution> billingResolutions = new ArrayList<BillingResolution>();
	private City city;
	private Set<ConsecutiveCollectionAccount> consecutiveCollectionAccountsList = new HashSet<ConsecutiveCollectionAccount>();
	private String billerObservation2;
	

	//	@Transient
//	private BusinessEntityType typeSearch = new BusinessEntityType();
	private String taxLiabilities;
	private List<Contact> contactos = new ArrayList<Contact>();

	public void setContactos(List<Contact> contactos) {
		this.contactos = contactos;
	}

	@Column(name = "tax_liabilities")
	public String getTaxLiabilities() {
		return taxLiabilities;
	}

	public void setTaxLiabilities(String taxLiabilities) {
		this.taxLiabilities = taxLiabilities;
	}

	public BusinessEntity() {
	}

	public BusinessEntity(int id, EconomicActivity economicActivity, BusinessEntity businessEntity, short idType, String idNumber, String nameBusinessEntity, String tradeName, String legalEntityType, boolean isBiller) {
		this.id = id;
		this.economicActivity = economicActivity;
		this.idType = idType;
		this.idNumber = idNumber;
		this.nameBusinessEntity = nameBusinessEntity;
		this.tradeName = tradeName;
		this.legalEntityType = legalEntityType;
		this.isBiller = isBiller;
	}

	public BusinessEntity(int id, EconomicActivity economicActivity, BusinessEntity businessEntity, short idType, String idNumber, int verificationNumber, String nameBusinessEntity, String tradeName, String email, String legalEntityType, byte[] logo, Set<BusinessEntity> businessEntities, List<BillingResolution> billingResolutions, Set<PhoneNumber> phoneNumbers, Set<Address> addresses, Set<Project> projects, List<BusinessEntityType> entityTypes, Set<BusinessLine> businessLines, Set<BusinessEntityContact> businessEntityContacts, Set<ConsecutiveAccountsBilling> consecutiveAccountsBillings,Set<ConsecutiveCreditNotes> consecutiveCreditNoteses,City city, String billerObservation, boolean isBiller, Set<ConsecutiveCollectionAccount> consecutiveCollectionAccounts, String firstCopy, String original, String secondCopy){
		this.id = id;
		this.economicActivity = economicActivity;
		this.idType = idType;
		this.idNumber = idNumber;
		this.verificationNumber = verificationNumber;
		this.nameBusinessEntity = nameBusinessEntity;
		this.tradeName = tradeName;
		this.email = email;
		this.legalEntityType = legalEntityType;
		this.logo=logo;
		this.phoneNumbers = phoneNumbers;
		this.addresses = addresses;
		this.projects = projects;
		this.entityTypes = entityTypes;
		this.businessLines = businessLines;
		this.businessEntityContacts = businessEntityContacts;
		this.consecutiveAccountsBillings = consecutiveAccountsBillings;
		this.consecutiveCreditNoteses = consecutiveCreditNoteses;
		this.billingResolutions = billingResolutions;
		this.city = city;
		this.billerObservation = billerObservation;
		this.isBiller = isBiller;
		this.consecutiveCollectionAccountsList = consecutiveCollectionAccounts;
		this.firstCopy = firstCopy;
		this.original = original;
		this.secondCopy = secondCopy;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "pk_sequence", sequenceName = "business_entity_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "economic_activity")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public EconomicActivity getEconomicActivity() {
		return this.economicActivity;
	}

	public void setEconomicActivity(EconomicActivity economicActivity) {
		this.economicActivity = economicActivity;
	}

	@Column(name = "id_type", nullable = false)
	public short getIdType() {
		return this.idType;
	}

	public void setIdType(short idType) {
		this.idType = idType;
	}

	@Column(name = "id_number", nullable = false)
	@NotNull
	public String getIdNumber() {
		return this.idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	@Column(name = "verification_number")
	public int getVerificationNumber() {
		return this.verificationNumber;
	}

	public void setVerificationNumber(int verificationNumber) {
		this.verificationNumber = verificationNumber;
	}

	@Column(name = "name_business_entity", nullable = false)
	@NotNull
	@OrderBy ("name")
	public String getNameBusinessEntity() {
		return this.nameBusinessEntity;
	}

	public void setNameBusinessEntity(String nameBusinessEntity) {
		this.nameBusinessEntity = nameBusinessEntity;
	}

	@Column(name = "trade_name")
	public String getTradeName() {
		return this.tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	@Column(name = "email")
	@Email
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "legal_entity_type", nullable = false)
	@NotNull
	public String getLegalEntityType() {
		return this.legalEntityType;
	}

	public void setLegalEntityType(String legalEntityType) {
		this.legalEntityType = legalEntityType;
	}

	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	@Column(name = "biller_observation")
	public String getBillerObservation() {
		return this.billerObservation;
	}

	public void setBillerObservation(String billerObservation) {
		this.billerObservation = billerObservation;
	}

	@Column(name = "is_biller", nullable = false)
	public boolean isIsBiller() {
		return this.isBiller;
	}

	public void setIsBiller(boolean isBiller) {
		this.isBiller = isBiller;
	}

	@Column(name = "logo")
	public byte[] getLogo() {
		return this.logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public List<BillingResolution> getBillingResolutions() {
		return this.billingResolutions;
	}

	public void setBillingResolutions(List<BillingResolution> billingResolutions) {
		this.billingResolutions = billingResolutions;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "businessEntity")
	public Set<PhoneNumber> getPhoneNumbers() {
		return this.phoneNumbers;
	}

	public void setPhoneNumbers(Set<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "businessEntity")
	public Set<Address> getAddresses() {
		return this.addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	@ManyToMany(targetEntity = org.koghi.terranvm.entity.BusinessEntityType.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "ENTITY_TYPE", joinColumns = @JoinColumn(name = "BUSINESS_ENTITY"), inverseJoinColumns = @JoinColumn(name = "TYPE"))
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public List<BusinessEntityType> getEntityTypes() {
		return this.entityTypes;
	}

	public void setEntityTypes(List<BusinessEntityType> entityTypes) {
		this.entityTypes = entityTypes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "businessEntity")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public Set<BusinessLine> getBusinessLines() {
		return this.businessLines;
	}

	public void setBusinessLines(Set<BusinessLine> businessLines) {
		this.businessLines = businessLines;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "businessEntity")
	public Set<BusinessEntityContact> getBusinessEntityContacts() {
		return this.businessEntityContacts;
	}

	public void setBusinessEntityContacts(Set<BusinessEntityContact> businessEntityContacts) {
		this.businessEntityContacts = businessEntityContacts;
	}

	@Transient
	public String[] getResponsabilidadesTributarias() {
		return this.taxLiabilities.split(",");
	}

	@ManyToMany(targetEntity = Contact.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "business_entity_contact", joinColumns = @JoinColumn(name = "business_entity"), inverseJoinColumns = @JoinColumn(name = "contact"))
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public List<Contact> getContactos() {
		return this.contactos;
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "businessEntity")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public Set<ConsecutiveCreditNotes> getConsecutiveCreditNoteses() {
		return this.consecutiveCreditNoteses;
	}

	public void setConsecutiveCreditNoteses(
			Set<ConsecutiveCreditNotes> consecutiveCreditNoteses) {
		this.consecutiveCreditNoteses = consecutiveCreditNoteses;
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "businessEntity")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public Set<ConsecutiveCollectionAccount> getConsecutiveCollectionAccountsList() {
		return consecutiveCollectionAccountsList;
	}

	public void setConsecutiveCollectionAccountsList(Set<ConsecutiveCollectionAccount> consecutiveCollectionAccountsList) {
		this.consecutiveCollectionAccountsList = consecutiveCollectionAccountsList;
	}
	
	@Transient
	public List<ConsecutiveCollectionAccount> getConsecutiveCollectionAccounts() {
		return consecutiveCollectionAccountsList == null ? null : new ArrayList<ConsecutiveCollectionAccount>(consecutiveCollectionAccountsList);
	}

	public void setConsecutiveCollectionAccounts(List<ConsecutiveCollectionAccount> consecutiveCollectionAccounts) {
		this.consecutiveCollectionAccountsList = new HashSet<ConsecutiveCollectionAccount>(consecutiveCollectionAccounts);
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "businessEntity")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public Set<ConsecutiveAccountsBilling> getConsecutiveAccountsBillings() {
		return this.consecutiveAccountsBillings;
	}

	public void setConsecutiveAccountsBillings(
			Set<ConsecutiveAccountsBilling> consecutiveAccountsBillings) {
		this.consecutiveAccountsBillings = consecutiveAccountsBillings;
	}
	
	public String idNumber() {
		return this.getIdNumber();
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="city") 
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
    public City getCity() {
        return this.city;
    }
    
    public void setCity(City city) {
        this.city = city;
    }
    
    
    @Column(name = "first_copy")
	public String getFirstCopy() {
		return this.firstCopy;
	}

	public void setFirstCopy(String firstCopy) {
		this.firstCopy = firstCopy;
	}

	@Column(name = "original")
	public String getOriginal() {
		return this.original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	@Column(name = "second_copy")
	public String getSecondCopy() {
		return this.secondCopy;
	}

	public void setSecondCopy(String secondCopy) {
		this.secondCopy = secondCopy;
	}
	
	/**
	* Método que retorna las responsabilidades tributarias del tercero de regimen común,
	* Gran Contibuyente, Autoretenedor, Regimen simplificado y sin regimen 
	* @return Obligacion Tributario del tercero para la configuración de matriz de impuestos
	*/	
	@Transient
	public List<Integer> getTaxLiability() {
		/**
		 * Se obtiene la configuraciÃ³n Tributaria y se cicla por ella.
		 */
		String configuration[] = this.getTaxLiabilities().split(",");
		
		List<Integer> taxliabilities = new ArrayList<Integer>();
		
		
		for (int i=0;i<configuration.length;i++ ){
			/**
			 * Se Verifica que ese configuracion tributaria sea correcta
			 */
			if (configuration[i] != null && !configuration[i].equals("")){
				Integer conf= Integer.parseInt(configuration[i]) ;
				taxliabilities.add(conf);
			}
		}
		
		taxliabilities = Taxfilter(taxliabilities);
		return taxliabilities;		
	}

	/**
	 * Retorna las responsabilidades tributarias que predominan sobre ptras
	 * @param taxliabilities todas las responsabilidades tributarias del tercero
	 */
	private List<Integer> Taxfilter(List<Integer> taxliabilities) {
		
		if (taxliabilities.contains(15) && taxliabilities.contains(13)){
			taxliabilities.remove(new Integer(13));
		} else if (taxliabilities.contains(11) && taxliabilities.contains(13)){
			taxliabilities.remove(new Integer(11));
		}
		
		if (taxliabilities.contains(15) || taxliabilities.contains(13)){
			taxliabilities.remove(new Integer(11));
			taxliabilities.remove(new Integer(12));
			taxliabilities.remove(new Integer(20));
			taxliabilities.remove(new Integer(23));
		}
		return taxliabilities;
	}
	
	@Column(name = "biller_observation2")
	public String getBillerObservation2() {
		return billerObservation2;
	}

	public void setBillerObservation2(String billerObservation2) {
		this.billerObservation2 = billerObservation2;
	}
	
}
