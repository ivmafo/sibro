package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

@Name("realPropertyList")
public class RealPropertyList extends EntityQuery<RealProperty> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select realProperty from RealProperty realProperty";

	private static final String[] RESTRICTIONS = { "lower(realProperty.nameProperty) like lower(concat(#{realPropertyList.realProperty.nameProperty},'%'))", "lower(realProperty.enrolmentNumber) like lower(concat(#{realPropertyList.realProperty.enrolmentNumber},'%'))", };

	private RealProperty realProperty = new RealProperty();
	
	@In(required=false)
    public String projectFilter;

    @In(required=false)
    public List<SelectItem> projectsFilter;

	public RealPropertyList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		//setMaxResults(25);
	}

	public RealProperty getRealProperty() {
		return realProperty;
	}
	
	@Override
	public String getEjbql() {
		return super.getEjbql();
	}
	
	@Override
	public void setEjbql(String ejbql) {
		super.setEjbql(ejbql);
	}
	
	@Override
	public List<RealProperty> getResultList() {
		List<RealProperty> result = super.getResultList();
		List<RealProperty> realPropertyNew = new ArrayList<RealProperty>();
		
		if(projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				for(RealProperty realProperty: result){
					if(realProperty.getProjectProperties()!=null && realProperty.getProjectProperties().size()>0){
						for (SelectItem item : projectsFilter) {
							//bandera para saber si ya añadio el realproperty a la lista
							boolean flag=false;
							for(ProjectProperty projectProperty : realProperty.getProjectProperties()){
								if (projectProperty.getProject()!=null 
										&& !item.getValue().equals("-1")
										&& projectProperty.getProject().getId()==Integer.parseInt((String)item.getValue())){
									realPropertyNew.add(realProperty);
									flag=true;
									break;
								}
							}
							if (flag){
								break;
							}
						}
					}else{
						realPropertyNew.add(realProperty);
					}
				}
			}else{
				for(RealProperty realProperty: result){
					if (realProperty.getProjectRealProperty() != null && !realProperty.getProjectRealProperty().isEmpty()){
						for (Iterator<Project> iterator = realProperty.getProjectRealProperty().iterator(); iterator.hasNext();) {
							Project project = iterator.next();
							if (project!=null && project.getId()==Integer.parseInt(projectFilter)){
								realPropertyNew.add(realProperty);
								break;
							}
						}
					}else {
						realPropertyNew.add(realProperty);
					}
//					if(realProperty.getProjectProperties()!=null && realProperty.getProjectProperties().size()>0){
//						for(ProjectProperty projectProperty : realProperty.getProjectProperties()){
//							if (projectProperty.getProject()!=null && projectProperty.getProject().getId()==Integer.parseInt(projectFilter)){
//								realPropertyNew.add(realProperty);
//								break;
//							}
//						}
//					}else{
//						realPropertyNew.add(realProperty);
//					}
					
				}
			}
		}else {
			realPropertyNew = result;
		}
		return realPropertyNew;
	}
}
