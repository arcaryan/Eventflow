package com.eventmanagement.controller.publicview;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.exception.ResourceNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/events/view")
public class EventViewServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException, jakarta.servlet.ServletException{try{long id=requiredId(req);var event=DAOFactory.events().findById(id);if(event==null||event.getStatus()!=com.eventmanagement.model.EventStatus.APPROVED)throw new ResourceNotFoundException("Event was not found.");req.setAttribute("event",event);req.setAttribute("tickets",DAOFactory.tickets().findByEvent(id));req.setAttribute("registered",currentUser(req)!=null&&DAOFactory.registrations().exists(id,currentUser(req).getId()));forward(req,res,"public/event-view.jsp");}catch(RuntimeException e){error(req,res,e.getMessage());}}
}
