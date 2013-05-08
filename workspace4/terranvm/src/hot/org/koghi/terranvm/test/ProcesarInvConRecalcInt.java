package org.koghi.terranvm.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Concept;
import org.koghi.terranvm.entity.ConceptRetentionRateAccount;
import org.koghi.terranvm.entity.Invoice;
import org.koghi.terranvm.entity.InvoiceConcept;
import org.koghi.terranvm.entity.RetentionRate;
import org.koghi.terranvm.entity.RetentionRateAccount;
import org.mockito.Mock;

public class ProcesarInvConRecalcInt {

    private EntityManager entityManager = mock(EntityManager.class);
    private Query query3 = mock(Query.class);
    private InvoiceConcept invoiceConcept = getNewInvoiceConcept(false, getNewConcept(false, true));
    private Query query = mock(Query.class);
    private Query query2 = mock(Query.class);
    private Query query4 = mock(Query.class);
    private Query query5 = mock(Query.class);
    private Concept concept = mock(Concept.class);
    @SuppressWarnings("unused")
    private Date date = new Date();
    @Mock
    private ArrayList<InvoiceConcept> listmockList = new ArrayList<InvoiceConcept>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {
        listmockList.add(invoiceConcept);

        
        restartMocks();
        restartWhen();
        // when(listmockList.get(anyInt())).thenReturn(invoiceConcept);

    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @After
    public void tearDown() throws Exception {}

    /**
     * esta prueba consiste en que el valor de los intereses baje a 0, y se
     * vuelvan a calcular los intereses asignados originalmente(recaudo-nota
     * crédito de 0).
     */
    @Test
    public void testProcesarRecalculacionIntereses() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Prueba 1");
        when(query4.getResultList()).thenReturn(invoiceConceptInterest(1));
        when(
                getEntityManager().createNativeQuery(
                        "SELECT ic.* from invoice_concept ic JOIN invoice i ON i.id = ic.invoice where ic.invoice_concept_parent = " + anyInt()
                                + " and i.approved = false and last_liquidation_date >= " + anyObject() + " ORDER BY last_liquidation_date ASC",
                        InvoiceConcept.class)).thenReturn(query4);
        BillingTools tools = new BillingTools(getEntityManager());
        Calendar ini = Calendar.getInstance(), end = Calendar.getInstance(), recoverDate = Calendar.getInstance();
        recoverDate.set(Calendar.MONTH, Calendar.FEBRUARY);
        recoverDate.set(Calendar.DAY_OF_MONTH, 15);
        InvoiceConcept invcon = getNewInvoiceConcept(false, concept);
        setDateValues(invcon, ini, end, Calendar.FEBRUARY, 01, Calendar.FEBRUARY, 28);
        List<InvoiceConcept> invoiceConcepts;
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, false, recoverDate.getTime());
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, true, recoverDate.getTime());
        assertEquals(new Double(1690389.7079323384), new Double(invoiceConcepts.get(0).getBalance()));

    }

    /**
     * esta prueba consiste en que el valor de los intereses baje a 0, y se
     * vuelvan a calcular los intereses de la mitad del balance.
     */
    @Test
    public void testProcesarRecalculacionIntereses2() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Prueba 2");
        when(query4.getResultList()).thenReturn(invoiceConceptInterest(1));
        when(
                getEntityManager().createNativeQuery(
                        "SELECT ic.* from invoice_concept ic JOIN invoice i ON i.id = ic.invoice where ic.invoice_concept_parent = " + anyInt()
                                + " and i.approved = false and last_liquidation_date >= " + anyObject() + " ORDER BY last_liquidation_date ASC",
                        InvoiceConcept.class)).thenReturn(query4);

        BillingTools tools = new BillingTools(getEntityManager());
        Calendar ini = Calendar.getInstance(), end = Calendar.getInstance(), recoverDate = Calendar.getInstance();
        recoverDate.set(Calendar.MONTH, Calendar.FEBRUARY);
        recoverDate.set(Calendar.DAY_OF_MONTH, 15);
        InvoiceConcept invcon = getNewInvoiceConcept(false, concept);
        setDateValues(invcon, ini, end, Calendar.FEBRUARY, 01, Calendar.FEBRUARY, 28);
        List<InvoiceConcept> invoiceConcepts;
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, false, recoverDate.getTime());
        invcon.setBalance(invcon.getBalance() / 2);
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, true, recoverDate.getTime());
        assertEquals(new Double(1690389.7079323384 / 2), new Double(invoiceConcepts.get(0).getBalance()));
    }

    /**
     * esta prueba consiste en que el valor de los intereses baje al valor que
     * se debe calcular para 5 días(563463,235977407), y se le sumen los
     * intereses de la mitad del balance por el resto de días(563463,235977407).
     */
    @Test
    public void testProcesarRecalculacionIntereses3() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Prueba 3");
        when(query4.getResultList()).thenReturn(invoiceConceptInterest(1));
        when(
                getEntityManager().createNativeQuery(
                        "SELECT ic.* from invoice_concept ic JOIN invoice i ON i.id = ic.invoice where ic.invoice_concept_parent = " + anyInt()
                                + " and i.approved = false and last_liquidation_date >= " + anyObject() + " ORDER BY last_liquidation_date ASC",
                        InvoiceConcept.class)).thenReturn(query4);
        BillingTools tools = new BillingTools(getEntityManager());
        Calendar recoverDate = Calendar.getInstance();
        recoverDate.set(Calendar.MONTH, Calendar.FEBRUARY);
        recoverDate.set(Calendar.DAY_OF_MONTH, 20);
        InvoiceConcept invcon = getNewInvoiceConcept(false, concept);
        // setDateValues(invcon, ini, end, Calendar.FEBRUARY, 01,
        // Calendar.FEBRUARY, 28);
        List<InvoiceConcept> invoiceConcepts;
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, false, recoverDate.getTime());
        System.out.println(invcon.getBalance());
        invcon.setBalance(invcon.getBalance() / 2);
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, true, recoverDate.getTime());
        System.out.println(invoiceConcepts.get(0).getBalance());
        assertEquals(new Double(1070580.1483571478), new Double(invoiceConcepts.get(0).getBalance()));

    }

    /**
     * esta prueba consiste en que el valor de los intereses baje a 0, y se
     * vuelvan a calcular los intereses asignados originalmente(recaudo-nota
     * crédito de 0), para esto se usan 2 tasas de usura diferentes, una de 0,5
     * y otra de 0,3.
     */
    @Test
    public void testProcesarRecalculacionIntereses4() {

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Prueba 4");
        restartMocks();
        restartWhen2();
        BillingTools tools = new BillingTools(getEntityManager());
        Calendar recoverDate = Calendar.getInstance();
        recoverDate.set(Calendar.MONTH, Calendar.MARCH);
        recoverDate.set(Calendar.DAY_OF_MONTH, 15);
        date = recoverDate.getTime();

        InvoiceConcept invcon = getNewInvoiceConcept(false, concept);
        List<InvoiceConcept> invoiceConcepts;
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, false, recoverDate.getTime());
        System.out.println(invcon.getBalance());
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, true, recoverDate.getTime());
        System.out.println(invoiceConcepts.get(0).getBalance());
        assertEquals(new Double(2936452.3596094646), new Double(invoiceConcepts.get(0).getBalance()));

    }

    /**
     * esta prueba consiste en que el valor de los intereses baje a los
     * generados en los primeros 5 días, y se vuelvan a calcular los intereses
     * para el resto de días, para esto se usan 2 tasas de usura diferentes, una
     * de 0,5 y otra de 0,3.
     */
    @Test
    public void testProcesarRecalculacionIntereses5() {
        
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Prueba 5");
        restartMocks();
        restartWhen2();
        BillingTools tools = new BillingTools(getEntityManager());
        Calendar recoverDate = Calendar.getInstance();
        recoverDate.set(Calendar.MONTH, Calendar.MARCH);
        recoverDate.set(Calendar.DAY_OF_MONTH, 20);
        date = recoverDate.getTime();

        InvoiceConcept invcon = getNewInvoiceConcept(false, concept);
        List<InvoiceConcept> invoiceConcepts;
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, false, recoverDate.getTime());
        invcon.setBalance(invcon.getBalance() / 2);
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, true, recoverDate.getTime());
        System.out.println(invoiceConcepts.get(0).getBalance());
        assertEquals(new Double(1749957.7977934552), new Double(invoiceConcepts.get(0).getBalance()));

    }

    /**
     * esta prueba consiste en que el valor de los intereses baje a 0, y se
     * vuelvan a calcular los intereses asignados originalmente(recaudo-nota
     * crédito de 0), para esto se usan 2 tasas de usura diferentes, una de 0,5
     * y otra de 0,3.
     */
    @Test
    public void testProcesarRecalculacionIntereses6() {

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Prueba 6");
        restartMocks();
        restartWhen3();
        RetentionRate iva = new RetentionRate(RetentionRate.RETENTION_RATE_IVA);
        RetentionRateAccount account = new RetentionRateAccount();
        account.setRetentionRate(iva);
        account.setValue(16.0);
        when(concept.getTax()).thenReturn(account);
        BillingTools tools = new BillingTools(getEntityManager());
        Calendar recoverDate = Calendar.getInstance();
        recoverDate.set(Calendar.MONTH, Calendar.MARCH);
        recoverDate.set(Calendar.DAY_OF_MONTH, 15);
        date = recoverDate.getTime();

        InvoiceConcept invcon = getNewInvoiceConcept(false, concept);
        List<InvoiceConcept> invoiceConcepts;
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, false, recoverDate.getTime());
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, true, recoverDate.getTime());
        assertEquals(new Double(2752667.3834131416), new Double(invoiceConcepts.get(0).getBalance()));

    }

    /**
     * esta prueba consiste en que el valor de los intereses baje a los
     * generados en los primeros 5 días, y se vuelvan a calcular los intereses
     * para el resto de días, para esto se usan 2 tasas de usura diferentes, una
     * de 0,5 y otra de 0,3.
     */
    @Test
    public void testProcesarRecalculacionIntereses7() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Prueba 7");
        restartMocks();
        restartWhen4();
        RetentionRate iva = new RetentionRate(RetentionRate.RETENTION_RATE_IVA);
        RetentionRateAccount account = new RetentionRateAccount();
        account.setRetentionRate(iva);
        account.setValue(16.0);
        when(concept.getTax()).thenReturn(account);
        BillingTools tools = new BillingTools(getEntityManager());
        Calendar recoverDate = Calendar.getInstance();
        recoverDate.set(Calendar.MONTH, Calendar.MARCH);
        recoverDate.set(Calendar.DAY_OF_MONTH, 20);
        date = recoverDate.getTime();
        InvoiceConcept invcon = getNewInvoiceConcept(false, concept);
        List<InvoiceConcept> invoiceConcepts;
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, false, recoverDate.getTime());
        invcon.setBalance(invcon.getBalance() / 2);
        invoiceConcepts = tools.procesarRecalculacionIntereses(invcon, true, recoverDate.getTime());
        System.out.println("FUUUU: " + invoiceConcepts.get(0).getBalance());
        assertEquals(new Double(1376333.6917065708), new Double(invoiceConcepts.get(0).getBalance()));

    }

    /**
     * genera una lista de invoiceConcept de tipo Interes, que seran los que se
     * vean afectados por el recalculo de Intereses.
     * 
     * @return lista Intereses
     */
    private List<InvoiceConcept> invoiceConceptInterest(int cantidadIntereses) {
        List<InvoiceConcept> concepts = new ArrayList<InvoiceConcept>();
        Calendar ini = Calendar.getInstance(), end = Calendar.getInstance();
        for (int i = 0; i < cantidadIntereses; i++) {
            InvoiceConcept invoiceConcept = getNewInvoiceConcept(true, this.invoiceConcept.getConcept());
            if (i == 0) {
                invoiceConcept.setBalance(1690389.7079323384);
                // invoiceConcept.setCalculatedTax(27679.80);
                invoiceConcept.setCalculatedCost(1352311.7663458707);
                ini.set(Calendar.MONTH, Calendar.FEBRUARY);
                ini.set(Calendar.DAY_OF_MONTH, 15);
                end.set(Calendar.MONTH, Calendar.MARCH);
                end.set(Calendar.DAY_OF_MONTH, 1);

            } else {
                invoiceConcept.setBalance(28000.0);
                ini.set(Calendar.MONTH, Calendar.MARCH);
                ini.set(Calendar.DAY_OF_MONTH, 15);
                end.set(Calendar.MONTH, Calendar.APRIL);
                end.set(Calendar.DAY_OF_MONTH, 1);
            }
            invoiceConcept.setIniPeriodDate(ini.getTime());
            invoiceConcept.setEndPeriodDate(end.getTime());
            invoiceConcept.setLastLiquidationDate(end.getTime());
            invoiceConcept.setCalculatedCost(invoiceConcept.getBalance());
            // invoiceConcept.setCalculatedTax(invoiceConcept.getBalance()*0.16);
            concepts.add(invoiceConcept);
        }
        return concepts;
    }

    /**
     * genera una lista de invoiceConcept de tipo Interes, que seran los que se
     * vean afectados por el recalculo de Intereses, estos invoiceConcept entran
     * dentro de 2 trimestres diferentes (usan 2 tasa de usura diferentes), el
     * primer periodo(Ene-Mar) tendra un valor de 0,5 y el segundo
     * periodo(Apr-May) tendra un valor de 0,3.
     * 
     * @return lista Intereses
     */
    private List<InvoiceConcept> invoiceConceptInterest1(int cantidadIntereses) {
        List<InvoiceConcept> concepts = invoiceConceptInterest(cantidadIntereses);
        Calendar ini = Calendar.getInstance(), end = Calendar.getInstance();
        for (InvoiceConcept invoiceConcept : concepts) {
            invoiceConcept.setBalance(2936452.3596094646);
            // invoiceConcept.setCalculatedTax(27679.80);
            invoiceConcept.setCalculatedCost(2936452.3596094646);
            ini.set(Calendar.MONTH, Calendar.MARCH);
            ini.set(Calendar.DAY_OF_MONTH, 15);
            end.set(Calendar.MONTH, Calendar.APRIL);
            end.set(Calendar.DAY_OF_MONTH, 15);
            invoiceConcept.setIniPeriodDate(ini.getTime());
            invoiceConcept.setEndPeriodDate(end.getTime());
            invoiceConcept.setLastLiquidationDate(end.getTime());
            invoiceConcept.setCalculatedCost(invoiceConcept.getBalance());
        }
        return concepts;
    }

    private List<InvoiceConcept> invoiceConceptInterestIva(int cantidadIntereses) {
        List<InvoiceConcept> concepts = invoiceConceptInterest(cantidadIntereses);
        asignarIva(concepts);
        return concepts;
    }

    private List<InvoiceConcept> invoiceConceptInterestIva1(int cantidadIntereses) {
        List<InvoiceConcept> concepts = invoiceConceptInterest1(cantidadIntereses);
        asignarIva(concepts);
        return concepts;
    }

    public void asignarIva(List<InvoiceConcept> concepts) {
        for (InvoiceConcept invoiceConcept : concepts) {
            invoiceConcept.setBalance(2752667.3834131416);
            invoiceConcept.setCalculatedCost(2372989.123632019);
            invoiceConcept.setCalculatedTax(379678.259781123);
        }
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
    public InvoiceConcept getNewInvoiceConcept(boolean isInterest, Concept concept) {
        InvoiceConcept invoiceConcept = new InvoiceConcept();
        if (isInterest) {
            invoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_INTEREST);
        } else {
            invoiceConcept.setInvoiceConceptType(InvoiceConcept.TYPE_NORMAL);
        }
        invoiceConcept.setConcept(concept);
        invoiceConcept.setAppliedRate(0.46);
        invoiceConcept.setBalance(100000000);
        invoiceConcept.setEndPeriodDate(new Date());
        invoiceConcept.setIniPeriodDate(new Date());
        invoiceConcept.setCalculatedCost(invoiceConcept.getBalance());
        invoiceConcept.setInvoice(new Invoice());
        invoiceConcept.setId(1);
        invoiceConcept.setEndPeriodDate(new Date());
        invoiceConcept.setLastLiquidationDate(new Date());
        if (isInterest) {
            invoiceConcept.setInvoiceConceptParent(getNewInvoiceConcept(false, concept));
        }
        return invoiceConcept;
    }

    /**
     * crea un nuevo concepto, y parametriza si este es o no un concepto de tipo
     * carga masiva
     * 
     * @param isBulkLoad
     * @return nuevo concepto
     */
    public Concept getNewConcept(boolean isBulkLoad, boolean withIva) {
        if (withIva) {
            RetentionRate iva = new RetentionRate(RetentionRate.RETENTION_RATE_IVA);
            iva.setRetantion(true);
            RetentionRateAccount account = new RetentionRateAccount();
            account.setValue(0.16);
            ConceptRetentionRateAccount account2 = new ConceptRetentionRateAccount();
            account2.setConcept(concept);
            account2.setRetentionRateAccount(account);
        }

        return concept;
    }

    /**
     * metodo que genera una lista de tasas de usura de los trimestres (ENE-MAR)
     * y (APR-JUN), que podran ser usadas para calcular los intereses en el
     * period
     * 
     * @return
     */
    private List<Object[]> usuryRates() {
        Calendar c = Calendar.getInstance(), c1 = Calendar.getInstance();
        c.set(Calendar.MONTH, Calendar.APRIL);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c1.set(Calendar.MONTH, Calendar.JANUARY);
        c1.set(Calendar.DAY_OF_MONTH, 1);
        double r1 = 0.3, r2 = 0.5;
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
     * metodo que settea los valores del balance, calculated_cost y
     * calculated_tax a un invoiceConcept.
     * 
     * @param balance
     * @param calculated_cost
     * @param calculated_tax
     */
    public void setNumericValues(InvoiceConcept invoiceConcept, double balance, double calculated_cost, double calculated_tax) {
        invoiceConcept.setBalance(balance);
        invoiceConcept.setCalculatedTax(calculated_tax);
        invoiceConcept.setCalculatedCost(calculated_cost);
    }

    /**
     * se settean los valores de los calendar de inicio y final
     * 
     * @param invoiceConcept
     * @param ini
     * @param end
     * @param iniMonth
     * @param iniDayOfMonth
     * @param endMonth
     * @param endDayOfMonth
     */
    public void setDateValues(InvoiceConcept invoiceConcept, Calendar ini, Calendar end, int iniMonth, int iniDayOfMonth, int endMonth,
            int endDayOfMonth) {
        ini.set(Calendar.MONTH, iniMonth);
        ini.set(Calendar.DAY_OF_MONTH, iniDayOfMonth);
        end.set(Calendar.MONTH, endMonth);
        end.set(Calendar.DAY_OF_MONTH, endDayOfMonth);
        invoiceConcept.setIniPeriodDate(ini.getTime());
        invoiceConcept.setEndPeriodDate(end.getTime());
    }

    public void restartMocks() {
        entityManager = null;
        query3 = null;
        invoiceConcept = null;
        query = null;
        query2 = null;
        query4 = null;
        date = null;
        entityManager = mock(EntityManager.class);
        query3 = mock(Query.class);
        invoiceConcept = getNewInvoiceConcept(false, getNewConcept(false, true));
        query = mock(Query.class);
        query2 = mock(Query.class);
        query4 = mock(Query.class);
        date = new Date();

    }

    public void restartWhen() {
        when(query5.getResultList()).thenReturn(minimunWagesList()); 
        when(query.getResultList()).thenReturn(new ArrayList<Object>());
        when(entityManager.createQuery(" FROM IpcYearly order by year")).thenReturn(query);
        when(entityManager.createQuery(" FROM IpcMonthly  order by year,monthly ")).thenReturn(query);
        when(entityManager.createQuery(" FROM IpcAccumulated  order by year,monthly ")).thenReturn(query);
        when(query3.getResultList()).thenReturn(usuryRates());
        when(getEntityManager().createNativeQuery("SELECT * from history_usury_rate order by date desc limit 2")).thenReturn(query3);
        when(entityManager.createNativeQuery("SELECT year, value FROM  minimum_wage order by year")).thenReturn(query);
    }

    public void restartWhen2() {
        restartWhen();
        when(query2.getResultList()).thenReturn(invoiceConceptInterest1(1));
        when(
                getEntityManager().createNativeQuery(
                        "SELECT ic.* from invoice_concept ic JOIN invoice i ON i.id = ic.invoice where ic.invoice_concept_parent = " + anyInt()
                                + " and i.approved = false and last_liquidation_date >= " + anyObject() + " ORDER BY last_liquidation_date ASC",
                        InvoiceConcept.class)).thenReturn(query2);
    }

    public void restartWhen3() {
        restartWhen();
        when(query2.getResultList()).thenReturn(invoiceConceptInterestIva(1));
        when(
                getEntityManager().createNativeQuery(
                        "SELECT ic.* from invoice_concept ic JOIN invoice i ON i.id = ic.invoice where ic.invoice_concept_parent = " + anyInt()
                                + " and i.approved = false and last_liquidation_date >= " + anyObject() + " ORDER BY last_liquidation_date ASC",
                        InvoiceConcept.class)).thenReturn(query2);
    }

    public void restartWhen4() {
        restartWhen();
        when(query2.getResultList()).thenReturn(invoiceConceptInterestIva1(1));
        when(
                getEntityManager().createNativeQuery(
                        "SELECT ic.* from invoice_concept ic JOIN invoice i ON i.id = ic.invoice where ic.invoice_concept_parent = " + anyInt()
                                + " and i.approved = false and last_liquidation_date >= " + anyObject() + " ORDER BY last_liquidation_date ASC",
                        InvoiceConcept.class)).thenReturn(query2);
    }  
    
    private List<Object[]> minimunWagesList(){
        List<Object[]> answer = new ArrayList<Object[]>();
        Object[] arreglo = new Object[2];
        arreglo[0] = new Integer(2012);
        arreglo[1] = 500000.0;
        answer.add(arreglo);
        return answer;
    }

}
