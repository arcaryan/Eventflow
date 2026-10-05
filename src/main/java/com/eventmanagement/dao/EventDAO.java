package com.eventmanagement.dao;

import com.eventmanagement.model.*;
import java.time.LocalDate;
import java.util.List;

public interface EventDAO {
    long create(Event event);
    void update(Event event);
    void updateStatus(long eventId, EventStatus status, String rejectionReason);
    void deleteDraft(long eventId, long organizerId);
    Event findById(long id);
    List<Event> findApproved(String search, String category);
    List<Event> findByOrganizer(long organizerId);
    List<Event> findPending();
    List<Event> findUpcomingForAttendee(long attendeeId);
    DashboardStats systemStats();
    DashboardStats organizerStats(long organizerId);
}
