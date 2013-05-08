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
 * RealPropertyContact generated by hbm2java
 */
@Audited
@Entity
@Table(name = "real_property_contact", schema = "public")
public class RealPropertyContact implements java.io.Serializable {

	private int id;
	private Contact contact;
	private RealProperty realProperty;

	public RealPropertyContact() {
	}

	public RealPropertyContact(int id, Contact contact, RealProperty realProperty) {
		this.id = id;
		this.contact = contact;
		this.realProperty = realProperty;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
		@SequenceGenerator(name = "pk_sequence", sequenceName = "real_property_contact_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "real_property", nullable = false)
	@NotNull
	public RealProperty getRealProperty() {
		return this.realProperty;
	}

	public void setRealProperty(RealProperty realProperty) {
		this.realProperty = realProperty;
	}

}