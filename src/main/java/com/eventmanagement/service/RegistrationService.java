package com.eventmanagement.service;

import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.dao.RegistrationDAO;
import com.eventmanagement.dao.SettingsDAO;
import com.eventmanagement.exception.BusinessRuleException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.EventStatus;

public class RegistrationService {
    private final RegistrationDAO registrations=DAOFactory.registrations();
    private final EventDAO events=DAOFactory.events();
    private final ActivityService activity = new ActivityService();
    private final SettingsDAO settings = DAOFactory.settings();
    public void register(long eventId,long attendeeId){
        Event event=events.findById(eventId);
        if ("false".equalsIgnoreCase(settings.findAll().getOrDefault("registration_enabled", "true"))) throw new BusinessRuleException("Registration is currently disabled by the administrator.");
        if(event==null||event.getStatus()!=EventStatus.APPROVED)throw new BusinessRuleException("Only approved events can be registered for.");
        if(registrations.exists(eventId,attendeeId))throw new BusinessRuleException("You are already registered for this event.");
        if(registrations.countActiveForEvent(eventId)>=event.getCapacity())throw new BusinessRuleException("This event is at capacity.");
        registrations.create(eventId,attendeeId);activity.log(attendeeId,"REGISTRATION_CREATED","EVENT",eventId,"Attendee registered for '"+event.getTitle()+"'.");
    }
}
