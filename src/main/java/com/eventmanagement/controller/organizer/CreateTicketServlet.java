package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.model.Ticket;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.TicketService;
import com.eventmanagement.util.ServletUtil;
import com.eventmanagement.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/organizer/tickets/create")
public class CreateTicketServlet extends BaseServlet {
    private final TicketService service=new TicketService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{req.setAttribute("events",com.eventmanagement.dao.DAOFactory.events().findByOrganizer(currentUser(req).getId()));forward(req,res,"organizer/ticket-form.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{Ticket t=read(req);var e=new EventService().require(t.getEventId());new EventService().requireOwner(e,currentUser(req).getId());service.create(t);ServletUtil.flash(req,"success","Ticket type created.");redirect(req,res,"/organizer/tickets");}catch(RuntimeException e){req.setAttribute("error",e.getMessage());req.setAttribute("events",com.eventmanagement.dao.DAOFactory.events().findByOrganizer(currentUser(req).getId()));forward(req,res,"organizer/ticket-form.jsp");}}
    protected Ticket read(HttpServletRequest req){Ticket t=new Ticket();t.setEventId(Long.parseLong(req.getParameter("eventId")));t.setName(ValidationUtil.required(req.getParameter("name"),"Ticket name"));t.setDescription(req.getParameter("description"));t.setPrice(ValidationUtil.nonNegativeMoney(req.getParameter("price")));t.setQuantity(ValidationUtil.positiveInt(req.getParameter("quantity"),"Quantity"));if(req.getParameter("salesStartAt")!=null&&!req.getParameter("salesStartAt").isBlank())t.setSalesStartAt(LocalDateTime.parse(req.getParameter("salesStartAt")));if(req.getParameter("salesEndAt")!=null&&!req.getParameter("salesEndAt").isBlank())t.setSalesEndAt(LocalDateTime.parse(req.getParameter("salesEndAt")));return t;}
}
