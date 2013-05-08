package org.koghi.terranvm.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.MakerChecker;

@Name("makerCheckerBusinessEntityList")
public class MakerCheckerBusinessEntityList extends EntityQuery<MakerChecker> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select makerChecker from MakerChecker makerChecker WHERE makerChecker.className like ('"+BusinessEntity.class.getCanonicalName()+"')";

	private MakerChecker makerChecker = new MakerChecker();
	
//	@In(required=false)
//    public String projectFilter;
//
//    @In(required=false)
//    public List<SelectItem> projectsFilter;

	public MakerCheckerBusinessEntityList() {
		
		setEjbql(EJBQL);
		setMaxResults(25);
	}

	public MakerChecker getMakerChecker() {
		return makerChecker;
	}

//	@Override
//	public String getEjbql() {
//		String filter = super.getEjbql();
//		System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"+filter);
//		if (projectFilter!=null && projectsFilter!=null){
//			if (projectFilter.equals("-1")){
//				String next = ",MakerCheckerXProject mcp WHERE makerChecker.className like ('"+BusinessEntity.class.getCanonicalName()+"') and ((mcp.makerChecker = makerChecker AND  mcp.project.id IN  (";
//				boolean in = false;
//				for (SelectItem item : projectsFilter) {
//					if (!item.getValue().equals("-1")){
//						in=true;
//						filter +=next;
//						next=null;
//						next = ",";
//						filter+=item.getValue();
//					}
//				}
//				if (in){
//					filter += ")"; 
//					System.out.println("qqqqqqqqqqqqqqqqq"+filter);
//				}
//				filter +=(") OR (SELECT COUNT(p) FROM MakerCheckerXProject p WHERE p.makerChecker = makerChecker) = 0)");
//				System.out.println("qqqqqqqqqqqqqqqqq2222222222"+filter);
//			}else{
//				filter += ",MakerCheckerXProject mcp WHERE makerChecker.className like ('"+BusinessEntity.class.getCanonicalName()+"') and ((mcp.project.id IN (" + projectFilter + ") AND mcp.makerChecker = makerChecker) OR (SELECT COUNT(p) FROM MakerCheckerXProject p WHERE p.makerChecker = makerChecker) = 0)";
//				System.out.println("ssssssssssssssssssssssssssssssssssss"+filter);
//			}
//		}
//		return filter;
//	}
}
