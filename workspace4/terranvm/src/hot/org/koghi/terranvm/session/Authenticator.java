package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.bean.StringMD;
import org.koghi.terranvm.entity.Action;
import org.koghi.terranvm.entity.Features;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.Role;
import org.koghi.terranvm.entity.User_Terranvm;

@Name("authenticator")
public class Authenticator {

	@In
	Identity identity;
	
	@In
	Credentials credentials;
	
	@In
	private EntityManager entityManager;

	private static final String USER_NAME_SEPARATOR = " -- ";
	//
	private String login;
	private String password;
	private Date currentDate;

	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	private String name1;

	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	public String role;

	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	public String projectFilter;

	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	public List<SelectItem> projectsFilter;

	@Out(scope = ScopeType.SESSION, required = false)
	@In(required = false)
	private User_Terranvm user;



	public boolean authenticate() {
		log(Level.INFO, "authenticating start "+credentials.getUsername());
		System.out.println(entityManager.getDelegate());
		// write your authentication logic here,

		this.login = credentials.getUsername();
		this.password = credentials.getPassword();
		this.currentDate = Calendar.getInstance().getTime();

		Query q = this.entityManager.createQuery("FROM User_Terranvm us WHERE us.login = ? AND us.password = ?");
		q.setParameter(1, this.login);
		q.setParameter(2, password = StringMD.getStringMessageDigest(this.password, StringMD.MD5));
		@SuppressWarnings("unchecked")
		List<User_Terranvm> users = q.getResultList();

		// if (users.size() > 0 && users.get(0).getState() == 1 &&
		// users.get(0).getEffectiveDate().after(currentDate)) {
		if (users.size() > 0 && users.get(0).getState() == 1) {
			user = users.get(0);
			log(Level.INFO, "EL USUARIO EXISTE......PUEDE ACCEDER");
			log(Level.INFO, user.getLogin());
			log(Level.INFO, this.currentDate.toString());
			log(Level.INFO, users.get(0).getRole().getRoleName());
			// procesos de usuario identificado

			identity.addRole(users.get(0).getRole().getRoleName()); // return
																	// true if
																	// the
																	// authentication
																	// was
			// successful, false otherwise

			this.name1 = user.getLogin() + USER_NAME_SEPARATOR + user.getNombre() + USER_NAME_SEPARATOR + user.getRole().getRoleName();
			role = user.getRole().getRoleName();

			projectsLoad(users.get(0));

			return true;
		} else if (users.size() == 0) {
			log(Level.INFO, "EL USUARIO NO EXISTE....NO PUEDE ACCEDER");
			return false;
		} else if (users.size() > 0 && users.get(0).getState() == 0) {
			// (users.size() > 0 && users.get(0).getState() == 0 ||
			// users.get(0).getEffectiveDate().before(currentDate)) {
			log(Level.INFO, "EL USUARIO EXISTE PERO SE ENCUENTRA INACTIVO O YA CADUCO LA FECHA DE VIGENCIA....NO PUEDE ACCEDER");
			log(Level.INFO, users.get(0).getLogin().toString());
			log(Level.INFO, this.currentDate.toString());
			return false;
		}
		return false;
	}

	public void projectsLoad(User_Terranvm user) {

		List<Project> projects = user.getProjectUsers();
		projectsFilter = new ArrayList<SelectItem>();
		for (Project project : projects) {
			SelectItem item = new SelectItem();
			item.setValue(project.getId() + "");
			item.setLabel(project.getNameProject());
			projectsFilter.add(item);
		}

		if (projectsFilter.size() > 1 && user.getRole().getId() == 1) {
			SelectItem item = new SelectItem();
			item.setValue("-1");
			item.setLabel("Filtrar Todos");
			projectsFilter.add(0, item);
		}

		if (projectsFilter.size() > 0) {
			projectFilter = (String) projectsFilter.get(0).getValue();
		}
	}

	public String Entity() {
		String Name = "";
		Query q = this.entityManager.createQuery("FROM Project pr ");
		@SuppressWarnings("unchecked")
		List<Project> projectList = (List<Project>) q.getResultList();

		if (this.projectFilter != null && Integer.parseInt(this.projectFilter) != -1) {
			for (Project project : projectList) {
				if (project.getId() == Integer.parseInt(this.projectFilter))
					Name = project.getBusinessEntity().getNameBusinessEntity();
			}

			return Name;
		} else
			return "";
	}

	public boolean validateShowPage(String namePage) {
		String userName = credentials.getUsername();
		if (userName != null && role != null) {
			Query q = this.entityManager.createNativeQuery("select r.* from role r , features f, role_features rf " + "WHERE r.role_name = ? and f.page = ? and rf.role = r.id and rf.features = f.id", Role.class);

			q.setParameter(1, role);
			q.setParameter(2, namePage);

			@SuppressWarnings("unchecked")
			List<Role> roles = (List<Role>) q.getResultList();

			if (roles != null && roles.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean validateShowFunction(String namePage, String action) {
		String userName = credentials.getUsername();
		if (userName != null && role != null) {
			Query q = this.entityManager.createQuery("FROM Role rl WHERE rl.roleName = ?");
			q.setParameter(1, role);

			@SuppressWarnings("unchecked")
			List<Role> roles = q.getResultList();

			if (roles != null && roles.size() > 0) {
				List<Features> features = roles.get(0).getFeatureses();
				if (features != null) {
					for (Features feature : features) {
						if (feature.getPage().equals(namePage)) {
							for (Action actionAux : feature.getActions()) {
								if (actionAux.getAction().equals(action)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;

	}

	public String getName1() {
		if (name1 == null) {
			return "";
		}
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public boolean restrictionPage(String names) {
		try {
			String name = names.replace("/", "").replace(".seam", ".xhtml");
			if (name.equals("login.xhtml") || name.equals("home.xhtml") || name.equals("error.xhtml") || names.equals("/seam/docstore/document.seam") || name.equals("ChangePasswordEdit.xhtml") || name.equals("VERSION.xhtml")) {
				return true;
			} else if (!checkTerranvmPage(name)) {
				/*
				 * 2012-12-07 @dvaldivieso - Si la página que se esta
				 * solicitando no es una funcionalidad de TERRANVM, puede ser un
				 * request especial de RICH (JS, CSS, etc), se debe dejar pasar.
				 */
				return true;
			} else {
				return validateShowPage(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return TRUE if the page is a functionality of TERRANVM.
	 */
	private boolean checkTerranvmPage(String page) {
		Query q = this.entityManager.createNativeQuery("select id from features WHERE page = ?");
		q.setParameter(1, page);
		q.setMaxResults(1);

		@SuppressWarnings("unchecked")
		List<Object> pages = q.getResultList();
		if (pages != null && !pages.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This function prints a message in log file
	 * 
	 * @param level
	 *            Level object
	 * @param message
	 *            String message to be printed
	 */
	private void log(Level level, String message) {
		BillingTools.printLog(Authenticator.class , level, message);
	}

}
