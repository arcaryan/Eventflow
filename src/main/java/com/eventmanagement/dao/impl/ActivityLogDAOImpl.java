package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.ActivityLogDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.ActivityLog;
import com.eventmanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAOImpl implements ActivityLogDAO {
    private ActivityLog map(ResultSet rs)throws SQLException{ActivityLog l=new ActivityLog();l.setId(rs.getLong("id"));long actor=rs.getLong("actor_user_id");if(!rs.wasNull())l.setActorUserId(actor);l.setActorName(rs.getString("actor_name"));l.setActionType(rs.getString("action_type"));l.setEntityType(rs.getString("entity_type"));long entity=rs.getLong("entity_id");if(!rs.wasNull())l.setEntityId(entity);l.setDescription(rs.getString("description"));Timestamp t=rs.getTimestamp("created_at");if(t!=null)l.setCreatedAt(t.toLocalDateTime());return l;}
    @Override public void create(ActivityLog l){String sql="INSERT INTO activity_logs(actor_user_id,action_type,entity_type,entity_id,description) VALUES(?,?,?,?,?)";try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){if(l.getActorUserId()==null)ps.setNull(1,Types.BIGINT);else ps.setLong(1,l.getActorUserId());ps.setString(2,l.getActionType());ps.setString(3,l.getEntityType());if(l.getEntityId()==null)ps.setNull(4,Types.BIGINT);else ps.setLong(4,l.getEntityId());ps.setString(5,l.getDescription());ps.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not write activity log.",e);}}
    @Override public List<ActivityLog> findRecent(int limit){List<ActivityLog> r=new ArrayList<>();String sql="SELECT a.*,u.name actor_name FROM activity_logs a LEFT JOIN users u ON u.id=a.actor_user_id ORDER BY a.created_at DESC LIMIT ?";try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setInt(1,Math.max(1,Math.min(limit,100)));try(ResultSet rs=ps.executeQuery()){while(rs.next())r.add(map(rs));}return r;}catch(SQLException e){throw new DatabaseException("Could not list activity logs.",e);}}
}
