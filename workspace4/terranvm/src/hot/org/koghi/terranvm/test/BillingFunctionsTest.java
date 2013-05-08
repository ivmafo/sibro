package org.koghi.terranvm.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.koghi.terranvm.bean.BillingFunctions;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Address;
import org.koghi.terranvm.entity.Area;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.City;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.Construction;
import org.koghi.terranvm.entity.ContributionModule;
import org.koghi.terranvm.entity.Country;
import org.koghi.terranvm.entity.Floor;
import org.koghi.terranvm.entity.Increased;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.IpcAccumulated;
import org.koghi.terranvm.entity.IpcMonthly;
import org.koghi.terranvm.entity.IpcYearly;
import org.koghi.terranvm.entity.PhoneNumber;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.ProjectProperty;
import org.koghi.terranvm.entity.RealProperty;
import org.koghi.terranvm.entity.Region;
import org.koghi.terranvm.entity.RentableUnit;
import org.koghi.terranvm.entity.RentableUnitContribution;
import org.koghi.terranvm.entity.RentableUnitType;
import org.koghi.terranvm.entity.Sales;
import org.koghi.terranvm.entity.SystemVariable;

public class BillingFunctionsTest {
    EntityManager entityManager = mock(EntityManager.class);
    BillingFunctions functions;
    Query query = mock(Query.class);
    Query query2 = mock(Query.class);
    Query q = mock(Query.class);
    Query query3 = mock(Query.class);
    Query query4 = mock(Query.class);
    Query ipcy = mock(Query.class);
    Query ipca = mock(Query.class);
    Query ipcm = mock(Query.class);
    private RentableUnit rentableUnit = rentableUnit();
    private RealProperty realProperty ;
    private Project project;

