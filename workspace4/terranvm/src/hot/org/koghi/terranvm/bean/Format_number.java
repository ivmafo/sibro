package org.koghi.terranvm.bean;

import java.text.DecimalFormat;
import java.util.Locale;

public class Format_number {

	/**
	 * Redondea numeros de tipo doble, para establecer un maximo de 4 decimales.
	 * 
	 * @param number
	 *            debe ser de tipo dpble, y sera quien sufra el formato.
	 * @return regresa el numero redondeado.
	 */
	public static double Format(double number) {
		DecimalFormat formato = new DecimalFormat("#.00");
		formato.format(number);
		return Double.valueOf(formato.format(number));
	}

	/**
	 * Redondea numeros de tipo doble, para establecer un maximo de 4 decimales.
	 * 
	 * @param number
	 *            debe ser de tipo dpble, y sera quien sufra el formato.
	 * @return regresa el numero redondeado.
	 */
	public static String FormatToString(double number) {
		DecimalFormat formato = new DecimalFormat("#.####");
		return formato.format(number);
	}

	public static String FormatToString2(double number) {
		Locale.setDefault(Locale.ENGLISH);
		DecimalFormat formato = new DecimalFormat("#######.00");
		return formato.format(number);
	}
	public static String FormatToString3(double number) {
		DecimalFormat formato = new DecimalFormat("#,###,###.00");
		return formato.format(number);
	}
	
	public static String FormatToString4(double number){
		DecimalFormat formato = new DecimalFormat("#,###,###");
		return formato.format(number);
	}
}
