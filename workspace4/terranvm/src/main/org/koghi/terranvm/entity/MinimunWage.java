package org.koghi.terranvm.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "minimum_wage", schema = "public")
public class MinimunWage {

	private int id;
	private double value;
	private int year;
	private Date date;

	public MinimunWage() {
		this.year = Calendar.getInstance().get(Calendar.YEAR);
		this.date = new Date();
	}

	@Id
	@SequenceGenerator(name = "pk_sequence", sequenceName = "minimum_wage_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "value")
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Column(name = "year")
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Column(name = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * metodo que obtiene cada uno de los Salarios minimos que se encuetren en
	 * la base de datos.
	 * 
	 * @param entityManager
	 * @return listado de salarios minimos
	 */
	@Transient
	public String getMinimumWages(EntityManager entityManager) {
		StringBuilder minimumWageString = new StringBuilder();
		minimumWageString.append("var SM = new Array(); ");
		String sql = "SELECT year, value FROM  minimum_wage order by year";
		Query query = entityManager.createNativeQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> minimumWasteList = query.getResultList();
		int lastyear = 0;
		float lastSM = 0;
		for (Object[] minWaste : minimumWasteList) {

			lastyear = (Integer) minWaste[0];
			minimumWageString.append("SM [").append(lastyear).append("] = ").append((Double) minWaste[1]).append("; ");
		}
		if (lastyear == 0) {
			lastyear = Calendar.getInstance().get(Calendar.YEAR) - 1;
		}
		for (int year = lastyear; year < Calendar.getInstance().get(Calendar.YEAR); year++) {
			minimumWageString.append("SM [").append((year + 1)).append("] = ").append(lastSM).append("; ");
		}
		return minimumWageString.toString();
	}

}
