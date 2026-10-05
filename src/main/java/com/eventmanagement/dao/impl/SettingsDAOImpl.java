package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.SettingsDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.util.DBConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsDAOImpl implements SettingsDAO {
    @Override public Map<String,String> findAll(){Map<String,String> r=new LinkedHashMap<>();try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("SELECT setting_key,setting_value FROM system_settings ORDER BY setting_key");ResultSet rs=ps.executeQuery()){while(rs.next())r.put(rs.getString(1),rs.getString(2));return r;}catch(SQLException e){throw new DatabaseException("Could not load settings.",e);}}
    @Override public void update(String key,String value){try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("INSERT INTO system_settings(setting_key,setting_value) VALUES(?,?) ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value)")){ps.setString(1,key);ps.setString(2,value);ps.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not update setting.",e);}}
}
