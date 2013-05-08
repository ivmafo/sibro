package org.koghi.terranvm.entity;

// Generated 05-ene-2011 12:42:23 by Hibernate Tools 3.4.0.Beta1

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.NotNull;

/**
 * Project generated by hbm2java
 */

@Audited
@Entity
@Table(name = "project", schema = "public")
public class Project implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private BusinessEntity businessEntity;
	private BusinessLine businessLine;
	private String nameProject;
	private String costCenterProject;
	private String projectPrefix;
	private Integer rounding;
	private Set<ProjectProperty> projectProperties = new HashSet<ProjectProperty>(0);
	private List<ProjectClosure> projectClosures = new ArrayList<ProjectClosure>();
	private List<SystemVariable> systemVariablesList = new ArrayList<SystemVariable>();
	
	private List<RealProperty> projectRealProperty = new ArrayList<RealProperty>();
	private Set<RecoverClosure> recoverClosures = new HashSet<RecoverClosure>(0);
	private Set<User_Terranvm> projectUsers = new HashSet<User_Terranvm>(0);
	
	private boolean mandatoryInterest;
	
	//este es un atributo para ser usado en la busqueda de la lista de proyectos
	private String businesLine;
	
	public Project() {
	}

	public Project(int id, BusinessEntity businessEntity, BusinessLine businessLine, String nameProject, String costCenterProject, String projectPrefix, boolean mandatoryInterest) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.businessLine = businessLine;
		this.nameProject = nameProject;
		this.costCenterProject = costCenterProject;
		this.projectPrefix = projectPrefix;
		this.mandatoryInterest = mandatoryInterest;
	}

	public Project(int id, BusinessEntity businessEntity, BusinessLine businessLine, String nameProject, String costCenterProject, Set<ProjectProperty> projectProperties, List<ProjectClosure> projectClosure, String projectPrefix , Integer rounding, Set<RecoverClosure> recoverClosures,Set<User_Terranvm> projectUsers, boolean mandatoryInterest) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.businessLine = businessLine;
		this.nameProject = nameProject;
		this.costCenterProject = costCenterProject;
		this.projectProperties = projectProperties;
		this.projectClosures = projectClosure;
		this.projectPrefix = projectPrefix;
		this.rounding = rounding;
		this.recoverClosures = recoverClosures;
		this.projectUsers = projectUsers;
		this.mandatoryInterest = mandatoryInterest;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
		@SequenceGenerator(name = "pk_sequence", sequenceName = "project_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	@NotNull
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public List<SystemVariable> getSystemVariablesList() {
		return systemVariablesList;
	}

	
	public void setSystemVariablesList(List<SystemVariable> systemVariablesList) {
		this.systemVariablesList = systemVariablesList;
	}
	
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "business_entity", nullable = false)
	@NotNull
	public BusinessEntity getBusinessEntity() {
		return this.businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_line", nullable = false)
	@NotNull
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public BusinessLine getBusinessLine() {
		return this.businessLine;
	}

	public void setBusinessLine(BusinessLine businessLine) {
		this.businessLine = businessLine;
	}

	@Column(name = "name_project", nullable = false)
	@NotNull
	public String getNameProject() {
		return this.nameProject;
	}

	public void setNameProject(String nameProject) {
		this.nameProject = nameProject;
	}

	@Column(name = "cost_center_project", nullable = false)
	@NotNull
	public String getCostCenterProject() {
		return this.costCenterProject;
	}

	public void setCostCenterProject(String costCenterProject) {
		this.costCenterProject = costCenterProject;
	}
	
	@Column(name = "project_prefix", nullable = false)
	@NotNull
	public String getProjectPrefix() {	
		if (projectPrefix==null){
			return this.projectPrefix;
		}
			return this.projectPrefix.toUpperCase().replace(" ", "");
		
	} 

	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix.toUpperCase().replace(" ", "");
	}
	
	
	@Column(name = "rounding")
	public Integer getRounding() {
		return this.rounding;
	}

	public void setRounding(Integer rounding) {
		this.rounding = rounding;
	}
	
	

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	public Set<ProjectProperty> getProjectProperties() {
		return this.projectProperties;
	}

	public void setProjectProperties(Set<ProjectProperty> projectProperties) {
		this.projectProperties = projectProperties;
	}
	
	public String costCenter() {
		return this.getCostCenterProject();
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	@OrderBy("closureDate DESC")
	public List<ProjectClosure> getProjectClosures() {
		return projectClosures;
	}

	
	public void setProjectClosures(List<ProjectClosure> projectClosures) {
		this.projectClosures = projectClosures;
	}
	
	
	@Transient 
	public double makeRounding(double numero)
	{
		Locale.setDefault(Locale.ENGLISH);
		
		if(this.getRounding()==1)
		{
			return  Math.floor(numero);
		}else if(this.getRounding()==10)
		{
			return (Math.floor(numero/10))*10;
		}else if(this.getRounding()==100)
		{
			return (Math.floor(numero/100))*100;
		}else if(this.getRounding()==1000)
		{
			return (Math.floor(numero/1000))*1000;
		}else
		{
			
			DecimalFormat df = new DecimalFormat("#.00");
			return Double.parseDouble(df.format(numero));
		}
	}
	
	@ManyToMany(targetEntity = RealProperty.class, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "project_has_realproperty", joinColumns = @JoinColumn(name = "project"), inverseJoinColumns = @JoinColumn(name = "realproperty"))
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public List<RealProperty> getProjectRealProperty() {
		return this.projectRealProperty;
	}

	public void setProjectRealProperty(List<RealProperty> projectRealProperty) {
		this.projectRealProperty = projectRealProperty;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	public Set<RecoverClosure> getRecoverClosures() {
		return this.recoverClosures;
	}

	public void setRecoverClosures(Set<RecoverClosure> recoverClosures) {
		this.recoverClosures = recoverClosures;
	}

	@ManyToMany(targetEntity = org.koghi.terranvm.entity.User_Terranvm.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "project_user", joinColumns = @JoinColumn(name = "project"), inverseJoinColumns = @JoinColumn(name = "user_terranvm"))
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	public Set<User_Terranvm> getProjectUsers() {
		return projectUsers;
	}

	public void setProjectUsers(Set<User_Terranvm> projectUsers) {
		this.projectUsers = projectUsers;
	}

	@Transient
	public String getBusinesLine() {
		return businesLine;
	}

	public void setBusinesLine(String businesLine) {
		this.businesLine = businesLine;
	}

	@NotNull
	@Column(name = "mandatory_interest", nullable = false)
    public boolean isMandatoryInterest() {
        return mandatoryInterest;
    }

    public void setMandatoryInterest(boolean mandatoryInterest) {
        this.mandatoryInterest = mandatoryInterest;
    }
	
	
}
