package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.model.Ticket;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.TicketService;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/tickets/edit")
public class EditTicketServlet extends CreateTicketServlet {
    private final TicketService service=new TicketService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{Ticket t=service.require(requiredId(req));new EventService().requireOwner(new EventService().require(t.getEventId()),currentUser(req).getId());req.setAttribute("ticket",t);req.setAttribute("events",com.eventmanagement.dao.DAOFactory.events().findByOrganizer(currentUser(req).getId()));forward(req,res,"organizer/ticket-form.jsp");}catch(RuntimeException e){error(req,res,e.getMessage());}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{Ticket t=read(req);t.setId(requiredId(req));new EventService().requireOwner(new EventService().require(t.getEventId()),currentUser(req).getId());service.update(t,currentUser(req).getId());ServletUtil.flash(req,"success","Ticket type updated.");redirect(req,res,"/organizer/tickets");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());redirect(req,res,"/organizer/tickets");}}
}
