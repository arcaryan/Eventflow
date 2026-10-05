package com.eventmanagement.dao;

import com.eventmanagement.model.Registration;
import java.util.List;

public interface RegistrationDAO {
    boolean exists(long eventId, long attendeeId);
    int countActiveForEvent(long eventId);
    void create(long eventId, long attendeeId);
    List<Registration> findByAttendee(long attendeeId);
    List<Registration> findByEvent(long eventId);
}
