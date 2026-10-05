package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.TicketOrderDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketOrderDAOImpl implements TicketOrderDAO {
    @Override public void create(Connection c,TicketOrder o){String sql="INSERT INTO ticket_orders(attendee_id,event_id,ticket_id,quantity,unit_price,total_amount,payment_status,reference_code) VALUES(?,?,?,?,?,?,?,?)";try(PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,o.getAttendeeId());ps.setLong(2,o.getEventId());ps.setLong(3,o.getTicketId());ps.setInt(4,o.getQuantity());ps.setBigDecimal(5,o.getUnitPrice());ps.setBigDecimal(6,o.getTotalAmount());ps.setString(7,o.getPaymentStatus().name());ps.setString(8,o.getReferenceCode());ps.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not create ticket order.",e);}}
    private TicketOrder map(ResultSet rs)throws SQLException{TicketOrder o=new TicketOrder();o.setId(rs.getLong("id"));o.setAttendeeId(rs.getLong("attendee_id"));o.setEventId(rs.getLong("event_id"));o.setTicketId(rs.getLong("ticket_id"));o.setEventTitle(rs.getString("event_title"));o.setTicketName(rs.getString("ticket_name"));o.setQuantity(rs.getInt("quantity"));o.setUnitPrice(rs.getBigDecimal("unit_price"));o.setTotalAmount(rs.getBigDecimal("total_amount"));o.setPaymentStatus(PaymentStatus.valueOf(rs.getString("payment_status")));o.setReferenceCode(rs.getString("reference_code"));Timestamp t=rs.getTimestamp("created_at");if(t!=null)o.setCreatedAt(t.toLocalDateTime());return o;}
    @Override public List<TicketOrder> findByAttendee(long attendeeId){List<TicketOrder> r=new ArrayList<>();String sql="SELECT o.*,e.title event_title,t.name ticket_name FROM ticket_orders o JOIN events e ON e.id=o.event_id JOIN tickets t ON t.id=o.ticket_id WHERE o.attendee_id=? ORDER BY o.created_at DESC";try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,attendeeId);try(ResultSet rs=ps.executeQuery()){while(rs.next())r.add(map(rs));}return r;}catch(SQLException e){throw new DatabaseException("Could not list orders.",e);}}
    @Override public long countByAttendee(long attendeeId){try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM ticket_orders WHERE attendee_id=? AND payment_status='PAID'")){ps.setLong(1,attendeeId);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getLong(1):0;}}catch(SQLException e){throw new DatabaseException("Could not count orders.",e);}}
    @Override public int quantityForEvent(long attendeeId,long eventId){try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement("SELECT COALESCE(SUM(quantity),0) FROM ticket_orders WHERE attendee_id=? AND event_id=? AND payment_status='PAID'")){ps.setLong(1,attendeeId);ps.setLong(2,eventId);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getInt(1):0;}}catch(SQLException e){throw new DatabaseException("Could not check attendee ticket limit.",e);}}
}
