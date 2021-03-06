package org.koghi.terranvm.entity;

// Generated 11/07/2011 02:40:41 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.NotNull;

/**
 * SystemConfiguration generated by hbm2java
 */
@Entity
@Table(name = "system_configuration", schema = "public")
public class SystemConfiguration implements java.io.Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String Carpeta_PDF = "Carpeta_PDF";
    public static final String Carpeta_SIIGO = "Carpeta_SIIGO";
    public static final String BOTON_ACTUALIZAR_NEW_BALANCE = "mostrar_boton_actualizar_new_balance";
	
	
	private int id;
	private String name;
	private String value;

	public SystemConfiguration() {
	}

	public SystemConfiguration(int id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	@Id
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

	@Column(name = "value", nullable = false)
	@NotNull
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
