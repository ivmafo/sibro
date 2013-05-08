package org.koghi.terranvm.entity;

// Generated 06-ene-2011 11:10:18 by Hibernate Tools 3.4.0.Beta1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.validator.NotNull;

/**
 * AreaType generated by hbm2java
 */

@Entity
@Table(name = "area_type", schema = "public")
public class AreaType implements java.io.Serializable {

	private int id;
	private String name;
	private Set<Area> areas = new HashSet<Area>(0);

	public AreaType() {
	}

	public AreaType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public AreaType(int id, String name, Set<Area> areas) {
		this.id = id;
		this.name = name;
		this.areas = areas;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "pk_sequence", sequenceName = "area_type_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "areaType")
	public Set<Area> getAreas() {
		return this.areas;
	}

	public void setAreas(Set<Area> areas) {
		this.areas = areas;
	}

}
