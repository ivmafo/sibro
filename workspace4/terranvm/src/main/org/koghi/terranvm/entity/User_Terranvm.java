package org.koghi.terranvm.entity;

// Generated 2/03/2011 03:36:38 PM by Hibernate Tools 3.4.0.CR1

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Email;
import org.hibernate.validator.NotNull;

/**
 * User generated by hbm2java
 */
@Entity
@Table(name = "user_terranvm", schema = "public")
public class User_Terranvm implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATE_INACTIVE = 0;
	public static final int STATE_ACTIVE = 1;
	//Arreglo que tiene los nombres de los estados de un usuario en español
	public static final String[] STRING_STATES = {"Inactivo","Activo"}; 
	
	private int id;
	private Role role;
	private String login;
	private String password;
	private Date effectiveDate;
	private int state;
	private String mail;
	private String nombre;
	private List<Project> projectUsers = new ArrayList<Project>(0);

	public User_Terranvm() {
	}

	public User_Terranvm(int id, Role role, String login, String password,
			Date effectiveDate, int state, String mail, String nombre) {
		this.id = id;
		this.role = role;
		this.login = login;
		this.password = password;
		this.effectiveDate = effectiveDate;
		this.state = state;
		this.mail = mail;
		this.nombre = nombre;
	}

	public User_Terranvm(int id, Role role, String login, String password,
			Date effectiveDate, int state, String mail, String nombre,
			List<Project> projectUsers) {
		this.id = id;
		this.role = role;
		this.login = login;
		this.password = password;
		this.effectiveDate = effectiveDate;
		this.state = state;
		this.mail = mail;
		this.nombre = nombre;
		this.projectUsers = projectUsers;
	}

	@Id
	@SequenceGenerator(name = "pk_sequence", sequenceName = "user_terranvm_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role", nullable = false)
	@NotNull
	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Column(name = "login", nullable = false)
	@NotNull
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "password", nullable = false)
	@NotNull
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "effective_date", length = 13)
	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Column(name = "state", nullable = false)
	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Column(name = "mail", nullable = false)
	@NotNull
	@Email
	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Column(name = "nombre", nullable = false)
	@NotNull
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@ManyToMany(targetEntity = org.koghi.terranvm.entity.Project.class, cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "project_user", joinColumns = @JoinColumn(name = "user_terranvm"), inverseJoinColumns = @JoinColumn(name = "project"))
	@OrderBy("nameProject")
	public List<Project> getProjectUsers() {
		return this.projectUsers;
	}

	public void setProjectUsers(List<Project> projectUsers) {
		this.projectUsers = projectUsers;
	}

}