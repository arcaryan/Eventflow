package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.TicketDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketDAOImpl implements TicketDAO {
    private static final String SELECT = "SELECT t.*, e.title event_title FROM tickets t JOIN events e ON e.id=t.event_id ";
    private Ticket map(ResultSet rs) throws SQLException {
        Ticket t=new Ticket(); t.setId(rs.getLong("id"));t.setEventId(rs.getLong("event_id"));t.setEventTitle(rs.getString("event_title"));
        t.setName(rs.getString("name"));t.setDescription(rs.getString("description"));t.setPrice(rs.getBigDecimal("price"));t.setQuantity(rs.getInt("quantity"));t.setSoldQuantity(rs.getInt("sold_quantity"));
        Timestamp start=rs.getTimestamp("sales_start_at"), end=rs.getTimestamp("sales_end_at");
        if(start!=null)t.setSalesStartAt(start.toLocalDateTime()); if(end!=null)t.setSalesEndAt(end.toLocalDateTime());
        t.setStatus(TicketStatus.valueOf(rs.getString("status"))); return t;
    }
    private List<Ticket> list(String sql,Object...args){List<Ticket> result=new ArrayList<>();try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)ps.setObject(i+1,args[i]);try(ResultSet rs=ps.executeQuery()){while(rs.next())result.add(map(rs));}return result;}catch(SQLException e){throw new DatabaseException("Could not list tickets.",e);}}
    @Override public List<Ticket> findByEvent(long eventId){return list(SELECT+"WHERE t.event_id=? ORDER BY t.price",eventId);}
    @Override public List<Ticket> findByOrganizer(long organizerId){return list(SELECT+"WHERE e.organizer_id=? ORDER BY e.event_date,t.price",organizerId);}
    @Override public Ticket findById(long id){List<Ticket> list=list(SELECT+"WHERE t.id=?",id);return list.isEmpty()?null:list.get(0);}
    @Override public long create(Ticket t){String sql="INSERT INTO tickets(event_id,name,description,price,quantity,sales_start_at,sales_end_at,status) VALUES(?,?,?,?,?,?,?,?)";try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){ps.setLong(1,t.getEventId());ps.setString(2,t.getName());ps.setString(3,t.getDescription());ps.setBigDecimal(4,t.getPrice());ps.setInt(5,t.getQuantity());if(t.getSalesStartAt()==null)ps.setTimestamp(6,null);else ps.setTimestamp(6,Timestamp.valueOf(t.getSalesStartAt()));if(t.getSalesEndAt()==null)ps.setTimestamp(7,null);else ps.setTimestamp(7,Timestamp.valueOf(t.getSalesEndAt()));ps.setString(8,t.getStatus().name());ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next()){t.setId(rs.getLong(1));return rs.getLong(1);}}throw new SQLException("No generated ticket id.");}catch(SQLException e){throw new DatabaseException("Could not create ticket.",e);}}
    @Override public void update(Ticket t){String sql="UPDATE tickets t JOIN events e ON e.id=t.event_id SET t.name=?,t.description=?,t.price=?,t.quantity=?,t.sales_start_at=?,t.sales_end_at=?,t.status=? WHERE t.id=? AND e.organizer_id=? AND t.sold_quantity<=t.quantity";try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,t.getName());ps.setString(2,t.getDescription());ps.setBigDecimal(3,t.getPrice());ps.setInt(4,t.getQuantity());ps.setTimestamp(5,t.getSalesStartAt()==null?null:Timestamp.valueOf(t.getSalesStartAt()));ps.setTimestamp(6,t.getSalesEndAt()==null?null:Timestamp.valueOf(t.getSalesEndAt()));ps.setString(7,t.getStatus().name());ps.setLong(8,t.getId());ps.setLong(9,t.getEventId());ps.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not update ticket.",e);}}
    @Override public void delete(long id,long organizerId){String sql="DELETE t FROM tickets t JOIN events e ON e.id=t.event_id WHERE t.id=? AND e.organizer_id=? AND t.sold_quantity=0";try(Connection c=DBConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setLong(1,id);ps.setLong(2,organizerId);ps.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not delete ticket.",e);}}
    @Override public boolean reserveStock(Connection c,long ticketId,int quantity){try(PreparedStatement ps=c.prepareStatement("UPDATE tickets SET sold_quantity=sold_quantity+?, status=CASE WHEN sold_quantity+? >= quantity THEN 'SOLD_OUT' ELSE status END WHERE id=? AND status='ACTIVE' AND sold_quantity+? <= quantity")){ps.setInt(1,quantity);ps.setInt(2,quantity);ps.setLong(3,ticketId);ps.setInt(4,quantity);return ps.executeUpdate()==1;}catch(SQLException e){throw new DatabaseException("Could not reserve ticket stock.",e);}}
}
