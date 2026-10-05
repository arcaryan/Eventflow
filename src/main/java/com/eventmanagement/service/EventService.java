package com.eventmanagement.service;

import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.exception.AuthorizationException;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.ValidationUtil;

import java.time.LocalDate;

public class EventService {
    private final EventDAO events=DAOFactory.events();
    private final ActivityService activity = new ActivityService();
    public void validate(Event e, boolean creating){
        ValidationUtil.required(e.getTitle(),"Title");ValidationUtil.required(e.getDescription(),"Description");ValidationUtil.required(e.getVenue(),"Venue");
        if(e.getCategory()==null||e.getCategory().isBlank())e.setCategory("General");
        if(e.getEventDate()==null|| (creating&&e.getEventDate().isBefore(LocalDate.now())))throw new ValidationException("Event date must be today or in the future.");
        if(e.getStartTime()==null||e.getEndTime()==null||!e.getEndTime().isAfter(e.getStartTime()))throw new ValidationException("End time must be after start time.");
        if(e.getCapacity()<=0)throw new ValidationException("Capacity must be positive.");
    }
    public long create(Event e){validate(e,true);e.setStatus(EventStatus.DRAFT);long id=events.create(e);activity.log(e.getOrganizerId(),"EVENT_CREATED","EVENT",id,"Organizer created event '"+e.getTitle()+"'.");return id;}
    public void update(Event e,long organizerId){Event existing=require(e.getId());requireOwner(existing,organizerId);if(existing.getStatus()==EventStatus.APPROVED)e.setStatus(EventStatus.PENDING_APPROVAL);else e.setStatus(existing.getStatus());e.setOrganizerId(organizerId);validate(e,false);events.update(e);activity.log(organizerId,"EVENT_UPDATED","EVENT",e.getId(),"Organizer updated event '"+e.getTitle()+"'.");}
    public Event require(long id){Event e=events.findById(id);if(e==null)throw new ResourceNotFoundException("Event was not found.");return e;}
    public void requireOwner(Event e,long organizerId){if(e.getOrganizerId()!=organizerId)throw new AuthorizationException("You do not own this event.");}
    public void submit(long id,long organizerId){Event e=require(id);requireOwner(e,organizerId);if(e.getStatus()!=EventStatus.DRAFT&&e.getStatus()!=EventStatus.REJECTED)throw new ValidationException("Only draft or rejected events can be submitted.");events.updateStatus(id,EventStatus.PENDING_APPROVAL,null);activity.log(organizerId,"EVENT_SUBMITTED","EVENT",id,"Event submitted for admin approval.");}
    public void cancel(long id,long organizerId){Event e=require(id);requireOwner(e,organizerId);if(e.getStatus()==EventStatus.COMPLETED)throw new ValidationException("Completed events cannot be cancelled.");events.updateStatus(id,EventStatus.CANCELLED,null);activity.log(organizerId,"EVENT_CANCELLED","EVENT",id,"Organizer cancelled event '"+e.getTitle()+"'.");}
    public void deleteDraft(long id,long organizerId){Event e=require(id);requireOwner(e,organizerId);if(e.getStatus()!=EventStatus.DRAFT)throw new ValidationException("Only draft events can be deleted.");events.deleteDraft(id,organizerId);activity.log(organizerId,"EVENT_DELETED","EVENT",id,"Organizer deleted a draft event.");}
}
