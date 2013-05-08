package org.koghi.terranvm.entity;

// Generated 29/03/2011 05:35:37 PM by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.NotNull;
/**
 * Recover generated by hbm2java
 */
@Entity
@Table(name = "recover", schema = "public")
public class Recover implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int AVAIBLE_PAY_FORM = 1;
	public static final int CHECK_pay_form = 2; 
	
	private int id;
	private double value;
	private Date date;
	private PayFormType payFormType;
	private String office;
	private String payIdentification;
	private String payerIdentification;
	private List<RecoverConcept> recoverConcepts = new ArrayList<RecoverConcept>();
	private double surplusValue;
	private String prefixCollectionAccounts;
	private String suffixCollectionAccounts;
	private Integer minCollectionAccounts;

	public Recover() {
	}

	public Recover(int id, double value, Date date) {
		this.id = id;
		this.value = value;
		this.date = date;
	}

	public Recover(int id, double value, Date date, List<RecoverConcept> recoverConcepts, PayFormType payFormType, String office, String payIdentification,String payerIdentification, String prefixCollectionAccounts, String suffixCollectionAccounts, Integer minCollectionAccounts) {
		this.id = id;
		this.value = value;
		this.date = date;
		this.payFormType = payFormType;
		this.office = office;
		this.payIdentification = payIdentification;
		this.payerIdentification = payerIdentification;
		this.recoverConcepts = recoverConcepts;
		this.prefixCollectionAccounts = prefixCollectionAccounts;
		this.suffixCollectionAccounts = suffixCollectionAccounts;
		this.minCollectionAccounts = minCollectionAccounts;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "pk_sequence", sequenceName = "recover_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "value", nullable = false, precision = 17, scale = 17)
	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "date", nullable = false, length = 13)
	@NotNull
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "recover", cascade = CascadeType.ALL)
	public List<RecoverConcept> getRecoverConcepts() {
		return this.recoverConcepts;
	}

	public void setRecoverConcepts(List<RecoverConcept> recoverConcepts) {
		this.recoverConcepts = recoverConcepts;
	}

	@Column(name = "surplus_value", nullable = false)
	public double getSurplusValue() {
		return surplusValue;
	}

	public void setSurplusValue(double surplusValue) {
		this.surplusValue = surplusValue;
	}
	
	@Column(name = "office")
	public String getOffice() {
		return this.office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	@Column(name = "pay_identification")
	public String getPayIdentification() {
		return this.payIdentification;
	}

	public void setPayIdentification(String payIdentification) {
		this.payIdentification = payIdentification;
	}

	@Column(name = "payer_identification")
	public String getPayerIdentification() {
		return this.payerIdentification;
	}

	public void setPayerIdentification(String payerIdentification) {
		this.payerIdentification = payerIdentification;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_form_type", nullable = false)
	@NotNull
	public PayFormType getPayFormType() {
		return this.payFormType;
	}

	public void setPayFormType(PayFormType payFormType) {
		this.payFormType = payFormType;
	}
	
	@Column(name = "prefix_collection_accounts")
	public String getPrefixCollectionAccounts() {
		return this.prefixCollectionAccounts;
	}

	public void setPrefixCollectionAccounts(String prefixCollectionAccounts) {
		this.prefixCollectionAccounts = prefixCollectionAccounts;
	}

	@Column(name = "suffix_collection_accounts")
	public String getSuffixCollectionAccounts() {
		return this.suffixCollectionAccounts;
	}

	public void setSuffixCollectionAccounts(String suffixCollectionAccounts) {
		this.suffixCollectionAccounts = suffixCollectionAccounts;
	}

	@Column(name = "min_collection_accounts")
	public Integer getMinCollectionAccounts() {
		return this.minCollectionAccounts;
	}

	public void setMinCollectionAccounts(Integer minCollectionAccounts) {
		this.minCollectionAccounts = minCollectionAccounts;
	}

}
