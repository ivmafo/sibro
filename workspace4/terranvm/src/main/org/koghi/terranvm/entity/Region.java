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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.validator.NotNull;

/**
 * Region generated by hbm2java
 */
@Entity
@Table(name = "region", schema = "public")
public class Region implements java.io.Serializable {

	private int id;
	private Country country;
	private String name;
	private String abbreviation;
	private Set<City> cities = new HashSet<City>(0);

	public Region() {
	}

	public Region(int id, Country country, String name, String abbreviation) {
		this.id = id;
		this.country = country;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	public Region(int id, Country country, String name, String abbreviation, Set<City> cities) {
		this.id = id;
		this.country = country;
		this.name = name;
		this.abbreviation = abbreviation;
		this.cities = cities;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
		@SequenceGenerator(name = "pk_sequence", sequenceName = "region_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id", nullable = false)
	@NotNull
	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "region")
	@OrderBy("name")
	public Set<City> getCities() {
		return this.cities;
	}

	public void setCities(Set<City> cities) {
		this.cities = cities;
	}

}
