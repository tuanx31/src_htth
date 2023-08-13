package core;

import client.Player;
import database.SQL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Start {

    public static void main(String[] args) {
		 ServerManager.gI().init();

//        List<String> name = new ArrayList<>();
//        List<Integer> name1 = new ArrayList<>();
//        
//        Manager.gI().init();
//        Connection conn = null;
//        Statement st = null;
//        ResultSet rs = null;
//        try {
//            conn = SQL.gI().getCon();
//            st = conn.createStatement();
//            rs = st.executeQuery("SELECT * FROM `players`;");
//            while (rs.next()) {
//name.add(rs.getString("name"));
//name1.add(rs.getInt("id"));
//            }
//        } catch (SQLException e) {
//
//        } finally {
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            if (st != null) {
//                try {
//                    st.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//        for (int i = 0; i < name.size(); i++) {
//           Player p0 = new Player(null,name.get(i));
//            p0.setup();
//            p0.id =name1.get(i);
//            Player.flush(p0, false);
//            System.out.println(i+" / " + name.size());
//        }
    }
}
