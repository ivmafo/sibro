package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.ConsecutiveCreditNotes;

@Name("consecutiveCreditNotesList")
public class ConsecutiveCreditNotesList extends
		EntityQuery<ConsecutiveCreditNotes> {

	private static final String EJBQL = "select consecutiveCreditNotes from ConsecutiveCreditNotes consecutiveCreditNotes";

	private static final String[] RESTRICTIONS = {
			"lower(consecutiveCreditNotes.prefix) like lower(concat(#{consecutiveCreditNotesList.consecutiveCreditNotes.prefix},'%'))",
			"lower(consecutiveCreditNotes.suffix) like lower(concat(#{consecutiveCreditNotesList.consecutiveCreditNotes.suffix},'%'))",
			"lower(consecutiveCreditNotes.siigoCode) like lower(concat(#{consecutiveCreditNotesList.consecutiveCreditNotes.siigoCode},'%'))", };

	private ConsecutiveCreditNotes consecutiveCreditNotes = new ConsecutiveCreditNotes();

	public ConsecutiveCreditNotesList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConsecutiveCreditNotes getConsecutiveCreditNotes() {
		return consecutiveCreditNotes;
	}
}
