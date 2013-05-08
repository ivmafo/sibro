package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("contactTypeList")
public class ContactTypeList extends EntityQuery<ContactType> {

	private static final String EJBQL = "select contactType from ContactType contactType";

	private static final String[] RESTRICTIONS = { "lower(contactType.name) like lower(concat(#{contactTypeList.contactType.name},'%'))", };

	private ContactType contactType = new ContactType();

	public ContactTypeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ContactType getContactType() {
		return contactType;
	}
}
