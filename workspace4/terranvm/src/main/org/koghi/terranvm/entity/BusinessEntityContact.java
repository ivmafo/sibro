package org.koghi.terranvm.entity;

// Generated 05-ene-2011 12:42:23 by Hibernate Tools 3.4.0.Beta1

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

import org.hibernate.envers.Audited;
import org.hibernate.validator.NotNull;

/**
 * BusinessEntityContact generated by hbm2java
 */
@Audited
@Entity
@Table(name = "business_entity_contact", schema = "public")
public class BusinessEntityContact implements java.io.Serializable {

	private int id;
	private BusinessEntity businessEntity;
	private Contact contact;

	public BusinessEntityContact() {
	}

	public BusinessEntityContact(int id, BusinessEntity businessEntity, Contact contact) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.contact = contact;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
		@SequenceGenerator(name = "pk_sequence", sequenceName = "business_entity_contact_id_seq", allocationSize = 1)
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
	@JoinColumn(name = "contact", nullable = false)
	@NotNull
	public Contact getContact() {
		return this.contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

}
