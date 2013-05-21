package org.koghi.terranvm.entity;

// Generated 17/03/2011 03:55:04 PM by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.Length;

/**
 * RetentionRate generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "retention_rate", schema = "public")
public class RetentionRate implements java.io.Serializable {
	
	public final static int RETENTION_RATE_RTEFUENTE=1;
	public final static int RETENTION_RATE_RTEIVA=2;
	public final static int RETENTION_RATE_IVA=3;
	public final static int RETENTION_RATE_RTEICA=4;
	public final static int RETENTION_RATE_TIMBRE=5;
	public final static int RETENTION_RATE_ACCOUNTS_RECEIVABLE=6;
	public final static int RETENTION_RATE_BANK_ACCOUNT=7;
	public final static int RETENTION_RATE_INCOME_ACCOUNT=8;
	public final static int RETENTION_RATE_PENALTY_OF_PORTAFOLIO=9;
	public final static int RETENTION_RATE_DISCOUNT=10;
	public final static int RETENTION_RATE_MEMORANDUM_ACCOUNTS=11;
	public final static int RETENTION_RATE_RTECREE=13;//CUENTAS DE ORDEN DEUDORA
	
	public final static int RETENTION_RATE_SUBCATEGORY_CUENTAS_POR_COBRAR = 1101;
	public final static int RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_BANCOS = 1102;
	public final static int RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_INGRESOS = 1103;
	public final static int RETENTION_RATE_SUBCATEGORY_CUENTAS_DE_IVA = 1104;
	public final static int RETENTION_RATE_SUBCATEGORY_RETEFUENTE = 1105;
	public final static int RETENTION_RATE_SUBCATEGORY_RETEIVA = 1106;
	public final static int RETENTION_RATE_SUBCATEGORY_RETEICA = 1107;
	public final static int RETENTION_RATE_SUBCATEGORY_OTROSINGRESOS = 1108;
	public final static int RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_INTERESES_DEUDAS_VENCIDAS = 1109;
	public final static int RETENTION_RATE_SUBCATEGORY_DEUDORAS_CONTROL_CONTRARIO = 1110;
	public final static int RETENTION_RATE_SUBCATEGORY_RETECREE= 1111;
	
	private int id;
	private String name;
	private boolean retantion;
	private Set<TaxConfiguration> taxConfigurations = new HashSet<TaxConfiguration>(0);	
	private Set<RetentionRateAccount> retentionRateAccounts = new HashSet<RetentionRateAccount>(0);
	private RetentionRate subcategoryFrom;
	private List<RetentionRate> subcategories = new ArrayList<RetentionRate>();
	

	public RetentionRate() {
	}

	public RetentionRate(int id) {
		this.id = id;
	}

	public RetentionRate(int id, String name, Set<TaxConfiguration> taxConfigurations, Set<RetentionRateAccount> retentionRateAccounts, boolean retantion) {
		this.id = id;
		this.name = name;
		this.taxConfigurations = taxConfigurations;		
		this.retantion = retantion;
		this.retentionRateAccounts = retentionRateAccounts;
	
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", length = 50)
	@Length(max = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "retantion", nullable = false)
	public boolean isRetantion() {
		return this.retantion;
	}

	public void setRetantion(boolean retantion) {
		this.retantion = retantion;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "retentionRate")
	public Set<TaxConfiguration> getTaxConfigurations() {
		return this.taxConfigurations;
	}

	public void setTaxConfigurations(Set<TaxConfiguration> taxConfigurations) {
		this.taxConfigurations = taxConfigurations;
	}

	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "retentionRate")
	public Set<RetentionRateAccount> getRetentionRateAccounts() {
		return this.retentionRateAccounts;
	}

	public void setRetentionRateAccounts(Set<RetentionRateAccount> retentionRateAccounts) {
		this.retentionRateAccounts = retentionRateAccounts;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_from", nullable = true)
    public RetentionRate getSubcategoryFrom() {
        return subcategoryFrom;
    }

    public void setSubcategoryFrom(RetentionRate subcategoryFrom) {
        this.subcategoryFrom = subcategoryFrom;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subcategoryFrom")
    public List<RetentionRate> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<RetentionRate> subcategories) {
        this.subcategories = subcategories;
    }

	

}
