package org.koghi.terranvm.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.session.ProjectHome;

public class GroupInvoiceConceptOnPDFTest {

	private Random random = new Random();
	private Concept concept;
	private Invoice invoice;
	private List<InvoiceConcept> invConcepts = new ArrayList<InvoiceConcept>();
	private HashMap<Integer, ResultadoEsperado> resultados = new HashMap<Integer, GroupInvoiceConceptOnPDFTest.ResultadoEsperado>();
	private EntityManager entityManager = mock(EntityManager.class);
    private Query query = mock(Query.class);
    ProjectHome home = mock(ProjectHome.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {   
	    
        when( home.getEntityManager()).thenReturn(entityManager);
        when( entityManager.createQuery(" FROM IpcYearly order by year")).thenReturn(query);
        when( entityManager.createQuery(" FROM IpcMonthly  order by year,monthly ")).thenReturn(query);
        when( entityManager.createQuery(" FROM IpcAccumulated  order by year,monthly ")).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<Object>());

	}



	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGroupInvoiceConceptInterest() {   
		
		home.setEntityManager(getEntityManager());
		Concept concept = getNewConcept(false);
		generateInvoiceConcept(1, 3, concept);
		generateInvoiceConcept(2, 4, getNewConcept(true));
		generateInvoiceConcept(3, 5, concept);
		List<InvoiceConcept> invoiceConcepts = home.groupInvoiceConceptInterest(invConcepts);
		for (InvoiceConcept invoiceConcept : invoiceConcepts) {
			ResultadoEsperado esperado;
			if(invoiceConcept.getConcept().getBulkLoad() && invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST){
				esperado = resultados.get(2);
				evaluar(esperado,invoiceConcept);
			}else if(!invoiceConcept.getConcept().getBulkLoad() && invoiceConcept.getInvoiceConceptType() == InvoiceConcept.TYPE_INTEREST){
				esperado = resultados.get(3);
				evaluar(esperado,invoiceConcept);
			}
		}
		
	}
	/**
	 * evalua si es correcto
	 */
	public void evaluar(ResultadoEsperado esperado,InvoiceConcept invoiceConcept){
		assertEquals(new Double(esperado.balance), new Double(invoiceConcept.getBalance()));
		assertEquals(new Double(esperado.calculatedCost), new Double(invoiceConcept.getCalculatedCost()));
		assertEquals(new Double(esperado.claculatedTax), new Double(invoiceConcept.getCalculatedTax()));
		assertEquals(esperado.calendarIniList, invoiceConcept.getIniPeriodDate());
		assertEquals(esperado.calendarEndList, invoiceConcept.getEndPeriodDate());
		if(invoiceConcept.getConcept().getBulkLoad())
		assertEquals(esperado.name + "*", invoiceConcept.getConcept().getName());
		else
			assertEquals(esperado.name, invoiceConcept.getConcept().getName());
	}
	/**
	 * metodo que genera InvoiceConcept del tipo especificado y los agrega a la lista, los tipos son de tipo normal = 1, intereses con concepto es de tipo carga masiva =2 
	 *  intereses con concepto es de tipo tipo normal = 3, si el concepto es de tipo 2 el concepto que es enviado como parametro debe ser de tipo bulkLoad = true.
	 *  si es de tipo 2  el concepto que es enviado como parametro debe ser de tipo bulkLoad = false y si es de tipo 1 no importa el tipo.
	 * @param invotype
	 */
	public void generateInvoiceConcept(Integer invotype, int howManyInvCon,Concept concept){
		if(!resultados.containsKey(invotype))
			resultados.put(invotype, new ResultadoEsperado());
		ResultadoEsperado esperado = resultados.get(invotype);
		for (int i = 0; i < howManyInvCon; i++) {
			InvoiceConcept invoiceConcept;
			System.out.println("invotype: " + invotype);
			if(invotype == 2 || invotype == 3){
			invoiceConcept = getNewInvoiceConcept(true, concept);}
			else{
				invoiceConcept  = getNewInvoiceConcept(false, concept);
			}
			esperado.name = invoiceConcept.getConcept().getName();
			esperado.balance += invoiceConcept.getBalance();
			esperado.calculatedCost += invoiceConcept.getCalculatedCost();
			esperado.claculatedTax += invoiceConcept.getCalculatedTax();
			if(esperado.calendarIniList == null)
				esperado.calendarIniList = invoiceConcept.getIniPeriodDate();
				else if(esperado.calendarIniList.after(invoiceConcept.getIniPeriodDate())){
					esperado.calendarIniList = invoiceConcept.getIniPeriodDate();
				}
				if(esperado.calendarEndList == null)
					esperado.calendarEndList = invoiceConcept.getEndPeriodDate();
				else if(invoiceConcept.getEndPeriodDate().after(esperado.calendarEndList)){
					esperado.calendarEndList = invoiceConcept.getEndPeriodDate();
				}
			invConcepts.add(invoiceConcept);
		}
	}

