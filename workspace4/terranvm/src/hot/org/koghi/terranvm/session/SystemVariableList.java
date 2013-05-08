package org.koghi.terranvm.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.SystemVariable;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

@Name("systemVariableList")
public class SystemVariableList extends EntityQuery<SystemVariable> {

	private static final String EJBQL = "select systemVariable from SystemVariable systemVariable";

	private static final String[] RESTRICTIONS = { "lower(systemVariable.name) like lower(concat(#{systemVariableList.systemVariable.name},'%'))", "lower(systemVariable.sintax) like lower(concat(#{systemVariableList.systemVariable.sintax},'%'))", "lower(systemVariable.value) like lower(concat(#{systemVariableList.systemVariable.value},'%'))", };

	private SystemVariable systemVariable = new SystemVariable();
	
	@In(required=false)
    public String projectFilter;
	
	@In(required=false)
    public List<SelectItem> projectsFilter;

	public SystemVariableList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SystemVariable getSystemVariable() {
		return systemVariable;
	}
	
	@Override
	public String getEjbql() {
		String filter = super.getEjbql();
		if (projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				String next = " WHERE systemVariable.project.id IN (";
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
				filter += " WHERE systemVariable.project.id IN (" + projectFilter + ")";  
			}
		}
		return filter;
	}
	
	
}
