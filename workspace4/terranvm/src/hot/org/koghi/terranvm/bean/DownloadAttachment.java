package org.koghi.terranvm.bean;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;

@Name("downloadAttachment")
public class DownloadAttachment {
	


	
		
	@In(value="#{facesContext}")
	FacesContext facesContext;
	

	
	
	@RequestParameter
	private String relativePath;
	
	
	public String download() {
		HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response.setContentType("text/plain");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
		path = path.substring(0, path.lastIndexOf("/")) + "/";
		String name = this.relativePath.substring(this.relativePath.lastIndexOf("/")+1, this.relativePath.length());
        response.addHeader("Content-disposition", "attachment; filename=\"" + name +"\"");
        
		try {
			//File f = new File(path+this.relativePath);
			File f = new File(this.relativePath);
			FileInputStream fileInputStream = new FileInputStream(f);
			ServletOutputStream os = response.getOutputStream();
			int i = -1;
			while((i= fileInputStream.read()) != -1){
				os.write(i);
			}
			os.flush();
			os.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch(Exception e) {
			System.out.println("\nFailure : " + e.toString() + "\n");
		}

		return null;
	}
	
	
	public String download(String linkPDF) {
		HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response.setContentType("application/pdf");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		String server = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath(server);
		path = path.substring(0, path.lastIndexOf("/")) + "/";
		String name = linkPDF.substring(linkPDF.lastIndexOf("/")+1, linkPDF.length());
        response.setHeader("Content-disposition", "attachment; filename=\"" + name +"\"");
        
		try {
			//File f = new File(path+this.relativePath);
			response.setHeader("Cache-Control", "no-cache");
			File f = new File(path+linkPDF);
			response.setContentLength((int)f.length());
			FileInputStream fileInputStream = new FileInputStream(f);
			ServletOutputStream os = response.getOutputStream();
			int i = -1;
			while((i= fileInputStream.read()) != -1){
				os.write(i);
			}
			os.flush();
			os.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch(Exception e) {
			System.out.println("\nFailure : " + e.toString() + "\n");
		}

		return null;
	}


	
	public String download(byte[] pdf,String name) {
		HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response.setContentType("application/pdf");
		try {
			response.setHeader("Content-disposition", "attachment; filename=\"" + name +"\"");
			response.setContentLength((int)pdf.length);
			ServletOutputStream os = response.getOutputStream();
			os.write(pdf);
			os.flush();
			os.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch(Exception e) {
			System.out.println("\nFailure : " + e.toString() + "\n");
		}

		return null;
	}
	
	public String downloadAux(String relativePath) { 
		setRelativePath(relativePath);
		return download();
	}
	
	
	public String getRelativePath() {
		return relativePath;
	}


	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
	
	
}