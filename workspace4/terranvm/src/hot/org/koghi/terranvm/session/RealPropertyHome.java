package org.koghi.terranvm.session;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.async.Log;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.bean.Format_number;
import org.koghi.terranvm.entity.Address;
import org.koghi.terranvm.entity.Area;
import org.koghi.terranvm.entity.AreaType;
import org.koghi.terranvm.entity.Avaluation;
import org.koghi.terranvm.entity.AvaluationType;
import org.koghi.terranvm.entity.Construction;
import org.koghi.terranvm.entity.ConstructionLicenses;
import org.koghi.terranvm.entity.ContributionModule;
import org.koghi.terranvm.entity.Floor;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.RealPropertyContact;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RentableUnitContribution;
import org.koghi.terranvm.entity.RentableUnitType;
import org.koghi.terranvm.entity.SegmentStage;
import org.richfaces.component.html.HtmlExtendedDataTable;

//import bsh.This;

@Name("realPropertyHome")
public class RealPropertyHome extends EntityHome<RealProperty> {
    /**
	 * 
	 */

    private static final long serialVersionUID = 1L;
    @In(create = true)
    LegalNatureOfPropertyHome legalNatureOfPropertyHome;
    @In(create = true)
    RealPropertyUseTypeHome realPropertyUseTypeHome;
    ConstructionHome constructionHome = new ConstructionHome();
    private Boolean approved;

    // @In(required = false)
    // @Out(required = false)
    // private HtmlExtendedDataTable tableOwnerBind;
    // private Selection selectionOwner;
    // private String tableStateOwner;
    // @DataModel
    // private List<BusinessEntity> owners;
    @In(required = false)
    @Out(required = false)
    private HtmlExtendedDataTable tableOwnerViewBind;

    // @In(required = false)
    // @Out(required = false)
    // private HtmlExtendedDataTable tableLesseeBind;
    // private Selection selectionLessee;
    // private String tableStateLessee;
    // @DataModel
    // private List<BusinessEntity> lessees;
    @In(required = false)
    @Out(required = false)
    private HtmlExtendedDataTable tableLesseeViewBind;

    // CONTRIBUTION MODULE
    @In(required = false)
    @Out(required = false)
    private HtmlExtendedDataTable tableContributionModuleBind;
    @In(required = false)
    @Out(required = false)
    private HtmlExtendedDataTable tableRentUnitContributionBind;
    private String contributionModuleListTableState;
    private String rentUnitContributionListTableState;
    private ContributionModule contributionModule;
    private List<RentableUnit> rentableUnitList;
    private List<RentableUnitContribution> rentableUnitContributionList;
    private List<RentableUnitContribution> rentableUnitContributionListTemp;
    private double contributionModuleSum;
    private int statusContributionModule;// new 0, edit 1, view 2
    private List<ContributionModule> templateList;
    private ContributionModule selectedTemplate;
    private String contributionModuleName;
    private boolean contributionModuleApportionment;
    private boolean isOriginalRentableContributionList;
    // CONTRIBUTION MODULE

    private AreaType areaType;
    public List<AreaType> areatypes;

    private Floor floor;
    private Area area;

    public List<RentableUnitType> rentaUnitTypes;
    private RentableUnit rentableUnit;

    private int edit;

    // validacion area construida por asignar
    private double areaFantaltanteConstruction;
    private double areaFaltanteFloor;
    private double areaFaltanteTA;
    private double areaFaltanteRentableUnit;

    // validacion area arrendable por asignar
    private double rentableAreaConst;
    private double rentableAreaFloor;
    private double rentableArea;
    private double rentableAreaUnit;

    // ValidaciÃ³n de Ã�reas
    private String messageLabel;
    private String messageLabel2;

    private Address address;

    private AddressHome addressHome;

    // tipos de avaluos
    private AvaluationType avaluationType;
    public List<AvaluationType> avaluationTypes;
    private Avaluation avaluation;

    private Construction selectedConstruction;
    private Construction construction = new Construction();
    private Floor selectFloor;

    
 // state deactivate
    private boolean messageRentableUnit;
	private boolean motiveRentableUnit;
	private boolean messageArea;
	private boolean motiveArea;
	private boolean messageFloor;
	private boolean motiveFloor;
	private boolean messageConstruction;
	private boolean motiveConstruction;
	private boolean messageRealProperty;
	private Log log = new Log(this);
	private boolean stateAjax;
	
	//validacion del motivo de cambio o desactivacion
	private String stringmotiveRU;
    @SuppressWarnings("unused")
	private String stringMotiveArea;
	private String stringMotiveFloor;
	private String stringMotiveConstruction;
	private String stringMotiveRealProperty;
	
	/**
	 * la variable se creo para enviar y recibir el id de los pisos en la lista desplegable de pisos,
	 * debido a que al enviar el objeto Floor no funcionaba
	 */
	private Integer selectedFloor;
	
	/**
	 * la variable se creo para enviar y recibir el id de los edificios en la lista desplegable de edificios,
	 * debido a que al enviar el objeto Construction no funcionaba
	 */
	private Integer selectedIdConstruction;
	    

	public RealPropertyHome() {
		this.messageRealProperty = false;
	}
	

    public int getEdit() {
        return edit;
    }

    public void changeEdit(int valor) {
        edit = valor;
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    @SuppressWarnings("unchecked")
    public List<AreaType> getAreasTypes() {
        List<AreaType> areastypes = this.getEntityManager().createQuery("from AreaType order by name").getResultList();
        if (areastypes.size() > 0 && areaType == null) {
            areaType = areastypes.get(0);
        }
        return this.getEntityManager().createQuery("from AreaType order by name").getResultList();
    }

    public void setAreatypes(List<AreaType> areastypes) {
        this.areatypes = areastypes;
    }

    public Floor getSelectFloor() {
        return selectFloor;
    }

    public void setSelectFloor(Floor selectFloor) {
        this.selectFloor = selectFloor;
    }

    public Construction getConstruction() {
        if (construction == null){
            construction = new Construction();
            messageConstruction = false;
            motiveConstruction = false;
        }
        return construction;
    }

    public void instanceConstructionEdit(Construction construction) {
        this.edit = 1;
        this.construction = construction;
    }

    public void instanceConstructionAdd(Construction construction) {
        this.edit = 0;
        this.construction = construction;
        this.floor = new Floor();
    }

    public void instanceFloorEdit(Floor floor) {
    	try {
			this.edit = 1;
			this.floor = floor;
			this.floor.getAreas();
		} catch (org.hibernate.LazyInitializationException e) {
			String sql = "from Floor where id = ?";
			Query query =getEntityManager().createQuery(sql);
			query.setParameter(1, floor.getId());
			this.floor=(Floor)query.getSingleResult(); 
		}
    }

    public void instanceFloorAdd(Floor floor) {
    	try {
			this.edit = 0;
			this.floor = floor;
			this.area =new Area();
			this.floor.getAreas();
		} catch (org.hibernate.LazyInitializationException e) {
			//esta excepcion se produce debio a no se realizan cambios en el piso
			//por lo cual se pueden traer de la base de datos sin afectar los cambios
			String sql = "from Floor where id = ?";
			Query query = getEntityManager().createQuery(sql);
			query.setParameter(1, floor.getId());
			this.floor=(Floor)query.getSingleResult(); 
		}

    }

    public void instanceAreaEdit(Area area) {
        this.edit = 1;
        this.area = area;
    }

    public void instanceAreaAdd(Area area) {
        this.edit = 0;
        this.area = area;
        this.rentableUnit = new RentableUnit();
    }

    public void setConstruction(Construction construction) {
        this.construction = construction;
    }

    public ConstructionHome getConstructionHome() {
        return constructionHome;
    }

    @Transactional
    public void persistConstruction() {
        // this.constructionHome.persist();
        // this.getEntityManager().clear();
        this.construction.setRealProperty(this.getInstance());
        this.construction.setAddress(this.address);
        this.instance.getConstructions().add(this.construction);
        this.stateAjax=this.getConstruction().isDeactivate();
        this.motiveConstruction=false;
        this.messageConstruction=false;
//        this.getEntityManager().persist(this.construction);
//        this.getEntityManager().flush();
//        this.clearConstruction();
    }

    public void setConstructionHome(ConstructionHome constructionHome) {
        this.constructionHome = constructionHome;
    }

    public Integer getRealPropertyId() {
        return (Integer) getId();
    }

    @Override
    protected RealProperty createInstance() {
        RealProperty realProperty = new RealProperty();
        return realProperty;
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
    	messageRealProperty = false;
    	checkPorcentFromRUC();
//        if(checkPorcentFromRUC())
//            return false;
        if ((this.messageLabel != null && this.messageLabel.length() > 0) || (this.messageLabel2 != null && this.messageLabel2.length() > 0))
            return false;
        if (getInstance().getProjectRealProperty() == null || getInstance().getProjectRealProperty().isEmpty())
            return false;
        return true;
    }

    public void setRealPropertyId(Integer id) {
        setId(id);
    }

    public RealProperty getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }

    public List<Address> getAddresses() {
        return getInstance() == null ? null : new ArrayList<Address>(getInstance().getAddresses());
    }

    public List<RealPropertyContact> getRealPropertyContacts() {
        return getInstance() == null ? null : new ArrayList<RealPropertyContact>(getInstance().getRealPropertyContacts());
    }

    public List<ProjectProperty> getProjectProperties() {
        return getInstance() == null ? null : new ArrayList<ProjectProperty>(getInstance().getProjectProperties());
    }

    public List<Construction> getConstructions() {
        return getInstance() == null ? null : new ArrayList<Construction>(getInstance().getConstructions());
    }

