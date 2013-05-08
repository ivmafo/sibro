package org.koghi.terranvm.async;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;

public class Log {

	public org.apache.log4j.Logger log;
	

	public Log(Object object) {
		URL url = Loader.getResource("log4j.properties");
	    PropertyConfigurator.configure(url);
		this.log = Logger.getLogger(object.getClass());
		
	}
	

}
