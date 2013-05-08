package org.koghi.terranvm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.IpcAccumulated;
import org.koghi.terranvm.entity.IpcMonthly;
import org.koghi.terranvm.entity.IpcYearly;

public class recalculateInterestTest {
	
	private EntityManager entityManager = mock(EntityManager.class);
	private BillingTools tools;
	
	private Query query = mock(Query.class);
	private Query query2 = mock(Query.class);
	Query ipcy = mock(Query.class);
    Query ipca = mock(Query.class);
    Query ipcm = mock(Query.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		when(query.getResultList()).thenReturn(new ArrayList<Object>());
		when(query2.getResultList()).thenReturn(usuryRates()); 
		when(ipcy.getResultList()).thenReturn(ipcyList());
		when( entityManager.createQuery(" FROM IpcYearly order by year")).thenReturn(ipcy);
		when(ipcm.getResultList()).thenReturn(ipcmList());
		when( entityManager.createQuery(" FROM IpcMonthly  order by year,monthly ")).thenReturn(ipcm);
		when(ipca.getResultList()).thenReturn(ipcaList());
		when( entityManager.createQuery(" FROM IpcAccumulated  order by year,monthly ")).thenReturn(ipca);
		when( entityManager.createNativeQuery("SELECT * from history_usury_rate order by date desc limit 2")).thenReturn(query2);
		when(query.getResultList()).thenReturn(minimunWagesList()); 
        when(entityManager.createNativeQuery("SELECT year, value FROM  minimum_wage order by year")).thenReturn(query);
		
		tools = new BillingTools(entityManager);
		
		
		
	}

	@After
	public void tearDown() throws Exception {
	}
	/**
	 * Calcula el valor generado de Intereses por 31 dias para un determinado balance, dentro del mismo mes 
	 */
	@Test
	public void testRecalculateInterest() {
		
		
		Calendar iniPeriod = Calendar.getInstance(), endPeriod = Calendar.getInstance();
		double balance = 100000.0;
		iniPeriod.set(Calendar.MONTH, Calendar.MAY);
		iniPeriod.set(Calendar.DAY_OF_MONTH, 1);
		endPeriod.set(Calendar.MONTH, Calendar.MAY);
		endPeriod.set(Calendar.DAY_OF_MONTH, 31);
		assertEquals(new Double(2260.071291133614), new Double(tools.recalculateInterest(iniPeriod.getTime(), endPeriod.getTime(), balance)));
	}
	/**
	 * Calcula el valor generado de Intereses por 31 dias para un determinado balance, en diferente mes, pero mismo trimestre.
	 */
	@Test
	public void testRecalculateInterest2() {
		
		
		Calendar iniPeriod = Calendar.getInstance(), endPeriod = Calendar.getInstance();
		double balance = 100000.0;
		iniPeriod.set(Calendar.MONTH, Calendar.APRIL);
		iniPeriod.set(Calendar.DAY_OF_MONTH, 15);
		endPeriod.set(Calendar.MONTH, Calendar.MAY);
		endPeriod.set(Calendar.DAY_OF_MONTH, 15);
		assertEquals(new Double(2260.071291133614), new Double(tools.recalculateInterest(iniPeriod.getTime(), endPeriod.getTime(), balance)));
	}
	
	/**
	 * Calcula el valor generado de Intereses por 31 dias para un determinado balance, en diferente mes, y diferente trimestre.
	 */
	@Test
	public void testRecalculateInterest3() {
		
		
		Calendar iniPeriod = Calendar.getInstance(), endPeriod = Calendar.getInstance();
		double balance = 100000.0;
		iniPeriod.set(Calendar.MONTH, Calendar.APRIL);
		iniPeriod.set(Calendar.DAY_OF_MONTH, 16);
		endPeriod.set(Calendar.MONTH, Calendar.JUNE);
		endPeriod.set(Calendar.DAY_OF_MONTH, 15);
		System.out.println(tools.recalculateInterest(iniPeriod.getTime(), endPeriod.getTime(), balance));
		assertFalse(2260.071291133614==tools.recalculateInterest(iniPeriod.getTime(), endPeriod.getTime(), balance));
	}
	
	/**
	 * Calcula el valor generado de Intereses por 31 dias para un determinado balance, en diferente mes, y diferente trimestre, para poder calcular esto,
	 * debera clacular 16 días con una tasa de usura del 0.50 y 15 días con una tasa de usura del 0.3
	 */
	@Test
	public void testRecalculateInterest4() {
		
		Calendar iniPeriod = Calendar.getInstance(), endPeriod = Calendar.getInstance();
		double balance = 100000.0;
		iniPeriod.set(Calendar.MONTH, Calendar.MARCH);
		iniPeriod.set(Calendar.DAY_OF_MONTH, 16);
		endPeriod.set(Calendar.MONTH, Calendar.APRIL);
		endPeriod.set(Calendar.DAY_OF_MONTH, 15);
		System.out.println(tools.recalculateInterest(iniPeriod.getTime(), endPeriod.getTime(), balance));
		assertEquals(new Double(2896.665237934415), new Double(tools.recalculateInterest(iniPeriod.getTime(), endPeriod.getTime(), balance)));
	}
	
	
	/**
	 * metodo que genera una lista de tasas de usura de los trimestres (ENE-MAR) y (APR-JUN), que podran ser usadas para calcular los intereses en el period
	 * @return
	 */
	private List<Object[]> usuryRates(){
        Calendar c = Calendar.getInstance(), c1= Calendar.getInstance();
        c.set(Calendar.MONTH, Calendar.APRIL);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c1.set(Calendar.MONTH, Calendar.JANUARY);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        double r1 = 0.3,r2 = 0.5;
        List<Object[]> answer = new ArrayList<Object[]>();
            ArrayList<Object> rate = new ArrayList<Object>(), rate2 = new ArrayList<Object>();
            rate.add("");
            rate.add(r2);
                rate.add(c1.getTime());
            answer.add(rate.toArray());
            rate2.add("");
            rate2.add(r1);
            rate2.add(c.getTime());
            answer.add(rate2.toArray());
        return answer;
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
		invoiceConcept.setBalance(100000);
		invoiceConcept.setCalculatedCost(invoiceConcept.getBalance());
		invoiceConcept.setCalculatedTax(invoiceConcept.getBalance() * 0.16);
		invoiceConcept.setInvoice(new Invoice());
		if (isInterest){
		invoiceConcept.setInvoiceConceptParent(getNewInvoiceConcept(false,concept));}
		
		return invoiceConcept;
	}

	 private List<Object[]> minimunWagesList(){
         List<Object[]> answer = new ArrayList<Object[]>();
         Object[] arreglo = new Object[2];
         arreglo[0] = new Integer(2012);
         arreglo[1] = 500000.0;
         answer.add(arreglo);
         return answer;
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
}
