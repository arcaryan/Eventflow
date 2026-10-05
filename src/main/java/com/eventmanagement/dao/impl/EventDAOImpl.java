package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDAOImpl implements EventDAO {
    private static final String SELECT = "SELECT e.*, u.name organizer_name, "
            + "(SELECT COUNT(*) FROM registrations r WHERE r.event_id=e.id AND r.status='REGISTERED') registration_count, "
            + "(SELECT COALESCE(SUM(t.sold_quantity),0) FROM tickets t WHERE t.event_id=e.id) tickets_sold, "
            + "(SELECT COALESCE(SUM(o.total_amount),0) FROM ticket_orders o WHERE o.event_id=e.id AND o.payment_status='PAID') revenue "
            + "FROM events e JOIN users u ON u.id=e.organizer_id ";

    private Event map(ResultSet rs) throws SQLException {
        Event event = new Event(rs.getLong("id"), rs.getLong("organizer_id"), rs.getString("title"), rs.getString("description"),
                rs.getString("category"), rs.getDate("event_date").toLocalDate(), rs.getTime("start_time").toLocalTime(),
                rs.getTime("end_time").toLocalTime(), rs.getString("venue"), rs.getInt("capacity"), rs.getString("banner_url"),
                EventStatus.valueOf(rs.getString("status")));
        event.setOrganizerName(rs.getString("organizer_name"));
        event.setRejectionReason(rs.getString("rejection_reason"));
        event.setRegistrationCount(rs.getInt("registration_count"));
        event.setTicketsSold(rs.getInt("tickets_sold"));
        event.setRevenue(rs.getBigDecimal("revenue"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) event.setCreatedAt(created.toLocalDateTime());
        return event;
    }

    private void set(PreparedStatement ps, Event event) throws SQLException {
        ps.setLong(1, event.getOrganizerId()); ps.setString(2, event.getTitle()); ps.setString(3, event.getDescription());
        ps.setString(4, event.getCategory()); ps.setDate(5, Date.valueOf(event.getEventDate())); ps.setTime(6, Time.valueOf(event.getStartTime()));
        ps.setTime(7, Time.valueOf(event.getEndTime())); ps.setString(8, event.getVenue()); ps.setInt(9, event.getCapacity());
        ps.setString(10, event.getBannerUrl()); ps.setString(11, event.getStatus().name());
    }

    @Override public long create(Event event) {
        String sql = "INSERT INTO events (organizer_id,title,description,category,event_date,start_time,end_time,venue,capacity,banner_url,status) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            set(ps, event); ps.executeUpdate();
            try (ResultSet rs=ps.getGeneratedKeys()) { if (rs.next()) { event.setId(rs.getLong(1)); return rs.getLong(1); } }
            throw new SQLException("No generated event id.");
        } catch (SQLException e) { throw new DatabaseException("Could not create event.", e); }
    }

    @Override public void update(Event event) {
        String sql = "UPDATE events SET title=?,description=?,category=?,event_date=?,start_time=?,end_time=?,venue=?,capacity=?,banner_url=?,status=? WHERE id=? AND organizer_id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,event.getTitle()); ps.setString(2,event.getDescription()); ps.setString(3,event.getCategory());
            ps.setDate(4,Date.valueOf(event.getEventDate())); ps.setTime(5,Time.valueOf(event.getStartTime())); ps.setTime(6,Time.valueOf(event.getEndTime()));
            ps.setString(7,event.getVenue()); ps.setInt(8,event.getCapacity()); ps.setString(9,event.getBannerUrl()); ps.setString(10,event.getStatus().name());
            ps.setLong(11,event.getId()); ps.setLong(12,event.getOrganizerId()); ps.executeUpdate();
        } catch (SQLException e) { throw new DatabaseException("Could not update event.", e); }
    }

    @Override public void updateStatus(long eventId, EventStatus status, String rejectionReason) {
        String sql = "UPDATE events SET status=?, rejection_reason=? WHERE id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,status.name()); ps.setString(2,rejectionReason); ps.setLong(3,eventId); ps.executeUpdate();
        } catch (SQLException e) { throw new DatabaseException("Could not update event status.", e); }
    }

    @Override public void deleteDraft(long eventId, long organizerId) {
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement("DELETE FROM events WHERE id=? AND organizer_id=? AND status='DRAFT'")) {
            ps.setLong(1,eventId); ps.setLong(2,organizerId); ps.executeUpdate();
        } catch (SQLException e) { throw new DatabaseException("Could not delete draft event.", e); }
    }

    @Override public Event findById(long id) {
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(SELECT + "WHERE e.id=?")) {
            ps.setLong(1,id); try (ResultSet rs=ps.executeQuery()) { return rs.next()?map(rs):null; }
        } catch (SQLException e) { throw new DatabaseException("Could not find event.", e); }
    }

    private List<Event> list(String sql, List<Object> args) {
        List<Event> events=new ArrayList<>();
        try (Connection c=DBConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            for(int i=0;i<args.size();i++) ps.setObject(i+1,args.get(i));
            try(ResultSet rs=ps.executeQuery()){while(rs.next())events.add(map(rs));} return events;
        } catch(SQLException e){throw new DatabaseException("Could not list events.",e);}
    }

    @Override public List<Event> findApproved(String search, String category) {
        StringBuilder sql=new StringBuilder(SELECT+"WHERE e.status='APPROVED' AND e.event_date >= CURRENT_DATE"); List<Object> args=new ArrayList<>();
        if(search!=null&&!search.isBlank()){sql.append(" AND (e.title LIKE ? OR e.description LIKE ?)");args.add("%"+search.trim()+"%");args.add("%"+search.trim()+"%");}
        if(category!=null&&!category.isBlank()){sql.append(" AND e.category=?");args.add(category);}
        sql.append(" ORDER BY e.event_date,e.start_time"); return list(sql.toString(),args);
    }
    @Override public List<Event> findByOrganizer(long organizerId){return list(SELECT+"WHERE e.organizer_id=? ORDER BY e.event_date DESC",List.of(organizerId));}
    @Override public List<Event> findPending(){return list(SELECT+"WHERE e.status='PENDING_APPROVAL' ORDER BY e.created_at ASC",List.of());}
    @Override public List<Event> findUpcomingForAttendee(long attendeeId){
        return list(SELECT+"JOIN registrations r ON r.event_id=e.id WHERE r.attendee_id=? AND r.status='REGISTERED' AND e.event_date>=CURRENT_DATE ORDER BY e.event_date",List.of(attendeeId));
    }

    private long count(Connection c,String sql,Object... args)throws SQLException{try(PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)ps.setObject(i+1,args[i]);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getLong(1):0;}}}
    private BigDecimal money(Connection c,String sql,Object... args)throws SQLException{try(PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)ps.setObject(i+1,args[i]);try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getBigDecimal(1):BigDecimal.ZERO;}}}
    private DashboardStats stats(Long organizerId){
        try(Connection c=DBConnection.getConnection()){
            String suffix=organizerId==null?"":" WHERE organizer_id=?";
            DashboardStats s=new DashboardStats();
            if(organizerId==null){s.setTotalUsers(count(c,"SELECT COUNT(*) FROM users"));s.setTotalOrganizers(count(c,"SELECT COUNT(*) FROM users WHERE role='ORGANIZER'"));s.setTotalAttendees(count(c,"SELECT COUNT(*) FROM users WHERE role='ATTENDEE'"));}
            s.setTotalEvents(count(c,"SELECT COUNT(*) FROM events"+suffix, organizerId==null?new Object[]{}:new Object[]{organizerId}));
            s.setPendingEvents(count(c,"SELECT COUNT(*) FROM events WHERE status='PENDING_APPROVAL'"+(organizerId==null?"":" AND organizer_id=?"), organizerId==null?new Object[]{}:new Object[]{organizerId}));
            s.setApprovedEvents(count(c,"SELECT COUNT(*) FROM events WHERE status='APPROVED'"+(organizerId==null?"":" AND organizer_id=?"), organizerId==null?new Object[]{}:new Object[]{organizerId}));
            String filter=organizerId==null?"":" AND e.organizer_id=?";
            s.setTotalRegistrations(count(c,"SELECT COUNT(*) FROM registrations r JOIN events e ON e.id=r.event_id WHERE r.status='REGISTERED'"+filter,organizerId==null?new Object[]{}:new Object[]{organizerId}));
            s.setTotalTicketsSold(count(c,"SELECT COALESCE(SUM(t.sold_quantity),0) FROM tickets t JOIN events e ON e.id=t.event_id WHERE 1=1"+filter,organizerId==null?new Object[]{}:new Object[]{organizerId}));
            s.setTotalRevenue(money(c,"SELECT COALESCE(SUM(o.total_amount),0) FROM ticket_orders o JOIN events e ON e.id=o.event_id WHERE o.payment_status='PAID'"+filter,organizerId==null?new Object[]{}:new Object[]{organizerId}));
            return s;
        }catch(SQLException e){throw new DatabaseException("Could not load dashboard statistics.",e);}
    }
    @Override public DashboardStats systemStats(){return stats(null);}
    @Override public DashboardStats organizerStats(long organizerId){return stats(organizerId);}
}
