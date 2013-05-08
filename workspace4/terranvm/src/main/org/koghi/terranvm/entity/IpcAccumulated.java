package org.koghi.terranvm.entity;

// Generated 2/05/2011 11:00:33 AM by Hibernate Tools 3.4.0.CR1

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.NotNull;

/**
 * IpcAccumulated generated by hbm2java
 */
@Entity
@Table(name = "ipc_accumulated", schema = "public")
public class IpcAccumulated implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Arreglo que representa los meses en español. La posición corresponde al
	 * mes que se hace referencia según la clase Calendar. Ejemplo, para obtener
	 * el mes de febrero Months[Calendar.FEBRUARY]
	 **/
	public static final String[] MONTHS = { "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIMEBRE", "OCTUBRE", "NOVIMEBRE", "DICIEMBRE" };

	private int id;
	private SystemVariable systemVariable;
	private Integer year;
	private Integer monthly;
	private Float value;

	public IpcAccumulated() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.add(Calendar.MONTH, -1);
		setMonthly(currentDate.get(Calendar.MONTH));
		setYear(currentDate.get(Calendar.YEAR));

	}

	public IpcAccumulated(int id) {
		this.id = id;
	}

	public IpcAccumulated(int id, SystemVariable systemVariable, Integer year, Integer monthly, Float value) {
		this.id = id;
		this.systemVariable = systemVariable;
		this.year = year;
		this.monthly = monthly;
		this.value = value;
	}

	@Id
	@SequenceGenerator(name = "pk_sequence", sequenceName = "ipc_accumulated_increment_seg", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ipc_accumulated")
	public SystemVariable getSystemVariable() {
		return this.systemVariable;
	}

	public void setSystemVariable(SystemVariable systemVariable) {
		this.systemVariable = systemVariable;
	}

	@Column(name = "year")
	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Column(name = "monthly")
	public Integer getMonthly() {
		return this.monthly;
	}

	public void setMonthly(Integer monthly) {
		this.monthly = monthly;
	}

	@Column(name = "value", precision = 8, scale = 8)
	@NotNull
	public Float getValue() {
		return this.value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	@Transient
	public static List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		int initYear = 2000;
		int endYear = 2030;

		for (int i = initYear; i <= endYear; i++) {
			years.add(i);
		}
		return years;
	}

	@Transient
	public String getIpcs(EntityManager entityManager) {
		String ipcString = "var IPCA = new Array(); ";
		String sql = " FROM IpcAccumulated  order by year,monthly ";
		Query query = entityManager.createQuery(sql);
		@SuppressWarnings("unchecked")
		List<IpcAccumulated> ipcAccumalatedList = (List<IpcAccumulated>) query.getResultList();
		Integer lastYear = 0, lastMonthly = 0;
		for (IpcAccumulated ipc : ipcAccumalatedList) {

			Integer monthly = (ipc.getYear() * 12) + ipc.getMonthly();
			ipcString += "IPCA [" + monthly + "] = " + ipc.getValue() + "; ";
			lastYear = ipc.getYear();
			lastMonthly = ipc.getMonthly();

		}
		/**
		 * Se fija la fecha del proximo perdio del IPC
		 */
		Calendar dateIpc = Calendar.getInstance();
		if (!ipcAccumalatedList.isEmpty()) {
			dateIpc.set(Calendar.YEAR, lastYear);
			dateIpc.set(Calendar.MONTH, lastMonthly);
			dateIpc.add(Calendar.MONTH, 1);
		} else {
			dateIpc.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);
			dateIpc.set(Calendar.MONTH, 0);
		}

		/*
		 * Se cicla has el mes actual y año actual.
		 */
		while (dateIpc.get(Calendar.YEAR) <= Calendar.getInstance().get(Calendar.YEAR)) {
			Integer monthly = ((dateIpc.get(Calendar.YEAR)) * 12) + (dateIpc.get(Calendar.MONTH));
			ipcString += "IPCA [" + monthly + "] = 0.0; ";
			dateIpc.add(Calendar.MONTH, 1);
		}

		// System.out.println("IPC Accumalado: "+ipcString);
		return ipcString;
	}

}
