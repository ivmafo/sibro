package org.koghi.terranvm.entity;

// Generated 18/03/2011 07:33:11 PM by Hibernate Tools 3.4.0.Beta1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;

/**
 * RetentionRateAccount generated by hbm2java
 */
@Entity
@Table(name = "retention_rate_account", schema = "public")
public class RetentionRateAccount implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;   
	private int id;
	private RetentionRate retentionRate;
	private String name;
	private String account;
	private Double value;
	private int accountsReceivableCode = 0;
	private BusinessEntity biller;
	private Set<ConceptRetentionRateAccount> conceptRetentionRateAccounts = new HashSet<ConceptRetentionRateAccount>(0);
	
	@Transient
	public static final char NATURALEZA_DEBITO = 'D';
	@Transient
	public static final char NATURALEZA_CREDITO = 'C';
	@Transient
    public static final char NATURALEZA_NA = '0';
	@Transient
	public static final String[][] NATURES = { { NATURALEZA_NA+"", "N/A" }, { NATURALEZA_CREDITO+"", "Crédito" }, { NATURALEZA_DEBITO+"", "Débito"} };
	
	private char natureBilling;
	private char natureRecover;
	private char natureCreditNote;
	private String description;
	
	
	public RetentionRateAccount() {
	}

	public RetentionRateAccount(int id) {
		this.id = id;
	}

	public RetentionRateAccount(int id, RetentionRate retentionRate, String name, String account, int accountsReceivableCode, BusinessEntity businessEntity, Set<ConceptRetentionRateAccount> conceptRetentionRateAccounts, Double value, char natB, char natR, char natCN, String desc) {
		this.id = id;
		this.retentionRate = retentionRate;
		this.name = name;
		this.account = account;
		this.conceptRetentionRateAccounts = conceptRetentionRateAccounts;
		this.value = value;
		this.accountsReceivableCode = accountsReceivableCode;
		this.biller = businessEntity;
		this.natureBilling = natB;
		this.natureRecover = natR;
		this.natureCreditNote = natCN;
		this.description = desc;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "pk_sequence", sequenceName = "retention_rate_account_increment_seg", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "retention_rate")
	public RetentionRate getRetentionRate() {
		return this.retentionRate;
	}

	public void setRetentionRate(RetentionRate retentionRate) {
		this.retentionRate = retentionRate;
	}

	@Column(name = "name", length = 50)
	@Length(max = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "account", length = 100)
	@Length(max = 100)
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "value", precision = 17, scale = 17)
	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	@Column(name = "accounts_receivable_code")
	public int getAccountsReceivableCode() {
		return this.accountsReceivableCode;
	}

	public void setAccountsReceivableCode(int accountsReceivableCode) {
		this.accountsReceivableCode = accountsReceivableCode;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "biller")
	public BusinessEntity getBiller() {
		return this.biller;
	}

	public void setBiller(BusinessEntity businessEntity) {
		this.biller = businessEntity;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "retentionRateAccount")
	public Set<ConceptRetentionRateAccount> getConceptRetentionRateAccounts() {
		return this.conceptRetentionRateAccounts;
	}

	public void setConceptRetentionRateAccounts(Set<ConceptRetentionRateAccount> conceptRetentionRateAccounts) {
		this.conceptRetentionRateAccounts = conceptRetentionRateAccounts;
	}

	@Column(name = "nature_billing")
    public char getNatureBilling() {
        return natureBilling;
    }

    public void setNatureBilling(char natureBilling) {
        this.natureBilling = natureBilling;
    }

    @Column(name = "nature_recover")
    public char getNatureRecover() {
        return natureRecover;
    }

    public void setNatureRecover(char natureRecover) {
        this.natureRecover = natureRecover;
    }

    @Column(name = "nature_credit_note")
    public char getNatureCreditNote() {
        return natureCreditNote;
    }

    public void setNatureCreditNote(char natureCreditNote) {
        this.natureCreditNote = natureCreditNote;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
