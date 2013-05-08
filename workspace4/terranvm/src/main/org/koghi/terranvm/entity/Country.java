package org.koghi.terranvm.entity;

// Generated 05-ene-2011 12:42:23 by Hibernate Tools 3.4.0.Beta1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.validator.NotNull;

/**
 * Country generated by hbm2java
 */
@Entity
@Table(name = "country", schema = "public")
public class Country implements java.io.Serializable {

	private int id;
	private String name;
	private String abbreviation;
	private Set<Region> regions = new HashSet<Region>(0);

	public Country() {
	}

	public Country(int id, String name, String abbreviation) {
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public Country(int id, String name, String abbreviation, Set<Region> regions) {
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
		this.regions = regions;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
		@SequenceGenerator(name = "pk_sequence", sequenceName = "country_id_seq", allocationSize = 1)
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

	@Column(name = "abbreviation", nullable = false)
	@NotNull
	public String getAbbreviation() {
		return this.abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
	@OrderBy("name")
	public Set<Region> getRegions() {
		return this.regions;
	}

	public void setRegions(Set<Region> regions) {
		this.regions = regions;
	}

}