    public List<Avaluation> getAvaluations() {
        return getInstance() == null ? null : new ArrayList<Avaluation>(getInstance().getAvaluations());
    }

    public List<ConstructionLicenses> getConstructionLicenseses() {
        return getInstance() == null ? null : new ArrayList<ConstructionLicenses>(getInstance().getConstructionLicenseses());
    }

    public List<SegmentStage> getSegmentStages() {
        return getInstance() == null ? null : new ArrayList<SegmentStage>(getInstance().getSegmentStages());
    }

    public List<Project> getProjectRealproperty() {
        return getInstance() == null ? null : new ArrayList<Project>(getInstance().getProjectRealProperty());
    }

    public List<Avaluation> getConstructionAvaluations() {
        return construction == null ? null : new ArrayList<Avaluation>(construction.getAvaluations());
    }

    @Override
    public String update() {
        String sentence = "SELECT DISTINCT(p.*) FROM project p, project_has_realproperty pr WHERE pr.project = p.id AND pr.realProperty = ?";
        Query query = getEntityManager().createNativeQuery(sentence, Project.class);
        query.setParameter(1, getInstance().getId());
        String respuesta = "";
        if (getInstance().getStep() == RealProperty.STEP_APPROVED) {
        	new MakerCheckerHome().persistObject(getInstance(), query.getResultList().toArray());
            updatedMessage();
            respuesta = "updated";
        } else if (getInstance().getStep() == RealProperty.STEP_DRAFT) {
            respuesta = super.update();
            updatedMessage();
        }
        return respuesta;
    }

	public boolean realPropertyListInApprove(RealProperty realProperty) {
        return new MakerCheckerHome().isObjectInMakerChecker(realProperty);
    }

    public void updateInstanceMaker(int makerCheckerId) {
        setInstance((RealProperty) new MakerCheckerHome().getInstance(makerCheckerId));
    }

