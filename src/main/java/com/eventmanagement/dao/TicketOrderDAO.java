package com.eventmanagement.dao;

import com.eventmanagement.model.TicketOrder;
import java.sql.Connection;
import java.util.List;

public interface TicketOrderDAO {
    void create(Connection connection, TicketOrder order);
    List<TicketOrder> findByAttendee(long attendeeId);
    long countByAttendee(long attendeeId);
    int quantityForEvent(long attendeeId, long eventId);
}
