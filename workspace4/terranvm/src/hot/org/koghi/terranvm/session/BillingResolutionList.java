package org.koghi.terranvm.session;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.BillingResolution;

@Name("billingResolutionList")
public class BillingResolutionList extends EntityQuery<BillingResolution> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select billingResolution from BillingResolution billingResolution";

	private static final String[] RESTRICTIONS = { "lower(billingResolution.prefix) like lower(concat(#{billingResolutionList.billingResolution.prefix},'%'))" };

	private BillingResolution billingResolution = new BillingResolution();

	public BillingResolutionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public BillingResolution getBillingResolution() {
		return billingResolution;
	}
	@In(required=false)
	    public String projectFilter;

	    @In(required=false)
	    public List<SelectItem> projectsFilter;
	@Override
	public String getEjbql() {
		String filter = super.getEjbql();
		if (projectFilter!=null && projectsFilter!=null){ 
			if (!projectFilter.equals("-1")){  
				filter += " WHERE BillingResolution.businessEntity = (SELECT p.businessLine.businessEntity from Project p  where p.id = " + projectFilter + ") OR BillingResolution.businessEntity =(SELECT p.businessEntity from Project p  where p.id = " + projectFilter + ")";
			}
		}
		return filter;
	}
	
	@Override
	public void setEjbql(String ejbql) {
		super.setEjbql(ejbql);
	}
	
	@Override
	public List<BillingResolution> getResultList() {
		super.refresh();
		return super.getResultList();
	}
}
