package org.koghi.terranvm.async;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.koghi.terranvm.bean.BillingTools;
import org.koghi.terranvm.entity.Concept;

public class DatabaseManager {
	
	public static final String host = "localhost";
	public static final String port = "5432";
	public static final String db = "david_2012_03_22";
	public static final String user = "postgres";
	public static final String password = "kh27mr19fz";

	private Connection connection;

	public DatabaseManager() throws SQLException, IOException, ClassNotFoundException {

		this.connection = this.createConnection();

	}

	/**
	 * Este metodo establece la conexión con la Bases de Datos (MAXDB)
	 * 
	 * @return la conexión a la B.D.
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Connection createConnection() throws SQLException, IOException, ClassNotFoundException {

		String driver = "org.postgresql.Driver";
		String connectString = "jdbc:postgresql://"+host+":"+port+"/"+db;
		Class.forName(driver);
		this.connection = DriverManager.getConnection(connectString, user, password);

		return connection;

	}

	/**
	 * Este método ejecuta la consulta a la B.D y devuelve en un arreglo de
	 * objetos json los resultados de la misma.
	 * 
	 * @param sql
	 *            cadena de caracteres con la consulta sql que desea que se
	 *            ejecute en la B.D.
	 * @return un arreglo de objetos json procedentes de los resultados de la
	 *         consulta hecha a la B.D.
	 */
	public List<HashMap<Object, Object>> executeQuery(String sql) {

		List<HashMap<Object, Object>> array = new ArrayList<HashMap<Object,Object>>();
		try {
			System.out.println(" QUERY:");
			System.out.println(sql);

			Statement stmt = this.connection.createStatement();
			stmt.executeQuery(sql);
			ResultSet rs = stmt.executeQuery(sql);
			rs = stmt.getResultSet();

			ResultSetMetaData mdata = rs.getMetaData(); // Obtener Metadatos
			int num_columnas = mdata.getColumnCount(); // Obtener número de
														// columnas

			while (rs.next()) {
				HashMap<Object, Object> hashmap = new HashMap<Object, Object>();
				for (int cont = 1; cont <= num_columnas; cont++) {
					Object o = rs.getObject(cont);
					String name = mdata.getColumnName(cont);
					hashmap.put(name, o);
					System.out.print("LENGTH: " + BillingTools.objectToBytes(o).length+" - ");
					System.out.println(name + " = " + o);
				}
				array.add(hashmap);

			}

			stmt.close();
			System.out.println();
			System.out.println("REGISTROS RECUPERADOS.------>" + array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}


	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		DatabaseManager databaseManager = new DatabaseManager();
		List<HashMap<Object, Object>> array = databaseManager.executeQuery("SELECT * FROM invoice_concept WHERE invoice_concept.id = 90527");
		// JSONArray array =
		// databaseManager.executeQuery("SELECT * FROM invoice_concept");
		for (Object object : array) {
			@SuppressWarnings("unchecked")
			HashMap<Object, Object> hashmap = (HashMap<Object, Object>) object;
			if (hashmap.containsKey("concept_serializable")) {
				System.out.println("\n\nSERIALIZABLE (tamaño en bytes)");
				System.out.println("________________________________________________________________________________________________________");
				byte[] size1 = (byte[]) hashmap.get("concept_serializable");

				Concept concept = (Concept) BillingTools.bytesToObject((byte[]) hashmap.get("concept_serializable"));

				@SuppressWarnings("rawtypes")
				Class cc = Class.forName("org.koghi.terranvm.entity.Concept");

				Method[] ff = cc.getMethods();
				for (int i = 0; i < ff.length; i++) {
					Annotation[] annotations = ff[i].getDeclaredAnnotations();
					boolean tieneTransient = false;
					for (Annotation annotation : annotations) {
						//System.out.println(annotation);
						if (annotation.toString().equalsIgnoreCase("@javax.persistence.Transient()")) {
							tieneTransient = true;
							break;
						}
					}
					if (tieneTransient)
						continue;
					if (ff[i].getName().substring(0, 3).equals("get")) {
						try {
							Object ans = ff[i].invoke(concept, new Object[0]);
							System.out.print("LENGTH: " + (BillingTools.objectToBytes(ans).length)+" - ");
							System.out.print(ff[i].getName() + ", VALUE= ");
							System.out.println(ans);
						} catch (Exception e) {
							System.out.println("Error " + e.getMessage());
						}
					}

				}
				
				System.out.println();
				System.out.println("TAMAÑO ORIGINAL: " + size1.length);

				concept.setProjectProperty(null);
				byte[] aaa = BillingTools.objectToBytes(concept);
				System.out.println("TAMAÑO MODIFICADO SIN ProjectProperty: " + aaa.length);
				
				concept.setConceptRetentionRateAccounts(null);
				aaa = BillingTools.objectToBytes(concept);
				System.out.println("TAMAÑO MODIFICADO SIN ConceptRetentionRateAccounts: " + aaa.length);

			}
		}

	}

}