    @BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    Calendar c = Calendar.getInstance();
	    when(q.getResultList()).thenReturn(rentableUnitId());
	    when(entityManager.createQuery("SELECT r FROM RentableUnit r, ProjectProperty pp WHERE pp.id = ? and pp.rentableUnit = r.id")).thenReturn(q);
	    when(query.getResultList()).thenReturn(new ArrayList<Object>());
	    when(ipcy.getResultList()).thenReturn(ipcyList());
        when( entityManager.createQuery(" FROM IpcYearly order by year")).thenReturn(ipcy);
        when(ipcm.getResultList()).thenReturn(ipcmList());
        when( entityManager.createQuery(" FROM IpcMonthly  order by year,monthly ")).thenReturn(ipcm);
        when(ipca.getResultList()).thenReturn(ipcaList());
        when( entityManager.createQuery(" FROM IpcAccumulated  order by year,monthly ")).thenReturn(ipca);
	    when(query2.getResultList()).thenReturn(minimunWagesList()); 
	    when(entityManager.createNativeQuery("SELECT year, value FROM  minimum_wage order by year")).thenReturn(query2);
	    when(query3.getResultList()).thenReturn(sales());
	    String querySales  = "FROM Sales sales WHERE sales.salesPeriod.realProperty = " +  realProperty + " AND sales.salesPeriod.year = "
	            + c.get(Calendar.YEAR) + " AND sales.salesPeriod.month = " + c.get(Calendar.MONTH)  + " AND sales.rentableUnit = " + rentableUnit ;
	    when(entityManager.createQuery(querySales)).thenReturn(query3);
	    String sql = "SELECT iv.id, iv.calculated_cost FROM invoice_concept iv where iv.concept in (select id from concept where project_property = ?)" +
	    		" and iv.concept in (select id from concept where dependent = ?) and (? between ini_period_date and end_period_date)";
	    when(query3.getResultList()).thenReturn(sales());
	    when(entityManager.createNativeQuery(sql)).thenReturn(query4);
	    when(entityManager.find(Concept.class, 250387)).thenReturn(generateOldConcept());
	    
	}

	/** 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	@Test
    public void testRetroactivesSalarioMinimo() {
        functions = new BillingFunctions();
        InvoiceConcept invoiceConcept = generateInvoiceConcept();
        functions.setEntityManager(entityManager);
        Double actual = functions.calculateConceptRetroactive(generateOldConcept(), invoiceConcept, null);
        assertEquals(esperado(invoiceConcept , 100000 , 600000), actual);
        
        
    }
    
    @Test
    public void testRetroactivesSalarioMinimo2() {
        functions = new BillingFunctions();
        InvoiceConcept invoiceConcept = generateInvoiceConcept();
        Concept concept = generateOldConcept();
        concept.setIncreased(null);
        concept.setExpression("VFIJO + SM[YEAR]");
        functions.setEntityManager(entityManager);
        Double actual = functions.calculateConceptRetroactive(concept, invoiceConcept, null);
        assertEquals(esperado(invoiceConcept, 600000 , 600000), actual);
        
        
    }
    
    
    @Test
    public void testRetroactivesIPCY() {
        functions = new BillingFunctions();
        InvoiceConcept invoiceConcept = generateInvoiceConcept();
        Concept concept = generateOldConcept();
        Increased increased = concept.getIncreased();
        increased.setExpression("VFIJO * (1 + IPCY[YEAR])");
        concept.setIncreased(increased);
        functions.setEntityManager(entityManager);
        Double actual = functions.calculateConceptRetroactive(concept, invoiceConcept, null);
        assertEquals(esperado(invoiceConcept, 100000, 140000), actual);
        
        
    }
    
    @Test
    public void testRetroactivesIPCY2() {
        functions = new BillingFunctions();
        InvoiceConcept invoiceConcept = generateInvoiceConcept();
        Concept concept = generateOldConcept();
        concept.setIncreased(null);
        concept.setExpression("VFIJO * (1 + IPCY[YEAR])");
        functions.setEntityManager(entityManager);
        Double actual = functions.calculateConceptRetroactive(concept, invoiceConcept, null);
        System.out.println("ACTUAL : " + actual);
        assertEquals(new Double(140000), actual);
        
        
    }
    
    
    @Test
    public void testRetroactivesIPCA() {
        functions = new BillingFunctions();
        InvoiceConcept invoiceConcept = generateInvoiceConcept();
        Concept concept = generateOldConcept();
        concept.getIncreased().setExpression("VFIJO * (1 + IPCA[MONTH])");
        functions.setEntityManager(entityManager);
        Double actual = functions.calculateConceptRetroactive(concept, invoiceConcept, null);
        System.out.println("ACTUAL : " + actual);
        assertEquals(esperado(invoiceConcept, 100000, 150000), actual);
        
        
    }
    
    
    @Test
    public void testRetroactivesIPCM() {
        functions = new BillingFunctions();
        InvoiceConcept invoiceConcept = generateInvoiceConcept();
        Concept concept = generateOldConcept();
        Increased increased = concept.getIncreased();
        increased.setExpression("VFIJO * (1 + IPCM[MONTH])");
        concept.setIncreased(increased);
        functions.setEntityManager(entityManager);
        Double actual = functions.calculateConceptRetroactive(concept, invoiceConcept, null);
        System.out.println("ACTUAL : " + actual);
        assertEquals(esperado(invoiceConcept, 100000, 180000), actual);
        
        
    }
    
    
	/**
	 * se genera el incremento del concepto
	 * @return increased
	 */
	private Increased generateIncreased(){
	    Calendar c1 = Calendar.getInstance();
	    Increased increased = new Increased();
	    increased.setId(5133);
	    increased.setExpression("VFIJO + SM[YEAR]");
	    increased.setPeriodicity(1);
	    increased.setPeriodicityType(ProjectProperty.MONTHS_PERIOD_TYPE);
	    c1.set(Calendar.DAY_OF_MONTH, 27);
	    c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.MILLISECOND, 0);
	    increased.setNextIncreased(c1.getTime());
	    System.out.println("Fecha del incremento: " + c1.getTime());
	    return increased;
	}
	
	
	/**
	 * genera 2 tipos de entidades de negocio diferentes, una facturadora y otra facturada
	 * para evitar que se cancele la ejcución del retroactivo por que el facturador y facturado sean el mismo
	 * @param isBiller
	 * @return entidadDeNegocio
	 */
	public BusinessEntity generateBusinessEntity(boolean isBiller){
	    BusinessEntity entity = new BusinessEntity();
	    return entity;
	}
	
	/**
	 * metodo que genera el concepto a serializar
	 * @return
	 */
	private Concept generateOldConcept(){
	    Concept concept = new Concept();
	    concept.setId(250387);
	    concept.setProjectProperty(projectProperty());
	    concept.setName("prueba retroactivos");
	    concept.setPeriodicityType(2);
	    concept.setPeriodicity((long) 1);
	    concept.setDocumentType(Concept.DOCUMENT_TYPE_BILL);
	    Calendar iniPeriod = Calendar.getInstance();
	    iniPeriod.set(Calendar.DAY_OF_MONTH, 11);
	    iniPeriod.set(Calendar.HOUR_OF_DAY, 0);
	    iniPeriod.set(Calendar.MINUTE, 0);
	    iniPeriod.set(Calendar.MILLISECOND, 0);
	    concept.setStartDate(iniPeriod.getTime());
	    concept.setNumberPeriods(5);
	    concept.setEarlyPayment(true);
	    concept.setGroupNumber(1);
	    concept.setExpression("VFIJO");
	    concept.setFixedValue(100000.0);
	    concept.setInterestArrears(false);
	    concept.setImmediatePaymentState(0);
	    concept.setIncreased(generateIncreased());
	    return concept;
        
    }
	
