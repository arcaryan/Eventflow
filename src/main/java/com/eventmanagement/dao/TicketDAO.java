package com.eventmanagement.dao;

import com.eventmanagement.model.Ticket;
import java.sql.Connection;
import java.util.List;

public interface TicketDAO {
    List<Ticket> findByEvent(long eventId);
    List<Ticket> findByOrganizer(long organizerId);
    Ticket findById(long id);
    long create(Ticket ticket);
    void update(Ticket ticket);
    void delete(long id, long organizerId);
    boolean reserveStock(Connection connection, long ticketId, int quantity);
}
