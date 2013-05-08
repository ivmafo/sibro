package org.koghi.terranvm.session;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.maker_checker.MakerCheckerConfig;

@Name("makerCheckerHome")
public class MakerCheckerHome extends EntityHome<MakerChecker> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String summaryUrl;
	private Concept concept;

	public void setMakerCheckerId(Integer id) {
		setId(id);
	}

	public Integer getMakerCheckerId() {
		return (Integer) getId();
	}

	@Override
	protected MakerChecker createInstance() {
		MakerChecker makerChecker = new MakerChecker();
		return makerChecker;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public MakerChecker getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	// @Override
	// public String persist() {
	// // Query query =
	// this.getEntityManager().createQuery("from BusinessEntityType be");
	// // List<BusinessEntityType> l = (List<BusinessEntityType>)
	// query.getResultList();
	// //
	// // ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	// // ObjectOutputStream oos;
	// // try {
	// // oos = new ObjectOutputStream(byteArray);
	// // oos.writeObject(l.get(0));
	// //
	// // MakerChecker makerChecker = new MakerChecker();
	// // makerChecker.setObject(byteArray.toByteArray());
	// // makerChecker.setClassName(makerChecker.getClass().getCanonicalName());
	// // this.setInstance(makerChecker);
	// // return super.persist();
	// // } catch (IOException e) {
	// // e.printStackTrace();
	// // }
	// return "failed";
	// }

	public void persistObject(Object object) {
		executeGets(object);
		int id;
		try {

			id = getObjectId(object);

			MakerChecker makerChecker = new MakerChecker();
			makerChecker.setObject(BillingTools.objectToBytes(object));
			makerChecker.setClassName(getObjectClass(object));
			makerChecker.setObjectId(id);

			this.setInstance(makerChecker);

			getEntityManager().clear();
			getEntityManager().persist(getInstance());
			getEntityManager().flush();

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public void persistObject(Object object, Object... projects) {
		getEntityManager().clear();
		Session session = (Session) getEntityManager().getDelegate();
		executeGets(object);
		int id;
		try {

			id = getObjectId(object);

			MakerChecker makerChecker = new MakerChecker();
			makerChecker.setObject(BillingTools.objectToBytes(object));
			makerChecker.setClassName(getObjectClass(object));
			makerChecker.setObjectId(id);

			Set<Project> projectsAux = makerChecker.getMakerCheckerXProjects();
			for (Object project : projects) {
				if (project instanceof Project) {
					session.refresh(project);
					// Project aux = getEntityManager().find(Project.class,
					// ((Project) project).getId());
					projectsAux.add((Project) project);

				}
			}

			this.setInstance(makerChecker);

			// getEntityManager().clear();
			session.persist(getInstance());
			session.flush();

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	private void executeGets(Object object) {
		log(Level.INFO, "************************MAKKER-CHKER********************");
		Method[] methods = object.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get") && !method.isVarArgs()) {
				try {
					log(Level.INFO, "Metodo Con Error: " + method.getName());
					Object ret = method.invoke(object, new Object[0]);
					if (ret != null) {
						Class<?> cla = ret.getClass();
						try {
							Method getId = cla.getMethod("getId", new Class<?>[0]);
							getId.invoke(ret, new Object[0]);
						} catch (SecurityException e) {
						} catch (NoSuchMethodException e) {
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}

	}

	private static String getObjectClass(Object object) {
		String nameClass = object.getClass().getCanonicalName();

		// Se elimina _$$_... que a veces java se lo adicciona al nombre de la
		// clase
		if (nameClass.contains("_$$"))
			nameClass = nameClass.replace(nameClass.subSequence(nameClass.indexOf("_$$"), nameClass.length()), "");

		return nameClass;
	}

	private static int getObjectId(Object object) {
		Method methodGetId;
		try {
			methodGetId = object.getClass().getMethod("getId", new Class[0]);
			return (Integer) methodGetId.invoke(object, new Object[0]);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public boolean isObjectInMakerChecker(Object object) {
		Query q = this.getEntityManager().createQuery("FROM MakerChecker mc WHERE mc.objectId=? and mc.className=?");

		q.setParameter(1, getObjectId(object));
		q.setParameter(2, getObjectClass(object));
		@SuppressWarnings("unchecked")
		List<MakerChecker> l = (List<MakerChecker>) q.getResultList();

		if (l.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public String getPageView(String ClassName) {
		return new MakerCheckerConfig().getViewPage(ClassName);
	}

	public String getNameView(String ClassName) {
		return new MakerCheckerConfig().getNameView(ClassName);
	}

	public String getParamNameView(String ClassName) {
		return new MakerCheckerConfig().getParamNameView(ClassName);
	}

	public Object getInstance(int makerCheckerId) {
		Query q = this.getEntityManager().createQuery("FROM MakerChecker mc WHERE mc.id = ?");
		q.setParameter(1, makerCheckerId);
		@SuppressWarnings("unchecked")
		List<MakerChecker> l = (List<MakerChecker>) q.getResultList();
		if (l.size() > 0) {
			try {
				return BillingTools.bytesToObject(l.get(0).getObject());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

		}
		return null;
	}

	public Object getInstance(MakerChecker makerChecker) {
		try {
			return BillingTools.bytesToObject(makerChecker.getObject());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Método que se encarga de devolver el makerchecker respectivamente a un
	 * objeto
	 * 
	 * @param object
	 *            objeto por el que se busca el makercheker
	 * @return mekercheker según el objeto pasado or parametro, si no se
	 *         encuentrá el objeto se devuelve null
	 */
	public MakerChecker getMakerChecker(Object object) {
		Query q = this.getEntityManager().createQuery("FROM MakerChecker mc WHERE mc.objectId=? and mc.className=?");

		q.setParameter(1, getObjectId(object));
		q.setParameter(2, getObjectClass(object));
		@SuppressWarnings("unchecked")
		List<MakerChecker> l = (List<MakerChecker>) q.getResultList();

		if (l.size() > 0)
			return l.get(0);
		else
			return null;
	}

	public void deleteMaker(Object object) {

		Query q = this.getEntityManager().createNativeQuery("DELETE FROM maker_checker_x_project WHERE maker_ckecker IN (SELECT mc.id FROM maker_checker mc WHERE mc.object_id=? and mc.class_name=?)");

		q.setParameter(1, getObjectId(object));
		q.setParameter(2, getObjectClass(object));

		q.executeUpdate();

		q = this.getEntityManager().createQuery("DELETE FROM MakerChecker mc WHERE mc.objectId=? and mc.className=?");

		q.setParameter(1, getObjectId(object));
		q.setParameter(2, getObjectClass(object));

		q.executeUpdate();
	}

	public void deleteMaker(int makerCheckerId) {

		Query q = this.getEntityManager().createNativeQuery("DELETE FROM maker_checker_x_project WHERE maker_ckecker IN (SELECT mc.id FROM maker_checker mc WHERE mc.id = ?)");

		q.setParameter(1, makerCheckerId);

		q.executeUpdate();

		q = this.getEntityManager().createQuery("DELETE FROM MakerChecker mc WHERE mc.id = ?");

		q.setParameter(1, makerCheckerId);

		q.executeUpdate();
	}

	public String getSummaryUrl() {
		if (this.summaryUrl == null || this.summaryUrl.isEmpty())
			this.summaryUrl = "No existe una URL de resumen asociada al plugin";
		return summaryUrl;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public void setSummaryUrl(String summaryUrl) {
		this.summaryUrl = summaryUrl;
	}

	public void instanceConcept(int conceptId) {
		Query q = this.getEntityManager().createQuery("from Concept c where c.id = " + conceptId);
		@SuppressWarnings("unchecked")
		List<Concept> l = (List<Concept>) q.getResultList();
		this.concept = l.get(0);
		this.summaryUrl = "";

	}

	public String hostURL() {
		String host = "http://";
		host += FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		host += ":" + FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort();
		return host;
	}

	public void updateMakerChackerObject(MakerChecker makerChecker, Object object) {
		executeGets(object);
		int id;
		try {

			id = getObjectId(object);

			makerChecker.setObject(BillingTools.objectToBytes(object));
			makerChecker.setClassName(getObjectClass(object));
			makerChecker.setObjectId(id);

			this.setInstance(makerChecker);

			getEntityManager().clear();
			getEntityManager().persist(getInstance());
			getEntityManager().flush();

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public String getClassProjectProperty() {
		return ProjectProperty.class.getCanonicalName();
	}

	public int stepProjectProperty() {
		Object obj = this.getInstance(this.getInstance().getId());
		int res = -1;
		if (obj instanceof ProjectProperty) {
			res = ((ProjectProperty) obj).getStep();
		}
		return res;
	}

	@Override
	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
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
	private void log(Level level, Object message) {
		BillingTools.printLog(MakerCheckerHome.class, level, message);
	}

}
