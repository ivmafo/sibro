package org.koghi.terranvm.entity;

// Generated 17/03/2011 03:55:04 PM by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * ConceptRetentionRateAccount generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "concept_retention_rate_account", schema = "public")
public class ConceptRetentionRateAccount implements java.io.Serializable, Comparable<ConceptRetentionRateAccount> {

	private int id;
	private Concept concept;
	private RetentionRateAccount retentionRateAccount;

	public ConceptRetentionRateAccount() {
	}

	public ConceptRetentionRateAccount(int id) {
		this.id = id;
	}

	public ConceptRetentionRateAccount(int id, Concept concept, RetentionRateAccount retentionRateAccount) {
		this.id = id;
		this.concept = concept;
		this.retentionRateAccount = retentionRateAccount;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "pk_sequence", sequenceName = "concept_retention_rate_account_increment_seg", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concept")
	public Concept getConcept() {
		return this.concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "retention_rate_account")
	public RetentionRateAccount getRetentionRateAccount() {
		return this.retentionRateAccount;
	}

	public void setRetentionRateAccount(RetentionRateAccount retentionRateAccount) {
		this.retentionRateAccount = retentionRateAccount;
	}

	public int compareTo(ConceptRetentionRateAccount conceptRetentionRateAccount) {
		if (this.retentionRateAccount.getRetentionRate().getId() < conceptRetentionRateAccount.getRetentionRateAccount().getRetentionRate().getId())
			return -1;
		else if (this.retentionRateAccount.getRetentionRate().getId() < conceptRetentionRateAccount.getRetentionRateAccount().getRetentionRate().getId())
			return 1;
		
		return 0;
	}

}
