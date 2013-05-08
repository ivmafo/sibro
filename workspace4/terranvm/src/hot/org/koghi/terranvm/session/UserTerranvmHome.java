package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.koghi.terranvm.bean.StringMD;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.Role;
import org.koghi.terranvm.entity.User_Terranvm;

@Name("userTerranvmHome")
public class UserTerranvmHome extends EntityHome<User_Terranvm> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String oldPassword;
	public String newpassword;
	public String passwordConfirm;
	
	public void setUserTerranvmId(Integer id) {
		setId(id);
	}

	public Integer getUserTerranvmId() {
		return (Integer) getId();
	}

	@Override
	protected User_Terranvm createInstance() {
		User_Terranvm userTerranvm = new User_Terranvm();
		return userTerranvm;
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
		if (getInstance().getRole() == null)
			return false;
		if (this.getProjectUsers()==null || (this.getProjectUsers()!=null && this.getProjectUsers().isEmpty()))
			return false;
		return true;
	}

	public User_Terranvm getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Project> getProjectUsers() {
		return getInstance() == null ? null : new ArrayList<Project>(getInstance().getProjectUsers());
	}
	
	public List<Role> getRoles(){
		Query q = getEntityManager().createQuery("FROM Role");
		@SuppressWarnings("unchecked")
		List<Role> rols = q.getResultList();
		
		if (rols!=null && !rols.isEmpty() && instance!=null && instance.getRole()==null){
			instance.setRole(rols.get(0));
		}
		return rols;
	}
	
	/**
	 * Metodo que Retorna un listado de los estados de un usuario
	 * 
	 * @return
	 */
	public List<SelectItem> getStates() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem(User_Terranvm.STATE_INACTIVE,User_Terranvm.STRING_STATES[User_Terranvm.STATE_INACTIVE]);
		items.add(item);
		item = new SelectItem(User_Terranvm.STATE_ACTIVE,User_Terranvm.STRING_STATES[User_Terranvm.STATE_ACTIVE]);;
		items.add(item);

		return items;
	}
	
	/**
	 * Método que retorna el nombre del estado
	 */
	public String getStateString(int state){
		return User_Terranvm.STRING_STATES[state];
	}
	
	public void removeProject(Project p){
		List<Project> projects = this.getInstance().getProjectUsers();
		for (Project project : projects){
			if (project.getId() == p.getId() && p.getId()!=0){
				projects.remove(p);
				break;
			}
		}
	}
	
	public void addProject(int id){
		Project project = this.getEntityManager().find(Project.class, id);
		List<Project> projects = this.getInstance().getProjectUsers();
		boolean thereProject=false;
		for (Project projectAux : projects) {
			if (projectAux.getId() == project.getId()){
				thereProject=true;
				break;
			}
		} 
		if (!thereProject){
			projects.add(project);
		}
	}
	
	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
	    
		this.oldPassword = StringMD.getStringMessageDigest(oldPassword, StringMD.MD5); 
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String changePassword(){
		if (oldPassword!=null && oldPassword.equals(getInstance().getPassword()) && passwordConfirm.equals(newpassword)){
			setPassword(newpassword);
			super.persist();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Cambio de contraseña", "Se realizó el cambio de contraseña exitosamente"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Cambio de contraseña", "Los campos son invalidos"));
		}
		cleanPasswords();
		return "";
	}

	private void cleanPasswords() {
		oldPassword = "";
		newpassword = "";
		passwordConfirm = "";
	}
	
	
	public String messageuser = "";

	public String getMessageuser() {
		return messageuser;
	}

	public void setMessageuser(String messageuser) {
		this.messageuser = messageuser;
		;
	}
	
	
	
	public void userExist() {

		//Consulta que traiga una lista con todos los usuarios repetidos de la base de datos
		
		Query query = this.getEntityManager().createQuery("FROM User_Terranvm ut WHERE ut.login = ? and  ut.id != ?");
		query.setParameter(1, this.instance.getLogin());
		query.setParameter(2, this.instance.getId());
		@SuppressWarnings("unchecked")
		List<User_Terranvm> user_Terranvms = query.getResultList();
		
		if(user_Terranvms.size()>0)
		{
			this.messageuser = "El Usuario ingresado ya existe, porfavor intente de nuevo";
			//return true;
		}
		else
		{
			this.messageuser = "";
			//return false;
		}
	}
	
	
	@Override
	public String persist() {
		// TODO Auto-generated method stub
		if (this.messageuser != "")
			return "";
		else
			return super.persist();
	}
	
	@Override
	public String update() {
		// TODO Auto-generated method stub
		if (this.messageuser != "")
			return "";
		else
			return super.update();
	}
	
	@Override
	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
		}
	}
	public String md5(String string){
	    System.out.println(StringMD.getStringMessageDigest(string, StringMD.MD5));
        return StringMD.getStringMessageDigest(string, StringMD.MD5);
	    
	}
	
	public String getPassword() {
        return this.instance.getPassword();
    }

    public void setPassword(String password) {
    Query query = this.getEntityManager().createQuery("FROM User_Terranvm ut WHERE ut.id = ? and ut.password = ?");
    query.setParameter(1, this.instance.getId());
    query.setParameter(2, this.instance.getPassword());
    
    @SuppressWarnings("unchecked")
    List<User_Terranvm> user_Terranvms = query.getResultList();
	
	if(user_Terranvms.size() > 0 )
		{
		
		}
	else
	password = StringMD.getStringMessageDigest(password, StringMD.MD5);
    this.instance.setPassword(password);

	
    }
}
