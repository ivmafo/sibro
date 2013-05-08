package org.koghi.terranvm.session;


import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.ProjectProperty;

@Name("makerCheckerProjectPropertyCounterList")
public class MakerCheckerProjectPropertyCounterList extends EntityQuery<MakerChecker> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select projectProperty from ProjectProperty projectProperty where projectProperty.step=1";

	private static final String[] RESTRICTIONS = {};

	private ProjectProperty projectProperty = new ProjectProperty();
	
	@In(required=false)
    public String projectFilter;

    @In(required=false)
    public List<SelectItem> projectsFilter;

	public MakerCheckerProjectPropertyCounterList() {
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
		if (projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				String next = " AND projectProperty.project.id IN (";
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
				filter += " AND projectProperty.project.id IN (" + projectFilter + ")";
			}
		}
		return filter;
	}
}
