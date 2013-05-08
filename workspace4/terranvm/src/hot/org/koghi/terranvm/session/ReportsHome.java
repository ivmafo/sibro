package org.koghi.terranvm.session;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityHome;
import org.koghi.terranvm.entity.Recover;
import org.richfaces.component.html.HtmlExtendedDataTable;

@Name("reportsHome")
public class ReportsHome extends EntityHome<Recover> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @In(required = false)
    public String projectFilter;

    @In(required = false)
    @Out(required = false)
    private HtmlExtendedDataTable htmlReportsDataTable;
    private String reportsListTableState;
    private List<Object[]> reportsList;
    private boolean opened = true;
    private final String GET_PARAMETER = "&projectId=";
    private final String DEFAULT_URL = "http://www.google.com";

    /**
     * El reporte seleccionado tiene un arreglo con los siguientes valores: pos
     * 0 = id pos 1 = name pos 2 = description pos 3 = link pos 4 =
     * report_for_all_projects
     */
    private Object[] selectedReport;

    public void log(Object o) {
        toLog(o == null ? "NULL" : o);
    }

    public void log() {
        toLog("");
    }

    public void toLog(Object o) {
        String prefix = " > > > > > > > > > > " + o.toString();
        Logger.getLogger(this.getClass().toString()).log(Level.INFO, prefix);
    }

    public HtmlExtendedDataTable getHtmlReportsDataTable() {
        return htmlReportsDataTable;
    }

    public void setHtmlReportsDataTable(HtmlExtendedDataTable htmlReportsDataTable) {
        this.htmlReportsDataTable = htmlReportsDataTable;
    }

    public String getReportsListTableState() {
        return reportsListTableState;
    }

    public void setReportsListTableState(String reportsListTableState) {
        this.reportsListTableState = reportsListTableState;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getReportsList() {
        if (this.reportsList == null) {
            int projectId = Integer.parseInt(this.projectFilter);
            String queryString = "SELECT rep.id, rep.name, rep.description, rep.link, rep.report_for_all_projects FROM reports rep WHERE rep.report_for_all_projects = ?";
            Query q = this.getEntityManager().createNativeQuery(queryString);
            if (projectId > 0) {
                q.setParameter(1, false);
            } else {
                q.setParameter(1, true);
            }
            this.reportsList = q.getResultList();
            if (this.reportsList.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "REPORTES", "No se encontraron reportes"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "REPORTES", "Número de reportes encontrados: " + this.reportsList.size()));
            }
        }
        return reportsList;
    }

    public void setReportsList(List<Object[]> reportsList) {
        this.reportsList = reportsList;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Object[] getSelectedReport() {
        return selectedReport;
    }

    public void setSelectedReport(Object[] selectedReport) {
        this.selectedReport = selectedReport;
    }

    public void instanceSelectedReport(Object[] selectedReport) {
        this.setSelectedReport(selectedReport);
        this.opened = false;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "REPORTES", "Reporte seleccionado: " + this.selectedReport[1]));
    }

    public String getLink() {
        if (this.selectedReport != null) {
            String link = this.selectedReport[3].toString();
            if(!link.startsWith("http://"))
                link = "http://" + link;
            boolean genericReport = new Boolean(this.selectedReport[4].toString());
            if (genericReport) {
                return link;
            } else {
                return link+ this.GET_PARAMETER + this.projectFilter;
            }
        }
        return this.DEFAULT_URL;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

}