//	/**
//	 * metodo que genera el concepto que se agregara al invoiceConcept
//	 * @return
//	 */
//	private Concept generateNewConcept(){
//	    Concept concept = new Concept();
//	    concept.setId(250387);
//        concept.setProjectProperty(projectProperty());
//        concept.setName("prueba retroactivos");
//        concept.setPeriodicityType(2);
//        concept.setPeriodicity((long) 1);
//        concept.setDocumentType(Concept.DOCUMENT_TYPE_BILL);
//        Calendar iniPeriod = Calendar.getInstance();
//        iniPeriod.set(Calendar.DAY_OF_MONTH, 11);
//        concept.setStartDate(iniPeriod.getTime());
//        concept.setNumberPeriods(5);
//        concept.setEarlyPayment(true);
//        concept.setGroupNumber(1);
//        concept.setExpression("VFIJO");
//        concept.setFixedValue(100000.0);
//        concept.setInterestArrears(false);
//        concept.setImmediatePaymentState(0);
//        concept.setIncreased(generateIncreased());
//        return concept;
//	}
	
	/**
	 * genera una nueva invoice, que sera agregada al invocon
	 * @return
	 */
	private Invoice generateInvoice(){
	    Invoice invoice = new Invoice();
	    invoice.setId(555555555);
	    invoice.setApproved(false);
	    
	    return invoice;
	}
	
	/**
	 * metodo que genera el invoice concept que se útilizara para la prueba de retroactivos
	 * @return
	 */
	private InvoiceConcept generateInvoiceConcept(){
	    Calendar c1 = Calendar.getInstance();
	    c1.set(Calendar.DAY_OF_MONTH, 11);
	    c1.set(Calendar.HOUR_OF_DAY, 0);
	    c1.set(Calendar.MINUTE, 0);
	    c1.set(Calendar.MILLISECOND, 0);
	    InvoiceConcept concept = new InvoiceConcept();
	    concept.setId(109049);
	    concept.setConcept(generateOldConcept());
	    concept.setLastLiquidationDate(c1.getTime());
	    concept.setCalculatedCost(100000);
	    concept.setCalculatedTax(0.0);
	    concept.setBalance(100000);
	    concept.setIniPeriodDate(c1.getTime());
	    c1.add(Calendar.MONTH, +1);
	    concept.setEndPeriodDate(c1.getTime());
	    concept.setInvoiceConceptType((short) 1l);
	    try {
            concept.setConceptSerializable(BillingTools.serializeConcept(generateOldConcept()));
        } catch (IOException e) {
            e.printStackTrace();
        }
	    concept.setInvoice(generateInvoice());
	    return concept;
	}
	
	
	  private List<Object[]> minimunWagesList(){
	        List<Object[]> answer = new ArrayList<Object[]>();
	        Object[] arreglo = new Object[2];
	        arreglo[0] = 2012;
	        arreglo[1] = 500000.0;
	        answer.add(arreglo);
	        return answer;
	    }
	  
	
	/**
	 * genera la hoja de términos que se agregara al concepto de la prueba.
	 * @return
	 */
	private ProjectProperty projectProperty(){
	    Calendar c1 = Calendar.getInstance();
	    c1.set(Calendar.DAY_OF_MONTH, 1);
	    c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.MILLISECOND, 0);
	      ProjectProperty property = new ProjectProperty();
	      property.setId(899);
	      property.setBiller(biller());
	      property.setBilled(billed());
	      property.setBillerAddress(billerAddress());
	      property.setBilledAddress(billedAddress());
	      property.setSubscriptionDate(c1.getTime());
	      property.setPhoneNumber(billedPhone());
	      property.setPhoneNumberByPhoneBiller(billerPhone());
	      property.setObligationsStartDate(c1.getTime());
	      property.setRentableUnit(rentableUnit);
	      property.setProject(project());
	      return property;
	  }
	
	
	private Country country(){
	    Country country = new Country();
	    country.setId(52);
	    country.setName("Colombia");
	    country.setAbbreviation("CO");
	    return country;
	}
	private Region region(){
	    Region region = new Region();
	    region.setId(663);
	    region.setName("Cundinamarca");
	    region.setAbbreviation("33");
	    region.setCountry(country());
	    return region;
	}
	
	/**
	 * la ciudad del facturador
	 * @return
	 */
	public City city(){
	    City city = new City();
	    city.setId(102);
	    city.setName("Bogotá");
	    city.setRegion(region());
	    return city;
	}
	
	/**
     * facturador de la hoja de términos de la prueba
     * @return
     */
    private BusinessEntity biller(){
        BusinessEntity entity = new BusinessEntity();
        entity.setId(100);
        entity.setIdType((short) 1);
        entity.setIdNumber("800256769");
        entity.setNameBusinessEntity("PATRIMONIOS AUTONOMOS FIDUCIARIA CORFICOLOMBIANA SA");
        entity.setEmail("wamaya@koghi.com");
        entity.setTradeName("PATRIMONIOS AUTONOMOS FIDUCIARIA CORFICOLOMBIANA SA");
        entity.setTaxLiabilities("13,14,11 ");
        entity.setLegalEntityType("1");
        entity.setVerificationNumber(6);
        entity.setCity(city());
        
        return entity;
	}
    
    /**
     * facturador de la hoja de términos de la prueba
     * @return
     */
    private BusinessEntity billed(){
        BusinessEntity entity = new BusinessEntity();
        entity.setId(254);
        entity.setIdType((short) 1);
        entity.setIdNumber("8002567693");
        entity.setNameBusinessEntity("aPATRIMONIOS AUTONOMOS FIDUCIARIA CORFICOLOMBIANA SA");
        entity.setEmail("wamaya@koghi.com");
        entity.setTradeName("PATRIMONIOSa AUTONOMOS FIDUCIARIA CORFICOLOMBIANA SA");
        entity.setTaxLiabilities("13,14,11 ");
        entity.setLegalEntityType("1");
        entity.setVerificationNumber(6);
        entity.setCity(city());
        
        return entity;
    }
    
    /**
     * dirección del facturador
     * @return
     */
    private Address billerAddress(){
        Address address = new Address();
        address.setId(100);
        address.setBusinessEntity(biller());
        address.setOther("CARRERA 13 NO.26-45");
        address.setCity(city());
        return address;
    }
    
    /**
     * dirección del facturado
     * @return
     */
    private Address billedAddress(){
        Address address = new Address();
        address.setId(254);
        address.setBusinessEntity(billed());
        address.setOther("CARRERA 13 NO.26-45");
        address.setCity(city());
        return address;
    }

    /**
     * telefono facturador
     * @return
     */
    private PhoneNumber billerPhone(){
        PhoneNumber number = new PhoneNumber();
        number.setId(100);
        number.setNumber("456789");
        number.setBusinessEntity(biller());
        return number;
    }
    

    /**
     * telefono facturador
     * @return
     */
    private PhoneNumber billedPhone(){
        PhoneNumber number = new PhoneNumber();
        number.setId(254);
        number.setNumber("456789");
        number.setBusinessEntity(biller());
        return number;
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public List<Integer> rentableUnitId(){
        List<Integer> list = new ArrayList<Integer>();
        list.add(134);
        return list;
    }
    
    public RentableUnit rentableUnit(){
        RentableUnit unit = new RentableUnit();
        unit.setId(144);
        unit.setArea(area());
        unit.setName("LC 213");
        unit.setRentableUnitType(new RentableUnitType());
        unit.setConstrucionEnrollment("50C-1535062-50C-1535063");
        unit.setTotalRentableArea(86);
        unit.setAddressByAddressOwner(billerAddress());
        unit.setPhoneNumberByPhonenumbeOwner(billedPhone());
        unit.setCostCenter(0);
        return unit;
    }
    public Area area(){
        Area area = new Area();
        area.setId(666);
        area.setFloor(floor());
        return area;
    }
    public Floor floor(){
        Floor floor = new Floor();
        floor.setId(444);
        floor.setConstruction(construction());
        return floor;
    }
    public Construction construction(){
        Construction construction = new Construction();
        construction.setId(1234);
        construction.setRealProperty(realProperty());
        return construction;
    }
    public RealProperty realProperty(){
        RealProperty property = new RealProperty();
        property.setId(7);
        this.realProperty = property;
        return property;
    }
    public List<Sales> sales(){
        List<Sales> list = new ArrayList<Sales>();
        Sales sales = new Sales();
        sales.setRentableUnit(rentableUnit);
        sales.setValue(1200);
        list.add(sales);
        return list;
    }
    public ContributionModule  contributionModule(){
        ContributionModule  contributionModule = new ContributionModule();
        contributionModule.setId(21);
        contributionModule.setName("sad");
        contributionModule.setRealProperty(realProperty);
        contributionModule.setRentableUnitContributions(rentableUnitContributions());
        return contributionModule;
    }
    public List<RentableUnitContribution> rentableUnitContributions(){
      
        List<RentableUnitContribution> list = new ArrayList<RentableUnitContribution>();
        list.add(rentableUnitContribution());
        return list;
    }
    
    public RentableUnitContribution rentableUnitContribution(){
        RentableUnitContribution rentableUnitContribution = new RentableUnitContribution();
        rentableUnitContribution.setId(32);
        rentableUnitContribution.setContributionRate(0.7);
        rentableUnitContribution.setRentableUnit(rentableUnit);
        return rentableUnitContribution;
    }
    
    private Project project(){
        Project project = new Project();
        this.project = project;
        project.setId(36);
        project.setSystemVariablesList(systemVariables());
        return project;
    }
    
    private List<SystemVariable> systemVariables(){
        List<SystemVariable> systemVariables = new ArrayList<SystemVariable>();
        SystemVariable systemVariable;
//        systemVariable = new  SystemVariable(1, "IPC ANUAL", "IPCY[YEAR]", true, null , null, project, null);
//        systemVariables.add(systemVariable);
//        systemVariable = new  SystemVariable(2, "IPC MENSUAL", "IPCM[MONTH]", true, null , null, project, null);
//        systemVariables.add(systemVariable);
//        systemVariable = new  SystemVariable(3, "IPC ACUMULADO", "IPCA[MONTH]", true, null , null, project, null);
//        systemVariables.add(systemVariable);
//        systemVariable = new  SystemVariable(4, "MODULO DE CONTRIBUCION", "MC", true, null , null, project, null);
//        systemVariables.add(systemVariable);
        return systemVariables;
    }
    
    private List<IpcYearly> ipcyList(){
        List<IpcYearly> ipcyList = new ArrayList<IpcYearly>();
        IpcYearly ipcYearly = new IpcYearly(1, null, 2012, (float) 0.4);
        ipcyList.add(ipcYearly);
        
        return ipcyList;
    }
    
    private List<IpcAccumulated> ipcaList(){
        List<IpcAccumulated> ipcaList = new ArrayList<IpcAccumulated>();
        Calendar calendar = Calendar.getInstance();
        IpcAccumulated ipc = new IpcAccumulated(1, null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) , (float) 0.5);
        
        ipcaList.add(ipc);
        return ipcaList;
    }
    private List<IpcMonthly> ipcmList(){
        List<IpcMonthly> ipcmList = new ArrayList<IpcMonthly>();
        Calendar calendar = Calendar.getInstance();
        IpcMonthly ipc = new IpcMonthly(1, null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) , (float) 0.8);
        ipcmList.add(ipc);
        return ipcmList;
    }
    public Double valorEsperado(Date fechaInicio, Date fechaIncremento, Date fechaFinal , double valorInicial , double ValorIncrementado){
        Double x = 0.0;
        int diff1 = fechaInicio.compareTo(fechaIncremento), diff2 = fechaIncremento.compareTo(fechaFinal);
        x = ((valorInicial * diff1) + (ValorIncrementado * diff2))/(diff1 + diff2);
        
        return x;
    }
    public Double esperado(InvoiceConcept invoiceConcept, double valorInicial , double ValorIncrementado){
        Calendar c1 = Calendar.getInstance() , c2 = Calendar.getInstance() , c3 = Calendar.getInstance();
        c1.setTime(invoiceConcept.getConcept().getStartDate());
        c2.setTime(invoiceConcept.getConcept().getIncreased().getNextIncreased());
        c3.setTime(invoiceConcept.getEndPeriodDate());
        Double esperado = valorEsperado(c1.getTime(), c2.getTime(), c3.getTime(), valorInicial, ValorIncrementado);
        return esperado;
    }
}   
