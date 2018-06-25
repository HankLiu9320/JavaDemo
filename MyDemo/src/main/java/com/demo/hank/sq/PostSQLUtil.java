package com.demo.hank.sq;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostSQLUtil {
   private static String url = "jdbc:postgresql://192.168.183.114:5432/postgres";
   private static String user = "trade";
   private static String password = "trade";

   public static Connection getConn() {
      Connection connection = null;
      try {
         Class.forName("org.postgresql.Driver");
         connection = DriverManager.getConnection(url, user, password);
         return connection;
      }
      catch(Exception e) {
         e.printStackTrace();
      }

      return null;
   }

   public static List<String> getFirstCol(String sql) throws SQLException {
      Connection c = getConn();
      Statement st = c.createStatement();
      ResultSet set = st.executeQuery(sql);
      List<String> list = new ArrayList<String>();

      while(set.next()) {
         String col = set.getString(1);
         list.add(col);
      }

      return list;
   }

   public static void main(String[] args) throws SQLException {
      String sql = "select name from public.tra_shang_quan group by name";
      List<String> list = getFirstCol(sql);
      System.err.println(list);
   }
}
