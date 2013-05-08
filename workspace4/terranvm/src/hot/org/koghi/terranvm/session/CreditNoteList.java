package org.koghi.terranvm.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.CreditNote;

import java.util.Arrays;

@Name("creditNoteList")
public class CreditNoteList extends EntityQuery<CreditNote> {

	private static final String EJBQL = "select creditNote from CreditNote creditNote";

	private static final String[] RESTRICTIONS = { "lower(creditNote.consecutive) like lower(concat(#{creditNoteList.creditNote.consecutive},'%'))", "lower(creditNote.reason) like lower(concat(#{creditNoteList.creditNote.reason},'%'))", };

	private CreditNote creditNote = new CreditNote();

	public CreditNoteList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CreditNote getCreditNote() {
		return creditNote;
	}
}
