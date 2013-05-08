package org.koghi.terranvm.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.CreditNote;
import org.koghi.terranvm.entity.MakerChecker;

@Name("makerCheckerCreditNotesList")
public class MakerCheckerCreditNotesList extends EntityQuery<MakerChecker> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select makerChecker from MakerChecker makerChecker WHERE makerChecker.className like ('"+CreditNote.class.getCanonicalName()+"')";

	private MakerChecker makerChecker = new MakerChecker();
	

	public MakerCheckerCreditNotesList() {
		
		setEjbql(EJBQL);
		setMaxResults(25);
	}

	public MakerChecker getMakerChecker() {
		return makerChecker;
	}


}
