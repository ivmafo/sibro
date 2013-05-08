package org.koghi.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Querys {

    /**
     * realiza una consulta a la base de datos.
     * 
     * @param consulta
     * @return ResultSet
     * @throws SQLException
     */
    @SuppressWarnings("static-access")
    public ResultSet consultar(String consulta) throws SQLException {
        Connection c = Concection.getInstance().getConction();
        // se prepara la consulta.
        Statement preparedStatement = c.createStatement();
        // se ejecuta la consulta y se devuelve el resulset
        ResultSet resultSet = preparedStatement.executeQuery(consulta);
        return resultSet;

    }

    /**
     * Consulta el valor de una tasa de usura y devuelve su valor double.
     * 
     * @param consulta
     * @return
     */
    public Double getTax(String consulta) {
        Double answer = null;
        ResultSet set;
        try {
            set = consultar(consulta);
            while (set.next()) {
                answer = set.getDouble(1);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer;
    }

    /**
     * Consulta los id de las notas crédito existentes en el sistema.
     * 
     * @return Lista con los id encontrados.
     */
    public List<Integer> getIdCreditNotes() {
        List<Integer> answer = new ArrayList<Integer>();

        ResultSet set;
        try {
            set = consultar("SELECT id from credit_note");
            while (set.next()) {
                answer.add(set.getInt(1));

            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return answer;
    }

    /**
     * Realiza una consulta, y devuelve un hashMap que tiene como key cada uno
     * de los conmbres de las columnas de la consulta.
     * 
     * @param consulta
     * @param campos
     * @return
     */
    public HashMap<String, Object> getFirstResult(String consulta, String[] campos) {
        HashMap<String, Object> answer = new HashMap<String, Object>();
        ResultSet set;
        // System.out.println(consulta);
        try {
            set = consultar(consulta);
            while (set.next()) {

                for (String string : campos) {
                    answer.put(string, set.getObject(string));
                    // System.out.println(string + ": " +
                    // set.getObject(string));
                }

                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer;

    }

    /**
     * metodo que consulta una dirección asociada a una entidad de negocio, y
     * devuelve dicha dirección.
     * 
     * @param businessEntity
     * @return
     */
    public String getAddres(int businessEntity) {
        String answer = "";
        try {
            ResultSet set = consultar("SELECT * from address where business_entity =" + businessEntity);
            while (set.next()) {
                answer = detetctNullValues(set, "kind_of_way") + detetctNullValues(set, "way_number") + detetctNullValues(set, "way_letter")
                        + detetctNullValues(set, "is_way_bis") + detetctNullValues(set, "way_bis_letter")
                        + detetctNullValues(set, "way_east_or_south") + detetctNullValues(set, "number") + detetctNullValues(set, "number_letter")
                        + detetctNullValues(set, "additional_number") + detetctNullValues(set, "number_east_or_south")
                        + detetctNullValues(set, "other");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer.trim();

    }

    /**
     * metodo que recorre un resulset, y una columna de este resulset, analiza
     * si el string contenido es null, de ser así, devuelve un String vacio, en
     * caso contrario, devuelve el String con un espacio al inicio.
     * 
     * @param resultSet
     * @param string
     * @return
     */
    private String detetctNullValues(ResultSet resultSet, String string) {
        try {
            if (resultSet.getString(string) == null)
                return "";
            return " " + resultSet.getString(string);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * metodo que realiza una consulta, y devuelve una lista de hashMap de los campos que se reciben como parametro.
     * @param consulta
     * @param campos
     * @return
     */
    public List<HashMap<String, Object>> getResultList(String consulta, String[] campos) {
        List<HashMap<String, Object>> answerList = new ArrayList<HashMap<String, Object>>();

        ResultSet set;
        // System.out.println(consulta);
        try {
            set = consultar(consulta);
            while (set.next()) {
                HashMap<String, Object> answer = new HashMap<String, Object>();;

                for (String string : campos) {
                    answer.put(string, set.getObject(string));
                    // System.out.println(string + ": " +
                    // set.getObject(string));
                }

                answerList.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return answerList;

    }

}
