package org.koghi.terranvm.session;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("contactList")
public class ContactList extends EntityQuery<Contact> {

	private static final String EJBQL = "select contact from Contact contact";

	private static final String[] RESTRICTIONS = { "lower(contact.firstName) like lower(concat(#{contactList.contact.firstName},'%'))", "lower(contact.lastName) like lower(concat(#{contactList.contact.lastName},'%'))", "lower(contact.idNumber) like lower(concat(#{contactList.contact.idNumber},'%'))", };

	private Contact contact = new Contact();

	public ContactList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Contact getContact() {
		return contact;
	}
}
