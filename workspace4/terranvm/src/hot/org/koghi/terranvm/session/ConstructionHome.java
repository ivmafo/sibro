package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.Avaluation;
import org.koghi.terranvm.entity.Construction;
import org.koghi.terranvm.entity.Floor;
import org.koghi.terranvm.entity.RealProperty;

@Name("constructionHome")
public class ConstructionHome extends EntityHome<Construction> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(create = true)
	RealPropertyHome realPropertyHome;
	@In(create=true)
	FloorHome floorHome;

	public FloorHome getFloorHome() {
		return floorHome;
	}

	public void setFloorHome(FloorHome floorHome) {
		this.floorHome = floorHome;
	}
	
	 public void persistFloor(){
	    	this.floorHome.persist();
	    	this.instance.getFloors().add(this.floorHome.getInstance());	
	        }

	public void setConstructionId(Integer id) {
		setId(id);
	}

	public Integer getConstructionId() {
		return (Integer) getId();
	}

	@Override
	protected Construction createInstance() {
		Construction construction = new Construction();
		return construction;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		RealProperty realProperty = realPropertyHome.getDefinedInstance();
		if (realProperty != null) {
			getInstance().setRealProperty(realProperty);
		}
	}

	public boolean isWired() {
		if (getInstance().getRealProperty() == null)
			return false;
		return true;
	}

	public Construction getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Floor> getFloors() {
		return getInstance() == null ? null : new ArrayList<Floor>(getInstance().getFloors());
	}
	
	public List<Avaluation> getAvaluations() {
		return getInstance() == null ? null : new ArrayList<Avaluation>(getInstance().getAvaluations());
	}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}
	
	public boolean constructionListInApprove(Construction construction){
		return new MakerCheckerHome().isObjectInMakerChecker(construction);
	}
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((Construction) new MakerCheckerHome().getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}
	
	@Transactional
	public void approveChange(){
		setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}","ApproveSuccessfully");
	}
	
	public void cancelChange(){
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_cancellation}","CancelSuccessfully");
	}
	
	public void setIdConstruction(){
		floorHome.getInstance().setConstruction(this.instance);
	}

}