    @SuppressWarnings("deprecation")
    @Transactional
    public void approveChange() {
        setInstance(getEntityManager().merge(getInstance()));
        joinTransaction();
        getEntityManager().flush();
        setPendingApproval(false);
        new MakerCheckerHome().deleteMaker(this.getInstance()); 
        BillingFunctions recalculation = new BillingFunctions(this.getEntityManager());
        if(recalculation.recalculateRealProperty(this.instance.getId())){
        	getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_passage}", "ApproveSuccessfully");
        }
        else{
        	getFacesMessages().addFromResourceBundle(Severity.ERROR, "Error ver log en recalculo del activo: "+this.instance.getId(), "ApproveSuccessfully");
        	
        }
    }

    @SuppressWarnings("deprecation")
    @Transactional
    public void cancelChange() {
        new MakerCheckerHome().deleteMaker(this.getInstance());
        setPendingApproval(false);
        getFacesMessages().addFromResourceBundle(Severity.INFO, "#{messages.Successful_cancellation}", "CancelSuccessfully");
    }

    public void setIdRealProperty() {
			this.clearAddressHome();
			constructionHome.getInstance().setRealProperty(this.instance);
			this.construction = new Construction();
			
    }

    public List<Construction> getConstructionss() {
        ArrayList<Construction> m =  new ArrayList<Construction>(getInstance().getConstructions());
        if (m != null && !m.isEmpty() && this.selectedConstruction == null) {
            this.selectedConstruction = m.get(0);
            this.construction = this.selectedConstruction;
            if (this.selectedConstruction.getFloors()!=null && !this.selectedConstruction.getFloors().isEmpty()){
            	setFloor(this.selectedConstruction.getFloors().get(0));
            	this.selectedFloor = this.getFloor().getId();
            }else
            	setFloor(null);
        }
        return m;
    }

    public Construction getSelectedConstruction() {
    	
        return selectedConstruction;
    }

    public void setSelectedConstruction(Construction selectedConstruction) {
        this.selectedConstruction = selectedConstruction;
        this.construction = this.selectedConstruction;
        try {
			this.construction.getFloors();
		} catch (org.hibernate.LazyInitializationException e) {
			String sql = "from Construction where id = ?";
			Query q = getEntityManager().createQuery(sql);
			q.setParameter(1, construction.getId());
			this.construction = (Construction)q.getSingleResult();
		}
		System.out.println("*********************" + construction.getId());
    }
    
    public Integer getSelectedIdConstruction() {
		return selectedIdConstruction;
	}


	public void setSelectedIdConstruction(Integer selectedIdConstruction) {
		this.selectedIdConstruction = selectedIdConstruction;
		for(Construction construction: getConstructionss()){
			if (construction.getId() == selectedIdConstruction.intValue()) {
				setSelectedConstruction(construction);
				setSelectedFloor(construction.getFloors()!=null && !construction.getFloors().isEmpty() ? construction.getFloors().get(0).getId() : null);
				break;
			}
		}
	}
	
	public List<SelectItem> getSelectedConstructionList() {
		List<SelectItem> options = new ArrayList<SelectItem>();
		for (Construction construction: getConstructionss()) {
			SelectItem option = new SelectItem();
			option.setLabel(construction.getName());
			option.setValue(construction.getId());
			options.add(option);
		}
		return options;
	}

    @Transactional
    public void persitRowConstruction() {
        this.getEntityManager().joinTransaction();
        this.floor.setConstruction(this.construction);
        this.getEntityManager().persist(this.floor);
        List<Floor> floor = this.construction.getFloors();
        floor.add(getFloor());
        this.construction.setFloors(floor);
        // this.construction.getFloors().add(this.floor);
        this.getEntityManager().flush();
        this.clearFloor();

    }

    @Transactional
    public void persitRowConstruction2() {
        this.floor.setConstruction(this.construction);
        this.stateAjax = this.getFloor().isDeactivate();
        this.motiveFloor=false;
        this.messageFloor=false;
        // this.construction.getFloors().add(this.floor);
    }

    @Transactional
    public void persitRowFloor() {
        this.area.setFloor(this.floor);
        this.area.setAreaType(this.areaType);
        this.getEntityManager().joinTransaction();
        this.getEntityManager().persist(this.area);
        List<Area> areas = this.floor.getAreas();
        areas.add(this.area);
        this.floor.setAreas(areas);
        this.getEntityManager().flush();
        this.clearArea();
    }

    @Transactional
    public void persitRowFloor2() {
        this.area.setFloor(this.floor);
        this.area.setAreaType(this.areaType);
        this.stateAjax = this.getArea().isDeactivate();
        this.motiveArea=false;
        this.messageArea=false;
        //this.floor.getAreas().add(this.area);
    }

    public Floor getFloor() {
        if (floor == null) {
            this.floor = new Floor();
            this.messageFloor = false;
			this.motiveFloor = false;
        }
        return floor;
    }
    
    /**
     * el metodo recibe un piso y y verifica sus areas, si lanza alguna la excepcion org.hibernate.LazyInitializationException  significa que no se realizo ningun cambio  
     * por ende se puede traer el piso de la base de datos.
     * @param floor
     */
    public void setFloor(Floor floor) {
        this.floor = floor;
        try{
        	this.floor.getAreas();
        	this.stateAjax = this.floor.isDeactivate();
    	}catch (org.hibernate.LazyInitializationException e){
    		String sql = "from Floor where id = ?";
    		Query q = getEntityManager().createQuery(sql);
    		q.setParameter(1, floor.getId());
    		this.floor = (Floor)q.getSingleResult();
    	}catch (NullPointerException e) {
    		this.stateAjax = false;
		}
    }
    
    
    public Integer getSelectedFloor() {
		return selectedFloor;
	}

    /**
     * el metodo recibe el id del piso seleccionado, luego lo busca y lo asigna a la variable
     * del home llamada floor. 
     * @param selectdFloor
     */
	public void setSelectedFloor(Integer selectdFloor) {
		this.selectedFloor = selectdFloor;
		
		if (selectdFloor == null){
			setFloor(null);
		}else{
			for (Floor floor : selectedConstruction.getFloorsList()) {
				if (floor.getId()==selectdFloor.intValue()){
					setFloor(floor);
					break;
				}
			}
		}
	}
	/**
	 * este metodo se encarga de obtener manualmente la lista de pisos de
	 * la variable selectedConstruction
	 * @return retorna un list de tipo SelectItem con la lista de pisos 
	 */
	public List<SelectItem> getSelectedFloorList() {
		if (this.selectedConstruction!= null) {
			List<SelectItem> options = new ArrayList<SelectItem>();
			for (Floor floor : selectedConstruction.getFloorsList()) {
				SelectItem option = new SelectItem();
				option.setLabel(floor.getFloorNumber());
				option.setValue(floor.getId());
				options.add(option);
			}
			return options;
		}else
			return null;
	}
    
    
    public Area getArea() {
        if (area == null) {
            this.area = new Area();
            setMessageArea(false);
        	setMotiveArea(false);
        }
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
        areaType = this.area.getAreaType();
        this.stateAjax = this.area.isDeactivate();
    }

    public List<Floor> getFloors() {
        if (this.selectedConstruction != null && this.selectedConstruction.getFloors() != null) {
            if (floor == null && this.selectedConstruction.getFloors().size() > 0)
                floor = this.selectedConstruction.getFloors().iterator().next();
            return new ArrayList<Floor>(selectedConstruction.getFloors());
        }
        return new ArrayList<Floor>();
    }

    public void clearFloor() {
        try {
            getEntityManager().refresh(this.floor);
        } catch (RuntimeException e) {}
        this.floor = new Floor();
        this.clearMessageLabel();
        this.messageFloor = false;
		this.motiveFloor = false;
    }

    public void clearConstruction() {
        try {
            getEntityManager().refresh(this.construction);
        } catch (RuntimeException e) {}
        this.construction = new Construction();
        this.clearMessageLabel();
        construction = new Construction();
        messageConstruction = false;
        motiveConstruction = false;
    }

    public void clearArea() {
        try {
            getEntityManager().refresh(this.area);
        } catch (RuntimeException e) {}
        this.area = new Area();
        areaType = new AreaType();
        this.clearMessageLabel();
        setMessageArea(false);
    	setMotiveArea(false);
    }

    public void clearListFloor() {
        this.construction = new Construction();
        this.clearMessageLabel();
    }

    public void clearListAreas() {
        this.floor = new Floor();
        this.clearMessageLabel();
    }

    public void clearMessageLabel() {
        messageLabel = "";
        messageLabel2 = "";
    }

    public RentableUnit getRentableUnit() {
        if (rentableUnit == null) {
            this.rentableUnit = new RentableUnit();
            messageRentableUnit = false;
            motiveRentableUnit = false;
        }
        return rentableUnit;
    }

    public void setRentableUnit(RentableUnit rentableUnit) {
        this.rentableUnit = rentableUnit;
        this.stateAjax=this.rentableUnit.isDeactivate();
    }

    @Transactional
    public void persitRowRentableUnit() {
        this.rentableUnit.setArea(this.area);
        //this.getEntityManager().joinTransaction();
        this.addRentableUnitToContributionModule(this.rentableUnit);
        this.area.getRentableUnits().add(this.rentableUnit);
        if (this.instance.getId() > 0) {
            List<ContributionModule> contributionModules = this.instance.getContributionModules();
            for (ContributionModule contributionModule : contributionModules) {
                RentableUnitContribution rentableUnitContribution = new RentableUnitContribution();
                rentableUnitContribution.setContributionModule(contributionModule);
                rentableUnitContribution.setContributionRate(0.0);
                rentableUnitContribution.setContributionRatePorcentual(0.0);
                rentableUnitContribution.setRentableUnit(rentableUnit);
                contributionModule.getRentableUnitContributionsList().add(rentableUnitContribution);
            }
        }

        this.clearRentableUnit();
        this.rentableUnitList = null;

    }

    @Transactional
    public void persitRowRentableUnit2() {
        this.rentableUnit.setArea(this.area);
        this.stateAjax = this.getRentableUnit().isDeactivate(); 
        this.motiveRentableUnit=false;
        this.messageRentableUnit=false;
    }

    public void clearRentableUnit() {
        try {
            getEntityManager().refresh(this.rentableUnit);
        } catch (RuntimeException e) {}
        this.rentableUnit = new RentableUnit();
        this.clearMessageLabel();
        messageRentableUnit = false;
        motiveRentableUnit = false;

    }

    public HtmlExtendedDataTable getTableOwnerViewBind() {
        return tableOwnerViewBind;
    }

    public void setTableOwnerViewBind(HtmlExtendedDataTable tableOwnerViewBind) {
        this.tableOwnerViewBind = tableOwnerViewBind;
    }

    // public void onSelectionOwnerViewChanged() {
    // if (selectionOwner != null) {
    // System.out.println("Selected keys: ");
    // Iterator it = selectionOwner.getKeys();
    // while (it.hasNext()) {
    // Object key = it.next();
    // System.out.println("key: " + key);
    // System.out
    // .println("tableOwnerViewBind state" + tableStateOwner);
    // tableOwnerViewBind.setRowKey(key);
    // if (tableOwnerViewBind.isRowAvailable()) {
    // if (rentableUnit == null)
    // rentableUnit = new RentableUnit();
    // rentableUnit
    // .setBusinessEntityByOwner((BusinessEntity) tableOwnerViewBind
    // .getRowData());
    // }
    // }
    // } else {
    // System.out.println("No SelectionOwnerChanged is set.");
    // }
    // }

    public HtmlExtendedDataTable getTableLesseeViewBind() {
        return tableLesseeViewBind;
    }

    public void setTableLesseeViewBind(HtmlExtendedDataTable tableLesseeViewBind) {
        this.tableLesseeViewBind = tableLesseeViewBind;
    }

    // public void onSelectionLesseeViewChanged() {
    // if (selectionLessee != null) {
    // System.out.println("Selected keys: ");
    // Iterator it = selectionLessee.getKeys();
    // while (it.hasNext()) {
    // Object key = it.next();
    // System.out.println("key: " + key);
    // System.out.println("tableLesseeViewBind state"
    // + tableStateLessee);
    // tableLesseeViewBind.setRowKey(key);
    // if (tableLesseeViewBind.isRowAvailable()) {
    // if (rentableUnit == null)
    // rentableUnit = new RentableUnit();
    // rentableUnit
    // .setBusinessEntityByLessee((BusinessEntity) tableLesseeViewBind
    // .getRowData());
    // }
    // }
    // } else {
    // System.out.println("No SelectionLesseeChanged is set.");
    // }
    // }

    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // START OF CONTRIBUTION MODULE FUNCTIONS

    public void useTemplateForActualContributionModule() {
        Locale.setDefault(Locale.ENGLISH);
        List<RentableUnitContribution> temp = this.selectedTemplate.getRentableUnitContributions();
        if (this.isOriginalRentableContributionList) {
            this.rentableUnitContributionListTemp = new ArrayList<RentableUnitContribution>();
        }
        DecimalFormat df = new DecimalFormat("#.##");
        for (int i = 0; i < this.rentableUnitContributionList.size(); i++) {
            double rate = Double.parseDouble(df.format(temp.get(i).getContributionRate()));
            if (this.isOriginalRentableContributionList) {
                this.rentableUnitContributionListTemp.add((RentableUnitContribution) this.rentableUnitContributionList.get(i).clone());
            }
            this.rentableUnitContributionList.get(i).setContributionRate(rate);

        }
        if (this.isOriginalRentableContributionList) {
            this.isOriginalRentableContributionList = false;
        }
    }

    public void revertTemplateForActualContributionModule() {
        Locale.setDefault(Locale.ENGLISH);

        if (this.contributionModule != null && this.rentableUnitContributionListTemp != null) {
            List<RentableUnitContribution> rucTempList = new ArrayList<RentableUnitContribution>();
            for (RentableUnitContribution ruc : this.rentableUnitContributionListTemp) {
                rucTempList.add((RentableUnitContribution) ruc.clone());
            }
            this.contributionModule.getRentableUnitContributions().clear();
            this.contributionModule.setRentableUnitContributions(rucTempList);
            this.rentableUnitContributionListTemp = null;
        }
    }

    public void addRentableUnitToContributionModule(RentableUnit ru) {
        System.out.println("***************************************** ADDING RUC to contribution Modules");
        Locale.setDefault(Locale.ENGLISH);
        DecimalFormat df = new DecimalFormat("#.##");
        if (this.instance.getId() > 0) {
            for (ContributionModule cm : this.instance.getContributionModules()) {
                RentableUnitContribution ruc = new RentableUnitContribution();
                ruc.setContributionModule(cm);
                ruc.setContributionRate(Double.parseDouble(df.format(0.0)));
                ruc.setRentableUnit(ru);
                cm.getRentableUnitContributions().add(ruc);
            }
        }
        this.rentableUnitList = null;
    }

    public void destroyInstanceContributionModule() {
        this.revertTemplateForActualContributionModule();
        this.rentableUnitContributionList = null;
    }

    public boolean validateContributionModule() {
        Locale.setDefault(Locale.ENGLISH);
        DecimalFormat df = new DecimalFormat("#.##");
        double sum = 0;
        for (RentableUnitContribution ruc : this.rentableUnitContributionList) {
            sum += ruc.getContributionRatePorcentual();
        }
        this.contributionModuleSum = Double.parseDouble(df.format(sum));
        if (this.contributionModuleSum == 100.0 && this.contributionModuleApportionment == true) {
            return true;
        } else if (this.contributionModuleSum != 100.0 && this.contributionModuleApportionment == true)
            return false;
        else
            return true;

    }

    public void newContributionModule() {
        this.contributionModule = new ContributionModule();
        this.contributionModuleName = "";
        this.contributionModuleApportionment = true;
        if (this.instance.getId() > 0)
            this.contributionModule.setRealProperty(this.instance);
    }

    public void instanceContributionModuleAdd() {
        this.newContributionModule();
        this.rentableUnitContributionList = null;
        this.templateList = null;
        this.getRentableUnitContributionList();
        this.statusContributionModule = 0;
    }

    public void instanceContributionModuleEdit(ContributionModule contributionModule) {
        this.statusContributionModule = 1;
        this.instanceContributionModule(contributionModule);
        this.templateList = null;
        this.getTemplateList();
    }

    public void instanceContributionModuleView(ContributionModule contributionModule) {
        this.statusContributionModule = 2;
        this.instanceContributionModule(contributionModule);
    }

    public void instanceContributionModule(ContributionModule contributionModule) {
        this.contributionModule = contributionModule;
        this.contributionModuleName = this.contributionModule.getName();
        this.contributionModuleApportionment = this.contributionModule.isApportionment();
        this.isOriginalRentableContributionList = true;
        this.rentableUnitContributionList = this.contributionModule.getRentableUnitContributions();
        if (this.rentableUnitContributionList == null || this.rentableUnitContributionList.isEmpty()) {
            this.rentableUnitContributionList = null;
            this.getRentableUnitContributionList();
        }

    }

    @Transactional
    public void saveContributionModule() {
        this.contributionModule.setName(this.contributionModuleName);
        this.contributionModule.setApportionment(this.contributionModuleApportionment);
        this.saveRentableUnitContributionList();
        if (this.instance.getId() > 0)
            this.instance.getContributionModules().add(this.contributionModule);
    }

    @Transactional
    public void saveContributionModuleWithoutAddingToList() {
        this.contributionModule.setName(this.contributionModuleName);
        this.contributionModule.setApportionment(this.contributionModuleApportionment);
    }

    @SuppressWarnings("unchecked")
    public List<RentableUnit> getRentableUnitList() {
        if (this.rentableUnitList == null && this.instance.getId() > 0) {
            Query query = this.getEntityManager().createQuery("FROM RentableUnit ru WHERE ru.area.floor.construction.realProperty = ?");
            query.setParameter(1, this.instance);
            this.rentableUnitList = (List<RentableUnit>) query.getResultList();
        }
        return rentableUnitList;
    }

    public void setRentableUnitList(List<RentableUnit> rentableUnitList) {
        this.rentableUnitList = rentableUnitList;
    }

    public List<RentableUnitContribution> getRentableUnitContributionList() {
        Locale.setDefault(Locale.ENGLISH);
        if ((this.rentableUnitContributionList == null || this.rentableUnitContributionList.isEmpty()) && this.getRentableUnitList() != null && !this.getRentableUnitList().isEmpty()) {

            List<RentableUnit> rentUnits = this.getRentableUnitList();
            this.rentableUnitContributionList = new ArrayList<RentableUnitContribution>();
            for (RentableUnit rentableUnit : rentUnits) {

                    RentableUnitContribution ruc = new RentableUnitContribution();
                    ruc.setContributionModule(this.contributionModule);
                    ruc.setContributionRate(0.0);
                    ruc.setContributionRatePorcentual(0.0);
                    ruc.setRentableUnit(rentableUnit);
                    this.rentableUnitContributionList.add(ruc);
            }
        }
        if (this.getRentableUnitList() != null && this.getRentableUnitList().isEmpty())
            this.rentableUnitContributionList = new ArrayList<RentableUnitContribution>();
        return rentableUnitContributionList;
    }

    @Transactional
    public void saveRentableUnitContributionList() {
        this.saveContributionModuleWithoutAddingToList();
        List<RentableUnitContribution> rucTempList = new ArrayList<RentableUnitContribution>();
        for (RentableUnitContribution ruc : this.rentableUnitContributionList) {
            rucTempList.add((RentableUnitContribution) ruc.clone());
        }
        this.contributionModule.setRentableUnitContributions(rucTempList);
    }

    public void setRentableUnitContributionList(List<RentableUnitContribution> rentableUnitContributionList) {
        this.rentableUnitContributionList = rentableUnitContributionList;
    }

    public String getRentUnitContributionListTableState() {
        return rentUnitContributionListTableState;
    }

    public void setRentUnitContributionListTableState(String rentUnitContributionListTableState) {
        this.rentUnitContributionListTableState = rentUnitContributionListTableState;
    }

    public HtmlExtendedDataTable getTableRentUnitContributionBind() {
        return tableRentUnitContributionBind;
    }

    public void setTableRentUnitContributionBind(HtmlExtendedDataTable tableRentUnitContributionBind) {
        this.tableRentUnitContributionBind = tableRentUnitContributionBind;
    }

    public double getContributionModuleSum() {
        return contributionModuleSum;
    }

    public void setContributionModuleSum(double contributionModuleSum) {
        this.contributionModuleSum = contributionModuleSum;
    }

    public List<ContributionModule> getTemplateList() {
        if (this.instance.getId() > 0) {
            this.templateList = new ArrayList<ContributionModule>();
            for (ContributionModule cm : this.instance.getContributionModules()) {
                List<RentableUnitContribution> temp = cm.getRentableUnitContributions();
                if (temp != null && !temp.isEmpty()) {
                    if (this.contributionModule != null && cm.getId() != this.contributionModule.getId() && cm.getId() > 0) {
                        this.templateList.add(cm);
                    }
                }
            }
            if (!this.templateList.isEmpty() && this.selectedTemplate == null) {
                this.selectedTemplate = this.templateList.get(0);
            }
        }
        return templateList;
    }

    public void setTemplateList(List<ContributionModule> templateList) {
        this.templateList = templateList;
    }

    public String getContributionModuleListTableState() {
        return contributionModuleListTableState;
    }

    public void setContributionModuleListTableState(String contributionModuleListTableState) {
        this.contributionModuleListTableState = contributionModuleListTableState;
    }

    public HtmlExtendedDataTable getTableContributionModuleBind() {
        return tableContributionModuleBind;
    }

    public void setTableContributionModuleBind(HtmlExtendedDataTable tableContributionModuleBind) {
        this.tableContributionModuleBind = tableContributionModuleBind;
    }

    public ContributionModule getContributionModule() {
        return contributionModule;
    }

    public void setContributionModule(ContributionModule contributionModule) {
        this.contributionModule = contributionModule;
    }

    public ContributionModule getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(ContributionModule selected) {
        this.selectedTemplate = selected;
    }

    public int getStatusContributionModule() {
        return statusContributionModule;
    }

    public void setStatusContributionModule(int statusContributionModule) {
        this.statusContributionModule = statusContributionModule;
    }

    public String getContributionModuleName() {
        return contributionModuleName;
    }

    public void setContributionModuleName(String contributionModuleName) {
        this.contributionModuleName = contributionModuleName;
    }

    public boolean isContributionModuleApportionment() {
        return contributionModuleApportionment;
    }

    public void setContributionModuleApportionment(boolean contributionModuleApportionment) {
        this.contributionModuleApportionment = contributionModuleApportionment;
    }

    public boolean whitapportionment() {
        if (this.contributionModuleApportionment == false)
            return true;
        else
            return false;
    }

    // END OF CONTRIBUTION MODULE FUNCTIONS
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////////////////////

    public String getMessageLabel() {
        return messageLabel;
    }

    public void setMessageLabel(String messageLabel) {
        this.messageLabel = messageLabel;
    }

    public String getMessageLabel2() {
        return messageLabel2;
    }

    public void setMessageLabel2(String messageLabel2) {
        this.messageLabel2 = messageLabel2;
    }

    public void realPropertyAreaValidation() {
        Set<Construction> contructions = this.getInstance().getConstructions();
        double areaTotal = 0;
        double areaTotalRentable = 0;
        if (this.getInstance().getConstructions().size() == 0) {}
        for (Construction construction : contructions) {
            areaTotal += construction.getArea() != null ? construction.getArea() : 0;
            areaTotalRentable += construction.getTotalRentableArea();
        }

        if (areaTotal > this.getInstance().getTotalContructionArea()) {
            this.messageLabel = "Áreas Invalida. La suma de áreas de los edificios es mayor a la del Activo";
        } else {
            this.messageLabel = "";
        }

        if (areaTotalRentable > this.getInstance().getTotalRentableArea()) {
            this.messageLabel2 = "La suma del area arrendable de los edificios es mayor al area arrendable del activo";
        } else
            this.messageLabel2 = "";
    }

    public boolean validateRentableArea() {
        Set<Construction> contructions = this.getInstance().getConstructions();
        double areaTotalRentable = 0;
        double areaTotal = 0;
        this.rentableAreaConst = this.getInstance().getTotalRentableArea();
        this.areaFantaltanteConstruction = this.getInstance().getTotalContructionArea();
        if (construction != null && construction.getId() == 0) {
            areaTotalRentable = construction.getTotalRentableArea();
            rentableAreaConst -= areaTotalRentable;

            areaTotal = construction.getArea() != null ? construction.getArea() : 0;
            areaFantaltanteConstruction -= areaTotal;

        }
        for (Construction construction : contructions) {
            areaTotalRentable = construction.getTotalRentableArea();
            rentableAreaConst -= areaTotalRentable;
            areaTotal = construction.getArea() != null ? construction.getArea() : 0;
            areaFantaltanteConstruction -= areaTotal;
        }
        areaTotal = Format_number.Format(areaTotal);
        areaTotalRentable = Format_number.Format(areaTotalRentable);
        rentableAreaConst = Format_number.Format(rentableAreaConst);
        areaFantaltanteConstruction = Format_number.Format(areaFantaltanteConstruction);
        if (rentableAreaConst != 0.0) {
            return false;
        }
        if (areaFantaltanteConstruction != 0.0) {
            return false;
        }
        return true;
    }

    public boolean validateRentableAreaFloor() {
        List<Floor> floors = new ArrayList<Floor>(construction.getFloors());
        double areaTotalRentable = 0;
        double areaTotal = 0;
        this.rentableAreaFloor = this.construction.getTotalRentableArea();
        this.areaFaltanteFloor = this.construction.getArea() != null ? this.construction.getArea() : 0;
        if (floor != null && floor.getId() == 0) {
            areaTotalRentable = floor.getTotalRentableArea();
            rentableAreaFloor -= areaTotalRentable;
            areaTotal = floor.getArea() != null ? floor.getArea() : 0;
            areaFaltanteFloor -= areaTotal;
        }
        for (Floor floor : floors) {
            areaTotalRentable = floor.getTotalRentableArea();
            rentableAreaFloor -= areaTotalRentable;
            areaTotal = floor.getArea() != null ? floor.getArea() : 0;
            areaFaltanteFloor -= areaTotal;
        }

        rentableArea = Format_number.Format(rentableArea);
        rentableAreaFloor = Format_number.Format(rentableAreaFloor);
        areaFaltanteTA = Format_number.Format(areaFaltanteTA);
        areaFaltanteFloor = Format_number.Format(areaFaltanteFloor);
        if (rentableAreaFloor != 0.0)
            return false;
        if (areaFaltanteFloor != 0.0)
            return false;
        return true;
    }

    public boolean validateRentableAreaArea() {
        List<Area> areas = this.floor.getAreas();

        double areaTotalRentable = 0;
        double areaTotal = 0;

        this.rentableArea = this.floor.getTotalRentableArea();
        this.areaFaltanteTA = this.floor.getArea() != null ? this.floor.getArea() : 0;
        if (area != null && area.getId() == 0) {
            areaTotalRentable = area.getTotalRentableArea();
            rentableArea -= areaTotalRentable;
            areaTotal = area.getArea() != null ? area.getArea() : 0;
            areaFaltanteTA -= areaTotal;

        }
        for (Area area : areas) {
            areaTotalRentable = area.getTotalRentableArea();
            rentableArea -= areaTotalRentable;
            areaTotal = area.getArea() != null ? area.getArea() : 0;
            areaFaltanteTA -= areaTotal;
        }
        rentableArea = Format_number.Format(rentableArea);
        areaFaltanteTA = Format_number.Format(areaFaltanteTA);
        if (rentableArea != 0.0) {
            return false;
        }
        if (areaFaltanteTA != 0.0) {
            return false;
        }
        return true;
    }

    public boolean validateRentableAreaRentableUnit() {
//
//        try {
//            getEntityManager().refresh(this.area);
//        } catch (RuntimeException ex) {}
        List<RentableUnit> rentableUnits = this.area.getRentableUnits();
        double areaTotalRentable = 0;
        double areaTotal = 0;
        this.rentableAreaUnit = this.area.getTotalRentableArea();
        this.areaFaltanteRentableUnit = this.area.getArea() != null ? this.area.getArea() : 0;
        if (rentableUnit != null && rentableUnit.getId() == 0) {
            areaTotalRentable = rentableUnit.getTotalRentableArea();
            rentableAreaUnit -= areaTotalRentable;
            areaTotal = rentableUnit.getMeters() != null ? rentableUnit.getMeters() : 0;
            areaFaltanteRentableUnit -= areaTotal;
        }

        for (RentableUnit rentableUnit : rentableUnits) {

            areaTotalRentable = rentableUnit.getTotalRentableArea();
            rentableAreaUnit -= areaTotalRentable;
            areaTotal = rentableUnit.getMeters() != null ? rentableUnit.getMeters() : 0;
            areaFaltanteRentableUnit -= areaTotal;
        }
        rentableAreaUnit = Format_number.Format(rentableAreaUnit);
        areaFaltanteRentableUnit = Format_number.Format(areaFaltanteRentableUnit);
        if (rentableAreaUnit != 0.0)
            return false;
        if (areaFaltanteRentableUnit != 0.0) {
            return false;
        }

        return true;
    }

    public void constructionAreaValidation() {
        validateRentableArea();

        List<Floor> floors = new ArrayList<Floor>(construction.getFloors());
        double areaTotal = 0;
        double areaTotalRentable = 0;

        for (Floor floor : floors) {
            areaTotal += floor.getArea() != null ? floor.getArea() : 0;
            areaTotalRentable += floor.getTotalRentableArea();
        }

        // Mensaje de area de construcción
        if (construction.getArea() != null && areaTotal > construction.getArea()) {
            this.messageLabel = "Áreas Invalida. La suma de áreas de los pisos es mayor a la del Edificio";
        } else
            this.messageLabel = "";

        // Mensaje de area arrendable
        if (areaTotalRentable > construction.getTotalRentableArea()) {
            this.messageLabel2 = "La suma arrendable de los pisos es mayor al area arrendable del Edificio";
        } else
            this.messageLabel2 = "";

        Set<Construction> contructions = this.getInstance().getConstructions();
        areaTotal = 0;
        areaTotalRentable = 0;
        for (Construction construction : contructions) {
            areaTotal += (construction.getArea() != null ? construction.getArea() : 0);
            areaTotalRentable += construction.getTotalRentableArea();

        }

        if (this.construction.getId() == 0) {
            areaTotal += this.construction.getArea() != null ? construction.getArea() : 0;
            areaTotalRentable += this.construction.getTotalRentableArea();
        }

        // Mensaje de area de construcción
        if (areaTotal > this.getInstance().getTotalContructionArea() && this.messageLabel.isEmpty()) {
            this.messageLabel = "Áreas Invalida. La suma de áreas de los edificios es mayor a la del Activo";
        }
        // Mensaje de area arrendable
        if (areaTotalRentable > this.getInstance().getTotalRentableArea() && this.messageLabel2.isEmpty())
            this.messageLabel2 = "La suma del area arrendable de los edificios es mayor al area arrendable del activo";

    }

    public void floorAreaValidation() {
        validateRentableAreaFloor();

        List<Area> areas = this.floor.getAreas();
        double areaTotal = 0;
        double areaTotalRentable = 0;
        if (this.floor.getAreas().size() == 0) {}
        for (Area area : areas) {
            areaTotal += area.getArea() != null ? area.getArea() : 0;
            areaTotalRentable += area.getTotalRentableArea();
        }

        // Mensaje de area de construcción
        if (areaTotal > (floor.getArea() != null ? floor.getArea() : 0)) {
            this.messageLabel = "Área Invalida. La suma de áreas de las zonas es mayor a la del piso";

        } else
            this.messageLabel = "";

        // Mensaje de area arrendale
        if (areaTotalRentable > floor.getTotalRentableArea())
            this.messageLabel2 = "La suma del area arrendable de la tipologia de area es mayor al area arrendable del piso";
        else
            this.messageLabel2 = "";

        List<Floor> floors = new ArrayList<Floor>(construction.getFloors());
        areaTotal = 0;
        areaTotalRentable = 0;
        for (Floor floor : floors) {
            areaTotal += floor.getArea() != null ? floor.getArea() : 0;
            areaTotalRentable += floor.getTotalRentableArea();
        }

        if (this.floor.getId() == 0) {
            areaTotal += this.floor.getArea() != null ? this.floor.getArea() : 0;
            areaTotalRentable += this.floor.getTotalRentableArea();
        }

        // Mensaje de area de construcción
        if (areaTotal > (this.construction.getArea() != null ? this.construction.getArea() : 0) && this.messageLabel.isEmpty())
            this.messageLabel = "Área Invalida. La suma de áreas de los pisos es mayor a la del Edificio";

        // Mensaje de area arrendable
        if (areaTotalRentable > this.construction.getTotalRentableArea() && this.messageLabel2.isEmpty())
            this.messageLabel2 = "La suma del area arrendable de los pisos es mayor al area arrendable del Edificio";

    }

    public void areaAreaValidation() {
        validateRentableAreaArea();
        List<RentableUnit> rentableUnits = this.area.getRentableUnits();
        double areaTotal = 0;
        double areaTotalRentable = 0;
        if (this.area.getRentableUnits().size() == 0) {}

        for (RentableUnit rentableUnit : rentableUnits) {
            areaTotal += rentableUnit.getMeters() != null ? rentableUnit.getMeters() : 0;
            areaTotalRentable += rentableUnit.getTotalRentableArea();
        }

        // Mensaje de area de construcción
        if (areaTotal > (area.getArea() != null ? area.getArea() : 0)) {
            this.messageLabel = "Área Invalida. La suma de áreas de las Unidades Arrendables es mayor a la de la zona";
        } else
            this.messageLabel = "";

        // Mensaje de area arrendable
        if (areaTotalRentable > area.getTotalRentableArea()) {
            this.messageLabel2 = "La suma del area arrendable es mayor al area arrendable de la tipologia de area";
        } else
            this.messageLabel2 = "";

        List<Area> areas = this.floor.getAreas();
        areaTotal = 0;
        areaTotalRentable = 0;
        for (Area area : areas) {
            areaTotal += area.getArea() != null ? area.getArea() : 0;
            areaTotalRentable += area.getTotalRentableArea();
        }

        if (this.area.getId() == 0) {
            areaTotal += this.area.getArea() != null ? this.area.getArea() : 0;
            areaTotalRentable += this.area.getTotalRentableArea();
        }

        // Mensaje de area de construcción
        if (areaTotal > (this.floor.getArea() != null ? this.floor.getArea() : 0) && this.messageLabel.isEmpty()) {
            this.messageLabel = "Área Invalida. La suma de áreas de las zonas es mayor a la del Piso";
        }

        // Mensaje de area arrendable
        if (areaTotalRentable > this.floor.getTotalRentableArea() && messageLabel2.isEmpty()) {
            this.messageLabel2 = "La suma de las areas arrendables de la tipologia de area es mayor a la area arrendable del piso";
        }

    }

    public void rentableUnitAreaValidation() {
        validateRentableAreaRentableUnit();
        List<RentableUnit> rentableUnits = this.area.getRentableUnits();
        double areaTotal = 0;
        double areaTotalRentable = 0;
        for (RentableUnit rentableUnit : rentableUnits) {
            areaTotal += (rentableUnit.getMeters() != null) ? rentableUnit.getMeters() : 0;
            areaTotalRentable += rentableUnit.getTotalRentableArea();
        }

        if (this.rentableUnit.getId() == 0) {
            areaTotal += this.rentableUnit.getMeters() != null ? this.rentableUnit.getMeters() : 0;
            areaTotalRentable += this.rentableUnit.getTotalRentableArea();
        }

        // Mensaje de area de construcción
        if (areaTotal > (this.area.getArea() != null ? this.area.getArea() : 0)) {
            this.messageLabel = "Área Invalida. La suma de áreas de las unidades arrendables es mayor a la de la zona";
        } else
            this.messageLabel = "";

        // Mensaje de area arrendable
        if (areaTotalRentable > this.area.getTotalRentableArea())
            this.messageLabel2 = "La suma del area arrendable de las unidades arrendables es mayor a la de la zona";
        else {
            this.messageLabel2 = "";
        }
    }

    public boolean checkProindiviso() {
        boolean ProindSelected = false;
        if (getInstance().getLegalNatureOfProperty() != null) {
            if (this.getInstance().getLegalNatureOfProperty().getId() == 4)
                ProindSelected = true;
            else
                ProindSelected = false;
        }
        return ProindSelected;
    }

    public boolean checkProindiviso2() {
        boolean ProindSelected2 = true;
        if (getInstance().getLegalNatureOfProperty() != null) {
            if (this.getInstance().getLegalNatureOfProperty().getId() == 4)
                ProindSelected2 = true;
            else
                ProindSelected2 = false;
            System.out.println(this.getInstance().getLegalNatureOfProperty().getName() + "     si es este ");
        }
        return ProindSelected2;

    }

    public double getAreaFantaltanteConstruction() {
        return areaFantaltanteConstruction;
    }

    public void setAreaFantaltanteConstruction(double areaFantaltanteConstruction) {
        this.areaFantaltanteConstruction = areaFantaltanteConstruction;
    }

    public double getAreaFaltanteFloor() {
        return areaFaltanteFloor;
    }

    public void setAreaFaltanteFloor(double areaFaltanteFloor) {
        this.areaFaltanteFloor = areaFaltanteFloor;
    }

    public double getAreaFaltanteTA() {
        DecimalFormat df = new DecimalFormat("#.####");
        df.format(areaFaltanteTA);
        return areaFaltanteTA;
    }

    public void setAreaFaltanteTA(double areaFaltanteTA) {
        this.areaFaltanteTA = areaFaltanteTA;
    }

    public double getAreaFaltanteRentableUnit() {
        return areaFaltanteRentableUnit;
    }

    public void setAreaFaltanteRentableUnit(double areaFaltanteRentableUnit) {
        this.areaFaltanteRentableUnit = areaFaltanteRentableUnit;
    }

    public Address getAddress() {
        if (address == null) {
            this.address = new Address();
        }
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getRentableAreaConst() {
        Format_number.Format(rentableAreaConst);
        return rentableAreaConst;
    }

    public void setRentableAreaConst(double rentableAreaConst) {
        this.rentableAreaConst = rentableAreaConst;
    }

    public double getRentableAreaFloor() {
        return rentableAreaFloor;
    }

    public void setRentableAreaFloor(double rentableAreaFloor) {
        this.rentableAreaFloor = rentableAreaFloor;
    }

    public double getRentableArea() {
        return rentableArea;
    }

    public void setRentableArea(double rentableArea) {
        this.rentableArea = rentableArea;
    }

    public double getRentableAreaUnit() {
        return rentableAreaUnit;
    }

    public void setRentableAreaUnit(double rentableAreaUnit) {
        this.rentableAreaUnit = rentableAreaUnit;
    }

    public boolean checkMessages() {
        if ((this.messageLabel == null || this.messageLabel.isEmpty()) && (this.messageLabel2 == null || this.messageLabel2.isEmpty())) {
            return true;
        }
        return false;
    }

    // public Double getContributionRate(RentableUnitContribution
    // rentableUnitContribution) {
    // if(this.contributionModule.isApportionment() == false)
    // return rentableUnitContribution.getContributionRate();
    // else
    // return (rentableUnitContribution.getContributionRate() != null ?
    // rentableUnitContribution.getContributionRate()*100 : null);
    // }

    public void setContributionRate(RentableUnitContribution rentableUnitContribution) {
        if (this.contributionModule.isApportionment() != false && rentableUnitContribution.getContributionRate() != null)
            rentableUnitContribution.setContributionRate(rentableUnitContribution.getContributionRate() / 100);
    }

    public AvaluationType getAvaluationType() {
        return avaluationType;
    }

    public void setAvaluationType(AvaluationType avaluationType) {
        this.avaluationType = avaluationType;
    }

    @SuppressWarnings("unchecked")
    public List<AvaluationType> getAvaluationTypes() {
        List<AvaluationType> avaluationTypes = this.getEntityManager().createQuery("from AvaluationType order by name").getResultList();
        if (avaluationTypes.size() > 0 && avaluation != null && avaluation.getAvaluationType() == null)
            avaluation.setAvaluationType(avaluationTypes.get(0));
        return avaluationTypes;
    }

    public void setAvaluationTypes(List<AvaluationType> avaluationTypes) {
        this.avaluationTypes = avaluationTypes;
    }

    public Avaluation getAvaluation() {
        if (avaluation == null)
            avaluation = new Avaluation();
        return avaluation;
    }

    public void setAvaluation(Avaluation avaluation) {
        this.avaluation = avaluation;
    }

    public void calculateEndDate() {
        Date calculateEndDate = null;
        Calendar s = Calendar.getInstance();
        s.setTime(this.avaluation.getActivationDate());
        s.add(Calendar.YEAR, 1);
        calculateEndDate = s.getTime();
        this.avaluation.setEffectiveEndDate(calculateEndDate);

    }

    public void clearAvaluation() {
        this.avaluation = new Avaluation();
    }

    public void addAvalution() {
        this.avaluation.setRealProperty(this.getInstance());
        this.instance.getAvaluations().add(this.avaluation);
        this.clearAvaluation();
    }

    public boolean avaluationSaved() {
        if (avaluation != null && avaluation.getId() != 0) {
            return false;
        }
        return true;
    }

    public void refreshAvaluation() {
        getEntityManager().refresh(avaluation);
    }

    public void addAvaluationConstruction() {
        this.avaluation.setConstruction(this.construction);
        // this.avaluation.setAvaluationType(this.avaluationType);
        this.construction.getAvaluations().add(this.avaluation);
        this.clearAvaluation();

    }

    public void avuationTypeList() {
        for (AvaluationType avaluationType : getAvaluationTypes()) {
            if (avaluationType.getId() == 2) {

                this.getAvaluation().setAvaluationType(avaluationType);
                break;
            }
        }
    }

    public void editConstructionAction(Construction construction) {
        try{
	    	this.setConstruction(construction);
	        this.address = construction.getAddress();
	        this.stateAjax=this.construction.isDeactivate();
	        getAddressHome().setInstance(this.address);
	        getAddressHome().getSelectedCountry();
        }catch (org.hibernate.LazyInitializationException e){
			String sql = "from Address where id = ?";
			Query query=getEntityManager().createQuery(sql);
			query.setParameter(1, this.address.getId());
			getAddressHome().setInstance((Address)query.getSingleResult());
		}
    }

    public AddressHome getAddressHome() {

        if (addressHome == null)
            addressHome = new AddressHome();
        return addressHome;
    }

    public void setAddressHome(AddressHome addressHome) {
        this.addressHome = addressHome;
    }

    public void clearAddressHome() {
        this.addressHome = new AddressHome();
        this.address = new Address();
    }

    public void memberchipProject(int projectId) {
        // System.out.println("........................IDProyecto"+projectId);
        Query projects = this.getEntityManager().createQuery("from Project project WHERE project.id = ?");
        projects.setParameter(1, projectId);
        @SuppressWarnings("unchecked")
        List<Project> projectRealproperty = projects.getResultList();
        if (getInstance().getProjectRealProperty().isEmpty()) {
            getInstance().getProjectRealProperty().add(projectRealproperty.get(0));
        } else {
            for (Project project : getInstance().getProjectRealProperty()) {
                // System.out.println("****************************tamaño"
                // +getInstance().getProjectRealProperty().size());
                if (project.getId() != projectId) {

                    getInstance().getProjectRealProperty().add(projectRealproperty.get(0));
                    // System.out.println("--------------------------proyecto"+getInstance().getProjectRealProperty().get(0).getId());
                    break;
                }
            }
        }

    }

    public RealProperty getInstanceMaker(MakerChecker makerChecker) {
        Object aux = new MakerCheckerHome().getInstance(makerChecker);
        if (aux instanceof RealProperty) {
            RealProperty realProperty = (RealProperty) aux;
            realProperty = getEntityManager().merge(realProperty);
            System.out.println("nombre"+realProperty.getNameProperty());
            return realProperty;
        }
        return null;
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

    public void deleteProject(Project project) {
        Project p = null;
        for (Project projectAux : this.getInstance().getProjectRealProperty()) {
            if (project.getId() == projectAux.getId())
                p = project;
        }

        if (p != null)
            this.getInstance().getProjectRealProperty().remove(p);
    }

    public String checkAssetManager(RealProperty property) {
        this.setInstance(property);
        if (this.instance.getStep() == RealProperty.STEP_APPROVED || (new MakerCheckerHome().isObjectInMakerChecker(property)))
            return "and";
        else
            return "or";
    }

    public String checkManager(RealProperty property) {
        this.setInstance(property);
        if (this.instance.getStep() == RealProperty.STEP_APPROVED && !(new MakerCheckerHome().isObjectInMakerChecker(property))) {
            return "and";
        } else {
            return "or";
        }
    }

    public boolean isApproved() {
        if (approved == null) {
            if (getInstance().getStep() == RealProperty.STEP_APPROVED)
                approved = true;
            else
                approved = false;
        }

        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isDraft() {
        if (getInstance().getStep() == RealProperty.STEP_DRAFT) {
            return true;
        } else
            return false;
    }

    public void approvedRealProperty() {

        if (approved) {
            getInstance().setStep(RealProperty.STEP_APPROVED);
        } else {
            getInstance().setStep(ProjectProperty.STATUS_DRAFT);
        }
    }

    public boolean realPropertyListInApprove() {
        return new MakerCheckerHome().isObjectInMakerChecker(this.instance);
    }
    

    /**
     * metodo que actualiza el  a false el campo, que indica si existe algun cambio pendiente por aprobar en el activo o unidad arrendable
     */
    public void cancelPendingApproval(){
        String consulta = "UPDATE RentableUnit ru set pendingApprove = ? where ru.area.floor.construction.realProperty = ? )";
        Query q = getEntityManager().createQuery(consulta); 
        q.setParameter(1, false);
        q.setParameter(2, this.instance);
        q.executeUpdate();
        consulta = "UPDATE RealProperty rp set pendingApprove = ? where rp = ? )";
        q = getEntityManager().createQuery(consulta); 
        q.setParameter(1, false);
        q.setParameter(2, this.instance);
        q.executeUpdate();
    }
     
	//deactivate rentable unit

    public String getMotive() {
        return getInstance().getMotive();
    }

    public void setMotive(String motive) {
        getInstance().setMotive(motive);
    }

    public boolean isMessageRentableUnit() {
        return messageRentableUnit;
    }

    public void setMessageRentableUnit(boolean messageRentableUnit) {
        this.messageRentableUnit = messageRentableUnit;
    }

    public boolean isMotiveRentableUnit() {
        return motiveRentableUnit;
    }

	public Boolean getStateRentableUnit() {
		return this.getRentableUnit().isDeactivate();
	}

	public void setStateRentableUnit(Boolean state) {
		this.getRentableUnit().setDeactivate(state);
	}
	
	/**
	 * verifica si una unidad rentable se puede activar o desactivar
	 * 
	 */
	public void valideStateRentableUnit() {
		//cuenta cuantas unidades rentables estan asociadas a una hoja de terminos
		String sentence = "SELECT count(ru.*) from rentable_unit ru join project_property pp on pp.rentable_unit = ru.id where ru.id = ? and (pp.status = 1 or  pp.status = 2)";
		Query query = this.getEntityManager().createNativeQuery(sentence);
		query.setParameter(1, this.getRentableUnit().getId());
		Integer list = ((BigInteger) query.getResultList().get(0)).intValue();

		if ( this.getRentableUnit().getArea().isDeactivate()||list != 0) {
			
			if (list != 0) {
				//la unidad no se puede desactivar
				this.getRentableUnit().setDeactivate(false);
				log.log.error("la unidad tiene hojas de termino asignadas");
			} else {
				//el area esta desactivada
				this.getRentableUnit().setDeactivate(false);
				log.log.error("el area esta desactivada");
			}
			this.messageRentableUnit = true;
			this.motiveRentableUnit = false;
			
		} else {
			//la unidad rentable se puede desactivar
			System.out.println(" el estado es" + this.getRentableUnit().isDeactivate());
			log.log.info("la unidad se activo/desactivo");
			if (this.stateAjax!=this.getRentableUnit().isDeactivate()) {
				//esta condicion libera el area arrendable de la unnidad cuando se desactiva
				if(this.getRentableUnit().isDeactivate()){
					
					this.rentableUnit.setTotalRentableArea(0);
				}
				this.motiveRentableUnit = true;
				
			} else {
				this.motiveRentableUnit = false;
			}
			this.messageRentableUnit = false;
			updateContibutionModules(this.getRentableUnit());
			 String consulta = "UPDATE RentableUnit ru set pendingApprove = ? where ru = ? )";
		        Query q = getEntityManager().createQuery(consulta); 
		        q.setParameter(1, true);
		        q.setParameter(2, this.getRentableUnit());
		        q.executeUpdate();
		}
	}
	
	//deactivate area
	public boolean isMessageArea() {
		return messageArea;
	}

	public void setMessageArea(boolean messageArea) {
		this.messageArea = messageArea;
	}

	public void setMotiveArea(boolean motiveArea) {
		this.motiveArea = motiveArea;
	}

	public boolean isMotiveArea() {
		return motiveArea;
	}

	
	public Boolean getDeactivateArea() {
		return this.getArea().isDeactivate();
	}

	public void setDeactivateArea(Boolean deactivate) {
		this.getArea().setDeactivate(deactivate);
	}
	
	/**
	 *valida que un area se pueda desactivar o no 
	 */
	public void valideStateArea() {
		//cuenta cuantas unidades rentables pertencientes a una area determinada estan asociadas a una hoja de terminos
		String  sentence ="SELECT count(ru.*) from rentable_unit ru join project_property pp on pp.rentable_unit = ru.id join area a on ru.area = a.id where a.id = ? and (pp.status = 1 or  pp.status = 2)"; 
		Query query=this.getEntityManager().createNativeQuery(sentence);
		query.setParameter(1, this.getArea().getId());
		Integer list = ((BigInteger)query.getResultList().get(0)).intValue();
		if( this.getArea().getFloor().isDeactivate()||list != 0){
			if (list!=0) {
				//el area tiene un unidad arrendable asociada a  hoja de termino
				this.getArea().setDeactivate(false);
				log.log.error("La unidad arrendable tiene hoja de termino");
			} else {
				//el piso esta deactivado
				this.getArea().setDeactivate(true);
				log.log.error(" el piso esta desactivado");
			}
			this.setMessageArea(true);
			this.setMotiveArea(false);
			
		}else{
			//no tiene ninguna hoja de termino asociada
			if(!getArea().isDeactivate()){
				
				//activa la unidad rentable
				log.log.info("Activar unidades rentables del area "+ this.getArea().getId());
				this.changeStateArea(this.getArea(), false);  
			}else{
				
				// cambia el estado a desactivado
				log.log.info("Desactivar unidades rentables del area "+ this.getArea().getId());
				this.changeStateArea(this.getArea(), true); 
			}
			if (this.stateAjax != this.getArea().isDeactivate()) {
				
					this.setMotiveArea(true);
			} else {
				
					this.setMotiveArea(false);
			}
			this.setMessageArea(false);
		}
	}
	
	/**
	 * metodo que cambia de estado todas las unidades arrendables de un area
	 * @param area Area a la cual se le va a cambiar el estado
	 * @param change variable que indica si se activa o desactiva 
	 */
	public void changeStateArea(Area area, boolean change){
		List<RentableUnit> list = area.getRentableUnits();
		for (RentableUnit rentableUnit : list) {
			setPendingApproval(rentableUnit,true);
			rentableUnit.setDeactivate(change);
			updateContibutionModules(rentableUnit);
			if(area.isDeactivate()){
				rentableUnit.setTotalRentableArea(0);
			}
		}
	}
	
	//desactivar piso
	
	/**
	 * valida si un piso puede ser desactivado o no
	 */
	public void valideStateFloor() {
		String sentence = "SELECT count(ru.*) from rentable_unit ru join project_property pp on pp.rentable_unit = ru.id join area a on ru.area = a.id " +
				"join floor f on a.floor = f.id where f.id = ? and (pp.status = 1 or  pp.status = 2)";
		Query query =this.getEntityManager().createNativeQuery(sentence);
		query.setParameter(1, this.getFloor().getId());
		Integer res =((BigInteger)query.getResultList().get(0)).intValue();
		if ( this.getFloor().getConstruction().isDeactivate()||res != 0) {
			if (res!=0) {
				//el piso tiene un unidad arrendable asociada a  hoja de termino
				this.getFloor().setDeactivate(false);
				log.log.error("El piso tiene hojas de terminos");
			} else {
				//el edificio esta desactivado
				this.getFloor().setDeactivate(true);
				log.log.error("El edificio esta desactivado");
			}
			this.messageFloor = true;
			this.motiveFloor = false;
			
		} else {
			//no tiene ninguna hoja de termino asociada
			if (!this.getFloor().isDeactivate()) {
				//activa las areas
				log.log.info("Activar areas del piso "+this.getFloor().getId() );
				this.changeStateFloor(this.getFloor(), false);
				
			} else {
				//cambiar estado a desactivado
				log.log.info("Desactivar areas del piso "+this.getFloor().getId() );
				this.changeStateFloor(this.getFloor(), true);
			}
			if (this.stateAjax!=this.getFloor().isDeactivate()) {
				
				this.motiveFloor = true;
			} else {
				
				this.motiveFloor = false;
			}
			this.messageFloor = false;
		}
		
	}
	
	/**
	 * cambia el estado de todos las areas de un piso de actico a desactivo o viceversa
	 * @param floor
	 * @param change
	 */
	public void changeStateFloor(Floor floor, boolean change){
		List<Area> list = floor.getAreas();
		System.out.println("estado piso "+floor.getId());
		for (Area area : list) {
			area.setDeactivate(change);
			this.changeStateArea(area, change);
		}
	}

	public Boolean getMessageFloor(){
		return this.messageFloor;
	}
	
	public Boolean getMotiveFloor(){
		return this.motiveFloor;
	}
	
	public Boolean getDeactivateFloor() {
		return this.getFloor().isDeactivate();
	}

	public void setDeactivateFloor(Boolean deactivate) {
		this.getFloor().setDeactivate(deactivate);
	}
	
	//desactivar edificio
	
	/**
	 * valida si un edificio se puede desactivar o no
	 */
	public void valideStateConstruction() {
		String sentence = "SELECT count(ru.*) from rentable_unit ru join project_property pp on pp.rentable_unit = ru.id join area a on ru.area = a.id join floor f on a.floor = f.id " +
				"join construction c on f.construction = c.id where c.id = ? and (pp.status = 1 or  pp.status = 2)";
		Query query = this.getEntityManager().createNativeQuery(sentence);
		query.setParameter(1, this.getConstruction().getId());
		Integer res=((BigInteger)query.getResultList().get(0)).intValue();
		System.out.println("id "+this.getConstruction().getRealProperty().getId()+ "estado "+this.getConstruction().getRealProperty().isDeactivate() );
		if (this.getConstruction().getRealProperty().isDeactivate() || res != 0) {
			if (res != 0) {
				//tiene hoja de termino asociada, no se puede desactivar
				this.getConstruction().setDeactivate(false);
				log.log.error("el edificio tiene hojas asociadas");
			} else {
				//el activo esta desactivcado, no se puede activar
				this.getConstruction().setDeactivate(true);
				log.log.error("el activo esta desactivado");
			}
			this.messageConstruction = true;
			this.motiveConstruction = false;
			
		} else {
			//no tiene hoja
			if (!this.getConstruction().isDeactivate()) {
				//activas los pisos
				log.log.info("Activar piso del edificio "+getConstruction().getId());
				this.changeStateConstruction(this.getConstruction(), false);
				
			} else {
				//cambia a desactivar los pisos
				log.log.info("Desactivar piso del edificio "+getConstruction().getId());
				this.changeStateConstruction(this.getConstruction(), true);
			}
			if (this.stateAjax!=this.getConstruction().isDeactivate()) {
				
				this.motiveConstruction = true;
			} else {
				
				this.motiveConstruction = false;
			}
			this.messageConstruction = false;
		}
		
	}
	
	/**
	 * cambia el estado de todos los pisos de un edificio de actico a desactivo o viceversa
	 * @param construction
	 * @param change
	 */
	public void changeStateConstruction(Construction construction, boolean change){
		List<Floor> list = construction.getFloors();
		System.out.println("estado construccion "+construction.getId());
		for (Floor floor : list) {
			floor.setDeactivate(change);
			this.changeStateFloor(floor, change);
		}
	}
	
	public Boolean getMessageConstruction(){
		return this.messageConstruction;
	}
	
	public Boolean getMotiveConstruction(){
		return this.motiveConstruction;
	}
	
	public Boolean getDeactivateConstruction() {
		return this.getConstruction().isDeactivate();
	}

	public void setDeactivateConstruction(Boolean deactivate) {
		this.getConstruction().setDeactivate(deactivate);
	}

	//desactivar activo
	/**valida el estado de un activo para saber si se puede desactivar o no
	 * 
	 */
	public void valideStateRealProperty() {
		String sentence = "select count(rp.*) from real_property rp join project_property pp on pp.real_property = rp.id where rp.id = ? and (pp.status = 1 or  pp.status = 2)";
		Query query = this.getEntityManager().createNativeQuery(sentence);
		query.setParameter(1, this.getInstance().getId());
		Integer res = ((BigInteger)query.getResultList().get(0)).intValue();
		if (res != 0) {
			// el activo esta asociado a una hoja de terminos
			this.getInstance().setDeactivate(false);
			this.messageRealProperty = true;
			log.log.error("el activo esta asociado a una hoja de terminos");
			
		} else {
			sentence = "SELECT count(ru.*) from rentable_unit ru join project_property pp on pp.rentable_unit = ru.id join area a on ru.area = a.id " +
					"join floor f on a.floor = f.id join construction c on f.construction = c.id join real_property rp on c.real_property =rp.id " +
					"where rp.id = ? and (pp.status = 1 or  pp.status = 2)";
			query = this.getEntityManager().createNativeQuery(sentence);
			query.setParameter(1, this.getInstance().getId());
			res = ((BigInteger)query.getResultList().get(0)).intValue();
			 if(res != 0){
				 //el activo tiene unidades arrendables asignadas
				this.getInstance().setDeactivate(false);
				this.messageRealProperty = true;
				log.log.error("el activo tiene unidades arrendables asignadas");
				
			 }else{
				 //el activo se puede desactivar
				this.messageRealProperty = false;
				if (!this.getInstance().isDeactivate()) {
					 //activar edificios
					log.log.info("Activando edificios del activo "+this.getInstance().getId());
					this.changeStateRealProperty(this.getInstance(), false);
				
				 } else {
					//deactivar edificios
					 log.log.info("Desactivando edificios del activo "+this.getInstance().getId());
					 this.changeStateRealProperty(this.getInstance(), true);
					 String consulta = "UPDATE RealProperty rp set pendingApprove = ? where rp = ? )";
				       Query q = getEntityManager().createQuery(consulta); 
				        q.setParameter(1, true);
				        q.setParameter(2, this.instance);
				        q.executeUpdate();
				}
				 
			 }
		}
	}
	
	/**
	 * cambia el estado de todos los edificios del activo que le llega
	 * @param property activo 
	 * @param change valor a cambiar
	 */
	public void changeStateRealProperty(RealProperty property, boolean change){
		Set<Construction> list = property.getConstructions();
		for (Construction construction : list) {
			construction.setDeactivate(change);
			this.changeStateConstruction(construction, change);
		}
	}
	
	public Boolean getMessageRealProperty(){
		return this.messageRealProperty;
	}
	
//	public Boolean getMotiveRealProperty(){
//		return this.motiveRealProperty;
//	}
	
	public Boolean getDeactivateRealProperty() {
		return this.getInstance().isDeactivate();
	}

	public void setDeactivateRealProperty(Boolean deactivate) {
		this.getInstance().setDeactivate(deactivate);
	}
	
    public void setMotiveRentableUnit(boolean motiveRentableUnit) {
        this.motiveRentableUnit = motiveRentableUnit;
    }


    @SuppressWarnings("unchecked")
    public void updateContibutionModules(RentableUnit rentableUnit) {
        Query q = getEntityManager()
                .createQuery(
                        "FROM RentableUnitContribution  ruc WHERE ruc.contributionModule.apportionment = ? AND ruc.contributionModule.realProperty = ? AND  ruc.rentableUnit = ?");
        q.setParameter(1, true);
        q.setParameter(2, this.getInstance());
        q.setParameter(3, rentableUnit);
        List<RentableUnitContribution> contributions = q.getResultList();
        for (RentableUnitContribution rentableUnitContribution : contributions) {
            rentableUnitContribution.setContributionRatePorcentual(0.0);

        }
    }

    /**
     * metodo que actualiza el pending_approve a false el campo, que indica si
     * existe algun cambio pendiente por aprobar en el activo o unidad
     * arrendable
     * 
     * @param property
     */
    
    public void setPendingApproval(boolean pendingApproval) {

        String consulta = "UPDATE rentable_unit set pending_approve = ? where id in (select ru.id from  rentable_unit ru join area a on ru.area=a.id "
                + "join floor f on f.id = a.floor join construction c on c.id= f.construction join real_property rp on rp.id=c.real_property where rp.id = "
                + this.instance.getId() + ")";
        Query q = getEntityManager().createNativeQuery(consulta);
        q.setParameter(1, pendingApproval);
        q.executeUpdate();
    }

    /**
     * metodo que actualiza el pending_approve a false el campo, que indica si
     * existe algun cambio pendiente por aprobar en el activo o unidad
     * arrendable
     * 
     * @param property
     */
    public void setPendingApproval(RentableUnit rentableUnit,boolean pendingApproval) {

        String consulta = "UPDATE rentable_unit set pending_approve = ? where id = ?";
        Query q = getEntityManager().createNativeQuery(consulta);
        q.setParameter(1, pendingApproval);
        q.setParameter(2, rentableUnit.getId());
        q.executeUpdate();
    }

    /**
     * metodo que valida que la suma de todos los porcentajes de los modulos de
     * contribución de este activo, que pertenecan al tipo prorrateo, sea del
     * 100%
     * 
     * @return true si la suma ruc es 100%
     */
    public boolean checkPorcentFromRUC() {
        if(!this.getInstance().isDeactivate()){
        List<ContributionModule> modules = this.instance.getContributionModules();
        for (ContributionModule contributionModule : modules) {
            if (contributionModule.isApportionment()) {
                List<RentableUnitContribution> contributions = contributionModule.getRentableUnitContributions();
                Double x = 0.0;
                for (RentableUnitContribution rentableUnitContribution : contributions) {
                    x += rentableUnitContribution.getContributionRatePorcentual();
                }  
                if(x != 100)  {
                	return true;
                }
            } 
        }
        	return false;
        }
        return true;
    }
    
    /**
     * limpia las variables de motiveRealProperty y messageRealProperty
     * para que no aparescan en el formulario cuando se desea editar
     */
    public void clearVariableRealProperty(){
    	messageRealProperty = false;
    }

    
    public String getDate(){
    	return (new Date()).toString();
    }

    
    //metodos para ajax del la desactivacion
    
    /**
     * esta variable se creo con el fin de usar solo el get y set
     */
    
    
    public void setStringmotiveRU(String stringmotiveRU) {
		this.stringmotiveRU = stringmotiveRU;
		this.getRentableUnit().setMotive(this.stringmotiveRU);
	}


	public String getStringmotiveRU() {
		return "";
	}

	
	
	public void setStringMotiveArea(String stringMotiveArea) {
		this.stringMotiveArea = stringMotiveArea;
		this.getArea().setMotive(stringMotiveArea);
	}


	public String getStringMotiveArea() {
		return "";
	}

	
	
	public void setStringMotiveFloor(String stringMotiveFloor) {
		this.stringMotiveFloor = stringMotiveFloor;
		this.getFloor().setMotive(this.stringMotiveFloor);
	}


	public String getStringMotiveFloor() {
		return "";
	}

	

	public void setStringMotiveConstruction(String stringMotiveConstruction) {
		this.stringMotiveConstruction = stringMotiveConstruction;
		this.getConstruction().setMotive(this.stringMotiveConstruction);
	}


	public String getStringMotiveConstruction() {
		return "";
	}
	
	


	public String getStringMotiveRealProperty() {
		return "";
	}


	public void setStringMotiveRealProperty(String stringMotiveRealProperty) {
		this.stringMotiveRealProperty = stringMotiveRealProperty;
		this.getInstance().setMotive(this.stringMotiveRealProperty);
	}

}

		