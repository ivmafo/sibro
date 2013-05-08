package org.koghi.terranvm.session;


import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.BusinessLine;
import org.koghi.terranvm.entity.MakerChecker;

@Name("makerCheckerBusinessLineList")
public class MakerCheckerBusinessLineList extends EntityQuery<MakerChecker> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select DISTINCT(makerChecker) from MakerChecker makerChecker";

	private MakerChecker makerChecker = new MakerChecker();
	
	@In(required=false)
    public String projectFilter;

    @In(required=false)
    public List<SelectItem> projectsFilter;

	public MakerCheckerBusinessLineList() {
		
		setEjbql(EJBQL);
		setMaxResults(25);
	}

	public MakerChecker getMakerChecker() {
		return makerChecker;
	}
	
	@Override
	public String getEjbql() {
		String filter = super.getEjbql();
		if (projectFilter!=null && projectsFilter!=null){
			if (projectFilter.equals("-1")){
				String next = ",MakerCheckerXProject mcp WHERE makerChecker.className like ('"+BusinessLine.class.getCanonicalName()+"') and ((mcp.makerChecker = makerChecker AND  mcp.project.id IN  (";
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
				filter +=(") OR (SELECT COUNT(p) FROM MakerCheckerXProject p WHERE p.makerChecker = makerChecker) = 0)");
			}else{
				filter += ",MakerCheckerXProject mcp WHERE makerChecker.className like ('"+BusinessLine.class.getCanonicalName()+"') and ((mcp.project.id IN (" + projectFilter + ") AND mcp.makerChecker = makerChecker) OR (SELECT COUNT(p) FROM MakerCheckerXProject p WHERE p.makerChecker = makerChecker) = 0)";
			}
		}
		return filter;
	}

}
