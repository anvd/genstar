package ummisco.genstar.dao.derby;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ummisco.genstar.exception.GenstarDAOException;

public class DatabaseResetter  {

    private static Connection getConnection() throws GenstarDAOException {
		return ((DerbyGenstarDAOFactory) DerbyGenstarDAOFactory.getInstance()).getConnection();
    }

    public static void resetDatabase() throws GenstarDAOException {
        String s = new String();
        StringBuffer sb = new StringBuffer();

        try {
            FileReader sqlScriptReader = new FileReader(new File("dbms/sql_scripts/genstar_sql_script.sql"));
            // be sure to not have line starting with "--" or "/*" or any other non aplhabetical character

            BufferedReader bufferedScriptReader = new BufferedReader(sqlScriptReader);

            while((s = bufferedScriptReader.readLine()) != null) { sb.append(s); }
            bufferedScriptReader.close();

            // here is our splitter ! We use ";" as a delimiter for each request
            // then we are sure to have well formed statements
            String[] inst = sb.toString().split(";");

            Connection connnection = getConnection();
            Statement sqlStatement = connnection.createStatement();

            for (int i = 0; i<inst.length; i++) {
                // we ensure that there is no spaces before or after the request string
                // in order to not execute empty statements
                if (!inst[i].trim().equals("")) {
                    sqlStatement.executeUpdate(inst[i]);
                    // System.out.println(">>"+inst[i]);
                }
            }
        } catch (Exception e) {
//            System.out.println("*** Error : "+e.toString());
//            System.out.println("*** ");
//            System.out.println("*** Error : ");
//            System.out.println("################################################");
//            System.out.println(sb.toString());
            
            throw new GenstarDAOException(e);
        }
    }
    
    
    public static void main(String[] args) throws Exception {
    	resetDatabase();
    }
}