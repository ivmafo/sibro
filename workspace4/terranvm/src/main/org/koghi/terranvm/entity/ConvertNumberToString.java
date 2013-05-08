package org.koghi.terranvm.entity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;


public class ConvertNumberToString {
	 
		private static final String[] UNIDADES = { "", "UN ", "DOS ", "TRES ",
				"CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ",
				"ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS",
				"DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE" };
	 
		private static final String[] DECENAS = { "VENTI", "TREINTA ", "CUARENTA ",
				"CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA ",
				"CIEN " };
	 
		private static final String[] CENTENAS = { "CIENTO ", "DOSCIENTOS ",
				"TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
				"SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS " };
		
		private static final TreeMap<Integer, String[]> MILLONES = new TreeMap<Integer, String[]>();
		
		{
			MILLONES.put(0, null);
			MILLONES.put(1, new String("MILLON,MILLONES").split(","));
			MILLONES.put(2, new String("BILLON,BILLONES").split(","));
			MILLONES.put(3, new String("TRILLON,TRILLONES").split(","));
		}
	 
		/**
		 * Convert number to letters
		 * <p>
		 * 
		 * @param number in String
		 * @return string number
		 * @throws NumberFormatException
		 */
		public static String convertNumberToLetter(String number) {
			return convertNumberToLetter(Double.parseDouble(number));
		}
	 
		/**
		 * Convert number to letters. The number must be less to 999'999.999 
		 * @param number
		 * @return string number
		 * @throws NumberFormatException
		 */
		public static String convertNumberToLetter(double number)
				throws NumberFormatException {
			String converted = new String();
	 
			// Validamos que sea un numero legal
			double doubleNumber = number;
			if (doubleNumber > 999999999)
				throw new NumberFormatException(
						"El numero es mayor de 999'999.999, "
								+ "no es posible convertirlo");
	 
			String splitNumber[] = String.valueOf(doubleNumber).replace('.', '#')
					.split("#");
	 
			// Descompone el trio de millones - ¡SGT!
			int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
					8))
					+ String.valueOf(getDigitAt(splitNumber[0], 7))
					+ String.valueOf(getDigitAt(splitNumber[0], 6)));
			if (millon == 1)
				converted = "UN MILLON ";
			if (millon > 1)
				converted = convertNumber(String.valueOf(millon)) + "MILLONES ";
	 
			// Descompone el trio de miles - ¡SGT!
			int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
					5))
					+ String.valueOf(getDigitAt(splitNumber[0], 4))
					+ String.valueOf(getDigitAt(splitNumber[0], 3)));
			if (miles == 1)
				converted += "MIL ";
			if (miles > 1)
				converted += convertNumber(String.valueOf(miles)) + "MIL ";
	 
			// Descompone el ultimo trio de unidades - ¡SGT!
			int cientos = Integer.parseInt(String.valueOf(getDigitAt(
					splitNumber[0], 2))
					+ String.valueOf(getDigitAt(splitNumber[0], 1))
					+ String.valueOf(getDigitAt(splitNumber[0], 0)));
			if (cientos == 1)
				converted += "UN";
	 
			if (millon + miles + cientos == 0)
				converted += "CERO";
			if (cientos > 1)
				converted += convertNumber(String.valueOf(cientos));
	 
			//converted += "PESOS";
	 
			// Descompone los centavos - Camilo
			int centavos = Integer.parseInt(String.valueOf(getDigitAt(
					splitNumber[1], 2))
					+ String.valueOf(getDigitAt(splitNumber[1], 1))
					+ String.valueOf(getDigitAt(splitNumber[1], 0)));
			if (centavos == 1)
				converted += " CON UN CENTAVO";
			if (centavos > 1)
				converted += " CON " + convertNumber(String.valueOf(centavos))
						+ "CENTAVOS";
	 
			return converted;
		}
	 
		/**
		 * Convert number to letters. The number must be less to three digits
		 * @param number
		 * @return string number
		 */
		private static String convertNumber(String number) {
			if (number.length() > 3)
				throw new NumberFormatException(
						"La longitud maxima debe ser 3 digitos");
	 
			String output = new String();
			if (getDigitAt(number, 2) != 0)
				output = CENTENAS[getDigitAt(number, 2) - 1];
	 
			int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1))
					+ String.valueOf(getDigitAt(number, 0)));
	 
			if (k <= 20)
				output += UNIDADES[k];
			else {
				if (k > 30 && getDigitAt(number, 0) != 0)
					output += DECENAS[getDigitAt(number, 1) - 2] + "Y "
							+ UNIDADES[getDigitAt(number, 0)];
				else
					output += DECENAS[getDigitAt(number, 1) - 2]
							+ UNIDADES[getDigitAt(number, 0)];
			}
	 
			// Caso especial con el 100
			if (getDigitAt(number, 2) == 1 && k == 0)
				output = "CIEN";
	 
			return output;
		}
	 
		/**
		 * Return a digit at index difined
		 * @param origin
		 * @param position
		 * @return Digit at index
		 */
		private static int getDigitAt(String origin, int position) {
			if (origin.length() > position && position >= 0)
				return origin.charAt(origin.length() - position - 1) - 48;
			return 0;
		}
		
		/**
		 * Convert number to letters with pesos and centavos format
		 * @param number
		 * @return string number
		 */
		public String convertToString(double number){
			Locale.setDefault(Locale.US);
			DecimalFormat formatter = new DecimalFormat("##0.0#");
			String [] split = formatter.format(number).replace(".", "#").split("#");
			ArrayList<String> aux =  new ArrayList<String>();
			String res = "";
			if ((split[0].length()%6) != 0)
				aux = splitEachSix(split[0],0,split[0].length()%6);
			else if (split[0].length()!= 0)
				aux = splitEachSix(split[0],0,6);
			
			int contAux=0;
			for (int i = aux.size()-1; i >= 0; i--) {
				String string = aux.get(i);
				if (Double.parseDouble(string)!=0){
					
					if (string.endsWith("1") && MILLONES.get(contAux)!=null)
						res = ConvertNumberToString.convertNumberToLetter(string) + MILLONES.get(contAux)[0] + " " + res;
					else if (MILLONES.get(contAux)!=null)
						res = ConvertNumberToString.convertNumberToLetter(string) + MILLONES.get(contAux)[1] + " "+res;
					else
						res = ConvertNumberToString.convertNumberToLetter(string) + res;
				}
				contAux++;
			}
			
			if (res.endsWith("LLONES "))
				res += "DE PESOS";
			else
				res += "PESOS";
			
			if (split.length>1 && Double.parseDouble(split[1])!=0){
				if (Double.parseDouble(split[1])==1)
					res += " CON UN CENTAVO";
				else
					res += " CON " + ConvertNumberToString.convertNumberToLetter(split[1]) + "CENTAVOS";
			}
			return res;
		}

		/**
		 * Split string to each six positions starting in i index
		 * @param string to split
		 * @param i 
		 * @param j
		 * @return split string
		 */
		private ArrayList<String> splitEachSix(String string, int i, int j) {
			ArrayList<String> aux =  new ArrayList<String>();
			aux.add(string.substring(i, j));
			if (j!=string.length())
				aux.addAll(splitEachSix(string,j,j+6));
			return aux;
		}
	 
	
}
