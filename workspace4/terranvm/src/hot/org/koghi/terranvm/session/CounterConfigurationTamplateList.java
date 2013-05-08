package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.CounterConfigurationTamplate;
import org.koghi.terranvm.entity.Project;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

@Name("counterConfigurationTamplateList")
public class CounterConfigurationTamplateList extends EntityQuery<CounterConfigurationTamplate> {

	private static final String EJBQL = "select counterConfigurationTamplate from CounterConfigurationTamplate counterConfigurationTamplate";

	private static final String[] RESTRICTIONS = { "lower(counterConfigurationTamplate.name) like lower(concat(#{counterConfigurationTamplateList.counterConfigurationTamplate.name},'%'))", };

	private CounterConfigurationTamplate counterConfigurationTamplate = new CounterConfigurationTamplate();

	
	public CounterConfigurationTamplateList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CounterConfigurationTamplate getCounterConfigurationTamplate() {
		return counterConfigurationTamplate;
	}
	
	
	private Project project = new Project();
	
	
	@In(required=false)
    public String projectFilter;

    @In(required=false)
    public List<SelectItem> projectsFilter;

	

	public Project getProject() {
		return project;
	}
	
	@Override
	public String getEjbql() {
		String filter = super.getEjbql();
		if (projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				String next = " WHERE project_id_1 IN ("; 
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
				filter += " WHERE project_id_1 IN (" + projectFilter + ")";
			}
		}
		return filter;
	}
	
	@Override
	public void setEjbql(String ejbql) {
		super.setEjbql(ejbql);
	}
	
	@Override
	public List<CounterConfigurationTamplate> getResultList() {
		super.refresh();
		return super.getResultList();
	}
}
