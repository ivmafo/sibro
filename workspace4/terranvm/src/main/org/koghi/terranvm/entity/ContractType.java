package org.koghi.terranvm.entity;

// Generated 24-ene-2011 9:16:15 by Hibernate Tools 3.4.0.Beta1

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.NotNull;

/**
 * ContractType generated by hbm2java
 */
@Audited
@Entity
@Table(name = "contract_type", schema = "public")
public class ContractType implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int LEASE_TYPE = 1;
	
	private int id;
	private String name;
	private String description;
	private Set<ProjectProperty> projectProperties = new HashSet<ProjectProperty>(0);

	public ContractType() {
	}

	public ContractType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public ContractType(int id, String name, String description, Set<ProjectProperty> projectProperties) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.projectProperties = projectProperties;
	}

	@Id
	@SequenceGenerator(name = "pk_sequence", sequenceName = "contract_type_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false)
	@NotNull
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contractType")
	public Set<ProjectProperty> getProjectProperties() {
		return this.projectProperties;
	}

	public void setProjectProperties(Set<ProjectProperty> projectProperties) {
		this.projectProperties = projectProperties;
	}

}