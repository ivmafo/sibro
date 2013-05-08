package org.koghi.terranvm.session;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.Project;

@Name("projectList")
public class ProjectList extends EntityQuery<Project> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select project from Project project";

	private static final String[] RESTRICTIONS = { 	"lower(project.nameProject) like lower(concat('%',concat(#{projectList.project.nameProject},'%')))", 
													"lower(project.costCenterProject) like lower(concat('%',concat(#{projectList.project.costCenterProject},'%')))", 
													"lower(project.businessLine.nameBusinessLine) like lower(concat('%',concat(#{projectList.project.businesLine},'%')))"};

	private Project project = new Project();
	
	
	@In(required=false)
    public String projectFilter;

    @In(required=false)
    public List<SelectItem> projectsFilter;

	public ProjectList() {

		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Project getProject() {
		return project;
	}
	
	@Override
	public String getEjbql() {
		String filter = super.getEjbql();
		if (projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				String next = " WHERE project.id IN (";
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
				filter += " WHERE project.id IN (" + projectFilter + ")";
			}
		}
		return filter;
	}
	
	@Override
	public void setEjbql(String ejbql) {
		super.setEjbql(ejbql);
	}
	
	@Override
	public List<Project> getResultList() {
		super.refresh();
		return super.getResultList();
	}
}
