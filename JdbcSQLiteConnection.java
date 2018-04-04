package examples.com.intelligt.modbus.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;


public class JdbcSQLiteConnection {
    public static String dbURL = "jdbc:sqlite:C:/Teledyne/study.db";
    public static String tableName = null;
    // jdbc Connection
    public static Connection conn = null;
    public static Statement stmt = null;
     /**
     * Connect to a sample database
     */
    public static Connection createConnection() {
     //
        try {
            // db parameters
            String url = dbURL;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e);        
                    
         
        
        }
        return conn;
    }
    private static void insertReading()//(String[] readingToStore)
    {
        try
        {

            //String url = dbURL;
            // create a connection to the database
             conn = DriverManager.getConnection(dbURL);

             Statement st = conn.createStatement();
             System.out.print("\nConnection OK");

             //String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
             Timestamp timestamp = new Timestamp(System.currentTimeMillis());
             
                        //2016-11-16 06:43:19.77
            System.out.format("\nTimestamp = %s\n",timestamp);

//          st.execute("insert into '"+tableName+"' ()values ('"+timestamp+"','00.1','0.2','00.0','1200','1070','1800','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1')");
            st.execute("insert into '"+tableName+"' (timestmp,ph1,temp1,ph2,temp2,mAo1_25,mAo1_26,mAo1_27,mAo1_28,mAo1_29,mAo2_25,mAo2_26,mAo2_27,mAo2_28,mAo2_29,relay1_16,relay1_17,relay1_18,relay1_19,relay1_20,relay1_21,relay1_22,relay1_23,relay2_16,relay2_17,relay2_18,relay2_19,relay2_20,relay2_21,relay2_22,relay2_23,relay3_16,relay3_17,relay3_18,relay3_19,relay3_20,relay3_21,relay3_22,relay3_23) values ('"+timestamp+"','00.1','0.2','00.0','1200','1070','1800','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1')");


            System.out.print("Insert Success\n");
            st.close();
        }
        catch (SQLException sqlExcept)
        { System.out.format("SQLException Insert");
            JOptionPane.showMessageDialog(null, sqlExcept);
        }
    }


     private static void selectReading()
    {
        try
        {
             String url = dbURL;
            // create a connection to the database
             conn = DriverManager.getConnection(url);
             String query = "SELECT * FROM '"+tableName+"'";

             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query);

            /*stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName);*/
            ResultSetMetaData rsmd = rs.getMetaData();

            int numberCols = rsmd.getColumnCount();
            for (int i=1; i<=numberCols; i++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(i)+"\t\t");
            }
        System.out.format("\nSQLException Reading resultset");
            int i=1;
       while (rs.next())
      {
        Timestamp ts1 = rs.getTimestamp("timestmp");
        String phValue1 = rs.getString("ph1");
        String temp1 = rs.getString("temp1");
        String temp2 = rs.getString("temp2");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
        i++;

        // print the results
        System.out.format("\n%s   %s   %s   %s   ",sdf.format(ts1),phValue1,temp1,temp2);
      }
      conn.close();
        }
        catch (SQLException sqlExcept)
        {
            System.out.format("SQLException Reading");
            JOptionPane.showMessageDialog(null, sqlExcept);
        }
    }

     public static void shutdown()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                conn.close();
            }
        }
        catch (SQLException sqlExcept)
        {
   System.out.format("SQLException Shutdown");
   JOptionPane.showMessageDialog(null, sqlExcept);
        }
    }

    /**
     * @param args the command line arguments
     *//*
    public static void main(String[] args) {
        createConnection();
     insertReading();
      selectReading();
    shutdown();
    }

*/
}