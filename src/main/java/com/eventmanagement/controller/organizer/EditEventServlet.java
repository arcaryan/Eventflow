package com.eventmanagement.controller.organizer;

import com.eventmanagement.controller.BaseServlet;
import com.eventmanagement.model.Event;
import com.eventmanagement.service.EventService;
import com.eventmanagement.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/organizer/events/edit")
public class EditEventServlet extends CreateEventServlet {
    private final EventService service=new EventService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{Event e=service.require(requiredId(req));service.requireOwner(e,currentUser(req).getId());req.setAttribute("event",e);forward(req,res,"organizer/event-form.jsp");}catch(RuntimeException e){error(req,res,e.getMessage());}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException,jakarta.servlet.ServletException{try{Event e=read(req);e.setId(requiredId(req));service.update(e,currentUser(req).getId());ServletUtil.flash(req,"success","Event updated.");redirect(req,res,"/organizer/events");}catch(RuntimeException e){req.setAttribute("error",e.getMessage());forward(req,res,"organizer/event-form.jsp");}}
}