	/**
	 * crea un nuevo concepto, y parametriza si este es o no un concepto de tipo
	 * carga masiva
	 * 
	 * @param isBulkLoad
	 * @return nuevo concepto
	 */
	public Concept getNewConcept(boolean isBulkLoad) {
		concept = new Concept();
		concept.setId(random.nextInt());
		concept.setName("ConceptoPrueba");
		concept.setBulkLoad(isBulkLoad);
		concept.setDiscountDays(0);
		return concept;
	}

	/**
	 * crea un invoiceConcept asignandole un concepto y parametrizando si es o
	 * no de tipo Interes de mora, tambien le asigna los valores que serana
	 * necesarios para que este invoiceConcept pueda ser utilizado por la
	 * funcion de agrupación, estos valores seran generados de manera aleatoria.
	 * 
	 * @param isInterest
	 * @param concept
	 * @return nuevo InvoiceConcept
	 */
	public InvoiceConcept getNewInvoiceConcept(boolean isInterest,
			Concept concept) {
		InvoiceConcept invoiceConcept = new InvoiceConcept();
		if(isInterest){
			invoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_INTEREST);
		}
		else{
			invoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_NORMAL);
			}
		invoiceConcept.setConcept(concept);
		invoiceConcept.setAppliedRate(0.46);
		invoiceConcept.setBalance(random.nextDouble() * 1000);
		invoiceConcept.setCalculatedCost(invoiceConcept.getBalance());
		invoiceConcept.setCalculatedTax(invoiceConcept.getBalance() * 0.16);
		if (invoice == null)
			invoiceConcept.setInvoice(getNewInvoice());
		if (isInterest){
		invoiceConcept.setInvoiceConceptParent(getNewInvoiceConcept(false,concept));}
		invoiceConcept.setIniPeriodDate(getNewDate());
		invoiceConcept.setLastLiquidationDate(getNewDate());
		
		return invoiceConcept;
	}

	/**
	 * crea un nuevo invoice
	 * 
	 * @return nuevo invoice
	 */
	public Invoice getNewInvoice() {
		invoice = new Invoice();
		invoice.setId(random.nextInt());
		return invoice;
	}

	/**
	 * metodo que genera una fecha aleatoria.
	 * @return
	 */
	public Date getNewDate() {
		Calendar calendar = Calendar.getInstance();
		int desition = random.nextInt(11);
		switch (desition) {
		case 0:
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(31));
			break;
		case 1:
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(28));
			break;
		case 2:
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(31));
			break;
		case 3:
			calendar.set(Calendar.MONTH, Calendar.APRIL);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(30));
			break;
		case 4:
			calendar.set(Calendar.MONTH, Calendar.MAY);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(31));
			break;
		case 5:
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(30));
			break;
		case 6:
			calendar.set(Calendar.MONTH, Calendar.JULY);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(31));
			break;
		case 7:
			calendar.set(Calendar.MONTH, Calendar.AUGUST);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(31));
			break;
		case 8:
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(30));
			break;
		case 9:
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(31));
			break;
		case 10:
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(30));
			break;
		case 11:
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			calendar.set(Calendar.DAY_OF_MONTH,random.nextInt(31));
			break;

		default:
			System.out.println(desition + " no esta en el rango espesificado");
			break;
		}
		return calendar.getTime();
	}
	
	
	

	
	
	public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }






    /**
	 * esta clase contendra los resultados que esperamos de cada uno de los invoiceConcept que retorne
	 * la agrupación, para esto se creara una instancia de esta clase por cada uno de los tipos de interes (carga masiva y normal)
	 * @author wfamaya
	 *
	 */
	private class ResultadoEsperado{
		private Date calendarIniList;
		private Date calendarEndList;
		private String name = "";
		private double balance = 0.0;
		private double calculatedCost = 0.0;
		private double claculatedTax = 0.0;
	}
	
	
	
	

}
