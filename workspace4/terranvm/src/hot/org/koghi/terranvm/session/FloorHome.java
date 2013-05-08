package org.koghi.terranvm.session;

import java.util.ArrayList;
import java.util.List;

import org.koghi.terranvm.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("floorHome")
public class FloorHome extends EntityHome<Floor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@In(create = true)
	ConstructionHome constructionHome;

	public void setFloorId(Integer id) {
		setId(id);
	}

	public Integer getFloorId() {
		return (Integer) getId();
	}

	@Override
	protected Floor createInstance() {
		Floor floor = new Floor();
		return floor;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Construction construction = constructionHome.getDefinedInstance();
		if (construction != null) {
			getInstance().setConstruction(construction);
		}
		
	}

	public boolean isWired() {
		if (getInstance().getConstruction() == null)
			return false;
		return true;
	}

	public Floor getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public List<Area> getAreas() {
		return getInstance() == null ? null : new ArrayList<Area>(getInstance()
				.getAreas());
	}
	
	public double totalArea() {
		List<Area> areas = this.getAreas();
		double totalArea = 0;
		for (int i = 0; i < areas.size(); i++) {
			totalArea += areas.get(i).getArea()!=null ? areas.get(i).getArea() : 0 ;
		}
		return totalArea;
	}
	
	public int areasNumber() {
		
		return this.getAreas().size();
	}
	
	@Override
	public String update() {
		new MakerCheckerHome().persistObject(getInstance());
		updatedMessage();
		return "updated";
	}
	
	public boolean floorListInApprove(Floor floor){
		return new MakerCheckerHome().isObjectInMakerChecker(floor);
	}
	
	public void updateInstanceMaker(int makerCheckerId){
		setInstance((Floor) new MakerCheckerHome().getInstance(makerCheckerId));
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

}
