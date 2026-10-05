package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.RegistrationDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAOImpl implements RegistrationDAO {
    private static final String SELECT="SELECT r.*,e.title event_title,e.event_date,e.venue,u.name attendee_name FROM registrations r JOIN events e ON e.id=r.event_id JOIN users u ON u.id=r.attendee_id ";
    private Registration map(ResultSet rs)throws SQLException{Registration r=new Registration();r.setId(rs.getLong("id"));r.setEventId(rs.getLong("event_id"));r.setAttendeeId(rs.getLong("attendee_id"));r.setEventTitle(rs.getString("event_title"));r.setAttendeeName(rs.getString("attendee_name"));r.setEventDate(rs.getDate("event_date").toLocalDate());r.setVenue(rs.getString("venue"));r.setStatus(RegistrationStatus.valueOf(rs.getString("status")));Timestamp t=rs.getTimestamp("registered_at");if(t!=null)r.setRegisteredAt(t.toLocalDateTime());return r;}
    @Override public boolean exists(long eventId,long attendeeId){try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM registrations WHERE event_id=? AND attendee_id=? AND status='REGISTERED'")){ps.setLong(1,eventId);ps.setLong(2,attendeeId);try(ResultSet rs=ps.executeQuery()){return rs.next()&&rs.getInt(1)>0;}}catch(SQLException e){throw new DatabaseException("Could not check registration.",e);}}
    @Override public int countActiveForEvent(long eventId){try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM registrations WHERE event_id=? AND status='REGISTERED'")){ps.setLong(1,eventId);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}catch(SQLException e){throw new DatabaseException("Could not count registrations.",e);}}
    @Override public void create(long eventId,long attendeeId){String sql="INSERT INTO registrations(event_id,attendee_id,status) VALUES(?,?,'REGISTERED') ON DUPLICATE KEY UPDATE status='REGISTERED',updated_at=CURRENT_TIMESTAMP";try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,eventId);ps.setLong(2,attendeeId);ps.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not register attendee.",e);}}
    private List<Registration> list(String sql,Object...args){List<Registration> result=new ArrayList<>();try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)ps.setObject(i+1,args[i]);try(ResultSet rs=ps.executeQuery()){while(rs.next())result.add(map(rs));}return result;}catch(SQLException e){throw new DatabaseException("Could not list registrations.",e);}}
    @Override public List<Registration> findByAttendee(long attendeeId){return list(SELECT+"WHERE r.attendee_id=? ORDER BY e.event_date DESC",attendeeId);}
    @Override public List<Registration> findByEvent(long eventId){return list(SELECT+"WHERE r.event_id=? AND r.status='REGISTERED' ORDER BY r.registered_at DESC",eventId);}
}
