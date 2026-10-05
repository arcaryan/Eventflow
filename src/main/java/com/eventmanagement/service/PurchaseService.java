package com.eventmanagement.service;

import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.dao.TicketDAO;
import com.eventmanagement.dao.TicketOrderDAO;
import com.eventmanagement.dao.SettingsDAO;
import com.eventmanagement.exception.BusinessRuleException;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.DBConnection;
import com.eventmanagement.util.ReferenceCodeUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

public class PurchaseService {
    private final TicketDAO tickets=DAOFactory.tickets();
    private final EventDAO events=DAOFactory.events();
    private final TicketOrderDAO orders=DAOFactory.orders();
    private final ActivityService activity = new ActivityService();
    private final SettingsDAO settings = DAOFactory.settings();
    public TicketOrder purchase(long attendeeId,long ticketId,int quantity){
        if(quantity<=0||quantity>20)throw new BusinessRuleException("Choose a valid ticket quantity.");
        Ticket ticket=tickets.findById(ticketId);if(ticket==null)throw new BusinessRuleException("Ticket was not found.");
        Event event=events.findById(ticket.getEventId());if(event==null||event.getStatus()!=EventStatus.APPROVED)throw new BusinessRuleException("Tickets are only available for approved events.");
        int maxPerAttendee;
        try { maxPerAttendee = Integer.parseInt(settings.findAll().getOrDefault("max_tickets_per_attendee", "6")); }
        catch (NumberFormatException ignored) { maxPerAttendee = 6; }
        if (orders.quantityForEvent(attendeeId, event.getId()) + quantity > maxPerAttendee) throw new BusinessRuleException("This purchase exceeds the attendee ticket limit.");
        LocalDateTime now=LocalDateTime.now();if(ticket.getSalesStartAt()!=null&&now.isBefore(ticket.getSalesStartAt()))throw new BusinessRuleException("Ticket sales have not started.");if(ticket.getSalesEndAt()!=null&&now.isAfter(ticket.getSalesEndAt()))throw new BusinessRuleException("Ticket sales have ended.");
        BigDecimal total=ticket.getPrice().multiply(BigDecimal.valueOf(quantity));
        TicketOrder order=new TicketOrder();order.setAttendeeId(attendeeId);order.setEventId(event.getId());order.setTicketId(ticketId);order.setQuantity(quantity);order.setUnitPrice(ticket.getPrice());order.setTotalAmount(total);order.setPaymentStatus(PaymentStatus.PAID);order.setReferenceCode(ReferenceCodeUtil.next());
        try(Connection c=DBConnection.getConnection()){
            c.setAutoCommit(false);
            try{if(!tickets.reserveStock(c,ticketId,quantity))throw new BusinessRuleException("Not enough tickets are available.");orders.create(c,order);c.commit();activity.log(attendeeId,"TICKET_PURCHASED","ORDER",null,"Simulated purchase completed for '"+event.getTitle()+"'.");return order;}catch(RuntimeException e){try{c.rollback();}catch(Exception ignored){}throw e;}catch(Exception e){try{c.rollback();}catch(Exception ignored){}throw new DatabaseException("Ticket purchase could not be completed.",e);}finally{try{c.setAutoCommit(true);}catch(Exception ignored){}}
        }catch(BusinessRuleException e){throw e;}catch(Exception e){throw new DatabaseException("Ticket purchase could not be completed.",e);}
    }
}
