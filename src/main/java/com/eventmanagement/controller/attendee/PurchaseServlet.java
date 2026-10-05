package com.eventmanagement.controller.attendee;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.service.PurchaseService;
import com.eventmanagement.util.ServletUtil;
import com.eventmanagement.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/attendee/purchase")
public class PurchaseServlet extends BaseServlet {
    private final PurchaseService service=new PurchaseService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{long ticketId=requiredId(req);var ticket=DAOFactory.tickets().findById(ticketId);if(ticket==null)throw new IllegalArgumentException("Ticket was not found.");req.setAttribute("ticket",ticket);req.setAttribute("event",DAOFactory.events().findById(ticket.getEventId()));forward(req,res,"attendee/purchase.jsp");}catch(RuntimeException e){error(req,res,e.getMessage());}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{try{int quantity=ValidationUtil.positiveInt(req.getParameter("quantity"),"Quantity");var order=service.purchase(currentUser(req).getId(),Long.parseLong(req.getParameter("ticketId")),quantity);ServletUtil.flash(req,"success","Purchase confirmed: "+order.getReferenceCode());redirect(req,res,"/attendee/tickets");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());redirect(req,res,"/attendee/events");}}
}
