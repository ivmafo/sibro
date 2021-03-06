package org.koghi.terranvm.entity;

// Generated 24-ene-2011 15:17:43 by Hibernate Tools 3.4.0.Beta1


import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.NotNull;

/**
 * BillingResolution generated by hbm2java
 */
@Audited
@Entity
@Table(name = "billing_resolution", schema = "public")
public class BillingResolution implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private BusinessEntity businessEntity;
	private BillingType billingType;
	private String prefix;
	private int min;
	private int max;
	private int current;
	private String dianNumber;
	private Date resolutionDate;
	private String siigoCode;
	private Date endDate;
	private String observation;

	public BillingResolution() {
	}

	public BillingResolution(int id, BusinessEntity businessEntity, int min,
			int max, int current, String dianNumber, Date resolutionDate,String siigoCode, Date endDate) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.min = min;
		this.max = max;
		this.current = current;
		this.dianNumber = dianNumber;
		this.resolutionDate = resolutionDate;
		this.siigoCode = siigoCode;
		this.endDate = endDate;
	}

	public BillingResolution(int id, BillingType billingType, BusinessEntity businessEntity,
			String prefix,  int min, int max, int current,
			String dianNumber, Date resolutionDate, Date endDate , String observation) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.billingType = billingType;
		this.prefix = prefix;
		this.min = min;
		this.max = max;
		this.current = current;
		this.dianNumber = dianNumber;
		this.resolutionDate = resolutionDate;
		this.endDate = endDate;
		this.observation = observation;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "pk_sequence", sequenceName = "billing_resolution_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_entity", nullable = false)
	@NotNull
	public BusinessEntity getBusinessEntity() {
		return this.businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_type")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public BillingType getBillingType() {
		return this.billingType;
	}

	public void setBillingType(BillingType billingType) {
		this.billingType = billingType;
	}

	@Column(name = "prefix")
	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Column(name = "min", nullable = false)
	public int getMin() {
		return this.min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	@Column(name = "max", nullable = false)
	public int getMax() {
		return this.max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@Column(name = "current", nullable = false)
	public int getCurrent() {
		return this.current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	@Column(name = "dian_number", nullable = false)
	@NotNull
	public String getDian_Number() {
		return this.dianNumber;
	}

	public void setDian_Number(String dianNumber) {
		this.dianNumber = dianNumber;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "resolution_date", nullable = false, length = 13)
	@NotNull
	public Date getResolutionDate() {
		return this.resolutionDate;
	}

	public void setResolutionDate(Date resolutionDate) {
		this.resolutionDate = resolutionDate;

	}
	
	@Column(name = "siigo_code")
	public String getSiigoCode() {
		return this.siigoCode;
	}

	public void setSiigoCode(String siigoCode) {
		this.siigoCode = siigoCode;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date", nullable = false, length = 13)
	@NotNull
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name = "observation")
	public String getObservation() {
		return this.observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}
	
	
}
