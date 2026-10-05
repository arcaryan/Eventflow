package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.service.EventService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/attendees")
public class OrganizerAttendeesServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{long eventId=requiredId(req);var e=new EventService().require(eventId);new EventService().requireOwner(e,currentUser(req).getId());req.setAttribute("event",e);req.setAttribute("registrations",DAOFactory.registrations().findByEvent(eventId));forward(req,res,"organizer/attendees.jsp");}catch(RuntimeException e){error(req,res,e.getMessage());}}
}
