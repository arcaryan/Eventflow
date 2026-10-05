package com.eventmanagement.dao;

import com.eventmanagement.model.EventMessage;
import java.util.List;

public interface EventMessageDAO {
    void create(EventMessage message);
    List<EventMessage> findByOrganizer(long organizerId);
    List<EventMessage> findForAttendee(long attendeeId);
}
