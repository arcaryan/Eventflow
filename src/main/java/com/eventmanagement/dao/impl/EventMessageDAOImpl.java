package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.EventMessageDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.EventMessage;
import com.eventmanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventMessageDAOImpl implements EventMessageDAO {
    private EventMessage map(ResultSet rs)throws SQLException{EventMessage m=new EventMessage();m.setId(rs.getLong("id"));m.setEventId(rs.getLong("event_id"));m.setOrganizerId(rs.getLong("organizer_id"));m.setEventTitle(rs.getString("event_title"));m.setSubject(rs.getString("subject"));m.setMessage(rs.getString("message"));Timestamp t=rs.getTimestamp("created_at");if(t!=null)m.setCreatedAt(t.toLocalDateTime());return m;}
    @Override public void create(EventMessage m){try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("INSERT INTO event_messages(event_id,organizer_id,subject,message) VALUES(?,?,?,?)")){ps.setLong(1,m.getEventId());ps.setLong(2,m.getOrganizerId());ps.setString(3,m.getSubject());ps.setString(4,m.getMessage());ps.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not send event update.",e);}}
    private List<EventMessage> list(String sql,Object...args){List<EventMessage> r=new ArrayList<>();try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)ps.setObject(i+1,args[i]);try(ResultSet rs=ps.executeQuery()){while(rs.next())r.add(map(rs));}return r;}catch(SQLException e){throw new DatabaseException("Could not list event updates.",e);}}
    @Override public List<EventMessage> findByOrganizer(long id){return list("SELECT m.*,e.title event_title FROM event_messages m JOIN events e ON e.id=m.event_id WHERE m.organizer_id=? ORDER BY m.created_at DESC",id);}
    @Override public List<EventMessage> findForAttendee(long id){return list("SELECT DISTINCT m.*,e.title event_title FROM event_messages m JOIN events e ON e.id=m.event_id JOIN registrations r ON r.event_id=e.id WHERE r.attendee_id=? AND r.status='REGISTERED' ORDER BY m.created_at DESC",id);}
}
