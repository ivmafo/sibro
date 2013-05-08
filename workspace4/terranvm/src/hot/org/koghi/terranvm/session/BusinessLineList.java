package org.koghi.terranvm.session;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.BusinessLine;

@Name("businessLineList")
public class BusinessLineList extends EntityQuery<BusinessLine> {

	private static final String EJBQL = "select DISTINCT(businessLine) from BusinessLine businessLine";

	private static final String[] RESTRICTIONS = { "lower(businessLine.nameBusinessLine) like lower(concat(#{businessLineList.businessLine.nameBusinessLine},'%'))", "lower(businessLine.costCenterBusinessLine) like lower(concat(#{businessLineList.businessLine.costCenterBusinessLine},'%'))"};

	private BusinessLine businessLine = new BusinessLine();
	
	@In(required=false)
    public String projectFilter;

    @In(required=false)
    public List<SelectItem> projectsFilter;


	public BusinessLineList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public BusinessLine getBusinessLine() {
		return businessLine;
	}
	
	@Override
	public String getEjbql() {
		String filter = super.getEjbql();
		if (projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				String next = ",Project project WHERE (project.businessLine = businessLine AND  project.id IN  (";
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
				filter +=(") OR (SELECT COUNT(p) FROM Project p WHERE p.businessLine = businessLine) = 0");
			}else{
				filter += ",Project project WHERE (project.id IN (" + projectFilter + ") AND project.businessLine = businessLine) OR (SELECT COUNT(p) FROM Project p WHERE p.businessLine = businessLine) = 0";
			}
		}
		return filter;
	}
	
	@Override
	public void setEjbql(String ejbql) {
		super.setEjbql(ejbql);
	}
	@Override
	public Long getResultCount() {
		// TODO Auto-generated method stub
		Query q = getEntityManager().createQuery(getEjbql());
		return new Long (q.getResultList().size());
	}
}
