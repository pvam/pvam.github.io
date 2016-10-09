package Utils;

import java.sql.*;

/**
 * Created by dsl on 7/9/15.
 */
public class Database {
    //    public static FileWriter resFile;
    String databaseURL;
    String databaseUser = "vamshi";
    String databasePassword = "";
    Connection connection;

    public Database(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public void connectDB() throws Exception {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(databaseURL, databaseUser,
                databasePassword);
        System.out.println("Connected to PgSQL!");
    }

    public void close() {
        try {
            connection.close();
//            resFile.close();
        } catch (Exception e) {
            System.out.println("Exception in close: " + e);
            e.printStackTrace();
        }
    }

    public String getQueryResult(String query) {
        Statement st;
        ResultSet rs;
        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            String s = null;
            if (rs.next())
                s = rs.getString(1);
            rs.close();
            st.close();

            return s;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void executeQuery(String query) {
        Statement st;
        try {
            st = connection.createStatement();
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
