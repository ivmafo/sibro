package org.koghi.terranvm.entity;

// Generated 10/03/2011 05:39:00 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.NotNull;

/**
 * BillingType generated by hbm2java
 */
@Entity
@Table(name = "billing_type", schema = "public")
public class BillingType implements java.io.Serializable {

	private int id;
	private String type;
	private Set<BillingResolution> billingResolutions = new HashSet<BillingResolution>(
			0);

	public BillingType() {
	}

	public BillingType(int id, String type) {
		this.id = id;
		this.type = type;
	}

	public BillingType(int id, String type,
			Set<BillingResolution> billingResolutions) {
		this.id = id;
		this.type = type;
		this.billingResolutions = billingResolutions;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "type", nullable = false)
	@NotNull
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "billingType")
	public Set<BillingResolution> getBillingResolutions() {
		return this.billingResolutions;
	}

	public void setBillingResolutions(Set<BillingResolution> billingResolutions) {
		this.billingResolutions = billingResolutions;
	}

}