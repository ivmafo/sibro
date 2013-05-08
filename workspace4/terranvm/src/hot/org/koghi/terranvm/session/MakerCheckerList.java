package org.koghi.terranvm.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.ProjectProperty;

@Name("makerCheckerList")
public class MakerCheckerList extends EntityQuery<MakerChecker> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select makerChecker from MakerChecker makerChecker WHERE makerChecker.className like ('"+ProjectProperty.class.getCanonicalName()+"')";

	//private static final String[] RESTRICTIONS = { };

	private MakerChecker makerChecker = new MakerChecker();

	public MakerCheckerList() {
		
		setEjbql(EJBQL);
		//setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MakerChecker getMakerChecker() {
		
		return makerChecker;
	}

}
