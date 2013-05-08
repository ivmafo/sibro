package org.koghi.terranvm.session;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.koghi.terranvm.entity.IpcMonthly;

@Name("ipcMonthlyList")
public class IpcMonthlyList extends EntityQuery<IpcMonthly> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select ipcMonthly from IpcMonthly ipcMonthly";

	private static final String[] RESTRICTIONS = {};

	private IpcMonthly ipcMonthly = new IpcMonthly();

	public IpcMonthlyList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public IpcMonthly getIpcMonthly() {
		return ipcMonthly;
	}
}
