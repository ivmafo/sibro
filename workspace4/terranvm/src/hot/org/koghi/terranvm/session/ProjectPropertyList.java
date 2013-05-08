package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

@Name("projectPropertyList")
public class ProjectPropertyList extends EntityQuery<ProjectProperty> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select projectProperty from ProjectProperty projectProperty";

	//private static final String[] RESTRICTIONS = {};
	private static final String[] RESTRICTIONS = { 	"lower(projectProperty.biller.nameBusinessEntity) like lower(concat('%',concat(#{projectPropertyList.projectProperty.billerName},'%')))", 
													"lower(projectProperty.biller.idNumber) like lower(concat(#{projectPropertyList.projectProperty.billerNumberId},'%'))",
													"lower(projectProperty.billed.nameBusinessEntity) like lower(concat('%',concat(#{projectPropertyList.projectProperty.billedName},'%')))",
													"lower(projectProperty.contractType.name) like lower(concat('%',concat(#{projectPropertyList.projectProperty.typeContract},'%')))", };

	private ProjectProperty projectProperty = new ProjectProperty();
	
	private String nameProject = new String();
	@In(required=false)
    public String projectFilter;

    @In(required=false)
    public List<SelectItem> projectsFilter;


	public ProjectPropertyList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ProjectProperty getProjectProperty() {
		return projectProperty;
	}
	
	
	
	@Override
	public String getEjbql() {
		String filter = super.getEjbql();
//		System.out.println("*************" + projectFilter + " *************** " + projectsFilter);
		if (projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				String next = " WHERE projectProperty.project.id IN (";
				boolean in = false;
				for (SelectItem item : projectsFilter) {
					if (!item.getValue().equals("-1")){
						in=true;
						filter +=next;
						next=null;
						next = ",";
						filter+=item.getValue();
					}
				}
				if (in){
					filter += ")"; 
				}
			}else{
				filter += " WHERE projectProperty.project.id IN (" + projectFilter + ")";
			}
		}
		return filter;
	}
	
	@Override
	public void setEjbql(String ejbql) {
		super.setEjbql(ejbql);
	}

	public String getNameProject() {
		return nameProject;
	}

	public void setNameProject(String nameProject) {
		this.nameProject = nameProject;
	}
}
