package com.eventmanagement.service;

import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.dao.TicketDAO;
import com.eventmanagement.exception.AuthorizationException;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Ticket;
import com.eventmanagement.model.TicketStatus;

import java.time.LocalDateTime;

public class TicketService {
    private final TicketDAO tickets=DAOFactory.tickets();
    public void validate(Ticket t){if(t.getName()==null||t.getName().isBlank())throw new ValidationException("Ticket name is required.");if(t.getPrice()==null||t.getPrice().signum()<0)throw new ValidationException("Ticket price cannot be negative.");if(t.getQuantity()<=0)throw new ValidationException("Ticket quantity must be positive.");if(t.getSalesStartAt()!=null&&t.getSalesEndAt()!=null&&!t.getSalesEndAt().isAfter(t.getSalesStartAt()))throw new ValidationException("Sale end must be after sale start.");}
    public long create(Ticket t){validate(t);t.setStatus(TicketStatus.ACTIVE);return tickets.create(t);}
    public void update(Ticket t,long organizerId){Ticket existing=require(t.getId());if(!sameOwner(existing,organizerId))throw new AuthorizationException("You do not own this ticket.");if(t.getQuantity()<existing.getSoldQuantity())throw new ValidationException("Quantity cannot be lower than tickets already sold.");validate(t);tickets.update(t);}
    public Ticket require(long id){Ticket t=tickets.findById(id);if(t==null)throw new ResourceNotFoundException("Ticket was not found.");return t;}
    private boolean sameOwner(Ticket t,long organizerId){return DAOFactory.events().findById(t.getEventId()).getOrganizerId()==organizerId;}
}
