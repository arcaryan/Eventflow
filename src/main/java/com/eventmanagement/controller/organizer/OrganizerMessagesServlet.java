package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.dao.DAOFactory;
import com.eventmanagement.model.EventMessage;
import com.eventmanagement.service.EventService;
import com.eventmanagement.util.ServletUtil;
import com.eventmanagement.util.ValidationUtil;
import com.eventmanagement.service.ActivityService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/messages")
public class OrganizerMessagesServlet extends BaseServlet {
    private final ActivityService activity = new ActivityService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{long id=currentUser(req).getId();req.setAttribute("events",DAOFactory.events().findByOrganizer(id));req.setAttribute("messages",DAOFactory.messages().findByOrganizer(id));forward(req,res,"organizer/messages.jsp");}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{long eventId=Long.parseLong(req.getParameter("eventId"));var e=new EventService().require(eventId);new EventService().requireOwner(e,currentUser(req).getId());EventMessage m=new EventMessage();m.setEventId(eventId);m.setOrganizerId(currentUser(req).getId());m.setSubject(ValidationUtil.required(req.getParameter("subject"),"Subject"));m.setMessage(ValidationUtil.required(req.getParameter("message"),"Message"));DAOFactory.messages().create(m);activity.log(currentUser(req).getId(),"MESSAGE_SENT","EVENT",eventId,"Organizer sent an attendee update.");ServletUtil.flash(req,"success","Update sent to registered attendees.");}catch(RuntimeException e){ServletUtil.flash(req,"error",e.getMessage());}redirect(req,res,"/organizer/messages");}
}
