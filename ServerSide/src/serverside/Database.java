/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverside;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reborn
 */
public class Database {
    static private Connection conn;
    public Database(){
        try {
                Class.forName("com.mysql.jdbc.Driver");
                conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/nurpacific?" + "user=root&password=root");
            } catch (SQLException ex) {
                Logger.getLogger(PlayerTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PlayerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public ResultSet getLagu(int id){
       PreparedStatement pst;
        try {
            pst = conn.prepareStatement("Select * from lagu where id_lagu=?");
            pst.setInt(1, id); 
            ResultSet rs = pst.executeQuery();                        
            return rs;     
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ResultSet getLagu(String judul){
        PreparedStatement pst;
        try {
            pst = conn.prepareStatement("Select * from lagu where nama_lagu like ?");
            judul="%"+judul+"%";
            pst.setString(1, judul); 
            ResultSet rs = pst.executeQuery();                        
            return rs;     
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ResultSet getAlbum(int id){
        PreparedStatement pst;
        try {
            pst = conn.prepareStatement("Select * from album where id_album=?");
            pst.setInt(1, id); 
            ResultSet rs = pst.executeQuery();                        
            return rs;     
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ResultSet getPenyanyi(int id){
        PreparedStatement pst;
        try {
            pst = conn.prepareStatement("Select * from penyanyi where id_penyanyi=?");
            pst.setInt(1, id); 
            ResultSet rs = pst.executeQuery();                        
            return rs;     
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
